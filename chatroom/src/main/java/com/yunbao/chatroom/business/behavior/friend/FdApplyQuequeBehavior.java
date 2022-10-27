package com.yunbao.chatroom.business.behavior.friend;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.chatroom.business.behavior.ApplyQuequeBehavior;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.friend.FriendSocketProxy;

public class FdApplyQuequeBehavior extends ApplyQuequeBehavior<FriendSocketProxy> {
    @Override
    public void applyUpWheat(LifecycleProvider lifecycleProvider, SuccessListner successListner) {
        if(mSocketProxy!=null&&mLiveBean!=null){
           mSocketProxy.getFriendWheatMannger().applyUpWheat(mLiveBean.getUid(),mLiveBean.getStream(),lifecycleProvider,successListner);
        }
    }
}