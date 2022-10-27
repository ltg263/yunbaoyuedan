package com.yunbao.common.bean;

import com.alibaba.android.arouter.utils.MapUtils;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2017/8/22.
 */

public class LiveChatBean implements com.chad.library.adapter.base.entity.MultiItemEntity {

    public static final int NORMAL = 0;
    public static final int SYSTEM = 1;
    public static final int GIFT = 2;
    public static final int ENTER_ROOM = 3;
    public static final int LIGHT = 4;
    public static final int RED_PACK = 5;
    public static final int FRIEND = 6;
    public static final int BOSS_PLACE_ORDER = 7;//老板 给某一用户下单成功的消息
    private String id;
    private String userNiceName;
    private int level;
    private int anchorLevel;
    private int showAnchorLevel;
    private String content;
    private int heart;
    private int type; //0是普通消息  1是系统消息 2是礼物消息
    private String liangName;
    private int vipType;
    private int guardType;
    private boolean anchor;
    private boolean manager;
    private int sex;
    private String toUserNiceName;
    private String age;
    private String avatar;




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JSONField(name = "user_nicename")
    public String getUserNiceName() {
        return userNiceName;
    }

    @JSONField(name = "user_nicename")
    public void setUserNiceName(String userNiceName) {
        this.userNiceName = userNiceName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getHeart() {
        return heart;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @JSONField(name = "liangname")
    public String getLiangName() {
        return liangName;
    }

    @JSONField(name = "liangname")
    public void setLiangName(String liangName) {
        if(!"0".equals(liangName)){
            this.liangName = liangName;
        }
    }

    public boolean isAnchor() {
        return anchor;
    }

    public void setAnchor(boolean anchor) {
        this.anchor = anchor;
    }

    @JSONField(name = "vip_type")
    public int getVipType() {
        return vipType;
    }

    @JSONField(name = "vip_type")
    public void setVipType(int vipType) {
        this.vipType = vipType;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    @JSONField(name = "guard_type")
    public int getGuardType() {
        return guardType;
    }

    @JSONField(name = "guard_type")
    public void setGuardType(int guardType) {
        this.guardType = guardType;
    }

    @Override
    public int getItemType() {
        return type;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getToUserNiceName() {
        return toUserNiceName;
    }

    public void setToUserNiceName(String toUserNiceName) {
        this.toUserNiceName = toUserNiceName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getAnchorLevel() {
        return anchorLevel;
    }

    public void setAnchorLevel(int anchorLevel) {
        this.anchorLevel = anchorLevel;
    }

    public int getShowAnchorLevel() {
        return showAnchorLevel;
    }

    public void setShowAnchorLevel(int showAnchorLevel) {
        this.showAnchorLevel = showAnchorLevel;
    }

    public boolean isShowAnchorLevel(){
        return showAnchorLevel == 1;
    }

    public UserBean convert(){
        UserBean userBean=new UserBean();
        userBean.setId(id);
        userBean.setUserNiceName(userNiceName);
        userBean.setSex(sex);
        userBean.setAge(age);
        userBean.setAvatar(avatar);
        userBean.setLevel(level);
        userBean.setAnchorLevel(anchorLevel);
        userBean.setShowAnchorLevel(showAnchorLevel);
        return userBean;
    }
}
