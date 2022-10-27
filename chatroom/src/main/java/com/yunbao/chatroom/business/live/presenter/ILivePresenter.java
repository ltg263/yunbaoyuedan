package com.yunbao.chatroom.business.live.presenter;
import com.yunbao.chatroom.business.live.LiveState;
import com.yunbao.im.business.IRoom;

public interface ILivePresenter extends IRoom {
    /*切换角色*/
    public void changeRole(int role);
    /*开启发言*/
    public void openSpeak(boolean openSpeak);
    /*获取当前直播状态*/
    public LiveState getLiveState();
    public void release();

}
