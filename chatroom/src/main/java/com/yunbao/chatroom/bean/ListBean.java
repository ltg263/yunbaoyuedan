package com.yunbao.chatroom.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;
import com.yunbao.common.utils.StringUtil;

public class ListBean implements MultiItemEntity {
    public static final int TOP_TYPE=1;
    public static final int NORMAL_TYPE=2;


    @SerializedName( "total")
    private String totalCoin;


    @SerializedName( "user_nickname")
    private String userNiceName;

    @SerializedName( "avatar_thumb")
    private String avatarThumb;
    private int sex;

    @SerializedName( "level_anchor")
    private int levelAnchor;
    @SerializedName( "level")
    private int level;

//    @SerializedName( "isAttention")
//    private int attention;

    private int age;
    private int position;

    private boolean mEmpty;


    public String getTotalCoin() {
        return totalCoin;
    }

    public void setTotalCoin(String totalCoin) {
        this.totalCoin = totalCoin;
    }


    public boolean isEmpty() {
        return mEmpty;
    }

    public void setEmpty(boolean empty) {
        mEmpty = empty;
    }

    public String getUserNiceName() {
        return userNiceName;
    }

    public void setUserNiceName(String userNiceName) {
        this.userNiceName = userNiceName;
    }

    public String getAvatarThumb() {
        return avatarThumb;
    }

    public void setAvatarThumb(String avatarThumb) {
        this.avatarThumb = avatarThumb;
    }

    public int getAnchorLevel() {
        return levelAnchor;
    }

    public void setLevelAnchor(int levelAnchor) {
        this.levelAnchor = levelAnchor;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


//    public int getAttention() {
//        return attention;
//    }
//
//    public void setAttention(int attention) {
//        this.attention = attention;
//    }

    public String getTotalCoinFormat(String unit) {
        return StringUtil.contact(totalCoin,unit);
       // return StringUtil.contact(StringUtil.toWan(this.totalCoin),unit);
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    @Override
    public int getItemType() {
        if(position==0||position==1||position==2){
            return TOP_TYPE;
        }
        return NORMAL_TYPE;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
