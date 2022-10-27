package com.yunbao.common;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.yunbao.common.event.ShowOrHideLiveRoomFloatWindowEvent;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.utils.L;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;

/**
 * Created by cxf on 2017/8/3.
 */

public class CommonAppContext extends MultiDexApplication {

    public static CommonAppContext sInstance;
    private int mCount;
    private boolean mFront;//是否前台
    private static Handler sMainThreadHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //初始化Http
        CommonHttpUtil.init();
        //初始化友盟统计
      //  UMengUtil.init(this);
        registerActivityLifecycleCallbacks();
    }

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        super.attachBaseContext(base);
    }

    /**
     * 判断当前应用是否是debug状态
     */

    public  boolean isApkInDebug() {
        try {
            ApplicationInfo info = getApplicationContext().getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void registerActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }
            @Override
            public void onActivityStarted(Activity activity) {
                mCount++;
                if (!mFront) {
                    mFront = true;
                    L.e("AppContext------->处于前台");
                    CommonAppConfig.getInstance().setFrontGround(true);
                    if (CommonAppConfig.getInstance().isFloatButtonShowing()){
                        EventBus.getDefault().post(new ShowOrHideLiveRoomFloatWindowEvent(1));
                    }
                }
            }
            @Override
            public void onActivityResumed(Activity activity) {
            }
            @Override
            public void onActivityPaused(Activity activity) {
            }
            @Override
            public void onActivityStopped(Activity activity) {
                    mCount--;
                if (mCount == 0) {
                    mFront = false;
                    L.e("AppContext------->处于后台");
                    CommonAppConfig.getInstance().setFrontGround(false);
                    if (CommonAppConfig.getInstance().isFloatButtonShowing()){
                        EventBus.getDefault().post(new ShowOrHideLiveRoomFloatWindowEvent(2));
                    }
                }
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }
            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public int getResourceColor(int res){
       return getResources().getColor(res);
    }



    /**
     * 获取主线程的Handler
     */
    private static void getMainThreadHandler() {
        try {
            Class clazz = Class.forName("android.app.ActivityThread");
            Field field = clazz.getDeclaredField("sMainThreadHandler");
            field.setAccessible(true);
            Object obj = field.get(clazz);
            if (obj != null && obj instanceof Handler) {
                sMainThreadHandler = (Handler) obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void postDelayed(Runnable runnable, long delayMillis) {
        if (sMainThreadHandler == null) {
            getMainThreadHandler();
        }
        if (sMainThreadHandler != null) {
            sMainThreadHandler.postDelayed(runnable, delayMillis);
        }
    }

    public static void post(Runnable runnable) {
        if (sMainThreadHandler == null) {
            getMainThreadHandler();
        }
        if (sMainThreadHandler != null) {
            sMainThreadHandler.post(runnable);
        }
    }


}
