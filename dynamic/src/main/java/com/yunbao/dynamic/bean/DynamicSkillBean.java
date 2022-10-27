package com.yunbao.dynamic.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.yunbao.common.bean.SkillBean;

public class DynamicSkillBean extends SkillBean  {
    @SerializedName("auth_thumb")
    private String authThumb;

    public DynamicSkillBean(Parcel in){
        super(in);
        authThumb=in.readString();
    }

    public void writeToParcel(Parcel dest, int flags){
        super.writeToParcel(dest,flags);
        dest.writeString(authThumb);
    }

    public String getAuthThumb() {
        return authThumb;
    }

    @Override
    public void setAuthThumb(String authThumb) {
        this.authThumb = authThumb;
    }
}
