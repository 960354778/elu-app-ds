package com.qingyun.zhiyunelu.ds.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by luohongzhen on 07/12/2017.
 */

public class WorkOrderAdapter extends BaseAdatper<String> {

    @Override
    public int getSubLayoutId() {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view, ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public void bindView(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void updateData(String args) {

    }
}
