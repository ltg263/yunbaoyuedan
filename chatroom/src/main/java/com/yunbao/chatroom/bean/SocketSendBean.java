package com.yunbao.chatroom.bean;

import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cxf on 2017/8/22.
 * 发送socket的实体类
 */

public class SocketSendBean {

    private JSONObject mResult;
    private JSONArray mMsg;
    private JSONObject mMsg0;

    public SocketSendBean() {
        mResult = new JSONObject();
        mMsg = new JSONArray();
        mMsg0 = new JSONObject();
    }

    public SocketSendBean param(String key, String value) {
        try {
            mMsg0.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public SocketSendBean param(UserBean userBean) {
        try {
            mMsg0.put("uid", userBean.getId());
            mMsg0.put("avatar", userBean.getAvatar());
            mMsg0.put("uname", userBean.getUserNiceName());
            mMsg0.put("sex", userBean.getSex());
            mMsg0.put("level", userBean.getLevel());
            mMsg0.put("level_anchor", userBean.getAnchorLevel());
            mMsg0.put("age", userBean.getAge());
            mMsg0.put("uhead", userBean.getAvatar());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    
    public SocketSendBean paramToUser(UserBean userBean) {
        try {
            mMsg0.put("touid", userBean.getId());
            mMsg0.put("toavatar", userBean.getAvatar());
            mMsg0.put("toname", userBean.getUserNiceName());
            mMsg0.put("toavatar", userBean.getAvatar());
            mMsg0.put("tosex", userBean.getSex());
            mMsg0.put("tolevel", userBean.getLevel());
            mMsg0.put("tolevel_anchor", userBean.getAnchorLevel());
            mMsg0.put("toage", userBean.getAge());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static UserBean parseUserBean(com.alibaba.fastjson.JSONObject jsonObject){
        UserBean userBean=new UserBean();
        userBean.setUserNiceName(jsonObject.getString("uname"));
        userBean.setId(jsonObject.getString("uid"));
        userBean.setAvatar(jsonObject.getString("avatar"));
        userBean.setAge(jsonObject.getString("age"));
        userBean.setSex(jsonObject.getIntValue("sex"));
        userBean.setAnchorLevel(jsonObject.getIntValue("level_anchor"));
        userBean.setLevel(jsonObject.getIntValue("level"));
        return userBean;
    }

    public static UserBean parseToUserBean(com.alibaba.fastjson.JSONObject jsonObject){
        UserBean userBean=new UserBean();
        userBean.setUserNiceName(jsonObject.getString("toname"));
        userBean.setAvatar(jsonObject.getString("toavatar"));
        userBean.setId(jsonObject.getString("touid"));
        userBean.setSex(jsonObject.getIntValue("tosex"));
        userBean.setAge(jsonObject.getString("toage"));
        userBean.setAnchorLevel(jsonObject.getIntValue("tolevel_anchor"));
        userBean.setLevel(jsonObject.getIntValue("tolevel"));
        return userBean;
    }


    public static UserBean parseUserBean(JSONObject jsonObject){
        UserBean userBean=new UserBean();
        try {
            userBean.setUserNiceName(jsonObject.getString("uname"));
            userBean.setId(jsonObject.getString("id"));
            userBean.setAvatar(jsonObject.getString("avatar"));
            return userBean;
        }catch (Exception e){
            e.printStackTrace();
        }
       return userBean;
    }

    public SocketSendBean param(String key, int value) {
        return param(key, String.valueOf(value));
    }

    public SocketSendBean param(String key, JSONObject value) {
        try {
            mMsg0.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public SocketSendBean paramJsonObject(String key, String value) {
        try {
            mMsg0.put(key, new JSONObject(value));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    
    public JSONObject create() {
        try {
            mMsg.put(mMsg0);
            mResult.put("retcode", "000000");
            mResult.put("retmsg", "ok");
            mResult.put("msg", mMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        L.e("发送socket-->" + mResult.toString());
        return mResult;
    }
    
}
