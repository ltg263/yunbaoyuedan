package com.yunbao.im.event;

public class NowServiceEvent {
    private String orderId;
    private int receptStatus;

    public NowServiceEvent(String orderId, int receptStatus) {
        this.orderId = orderId;
        this.receptStatus = receptStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getReceptStatus() {
        return receptStatus;
    }

    public void setReceptStatus(int receptStatus) {
        this.receptStatus = receptStatus;
    }
}
