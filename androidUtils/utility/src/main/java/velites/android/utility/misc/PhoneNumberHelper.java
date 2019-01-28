package velites.android.utility.misc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.StringUtil;

/**
 * Created by regis on 2018/3/8.
 */

public final class PhoneNumberHelper {
    private PhoneNumberHelper() {
    }

    public static String normalizeTelNumber(String number) {
        if (number == null) {
            return null;
        }
        return number.replaceAll("[\\s\\-]", "");
    }

    public static String getChinaCallableNumber(String number, String areaCode) {
        if (StringUtil.isNullOrSpace(areaCode)) {
            return number;
        }
        return "0086" + areaCode.replaceFirst("^0+", "") + number;
    }

    public static String getDisplayNumber(String number, String areaCode, String extension) {
        return (StringUtil.isNullOrSpace(areaCode) ? "" : areaCode + "-") + number + (StringUtil.isNullOrSpace(extension) ? "" : "*" + extension);
    }

    @SuppressLint("MissingPermission")
    public static void callOut(Context ctx, String phoneNumber) {
        if (ctx != null && !StringUtil.isNullOrSpace(phoneNumber)) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + phoneNumber);
            intent.setData(data);
            ctx.startActivity(intent);
            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, PhoneNumberHelper.class, "Dialed out phone number: %s", phoneNumber));
        }
    }
}
