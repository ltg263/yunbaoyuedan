package com.yunbao.live.bean;

/**
 * Created by cxf on 2018/10/12.
 */

public class LiveEnterRoomBean {

    private LiveUserGiftBean mUserBean;
    private com.yunbao.common.bean.LiveChatBean mLiveChatBean;

    public LiveEnterRoomBean(LiveUserGiftBean UserBean, com.yunbao.common.bean.LiveChatBean liveChatBean) {
        mUserBean = UserBean;
        mLiveChatBean = liveChatBean;
    }


    public LiveUserGiftBean getUserBean() {
        return mUserBean;
    }

    public void setUserBean(LiveUserGiftBean UserBean) {
        mUserBean = UserBean;
    }

    public com.yunbao.common.bean.LiveChatBean getLiveChatBean() {
        return mLiveChatBean;
    }

    public void setLiveChatBean(com.yunbao.common.bean.LiveChatBean liveChatBean) {
        mLiveChatBean = liveChatBean;
    }
}
