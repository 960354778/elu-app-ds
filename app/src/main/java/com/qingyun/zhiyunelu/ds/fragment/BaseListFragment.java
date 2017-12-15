package com.qingyun.zhiyunelu.ds.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.adapter.BaseAdatper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by luohongzhen on 15/12/2017.
 */

public class BaseListFragment<T> extends Fragment {

    public boolean isLoading = false;
    private boolean isSlidingToLast = false;

    class Widgets{
        @BindView(R.id.recyViewId)
        RecyclerView recyclerView;
        @BindView(R.id.swipeLayoutId)
        SwipeRefreshLayout swipeRefreshLayout;
    }

    protected final Widgets widgets = new Widgets();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getContentIds(), null);
        ButterKnife.bind(widgets, view);
        bindView();
        return view;
    }

    private void bindView(){
        widgets.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        widgets.recyclerView.setAdapter(getAdatper());
        widgets.recyclerView.setNestedScrollingEnabled(false);
        widgets.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();

                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast && !isLoading) {
                        isLoading = true;
                        requestData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    isSlidingToLast = true;
                } else {
                    isSlidingToLast = false;
                }
            }
        });
        widgets.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    protected int getContentIds(){return R.layout.view_recycler_layout;}

    protected BaseAdatper<T> getAdatper(){return null;}

    public boolean isCanRefresh(){
        if (widgets.recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = widgets.recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                View childAt = widgets.recyclerView.getChildAt(0);
                if (childAt == null || (firstVisibleItemPosition == 0 &&
                        layoutManager.getDecoratedTop(childAt) == 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void requestData(){};
    protected void refresh(){};
    protected void stopRefresh(){
        if(widgets != null && widgets.swipeRefreshLayout != null){
            widgets.swipeRefreshLayout.setRefreshing(false);
        }
    }

}
