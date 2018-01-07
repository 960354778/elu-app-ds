package com.qingyun.zhiyunelu.ds.net.NetLife;

/**
 * Created by luohongzhen on 07/01/2018.
 */

public class Response<T> {

    public final T result;

    public String error;

    public static <T> Response<T> success(T result){
        return new Response<T>(result);
    }

    public static <T> Response<T> error(String error){
        return new Response<>(error);
    }

    private Response(T result){
        this.result = result;
    }

    private Response(String error) {
        this.result = null;
        this.error = error;
    }
}
