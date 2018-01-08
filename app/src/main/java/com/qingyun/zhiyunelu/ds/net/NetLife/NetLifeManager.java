package com.qingyun.zhiyunelu.ds.net.NetLife;

/**
 * Created by luohongzhen on 07/01/2018.
 */

public class NetLifeManager {

    public static RequestQueue newRequestQueue() {
        RequestQueue queue = new RequestQueue();
        queue.start();
        return queue;
    }
}
