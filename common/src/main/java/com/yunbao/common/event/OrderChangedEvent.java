package com.yunbao.common.event;

/**
 * Created by cxf on 2019/8/5.
 */

public class OrderChangedEvent {
    private String mOrderId;
    private int status=-10;

    public OrderChangedEvent(String orderId) {
        mOrderId = orderId;
    }

    public String getOrderId(){
        return mOrderId;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
