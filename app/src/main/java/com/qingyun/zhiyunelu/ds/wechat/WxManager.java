package com.qingyun.zhiyunelu.ds.wechat;

import com.qingyun.zhiyunelu.ds.Constants;

import velites.android.support.wx.WechatHelper;
import velites.android.utility.root.RootUtility;
import velites.java.utility.generic.Action2;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;

/**
 * Created by luohongzhen on 03/12/2017.
 */

public class WxManager {

    public static void test(){

        if(RootUtility.isCanRoot()){
            RootUtility.runAsRoot("chmod -R 777 "+Constants.FilePaths.WX_MICROMS_PATH);
        }

        WechatHelper.requestPwd(Constants.FilePaths.WX_SHARE_PREFS_PATH, "key", new Action2<String, String>() {
            @Override
            public void a(String arg1, String arg2) {
                LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, WxManager.class, "wx db pwd %s", arg1));
            }
        });
    }
}
