package com.yunbao.shortvideo;

import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.meihu.beautylibrary.MHSDK;
import com.mob.MobSDK;
import com.mob.OperationCallback;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.rtmp.TXLiveBase;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.DecryptUtil;
import com.yunbao.common.utils.L;
import com.yunbao.im.utils.ImMessageUtil;

import cn.net.shoot.sharetracesdk.ShareTrace;

/**
 * Created by cxf on 2017/8/3.
 */

public class AppContext extends CommonAppContext {

    private boolean mBeautyInited;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        boolean isDebug=isApkInDebug();
        L.setDeBug(isDebug);

        //腾讯云直播鉴权url
        String liveLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/26aa72debb817516c/TXLiveSDK.licence";
        //腾讯云直播鉴权key
        String liveLicenceKey = "5531ed2c8";
        TXLiveBase.getInstance().setLicence(this, liveLicenceUrl, liveLicenceKey);
        //初始化腾讯bugly
        CrashReport.initCrashReport(this);
        CrashReport.setAppVersion(this, CommonAppConfig.getInstance().getVersion());
        //初始化ShareSdk
        MobSDK.init(this,CommonAppConfig.getMetaDataString("MobAppKey"), CommonAppConfig.getMetaDataString("MobAppSecret"));
        MobSDK.submitPolicyGrantResult(true, new OperationCallback<Void>() {
            @Override
            public void onComplete(Void aVoid) {
                L.e("MobSDK", "隐私授权成功---->");
            }

            @Override
            public void onFailure(Throwable throwable) {
                L.e("MobSDK", "隐私授权失败---->");
            }
        });
        //初始化极光推送
        //ImPushUtil.getInstance().init(this);
        //初始化IM
        ImMessageUtil.getInstance().init();
        //初始化 ARouter
        if (isDebug) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
        ShareTrace.init(this);

      /* if (!LeakCanary.isInAnalyzerProcess(this)) {
           LeakCanary.install(this);
       }*/
    }

    /*低内存的时候释放掉GLide的缓存*/
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ImgLoader.clearMemory(this);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        L.e("onTrimMemory==");
    }

    /**
     * 初始化美狐
     */
    public void initBeautySdk(String beautyAppId,String beautyKey) {
        if (!TextUtils.isEmpty(beautyKey)) {
            if (!mBeautyInited) {
                mBeautyInited = true;
                MHSDK.init(this, beautyAppId,beautyKey);
                CommonAppConfig.getInstance().setMhBeautyEnable(true);
                L.e("美狐初始化------->" + beautyKey);
            }
        } else {
            CommonAppConfig.getInstance().setMhBeautyEnable(false);
        }
    }
}
