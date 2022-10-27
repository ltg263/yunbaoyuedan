package com.yunbao.chatroom.business.behavior.friend;

import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.business.behavior.SkPowerBehavior;
import com.yunbao.chatroom.business.socket.friend.FriendSocketProxy;

public class FdSkPowerBehavior extends SkPowerBehavior<FriendSocketProxy> {
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
          mSocketProxy.getFriendWheatMannger().controlDownWheat(userBean,position+1);
        }else{
          mSocketProxy.getFriendWheatMannger().sendSocketSelfDownWheat();
        }
    }

}