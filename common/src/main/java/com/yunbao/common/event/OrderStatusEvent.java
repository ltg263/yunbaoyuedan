package com.yunbao.common.event;

public class OrderStatusEvent {
    private int status;
    private String orderId;

    public OrderStatusEvent(int status, String orderId) {
        this.status = status;
        this.orderId = orderId;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
