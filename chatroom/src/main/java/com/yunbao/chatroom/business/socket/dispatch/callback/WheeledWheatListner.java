package com.yunbao.chatroom.business.socket.dispatch.callback;

import com.yunbao.common.bean.UserBean;

/*轮麦监听器*/
public interface WheeledWheatListner {
    public void openWheeledWheat(boolean isOpen);
    public void changeSpeakUser(UserBean userBean);
}
