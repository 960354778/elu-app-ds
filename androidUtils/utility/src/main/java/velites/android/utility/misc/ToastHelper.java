package velites.android.utility.misc;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by luohongzhen on 14/12/2017.
 */

public class ToastHelper {

    public static void showToastShort(Context context, String content){
        ThreadHelper.runOnUiThread(() -> Toast.makeText(context, content, Toast.LENGTH_SHORT).show());
    }
    public static void showToastShort(Context context, int resId){
        ThreadHelper.runOnUiThread(() -> Toast.makeText(context, resId, Toast.LENGTH_SHORT).show());
    }

    public static void showToastLong(Context context, int resId){
        ThreadHelper.runOnUiThread(() -> Toast.makeText(context, resId, Toast.LENGTH_LONG).show());
    }

    public static void showToastLong(Context context, String content){
        ThreadHelper.runOnUiThread(() -> Toast.makeText(context, content, Toast.LENGTH_LONG).show());
    }
}
