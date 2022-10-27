package com.yunbao.chatroom.business.behavior.song;

import android.view.View;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.business.behavior.ApplyResultBehavior;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.song.SongSocketProxy;

public class SongApplyResultBehavior extends ApplyResultBehavior<SongSocketProxy> {
    @Override
    public void agree(UserBean userBean, LifecycleProvider lifecycleProvider, View view,SuccessListner successListner) {
        if(mSocketProxy!=null&&mLiveBean!=null){
           mSocketProxy.getWheatMannger().agreeUserUpBossWheat(userBean,mLiveBean.getStream(),view,lifecycleProvider,successListner);
        }
    }


    @Override
    public void refuse(UserBean userBean, LifecycleProvider lifecycleProvider,SuccessListner successListner) {
        if(mSocketProxy!=null&&mLiveBean!=null){
           mSocketProxy.getWheatMannger().refuseUserUpBossWheat(userBean);
            successListner.success();
        }
    }
}
