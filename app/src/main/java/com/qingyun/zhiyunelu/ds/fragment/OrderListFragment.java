package com.qingyun.zhiyunelu.ds.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qingyun.zhiyunelu.ds.AppAssistant;
import com.qingyun.zhiyunelu.ds.Constants;
import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.adapter.BaseAdatper;
import com.qingyun.zhiyunelu.ds.data.OrderInfo;
import com.qingyun.zhiyunelu.ds.record.RecordRequest;
import com.qingyun.zhiyunelu.ds.widget.ShowPhoeListDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import velites.android.utility.utils.ToastUtil;
import velites.java.utility.generic.Action1;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogHub;
import velites.java.utility.misc.FileUtil;
import velites.java.utility.misc.StringUtil;

/**
 * Created by luohongzhen on 15/12/2017.
 */

public class OrderListFragment extends BaseListFragment<OrderInfo> {

    private OrderAdpater orderAdpater;
    private int adpaterType = -1;
    private int requestType = -1;

    private OrderInfo orderInfo;

    public static OrderListFragment newInstance(int index, int requestType) {
        OrderListFragment of = new OrderListFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("index", index);
        arguments.putInt("requestType", requestType);
        of.setArguments(arguments);
        return of;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected BaseAdatper<OrderInfo> getAdatper() {
        return orderAdpater;
    }

    private void initView() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            adpaterType = bundle.getInt("index");
            requestType = bundle.getInt("requestType");
        }
        orderAdpater = new OrderAdpater(adpaterType);
        loadData(requestType, 1);
    }

    class OrderAdpater extends BaseAdatper<OrderInfo> {
        private int type = -1;
        private List<OrderInfo> datas;

        public OrderAdpater(int type) {
            this.type = type;
        }

        @Override
        public int getItemCount() {
            return super.getItemCount();
        }

        @Override
        public int getSubLayoutId() {
            return R.layout.view_item_cardview_layout;
        }

        @Override
        public RecyclerView.ViewHolder getViewHolder(View view, ViewGroup parent, int viewType) {
            ViewHolder viewHolder = new ViewHolder(view);
            ButterKnife.bind(viewHolder, view);
            return viewHolder;
        }

        @Override
        public Context getContext() {
            return getActivity();
        }

        @Override
        public int getCount() {
            return datas == null ? 0 : datas.size();
        }

        @Override
        public void bindView(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            OrderInfo info = datas.get(position);
            String docterName = info.getDoctorName();
            fillItem(viewHolder.tt1, docterName, "医生： %s");
            String hospitalName = info.getHospitalName();
            fillItem(viewHolder.tt2, hospitalName, "医院： %s");
            String departmentName = info.getDepartmentName();
            fillItem(viewHolder.tt3, departmentName, "科室： %s");
            String brandName = info.getBrandName();
            fillItem(viewHolder.tt4, brandName, "品牌： %s");
            String repName = info.getRepresentativeName();
            fillItem(viewHolder.tt5, repName, "分配专员： %s");
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
            fillItem(viewHolder.tt6, builder.toString(), "地址： %s");
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
            fillItem(viewHolder.tt7, dt == null ? null : new SimpleDateFormat("YYYY-MM-DD").format(dt), "截止日期:  %s");
            viewHolder.itemLayout.setTag(position);
            if (adpaterType == 0) {
                viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = (Integer) v.getTag();
                        final OrderInfo itemInfo = datas.get(index);
                        final ShowPhoeListDialog dialog = new ShowPhoeListDialog(getActivity(), R.style.CustomDialog, datas.get(index).getPhones(), new Action1<String>() {
                            @Override
                            public void a(String arg1) {
                                if (!StringUtil.isNullOrEmpty(arg1)) {
                                    RecordRequest request = new RecordRequest(arg1, null, itemInfo.getTaskId());
                                    AppAssistant.getRequestQueue().addWaitTask(arg1, request);
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    Uri data = Uri.parse("tel:" + arg1);
                                    intent.setData(data);
                                    getActivity().startActivity(intent);

                                }

                            }
                        });
                        dialog.show();
                    }
                });
            } else {
                viewHolder.callPhone.setVisibility(View.INVISIBLE);
            }
        }

        private void fillItem(TextView tv, String content, String formatStr) {
            tv.setText(StringUtil.isNullOrEmpty(content) ? "" : String.format(formatStr, content));
            if (StringUtil.isNullOrEmpty(content))
                tv.setVisibility(View.GONE);
            else
                tv.setVisibility(View.VISIBLE);
        }

        @Override
        public void updateData(OrderInfo args) {
            orderInfo = args == null ? null : args.getDoctorTasks();
            if (orderInfo == null) {
                orderInfo = args;
            }
            if (orderInfo != null && orderInfo.getList() != null) {
                if (orderInfo.getPage() != null) {
                    int index = orderInfo.getPage().getPageNumber();
                    if (index == 1) {
                        datas = orderInfo.getList();
                    } else {
                        datas.addAll(orderInfo.getList());
                    }
                }
                notifyDataSetChanged();
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemLayoutId)
        LinearLayout itemLayout;
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
        @BindView(R.id.ivCallId)
        ImageView callPhone;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private void loadData(int requestType, int pageNum) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageNumber", pageNum);
        if (adpaterType == 0 || adpaterType == 1)
            params.put("taskStatuses", adpaterType == 0 ? new String[] {"Undo", "Booked", "Processing"} : (adpaterType == 1 ? new String[] {"Completed"} : null));
        try{
            AppAssistant.getApi().getOrderList(requestType, params)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<OrderInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(OrderInfo orderInfo) {
                            try {
                                if (orderInfo != null && orderAdpater != null && orderInfo.getData() != null) {
                                    LogHub.log(new LogEntry(LogHub.LOG_LEVEL_INFO, "orderList", "msg:%s", orderInfo.toString()));
                                    orderAdpater.updateData(orderInfo.getData());
                                }
                            } catch (Exception e) {
                                ToastUtil.showToastShort(getActivity(), "请求异常");
                            }
                            isLoading = false;
                            stopRefresh();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            ToastUtil.showToastShort(getActivity(), "请求异常");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
            ToastUtil.showToastShort(getActivity(), "请求异常");
        }

    }

    @Override
    public void requestData() {
        int requestNum = 1;
        if (orderInfo != null && orderInfo.getPage() != null) {
            int pageNum = orderInfo.getPage().getPageNumber();
            int allCount = orderInfo.getPage().getTotalCount();
            if (pageNum < allCount) {
                requestNum = pageNum + 1;
            }else{
                return;
            }
        }
        loadData(requestType, requestNum);
    }

    @Override
    protected void refresh() {
        loadData(requestType, 1);
    }
}
