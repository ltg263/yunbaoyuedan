package com.yunbao.chatroom.business.socket.base.callback;

public interface SystemMessageListnter  {
  /*结束直播回调*/
  public void endLive();
  /*进入房间*/
  public void enter(String uid,String uname,boolean isEnter);
}
