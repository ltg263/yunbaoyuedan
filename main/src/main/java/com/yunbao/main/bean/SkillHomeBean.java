package com.yunbao.main.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.yunbao.common.bean.SkillBean;

/**
 * Created by cxf on 2019/7/31.
 */

public class SkillHomeBean extends SkillBean {

    private int mSex;

    @JSONField(name = "sex")
    public int getSex() {
        return mSex;
    }
    @JSONField(name = "sex")
    public void setSex(int sex) {
        mSex = sex;
    }

}
