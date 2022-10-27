package com.yunbao.im.event;

/**
 * Created by cxf on 2018/10/24.
 */

public class ImUnReadCountEvent {

    public String mUnReadCount;
    //是否为直播聊天室内用到的消息未读红点
    private boolean mLiveChatRoom;

    public ImUnReadCountEvent(String unReadCount) {
        mUnReadCount = unReadCount;
    }

    public ImUnReadCountEvent(String unReadCount, boolean liveChatRoom) {
        mUnReadCount = unReadCount;
        mLiveChatRoom = liveChatRoom;
    }

    public String getUnReadCount() {
        return mUnReadCount;
    }

    public void setUnReadCount(String unReadCount) {
        mUnReadCount = unReadCount;
    }

    public boolean isLiveChatRoom() {
        return mLiveChatRoom;
    }

    public void setLiveChatRoom(boolean liveChatRoom) {
        mLiveChatRoom = liveChatRoom;
    }
}
