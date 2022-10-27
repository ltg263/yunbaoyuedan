package com.yunbao.chatroom.ui.activity.dispatch;

import android.widget.FrameLayout;
import android.widget.TextView;

import com.yunbao.common.bean.LiveInfo;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.viewanimator.AnimationListener;
import com.yunbao.common.custom.viewanimator.ViewAnimator;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.behavior.factory.AbsBehaviorFactory;
import com.yunbao.chatroom.business.behavior.factory.DisPatchBehaviorFactory;
import com.yunbao.chatroom.business.socket.base.callback.WheatControllListner;
import com.yunbao.chatroom.business.socket.dispatch.DispatchSocketProxy;
import com.yunbao.chatroom.business.socket.dispatch.callback.OrderMessageListner;
import com.yunbao.chatroom.business.socket.dispatch.callback.WheatLisnter;
import com.yunbao.chatroom.business.socket.dispatch.callback.WheeledWheatListner;
import com.yunbao.chatroom.business.socket.dispatch.impl.DispatchSmsListnerImpl;
import com.yunbao.chatroom.ui.activity.LiveActivity;
import com.yunbao.chatroom.ui.view.seat.LiveSpatchViewHolder;

public  abstract class LiveSpatchActivity extends LiveActivity<DispatchSmsListnerImpl,DispatchSocketProxy,LiveSpatchViewHolder> implements
        OrderMessageListner,
        WheatLisnter,
        WheatControllListner,
        WheeledWheatListner {

    protected TextView mBtnOrderTip;
    private boolean mIsStartAnim;


    @Override
    protected DispatchSocketProxy onCreateSocketProxy(String chatserver, DispatchSmsListnerImpl socketMessageBrider, LiveInfo parseLiveInfo) {
        return new DispatchSocketProxy(chatserver,socketMessageBrider,parseLiveInfo);
    }

    @Override
    public void main() {
        super.main();
    }

    @Override
    protected void initView() {
        super.initView();
        mBtnOrderTip = (TextView) findViewById(R.id.btn_order_tip);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_spatch;
    }



    @Override
    protected LiveSpatchViewHolder initSeatViewHolder(FrameLayout vpSeatContainer) {
        return new LiveSpatchViewHolder(this,vpSeatContainer);
    }


    @Override
    protected AbsBehaviorFactory onCreateBehaviorFactory() {
       return new DisPatchBehaviorFactory();
    }

    @Override
    protected DispatchSmsListnerImpl initSocketMessageBride() {
        DispatchSmsListnerImpl dispatchSmsListner=new DispatchSmsListnerImpl(this);
        dispatchSmsListner.setOrderMessageListner(this);
        dispatchSmsListner.setWheatLisnter(this);
        dispatchSmsListner.setWheeledWheatListner(this);
        dispatchSmsListner.setWheatControllListner(this);
        return dispatchSmsListner;
    }

    @Override
    public void onOrderUpDate(String skillid) {
        if(mLiveActivityLifeModel !=null){
           mLiveActivityLifeModel.setSkillId(skillid);
        }
        startShowOrderButton();
    }

    @Override
    public void applyBosssWheat(String uid, boolean isUp) {

    }

    @Override
    public void upNormalWheatSuccess(UserBean userBean, int positon) {
        String tip=getString(R.string.up_normal_wheat,userBean.getUserNiceName(),Integer.toString(positon));
        sendLocalTip(tip);
        mLiveSeatViewHolder.onUpperSeat(userBean,positon-1);
    }

    @Override
    public void upBossWheatSuccess(UserBean userBean) {
        String tip=getString(R.string.up_boss_wheat,userBean.getUserNiceName());
        sendLocalTip(tip);
        mLiveSeatViewHolder.onUpperSeat(userBean,7);
    }

    @Override
    public void resfuseUpWheat(String uid) {

    }

    @Override
    public int downWheat(UserBean userBean, boolean isSelf) {
       int index= mLiveSeatViewHolder.onDownSeat(userBean,true);
       if(index!=-1){
          showDownWheatTip(index,userBean);
       }
       if(index==7){
           startHideOrderButton();

       }

       return index;
    }

    /*显示下麦信息*/
    private void showDownWheatTip(int index,UserBean userBean) {
        String tip="";
        if(index==7){
            tip=userBean.getUserNiceName() + getString(R.string.boss_down_wheat);
        }else{
            tip=getString(R.string.user_down_wheat,userBean.getUserNiceName(),Integer.toString(index+1));
        }
        sendLocalTip(tip);
    }

    /*轮流发言中，切换发言者*/
    @Override
    public void changeSpeakUser(UserBean userBean) {

    }
    /*切换是否开启轮流的状态*/
    @Override
    public void openWheeledWheat(boolean isOpen) {

    }


    /*显示订单按钮*/
    private void startShowOrderButton() {
        if(mIsStartAnim){
            return;
        }
        mIsStartAnim=true;
        ViewAnimator.animate(mBtnOrderTip).duration(1000).
                slideRightIn().start()
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        mIsStartAnim=false;
                    }
                });
    }

    /*隐藏订单按钮*/
    private void startHideOrderButton() {
        if(mIsStartAnim||mBtnOrderTip.getTranslationX()>100){
            return;
        }
        ViewAnimator.animate(mBtnOrderTip).duration(1000).
                slideRightOut().start()
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        mIsStartAnim=false;
                    }
              });
    }

    @Override
    public void openSpeak(UserBean userBean, boolean isOpen) {

    }

    @Override
    public void userAudioOpen(String uid, boolean isOpen) {
        if(mLiveSeatViewHolder!=null){
           mLiveSeatViewHolder.onTalkStateChange(uid,isOpen);
        }
    }


}
