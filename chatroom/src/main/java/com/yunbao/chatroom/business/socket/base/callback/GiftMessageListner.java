package com.yunbao.chatroom.business.socket.base.callback;

import com.yunbao.common.bean.ChatReceiveGiftBean;

public interface GiftMessageListner {
    /*收到礼物信息*/
   void onSendGift(ChatReceiveGiftBean bean);
}
