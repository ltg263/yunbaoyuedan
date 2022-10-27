package com.yunbao.chatroom.business.live.view;

import android.content.Context;

public interface ILiveView {
    /*presnter获取上下文*/
    public Context getContext();
    /*退出房间成功*/
    public void exitSdkRoomSuccess();
    /*进入sdk房间成功*/
    public void enterSdkRoomSuccess();

}
