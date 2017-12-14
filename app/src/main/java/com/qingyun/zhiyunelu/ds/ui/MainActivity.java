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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
                    break;
                case R.id.MyHosListTab:
                    break;
                case R.id.tvDoctersTab:
                    break;
                case R.id.tvhosTab:
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
        if(!StringUtil.isNullOrEmpty(mToken)){
            displayName = AppAssistant.getPrefs().getStr(Constants.PrefsKey.LOGIN_NAME);
            if(StringUtil.isNullOrEmpty(displayName)){
                displayName = "已登录";
            }
            widgets.mLogoutTab.setVisibility(View.VISIBLE);
        }else{
            widgets.mLogoutTab.setVisibility(View.GONE);
        }
        widgets.mLoginTab.setText(displayName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void loginDispose(){
        if(StringUtil.isNullOrEmpty(mToken)){
            LoginActivity.launchMe(MainActivity.this);
        }
    }

}
