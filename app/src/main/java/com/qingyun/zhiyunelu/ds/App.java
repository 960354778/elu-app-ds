package com.qingyun.zhiyunelu.ds;

import com.qingyun.zhiyunelu.ds.bcst.CallPhoneBCSTR;

import velites.android.utility.framework.BaseApplication;

/**
 * Created by regis on 2017/11/29.
 */

public class App extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        AppAssistant.ensureInit(this);
        CallPhoneBCSTR.ensureInit(this);
    }
}
