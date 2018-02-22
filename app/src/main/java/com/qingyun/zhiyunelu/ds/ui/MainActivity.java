package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.alipush.AliPushCenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import velites.android.utility.utils.ToastUtil;
import velites.java.utility.misc.StringUtil;

public class MainActivity extends BaseTemplatedActivity {

    private String mToken;

    public static void launchMe(Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        ctx.startActivity(intent);
    }

    class Widgets {

        @BindView(R.id.LLLoginTab)
        LinearLayout LLLoginTab;
        @BindView(R.id.tvLogout)
        TextView mLogoutTab;
        @BindView(R.id.tvLoginTab)
        TextView mLoginTab;
        @BindView(R.id.MyDoctorListTab)
        TextView mMyDoctersTab;
        @BindView(R.id.MyHosListTab)
        TextView mMyHosTab;
        @BindView(R.id.tvDoctersTab)
        TextView mDoctersTab;
        @BindView(R.id.tvhosTab)
        TextView mHosTab;

        @OnClick({R.id.LLLoginTab, R.id.MyDoctorListTab, R.id.MyHosListTab, R.id.tvDoctersTab, R.id.tvhosTab, R.id.tvLogout} )
        void onTabClick(View view){
            switch (view.getId()){
                case R.id.LLLoginTab:
                    if(mLogoutTab.getVisibility() == View.GONE)
                        loginDispose();
                    break;
                case R.id.MyDoctorListTab:
                    if(!checkLogin())
                        return;
                    ViewPagerActivity.lanuchMe(MainActivity.this,"我的医生列表", Constants.Codes.REQUEST_NET_MY_DOCTER_LIST_TAG);
                    break;
                case R.id.MyHosListTab:
                    if(!checkLogin())
                        return;
                    ViewPagerActivity.lanuchMe(MainActivity.this,"我的医院列表", Constants.Codes.REQUEST_NET_MY_HOSPITAL_LIST_TAG);
                    break;
                case R.id.tvDoctersTab:
                    ViewPagerActivity.lanuchMe(MainActivity.this,"医生列表", Constants.Codes.REQUEST_NET_DOCTER_LIST_TAG);
                    break;
                case R.id.tvhosTab:
                    ViewPagerActivity.lanuchMe(MainActivity.this,"医院列表", Constants.Codes.REQUEST_NET_HOSPITAL_LIST_TAG);
                    break;
                case R.id.tvLogout:
                    AppAssistant.getPrefs().setStr(Constants.PrefsKey.AUTH_TOKEN_KEY, "");
                    initView();
                    break;
                    default:
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
    }

    private void decorateToolbar() {
        getToolbar().setNavigationIcon(null);
    }

    private void initView(){
        String displayName = "登录";
        mToken = AppAssistant.getPrefs().getStr(Constants.PrefsKey.AUTH_TOKEN_KEY);
        long expire = AppAssistant.getPrefs().getLong(Constants.PrefsKey.AUTH_EXPIRE_KEY);
        if(!StringUtil.isNullOrEmpty(mToken) && System.currentTimeMillis() < expire){
            displayName = AppAssistant.getPrefs().getStr(Constants.PrefsKey.LOGIN_NAME);
            if(StringUtil.isNullOrEmpty(displayName)){
                displayName = "已登录";
            }else{
                AliPushCenter.getInstance().bindAccount(displayName);
            }
            widgets.mLogoutTab.setVisibility(View.VISIBLE);
        }else{
            widgets.mLogoutTab.setVisibility(View.GONE);
            AliPushCenter.getInstance().unbindAccount();
        }
        widgets.mLoginTab.setText(displayName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();

//        NotifyShowActivity.launchMe(this, new Gson().fromJson(test, OrderInfo.class));
    }

    private void loginDispose(){
        long expire = AppAssistant.getPrefs().getLong(Constants.PrefsKey.AUTH_EXPIRE_KEY);
        if(StringUtil.isNullOrEmpty(mToken) || System.currentTimeMillis() > expire){
            LoginActivity.launchMe(MainActivity.this);
        }
    }

    @Override
    protected String getTitleStr() {
        return "首页";
    }

    private boolean checkLogin(){
        String token = AppAssistant.getPrefs().getStr(Constants.PrefsKey.AUTH_TOKEN_KEY);
        long expire = AppAssistant.getPrefs().getLong(Constants.PrefsKey.AUTH_EXPIRE_KEY);
        if(StringUtil.isNullOrEmpty(token) && System.currentTimeMillis() < expire){
            ToastUtil.showToastShort(this, "请先登录");
            return false;
        }
        return true;
    }
}
