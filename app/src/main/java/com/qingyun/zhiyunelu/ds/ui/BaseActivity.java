package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.Window;

import com.qingyun.zhiyunelu.ds.App;
import com.qingyun.zhiyunelu.ds.R;

import velites.android.support.ui.BaseTemplatedActivity;
import velites.java.utility.generic.Tuple2;
import velites.java.utility.thread.BaseInitializer;

public abstract class BaseActivity extends BaseTemplatedActivity {
    private static BaseInitializer<Tuple2<Context, Intent>> initializer = new BaseInitializer<Tuple2<Context, Intent>>(false, null) {
        @Override
        protected void doInit(Tuple2<Context, Intent> values) {
            baseIntent = values.v2;
        }
    };

    private static Intent baseIntent;

    public static Intent getBaseIntent() {
        awaitInit();
        return baseIntent;
    }

    public static void awaitInit() {
        initializer.awaitInitializedNoThrows(null);
    }

    protected static App.Assistant  getAppAssistant() {
        return App.getInstance().getAssistant();
    }

    @Override
    protected int getTemplateResId() {
        return R.layout.activity_base;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initializer.ensureInit(new Tuple2<Context, Intent>(getApplicationContext(), getIntent()));

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return true;
    }

    @Override
    protected boolean isDisplayShowTitleForAppName() {
        return true;
    }
}
