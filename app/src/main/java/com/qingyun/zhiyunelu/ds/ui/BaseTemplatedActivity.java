package com.qingyun.zhiyunelu.ds.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

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

    class Titles{
        @BindView(R.id.ttTitleId)
        TextView title;
    }
    private final Widgets widgets = new Widgets();
    private final Titles titles = new Titles();

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
        getSupportActionBar().setDisplayShowTitleEnabled(isDisplayShowTitleForAppName());
        setTitleView();
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

    private void setTitleView() {
        if(isShowCustomTitle()){
            int id = getTitleViewId();
            if(id > 0){
                View view = getLayoutInflater().inflate(id, null);
                ButterKnife.bind(titles, view);
                widgets.toolbar.addView(view, new ViewGroup.LayoutParams(-1, -1));
                titles.title.setText(getTitleStr());
            }
        }
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
    protected boolean isDisplayShowTitleForAppName(){return false;}
    protected boolean isShowCustomTitle(){return true;}
    protected int getTitleViewId(){return R.layout.view_title_layout;}
    protected String getTitleStr() {return "";}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                //SettingsActivity.launchMe(this);
                break;
        }
        return true;
    }
}
