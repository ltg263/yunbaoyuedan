package com.yunbao.main.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2019/3/30.
 */

public class BannerBean {
    private String id;
    private String mImageUrl;
    private String mLink;

    @JSONField(name = "image")
    public String getImageUrl() {
        return mImageUrl;
    }

    @JSONField(name = "image")
    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    @JSONField(name = "url")
    public String getLink() {
        return mLink;
    }

    @JSONField(name = "url")
    public void setLink(String link) {
        mLink = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEqual(BannerBean bean) {
        if (bean == null) {
            return false;
        }
        if (TextUtils.isEmpty(mImageUrl) || !mImageUrl.equals(bean.getImageUrl())) {
            return false;
        }
        if (TextUtils.isEmpty(mLink) || !mLink.equals(bean.getLink())) {
            return false;
        }
        return true;
    }
}
