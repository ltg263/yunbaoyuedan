package com.yunbao.chatroom.business.behavior.gossip;

import android.view.View;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.business.behavior.ApplyResultBehavior;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.gossip.GossipSocketProxy;

public class GpApplyResultBehavior extends ApplyResultBehavior<GossipSocketProxy> {
    @Override
    public void agree(UserBean userBean, LifecycleProvider lifecycleProvider, View view, SuccessListner successListner) {
        if (mSocketProxy != null && mLiveBean != null) {
            mSocketProxy.getGossipWheatMannger().agreeUserUpWheat(userBean, mLiveBean.getStream(), view, lifecycleProvider, successListner);
        }
    }

    @Override
    public void refuse(UserBean userBean, LifecycleProvider lifecycleProvider,SuccessListner successListner) {
        if(mSocketProxy!=null&&mLiveBean!=null){
            mSocketProxy.getGossipWheatMannger().sendSocketHostRefuseBossUpWheat(userBean);
            successListner.success();
        }
    }
}