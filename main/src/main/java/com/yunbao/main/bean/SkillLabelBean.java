package com.yunbao.main.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2019/7/26.
 */

public class SkillLabelBean {

    private String mId;
    private String mName;
    private boolean mChecked;

    public SkillLabelBean() {
    }

    public SkillLabelBean(String name) {
        mName = name;
    }

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

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
