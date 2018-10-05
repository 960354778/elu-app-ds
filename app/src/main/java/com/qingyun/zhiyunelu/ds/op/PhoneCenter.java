package com.qingyun.zhiyunelu.ds.op;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.qingyun.zhiyunelu.ds.App;
import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.data.ApiResult;
import com.qingyun.zhiyunelu.ds.data.PendingSoundRecordInfo;
import com.qingyun.zhiyunelu.ds.data.PhoneInfo;
import com.qingyun.zhiyunelu.ds.data.RecordEntity;
import com.qingyun.zhiyunelu.ds.data.RecordInfo;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import velites.android.support.devices.xiaomi.Behavior;
import velites.android.support.devices.xiaomi.XiaomiConstants;
import velites.android.support.media.MediaHelper;
import velites.android.utility.misc.PhoneNumberHelper;
import velites.android.utility.misc.RxHelper;
import velites.android.utility.misc.ThreadHelper;
import velites.android.utility.misc.ToastHelper;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.DateTimeUtil;
import velites.java.utility.misc.EncryptUtil;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.FileUtil;
import velites.java.utility.misc.PathUtil;
import velites.java.utility.misc.RxUtil;
import velites.java.utility.misc.SerializationUtil;
import velites.java.utility.misc.StringUtil;

public class PhoneCenter {

    private final App.Assistant assistant;
    private final TelephonyManager telephony;
    private PhoneStateListener listener;
    private RecordEntity callingRecord;
    private Integer currentState;

