package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.data.ApiResult;
import com.qingyun.zhiyunelu.ds.data.TokenInfo;
import com.qingyun.zhiyunelu.ds.op.ApiService;
import com.trello.rxlifecycle2.android.ActivityEvent;

import butterknife.BindView;
import butterknife.OnClick;
import velites.android.support.ui.BaseLayoutWidget;
import velites.android.utility.misc.RxHelper;
import velites.android.utility.misc.ToastHelper;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.RxUtil;
import velites.java.utility.misc.SyntaxUtil;

public class MainActivity extends BaseActivity {

    public static void launchMe(Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        ctx.startActivity(intent);
    }

    private String mToken;

    class Widgets extends BaseLayoutWidget {
        boolean loggedIn;
        @BindView(R.id.main_login_area)
        LinearLayout llLoginArea;
        @BindView(R.id.main_login_display)
        TextView tvLogin;
        @BindView(R.id.main_logout)
        TextView tvLogout;
        @BindView(R.id.main_manually_fetch)
        TextView tvManuallyFetch;

        private void render() {
            TokenInfo token = getAppAssistant().getApi().getToken();
            loggedIn = token != null;
            if (loggedIn) {
                tvLogin.setText(token.account.displayName);
                tvLogout.setVisibility(View.VISIBLE);
                tvManuallyFetch.setVisibility(View.VISIBLE);
            } else {
                tvLogin.setText(R.string.label_login);
                tvLogout.setVisibility(View.GONE);
                tvManuallyFetch.setVisibility(View.INVISIBLE);
            }
        }

        @OnClick(R.id.main_login_area)
        void doLogin(View view) {
            if (!loggedIn) {
                LoginActivity.launchMe(MainActivity.this);
            }
        }

        @OnClick(R.id.main_logout)
        void doLogout(View view) {
            TokenInfo token = getAppAssistant().getApi().getToken();
            String user = token == null ? null : token.account.loginName;
            getAppAssistant().getApi().clearToken();
            getAppAssistant().getApi().createAsyncApi().logout()
                    .subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(new ApiService.ApiObserver(MainActivity.this) {
                        @Override
                        public boolean processResult(Object o, ApiResult res) {
                            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, MainActivity.this, "Logged out from user: %s", user));
                            return true;
                        }
                    });
        }

        @OnClick(R.id.main_manually_fetch)
        void doManuallyFetch(View view) {
            getAppAssistant().getMessaging().syncTaskMessages()
                    .subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(new ApiService.ApiErrorObserver<Boolean>(MainActivity.this) {
                        @Override
                        public void onNext(Boolean res) {
                            super.onNext(res);
                            if (SyntaxUtil.nvl(res, false)) {
                                TasksActivity.launchMe(MainActivity.this);
                            } else {
                                Popups.buildAlert(MainActivity.this, getString(R.string.warn_no_cached_dial_message), true);
                            }
                        }
                    });
        }
    }
    private final Widgets widgets = new Widgets();

    @Override
    protected Integer getContentResId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean isAtLast() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorateToolbar();
        widgets.bind(this);
        getAppAssistant().getApi().getLoginStateChanged()
                .observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                .compose(this.bindToLifecycle())
                .subscribe(loggedIn -> this.widgets.render(), RxUtil.simpleErrorConsumer);
        if(!getAppAssistant().getSms().checkMySelfPhoneSet()){
            ToastHelper.showToastLong(this, R.string.warn_need_self_phone_number);
        }
    }

    private void decorateToolbar() {
        getToolbar().setNavigationIcon(null);
    }
}
