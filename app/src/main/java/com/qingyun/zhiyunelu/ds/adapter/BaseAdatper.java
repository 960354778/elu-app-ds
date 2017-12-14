package com.qingyun.zhiyunelu.ds.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by luohongzhen on 07/12/2017.
 */

public abstract class BaseAdatper<T1> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getContext()).inflate(getSubLayoutId(), null);
        return getViewHolder(v, parent, viewType);
    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindView(holder, position);
    }

    public abstract int getSubLayoutId();
    public abstract RecyclerView.ViewHolder getViewHolder(View view, ViewGroup parent, int viewType);
    public abstract Context getContext();
    public abstract int getCount();
    public abstract void bindView(RecyclerView.ViewHolder holder, int position);
    public abstract void updateData(T1 args);
}
