package com.yunbao.main.event;

/**
 * Created by Sky.L on 2020-07-04
 */
public class OrderCancelEvent {
    private boolean mIsCancel;

    public OrderCancelEvent(boolean isCancel) {
        mIsCancel = isCancel;
    }

    public boolean isCancel() {
        return mIsCancel;
    }

    public void setCancel(boolean cancel) {
        mIsCancel = cancel;
    }
}
