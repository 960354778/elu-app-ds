package com.qingyun.zhiyunelu.ds.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.donkingliang.imageselector.utils.ImageSelector;
import com.orhanobut.logger.Logger;
import com.qingyun.zhiyunelu.ds.App;
import com.qingyun.zhiyunelu.ds.R;
import com.qingyun.zhiyunelu.ds.data.ApiResult;
import com.qingyun.zhiyunelu.ds.data.TokenInfo;
import com.qingyun.zhiyunelu.ds.db.Phonedb.PhocneDatabase;
import com.qingyun.zhiyunelu.ds.db.Phonedb.PhocneEntity;
import com.qingyun.zhiyunelu.ds.db.Phonedb.StudentDao;
import com.qingyun.zhiyunelu.ds.op.ApiService;
import com.qingyun.zhiyunelu.ds.usilt.Photo;
import com.qingyun.zhiyunelu.ds.usilt.PhotoUpload;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import velites.android.support.ui.BaseLayoutWidget;
import velites.android.utility.misc.RxHelper;
import velites.android.utility.misc.ToastHelper;
import velites.java.utility.log.LogEntry;
import velites.java.utility.log.LogStub;
import velites.java.utility.misc.PathUtil;
import velites.java.utility.misc.RxUtil;
import velites.java.utility.misc.SyntaxUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_Wx;
    private TextView tv_upload;
    private ImageView ivSetting;

    private ImageView IvHeadPortrait,IvPush,IvData,IvPicture;



    private static final int REQUEST_CODE = 0x00000011;

    private final Widgets widgets = new Widgets();

    public static void launchMe(Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        ctx.startActivity(intent);
    }

    private String mToken;



    class Widgets extends BaseLayoutWidget {
        boolean loggedIn;
        @BindView(R.id.main_login_area)
        LinearLayout llLoginArea;
        @BindView(R.id.main_login_display)
        TextView tvLogin;
        @BindView(R.id.main_logout)
        TextView tvLogout;
        @BindView(R.id.main_manually_fetch)
        TextView tvManuallyFetch;

        private void render() {
            TokenInfo token = getAppAssistant().getApi().getToken();
            loggedIn = token != null;
            if (loggedIn) {
                tvLogin.setText(token.account.displayName);
            } else {
                startActivity(new Intent(MainActivity.this,SplashActivity.class));
                finish();
            }
        }

        @OnClick(R.id.main_login_area)
        void doLogin(View view) {
            if (!loggedIn) {
                LoginActivity.launchMe(MainActivity.this);
            }
        }

        @OnClick(R.id.main_logout)
        void doLogout(View view) {
            TokenInfo token = getAppAssistant().getApi().getToken();
            String user = token == null ? null : token.account.loginName;
            getAppAssistant().getApi().clearToken();
            getAppAssistant().getApi().createAsyncApi().logout()
                    .subscribeOn(RxHelper.createKeepingScopeIOSchedule())
                    .observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(new ApiService.ApiObserver(MainActivity.this) {
                        @Override
                        public boolean processResult(Object o, ApiResult res) {
                            LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, MainActivity.this, "Logged out from user: %s", user));
                            return true;
                        }
                    });
        }

        @OnClick(R.id.main_manually_fetch)
        void doManuallyFetch(View view) {
            getAppAssistant().getMessaging().syncTaskMessages()
                    .subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(new ApiService.ApiErrorObserver<Boolean>(MainActivity.this) {
                        @Override
                        public void onNext(Boolean res) {
                            super.onNext(res);
                            if (SyntaxUtil.nvl(res, false)) {
                                TasksActivity.launchMe(MainActivity.this);
                            } else {
                                Popups.buildAlert(MainActivity.this, getString(R.string.warn_no_cached_dial_message), true);
                            }
                        }
                    });
        }

    }


    @Override
    protected boolean isAtLast() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        decorateToolbar();
        widgets.bind(this);
        getAppAssistant().getApi().getLoginStateChanged()
                .observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                .compose(this.bindToLifecycle())
                .subscribe(loggedIn -> this.widgets.render(), RxUtil.simpleErrorConsumer);
        if(!getAppAssistant().getSms().checkMySelfPhoneSet()){
            ToastHelper.showToastLong(this, R.string.warn_need_self_phone_number);
        }
    }

    private void initView() {
        tv_Wx = (TextView) findViewById(R.id.tv_Wx);
        tv_Wx.setOnClickListener(this);
        tv_upload = (TextView) findViewById(R.id.tv_upload);
        tv_upload.setOnClickListener(this);
        ivSetting = (ImageView) findViewById(R.id.iv_setting);
        ivSetting.setOnClickListener(this);

        IvHeadPortrait = (ImageView) findViewById(R.id.iv_head_portrait);
        IvHeadPortrait.setOnClickListener(this);
        IvPush = (ImageView) findViewById(R.id.iv_push);
        IvPush.setOnClickListener(this);
        IvData = (ImageView) findViewById(R.id.iv_data);
        IvData.setOnClickListener(this);
        IvPicture = (ImageView) findViewById(R.id.iv_picture);
        IvPicture.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //上传手机数据
            case R.id.tv_Wx:
                App.getInstance().getAssistant().getPolling().startPolling(true,true);
                CountDownTimer timer1 = new CountDownTimer(5*60*1000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
                        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                        tv_Wx.setText(dateFormat.format(new Date(millisUntilFinished))+"秒");
                        tv_Wx.setTextColor(getResources().getColor(R.color.main_grey));
                        IvData.setClickable(false);
                        IvData.setColorFilter(R.color.main_grey, PorterDuff.Mode.MULTIPLY);
                        tv_Wx.setClickable(false);
                    }
                    @Override
                    public void onFinish() {
                        tv_Wx.setText("手动提交数据");
                        tv_Wx.setTextColor(getResources().getColor(R.color.primaryDark));
                        IvData.setClickable(true);
                        IvData.clearColorFilter();
                        tv_Wx.setClickable(true);
                    }
                }.start();
                break;
            case R.id.tv_upload:
                //进行图片上传 跳转
                ImageSelector.builder()
                        .useCamera(false) // 设置是否使用拍照
                        .setSingle(false)  //设置是否单选
                        .setViewImage(true) //是否点击放大图片查看,，默认为true
                        .setMaxSelectCount(0) // 图片的最大选择数量，小于等于0时，不限数量。
                        .start(this, REQUEST_CODE); // 打开相册
                break;
            case R.id.iv_setting:
                startActivity(new Intent(MainActivity.this,SettingActivity.class));
                break;
            case R.id.iv_head_portrait:
                TokenInfo token = getAppAssistant().getApi().getToken() ;
                String user = token == null ? null : token.account.loginName;
                getAppAssistant().getApi().clearToken();
                getAppAssistant().getApi().createAsyncApi().logout()
                        .subscribeOn(RxHelper.createKeepingScopeIOSchedule())
                        .observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                        .compose(bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(new ApiService.ApiObserver(MainActivity.this) {
                            @Override
                            public boolean processResult(Object o, ApiResult res) {
                                LogStub.log(new LogEntry(LogStub.LOG_LEVEL_INFO, MainActivity.this, "Logged out from user: %s", user));
                                return true;
                            }
                        });
                break;
            case R.id.iv_push:
                getAppAssistant().getMessaging().syncTaskMessages()
                        .subscribeOn(RxHelper.createKeepingScopeIOSchedule()).observeOn(RxHelper.createKeepingScopeMainThreadSchedule())
                        .compose(bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(new ApiService.ApiErrorObserver<Boolean>(MainActivity.this) {
                            @Override
                            public void onNext(Boolean res) {
                                super.onNext(res);
                                if (SyntaxUtil.nvl(res, false)) {
                                    TasksActivity.launchMe(MainActivity.this);
                                } else {
                                    Popups.buildAlert(MainActivity.this, getString(R.string.warn_no_cached_dial_message), true);
                                }
                            }
                        });
                break;
            case R.id.iv_data:
                App.getInstance().getAssistant().getPolling().startPolling(true,true);
                CountDownTimer timer = new CountDownTimer(5*60*1000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
                        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                        tv_Wx.setText(dateFormat.format(new Date(millisUntilFinished))+"秒");
                        tv_Wx.setTextColor(getResources().getColor(R.color.main_grey));
                        IvData.setClickable(false);
                        IvData.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        tv_Wx.setClickable(false);
                    }
                    @Override
                    public void onFinish() {
                        tv_Wx.setText("手动提交数据");
                        tv_Wx.setTextColor(getResources().getColor(R.color.primaryDark));
                        IvData.clearColorFilter();
                        IvData.setClickable(true);
                        tv_Wx.setClickable(true);
                    }
                }.start();
                break;
            case R.id.iv_picture:
                //进行图片上传 跳转
              /*  ImageSelector.builder()
                        .useCamera(false) // 设置是否使用拍照
                        .setSingle(false)  //设置是否单选
                        .setViewImage(true) //是否点击放大图片查看,，默认为true
                        .setMaxSelectCount(0) // 图片的最大选择数量，小于等于0时，不限数量。
                        .start(this, REQUEST_CODE); // 打开相册*/


                //String path = new String(PathUtil.concat(getAppAssistant().getUploadedFileDir().getPath(),"通话录音@10086(10086)_20191104110052.mp3"));
                //Photo photo = new  Photo();
                //Logger.i("录音时长："+getAudioFileVoiceTime(path)+"\n录音日期："+photo.getData(path));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Photo photo = new  Photo();
        PhotoUpload photoUpload = new PhotoUpload();
        if (requestCode == REQUEST_CODE && data != null) {
            ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
            List<File> list = new ArrayList<>();
            for (int i = 0;i<images.size();i++){
                Log.e("图片路径",images.get(i));
                Log.e("图片名",photo.getName(images.get(i)));
                try {
                    Log.e("图片大小",photo.getPhotoSize(images.get(i)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("图片时间",photo.getData(images.get(i)));
                list.add(new File(images.get(i)));
            }
            //上传图片文件
            photoUpload.upload(list,MainActivity.this);

        }
    }



    private void decorateToolbar() {
        getToolbar().setNavigationIcon(null);
    }




}
