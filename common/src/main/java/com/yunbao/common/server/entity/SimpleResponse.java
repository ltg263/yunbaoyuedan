package com.yunbao.common.server.entity;

import java.util.List;

public class SimpleResponse {
    private int ret;
    private String msg;
    private SimpleData data;

    public BaseResponse toBaseResponse() {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setRet(ret);
        baseResponse.setMsg(msg);
        Data newData=new Data();
        newData.setCode(data.getCode());
        newData.setMsg(data.getMsg());
        baseResponse.setData(newData);
        return baseResponse;
    }

    public BaseResponse toBaseResponse(List list) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setRet(ret);
        baseResponse.setMsg(msg);
        Data currentData=new Data();
        data.setCode(data.getCode());
        data.setMsg(data.getMsg());
        currentData.setInfo(list);
        baseResponse.setData(currentData);
        return baseResponse;
    }


    public SimpleData getData() {
        return data;
    }

    public void setData(SimpleData data) {
        this.data = data;
    }

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
}
