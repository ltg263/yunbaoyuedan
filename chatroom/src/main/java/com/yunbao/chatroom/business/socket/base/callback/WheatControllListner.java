package com.yunbao.chatroom.business.socket.base.callback;

import com.yunbao.common.bean.UserBean;

public interface WheatControllListner {
    /*监听开启用户发言权限*/
    public void openSpeak(UserBean userBean, boolean isOpen);
    /*监听用户发言状态变化*/
    public void userAudioOpen(String uid, boolean isOpen);
}
