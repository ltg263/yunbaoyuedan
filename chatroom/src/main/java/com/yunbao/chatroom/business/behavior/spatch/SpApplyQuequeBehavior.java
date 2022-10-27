package com.yunbao.chatroom.business.behavior.spatch;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.chatroom.business.behavior.ApplyQuequeBehavior;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.dispatch.DispatchSocketProxy;

public class SpApplyQuequeBehavior extends ApplyQuequeBehavior<DispatchSocketProxy> {

    @Override
    public void applyUpWheat(LifecycleProvider lifecycleProvider, SuccessListner successListner) {
        if(mSocketProxy!=null&&mLiveBean!=null){
           mSocketProxy.getWheatMannger().applyBossUpWheat(mLiveBean.getUid(),mLiveBean.getStream(),lifecycleProvider,successListner);
        }
    }
}
