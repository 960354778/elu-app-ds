package com.qingyun.zhiyunelu.ds.net.NetLife;

import android.os.Process;

import java.util.concurrent.BlockingQueue;

/**
 * Created by luohongzhen on 07/01/2018.
 */

public class NetworkDispatcher extends Thread{

    private final BlockingQueue<Request<?>> mQueue;
    private volatile  boolean mQuit = false;

    public NetworkDispatcher(BlockingQueue<Request<?>> queue) {
        this.mQueue = queue;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true){
            try {
                processRequest();
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
            }
        }
    }

    public void quit(){
        mQuit = true;
        interrupt();
    }

    private void processRequest() throws InterruptedException {
        Request<?> request = mQueue.take();
        if(request.isCanceled()){
            request.finish();
            request.notifyListenerResponseNotUsable();
            return;
        }
        try{
            Response<?> response = request.performRequest();
            request.notifyListenerResponseReceived(response);
        }catch (Exception e){
            request.notifyListenerErrorReceived(Response.error(e.getMessage()));
        }finally {
            request.finish();
        }
    }
}
