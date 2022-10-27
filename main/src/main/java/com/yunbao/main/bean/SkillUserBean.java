package com.yunbao.main.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.bean.UserBean;

/**
 * Created by cxf on 2019/7/29.
 */

public class SkillUserBean extends SkillBean {

    private UserBean mUserBean;

    @JSONField(name = "userinfo")
    public UserBean getUserBean() {
        return mUserBean;
    }

    @JSONField(name = "userinfo")
    public void setUserBean(UserBean userBean) {
        mUserBean = userBean;
    }

}
