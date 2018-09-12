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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import velites.android.utility.misc.RxHelper;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.RxUtil;

public class MainActivity extends BaseActivity {

    public static void launchMe(Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        ctx.startActivity(intent);
    }

    private String mToken;

    class Widgets {
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
            } else {
                tvLogin.setText(R.string.label_login);
                tvLogout.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.main_login_area)
        void doLogin(View view){
            if (!loggedIn) {
                LoginActivity.launchMe(MainActivity.this);
            }
        }
        @OnClick(R.id.main_logout)
        void doLogout(View view){
            TokenInfo token = getAppAssistant().getApi().getToken();
            String user = token == null ? null : token.account.loginName;
            getAppAssistant().getApi().clearToken();
            getAppAssistant().getApi().createAsyncApi().logout()
                    .subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                    .subscribe(new ApiService.ApiObserver(MainActivity.this) {
                        @Override
                        public boolean processResult(Object o, ApiResult res) {
                            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, MainActivity.this,"Logged out from user: %s", user));
                            return true;
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
        ButterKnife.bind(widgets, this);
        getAppAssistant().getApi().getTokenChanged()
                .observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                .subscribe(loggedIn -> this.widgets.render(), RxUtil.simpleErrorConsumer);
    }

    private void decorateToolbar() {
        getToolbar().setNavigationIcon(null);
    }
}
