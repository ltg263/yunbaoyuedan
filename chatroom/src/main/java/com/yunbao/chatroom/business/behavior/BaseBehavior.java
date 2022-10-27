package com.yunbao.chatroom.business.behavior;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.business.socket.SocketProxy;


/*设计实现的目的是确保每一种行为实现的可替换性,保证UI的独立,跟数据层的耦合交由封装后的Behavior*/
public abstract class BaseBehavior<T extends SocketProxy>{
    protected LiveBean mLiveBean;
    protected T mSocketProxy;

    public void subscribe(@NonNull LifecycleOwner lifecycleOwner){
     LiveActivityLifeModel<T> liveActivityLifeModel= LiveActivityLifeModel.getByContext(lifecycleOwner,LiveActivityLifeModel.class);
     mLiveBean=liveActivityLifeModel.getLiveBean();
     mSocketProxy=liveActivityLifeModel.getSocketProxy();
   }

   public void unSubscribe(){
       mSocketProxy=null;
       mLiveBean=null;
   }

}

