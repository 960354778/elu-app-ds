package com.qingyun.zhiyunelu.ds.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by regis on 16/11/14.
 */
public class BaseActivity extends velites.android.support.ui.BaseActivity {
    private Long lastBackPressedAt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        UIAssistant.ensureInit(getApplicationContext(), getIntent());
        super.onCreate(savedInstanceState);
    }
}
