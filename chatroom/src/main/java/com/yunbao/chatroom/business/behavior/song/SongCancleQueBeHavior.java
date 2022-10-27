package com.yunbao.chatroom.business.behavior.song;

import android.view.View;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.chatroom.business.behavior.CancleQueBehavior;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.song.SongSocketProxy;

/*派单中取消排队的行为*/
public class SongCancleQueBeHavior extends CancleQueBehavior<SongSocketProxy> {
    @Override
    public void cancleApplyQueue(View view, LifecycleProvider lifecycleProvider, SuccessListner successListner) {
        if(mSocketProxy!=null&&mLiveBean!=null){
           mSocketProxy.getWheatMannger().cancleBossUpWheat(mLiveBean.getUid(),mLiveBean.getStream(),lifecycleProvider,view,successListner);
        }
    }
}
