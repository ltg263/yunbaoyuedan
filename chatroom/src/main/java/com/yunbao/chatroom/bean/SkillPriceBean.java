package com.yunbao.chatroom.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2019/4/17.
 */

public class SkillPriceBean {

    private String mId;
    private String mCoin;
    private int mCanUse;


    public SkillPriceBean() {
    }

    @JSONField(name = "id")
    public String getId() {
        return mId;
    }

    @JSONField(name = "id")
    public void setId(String id) {
        mId = id;
    }

    @JSONField(name = "coin")
    public String getCoin() {
        return mCoin;
    }

    @JSONField(name = "coin")
    public void setCoin(String coin) {
        mCoin = coin;
    }


    @JSONField(name = "canselect")
    public int getCanUse() {
        return mCanUse;
    }

    @JSONField(name = "canselect")
    public void setCanUse(int canUse) {
        mCanUse = canUse;
    }

    public boolean isCanUse() {
        return this.mCanUse == 1;
    }
}
