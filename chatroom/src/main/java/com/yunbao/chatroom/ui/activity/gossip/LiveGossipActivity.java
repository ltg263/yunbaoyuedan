package com.yunbao.chatroom.ui.activity.gossip;

import android.widget.FrameLayout;
import com.yunbao.common.bean.LiveInfo;
import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.behavior.factory.AbsBehaviorFactory;
import com.yunbao.chatroom.business.behavior.factory.GossipBehaviorFactory;
import com.yunbao.chatroom.business.socket.base.callback.WheatControllListner;
import com.yunbao.chatroom.business.socket.gossip.GossipSocketProxy;
import com.yunbao.chatroom.business.socket.gossip.callback.GossipWheatLisnter;
import com.yunbao.chatroom.business.socket.gossip.impl.GossipSmsListnerImpl;
import com.yunbao.chatroom.ui.activity.LiveActivity;
import com.yunbao.chatroom.ui.view.seat.LiveGossipSeatViewHolder;

public  abstract class LiveGossipActivity extends LiveActivity<GossipSmsListnerImpl, GossipSocketProxy, LiveGossipSeatViewHolder> implements
        GossipWheatLisnter,
        WheatControllListner
{

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_gossip;
    }
    @Override
    protected AbsBehaviorFactory onCreateBehaviorFactory() {
        return new GossipBehaviorFactory();
    }
    @Override
    protected GossipSocketProxy onCreateSocketProxy(String chatserver, GossipSmsListnerImpl socketMessageBrider, LiveInfo parseLiveInfo) {
        return new GossipSocketProxy(chatserver,socketMessageBrider,parseLiveInfo);
    }
    @Override
    protected LiveGossipSeatViewHolder initSeatViewHolder(FrameLayout vpSeatContainer) {
        return new LiveGossipSeatViewHolder(this,vpSeatContainer);
    }

    @Override
    protected GossipSmsListnerImpl initSocketMessageBride() {
        GossipSmsListnerImpl gossipSmsListner=new GossipSmsListnerImpl(this);
        gossipSmsListner.setGossipWheatLisnter(this);
        gossipSmsListner.setWheatControllListner(this);
        return gossipSmsListner;
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
}
