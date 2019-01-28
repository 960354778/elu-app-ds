package velites.android.utility.framework;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by regis on 17/4/21.
 */

public abstract class BaseApplication extends Application {
    public static final Handler defaultMainHandler = new Handler(Looper.getMainLooper());
}
