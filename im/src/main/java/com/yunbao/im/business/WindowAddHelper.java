package com.yunbao.im.business;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ToastUtil;

import java.lang.reflect.Method;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class WindowAddHelper {
    private static final String TAG = "WindowAddHelper";
    public static final int PERMISSON_OVERLAYS=10;
    private SysPermisssonFragment mSysPermisssonFragment;
    private Context mContext;

    public WindowAddHelper(FragmentActivity activity) {
        this.mContext=activity;
        this.mSysPermisssonFragment = new SysPermisssonFragment();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();
        tx.add(mSysPermisssonFragment, "ProcessFragment").commit();
    }

    public  WindowManager.LayoutParams createDefaultWindowsParams(int x, int y) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //初始化位置
        params.gravity = Gravity.CENTER | Gravity.LEFT;
        params.x = x;
        params.y = y;
        params.width= ActionBar.LayoutParams.WRAP_CONTENT;
        params.height= ActionBar.LayoutParams.WRAP_CONTENT;
        //设置图片格式，效果为背景透明
        params.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //接收touch事件
        params.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        //排版不受限制
        params.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//6.0+
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        else {
            params.type =  WindowManager.LayoutParams.TYPE_TOAST;
        }
        return params;
    }

    /*检查悬浮窗权限*/
    public Observable<Boolean> checkOverLay(Context context,boolean needRequestPermisson) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isDrawOverLays=isDrawOverLay();
            if(!isDrawOverLays&&needRequestPermisson){
                return  openMakeWindowsPermissonDialog(context).flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(Boolean aBoolean) throws Exception {
                     return requestOverLay();
                    }
                });
            }
            return  Observable.just(isDrawOverLays);
        }else{
            return  Observable.just(true);
        }
    }

    /**
     * 判断是否拥有悬浮窗权限
     *
     * @param isApplyAuthorization 是否申请权限
     */
    public  boolean canDrawOverlays(Context context, boolean isApplyAuthorization) {
        //Android 6.0 以下无需申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //判断是否拥有悬浮窗权限，无则跳转悬浮窗权限授权页面
            if (Settings.canDrawOverlays(context)) {
                return true;
            } else {
                ToastUtil.show("请先开启【悬浮窗】权限");
                if (isApplyAuthorization) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                    if (context instanceof Service) {
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    }
                    context.startActivity(intent);
                    return false;
                } else {
                    return false;
                }
            }
        } else {
            return true;
        }
    }

    public  boolean canBackgroundStart(Context context) {
        AppOpsManager ops = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        try {
            int op = 10021; // >= 23 
            // ops.checkOpNoThrow(op, uid, packageName) 
            Method method = ops.getClass().getMethod("checkOpNoThrow", new Class[]
                    {int.class, int.class, String.class}
            );
            Integer result = (Integer) method.invoke(ops, op, android.os.Process.myUid(), context.getPackageName());
            return result == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            Log.e(TAG, "not support", e);
        }
        return false;
    }



    public boolean isDrawOverLay() {
        boolean isDrawOverLays=true;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
             isDrawOverLays= Settings.canDrawOverlays(mContext);
        }
        return isDrawOverLays;
    }


    private Observable<Boolean> openMakeWindowsPermissonDialog(final Context context){
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                openMakeWindowsPermissonDialog(context,e);
            }
        });
    }

    private void openMakeWindowsPermissonDialog(Context context,final ObservableEmitter<Boolean> e){
        Dialog dialog= new DialogUitl.Builder(context)
                .setTitle("")
                .setContent("你的手机没有授权浮窗权限,通话最小化无法正常使用")
                .setCancelable(true)
                .setBackgroundDimEnabled(true)
                .setCancelString(DialogUitl.GONE)
                .setConfrimString("开启")
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        e.onNext(true);
                    }
                })
                .build();
        dialog.setCancelable(false);
        dialog.show();
    }

    public  Observable<Boolean>  requestOverLay(){
         if(mSysPermisssonFragment!=null){
           return Observable.create(new ObservableOnSubscribe<Boolean>() {
               @Override
               public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                   mSysPermisssonFragment.startAciton(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,e);
               }
           }) ;
         }
        return null;
    }

    public boolean moveToFront(Context context) {
        if(context==null){
            return false;
        }
        if (Build.VERSION.SDK_INT >= 11) { // honeycomb
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> recentTasks = manager.getRunningTasks(Integer.MAX_VALUE);
            for (int i = 0; i < recentTasks.size(); i++){
                Log.e("xk", "  "+recentTasks.get(i).baseActivity.toShortString() + "   ID: "+recentTasks.get(i).id+"");
                Log.e("xk","@@@@  "+recentTasks.get(i).baseActivity.toShortString());
                // bring to front
                if (recentTasks.get(i).baseActivity.toShortString().indexOf(context.getPackageName()) > -1) {
                    if(recentTasks.get(i).baseActivity.getShortClassName().equals(context.getClass().getName())){
                        manager.moveTaskToFront(recentTasks.get(i).id, ActivityManager.MOVE_TASK_WITH_HOME);
                        return true;
                    }
                }
            }
        }
        return false;
    }






}
