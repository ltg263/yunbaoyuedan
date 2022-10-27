package com.yunbao.chatroom.business.behavior.friend;

import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.business.behavior.WatchApplyBehavior;
import com.yunbao.chatroom.business.socket.friend.FriendSocketProxy;
import com.yunbao.chatroom.business.socket.gossip.callback.GossipWheatLisnter;

public class FdWatchApplyBehavior extends WatchApplyBehavior<FriendSocketProxy> implements GossipWheatLisnter {

    @Override
    public void watch(WatchApplyListner watchApplyListner) {
        mWatchApplyListner=watchApplyListner;
        if(mSocketProxy!=null){
           mSocketProxy.getFriendWheatMannger().addGossipWheatLisnter(this);
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
           mSocketProxy.getFriendWheatMannger().removeGossipWheatLisnter(this );
        }
    }

}
