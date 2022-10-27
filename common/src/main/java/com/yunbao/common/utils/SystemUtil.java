package com.yunbao.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.util.List;

public class SystemUtil {
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                Log.i(context.getPackageName(), "此appimportace =" + appProcess.importance
                        + ",context.getClass().getName()=" + context.getClass().getName());
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台" + appProcess.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "处于前台" + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }
    public static int getWindowsPixelWidth(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        // 取得窗口属性
        manager.getDefaultDisplay().getMetrics(dm);
        // 窗口的宽度
        int viewWidth = dm.widthPixels;
        return viewWidth;
    }

    public static int getWindowsPixelHeight(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        // 取得窗口属性
        manager.getDefaultDisplay().getMetrics(dm);
        // 窗口的宽度
        int screenHeight = dm.heightPixels;
        return screenHeight;
    }



    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }


    /*关闭所有的弹框*/
    public static void disMissAllDialog(FragmentActivity fragmentActivity) {
        List<Fragment> fragmentList=fragmentActivity.getSupportFragmentManager().getFragments();
        for(Fragment fragment:fragmentList){
            if(fragment instanceof DialogFragment){
                DialogFragment dialogFragment= (DialogFragment) fragment;
                if (dialogFragment != null) {
                    dialogFragment.dismissAllowingStateLoss();

                }
            }
        }
    }

}
