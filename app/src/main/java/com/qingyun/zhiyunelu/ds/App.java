package com.qingyun.zhiyunelu.ds;

import com.qingyun.zhiyunelu.ds.alipush.AliPushCenter;
import com.qingyun.zhiyunelu.ds.bcst.CallPhoneBCSTR;

import velites.android.utility.framework.BaseApplication;
import velites.android.utility.utils.SystemUtil;

/**
 * Created by regis on 2017/11/29.
 */

public class App extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        if(SystemUtil.isAppProcess(this)){
            AppAssistant.ensureInit(this);
            CallPhoneBCSTR.ensureInit(this);
        }
        AliPushCenter.getInstance().initPush(this);
    }


}
