package com.yunbao.chatroom.business.behavior.song;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.chatroom.business.behavior.ApplyQuequeBehavior;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.song.SongSocketProxy;

public class SongApplyQuequeBehavior extends ApplyQuequeBehavior<SongSocketProxy> {

    @Override
    public void applyUpWheat(LifecycleProvider lifecycleProvider, SuccessListner successListner) {
        if(mSocketProxy!=null&&mLiveBean!=null){
           mSocketProxy.getWheatMannger().applyBossUpWheat(mLiveBean.getUid(),mLiveBean.getStream(),lifecycleProvider,successListner);

        }
    }
}
