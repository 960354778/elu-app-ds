package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.adapter.ViewPagerAdatper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;

/**
 * Created by luohongzhen on 15/12/2017.
 */

public class ViewPagerActivity extends BaseTemplatedActivity {


    public static void lanuchMe(Context ctx, String title, int requestType) {
        Intent intent = new Intent(ctx, ViewPagerActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("requestType", requestType);
        ctx.startActivity(intent);
    }

    private ViewPagerAdatper mPagerAdapter;
    private final String[] tabs = {"未执行", "已执行", "全部"};

    class Widgets {
        @BindView(R.id.vpPagerId)
        ViewPager viewPager;
        @BindView(R.id.tabStripLayoutId)
        FrameLayout tabStripLayout;

        PagerSlidingTabStrip tabStrip;

        @OnPageChange(R.id.vpPagerId)
        void onPageChange(int position) {
            if(mPagerAdapter != null)
                mPagerAdapter.setIndex(position);
        }
    }

    private final Widgets widgets = new Widgets();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(widgets, this);
        bindData();
    }

    private void bindData() {
        createTabview();
        mPagerAdapter = new ViewPagerAdatper(getSupportFragmentManager());
        mPagerAdapter.setTabs(tabs);
        mPagerAdapter.setRequestType(getIntent().getIntExtra("requestType", -1));
        widgets.viewPager.setAdapter(mPagerAdapter);
        widgets.tabStrip.setViewPager(widgets.viewPager);
        mPagerAdapter.setIndex(0);
        widgets.viewPager.setOffscreenPageLimit(3);
    }

    @Override
    protected Integer getContentResId() {
        return R.layout.activity_viewpager_layout;
    }

    @Override
    protected String getTitleStr() {
        return getIntent().getStringExtra("title");
    }

    private void createTabview(){
        widgets.tabStrip = new PagerSlidingTabStrip(this);
        widgets.tabStrip.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        widgets.tabStrip.setShouldExpand(true);
        widgets.tabStrip.setAllCaps(false);
        widgets.tabStrip.setTextSize(40);
        widgets.tabStrip.setTextColor(Color.BLACK);
        widgets.tabStrip.setDividerColor(Color.BLUE);
        widgets.tabStrip.setDividerPadding(40);
        widgets.tabStrip.setIndicatorColor(Color.BLUE);
        widgets.tabStrip.setIndicatorHeight(10);
        widgets.tabStrip.setUnderlineColor(Color.WHITE);
        widgets.tabStripLayout.addView(widgets.tabStrip);
    }

}
