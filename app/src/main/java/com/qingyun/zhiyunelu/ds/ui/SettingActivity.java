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
import com.qingyun.zhiyunelu.ds.op.ObserverWithProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import velites.android.utility.misc.RxHelper;
import velites.java.utility.misc.RxUtil;

/**
 * Created by regis on 17/5/1.
 */

public class SettingActivity extends BaseActivity {
    public static void launchMe(Context ctx) {
        Intent intent = new Intent(ctx, SettingActivity.class);
        ctx.startActivity(intent);
    }

    private Observer<Integer> buildSaveObserver() {
        return new ObserverWithProgress<Integer>(SettingActivity.this) {
            @Override
            public void onNext(Integer res) {
                super.onNext(res);
                Popups.buildAlert(SettingActivity.this, getString(R.string.info_saved), true);
            }
        };
    }
    class Widgets {
        @BindView(R.id.setting_build_date)
        TextView tvBuildDate;
        @BindView(R.id.setting_build_revision)
        TextView tvBuildRevision;
        @BindView(R.id.setting_self_phone)
        EditText etSelfPhone;
        @BindView(R.id.setting_area_debug_url)
        View areaDebugUrl;
        @BindView(R.id.setting_debug_url)
        EditText etDebugUrl;
        @BindView(R.id.setting_export_wx)
        Button btExportWx;

        void render() {
            tvBuildDate.setText(getAppAssistant().getBuildDate());
            tvBuildRevision.setText(getAppAssistant().getBuildRevision());
            etSelfPhone.setText(getAppAssistant().getPrefs().getSelfPhone());
            if (getAppAssistant().isDebug()) {
                areaDebugUrl.setVisibility(View.VISIBLE);
                btExportWx.setVisibility(View.VISIBLE);
                etDebugUrl.setText(getAppAssistant().getPrefs().getDebugApiBase());
            } else {
                areaDebugUrl.setVisibility(View.GONE);
                btExportWx.setVisibility(View.GONE);
            }
        }

        @OnClick({R.id.setting_save_self_phone})
        void saveSelfPhone() {
            Observable.create(RxUtil.buildSimpleActionObservable(() -> getAppAssistant().getPrefs().setSelfPhone(etSelfPhone.getText().toString())))
                    .subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                    .subscribe(buildSaveObserver());
        }
        @OnClick({R.id.setting_save_debug_url})
        void saveDebugUrl(){
            Observable.create(RxUtil.buildSimpleActionObservable(() -> getAppAssistant().updateDebugApiBase(etDebugUrl.getText().toString())))
                    .subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                    .subscribe(buildSaveObserver());
        }
        @OnClick({R.id.setting_export_wx})
        void exportWx() {
            Observable.create(RxUtil.buildSimpleActionObservable(() -> getAppAssistant().getWechat().exportWxDatabase()))
                    .subscribeOn(RxHelper.createKeepingScopeComputationSchedule()).observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                    .subscribe(new ObserverWithProgress<Integer>(SettingActivity.this) {
                    });
        }
    }
    private final Widgets widgets = new Widgets();


    @Override
    protected Integer getContentResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(widgets, this);
        widgets.render();
    }
}
