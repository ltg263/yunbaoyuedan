package com.yunbao.im.bean;

import com.yunbao.common.bean.LiveBean;

public class IMLiveBean extends LiveBean implements com.chad.library.adapter.base.entity.MultiItemEntity {
    public static final int TYPE_LIVE = 0;//系统消息，聊天室消息
    public static final int TYPE_AUTH_NOTICE = 1;//系统消息 认证消息通知

    private String time;
    private int tip_type;//系统消息 类型
    private String tip_des;
    private String tip_title;
    private int action;


    @Override
    public int getItemType() {
        return tip_type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTip_type() {
        return tip_type;
    }

    public void setTip_type(int tip_type) {
        this.tip_type = tip_type;
    }

    public String getTip_des() {
        return tip_des;
    }

    public void setTip_des(String tip_des) {
        this.tip_des = tip_des;
    }

    public String getTip_title() {
        return tip_title;
    }

    public void setTip_title(String tip_title) {
        this.tip_title = tip_title;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
