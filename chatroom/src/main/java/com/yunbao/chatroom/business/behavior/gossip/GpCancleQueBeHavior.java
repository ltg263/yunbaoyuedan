package com.yunbao.chatroom.business.behavior.gossip;

import android.view.View;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.chatroom.business.behavior.CancleQueBehavior;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.gossip.GossipSocketProxy;

public class GpCancleQueBeHavior extends CancleQueBehavior<GossipSocketProxy> {
    @Override
    public void cancleApplyQueue(View view, LifecycleProvider lifecycleProvider, SuccessListner successListner) {
        if(mSocketProxy!=null&&mLiveBean!=null){
            mSocketProxy.getGossipWheatMannger().cancleApplyUpWheat(mLiveBean.getUid(),mLiveBean.getStream(),lifecycleProvider,view,successListner);
        }
    }
}
