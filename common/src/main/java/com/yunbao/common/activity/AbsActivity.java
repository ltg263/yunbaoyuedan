package com.yunbao.common.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.R;
import com.yunbao.common.business.acmannger.ActivityMannger;
import com.yunbao.common.event.ReduceEvent;
import com.yunbao.common.interfaces.LifeCycleListener;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.HeybroadHelper;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.LanguageUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.SystemUtil;
import com.yunbao.common.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2017/8/3.
 */


public abstract class AbsActivity extends RxAppCompatActivity {

    protected String mTag;
    protected Context mContext;
    protected List<LifeCycleListener> mLifeCycleListeners;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addToActivityMannger();
//        L.e("当前AbsActivity="+this.getClass().getName());
        mTag = this.getClass().getSimpleName();
        getIntentParams();
        setStatusBar();
        setContentView(getLayoutId());
        mContext = this;
        mLifeCycleListeners = new ArrayList<>();
        main(savedInstanceState);
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onCreate();
            }
        }
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
    }


    protected void getIntentParams() {

    }


    public void addToActivityMannger(){
      ActivityMannger.getInstance().addActivity(this);
    }



    public void removToAcitivyMannger(){
        ActivityMannger.getInstance().removeActivity(this);
    }

    protected abstract int getLayoutId();

    protected void main(Bundle savedInstanceState) {
        main();
    }

    protected void main() {

    }

    protected boolean isStatusBarWhite() {
        return false;
    }

    protected void setTitle(String title) {
        TextView titleView = (TextView) findViewById(R.id.titleView);
        if (titleView != null) {
            titleView.setText(title);
        }
    }



     public void setOnClickListener(int id, View.OnClickListener clickListener){
        View view=findViewById(id);
        if(view!=null){
            view.setOnClickListener(clickListener);
        }
     }



    public TextView setRightTitle(String rightTitle){
        TextView titleView =  findViewById(R.id.tv_right_title);
        if(titleView!=null){
          titleView.setText(rightTitle);
        }
        return titleView;
    }


    public void setTabBackGroudColor(String rightTitle){
       View flTab= findViewById(R.id.fl_tab);
       if(flTab!=null){
           flTab.setBackgroundColor(Color.parseColor(rightTitle));
       }
    }


    protected void setTitleById(int title) {
        TextView titleView = (TextView) findViewById(R.id.titleView);
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    protected void setBackImageResoure(int resourceId){
       ImageView imageView=findViewById(R.id.btn_back);
       if(imageView!=null){
         imageView.setImageResource(resourceId);
       }
    }




    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                HeybroadHelper.hideKeyboard(ev, view, AbsActivity.this);//调用方法判断是否需要隐藏键盘
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    public void backClick(View v) {
        if (v.getId() == R.id.btn_back) {
            onBackPressed();
        }
    }

    protected boolean canClick() {
        return ClickUtil.canClick();
    }


    /**
     * 设置透明状态栏
     */
    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (isStatusBarWhite()) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0);
        }
    }

    @Override
    protected void onDestroy() {
        removToAcitivyMannger();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onDestroy();
            }
            mLifeCycleListeners.clear();
            mLifeCycleListeners = null;
        }
        if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*这两个方法的顺序千万不能变化*/
        launchfirstOntherStack();
        setIsBackGround();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onStart();
            }
        }
    }

    private void launchfirstOntherStack(){
      ActivityMannger.getInstance().launchOntherStackToTopActivity(true,this);
    }

    public void setIsBackGround() {
     ActivityMannger.getInstance().setBackGround(SystemUtil.isBackground(this));
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onReStart();
            }
        }
    }


    protected <T extends Activity> void startActivity(Class<T>cs){
        startActivity(new Intent(this,cs));
    }

    protected <T extends Activity> void startActivityForResult(Class<T>cs,int requestCode){
        startActivityForResult(new Intent(this,cs),requestCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onResume();
            }
        }
        //友盟统计
        //UMengUtil.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onPause();
            }
        }
        //友盟统计
        //UMengUtil.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onStop();
            }
        }
        setIsBackGround();
    }

    public void addLifeCycleListener(LifeCycleListener listener) {
        if (mLifeCycleListeners != null && listener != null) {
            mLifeCycleListeners.add(listener);
        }
    }

    public void addAllLifeCycleListener(List<LifeCycleListener> listeners) {
        if (mLifeCycleListeners != null && listeners != null) {
            mLifeCycleListeners.addAll(listeners);
        }
    }

    public void removeLifeCycleListener(LifeCycleListener listener) {
        if (mLifeCycleListeners != null) {
            mLifeCycleListeners.remove(listener);
        }
    }

    /**
     * 未登录的弹窗
     */
    public void showNotLoginDialog() {
//        NotLoginDialogFragment fragment = new NotLoginDialogFragment();
//        fragment.show(getSupportFragmentManager(), "NotLoginDialogFragment");
    }



    public boolean checkLogin() {
//        if (!CommonAppConfig.getInstance().isLogin()) {
//            showNotLoginDialog();
//            return false;
//        }
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReduceEvent(ReduceEvent e) {
        new DialogUitl.Builder(this)
                .setContent(e.getMsg())
                .setCancelable(false)
                .setCancelString(WordUtil.getString(R.string.tip_13))
                .setConfrimString(WordUtil.getString(R.string.to_close))
                .setClickCallback(new DialogUitl.SimpleCallback2() {

                    @Override
                    public void onCancelClick() {
                        ActivityManager activityManager = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                        List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
                        for (ActivityManager.AppTask appTask : appTaskList) {
                            appTask.finishAndRemoveTask();
                        }

                    }
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        RouteUtil.forwardTeenager();
                        if (!dialog.isShowing()){
                            dialog.show();
                        }

                    }
                })
                .build()
                .show();
    }
}
