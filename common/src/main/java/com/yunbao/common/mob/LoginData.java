package com.yunbao.common.mob;

/**
 * Created by cxf on 2018/9/21.
 */

public class LoginData {

    private String mType;
    private String mOpenID;
    private String mNickName;
    private String mAvatar;
    private int mFlag; //第三方标识,0PC，1QQ，2微信，3新浪，4facebook，5twitter
    private String mAccessToken;

    public LoginData() {

    }

    public LoginData(String type, String openID, String accessToken,String nickName, String avatar,int flag) {
        mType = type;
        mOpenID = openID;
        mNickName = nickName;
        mAvatar = avatar;
        mFlag =flag;
        mAccessToken = accessToken;

    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getOpenID() {
        return mOpenID;
    }

    public void setOpenID(String openID) {
        mOpenID = openID;
    }

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String nickName) {
        mNickName = nickName;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public int getFlag() {
        return mFlag;
    }

    public void setFlag(int flag) {
        mFlag = flag;
    }
    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }
}
