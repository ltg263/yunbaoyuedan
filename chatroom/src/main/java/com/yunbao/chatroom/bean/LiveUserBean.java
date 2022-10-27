package com.yunbao.chatroom.bean;

import com.google.gson.annotations.SerializedName;
import com.yunbao.common.bean.UserBean;

public class LiveUserBean extends UserBean {
    @SerializedName("sittype")
    private int status;

    public int getStatus() {
      return status;

    }

    public void setStatus(int status) {
        this.status = status;
    }
}
