package velites.android.utility.misc;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.telephony.TelephonyManager;

import java.util.List;

/**
 * Created by luohongzhen on 29/01/2018.
 */

public class SystemHelper {

    public static boolean isAppProcess(Context context) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
        int pid = Process.myPid();
        if (runningAppProcesses != null && runningAppProcesses.size() != 0) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                if(runningAppProcessInfo.pid == pid && runningAppProcessInfo.processName != null && runningAppProcessInfo.processName.equals(context.getPackageName())){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param ctx
     * @return yourself phone num or null
     * because the old sim card may not get the phone number
     */
    public static String getMyselfPhone(Context ctx){
        try{
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getLine1Number();
        }catch (SecurityException e){
            return null;
        }
    }
}
