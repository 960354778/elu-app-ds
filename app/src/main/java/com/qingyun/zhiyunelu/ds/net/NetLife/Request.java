package com.qingyun.zhiyunelu.ds.net.NetLife;

import android.support.annotation.NonNull;

/**
 * Created by luohongzhen on 07/01/2018.
 */

public abstract class Request<T> implements Comparable<Request<T>> {

    private final byte[] mLock = new byte[0];
    private RequestQueue mRequestQueue;
    private boolean mCanceled = false;

    private NetworkRequestCompleteListener mRequestCompListener;

    interface NetworkRequestCompleteListener{
        void onResponseReceived(Request<?> request, Response<?> response);
        void onNoUsableResponseReceived(Request<?> request);
    }

    private String mTag;


    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    public String getTag() {
        return mTag;
    }

    public Request<?> setRequestQueue(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
        return this;
    }

    void finish() {
        if (mRequestQueue != null) {
//            mRequestQueue.finish(this);
        }
    }


    public void cancel() {
        synchronized (mLock) {
            mCanceled = true;
        }
    }

    public boolean isCanceled() {
        synchronized (mLock) {
            return mCanceled;
        }
    }

    public void setmRequestCompListener(NetworkRequestCompleteListener mRequestCompListener) {
        synchronized (mLock){
            this.mRequestCompListener = mRequestCompListener;
        }
    }

   public void notifyListenerResponseReceived(Response<?> response) {
        NetworkRequestCompleteListener listener;
        synchronized (mLock) {
            listener = mRequestCompListener;
        }
        if (listener != null) {
            listener.onResponseReceived(this, response);
        }
    }

    public Response<T> performRequest(){
        //TODO synchronous request api
        return null;
    }

    public void notifyListenerResponseNotUsable() {
        NetworkRequestCompleteListener listener;
        synchronized (mLock) {
            listener = mRequestCompListener;
        }
        if (listener != null) {
            listener.onNoUsableResponseReceived(this);
        }
    }

    public Priority getPriority() {
        return Priority.NORMAL;
    }

    @Override
    public int compareTo(@NonNull Request<T> o) {
        Priority top = this.getPriority();
        Priority down = o.getPriority();
        return top == down ? 0 : down.ordinal() - top.ordinal();
    }

}
