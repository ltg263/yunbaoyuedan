package com.yunbao.main.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2019/4/17.
 */

public class SkillPriceTipBean {

    private String mSkillPrice;
    private String mOrderNum;

    @JSONField(name = "coin")
    public String getSkillPrice() {
        return mSkillPrice;
    }

    @JSONField(name = "coin")
    public void setSkillPrice(String skillPrice) {
        mSkillPrice = skillPrice;
    }

    @JSONField(name = "orders")
    public String getOrderNum() {
        return mOrderNum;
    }

    @JSONField(name = "orders")
    public void setOrderNum(String orderNum) {
        mOrderNum = orderNum;
    }
}
