package velites.android.utility.framework;

import android.content.IntentFilter;

/**
 * Created by regis on 17/4/20.
 */

public final class BroadcastHelper {
    private BroadcastHelper() {}

    public static final IntentFilter createIntentFilter(String... actions) {
        IntentFilter filter = new IntentFilter();
        if (actions != null) {
            for (String action : actions) {
                filter.addAction(action);
            }
        }
        return filter;
    }
}
