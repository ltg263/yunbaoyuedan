package com.yunbao.chatroom.business.behavior.gossip;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.chatroom.business.behavior.ApplyQuequeBehavior;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.gossip.GossipSocketProxy;

public class GpApplyQuequeBehavior extends ApplyQuequeBehavior<GossipSocketProxy> {
    @Override
    public void applyUpWheat(LifecycleProvider lifecycleProvider, SuccessListner successListner) {
        if(mSocketProxy!=null&&mLiveBean!=null){
           mSocketProxy.getGossipWheatMannger().applyUpWheat(mLiveBean.getUid(),mLiveBean.getStream(),lifecycleProvider,successListner);

        }
    }
}