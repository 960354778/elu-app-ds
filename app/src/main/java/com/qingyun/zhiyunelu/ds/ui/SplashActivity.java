package com.qingyun.zhiyunelu.ds.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.qingyun.zhiyunelu.ds.App;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.R;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import velites.android.support.devices.xiaomi.XiaomiConstants;
import velites.android.support.ui.RequestPermissionAssistant;
import velites.android.utility.framework.BaseApplication;
import velites.android.utility.framework.EnvironmentInfo;
import velites.android.utility.root.RootUtility;
import velites.android.utility.utils.ToastUtil;
import velites.java.utility.generic.Func0;
import velites.java.utility.generic.Func2;
import velites.java.utility.generic.Tuple2;
import velites.java.utility.misc.FileUtil;
import velites.java.utility.misc.StringUtil;
import velites.java.utility.misc.SyntaxUtil;
import velites.java.utility.thread.RunnableKeepingScope;

/**
 * Created by regis on 17/4/23.
 */

public class SplashActivity extends BaseActivity {

    private static final long STAY_FROM_ENTER_IN_MS = 3000;
    private static final long STAY_FROM_INIT_IN_MS = 2000;
    private static final long INTERVAL_CHECK_MS = 3000;

    class Widgets {
        @BindView(R.id.splash_version)
        TextView version;
    }
    private final Widgets widgets = new Widgets();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Single<Integer> wait = Single.just(0).delay(STAY_FROM_ENTER_IN_MS, TimeUnit.MILLISECONDS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(widgets, this);
        Observable.create((ObservableEmitter<Integer> emitter) -> {
            App.getInstance().getAssistant().awaitInit();
            awaitInit();
            emitter.onNext(SyntaxUtil.nvl(App.getInstance().getAssistant().checkEnv()));
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> this.showInfo(o, wait));
    }

    private void showInfo(final int msg, Single<Integer> wait) {
        Tuple2<String, Integer> v = EnvironmentInfo.obtainAppVersion(this);
        widgets.version.setText(getString(R.string.content_splash_version, v.v1, v.v2, App.getInstance().getAssistant().isDebug() ? "DEBUG" : ""));
        RequestPermissionAssistant.startRequestPermission(this, Constants.Codes.REQUEST_CODE_REQUIRE_PERMISSION, !App.getInstance().getAssistant().getPrefs().getPermissionRequested(), new Func2<Func0<Boolean>, String[], Boolean>() {
            @Override
            public Boolean f(Func0<Boolean> arg1, String[] arg2) {
                if (arg1 == null) {
                    App.getInstance().getAssistant().getPrefs().setPermissionRequested(true);
                    warnOrJump(msg, wait);
                } else if (!SyntaxUtil.nvl(arg1.f())) {
                    ToastUtil.showToastLong(SplashActivity.this, R.string.warn_requires_permission);
                    SplashActivity.this.finish();
                }
                return true;
            }
        }, Constants.PERMISSIONS_MUST_HAVE, Constants.PERMISSIONS_NICE_TO_HAVE);
        this.warnOrJump(msg, wait);
    }

    private void warnOrJump(int msg, Single<Integer> wait) {
        if (msg == 0) {
            wait.zipWith(Single.just(0).delay(STAY_FROM_INIT_IN_MS, TimeUnit.MILLISECONDS), (integer, integer2) -> 0)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(o -> MainActivity.launchMe(SplashActivity.this));
        } else {
            ToastUtil.showToastLong(this, msg);
            Single.just(0).delay(INTERVAL_CHECK_MS, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .map(integer -> SyntaxUtil.nvl(App.getInstance().getAssistant().checkEnv()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> this.warnOrJump(s, wait));
        }
    }

    @Override
    protected boolean isAtLast() {
        return true;
    }
}
