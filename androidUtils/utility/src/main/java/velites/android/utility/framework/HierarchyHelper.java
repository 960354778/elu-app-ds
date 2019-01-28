package velites.android.utility.framework;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * Created by regis on 17/4/27.
 */

public final class HierarchyHelper {
    private HierarchyHelper() {}

    public static void moveView(View v, ViewGroup dest) {
        if (v == null || dest == null) {
            return;
        }
        ViewParent vp = v.getParent();
        if (vp instanceof ViewGroup) {
            ((ViewGroup) vp).removeView(v);
        }
        dest.addView(v);
    }

    public static void moveViews(View src, ViewGroup dest, int[] ids) {
        if (src == null || dest == null || ids == null) {
            return;
        }
        for (int id : ids) {
            moveView(src.findViewById(id), dest);
        }
    }
}
