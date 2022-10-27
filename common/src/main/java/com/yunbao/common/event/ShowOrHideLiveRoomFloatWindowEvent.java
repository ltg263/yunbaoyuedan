package com.yunbao.common.event;

/**
 * Created by Sky.L on 2020-09-28
 */
public class ShowOrHideLiveRoomFloatWindowEvent {
    private int showStatus;// 0 关闭 ；1显示； 2隐藏

    public ShowOrHideLiveRoomFloatWindowEvent(int showStatus) {
        this.showStatus = showStatus;
    }

    public int getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(int showStatus) {
        this.showStatus = showStatus;
    }
}
