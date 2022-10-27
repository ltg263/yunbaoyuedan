package com.yunbao.chatroom.business.behavior.spatch;

import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.business.behavior.SkPowerBehavior;
import com.yunbao.chatroom.business.socket.dispatch.DispatchSocketProxy;

public class SpSkPowerBehavior extends SkPowerBehavior<DispatchSocketProxy> {
    @Override
    public void sendWheatIsOpen(UserBean userBean, boolean isOpen) {
        if(mSocketProxy!=null){
           mSocketProxy.getWheatControllMannger().sendWheatIsOpen(userBean,isOpen);
        }
    }
    @Override
    public void downWheat(UserBean userBean, int position, boolean isSelf) {
        if(mSocketProxy==null){
           return;
        }
        if(!isSelf){
          mSocketProxy.getWheatMannger().controllDownWheat(userBean,position+1);
        }else{
          mSocketProxy.getWheatMannger().sendSocketSelfDownWheat();
        }
    }

}
