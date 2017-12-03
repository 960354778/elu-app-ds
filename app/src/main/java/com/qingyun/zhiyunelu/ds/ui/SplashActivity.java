package com.qingyun.zhiyunelu.ds.ui;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.wechat.WxManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import velites.android.support.ui.RequestPermissionAssistant;
import velites.android.utility.framework.BaseApplication;
import velites.android.utility.framework.EnvironmentInfo;
import velites.java.utility.generic.Func0;
import velites.java.utility.generic.Func2;
import velites.java.utility.generic.Tuple2;
import velites.java.utility.misc.SyntaxUtil;
import velites.java.utility.thread.RunnableKeepingScope;

/**
 * Created by regis on 17/4/23.
 */

public class SplashActivity extends BaseActivity {

    private static final long STAY_BEFORE_ENTER_IN_MS = 2000;

    class Widgets {
        @BindView(R.id.splash_version)
        TextView version;
    }
    private final Widgets widgets = new Widgets();

    private long leavingPointThreshold;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        leavingPointThreshold = SystemClock.uptimeMillis() + STAY_BEFORE_ENTER_IN_MS;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(widgets, this);
        Tuple2<String, Integer> v = EnvironmentInfo.obtainAppVersion(this);
        widgets.version.setText(getString(R.string.content_splash_version, v.v1, v.v2));
        RequestPermissionAssistant.startRequestPermission(this, Constants.Codes.REQUEST_CODE_REQUIRE_PERMISSION, !AppAssistant.getPrefs().getPermissionRequested(), new Func2<Func0<Boolean>, String[], Boolean>() {
            @Override
            public Boolean f(Func0<Boolean> arg1, String[] arg2) {
                if (arg1 == null) {
                    AppAssistant.getPrefs().setPermissionRequested(true);
                    gotPermission = true;
                    scheduleLeave(false);
                } else if (!SyntaxUtil.nvl(arg1.f())) {
                    scheduleLeave(true); // for those api < 23
                }
                return true;
            }
        }, Constants.PERMISSIONS_MUST_HAVE, Constants.PERMISSIONS_NICE_TO_HAVE);
        WxManager.test();
    }

    @Override
    protected boolean isAtLast() {
        return true;
    }

    private boolean gotPermission = false;
    private final Runnable doLeave = new RunnableKeepingScope() {
        @Override
        protected void doRun() {
            if (gotPermission) {
                MainActivity.launchMe(SplashActivity.this);
            }
            finish();
        }
    };

    private void scheduleLeave(boolean immediate) {
        BaseApplication.defaultMainHandler.removeCallbacks(doLeave);
        if (immediate) {
            doLeave.run();
        } else {
            BaseApplication.defaultMainHandler.postAtTime(doLeave, leavingPointThreshold);
        }
    }
}
