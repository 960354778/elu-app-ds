package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import velites.java.utility.misc.StringUtil;

/**
 * Created by regis on 17/5/1.
 */

public class SettingsActivity extends BaseTemplatedActivity {


    public static void launchMe(Context ctx) {
        Intent intent = new Intent(ctx, SettingsActivity.class);
        ctx.startActivity(intent);
    }

    //TODO: change to use pref

    class Widgets {
        @BindView(R.id.buildDate)
        TextView buildDate;
        @BindView(R.id.buildRevision)
        TextView buildRevision;
        @BindView(R.id.etPhone)
        EditText phoneEt;
        @BindView(R.id.layoutDebugUrl)
        View layoutDebugUrl;
        @BindView(R.id.debugUrl)
        EditText debugUrl;

        @OnClick({R.id.savePhoneBt})
        void onPhoneNumberClick(){
            if(widgets.phoneEt.getText() != null){
                String phone = widgets.phoneEt.getText().toString();
                if(!StringUtil.isNullOrEmpty(phone)){
                    AppAssistant.getPrefs().setStr(Constants.PrefsKey.MYSELF_PHONE_NUM, phone);
                }
            }
        }
        @OnClick({R.id.saveDebugUrlBt})
        void onDebugUrlClick(){
            if(widgets.debugUrl.getText() != null){
                String url = widgets.debugUrl.getText().toString();
                if(!StringUtil.isNullOrEmpty(url)){
                    AppAssistant.setApiBaseUrl(url);
                }
            }
        }
    }

    private final Widgets widgets = new Widgets();


    @Override
    protected Integer getContentResId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(widgets, this);
        widgets.buildDate.setText(AppAssistant.getBuildDate());
        widgets.buildRevision.setText(AppAssistant.getBuildRevision());
        if (AppAssistant.isDebug()) {
            widgets.layoutDebugUrl.setVisibility(View.VISIBLE);
            widgets.debugUrl.setText(AppAssistant.getApiBaseUrl());
        }
        checkMyPhone();
    }

    private void checkMyPhone(){
        String phone = AppAssistant.getPrefs().getStr(Constants.PrefsKey.MYSELF_PHONE_NUM);
        if(!StringUtil.isNullOrEmpty(phone)){
            widgets.phoneEt.setText(phone);
        }
    }
}
