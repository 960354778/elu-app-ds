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
        ret.network.apiRootUrl = "https://api.demo.zhiyunyilu.com/";
        return ret;
    }

    static {
        SETTING_CHANNEL = buildChannelSetting();
    }
}
