package com.qingyun.zhiyunelu.ds.net.NetLife;

import android.support.annotation.NonNull;

/**
 * Created by luohongzhen on 07/01/2018.
 */

public abstract class Request<T> implements Comparable<Request<T>> {

    private Priority mPriority = Priority.NORMAL;
    private final byte[] mLock = new byte[0];
    private RequestQueue mRequestQueue;
    private boolean mCanceled = false;

    private String mTaskId;
    private String mPhone;

    private NetworkRequestCompleteListener mRequestCompListener;

    public interface NetworkRequestCompleteListener{
        void onResponseReceived(Request<?> request, Response<?> response);
        void onNoUsableResponseReceived(Request<?> request);
        void onResponseError(Request<?> request, Response<?> response);
    }

    private String mTag;
    private String mUrl;


    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    public String getTag() {
        return mTag;
    }

    public void setmTag(String mTag) {
        this.mTag = mTag;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public Request<?> setRequestQueue(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
        return this;
    }

    void finish() {
        if (mRequestQueue != null) {
            mRequestQueue.finish(this);
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

    public String getTaskId() {
        return mTaskId;
    }

    public void setTaskId(String mTaskId) {
        this.mTaskId = mTaskId;
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
        finish();
    }

    public void notifyListenerErrorReceived(Response<?> errorResponse){
        NetworkRequestCompleteListener listener;
        synchronized (mLock) {
            listener = mRequestCompListener;
        }
        if(mRequestQueue != null)
            mRequestQueue.addFail(this);

        if (listener != null) {
            listener.onResponseError(this, errorResponse);
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
        return mPriority;
    }

    public void setPriority(Priority mPriority) {
        this.mPriority = mPriority;
    }

    @Override
    public int compareTo(@NonNull Request<T> o) {
        Priority top = this.getPriority();
        Priority down = o.getPriority();
        return top == down ? 0 : down.ordinal() - top.ordinal();
    }

}
