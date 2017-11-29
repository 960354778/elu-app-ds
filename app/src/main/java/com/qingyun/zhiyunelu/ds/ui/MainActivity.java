package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;


import com.qingyun.zhiyunelu.ds.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import velites.java.utility.generic.Func2;

public class MainActivity extends BaseTemplatedActivity {

    public static void launchMe(Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        ctx.startActivity(intent);
    }

    class Widgets {
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
}
