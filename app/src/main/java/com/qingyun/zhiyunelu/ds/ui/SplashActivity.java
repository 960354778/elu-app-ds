package com.qingyun.zhiyunelu.ds.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.qingyun.zhiyunelu.ds.App;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.data.TokenInfo;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import velites.android.support.ui.BaseLayoutWidget;
import velites.android.support.ui.RequestPermissionAssistant;
import velites.android.utility.framework.EnvironmentInfo;
import velites.android.utility.misc.RxHelper;
import velites.android.utility.misc.ToastHelper;
import velites.java.utility.generic.Func0;
import velites.java.utility.generic.Func2;
import velites.java.utility.generic.Tuple2;
import velites.java.utility.misc.RxUtil;
import velites.java.utility.misc.SyntaxUtil;

public class SplashActivity extends BaseActivity {

    private static final long STAY_FROM_ENTER_IN_MS = 3000;
    private static final long STAY_FROM_INIT_IN_MS = 2000;
    private static final long INTERVAL_CHECK_MS = 3000;

    boolean loggedIn;
    private TextView tv_login;

    class Widgets extends BaseLayoutWidget {
       /* @BindView(R.id.splash_version)
        TextView version;*/
    }
    private final Widgets widgets = new Widgets();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Subject<Integer> wait = ReplaySubject.create();
        Observable.just(0).delay(STAY_FROM_ENTER_IN_MS, TimeUnit.MILLISECONDS).subscribe(wait);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv_login = (TextView) findViewById(R.id.tv_login);
        TokenInfo token = getAppAssistant().getApi().getToken();
        loggedIn = token != null;
        if (loggedIn){
            tv_login.setVisibility(View.GONE);
        }else {
            tv_login.setVisibility(View.VISIBLE);
        }
        tv_login.setClickable(false);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                finish();
            }
        });
        widgets.bind(this);
        Observable.create(RxUtil.buildSimpleFuncObservable(() -> {
            App.getInstance().getAssistant().awaitInit();
            awaitInit();
            return SyntaxUtil.nvl(App.getInstance().getAssistant().checkEnv());
        })).subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                .compose(this.bindToLifecycle())
                .subscribe(o -> this.showInfo(o, wait), RxUtil.simpleErrorConsumer);
    }

    private void showInfo(final int msg, Subject<Integer> wait) {
        Tuple2<String, Integer> v = EnvironmentInfo.obtainAppVersion(this);
        //widgets.version.setText(getString(R.string.content_splash_version, v.v1, v.v2, App.getInstance().getAssistant().isDebug() ? "DEBUG" : ""));
        RequestPermissionAssistant.startRequestPermission(this, Constants.Misc.REQUEST_CODE_REQUIRE_PERMISSION, !App.getInstance().getAssistant().getPrefs().getPermissionRequested(), new Func2<Func0<Boolean>, String[], Boolean>() {
            @Override
            public Boolean f(Func0<Boolean> arg1, String[] arg2) {
                if (arg1 == null) {
                    App.getInstance().getAssistant().getPrefs().setPermissionRequested(true);
                    warnOrJump(msg, wait);
                } else if (!SyntaxUtil.nvl(arg1.f())) {
                    ToastHelper.showToastLong(SplashActivity.this, R.string.warn_requires_permission);
                    SplashActivity.this.finish();
                }
                return true;
            }
        }, Constants.Misc.PERMISSIONS_MUST_HAVE, Constants.Misc.PERMISSIONS_NICE_TO_HAVE);
    }

    private void warnOrJump(int msg, Subject<Integer> wait) {
        if (msg == 0) {
            wait.zipWith(Observable.just(0).delay(STAY_FROM_INIT_IN_MS, TimeUnit.MILLISECONDS), (integer, integer2) -> 0)
                    .subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                    .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(o -> {
                        tv_login.setClickable(true);
                        TokenInfo token = getAppAssistant().getApi().getToken();
                        loggedIn = token != null;
                        if (loggedIn) {
                            MainActivity.launchMe(SplashActivity.this);
                            SplashActivity.this.finish();
                        } else {
                            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                            finish();
                        }
                    }, RxUtil.simpleErrorConsumer);
        } else {
            ToastHelper.showToastLong(this, msg);
            Observable.just(0).delay(INTERVAL_CHECK_MS, TimeUnit.MILLISECONDS)
                    .subscribeOn(RxHelper.createKeepingScopeIOSchedule())
                    .map(integer -> SyntaxUtil.nvl(App.getInstance().getAssistant().checkEnv()))
                    .observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                    .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(s -> this.warnOrJump(s, wait), RxUtil.simpleErrorConsumer);
        }
    }

    @Override
    protected boolean isAtLast() {
        return true;
    }
}
