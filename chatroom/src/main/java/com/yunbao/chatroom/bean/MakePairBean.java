package com.yunbao.chatroom.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class MakePairBean {
  @JSONField(name = "man_user_nickname")
  private String manUserNickname;
  @JSONField(name = "man_avatar")
  private String manAvatar;
    @JSONField(name = "man_id")
    private String manUid;


    @JSONField(name = "woman_user_nickname")
    private String womanUserNickname;
    @JSONField(name = "woman_avatar")
    private String womanAvatar;
    @JSONField(name = "woman_id")
    private String womanUid;


    public String getManUserNickname() {
        return manUserNickname;
    }

    public void setManUserNickname(String manUserNickname) {
        this.manUserNickname = manUserNickname;
    }

    public String getManAvatar() {
        return manAvatar;
    }

    public void setManAvatar(String manAvatar) {
        this.manAvatar = manAvatar;
    }

    public String getManUid() {
        return manUid;
    }

    public void setManUid(String manUid) {
        this.manUid = manUid;
    }

    public String getWomanUserNickname() {
        return womanUserNickname;
    }

    public void setWomanUserNickname(String womanUserNickname) {
        this.womanUserNickname = womanUserNickname;
    }

    public String getWomanAvatar() {
        return womanAvatar;
    }

    public void setWomanAvatar(String womanAvatar) {
        this.womanAvatar = womanAvatar;
    }

    public String getWomanUid() {
        return womanUid;
    }

    public void setWomanUid(String womanUid) {
        this.womanUid = womanUid;
    }
}
