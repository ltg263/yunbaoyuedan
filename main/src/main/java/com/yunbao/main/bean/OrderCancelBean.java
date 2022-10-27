package com.yunbao.main.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2019/8/5.
 * 取消订单的理由
 */

public class OrderCancelBean {
    private String mId;
    private String mName;
    private boolean mChecked;

    @JSONField(name = "id")
    public String getId() {
        return mId;
    }

    @JSONField(name = "id")
    public void setId(String id) {
        mId = id;
    }

    @JSONField(name = "name")
    public String getName() {
        return mName;
    }

    @JSONField(name = "name")
    public void setName(String name) {
        mName = name;
    }

    @JSONField(serialize = false)
    public boolean isChecked() {
        return mChecked;
    }

    @JSONField(serialize = false)
    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public void toggle() {
        mChecked = !mChecked;
    }
}
