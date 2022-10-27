package com.yunbao.chatroom.business.behavior.friend;

import android.view.View;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.business.behavior.ApplyResultBehavior;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.friend.FriendSocketProxy;

public class FdApplyResultBehavior extends ApplyResultBehavior<FriendSocketProxy> {
    @Override
    public void agree(UserBean userBean, LifecycleProvider lifecycleProvider, View view, SuccessListner successListner) {
        if (mSocketProxy != null && mLiveBean != null) {
            mSocketProxy.getFriendWheatMannger().agreeUserUpWheat(userBean, mLiveBean.getStream(), view, lifecycleProvider, successListner);
        }
    }

    @Override
    public void refuse(UserBean userBean, LifecycleProvider lifecycleProvider,SuccessListner successListner) {
        if(mSocketProxy!=null&&mLiveBean!=null){
            mSocketProxy.getFriendWheatMannger().sendSocketHostRefuseBossUpWheat(userBean);
            successListner.success();
        }
    }
}