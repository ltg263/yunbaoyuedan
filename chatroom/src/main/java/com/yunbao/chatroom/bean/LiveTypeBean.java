package com.yunbao.chatroom.bean;

import com.google.gson.annotations.SerializedName;
import com.yunbao.common.adapter.radio.IRadioChecker;
public class LiveTypeBean implements IRadioChecker {
    @SerializedName("id")
    private int type;
    @SerializedName("name")
    private String content;

    public LiveTypeBean(){
    }

    public LiveTypeBean(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }
    @Override
    public String getId() {
        return Integer.toString(type);
    }
}
