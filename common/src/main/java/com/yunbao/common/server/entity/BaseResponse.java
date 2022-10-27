package com.yunbao.common.server.entity;

public class BaseResponse<T> {
    private int ret;
    private String msg;
    private Data<T> data;

    public int getRet() {

        return ret;
    }
    public void setRet(int ret) {
        this.ret = ret;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Data<T> getData() {
        return data;
    }
    public void setData(Data data) {
        this.data = data;
    }
}
