package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.data.TaskMessage;

import butterknife.BindView;
import butterknife.OnClick;
import velites.android.support.ui.BaseBindableViewHolder;
import velites.android.support.ui.BaseLayoutWidget;
import velites.android.utility.misc.ToastHelper;
import velites.java.utility.misc.StringUtil;

public class TasksActivity extends BaseActivity {

    public static void launchMe(Context ctx) {
        Intent intent = new Intent(ctx, TasksActivity.class);
        ctx.startActivity(intent);
    }

    private BaseLayoutWidget.List list;

    @Override
    protected Integer getContentResId() {
        return R.layout.layout_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new BaseLayoutWidget.List(this);
        list.getList().setAdapter(new Adapter(getAppAssistant().getMessaging().getTasks()));
        list.getList().getAdapter().notifyDataSetChanged();
    }

    class ViewHolder extends BaseBindableViewHolder<TaskMessage> {

        class Widget extends BaseLayoutWidget {
            @BindView(R.id.dialing_task_code_area)
            LinearLayout llTaskCode;
            @BindView(R.id.dialing_task_code_value)
            TextView tvTaskCode;
            @BindView(R.id.dialing_doctor_no_area)
            LinearLayout llDoctorNo;
            @BindView(R.id.dialing_doctor_no_value)
            TextView tvDoctorNo;
            @BindView(R.id.dialing_doctor_name_area)
            LinearLayout llDoctorName;
            @BindView(R.id.dialing_doctor_name_value)
            TextView tvDoctorName;

            public Widget(View view) {
                super(view);
            }

            @OnClick
            void doDial(View view) {
                ToastHelper.showToastLong(TasksActivity.this, "dialmmm");
            }
        }
        private final Widget widget;

        ViewHolder(View itemView) {
            super(itemView);
            widget = new Widget(itemView);
        }

        @Override
        public void bindItem(TaskMessage task) {
            if (StringUtil.isNullOrEmpty(task.taskCode)) {
                widget.llTaskCode.setVisibility(View.GONE);
            } else {
                widget.llTaskCode.setVisibility(View.VISIBLE);
                widget.tvTaskCode.setText(task.taskCode);
            }
            if (StringUtil.isNullOrEmpty(task.doctorNo)) {
                widget.llDoctorNo.setVisibility(View.GONE);
            } else {
                widget.llDoctorNo.setVisibility(View.VISIBLE);
                widget.tvDoctorNo.setText(task.doctorNo);
            }
            if (StringUtil.isNullOrEmpty(task.doctorName)) {
                widget.llDoctorName.setVisibility(View.GONE);
            } else {
                widget.llDoctorName.setVisibility(View.VISIBLE);
                widget.tvDoctorName.setText(task.doctorName);
            }
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private final TaskMessage[] tasks;

        Adapter(TaskMessage[] tasks) {
            this.tasks = tasks;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialing_task, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindItem(tasks[position]);
        }

        @Override
        public int getItemCount() {
            return tasks == null ? 0 : tasks.length;
        }
    }
}
