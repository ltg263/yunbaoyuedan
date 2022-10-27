package com.yunbao.chatroom.business.socket.base.callback;

import com.yunbao.common.bean.LiveChatBean;

public interface ChatMessageListner {
    /**
     * 聊天室  收到聊天消息
     */
    void onChat(LiveChatBean bean);

}
