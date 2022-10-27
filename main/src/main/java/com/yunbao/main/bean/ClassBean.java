package com.yunbao.main.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Sky.L on 2022-02-07
 */
public class ClassBean {
    private String mId;
    private String mThumb;
    private String mName;

    @JSONField(name = "id")
    public String getId() {
        return mId;
    }

    @JSONField(name = "id")
    public void setId(String id) {
        mId = id;
    }

    @JSONField(name = "thumb")
    public String getThumb() {
        return mThumb;
    }

    @JSONField(name = "thumb")
    public void setThumb(String thumb) {
        mThumb = thumb;
    }

    @JSONField(name = "name")
    public String getName() {
        return mName;
    }

    @JSONField(name = "name")
    public void setName(String name) {
        mName = name;
    }


}
