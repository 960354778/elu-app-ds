package com.qingyun.zhiyunelu.ds.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.qingyun.zhiyunelu.ds.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import velites.android.utility.framework.HierarchyHelper;


/**
 * Created by regis on 16/11/11.
 */

public abstract class BaseTemplatedActivity extends BaseActivity {

    class Widgets {
        @BindView(R.id.header_toolbar)
        Toolbar toolbar;
        @BindView(R.id.header_more)
        FrameLayout more;
        @BindView(R.id.body)
        FrameLayout body;
    }
    private final Widgets widgets = new Widgets();

    protected Integer getContentResId() {
        return null;
    }

    protected Integer getHeadMoreContentResId() {
        return null;
    }

    protected int[] getMoveToHeadMoreViewIds() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(widgets, this);
        setSupportActionBar(widgets.toolbar);
        Integer rid = getHeadMoreContentResId();
        if (rid != null) {
            getLayoutInflater().inflate(rid, widgets.more, true);
        }
        rid = getContentResId();
        if (rid != null) {
            View body = getLayoutInflater().inflate(rid, widgets.body, true);
            HierarchyHelper.moveViews(body, widgets.more, getMoveToHeadMoreViewIds());
        }
        widgets.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected Toolbar getToolbar() {
        return widgets.toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        int[] ids = getInvisibleMenuIds();
        if (ids != null) {
            for (int id : ids) {
                MenuItem item = menu.findItem(id);
                if (item == null) {
                    menu.setGroupVisible(id, false);
                } else {
                    item.setVisible(false);
                }
            }
        }
        ids = getVisibleMenuIds();
        if (ids != null) {
            for (int id : ids) {
                MenuItem item = menu.findItem(id);
                if (item == null) {
                    menu.setGroupVisible(id, true);
                } else {
                    item.setVisible(true);
                }
            }
        }
        return true;
    }

    protected int[] getInvisibleMenuIds() {
        return null;
    }
    protected int[] getVisibleMenuIds() {
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                SettingsActivity.launchMe(this);
                break;
        }
        return true;
    }
}
