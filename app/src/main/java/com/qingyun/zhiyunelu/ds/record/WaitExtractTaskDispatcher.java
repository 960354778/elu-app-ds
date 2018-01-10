package com.qingyun.zhiyunelu.ds.record;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.net.NetLife.Request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import velites.android.utility.utils.NetUtil;
import velites.java.utility.thread.ThreadUtil;

/**
 * Created by luohongzhen on 10/01/2018.
 */

public class WaitExtractTaskDispatcher {
    private static final BlockingQueue<Request<?>> mWaitQueue = new LinkedBlockingDeque<>();

    public static void add(Request<?> task) {
        mWaitQueue.add(task);
    }

    public static void remove() {
        ThreadUtil.runInNewThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request<?> task = mWaitQueue.take();
                    while (!NetUtil.isConNetWok(AppAssistant.getDefaultContext())) {
                        Thread.sleep(500);
                    }
                    if (task != null) {
                        AppAssistant.getRequestQueue().add(task);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "waitQueue", null);
    }

}
