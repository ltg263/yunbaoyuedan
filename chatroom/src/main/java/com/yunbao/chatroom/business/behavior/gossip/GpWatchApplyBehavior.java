package com.yunbao.chatroom.business.behavior.gossip;

import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.business.behavior.WatchApplyBehavior;
import com.yunbao.chatroom.business.socket.gossip.GossipSocketProxy;
import com.yunbao.chatroom.business.socket.gossip.callback.GossipWheatLisnter;

public class GpWatchApplyBehavior extends WatchApplyBehavior<GossipSocketProxy> implements GossipWheatLisnter {

    @Override
    public void watch(WatchApplyListner watchApplyListner) {
        mWatchApplyListner=watchApplyListner;
        if(mSocketProxy!=null){
           mSocketProxy.getGossipWheatMannger().addGossipWheatLisnter(this);
        }
    }
    @Override
    public void applyWheat(String uid, boolean isUp) {
        if(isUp){
            mWatchApplyListner.watch(isUp);
        }
    }
    @Override
    public void argreeUpWheat(UserBean userBean, int position) {
    }
    @Override
    public void refuseUpWheat(UserBean userBean) {

    }
    @Override
    public int downWheat(UserBean userBean, boolean isSelf) {
        return 0;
    }
    @Override
    public void unSubscribe() {
        super.unSubscribe();
        if(mSocketProxy!=null){
            mSocketProxy.getGossipWheatMannger().removeGossipWheatLisnter(this );
        }
    }


}
