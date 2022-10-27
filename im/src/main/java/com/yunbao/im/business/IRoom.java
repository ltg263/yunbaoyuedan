package com.yunbao.im.business;

public interface IRoom {
    /*初始化*/
    public void init();
    /*退出房间 implicit=true直接退出,=false只退出sdk房间*/
    public void exitRoom(boolean implicit);
    public void exitRoom();
    /*进入房间*/
    public void enterRoom(int roomId);

}
