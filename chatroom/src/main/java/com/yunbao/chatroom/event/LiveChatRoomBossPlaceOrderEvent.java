package com.yunbao.chatroom.event;

import com.yunbao.common.bean.UserBean;

/**
 * Created by Sky.L on 2020-11-04
 * 聊天室内   老板下单成功，发送通知
 */
public class LiveChatRoomBossPlaceOrderEvent {
    private UserBean mToUserBean;

    public LiveChatRoomBossPlaceOrderEvent(UserBean toUserBean) {
        mToUserBean = toUserBean;
    }

    public UserBean getToUserBean() {
        return mToUserBean;
    }

    public void setToUserBean(UserBean toUserBean) {
        mToUserBean = toUserBean;
    }
}
