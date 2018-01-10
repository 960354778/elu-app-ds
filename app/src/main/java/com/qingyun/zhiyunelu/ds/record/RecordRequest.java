package com.qingyun.zhiyunelu.ds.record;

import android.util.Log;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.data.RecordInfo;
import com.qingyun.zhiyunelu.ds.net.NetLife.Request;
import com.qingyun.zhiyunelu.ds.net.NetLife.Response;

import io.reactivex.functions.Consumer;
import velites.java.utility.misc.StringUtil;

/**
 * Created by luohongzhen on 08/01/2018.
 */

public class RecordRequest extends Request {

    public RecordRequest(String phone ,String tag, String path,String taskId, NetworkRequestCompleteListener listener) {
        setmTag(String.format("%s_%s", phone, System.currentTimeMillis()+""));
        setmUrl(path);
        setmRequestCompListener(listener);
        setTaskId(taskId);
    }

    public RecordRequest(String phone, String path, String taskId){
        this(phone,phone, path, taskId, null);
    }

    @Override
    public Response performRequest() {
        //TODO upload sound
        AppAssistant.getApi().recordCalledOut(new RecordInfo.RecordRequestBody(getTaskId()))
                .subscribe(new Consumer<RecordInfo>() {
                    @Override
                    public void accept(RecordInfo recordInfo) throws Exception {
                        if(recordInfo != null){
                            String taskRecordId = recordInfo.getTaskRecordId();
                            if(StringUtil.isNullOrEmpty(taskRecordId)){
                                throw new NullPointerException("taskRecordId is null");
                            }
                            Log.d("LHZ", "---"+taskRecordId);
                        }
                    }
                });
        return null;
    }
}
