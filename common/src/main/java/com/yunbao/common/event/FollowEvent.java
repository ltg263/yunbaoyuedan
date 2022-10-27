package com.yunbao.common.event;

/**
 * Created by cxf on 2018/9/28.
 */

public class FollowEvent {

    private String mToUid;
    private int mAttention;

    public FollowEvent(String toUid, int attention) {
        mToUid = toUid;
        mAttention = attention;
    }

    public String getToUid() {
        return mToUid;
    }

    public void setToUid(String toUid) {
        mToUid = toUid;
    }

    public int getAttention() {
        return mAttention;
    }

    public void setAttention(int attention) {
        mAttention = attention;
    }
}
