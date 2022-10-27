package com.yunbao.chatroom.business.behavior.gossip;

import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.business.behavior.SkPowerBehavior;
import com.yunbao.chatroom.business.socket.gossip.GossipSocketProxy;

public class GpSkPowerBehavior extends SkPowerBehavior<GossipSocketProxy> {
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
          mSocketProxy.getGossipWheatMannger().controlDownWheat(userBean,position+1);
        }else{
          mSocketProxy.getGossipWheatMannger().sendSocketSelfDownWheat();
        }
    }

}