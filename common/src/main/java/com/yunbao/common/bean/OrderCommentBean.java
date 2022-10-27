package com.yunbao.common.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.yunbao.common.bean.UserBean;

/**
 * Created by cxf on 2019/8/7.
 */

public class OrderCommentBean {
    @SerializedName("id")
    private String mId;
    @SerializedName("uid")
    private String mUid;
    @SerializedName("liveuid")
    private String mLiveUid;
    @SerializedName("skillid")
    private String mSkillId;
    @SerializedName("orderid")
    private String mOrderId;
    @SerializedName("content")
    private String mContent;
    @SerializedName("star")
    private int mStar;

    private String mLabelString;
    @SerializedName("addtime")
    private long mAddTime;
    private String mAddTimeString;
    @SerializedName("label_a")
    private String[] mLables;
    @SerializedName("userinfo")
    private UserBean mUserBean;

    @JSONField(name = "id")
    public String getId() {
        return mId;
    }

    @JSONField(name = "id")
    public void setId(String id) {
        mId = id;
    }

    @JSONField(name = "uid")
    public String getUid() {
        return mUid;
    }

    @JSONField(name = "uid")
    public void setUid(String uid) {
        mUid = uid;
    }

    @JSONField(name = "liveuid")
    public String getLiveUid() {
        return mLiveUid;
    }

    @JSONField(name = "liveuid")
    public void setLiveUid(String liveUid) {
        mLiveUid = liveUid;
    }

    @JSONField(name = "skillid")
    public String getSkillId() {
        return mSkillId;
    }

    @JSONField(name = "skillid")
    public void setSkillId(String skillId) {
        mSkillId = skillId;
    }

    @JSONField(name = "orderid")
    public String getOrderId() {
        return mOrderId;
    }

    @JSONField(name = "orderid")
    public void setOrderId(String orderId) {
        mOrderId = orderId;
    }

    @JSONField(name = "content")
    public String getContent() {
        return mContent;
    }

    @JSONField(name = "content")
    public void setContent(String content) {
        mContent = content;
    }

    @JSONField(name = "star")
    public int getStar() {
        return mStar;
    }

    @JSONField(name = "star")
    public void setStar(int star) {
        mStar = star;
    }

    @JSONField(serialize = false)
    public String getLabelString() {
        if (TextUtils.isEmpty(mLabelString)) {
            if (mLables != null && mLables.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (String s : mLables) {
                    sb.append(s);
                    sb.append(" ");
                }
                mLabelString = sb.toString().trim();
            }
        }
        return mLabelString;
    }

    @JSONField(serialize = false)
    public void setLabelString(String labelString) {
        mLabelString = labelString;
    }

    @JSONField(name = "addtime")
    public long getAddTime() {
        return mAddTime;
    }

    @JSONField(name = "addtime")
    public void setAddTime(long addTime) {
        mAddTime = addTime;
    }

    @JSONField(name = "add_time")
    public String getAddTimeString() {
        return mAddTimeString;
    }

    @JSONField(name = "add_time")
    public void setAddTimeString(String addTimeString) {
        mAddTimeString = addTimeString;
    }

    @JSONField(name = "label_a")
    public String[] getLables() {
        return mLables;
    }

    @JSONField(name = "label_a")
    public void setLables(String[] lables) {
        mLables = lables;
    }

    @JSONField(name = "userinfo")
    public UserBean getUserBean() {
        return mUserBean;
    }

    @JSONField(name = "userinfo")
    public void setUserBean(UserBean userBean) {
        mUserBean = userBean;
    }
}
