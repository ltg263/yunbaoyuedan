package com.yunbao.main.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Sky.L on 2020-12-21
 */
public class CountryCodeBean {
    private String name;
    private String mNameEn;
    private String tel;
    private boolean mIsTitle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JSONField(name = "name_en")
    public String getNameEn() {
        return mNameEn;
    }

    @JSONField(name = "name_en")
    public void setNameEn(String nameEn) {
        mNameEn = nameEn;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public boolean isTitle() {
        return mIsTitle;
    }

    public void setTitle(boolean title) {
        mIsTitle = title;
    }
}
