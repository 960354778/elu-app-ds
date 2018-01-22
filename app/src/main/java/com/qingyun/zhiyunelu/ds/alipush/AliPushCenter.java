package com.qingyun.zhiyunelu.ds.alipush;

import android.content.Context;

import com.alibaba.sdk.android.ams.common.global.AmsGlobalHolder;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;

import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;

/**
 * Created by luohongzhen on 22/01/2018.
 */

public class AliPushCenter {
    private static volatile AliPushCenter mInstance;
    private CloudPushService pushService;

    public static AliPushCenter getInstance(){
        if(mInstance == null){
            synchronized (AliPushCenter.class){
                if(mInstance == null){
                    mInstance = new AliPushCenter();
                }
            }
        }
        return mInstance;
    }

    private AliPushCenter() {
    }

    public AliPushCenter initPush(Context ctx){
        PushServiceFactory.init(ctx);
        pushService = PushServiceFactory.getCloudPushService();
        pushService.register(ctx, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, AliPushCenter.class, "init ali push success:%s device id:%s Appkey:%s", response, pushService.getDeviceId(),  AmsGlobalHolder.getAppMetaData("com.alibaba.app.appkey")));
                pushService.setPushIntentService(MyMessageIntentService.class);
            }
            @Override
            public void onFailed(String errorCode, String errorMessage) {
                LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, AliPushCenter.class, "init ali push fail code:%s errormsg:%s device id:%s", errorCode, errorMessage, pushService.getDeviceId()));
            }
        });

        return mInstance;
    }

    public void bindAccount(String account){
        if(pushService != null){
            pushService.bindAccount(account, new CommonCallback() {
                @Override
                public void onSuccess(String response) {
                    LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, AliPushCenter.class, "bind account success:%s", response));
                }

                @Override
                public void onFailed(String errorCode, String errorMessage) {
                    LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, AliPushCenter.class, "bind account fail code:%s errormsg:%s", errorCode, errorMessage));
                }
            });
        }
    }

    public void unbindAccount(){
        if(pushService != null){
            pushService.unbindAccount(new CommonCallback() {
                @Override
                public void onSuccess(String response) {
                    LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, AliPushCenter.class, "unbind account success:%s", response));
                }

                @Override
                public void onFailed(String errorCode, String errorMessage) {
                    LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, AliPushCenter.class, "bind account fail code:%s errormsg:%s", errorCode, errorMessage));
                }
            });
        }
    }
}
