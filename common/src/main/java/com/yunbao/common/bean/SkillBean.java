package com.yunbao.common.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.utils.StringUtil;

/**
 * Created by cxf on 2019/8/1.
 */

public class SkillBean implements Parcelable, ExportNamer {

    @SerializedName( "id")
    private String mId;

    @SerializedName( "uid")
    private String mUid;

    @SerializedName( "skillid")
    private String mSkillId;

    @SerializedName( "skillname")
    private String mSkillName;

    @SerializedName( "name")
    private String mSkillName2;//服务端有时候用 skillname  有时候用 name,不统一，没办法

    @SerializedName( "thumb")
    private String mSkillThumb;

    @SerializedName( "level")
    private String mSkillLevel;//游戏等级

    @SerializedName( "stars")
    private String mStarLevel;//星级

    @SerializedName("star_level")
    private int mStarCount;//星数

    @SerializedName( "coin")
    private String mSkillPrice;//价格


    @SerializedName( "voice")
    private String mSkillVoice;


    @SerializedName( "voice_l")
    private int mSkillVoiceDuration;

    @SerializedName( "orders")
    private String mOrderNum;//接单量


    @SerializedName( "method")
    private String mUnit;//游戏单位

    @SerializedName( "des")
    private String mDes;

    @SerializedName( "label_a")
    private String[] mLabels;

    @SerializedName( "switch")
    private int mIsOpen;

    @SerializedName("auth_thumb")
    private String authThumb;


    private String mPirceResult;
    private int mPirceValue = -1;

    private String skill_thumb;

    private String mSelected;

    public SkillBean() {
    }

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

    @JSONField(name = "skillid")
    public String getSkillId() {
        if (!TextUtils.isEmpty(mSkillId)) {
            return mSkillId;
        }
        return mId;
    }


    public String getAuthThumb() {
        if(TextUtils.isEmpty(authThumb)){
            return mSkillThumb;
        }
        return authThumb;
    }

    public void setAuthThumb(String authThumb) {
        this.authThumb = authThumb;
    }

    @JSONField(name = "skillid")
    public void setSkillId(String gameId) {
        mSkillId = gameId;
    }

    @JSONField(name = "skillname")
    public String getSkillName() {
        if (!TextUtils.isEmpty(mSkillName2)) {
            return mSkillName2;
        }
        return mSkillName;
    }

    @JSONField(name = "skillname")
    public void setSkillName(String skillName) {
        mSkillName = skillName;
    }

    @JSONField(name = "name")
    public String getSkillName2() {
        return mSkillName2;
    }

    @JSONField(name = "name")
    public void setSkillName2(String skillName2) {
        mSkillName2 = skillName2;
    }

    @JSONField(name = "thumb")
    public String getSkillThumb() {
        return mSkillThumb;
    }

    @JSONField(name = "thumb")
    public void setSkillThumb(String skillThumb) {
        mSkillThumb = skillThumb;
    }

    @JSONField(name = "level")
    public String getSkillLevel() {
        return mSkillLevel;
    }

    @JSONField(name = "level")
    public void setSkillLevel(String gameLevel) {
        mSkillLevel = gameLevel;
    }

    @JSONField(name = "stars")
    public String getStarLevel() {
        return mStarLevel;
    }

    @JSONField(name = "stars")
    public void setStarLevel(String starLevel) {
        mStarLevel = starLevel;
    }

    @JSONField(name = "star_level")
    public int getStarCount() {
        return mStarCount;
    }

    @JSONField(name = "star_level")
    public void setStarCount(int starCount) {
        mStarCount = starCount;
    }

    @JSONField(name = "coin")
    public String getSkillPrice() {
        return mSkillPrice;
    }

    @JSONField(name = "coin")
    public void setSkillPrice(String price) {
        mSkillPrice = price;
    }

    @JSONField(name = "voice")
    public String getSkillVoice() {
        return mSkillVoice;
    }

    @JSONField(name = "voice")
    public void setSkillVoice(String skillVoice) {
        mSkillVoice = skillVoice;
    }

    @JSONField(name = "voice_l")
    public int getSkillVoiceDuration() {
        return mSkillVoiceDuration;
    }

    @JSONField(name = "voice_l")
    public void setSkillVoiceDuration(int voiceDuration) {
        mSkillVoiceDuration = voiceDuration;
    }

