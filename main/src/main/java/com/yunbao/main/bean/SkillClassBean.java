package com.yunbao.main.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2019/7/19.
 */

public class SkillClassBean implements Parcelable{

    private String mId;
    private String mThumb;
    private String mName;
    private String mUnit;
    private boolean mMore;
    private int mStatus;

    public SkillClassBean() {
    }

    public SkillClassBean(boolean more) {
        mMore = more;
    }

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

    @JSONField(name = "method")
    public String getUnit() {
        return mUnit;
    }

    @JSONField(name = "method")
    public void setUnit(String unit) {
        mUnit = unit;
    }

    @JSONField(name = "status")
    public int getStatus() {
        return mStatus;
    }
    @JSONField(name = "status")
    public void setStatus(int status) {
        mStatus = status;
    }

    @JSONField(serialize = false)
    public boolean isMore() {
        return mMore;
    }
    @JSONField(serialize = false)
    public void setMore(boolean more) {
        mMore = more;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mThumb);
        dest.writeString(mName);
        dest.writeString(mUnit);
    }

    public SkillClassBean(Parcel in) {
        mId = in.readString();
        mThumb = in.readString();
        mName = in.readString();
        mUnit = in.readString();
    }

    public static final Creator<SkillClassBean> CREATOR = new Creator<SkillClassBean>() {
        @Override
        public SkillClassBean createFromParcel(Parcel in) {
            return new SkillClassBean(in);
        }

        @Override
        public SkillClassBean[] newArray(int size) {
            return new SkillClassBean[size];
        }
    };

}
