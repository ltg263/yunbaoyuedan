package com.yunbao.im.bean;


import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.bean.UserBean;

public class OrderTipBean {
    public static final int SOON_START=0;
    public static final int START=1;
    public static final int START_NOW=2;
    public static final int SOON_END=3;


   // 0：订单即将开始（提前十分钟提示）；1：订单开始；2：点击同意，订单开始 ；3：订单即将结束通知
    private int action;
    private String tip_title;
    private String tip_des;
    private String tip_des2;
    private String uid;
    private String liveuid;
    private String skillid;
    private String orderid;//订单id

    private UserBean mUserBean;
    private SkillBean mSkillBean;

    public int getAction() {
        return action;
    }


    public void setAction(int action) {
        this.action = action;
    }
    public String getTip_title() {
        return tip_title;
    }
    public void setTip_title(String tip_title) {
        this.tip_title = tip_title;
    }
    public String getTip_des() {
        return tip_des;
    }
    public void setTip_des(String tip_des) {
        this.tip_des = tip_des;
    }
    public String getTip_des2() {
        return tip_des2;
    }
    public void setTip_des2(String tip_des2) {
        this.tip_des2 = tip_des2;
    }

    public UserBean getUserBean() {
        return mUserBean;
    }

    public void setUserBean(UserBean userBean) {
        mUserBean = userBean;
    }
    public SkillBean getSkillBean() {
        return mSkillBean;
    }

    public void setSkillBean(SkillBean skillBean) {
        mSkillBean = skillBean;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLiveuid() {
        return liveuid;
    }

    public void setLiveuid(String liveuid) {
        this.liveuid = liveuid;
    }

    public String getSkillid() {
        return skillid;
    }

    public void setSkillid(String skillid) {
        this.skillid = skillid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }
}
