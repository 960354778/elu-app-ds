package velites.android.support.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import velites.android.utility.framework.BaseApplication;
import velites.java.utility.generic.Func0;
import velites.java.utility.generic.Func2;
import velites.java.utility.misc.StringUtil;
import velites.java.utility.misc.SyntaxUtil;
import velites.java.utility.thread.RunnableKeepingScope;

/**
 * Created by regis on 17/4/24.
 */

public class RequestPermissionAssistant extends Fragment {

    private static final String FRAGMENT_TAG = RequestPermissionAssistant.class.getSimpleName();

    private int requestCode = -1;
    private final Map<String, Boolean> permissions = new HashMap<>();
    private Func2<Func0<Boolean>, String[], Boolean> callback;
    private boolean forceRequest;

    private void ensurePermissions(final boolean mustRequest) {
        boolean shouldRequest = mustRequest;
        final List<String> toRequest = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
            Boolean b = entry.getValue();
            if (b != null) {
                toRequest.add(entry.getKey());
                shouldRequest |= b;
            }
        }
        final String[] ps = toRequest.toArray(new String[0]);
        Func0<Boolean> func = shouldRequest && ps.length > 0 ? new Func0<Boolean>() {
            @Override
            public Boolean f() {
                boolean run = mustRequest || Build.VERSION.SDK_INT >= 23;
                if (run) {
                    BaseApplication.defaultMainHandler.post(new RunnableKeepingScope(new Runnable() {
                        @Override
                        public void run() {
                            requestPermissions(ps, requestCode);
                        }
                    }));
                }
                return run;
            }
        } : null;
        if (mustRequest && func != null || callback == null || !SyntaxUtil.nvl(callback.f(func, ps))) {
            if (func != null) {
                func.f();
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ensurePermissions(forceRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (this.requestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    this.permissions.put(permissions[i], null);
                }
            }
        }
        ensurePermissions(false);
    }

    private void checkRegisterPermissions(Context ctx, String[] ps, boolean isMustHave) {
        if (ps != null) {
            for (String p : ps) {
                if (!StringUtil.isNullOrEmpty(p) && ContextCompat.checkSelfPermission(ctx, p) != PackageManager.PERMISSION_GRANTED) {
                    permissions.put(p, isMustHave);
                }
            }
        }
    }

    /**
     * @param activity
     * @param requestCode
     * @param forceRequest {@code false} mean not request if no {@code mustHave} permission missing.
     * @param callback {@link Func2#f}({@link Func0<Boolean>} {@code continueRequest}, {@link String}[] {@code permissionsToRequest}) => {@link Boolean} {@code handlingOutside},
     *                                {@code continueRequest} {@code null} means all {@code mustHave} got granted, its return value mean actually requested ({@code true}) or not ({@code false}/{@code null}).
     *                                {@code handlingOutside} {@code true} means {@code continueRequest} is taken by caller, otherwise({@code false} or {@code null}, invoked directly right after.
     * @param mustHave
     * @param niceToHave
     * @return
     */
    public static RequestPermissionAssistant startRequestPermission(FragmentActivity activity, int requestCode, boolean forceRequest, Func2<Func0<Boolean>, String[], Boolean> callback, String[] mustHave, String[] niceToHave) {
        RequestPermissionAssistant frag = new RequestPermissionAssistant();
        frag.requestCode = requestCode;
        frag.callback = callback;
        frag.forceRequest = forceRequest;
        frag.checkRegisterPermissions(activity, niceToHave, false);
        frag.checkRegisterPermissions(activity, mustHave, true);
        activity.getSupportFragmentManager().beginTransaction().add(frag, FRAGMENT_TAG).commit();
        return frag;
    }
}
