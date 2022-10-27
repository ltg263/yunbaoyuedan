package com.yunbao.chatroom.business.socket.friend.callback;

import com.yunbao.common.bean.LiveChatBean;
import com.yunbao.chatroom.bean.MakePairBean;
import java.util.List;

public interface FriendStateListner {
    public void changeState(int state);
    public void heartBeatResult(List<LiveChatBean>chatArray, List<MakePairBean>makePairBeanList);
}
