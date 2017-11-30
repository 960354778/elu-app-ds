package com.qingyun.zhiyunelu.ds.bcst;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;

/**
 * Created by luohongzhen on 30/11/2017.
 */

public class CallPhoneBCSTR extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, this,"onReceive action:%s Phone number is %s", intent.getAction(), intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)));
    }
}
