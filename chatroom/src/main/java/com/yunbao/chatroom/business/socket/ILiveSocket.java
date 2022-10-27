package com.yunbao.chatroom.business.socket;

import com.yunbao.chatroom.bean.SocketSendBean;

public interface ILiveSocket {
    public void connect();
    public void disConnect();
    public void send(SocketSendBean socketSendBean);
}
