package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.qingyun.zhiyunelu.ds.R;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    }
}
