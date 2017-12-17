package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.data.LoginInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import velites.android.utility.utils.ToastUtil;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;
import velites.java.utility.misc.StringUtil;

/**
 * Created by luohongzhen on 13/12/2017.
 */

public class LoginActivity extends BaseTemplatedActivity {

    class Widgets{
        @BindView(R.id.TVloginAccount)
        EditText mLoginET;
        @BindView(R.id.TVloginPwd)
        EditText mPwdET;
        @BindView(R.id.BTLogin)
        Button mLoginButton;

        @OnClick(R.id.BTLogin)
        void onLoginClick(View view){
            String name = mLoginET.getText().toString();
            String pwd = mPwdET.getText().toString();
            if(StringUtil.isNullOrEmpty(name) || StringUtil.isNullOrEmpty(pwd)){
                ToastUtil.showToastShort(AppAssistant.getDefaultContext(), "请输入账号或密码");
                return;
            }
            AppAssistant.getPrefs().setStr(Constants.PrefsKey.ACCOUNT_NAME_KEY, name);
            AppAssistant.getPrefs().setStr(Constants.PrefsKey.ACCOUNT_PWD_KEY, pwd);

            AppAssistant.getApi().login(new LoginInfo.LoginRequest(name, pwd))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<LoginInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(LoginInfo loginInfo) {
                            LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, "LoginActivity","thread name:%s loginInfo: %s", Thread.currentThread().getName(), loginInfo.toString()));
                            if(loginInfo != null && !StringUtil.isNullOrEmpty(loginInfo.getToken())){
                                AppAssistant.getPrefs().setStr(Constants.PrefsKey.AUTH_TOKEN_KEY, loginInfo.getToken());
                                AppAssistant.getPrefs().setStr(Constants.PrefsKey.LOGIN_NAME, loginInfo.getData() != null ?loginInfo.getData().getDisplayName():"");

                                ToastUtil.showToastShort(AppAssistant.getDefaultContext(), "登录成功");
                                finish();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            ToastUtil.showToastShort(LoginActivity.this, "请求异常");
                        }

                        @Override
                        public void onComplete() {
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
        widgets.mLoginET.setText(AppAssistant.getPrefs().getStr(Constants.PrefsKey.ACCOUNT_NAME_KEY));
        widgets.mPwdET.setText(AppAssistant.getPrefs().getStr(Constants.PrefsKey.ACCOUNT_PWD_KEY));
    }

    @Override
    protected Integer getContentResId() {
        return R.layout.activity_login;
    }

    private void decorateToolbar() {
        getToolbar().setNavigationIcon(null);
    }

    @Override
    protected String getTitleStr() {
        return "登录";
    }
}
