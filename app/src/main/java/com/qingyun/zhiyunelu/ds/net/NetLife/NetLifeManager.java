package com.qingyun.zhiyunelu.ds.net.NetLife;

import android.content.Context;

/**
 * Created by luohongzhen on 07/01/2018.
 */

public class NetLifeManager {

    public static RequestQueue newRequestQueue(Context ctx){
        RequestQueue queue = new RequestQueue();
        return queue;
    }
}
