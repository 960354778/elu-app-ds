package com.qingyun.zhiyunelu.ds.usilt;


import android.util.Log;
import android.widget.Toast;

import com.qingyun.zhiyunelu.ds.App;
import com.qingyun.zhiyunelu.ds.data.ApiResult;
import com.qingyun.zhiyunelu.ds.data.TokenInfo;
import com.qingyun.zhiyunelu.ds.data.picture;
import com.qingyun.zhiyunelu.ds.op.ApiService;
import com.qingyun.zhiyunelu.ds.op.Prefs;
import com.qingyun.zhiyunelu.ds.ui.MainActivity;


import java.io.File;
import java.util.List;

import com.orhanobut.logger.Logger;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import velites.java.utility.misc.RxUtil;
import velites.java.utility.misc.SerializationUtil;

public class PhotoUpload {



    /**
    * 上传图片文件及信息
    * */
    public void upload(List<File> compressFile, MainActivity mainActivity){
        Log.e("------------", "uploadImages: 图片开始上传...");
        final MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);//表单类型

        for (int i = 0; i < compressFile.size(); i++) {
            File file = compressFile.get(i);
            RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            builder.addFormDataPart("img_url[]", "系统管理员"+file.getName(), imageBody);

        }
        List<MultipartBody.Part> parts =  builder.build().parts();

        for (int i = 0;i < parts.size();i++){
            Logger.i("-------parts-------"+i+SerializationUtil.describe(parts.get(i)));
        }

        TokenInfo tokenInfo = App.getInstance().getAssistant().getApi().getToken();

        Prefs prefs = new Prefs(mainActivity);
        String phoneNum = null;
        if (prefs.getSelfPhone().contains("+")) {
            phoneNum = prefs.getSelfPhone().substring(3, prefs.getSelfPhone().length());
        }else {
            phoneNum = prefs.getSelfPhone();
        }
        String finalPhoneNum = phoneNum;
        Log.e("-----------", "uploadImages: phoneNum"+finalPhoneNum);
        App.getInstance().getAssistant()
                .getApi()
                .createAsyncApi("http://zhijian.zhiyunelu.com/")
                .uploadImages(tokenInfo.account.loginName,tokenInfo.account.displayName,finalPhoneNum,parts)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiService.ApiObserver<picture>() {
                    @Override
                    protected boolean processResult(picture picture, ApiResult<picture> res) {
                        try {
                            Logger.e("-------------------------");
                            Log.e("-----------", "uploadImages:"+finalPhoneNum);
                            Log.e("-----------", "uploadImages: 上传成功");
                            Toast.makeText(mainActivity,"上传成功",Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e("Exception","picture没有数据");
                        }
                        return true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.e("-----------", "uploadImages: 图片上传失败"+e.toString());
                        Toast.makeText(mainActivity,"图片上传失败",Toast.LENGTH_SHORT).show();

                    }
                });


    }




}
