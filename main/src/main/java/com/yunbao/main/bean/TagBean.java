package com.yunbao.main.bean;

import android.graphics.Color;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2018/10/15.
 * 标签实体类
 */

public class TagBean {

    private int mId;
    private String mName;
    private String mTextColorString;
    private String mBorderColorString;
    private String mBgColorString;
    private int mTextColor;
    private int mBorderColor;
    private int mBgColor;

    public TagBean() {
    }

    public TagBean(String name, String textColorString, String borderColorString) {
        mName = name;
        mTextColorString = textColorString;
        mBorderColorString = borderColorString;
    }

    public TagBean(String name, String textColorString, String borderColorString, String bgColorString) {
        mName = name;
        mTextColorString = textColorString;
        mBorderColorString = borderColorString;
        mBgColorString = bgColorString;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
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

    @JSONField(name = "colour_font")
    public String getTextColorString() {
        return mTextColorString;
    }

    @JSONField(name = "colour_font")
    public void setTextColorString(String textColorString) {
        mTextColorString = textColorString;
    }

    public String getBorderColorString() {
//        return mBorderColorString;
        return mTextColorString;
    }

    public void setBorderColorString(String borderColorString) {
        mBorderColorString = borderColorString;
    }

    @JSONField(name = "colour_bg")
    public String getBgColorString() {
        return mBgColorString;
    }

    @JSONField(name = "colour_bg")
    public void setBgColorString(String bgColorString) {
        mBgColorString = bgColorString;
    }


    public int getTextColor() {
        if (mTextColor == 0 && !TextUtils.isEmpty(mTextColorString)) {
            mTextColor = Color.parseColor(mTextColorString);
        }
        return mTextColor;
    }


    public int getBorderColor() {
//        if (mBorderColor == 0 && !TextUtils.isEmpty(mBorderColorString)) {
//            mBorderColor = Color.parseColor(mBorderColorString);
//        }
//        return mBorderColor;
        return getTextColor();
    }

    public int getBgColor() {
        if (mBgColor == 0 && !TextUtils.isEmpty(mBgColorString)) {
            mBgColor = Color.parseColor(mBgColorString);
        }
        return mBgColor;
    }
}
