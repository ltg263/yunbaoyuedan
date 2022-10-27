package com.yunbao.chatroom.business.behavior;

import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.business.socket.SocketProxy;

/*控制麦的行为*/
public abstract class SkPowerBehavior <T extends SocketProxy> extends BaseBehavior<T>{
    /*设置麦是否可用*/
    public abstract void sendWheatIsOpen(UserBean userBean,boolean isOpen);
    /*下麦控制*/
    public abstract void downWheat(UserBean userBean,int position,boolean isSelf);
}
