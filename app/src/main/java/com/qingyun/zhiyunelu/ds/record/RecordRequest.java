package com.qingyun.zhiyunelu.ds.record;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.data.RecordInfo;
import com.qingyun.zhiyunelu.ds.net.NetLife.Request;
import com.qingyun.zhiyunelu.ds.net.NetLife.Response;

import java.io.File;

import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;
import velites.java.utility.misc.EncryptUtil;
import velites.java.utility.misc.FileUtil;
import velites.java.utility.misc.StringUtil;

/**
 * Created by luohongzhen on 08/01/2018.
 */

public class RecordRequest extends Request {

    public RecordRequest(String phone, String path, String taskId, NetworkRequestCompleteListener listener) {
        setmTag(String.format("%s_%s", phone, System.currentTimeMillis() + ""));
        setmUrl(path);
        setmRequestCompListener(listener);
        setTaskId(taskId);
        setPhone(phone);
        setStartTime(System.currentTimeMillis());
    }

    public RecordRequest(String phone, String path, String taskId) {
        this(phone, path, taskId, null);
    }

    @Override
    public Response<RecordInfo> performRequest() {
        //TODO upload sound
        try {
            final RecordInfo[] data = {null};
            AppAssistant.getApi().recordCalledOut(new RecordInfo.RecordRequestBody(getTaskId(),getPhone()))
                    .subscribe(new Consumer<RecordInfo>() {
                        @Override
                        public void accept(RecordInfo recordInfo){
                            try{
                                LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, this, "phone %s recordCallOut request info:%s", getPhone(), recordInfo == null ? "fail" : recordInfo.toString()));
                                if (recordInfo != null) {
                                    String taskRecordId = recordInfo.getData().getTaskRecordId();
                                    if (StringUtil.isNullOrEmpty(taskRecordId)) {
                                        throw new NullPointerException("taskRecordId is null");
                                    }
                                    String url = getmUrl();
                                    if (url == null) {
                                        url = FileUtil.getRecentlyMiUiSoundPath(getPhone(), Constants.FilePaths.MIUI_SOUND_DIR);
                                        setmUrl(url);
                                    }
                                    if (url == null) {
                                        LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, this, "phone %s recordCallOut request but path is null", getPhone()));
                                    }

                                    File file = new File(getmUrl());
                                    if (file.exists()) {
                                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                                        MultipartBody.Part filePart = MultipartBody.Part.createFormData("mp3", file.getName(), requestFile);
                                        long startTime = getStartTime();
                                        AppAssistant.getApi().upLoadRecord(taskRecordId, EncryptUtil.getFileSha1(file.getAbsolutePath()), filePart, startTime > 0? (System.currentTimeMillis() - startTime)+"": "0")
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
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }catch (Exception e1){
                                LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, this, "recordCallOut request error:%s", e1.getMessage()));
                            }
                        }
                    });

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
