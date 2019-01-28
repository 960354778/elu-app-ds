package velites.android.utility.misc;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by luohongzhen on 24/01/2018.
 */

public class ScreenHelper {

    public static int dip2px(float dipValue, Context ctx) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue, Context ctx) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(float pxValue, Context ctx) {
        final float fontScale = ctx.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(float spValue, Context ctx) {
        final float fontScale = ctx.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int getScreenWidth(Context ctx) {
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context ctx) {
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }
}
