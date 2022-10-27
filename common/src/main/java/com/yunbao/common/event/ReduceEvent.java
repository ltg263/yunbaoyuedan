package com.yunbao.common.event;

/**
 *
 * @author cxf
 * @date 2018/9/28
 */

public class ReduceEvent {
    private String mMsg;
    public ReduceEvent(String msg) {
        mMsg=msg;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }
}
