package com.yunbao.im.views.call;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.business.acmannger.ActivityMannger;
import com.yunbao.common.business.acmannger.ReleaseListner;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.RomUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.views.AbsViewHolder2;
import com.yunbao.im.R;
import com.yunbao.im.business.CallIMHelper;
import com.yunbao.im.business.ICallPresnter;
import com.yunbao.im.business.IExpotFlowContainer;
import com.yunbao.im.business.IVideoCallView;
import com.yunbao.im.business.TimeModel;
import com.yunbao.im.business.WindowAddHelper;
import com.yunbao.im.config.CallConfig;
import com.yunbao.im.custom.FloatFrameLayout;
import com.yunbao.im.http.ImHttpUtil;
import com.yunbao.im.receiver.HomeWatcherReceiver;
import com.yunbao.im.utils.Utils;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public abstract class AbsCallViewHolder extends AbsViewHolder2 implements View.OnClickListener, IVideoCallView<TXCloudVideoView>, IExpotFlowContainer {

    protected ICallPresnter presnter;
    protected CallWaitViewHolder callWaitViewHolder;
    private WindowAddHelper windowAddHelper;
    private WindowManager windowManager;
    private FloatFrameLayout windowsFloatLayout;
    private boolean isFloatAtWindow;
    protected int roomId;
    protected UserBean userBean;

    /*home????????????*/
    private HomeWatcherReceiver mHomeWatcherReceiver;
    private ReleaseListner mReleaseListner;
    private Disposable mDisposable;

    public AbsCallViewHolder(Context context, ViewGroup parentView, int roomId,UserBean userBean) {
        super(context, parentView, roomId,userBean);
        CallConfig.setIsBusy(true);
    }

    @Override
    public void init() {
        setOnClickListner(R.id.btn_close,this);
        setOnClickListner(R.id.btn_narrow,this);
        windowAddHelper=new WindowAddHelper((FragmentActivity) mContext);
        mHomeWatcherReceiver=new HomeWatcherReceiver();

        /*??????home????????????*/
        mHomeWatcherReceiver.setShortHomeClickLitner(new HomeWatcherReceiver.ShortHomeClickLitner() {
            @Override
            public void shortClick() {
                ActivityMannger.getInstance().setBackGround(true);
                checkPermissonOpenNarrow(mContext,false);
            }
        });

        ActivityMannger.getInstance().setOnLaunchListner(new ActivityMannger.OnLaunchListner() {
            @Override
            public boolean launchFromBackGround() {
                if(isFloatAtWindow){
                   return false;
                }else if(!windowAddHelper.isDrawOverLay()){
                  checkPermissonOpenNarrow(ActivityMannger.getInstance().getMainStackTopActivity(),true);
                }
                return true;
            }
        });
    }


    public void showWaitViewHolder(int state) {
        callWaitViewHolder=new CallWaitViewHolder(mContext, (ViewGroup)mContentView,stateCall(),userBean);
        callWaitViewHolder.setAbsCallViewHolder(this);
        callWaitViewHolder.addToParent();
        callWaitViewHolder.changeState(state);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_close){
            cancleAndBrocast();
        }else if(id==R.id.btn_narrow){
            checkPermissonOpenNarrow(mContext,true);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        registerReceiverHomeWatch();
    }


    protected  void registerReceiverHomeWatch(){
        try {
            if(mContext!=null){
                mContext.registerReceiver(mHomeWatcherReceiver,new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiverHomeWatch();

    }

    private void unregisterReceiverHomeWatch() {
        try {
        if(mContext!=null){
                mContext.unregisterReceiver(mHomeWatcherReceiver);
         }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /*??????????????????*/
    protected  void openNarrow(){
        if(!ClickUtil.canClick()){
            return;
        }

       isFloatAtWindow=true;
       final  WindowManager.LayoutParams layoutParams=windowAddHelper.createDefaultWindowsParams(0,100);
       //Android 8.0???type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        final View view=exportFlowView();
        initWindowMannger();
        if(view!=null){
            getActivity().moveTaskToBack(false);//????????????????????????
            int time=delayToFloatWindowTime();
            if(time<=0){
               createNarrowWindow(layoutParams,view);
            }else{
                mDisposable= Observable.timer(time, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                     createNarrowWindow(layoutParams,view);
                    }
                });
            }
        }
    }

    protected int delayToFloatWindowTime() {
        return 0;
    }


    private void disposable() {
        if(mDisposable!=null&&!mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }


    private void createNarrowWindow(WindowManager.LayoutParams layoutParams,View view) {
        windowsFloatLayout=new FloatFrameLayout(mContext);
        makeParm(windowsFloatLayout,layoutParams,view);
        windowsFloatLayout.setView(view);
        windowsFloatLayout.setWmParams(layoutParams);
        windowManager.addView(windowsFloatLayout,layoutParams);
        windowsFloatLayout.setOnNoTouchClickListner(new FloatFrameLayout.OnNoTouchClickListner() {
            @Override
            public void click(View view) {
                restoreVideoFromWindowFlat(view);
            }
        });
    }


    /*?????????windowMannger*/
    private void initWindowMannger() {
        if(windowManager==null){
            windowManager  = (WindowManager) CommonAppContext.sInstance
                    .getSystemService(CommonAppContext.sInstance.WINDOW_SERVICE);
        }
    }

    /*???????????????????????????video???????????????*/
    protected void makeParm(FloatFrameLayout windowsFloatLayout, WindowManager.LayoutParams layoutParams, View view) {
        layoutParams.width=Utils.subWidth!=0?Utils.subWidth:view.getWidth();
        layoutParams.height=Utils.subHeight!=0?Utils.subHeight+ DpUtil.dp2px(20) :view.getHeight();
    }
    public void  restoreVideoFromWindowFlat(){
        disposable();
        if(windowsFloatLayout!=null){
            restoreVideoFromWindowFlat(windowsFloatLayout);
        }
    }

    /*?????????????????????activity??????*/
    private void restoreVideoFromWindowFlat(View view) {
        if(windowManager==null||!isFloatAtWindow){
            return;
        }
        try {
            isFloatAtWindow=false;
            windowManager.removeView(view);
            /*???????????????*/
//            if(!ActivityMannger.getInstance().isBackGround()){
//                ActivityMannger.getInstance().launchOntherStackToTopActivity(false,ActivityMannger.getInstance().getMainStackTopActivity());
//            }
            ActivityMannger.getInstance().launchOntherStackToTopActivity(false,ActivityMannger.getInstance().getMainStackTopActivity());

            if(!this.isDestoryed()){
                this.restoreFlowView((FloatFrameLayout) view);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*?????????????????????????????????????????????*/
    public void checkPermissonOpenNarrow(Context context,boolean needRequestPermisson) {
        if (RomUtil.isMiui()){
            L.e("--isMiui-->");
            //???????????????????????????????????????????????????
            if (!windowAddHelper.canBackgroundStart(mContext)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                if (context instanceof Service) {
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
                ToastUtil.show("??????????????????????????????????????????");
                return;
            }
        }
        if (windowAddHelper.canDrawOverlays(mContext,true)){
            openNarrow();
        }
//        if( windowAddHelper!=null&&context!=null){
//            windowAddHelper.checkOverLay(context,needRequestPermisson).subscribe(new Consumer<Boolean>() {
//                @Override
//                public void accept(Boolean aBoolean) throws Exception {
//                    if(aBoolean){
//                        if (windowAddHelper.canDrawOverlays(mContext,true)){
//                            openNarrow();
//                        }
//                    }
//                }
//            });
//        }
    }


    public ICallPresnter getPresnter() {
        return presnter;
    }


    /*??????????????????*/
    protected  void removeWait(){
        if(callWaitViewHolder!=null){
            callWaitViewHolder.removeFromParent();
            callWaitViewHolder.onDestroy();
            callWaitViewHolder=null;
        }
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
        this.roomId= (int) args[0];
        userBean= (UserBean) args[1];
    }

    /*????????????*/
    public void accept(){
        if(presnter!=null){
           presnter.init();
           presnter.enterRoom(roomId);
           removeWait();
        }
    }

    /*???????????????????????????*/
    public void acceptAndBrocast(String touid){
        ImHttpUtil.getUserOnlineStatus(touid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0){
                    accept();
                    CallIMHelper.sendAccept(stateCall(),userBean.getId());
                }else {
                    ToastUtil.show(msg);
                    refuse();
                }
            }
        });
    }

    /*???????????????????????????*/
    public void refuseAndBrocast(){
        CallIMHelper.sendRefuse(stateCall(),userBean.getId());
        refuse();
    }

    /*????????????*/
    public void  refuse(){
        if(presnter!=null){
            presnter.exitRoom();
        }
    }

    /*??????????????????????????????*/
    public void cancleAndBrocast(){
        cancle();
        CallIMHelper.sendCancle(stateCall(), TimeModel.getInstance().getTime(),userBean.getId());
    }

    /*???????????????*/
    public void cancle(){
        if(presnter!=null){
           presnter.exitRoom();
        }
    }

    public void setCallPresnter(ICallPresnter presnter) {
        this.presnter = presnter;
    }
    /*presnter.exitRoom ????????? ?????????onExitRoom??????*/

    @Override
    public void onExitRoom() {

         onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void onDestroy() {
        disposable();
        L.e(getClass().getSimpleName(),"onDestroy-----");
        unregisterReceiverHomeWatch();
        if(mReleaseListner!=null){
           mReleaseListner.release();//??????finish()???????????????????????????????????????,????????????????????????????????????????????????
           mReleaseListner=null;
        }
        if(presnter!=null){
            presnter.release();
        }
        removeWait();
//        TimeModel.getInstance().clear();
        clearFloatWindowState();
        presnter=null;
        windowManager=null;
        CallConfig.setIsBusy(false);
        ActivityMannger.getInstance().setOnLaunchListner(null);
        super.onDestroy();
    }

    private void clearFloatWindowState() {
        if(windowsFloatLayout!=null&&isFloatAtWindow){
           windowsFloatLayout.performTouchClick();
           windowsFloatLayout=null;
        }
    }


    public void setReleaseListner(ReleaseListner releaseListner) {
        mReleaseListner = releaseListner;
    }

    /*?????????????????????????????????,??????????????????????????????,????????????UI*/
    public abstract int stateCall();

}
