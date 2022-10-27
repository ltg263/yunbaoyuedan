package com.yunbao.im.views.call;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.CheckImageView;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ViewUtil;
import com.yunbao.im.R;
import com.yunbao.im.business.IVideoCallView;
import com.yunbao.im.business.TimeModel;
import com.yunbao.im.custom.FloatFrameLayout;
import com.yunbao.im.custom.FlowVideoLayout;
import com.yunbao.im.event.VideoAllCloseEvent;
import com.yunbao.im.utils.Utils;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class VideoCallViewHolder extends AbsCallViewHolder implements IVideoCallView<TXCloudVideoView> {

    //代码里面设置排列,数组index 为1的永远是最上层的小窗口view
    private FlowVideoLayout[]flowVideoLayouts;
    private FlowVideoLayout  flowLayoutMain;
    private FlowVideoLayout  flowLayoutVit;
    private ArrayList<FrameLayout.LayoutParams>layoutParams;
    private CheckImageView ciMute;
    private CheckImageView ciCamera;
    private FrameLayout container;
    private TextView tvCameraToggle;
    private RoundedImageView imgAvator;
    private TextView tvUserName;
    private TextView tvCallTime;
    private FrameLayout flWindowTools;

    private View.OnClickListener toBigClickLisnter;
    private View.OnClickListener showToolsOnclick;
    private TimeModel.TimeListner timeListner;
    private float windowScale;
    private Drawable mSmallBgBorder;


    private boolean mSelfIsBigWindow;//自己是大窗口
    private View v_cover;//预览画面 遮罩



    public VideoCallViewHolder(Context context, ViewGroup parentView, int roomId, UserBean userBean) {
        super(context, parentView, roomId, userBean);
    }


    @Override
    public int stateCall() {
        return Constants.CHAT_TYPE_VIDEO;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_video_call;
    }

    @Override
    public void accept() {
        super.accept();
        if(presnter!=null){
            presnter.startSDKLocalPreview(true);
        }
    }

    @Override
    public void init() {
        super.init();
        flowVideoLayouts=new FlowVideoLayout[2];
        v_cover = LayoutInflater.from(mContext).inflate(R.layout.view_video_call_cover,null);
        flowLayoutMain = (FlowVideoLayout) findViewById(R.id.flowLayout_main);
        flowLayoutVit = (FlowVideoLayout) findViewById(R.id.flowLayout_vit);
        mSmallBgBorder = mContext.getDrawable(R.drawable.bg_call_small_video_border);
        container = (FrameLayout) findViewById(R.id.container);
        ciMute =  findViewById(R.id.ci_mute);
        ciCamera =  findViewById(R.id.ci_camera);
        tvCameraToggle = (TextView) findViewById(R.id.tv_camera_toggle);
        imgAvator = (RoundedImageView) findViewById(R.id.img_avator);
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvCallTime = (TextView) findViewById(R.id.tv_call_time);
        flWindowTools = (FrameLayout) findViewById(R.id.fl_window_tools);

        setUserData();
        setOnClickListner(R.id.btn_mute,this);
        setOnClickListner(R.id.btn_cal_flip,this);
        setOnClickListner(R.id.btn_camera,this);

        layoutParams=Utils.initFloatParamList(mContext);
        toBigClickLisnter=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBig(v);
            }
        };
        showToolsOnclick=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToolWindowVisible();
            }
        };
        setBig(flowLayoutMain);
        tvCallTime.setText("00:00:00");
        timeListner=new TimeModel.TimeListner() {
            @Override
            public void time(String string) {
                tvCallTime.setText(string);
            }
        };
        TimeModel.getInstance().addTimeListner(timeListner);
    }

    private boolean toolsWindowIsVisible() {
       return flWindowTools.getVisibility()==View.VISIBLE;
    }

    private void setUserData() {
        if(userBean!=null){
            ImgLoader.display(mContext,userBean.getAvatar(),imgAvator);
            tvUserName.setText(userBean.getUserNiceName());
        }
    }

    /*设置变大的是哪个视频窗口,变小的可以在acitivty内自由悬浮*/
    private void setBig(View view) {
       if(view==flowLayoutMain){
           flowVideoLayouts[0]=flowLayoutMain;
           flowVideoLayouts[1]=flowLayoutVit;
           mSelfIsBigWindow = true;
       }else{
           flowVideoLayouts[1]=flowLayoutMain;
           flowVideoLayouts[0]=flowLayoutVit;
           mSelfIsBigWindow = false;
       }
           setFlowLayoutParm();
    }

    //设置activity内的悬浮参数
    private void setFlowLayoutParm() {
        int length=flowVideoLayouts.length;
        for(int i=0;i<length;i++){
            View view=flowVideoLayouts[i];
            flowVideoLayouts[i].setLayoutParams(layoutParams.get(i));
            if(i==1){
                container.bringChildToFront(view);
            }
        }
        flowVideoLayouts[0].setBackground(null);
        flowVideoLayouts[0].setPadding(0,0,0,0);
        flowVideoLayouts[1].setBackground(mSmallBgBorder);
        flowVideoLayouts[1].setPadding(DpUtil.dp2px(1),DpUtil.dp2px(1),DpUtil.dp2px(1),DpUtil.dp2px(1));
        setClick();
    }

    /*setMoveable*/
    private void setClick() {
        FlowVideoLayout flowLayout=flowVideoLayouts[0];
        flowLayout.setMoveable(false);
        flowLayout.setOnClick(showToolsOnclick);
        flowLayout=flowVideoLayouts[1];
        flowLayout.setOnClick(toBigClickLisnter);
        flowLayout.setMoveable(true);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id=v.getId();
        if(id==R.id.btn_mute){
           boolean isMute= !presnter.getCallState().isMute;
           presnter.isMute(isMute);
           ciMute.setChecked(isMute);
        }else if(id==R.id.btn_cal_flip){
            boolean isFront= !presnter.getCallState().isFront; //callPresnter层存储相关变量进行toggle
            presnter.isFront(isFront);
        }else if(id==R.id.btn_camera){
            boolean isOpenCamera= !presnter.getCallState().isOpenCamera;
            presnter.openCamera(isOpenCamera);
            ciCamera.setChecked(isOpenCamera);
            L.e("--isOpenCamera--1->"+presnter.getCallState().isOpenCamera);
            if(isOpenCamera){
               tvCameraToggle.setText(R.string.close_camera);
                if (mSelfIsBigWindow){
                    if (v_cover.getParent() != null){
                        flowVideoLayouts[0].removeView(v_cover);
                    }
                }else {
                    if (v_cover.getParent() != null){
                        flowVideoLayouts[1].removeView(v_cover);
                    }
                }
            }else{
               tvCameraToggle.setText(R.string.open_camera);
                if (v_cover.getParent() != null){
                    ((ViewGroup)v_cover.getParent()).removeView(v_cover);
                }
               if (mSelfIsBigWindow){
                   flowVideoLayouts[0].addView(v_cover);
               }else {
                   flowVideoLayouts[1].addView(v_cover);
               }
            }
        }
    }

    private Disposable disposable;
    //计时隐藏
    private void setToolWindowVisible() {
        flWindowTools.setVisibility(View.VISIBLE);
        if(disposable!=null&&!disposable.isDisposed()){
           disposable.dispose();
        }
          disposable= Observable.timer(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
          flWindowTools.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public TXCloudVideoView getVideoView(String id) {
        return flowLayoutVit.getVideoView();
    }


    @Override
    public TXCloudVideoView getMainVideoView() {
        return flowLayoutMain.getVideoView();
    }

    //每个接口声明都写了注释
    @Override
    public void ontherOpenVideo(boolean isOpen) {
        if(presnter!=null&&presnter.getCallState().isVideo&&!isOpen){
           allVideoClose();
        }
    }

    /*当两方视频都关闭的时候进行跳转语音通话界面，暂时还没有处理,仅提供了入口*/
    private void allVideoClose() {
        EventBus.getDefault().post(new VideoAllCloseEvent());
    }

    //为抽象类里面的方法暴露出需要系统级别的悬浮View
    @Override
    public View exportFlowView() {
        flowVideoLayouts[0].setMoveable(false);
        flowVideoLayouts[1].setMoveable(false);
        flowVideoLayouts[0].setOnClick(null);
        flowVideoLayouts[1].setOnClick(null);
        return container;
    }

    /*window浮动窗口设置大小参数*/

    @Override
    protected void makeParm(FloatFrameLayout windowsFloatLayout, WindowManager.LayoutParams layoutParams, View view) {
        layoutParams.width=ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height=ViewGroup.LayoutParams.WRAP_CONTENT;
        float targetWidth=Utils.subWidth;
        float targetHeight=Utils.subHeight;
        float viewWidth=container.getWidth();
        float viewHeight=container.getHeight();
        float scaleWidth=targetWidth/viewWidth;
        float scaleHeight=targetHeight/viewHeight;

        windowScale=scaleWidth>scaleHeight?scaleWidth:scaleHeight;
        container.getLayoutParams().width= (int) viewWidth;
        container.getLayoutParams().height= (int) viewHeight;
        ViewUtil.scaleContents((Activity) mContext,view,windowScale);


        if (presnter.getCallState().isOpenCamera){
            presnter.startSDKLocalPreview(false);
            presnter.startSDKLocalPreview(true);
            presnter.getCallState().isOpenCamera = true;
        }else {
            presnter.startSDKLocalPreview(false);
            presnter.startSDKLocalPreview(true);
            presnter.getCallState().isOpenCamera = false;
        }
        L.e("--isOpenCamera--2->"+presnter.getCallState().isOpenCamera);
//        presnter.startSDKLocalPreview(presnter.getCallState().isOpenCamera);
    }

    /*恢复回原来的界面*/
    @Override
    public void restoreFlowView(FloatFrameLayout floatFrameLayout) {
        floatFrameLayout.removeAllViews();
        ViewGroup viewGroup= (ViewGroup) mContentView;
        container.getLayoutParams().width= ViewGroup.LayoutParams.MATCH_PARENT;
        container.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;
        ViewUtil.scaleContents((Activity) mContext,container,1F/windowScale);
        viewGroup.addView(container,0);
        if (presnter.getCallState().isOpenCamera){
            presnter.startSDKLocalPreview(false);
            presnter.startSDKLocalPreview(true);
            presnter.getCallState().isOpenCamera = true;
        }else {
            presnter.startSDKLocalPreview(false);
            presnter.startSDKLocalPreview(true);
            presnter.getCallState().isOpenCamera = false;
        }
        setClick();
    }

    @Override
    protected int delayToFloatWindowTime(){
        return 1;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        TimeModel.getInstance().removeTimeListner(timeListner);
        TimeModel.getInstance().clear();

    }

}
