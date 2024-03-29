package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.data.ApiResult;
import com.qingyun.zhiyunelu.ds.data.PhoneInfo;
import com.qingyun.zhiyunelu.ds.data.RecordCalledOutDto;
import com.qingyun.zhiyunelu.ds.data.RecordInfo;
import com.qingyun.zhiyunelu.ds.data.TaskMessage;
import com.qingyun.zhiyunelu.ds.op.ApiService;
import com.trello.rxlifecycle2.android.ActivityEvent;

import butterknife.BindView;
import butterknife.OnClick;
import velites.android.support.R2;
import velites.android.support.ui.BaseBindableViewHolder;
import velites.android.support.ui.BaseLayoutWidget;
import velites.android.utility.misc.PhoneNumberHelper;
import velites.android.utility.misc.RxHelper;
import velites.java.utility.misc.CollectionUtil;
import velites.java.utility.misc.DateTimeUtil;
import velites.java.utility.misc.SerializationUtil;
import velites.java.utility.misc.StringUtil;

public class TasksActivity extends BaseActivity {

    private TextView tv_return;

    public static void launchMe(Context ctx) {
        Intent intent = new Intent(ctx, TasksActivity.class);
        ctx.startActivity(intent);
    }

    private BaseLayoutWidget.List list;

    /*@Override
    protected Integer getContentResId() {
        return R.layout.layout_list;
    }*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        list = new BaseLayoutWidget.List(this);
        list.getList().setAdapter(new Adapter(getAppAssistant().getMessaging().getTasks()));
        tv_return = (TextView) findViewById(R.id.tv_return);
        tv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void dial(RecordInfo rec, PhoneInfo p) {
        getAppAssistant().getPhone().dial(this, rec, p);
        finish();
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
            @BindView(R.id.dialing_hospital_area)
            LinearLayout llHospital;
            @BindView(R.id.dialing_hospital)
            TextView tvHospital;
            @BindView(R.id.dialing_department_area)
            LinearLayout llDepartment;
            @BindView(R.id.dialing_department)
            TextView tvDepartment;
            @BindView(R.id.dialing_location_area)
            LinearLayout llLocation;
            @BindView(R.id.dialing_location)
            TextView tvLocation;
            @BindView(R.id.dialing_representative_area)
            LinearLayout llRepresentative;
            @BindView(R.id.dialing_representative)
            TextView tvRepresentative;

            public Widget(View view) {
                super(view);
            }

            @OnClick
            void doDial(View view) {
                if (CollectionUtil.isNullOrEmpty(task.phones)) {
                    Popups.buildAlert(TasksActivity.this, getString(R.string.info_no_phone_number), true);
                } else {
                    new AlertDialog.Builder(TasksActivity.this, R.style.EluTheme_Dialog)
                            .setItems(SerializationUtil.convert(task.phones, p -> StringUtil.formatInvariant("%s(%s%s)", PhoneNumberHelper.getDisplayNumber(p.number, p.areaCode, p.extension), getAppAssistant().getApi().translateByPocket("phoneSources", p.phoneSource), p.remark == null ? StringUtil.STRING_EMPTY : "-" + p.remark)).toArray(new CharSequence[0]), (dialog, which) -> {
                                final PhoneInfo p = task.phones[which];
                                RecordCalledOutDto req = new RecordCalledOutDto();
                                req.taskId = task.taskId;
                                req.phoneId = p.phoneId;
                                req.execDate = DateTimeUtil.now();
                                getAppAssistant().getApi().createAsyncApi().recordCalledOut(req)
                                        .subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeComputationSchedule())
                                        .map(res -> {
                                            dial(res.data, p);
                                            return res;
                                        })
                                        .compose(bindUntilEvent(ActivityEvent.DESTROY))
                                        .subscribe(new ApiService.ApiObserver<RecordInfo>(TasksActivity.this) {
                                            @Override
                                            public boolean processResult(RecordInfo rec, ApiResult<RecordInfo> res) {
                                                return true;
                                            }
                                        });
                            }).show();
                }
            }
        }
        private final Widget widget;
        private TaskMessage task;

        ViewHolder(View itemView) {
            super(itemView);
            widget = new Widget(itemView);
        }

        @Override
        public void bindItem(TaskMessage task) {
            this.task = task;
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
            widget.tvHospital.setText(task.hospitalName);
            widget.tvDepartment.setText(task.departmentName);
            widget.tvLocation.setText(StringUtil.join(true, "-", (Object[]) new String[] {task.provinceName, task.cityName, task.districtName}));
            if (StringUtil.isNullOrEmpty(task.representativeName)) {
                widget.llRepresentative.setVisibility(View.GONE);
            } else {
                widget.llRepresentative.setVisibility(View.VISIBLE);
                widget.tvRepresentative.setText(task.representativeName);
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
