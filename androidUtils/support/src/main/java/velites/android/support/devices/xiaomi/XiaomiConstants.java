package velites.android.support.devices.xiaomi;

import android.os.Environment;

import velites.java.utility.misc.PathUtil;

public class XiaomiConstants {
    private XiaomiConstants() {}

    public static final String MIUI_SOUND_DIR = PathUtil.concat(Environment.getExternalStorageDirectory().getAbsolutePath(), "MIUI/sound_recorder/call_rec");
}
