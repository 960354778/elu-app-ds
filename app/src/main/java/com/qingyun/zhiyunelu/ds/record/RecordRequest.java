package com.qingyun.zhiyunelu.ds.record;

import com.qingyun.zhiyunelu.ds.net.NetLife.Request;
import com.qingyun.zhiyunelu.ds.net.NetLife.Response;

/**
 * Created by luohongzhen on 08/01/2018.
 */

public class RecordRequest extends Request {

    public RecordRequest(String phone ,String path, NetworkRequestCompleteListener listener) {
        setmTag(String.format("%s_%l", phone, System.currentTimeMillis()));
        setmUrl(path);
        setmRequestCompListener(listener);
    }

    public RecordRequest(String phone, String path){
        this(phone, path, null);
    }

    @Override
    public Response performRequest() {
        //TODO upload sound
        return null;
    }
}
