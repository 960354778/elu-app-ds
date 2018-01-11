package com.qingyun.zhiyunelu.ds.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.qingyun.zhiyunelu.ds.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import velites.java.utility.generic.Action1;

/**
 * Created by luohongzhen on 16/12/2017.
 */

public class ShowPhoeListDialog extends Dialog {

    private List<String> mDatas;
    private Action1<String> action;
    class Widgets{
        @BindView(R.id.lvPhoneListId)
        MyListView myListView;
        @BindView(R.id.btnCancel)
        Button cancelButton;

        @OnItemClick(R.id.lvPhoneListId)
        void onItemClick(AdapterView<?> parent, View view, int position, long id){
            dismiss();
            if(action != null && mDatas != null && mDatas.size() > position)
                action.a(mDatas.get(position));
        }

        @OnClick(R.id.btnCancel)
        void onClick(View view){
            dismiss();
        }
    }

    private final Widgets widgets = new Widgets();

    public ShowPhoeListDialog(@NonNull Context context, int themeResId, List<String> args, Action1<String> action) {
        super(context, themeResId);
        this.mDatas = args;
        this.action = action;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_choose_phone_dialog);
        ButterKnife.bind(widgets,this);
        setCanceledOnTouchOutside(true);
        widgets.myListView.setAdapter(new PhoneAdapter());
    }


    private class PhoneAdapter extends BaseAdapter{


        public PhoneAdapter() {
        }

        @Override
        public int getCount() {
            return mDatas == null ? 0: mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.view_phone_item_layout, null);
                ButterKnife.bind(viewHolder, convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String phoneNum = mDatas.get(position);
            viewHolder.tvTitle.setText(phoneNum);
            return convertView;
        }
    }

    protected class ViewHolder {
        @BindView(R.id.tvPhoneTextId)
        TextView tvTitle;
    }
}