    public PhoneCenter(App.Assistant assistant) {
        this.assistant = assistant;
        this.telephony = (TelephonyManager) assistant.getDefaultContext().getSystemService(Context.TELEPHONY_SERVICE);
//        this.assistant.getDefaultContext().registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "PRECISE_CALL_STATE changed: %s", intent));
//            }
//        }, new IntentFilter("android.intent.action.PRECISE_CALL_STATE"));
        ThreadHelper.runOnUiThread(() -> {
            this.listener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    Observable.just(0)
                            .subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeComputationSchedule())
                            .subscribe(v -> {
                                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Call state changed to: %d, number: %s", state, phoneNumber));
                                handleCallStateChanged(state, phoneNumber);
                            }, RxUtil.simpleErrorConsumer);
                }
            };
            this.telephony.listen(this.listener, PhoneStateListener.LISTEN_CALL_STATE);
        });
    }

    public void dial(Context ctx, RecordInfo rec, PhoneInfo p) {
        RecordEntity r = new RecordEntity();
        r.taskRecordId = rec.taskRecordId;
        r.phoneId = rec.phoneId;
        r.executionTime = rec.execDate;
        r.phoneNumber = PhoneNumberHelper.getChinaCallableNumber(p.number, p.areaCode);
        r.status = RecordEntity.Status.Initial;
        this.assistant.getData().getDb().records().save(r);
        this.callingRecord = r;
        PhoneNumberHelper.callOut(ctx == null ? this.assistant.getDefaultContext() : ctx, this.callingRecord.phoneNumber);
    }

    private void handleCallStateChanged(int state, String phoneNumber) {
        Integer originState = this.currentState;
        this.currentState = state;
        Calendar now = DateTimeUtil.now();
        Calendar thresholdTime = DateTimeUtil.add(now, -assistant.getSetting().logic.callElapseThresholdMs, TimeUnit.MILLISECONDS);
        if (state == TelephonyManager.CALL_STATE_RINGING) {
            RecordEntity r = new RecordEntity();
            r.executionTime = now;
            r.phoneNumber = phoneNumber;
            r.isIncoming = true;
            r.status = RecordEntity.Status.Connecting;
            this.assistant.getData().getDb().records().save(r);
        } else {
            if (this.callingRecord == null || !TextUtils.equals(phoneNumber, this.callingRecord.phoneNumber)) {
                this.callingRecord = StringUtil.isNullOrEmpty(phoneNumber) ? null : this.assistant.getData().getDb().records().fetchLatestByNumberAndStatus(phoneNumber, new RecordEntity.Status[]{RecordEntity.Status.Initial, RecordEntity.Status.Connecting, RecordEntity.Status.Connected}, thresholdTime);
            }
            if (this.callingRecord != null) {
                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    this.callingRecord.status = this.callingRecord.isIncoming ? RecordEntity.Status.Connected : RecordEntity.Status.Connecting;
                    this.assistant.getData().getDb().records().save(this.callingRecord);
                } else if (state == TelephonyManager.CALL_STATE_IDLE && originState == TelephonyManager.CALL_STATE_OFFHOOK) {
                    this.callingRecord.status = RecordEntity.Status.Finished;
                    this.assistant.getData().getDb().records().save(this.callingRecord);
                    this.matchRecordFile(this.callingRecord);
                    this.callingRecord = null;
                }
            }
        }
    }

    private void matchRecordFile(RecordEntity r) {
        Observable.interval(assistant.getSetting().logic.callRecordMatchDelayMs, assistant.getSetting().logic.callRecordMatchIntervalMs, TimeUnit.MILLISECONDS)
                .subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeComputationSchedule())
                .subscribe(new Observer<Long>() {
                    private Long timeRangeEnd;
                    private int sameTimes = 0;
                    private File file = null;
                    private long size = 0L;
                    private Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (timeRangeEnd == null) {
                            timeRangeEnd = DateTimeUtil.nowTimestamp();
                        }
                        String url = Behavior.getRecentlyMiUiSoundPath(PhoneNumberHelper.normalizeTelNumber(r.phoneNumber), XiaomiConstants.MIUI_SOUND_DIR, r.executionTime.getTimeInMillis(), timeRangeEnd);
                        File originFile = file;
                        long originSize = size;
                        File newFile = url == null ? null : new File(url);
                        long newSize = newFile != null && newFile.exists() ? newFile.length() : 0;
                        if ((newFile == null && file == null || newFile != null && newFile.equals(file)) && size == newSize) {
                            sameTimes++;
                        } else {
                            file = newFile;
                            size = newSize;
                            sameTimes = 0;
                        }
                        LogStub.log(new LogEntry(LogStub.LOG_LEVEL_DEBUG, this, "Waiting record audio file write complete, old: (%s)%d, new: (%s)%d, sameTimes: %d", originFile, originSize, newFile, newSize, sameTimes));
                        if (sameTimes >= assistant.getSetting().logic.callRecordMatchSameTimesThreshold) {
                            disposable.dispose();
                            if (file == null) {
                                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_WARNING, this, "Failed to match sound record file, record: %s", SerializationUtil.describe(r)));
                            } else {
                                r.fileName = file.getName();
                                assistant.getData().getDb().records().save(r);
                                uploadRecordAsync(r, file);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        disposable.dispose();
                        RxUtil.handleRxExceptionByDefault(e);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void uploadRecordAsync(RecordEntity r, File f) {
        if (r.taskRecordId != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse(MediaHelper.retrieveMediaMimetype(f.toString())), f);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData(r.taskRecordId, f.getName(), requestFile);
            String duration = MediaHelper.retrieveMediaDuration(f.toString());
            String hash = EncryptUtil.computeFileHashByDefault(f);
            assistant.getApi().createAsyncApi().uploadSoundRecord(r.taskRecordId, hash, duration, filePart)
                    .subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeComputationSchedule())
                    .subscribe(new ApiService.ApiObserver<RecordInfo>() {
                        @Override
                        protected boolean processResult(RecordInfo rec, ApiResult<RecordInfo> res) {
                            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, this, "Uploaded(async) sound record file: %s, record: %s", f, SerializationUtil.describe(r)));
                            r.status = RecordEntity.Status.Uploaded;
                            assistant.getData().getDb().records().save(r);
                            ToastHelper.showToastShort(assistant.getDefaultContext(), assistant.getDefaultContext().getString(R.string.info_sound_record_uploaded, r.phoneNumber, r.fileName));
                            ExceptionUtil.executeWithRetry(() -> FileUtil.moveFile(f, new File(PathUtil.concat(assistant.getUploadedFileDir().getPath(), f.getName()))), 1, null);
                            return true;
                        }
                        @Override
                        public void onError(Throwable ex) {
                            super.onError(ex);
                            r.error = ExceptionUtil.extractException(ex);
                            assistant.getData().getDb().records().save(r);
                            ToastHelper.showToastLong(assistant.getDefaultContext(), assistant.getDefaultContext().getString(R.string.info_sound_record_upload_failed, r.phoneNumber, f));
                        }
                    });
        }
    }

    private void uploadRecordSync(RecordEntity r, File f) {
        if (r.taskRecordId != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse(MediaHelper.retrieveMediaMimetype(f.toString())), f);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData(r.taskRecordId, f.getName(), requestFile);
            String duration = MediaHelper.retrieveMediaDuration(f.toString());
            String hash = EncryptUtil.computeFileHashByDefault(f);
            try {
                assistant.getApi().createSyncApi().uploadSoundRecord(r.taskRecordId, hash, duration, filePart);
            } catch (Throwable ex) {
                r.error = ExceptionUtil.extractException(ex);
                assistant.getData().getDb().records().save(r);
                ToastHelper.showToastLong(assistant.getDefaultContext(), assistant.getDefaultContext().getString(R.string.info_sound_record_upload_failed, r.phoneNumber, f));
                ExceptionUtil.rethrowAsRuntime(ex, true);
            }
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, this, "Uploaded(sync) sound record file: %s, record: %s", f, SerializationUtil.describe(r)));
            r.status = RecordEntity.Status.Uploaded;
            assistant.getData().getDb().records().save(r);
            ToastHelper.showToastShort(assistant.getDefaultContext(), assistant.getDefaultContext().getString(R.string.info_sound_record_uploaded, r.phoneNumber, r.fileName));
            ExceptionUtil.executeWithRetry(() -> FileUtil.moveFile(f, new File(PathUtil.concat(assistant.getUploadedFileDir().getPath(), f.getName()))), 1, null);
        }
    }

    public void uploadErrorRecords() {
        RecordEntity[] rs = assistant.getData().getDb().records().fetchErrors(new RecordEntity.Status[] {RecordEntity.Status.Uploaded});
        for (RecordEntity r : rs) {
            if (r.taskRecordId == null || r.status != RecordEntity.Status.Finished) {
                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_WARNING, this, "There is record having error as unexpected (without TaskRecordId or not finished): %s, error: %s", SerializationUtil.describe(r), r.error));
                continue;
            }
            File f = StringUtil.isNullOrEmpty(r.fileName) ? null : new File(PathUtil.concat(XiaomiConstants.MIUI_SOUND_DIR, r.fileName));
            if (f == null || !f.exists()) {
                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_WARNING, this, "Failed to find sound record file at: %s, record: %s", f, SerializationUtil.describe(r)));
                continue;
            }
            uploadRecordSync(r, f);
        }
    }

    public void uploadUnmatchedSoundRecords() {
        Map<String, File> files = new HashMap<>();
        long limit = DateTimeUtil.nowTimestamp() - assistant.getSetting().logic.callRecordUnmatchedOffsetMs;
        File dir = new File(XiaomiConstants.MIUI_SOUND_DIR);
        if (dir.exists() && dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                if (f != null && f.lastModified() >= assistant.getSetting().logic.callRecordUnmatchedStartEpochMs && f.lastModified() < limit) {
                    files.put(f.getName(), f);
                }
            }
        }
        if (files.size() > 0) {
            PendingSoundRecordInfo[] ps = assistant.getApi().createSyncApi().checkAudioToRecords(files.keySet().toArray(new String[0])).data;
            if (ps != null) {
                for (PendingSoundRecordInfo p : ps) {
                    File f = files.get(p.fileName);
                    if (f != null) {
                        RecordEntity r = assistant.getData().getDb().records().fetchByFileName(f.getName());
                        if (r == null) {
                            r = new RecordEntity();
                            r.taskRecordId = p.taskRecordId;
                            r.isIncoming = p.isCallback;
                            r.status = RecordEntity.Status.Finished;
                            r.fileName = f.getName();
                            this.assistant.getData().getDb().records().save(r);
                        }
                        uploadRecordSync(r, f);
                    }
                }
            }
        }
    }
}
