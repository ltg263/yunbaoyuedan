package com.yunbao.chatroom.business.behavior.friend;

import android.view.View;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.chatroom.business.behavior.CancleQueBehavior;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.friend.FriendSocketProxy;

public class FdCancleQueBeHavior extends CancleQueBehavior<FriendSocketProxy> {
    @Override
    public void cancleApplyQueue(View view, LifecycleProvider lifecycleProvider, SuccessListner successListner) {
        if(mSocketProxy!=null&&mLiveBean!=null){
            mSocketProxy.getFriendWheatMannger().cancleApplyUpWheat(mLiveBean.getUid(),mLiveBean.getStream(),lifecycleProvider,view,successListner);
        }
    }
}
