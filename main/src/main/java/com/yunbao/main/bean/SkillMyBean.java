package com.yunbao.main.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.yunbao.common.bean.SkillBean;

/**
 * Created by cxf on 2019/7/26.
 */

public class SkillMyBean extends SkillBean {

    public static final int ADD = 1;
    private int mType;
    private SkillClassBean mSkillClass;


    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    @JSONField(name = "skill")
    public SkillClassBean getSkillClass() {
        return mSkillClass;
    }

    @JSONField(name = "skill")
    public void setSkillClass(SkillClassBean skillClass) {
        mSkillClass = skillClass;
    }
}
