package com.yunbao.common.upload;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.JsonUtil;
import com.yunbao.common.utils.ToastUtil;

/**
 * Created by Sky.L on 2020-12-22
 * 管理上传工具类型
 */
public class FileUploadManager {
    private static FileUploadManager mInstance;

    public static FileUploadManager getInstance(){
        if (mInstance == null){
           synchronized (FileUploadManager.class){
               if (mInstance == null){
                   mInstance = new FileUploadManager();
               }
           }
        }
        return mInstance;
    }

    public void createUploadImpl(final Context context,final CommonCallback<UploadStrategy> commonCallback){
        CommonHttpUtil.getUploadCosInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0){
                    UploadInfoBean infoBean = JsonUtil.getJsonToBean(info[0],UploadInfoBean.class);
                    UploadStrategy uploadStrategy = null;
                    if (infoBean != null){
                        if (UploadInfoBean.CLOUD_TYPE_QINIU.equals(infoBean.getCloudType())){
                            uploadStrategy = new UploadQnImpl(context,infoBean);
                        }else if (UploadInfoBean.CLOUD_TYPE_AWS.equals(infoBean.getCloudType())){
                            uploadStrategy = new AWSUploadImpl(context,infoBean);
                        }
                        if (commonCallback!=null){
                            commonCallback.callback(uploadStrategy);
                        }
                    }
                }else {
                    if (commonCallback!=null){
                        commonCallback.callback(null);
                    }
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onError() {
                super.onError();
                if (commonCallback!=null){
                    commonCallback.callback(null);
                }
            }
        });
    }


    public static void startUpload(final CommonCallback<UploadStrategy> commonCallback) {
        CommonHttpUtil.getUploadCosInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String cloudType = obj.getString("cloudtype");
                    if (UploadInfoBean.CLOUD_TYPE_QINIU.equals(cloudType)) {//七牛云存储
                        JSONObject qiniuInfo = obj.getJSONObject("qiniuInfo");
                        UploadStrategy strategy = new UploadQnImpl2(qiniuInfo.getString("qiniuToken"), qiniuInfo.getString("qiniu_zone"), cloudType);
                        if (commonCallback != null) {
                            commonCallback.callback(strategy);
                        }
                    } else if (UploadInfoBean.CLOUD_TYPE_AWS.equals(cloudType)) {//亚马逊存储
                        UploadInfoBean infoBean = JsonUtil.getJsonToBean(info[0],UploadInfoBean.class);
                        UploadStrategy strategy = new AWSUploadImpl(CommonAppContext.sInstance,infoBean);
                        if (commonCallback != null) {
                            commonCallback.callback(strategy);
                        }
                    }
                }
            }
        });
    }

}
