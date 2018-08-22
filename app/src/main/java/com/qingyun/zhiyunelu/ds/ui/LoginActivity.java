package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.data.ApiResult;
import com.qingyun.zhiyunelu.ds.data.LoginDto;
import com.qingyun.zhiyunelu.ds.op.ApiService;

import org.w3c.dom.NameList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import velites.android.support.ui.BaseTemplatedActivity;
import velites.android.utility.helpers.RxHelper;
import velites.android.utility.helpers.ToastUtil;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.DateTimeUtil;
import velites.java.utility.misc.ExceptionUtil;
import velites.java.utility.misc.StringUtil;

/**
 * Created by luohongzhen on 13/12/2017.
 */

public class LoginActivity extends BaseActivity {

    class Widgets {
        @BindView(R.id.login_account)
        EditText etAccount;
        @BindView(R.id.login_password)
        EditText etPassword;
        @BindView(R.id.login_login)
        Button btLogin;

        @OnClick(R.id.login_login)
        void onLoginClick(View view) {
            String name = etAccount.getText().toString();
            String pwd = etPassword.getText().toString();
            if (StringUtil.isNullOrEmpty(name) || StringUtil.isNullOrEmpty(pwd)) {
                ToastUtil.showToastShort(LoginActivity.this, R.string.warn_missing_account_password);
                return;
            }
            getAppAssistant().getPrefs().setLastUsername(name);
            getAppAssistant().getPrefs().setLastPassword(pwd);
            LoginDto login = new LoginDto();
            login.loginName = name;
            login.password = pwd;
            getAppAssistant().getApi().getDefaultApi().login(login)
                    .subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                    .subscribe(new ApiService.ApiObserver<ApiResult>() {
                        @Override
                        public void onSuccess(ApiResult apiResult) {
                            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, "LoginActivity","thread name:%s loginInfo: %s", Thread.currentThread().getName(), apiResult.toString()));
                        }

                        @Override
                        public void onFail(Throwable e) {
                            ExceptionUtil.swallowThrowable(e);
                        }
                    });
        }
    }

    private final Widgets widgets = new Widgets();

    public static void launchMe(Context ctx) {
        Intent intent = new Intent(ctx, LoginActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorateToolbar();
        ButterKnife.bind(widgets, this);
        initNamePwd();
    }

    private void initNamePwd(){
//        widgets.etAccount.setText(AppAssistant.getPrefs().getStr(Constants.PrefsKey.ACCOUNT_NAME_KEY));
//        widgets.etPassword.setText(AppAssistant.getPrefs().getStr(Constants.PrefsKey.ACCOUNT_PWD_KEY));
    }

    @Override
    protected Integer getContentResId() {
        return R.layout.activity_login;
    }

    private void decorateToolbar() {
        getToolbar().setNavigationIcon(null);
    }
}
