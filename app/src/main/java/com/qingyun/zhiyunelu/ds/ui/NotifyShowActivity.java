package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.data.OrderInfo;
import com.qingyun.zhiyunelu.ds.record.RecordRequest;
import com.qingyun.zhiyunelu.ds.widget.ShowPhoeListDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.internal.Util;
import velites.android.utility.utils.ScreenUtil;
import velites.java.utility.generic.Action1;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;
import velites.java.utility.misc.StringUtil;

/**
 * Created by luohongzhen on 24/01/2018.
 */

public class NotifyShowActivity extends BaseTemplatedActivity {

    class Widgets {
        @BindView(R.id.ivCallId)
        ImageView ivCallId;
        @BindView(R.id.tv1Id)
        TextView tt1;
        @BindView(R.id.tv2Id)
        TextView tt2;
        @BindView(R.id.tv3Id)
        TextView tt3;
        @BindView(R.id.tv4Id)
        TextView tt4;
        @BindView(R.id.tv5Id)
        TextView tt5;
        @BindView(R.id.tv6Id)
        TextView tt6;
        @BindView(R.id.tv7Id)
        TextView tt7;
        @BindView(R.id.itemLayoutId)
        LinearLayout itemLayout;
    }

    private final Widgets widgets = new Widgets();
    private OrderInfo info;

    public static void launchMe(Context ctx, OrderInfo info) {
        Intent intent = new Intent(ctx, NotifyShowActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("info", info);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(widgets, this);
        info = getIntent().getParcelableExtra("info");
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)widgets.itemLayout.getLayoutParams();
        if(params == null){
            params = new RelativeLayout.LayoutParams(-1, -1);
        }

        params.height = ScreenUtil.dip2px(240, this);
        widgets.itemLayout.setLayoutParams(params);
        if(info != null){
            LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, "notify info:%s", info.toString()));
            fillData();
        }
    }

    private void fillData(){
        String docterName = info.getDoctorName();
        fillItem(widgets.tt1, docterName, "医生： %s");
        String hospitalName = info.getHospitalName();
        fillItem(widgets.tt2, hospitalName, "医院： %s");
        String departmentName = info.getDepartmentName();
        fillItem(widgets.tt3, departmentName, "科室： %s");
        String brandName = info.getBrandName();
        fillItem(widgets.tt4, brandName, "品牌： %s");
        String repName = info.getRepresentativeName();
        fillItem(widgets.tt5, repName, "分配专员： %s");
        StringBuilder builder = new StringBuilder();
        String provinceName = info.getProvinceName();
        builder.append(provinceName);
        String cityName = info.getCityName();
        if (!StringUtil.isNullOrEmpty(cityName)) {
            builder.append("--").append(cityName);
        }
        String districtName = info.getDistrictName();
        if (!StringUtil.isNullOrEmpty(districtName)) {
            builder.append("--").append(districtName);
        }
        fillItem(widgets.tt6, builder.toString(), "地址： %s");
        String endDate = info.getEndDate();
        Date dt = null;
        if (!StringUtil.isNullOrEmpty(endDate)) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                dt = df.parse(endDate.split("T")[0]);
                dt = new Date(dt.getTime() + 1000 * 60 * 60 * 24);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        fillItem(widgets.tt7, dt == null ? null : new SimpleDateFormat("yyyy-MM-dd").format(dt), "截止日期:  %s");
        widgets.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ShowPhoeListDialog dialog = new ShowPhoeListDialog(NotifyShowActivity.this, R.style.CustomDialog, info.getPhones(), new Action1<String>() {
                        @Override
                        public void a(String arg1) {
                            if (!StringUtil.isNullOrEmpty(arg1)) {
                                RecordRequest request = new RecordRequest(arg1, null, info.getTaskId());
                                AppAssistant.getRequestQueue().addWaitTask(arg1, request);
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                Uri data = Uri.parse("tel:" + arg1);
                                intent.setData(data);
                                NotifyShowActivity.this.startActivity(intent);

                            }

                        }
                    });
                    dialog.show();
                }
            });
    }

    private void fillItem(TextView tv, String content, String formatStr) {
        tv.setText(StringUtil.isNullOrEmpty(content) ? "" : String.format(formatStr, content));
        if (StringUtil.isNullOrEmpty(content))
            tv.setVisibility(View.GONE);
        else
            tv.setVisibility(View.VISIBLE);
    }

    @Override
    protected Integer getContentResId() {
        return R.layout.activity_notify_layout;
    }

    @Override
    protected String getTitleStr() {
        return "拨打工单电话";
    }

}
