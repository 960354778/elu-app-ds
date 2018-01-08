package com.qingyun.zhiyunelu.ds.net.NetLife;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import velites.java.utility.generic.Func1;
import velites.java.utility.misc.StringUtil;

/**
 * Created by luohongzhen on 07/01/2018.
 */

public class RequestQueue {

    private static final int DEFAULT_NETWORK_THEAD_POOL_SIZE = 4;

    private final Set<Request<?>> mCurrentRequests = new HashSet<Request<?>>();
    private final Set<Request<?>> mFailRequests = new HashSet<Request<?>>();
    private final PriorityBlockingQueue<Request<?>> mNetworkQueue = new PriorityBlockingQueue<>();
    private final NetworkDispatcher[] mDispatchers;


    public RequestQueue() {
        this(DEFAULT_NETWORK_THEAD_POOL_SIZE);
    }

    public RequestQueue(int threadPoolSize) {
        this.mDispatchers = new NetworkDispatcher[threadPoolSize];
    }

    public void start() {
        stop();
        for (int i = 0; i < mDispatchers.length; i++) {
            NetworkDispatcher networkDispatcher = new NetworkDispatcher(mNetworkQueue);
            mDispatchers[i] = networkDispatcher;
            networkDispatcher.start();
        }

    }

    public void stop() {
        for (final NetworkDispatcher item : mDispatchers) {
            if (item != null) {
                item.quit();
            }
        }
    }

    public <T> Request<T> addFail(Request<T> request){
        request.setRequestQueue(this);
        synchronized (mFailRequests){
            mFailRequests.add(request);
        }
        return request;
    }

    public void runFailRequest(){
        if(mFailRequests != null && mFailRequests.size() > 0){
            for(Request<?> item : mFailRequests){
                if(item != null){
                    item.setPriority(Request.Priority.IMMEDIATE);
                    add(item);
                    mFailRequests.remove(item);
                }
            }
        }
    }

    public <T> Request<T> add(Request<T> request) {
        request.setRequestQueue(this);
        synchronized (mCurrentRequests) {
            mCurrentRequests.add(request);
        }
        mNetworkQueue.add(request);
        return request;
    }

    public <T> void finish(Request<T> request) {
        // Remove from the set of requests currently being processed.
        synchronized (mCurrentRequests) {
            mCurrentRequests.remove(request);
        }
    }

    public void cancelAll(Func1<Request<?>, Boolean> func1){
        synchronized (mCurrentRequests){
            for(Request<?> reuest: mCurrentRequests){
                if(func1.f(reuest)){
                    reuest.cancel();
                }
            }
        }
    }

    public void cancelAll(final String tag) {
       if(!StringUtil.isNullOrEmpty(tag)){
           cancelAll(new Func1<Request<?>, Boolean>() {
               @Override
               public Boolean f(Request<?> arg1) {
                   return tag.equals(arg1.getTag());
               }
           });
       }
    }

}