package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.data.ApiResult;
import com.qingyun.zhiyunelu.ds.data.LoginDto;
import com.qingyun.zhiyunelu.ds.op.ApiService;
import com.trello.rxlifecycle2.android.ActivityEvent;

import butterknife.BindView;
import butterknife.OnClick;
import velites.android.support.ui.BaseLayoutWidget;
import velites.android.utility.misc.RxHelper;
import velites.android.utility.misc.ToastHelper;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.StringUtil;

/**
 * Created by luohongzhen on 13/12/2017.
 */

public class LoginActivity extends BaseActivity {

    public static void launchMe(Context ctx) {
        Intent intent = new Intent(ctx, LoginActivity.class);
        ctx.startActivity(intent);
    }

    class Widgets extends BaseLayoutWidget {
        @BindView(R.id.login_account)
        EditText etAccount;
        @BindView(R.id.login_password)
        EditText etPassword;
        @BindView(R.id.login_login)
        TextView btLogin;

        private void render(){
            etAccount.setText(getAppAssistant().getPrefs().getLastUsername());
            etPassword.setText(getAppAssistant().getPrefs().getLastPassword());
        }

        @OnClick(R.id.login_login)
        void onLoginClick(View view) {
            String name = etAccount.getText().toString();
            String pwd = etPassword.getText().toString();
            if (StringUtil.isNullOrEmpty(name) || StringUtil.isNullOrEmpty(pwd)) {
                ToastHelper.showToastShort(LoginActivity.this, R.string.warn_missing_account_password);
                return;
            }
            getAppAssistant().getPrefs().setLastUsername(name);
            getAppAssistant().getPrefs().setLastPassword(pwd);
            LoginDto login = new LoginDto();
            login.loginName = name;
            login.password = pwd;
            getAppAssistant().getApi().createAsyncApi().login(login)
                    .subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(new ApiService.ApiObserver(LoginActivity.this) {
                        @Override
                        public boolean processResult(Object o, ApiResult res) {
                            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, LoginActivity.this,"Logged in as user: %s", res.token.account.loginName));
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            LoginActivity.this.finish();
                            return true;
                        }
                    });
        }
    }

    private final Widgets widgets = new Widgets();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        decorateToolbar();
        widgets.bind(this);
        widgets.render();
    }



    private void decorateToolbar() {
        getToolbar().setNavigationIcon(null);
    }
}
