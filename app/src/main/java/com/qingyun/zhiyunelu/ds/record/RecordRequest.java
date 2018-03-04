package com.qingyun.zhiyunelu.ds.record;

import android.text.TextUtils;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.data.RecordInfo;
import com.qingyun.zhiyunelu.ds.net.NetLife.Request;
import com.qingyun.zhiyunelu.ds.net.NetLife.Response;

import java.io.File;
import java.util.Date;

import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import velites.android.support.media.MediaHelper;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;
import velites.java.utility.misc.EncryptUtil;
import velites.java.utility.misc.FileUtil;
import velites.java.utility.misc.PathUtil;
import velites.java.utility.misc.StringUtil;
import velites.java.utility.thread.ThreadUtil;

/**
 * Created by luohongzhen on 08/01/2018.
 */

public class RecordRequest extends Request {

    private final String TAG = RecordRequest.class.getSimpleName();
    private String taskRecordId;

    public RecordRequest(String phone, String path, String taskId, NetworkRequestCompleteListener listener) {
        setmTag(String.format("%s_%s", phone, System.currentTimeMillis() + ""));
        setmUrl(path);
        setmRequestCompListener(listener);
        setTaskId(taskId);
        setPhone(phone);
        setStartTime(new Date().getTime());
    }

    public RecordRequest(String phone, String path, String taskId) {
        this(phone, path, taskId, null);
    }

    public void sendRequest(){
        ThreadUtil.runInNewThread(new Runnable() {
            @Override
            public void run() {
                recordCallOut();
            }
        },TAG, null);
    }

    private void recordCallOut() {
        AppAssistant.getApi().recordCalledOut(new RecordInfo.RecordRequestBody(getTaskId(), getPhone()))
                .subscribe(new Consumer<RecordInfo>() {
                    @Override
                    public void accept(RecordInfo recordInfo) throws Exception {
                        try {
                            LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, this, "phone %s recordCallOut request info:%s", getPhone(), recordInfo == null ? "fail" : recordInfo.toString()));
                            taskRecordId = recordInfo.getData().getTaskRecordId();
                            if (StringUtil.isNullOrEmpty(taskRecordId)) {
                                throw new NullPointerException("taskRecordId is null");
                            }
                            AppAssistant.getRequestQueue().addWaitTask(getPhone(), RecordRequest.this);
                        } catch (Exception e) {
                            LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, this, "recordCallOut request error:%s", e.getMessage()));
                        }
                    }
                });
    }

    @Override
    public Response<RecordInfo> performRequest() {
        try {
            final RecordInfo[] data = {null};
            try {

                LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, this, "phone %s recordCallOut", getPhone()));
                if (StringUtil.isNullOrEmpty(taskRecordId)) {
                    throw new NullPointerException("taskRecordId is null");
                }
                long timeRangeEnd = new Date().getTime();
                int sameTimes = 0;
                File file = null;
                long oldSize = 0;
                while (sameTimes < 16) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                    }
                    String url = FileUtil.getRecentlyMiUiSoundPath(getPhone(), Constants.FilePaths.MIUI_SOUND_DIR, getStartTime(), timeRangeEnd);
                    File newFile = url == null ? null : new File(url);
                    long newSize = newFile != null && newFile.exists() ? newFile.length() : 0;
                    if ((newFile == null && file == null || newFile != null && newFile.equals(file)) && oldSize == newSize) {
                        sameTimes++;
                    } else {
                        file = newFile;
                        oldSize = newSize;
                        sameTimes = 0;
                    }
                    LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, this, "wait record video file write complete oldsize: %d, nowsize: %d, sameTimes: %d", oldSize, newSize, sameTimes));
                }
                if (file == null) {
                    LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, this, "phone %s recordCallOut request but path is null", getPhone()));
                } else {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part filePart = MultipartBody.Part.createFormData("mp3", file.getName(), requestFile);
                    String duration = MediaHelper.retrieveMediaDuration(file.toString());
                    final File f = file;
                    AppAssistant.getApi().upLoadRecord(taskRecordId, EncryptUtil.getFileSha1(f.getAbsolutePath()), filePart, duration)
                            .subscribe(new Consumer<RecordInfo>() {
                                @Override
                                public void accept(RecordInfo recordInfo) throws Exception {
                                    LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, this, "phone:%s upload request info:%s", getPhone(), recordInfo == null ? "fail" : recordInfo.toString()));
                                    if (recordInfo != null) {
                                        if (recordInfo.getError() != null && recordInfo.getError().getCode() != null) {
                                            int num = getmRepeatRequest();
                                            if (num > 0) {
                                                setmRepeatRequest(--num);
                                                performRequest();
                                                return;
                                            }
                                        }
                                        data[0] = recordInfo;
                                        FileUtil.moveFile(f, new File(PathUtil.concat(AppAssistant.getUploadedFileDir(), f.getName())));
                                    }
                                }
                            });
                }
            } catch (Exception e1) {
                LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, this, "recordCallOut request error:%s", e1.getMessage()));
            }

            if (data[0] != null) {
                return Response.success(data[0]);
            } else {
                return Response.error("upLoad fail");
            }
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}
