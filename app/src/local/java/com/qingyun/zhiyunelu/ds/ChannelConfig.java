package com.qingyun.zhiyunelu.ds;

import com.qingyun.zhiyunelu.ds.data.Setting;

/**
 * Created by regis on 17/4/25.
 */

public final class ChannelConfig {
    private ChannelConfig() {}

    public static final Setting SETTING_CHANNEL;

    private static Setting buildChannelSetting() {
        Setting ret = new Setting();
        ret.network = new Setting.Network();
        ret.network.apiRootUrl = "http://192.168.88.54:5008/";
        ret.logic = new Setting.Logic();
//        ret.logic.pollingIntervalMs = 2 * 60 * 1000L;
        return ret;
    }

    static {
        SETTING_CHANNEL = buildChannelSetting();
    }
}
