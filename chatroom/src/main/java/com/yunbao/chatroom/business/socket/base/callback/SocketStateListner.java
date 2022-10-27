package com.yunbao.chatroom.business.socket.base.callback;

public interface SocketStateListner  {
    /**
     * 聊天室  连接成功socket后调用
     */
    void onConnect(boolean successConn);
    /**
     * 聊天室  自己的socket断开
     */
    void onDisConnect();

}
