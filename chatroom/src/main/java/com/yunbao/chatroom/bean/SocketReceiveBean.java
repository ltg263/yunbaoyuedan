package com.yunbao.chatroom.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by cxf on 2017/8/22.
 * 接收socket的实体类
 */

public class SocketReceiveBean {
    private String retcode;
    private String retmsg;
    private JSONArray msg;
    private JSONObject msgData;

    public String getRetcode() {
        return retcode;
    }

    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    public String getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    public JSONArray getMsg() {
        return msg;
    }

    public void setMsg(JSONArray msg) {
        this.msg = msg;
        msgData=msg.getJSONObject(0);
    }

    public JSONObject getMsgData(){
        return  msg.getJSONObject(0);
    }

    public String method(){
        return getMsgData().getString("_method_");
    }

}