    @JSONField(name = "orders")
    public String getOrderNum() {
        if(TextUtils.isEmpty(mOrderNum)){
            return "0";
        }
        return mOrderNum;
    }

    @JSONField(name = "orders")
    public void setOrderNum(String orderNum) {
        mOrderNum = orderNum;
    }

    @JSONField(name = "method")
    public String getUnit() {
        return mUnit;
    }

    @JSONField(name = "method")
    public void setUnit(String unit) {
        mUnit = unit;
    }

    @JSONField(name = "des")
    public String getDes() {
        return mDes;
    }

    @JSONField(name = "des")
    public void setDes(String des) {
        mDes = des;
    }

    @JSONField(name = "label_a")
    public String[] getLabels() {
        return mLabels;
    }

    @JSONField(name = "label_a")
    public void setLabels(String[] tags) {
        mLabels = tags;
    }

    @JSONField(name = "switch")
    public int getIsOpen() {
        return mIsOpen;
    }

    @JSONField(name = "switch")
    public void setIsOpen(int isOpen) {
        mIsOpen = isOpen;
    }

    public String getSelected() {
        return mSelected;
    }

    public boolean isSelected(){
        return "1".equals(mSelected);
    }

    public void setSelected(String selected) {
        mSelected = selected;
    }

    public String getSkill_thumb() {
        return skill_thumb;
    }

    public void setSkill_thumb(String skill_thumb) {
        this.skill_thumb = skill_thumb;
    }

    public String getPirceResult(String coinName) {
        if (TextUtils.isEmpty(mPirceResult)) {
            mPirceResult = StringUtil.contact(mSkillPrice, coinName, "/", mUnit);
        }
        return mPirceResult;
    }


    public String getPirceResult() {
        if (TextUtils.isEmpty(mPirceResult)) {
            mPirceResult = StringUtil.contact(mSkillPrice, CommonAppConfig.getInstance().getCoinName(), "/", mUnit);
        }
        return mPirceResult;
    }


    public SkillBean(Parcel in) {
        mId = in.readString();
        mUid = in.readString();
        mSkillId = in.readString();
        mSkillName = in.readString();
        mSkillName2 = in.readString();
        mSkillThumb = in.readString();
        mSkillLevel = in.readString();
        mStarLevel = in.readString();
        mStarCount = in.readInt();
        mSkillPrice = in.readString();
        mSkillVoice = in.readString();
        mSkillVoiceDuration = in.readInt();
        mOrderNum = in.readString();
        mUnit = in.readString();
        mDes = in.readString();
        skill_thumb = in.readString();
        mLabels = in.createStringArray();
        authThumb=in.readString();
        mSelected=in.readString();
    }

    public static final Creator<SkillBean> CREATOR = new Creator<SkillBean>() {
        @Override
        public SkillBean createFromParcel(Parcel in) {
            return new SkillBean(in);
        }

        @Override
        public SkillBean[] newArray(int size) {
            return new SkillBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mUid);
        dest.writeString(mSkillId);
        dest.writeString(mSkillName);
        dest.writeString(mSkillName2);
        dest.writeString(mSkillThumb);
        dest.writeString(mSkillLevel);
        dest.writeString(mStarLevel);
        dest.writeInt(mStarCount);
        dest.writeString(mSkillPrice);
        dest.writeString(mSkillVoice);
        dest.writeInt(mSkillVoiceDuration);
        dest.writeString(mOrderNum);
        dest.writeString(mUnit);
        dest.writeString(mDes);
        dest.writeString(skill_thumb);
        dest.writeStringArray(mLabels);
        dest.writeString(authThumb);
        dest.writeString(mSelected);
    }

    public int getPriceVal() {
        if (mPirceValue == -1) {
            if (!TextUtils.isEmpty(mSkillPrice)) {
                String s = mSkillPrice;
                if (s.contains(".")) {
                    s = s.substring(0, mSkillPrice.indexOf("."));
                }
                try {
                    mPirceValue = Integer.parseInt(s);
                    return mPirceValue;
                } catch (Exception e) {
                    return 0;
                }
            } else {
                return 0;
            }
        } else {
            return mPirceValue;
        }
    }
    @Override
    public String exportName() {
        return mSkillName2;
    }

    @Override
    public String exportId() {
        return mId;
    }
}
