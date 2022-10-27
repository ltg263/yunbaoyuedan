package com.yunbao.chatroom.ui.activity.friend;

import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.yunbao.common.Constants;
import com.yunbao.common.bean.LiveChatBean;
import com.yunbao.common.bean.LiveInfo;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.business.TimeModel;
import com.yunbao.common.utils.Parser;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.MakePairBean;
import com.yunbao.chatroom.business.behavior.factory.AbsBehaviorFactory;
import com.yunbao.chatroom.business.behavior.factory.FriendBehaviorFactory;
import com.yunbao.chatroom.business.socket.base.callback.WheatControllListner;
import com.yunbao.chatroom.business.socket.friend.FriendSocketProxy;
import com.yunbao.chatroom.business.socket.friend.callback.FriendStateListner;
import com.yunbao.chatroom.business.socket.friend.impl.FriendSmsListnerImpl;
import com.yunbao.chatroom.business.socket.gossip.callback.GossipWheatLisnter;
import com.yunbao.chatroom.ui.activity.LiveActivity;
import com.yunbao.chatroom.ui.dialog.MakePairDialogFragment;
import com.yunbao.chatroom.ui.dialog.MakePairFailureDialogFragment;
import com.yunbao.chatroom.ui.view.seat.LiveFriendSeatViewHolder;

import java.util.List;

public  abstract class LiveFriendActivity extends LiveActivity <FriendSmsListnerImpl, FriendSocketProxy,LiveFriendSeatViewHolder>implements
        TimeModel.TimeListner,
        FriendStateListner,
        WheatControllListner,
        GossipWheatLisnter

{

    protected RadioGroup mRgBtnProgress;
    private RadioButton mRadioLevel1;
    private RadioButton mRadioLevel2;
    private RadioButton mRadioLevel3;
    private TimeModel mTimeModel;
    protected int mCurrentState;

    protected int mTotalTime=300;

    /*行为工厂模版*/
    @Override
    protected AbsBehaviorFactory onCreateBehaviorFactory() {
        return new FriendBehaviorFactory();
    }

    /*交友相关socket*/
    @Override
    protected FriendSocketProxy onCreateSocketProxy(String chatserver, FriendSmsListnerImpl socketMessageBrider, LiveInfo parseLiveInfo) {
        return new FriendSocketProxy(chatserver,socketMessageBrider,parseLiveInfo);
    }
    /*座位*/
    @Override
    protected LiveFriendSeatViewHolder initSeatViewHolder(FrameLayout vpSeatContainer) {
        LiveFriendSeatViewHolder liveFriendSeatViewHolder= new LiveFriendSeatViewHolder(this,vpSeatContainer);
        return liveFriendSeatViewHolder;
    }
    /*交友相关socket桥接*/
    @Override
    protected FriendSmsListnerImpl initSocketMessageBride() {
        FriendSmsListnerImpl friendSmsListner=new FriendSmsListnerImpl(this);
        friendSmsListner.setFriendStateListner(this);
        friendSmsListner.setGossipWheatLisnter(this);
        friendSmsListner.setWheatControllListner(this);
        return friendSmsListner;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_friend;
    }

    @Override
    protected void initView() {
        super.initView();
        initSeatItemClickListner();
        mRgBtnProgress = (RadioGroup) findViewById(R.id.rg_btn_progress);
        mRadioLevel1 = (RadioButton) findViewById(R.id.radio_level_1);
        mRadioLevel2 = (RadioButton) findViewById(R.id.radio_level_2);
        mRadioLevel3 = (RadioButton) findViewById(R.id.radio_level_3);
    }

    protected abstract void initSeatItemClickListner();

    @Override
    public void changeState(int state) {
        if(mCurrentState==state){
            return;
        }
        mCurrentState=state;
        switch (state){
            case Constants.STATE_PREPARATION_LINK:
                stateAsPreparationLink();
                break;
            case Constants.STATE_CARDIAC_SELECTION:
                stateAsCardiacSelection();
                break;
            case Constants.STATE_ANNOUNCE_HEARTBEAT:
                stateAnnounceHeartbeat();
                break;
                default:
                  break;
        }
    }

    @Override
    public void heartBeatResult(List<LiveChatBean> chatArray, List<MakePairBean> makePairBeanList) {
        MakePairDialogFragment makePairDialogFragment=new MakePairDialogFragment();
        makePairDialogFragment.makePair(makePairBeanList);
        makePairDialogFragment.show(getSupportFragmentManager());

        if(chatArray!=null){
            for(LiveChatBean liveChatBean:chatArray){
                sendLocalTip(liveChatBean);
            }
        }
        if (makePairBeanList.isEmpty()){
            MakePairFailureDialogFragment failureDialogFragment = new MakePairFailureDialogFragment();
            failureDialogFragment.show(getSupportFragmentManager());
            LiveChatBean bean = new LiveChatBean();
            bean.setContent(WordUtil.getString(R.string.hand_in_Hand_failure));
            bean.setType(LiveChatBean.SYSTEM);
            sendLocalTip(bean);
        }
    }

    /*准备阶段*/
    protected void stateAsPreparationLink() {
        stopCutDown();
        mRadioLevel1.setChecked(true);
        mRadioLevel2.setText(getString(R.string.cardiac_selection));
       if(mLiveSeatViewHolder!=null){
          mLiveSeatViewHolder.onDownAllSeat();
        }
    }

    /*中止倒计时*/
    private void stopCutDown() {
        if(mTimeModel!=null&&mTimeModel.isActivitve()){
           mTimeModel.dispose();
        }
        mTotalTime=300;
    }

    /*心动选择*/
    protected void stateAsCardiacSelection() {
        startCardiacSelectionCutDown();
        mRadioLevel2.setChecked(true);
    }

    /*开始心动倒计时*/
    private void startCardiacSelectionCutDown() {
        if(mTimeModel==null){
           mTimeModel=new TimeModel().
           setParserMode(Parser.MODE_MINUTE)
           .setState(TimeModel.COUNT_DOWN)
           .setBeforeString(WordUtil.getString(R.string.time)+" ")
           .setTotalUseTime(mTotalTime);
        }
        mTimeModel.addTimeListner(this);
        mTimeModel.start();
    }

    /*公布心动*/
    protected void stateAnnounceHeartbeat() {
        stopCutDown();
        mRadioLevel2.setText(getString(R.string.cardiac_selection));
        mRadioLevel3.setChecked(true);
    }

    /*倒计时显示数据*/
    @Override
    public void time(String string) {
        mRadioLevel2.setText(string);
    }

    @Override
    public void compelete() {

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

    @Override
    public void applyWheat(String uid, boolean isUp) {
    }
    @Override
    public void argreeUpWheat(UserBean userBean, int seatid) {
        String tip=getString(R.string.up_normal_wheat,userBean.getUserNiceName(),Integer.toString(seatid));
        sendLocalTip(tip);
        mLiveSeatViewHolder.onUpperSeat(userBean,seatid-1);
    }

    @Override
    public void refuseUpWheat(UserBean userBean) {

    }

    private void showDownWheatTip(int index, UserBean userBean) {
        String tip=getString(R.string.user_down_wheat,userBean.getUserNiceName(),Integer.toString(index+1));
        sendLocalTip(tip);
    }

    @Override
    public int downWheat(UserBean userBean, boolean isSelf) {
        int index= mLiveSeatViewHolder.onDownSeat(userBean);
        if(index!=-1){
            showDownWheatTip(index,userBean);
        }
        return index;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTimeModel!=null){
           mTimeModel.clear();
        }
    }
}
