package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.data.TokenInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private String mToken;

    public static void launchMe(Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        ctx.startActivity(intent);
    }

    class Widgets {
        boolean loggedIn;
        @BindView(R.id.main_login_area)
        LinearLayout llLoginArea;
        @BindView(R.id.main_login_display)
        TextView tvLogout;
        @BindView(R.id.main_logout)
        TextView tvLogin;
        @BindView(R.id.main_manually_fetch)
        TextView tvManuallyFetch;

        private void render() {
            TokenInfo token = getAppAssistant().getApi().getToken();
            loggedIn = token != null;
            if (loggedIn) {
                tvLogin.setText(token.account.displayName);
                tvLogout.setVisibility(View.GONE);
            } else {
                tvLogin.setText(R.string.label_login);
                tvLogout.setVisibility(View.VISIBLE);
            }
        }

        @OnClick(R.id.main_login_area)
        void onTabClick(View view){
            if (!loggedIn) {
                LoginActivity.launchMe(MainActivity.this);
            }
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
        widgets.render();
    }

    private void decorateToolbar() {
        getToolbar().setNavigationIcon(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        NotifyShowActivity.launchMe(this, new Gson().fromJson(test, OrderInfo.class));
    }

    private void loginDispose(){
//        long expire = AppAssistant.getPrefs().getLong(Constants.PrefsKey.AUTH_EXPIRE_KEY);
//        if(StringUtil.isNullOrEmpty(mToken) || System.currentTimeMillis() > expire){
//            LoginActivity.launchMe(MainActivity.this);
//        }
    }

    private boolean checkLogin(){
//        String token = AppAssistant.getPrefs().getStr(Constants.PrefsKey.AUTH_TOKEN_KEY);
//        long expire = AppAssistant.getPrefs().getLong(Constants.PrefsKey.AUTH_EXPIRE_KEY);
//        if(StringUtil.isNullOrEmpty(token) && System.currentTimeMillis() < expire){
//            ToastUtil.showToastShort(this, "请先登录");
//            return false;
//        }
        return true;
    }
}
