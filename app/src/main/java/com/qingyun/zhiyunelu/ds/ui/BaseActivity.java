package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import velites.java.utility.generic.Tuple2;
import velites.java.utility.thread.BaseInitializer;

/**
 * Created by regis on 16/11/14.
 */
public class BaseActivity extends velites.android.support.ui.BaseActivity {
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

    public static final void awaitInit() {
        initializer.awaitInitializedNoThrows(null);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initializer.ensureInit(new Tuple2<Context, Intent>(getApplicationContext(), getIntent()));
        super.onCreate(savedInstanceState);
    }
}
