package com.qingyun.zhiyunelu.ds.record;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Message;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.data.PendingSoundRecordInfo;
import com.qingyun.zhiyunelu.ds.data.SmsMsgInfo;
import com.qingyun.zhiyunelu.ds.net.ApiService;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import velites.android.utility.utils.HandlerUtil;
import velites.java.utility.generic.Action1;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.StringUtil;

/**
 * Created by luohongzhen on 22/02/2018.
 */

public class SoundRecordSynchronizer {

    private final static String TAG = SoundRecordSynchronizer.class.getSimpleName();

    private static HandlerUtil handlerUtil;
    private static final int MSG_TAG_UPLOAD_SOUND_RECORD = 3003;

    public static void init(){
        if(handlerUtil != null){
            handlerUtil.releaseData();
            handlerUtil = null;
        }
        handlerUtil = HandlerUtil.create(false, TAG, new Action1<Message>() {
            @Override
            public void a(Message arg1) {
                String token = AppAssistant.getPrefs().getStr(Constants.PrefsKey.AUTH_TOKEN_KEY);
                uploadSoundRecordList();
                handlerUtil.removeMessages(MSG_TAG_UPLOAD_SOUND_RECORD);
                handlerUtil.sendEmptyMessageDelayed(MSG_TAG_UPLOAD_SOUND_RECORD, Constants.UPLOAD_SOUND_RECORD_CYCLE);
            }
        });
        handlerUtil.sendEmptyMessageDelayed(MSG_TAG_UPLOAD_SOUND_RECORD, Constants.UPLOAD_SOUND_RECORD_CYCLE);
        LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, TAG, "init sound record synchronizer"));
    }

    private static void uploadSoundRecordList() {
        try {
            Map<String, File> files = new HashMap<>();
            long limit = new Date().getTime() - Constants.UPLOAD_SOUND_RECORD_OFFSET;
            File dir = new File(Constants.FilePaths.MIUI_SOUND_DIR);
            if (dir.exists() && dir.isDirectory()) {
                for (File f : dir.listFiles()) {
                    if (f != null && f.lastModified() >= Constants.UPLOAD_SOUND_RECORD_START.getTime() && f.lastModified() < limit) {
                        files.put(f.getName(), f);
                    }
                }
            }
            if (files.size() > 0) {
                AppAssistant.getApi().checkAudioToRecords(files.keySet().toArray(new String[0])).subscribe(new ApiService.ApiObserver<List<PendingSoundRecordInfo>>() {
                    @Override
                    public void onSuccess(List<PendingSoundRecordInfo> pendings) {
                        if (pendings != null) {
                            for (PendingSoundRecordInfo p : pendings) {
                                File f = files.get(p.getFileName());
                                if (f != null) {
                                    RecordRequest.uploadSoundRecordFile(f, p.getTaskRecordId(), null);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFail(Throwable e) {
                        ExceptionUtil.swallowThrowable(e);
                    }
                });
            }
        } catch (Throwable ex) {
            ExceptionUtil.swallowThrowable(ex);
        }
    }
}
