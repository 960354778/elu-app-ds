package com.qingyun.zhiyunelu.ds.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import com.qingyun.zhiyunelu.ds.fragment.OrderListFragment;

/**
 * Created by luohongzhen on 15/12/2017.
 */

public class ViewPagerAdatper extends FragmentPagerAdapter {

    private String[] tabs;
    private SparseArray<OrderListFragment> fragmentMaps;
    private int requestType;
    private int index;

    public ViewPagerAdatper(FragmentManager fm) {
        super(fm);
        fragmentMaps = new SparseArray<>();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setTabs(String[] tabs) {
        this.tabs = tabs;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    @Override
    public Fragment getItem(int position) {
        OrderListFragment fragment = fragmentMaps.get(position);
        if(fragment == null){
            fragment = OrderListFragment.newInstance(position, requestType);
            fragmentMaps.put(position, fragment);
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs == null ?"":tabs[position];
    }

    @Override
    public int getCount() {
        return tabs == null ?0: tabs.length;
    }

    public boolean isCanRefresh(){
        if(fragmentMaps != null){
            OrderListFragment currentFragment = fragmentMaps.get(index);
            if(currentFragment != null){
                return currentFragment.isCanRefresh();
            }
        }
        return false;
    }

    public void loadData(){
        if(fragmentMaps != null){
            OrderListFragment currentFragment = fragmentMaps.get(index);
            if(currentFragment != null){
                currentFragment.requestData();
            }
        }
    }
}
