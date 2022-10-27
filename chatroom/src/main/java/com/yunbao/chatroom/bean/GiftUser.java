package com.yunbao.chatroom.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;

public class GiftUser implements MultiItemEntity {
    public static final int GIFT_HEAD=1;
    public static final int GIFT_NORMAL=2;
    public static final String ALL_UID="all";

    private String avator;
    private boolean isCheck;
    private int selectBackGround;
    private int normalBackGround;
    private String uid;
    private String maiXu;



    public String getAvator() {
        return avator;
    }
    public void setAvator(String avator) {
        this.avator = avator;
    }
    public boolean isCheck() {
        return isCheck;
    }
    public void setCheck(boolean check) {
        isCheck = check;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getMaiXu() {
        return maiXu;
    }
    public void setMaiXu(String maiXu) {
        this.maiXu = maiXu;
    }
    @Override
    public int getItemType() {
        if(StringUtil.equals(uid,ALL_UID)){
            return GIFT_HEAD;
        }
        return GIFT_NORMAL;
    }
    public int getSelectBackGround() {
        return selectBackGround;
    }
    public void setSelectBackGround(int selectBackGround) {
        this.selectBackGround = selectBackGround;
    }
    public int getNormalBackGround() {
        return normalBackGround;
    }
    public void setNormalBackGround(int normalBackGround) {
        this.normalBackGround = normalBackGround;
    }



    public static GiftUser createHeadUser(){
        GiftUser giftUser=new GiftUser();
        giftUser.setUid(ALL_UID);
        giftUser.setMaiXu(WordUtil.getString(R.string.all_wheat));
        giftUser.setNormalBackGround(R.mipmap.icon_gift_reward_all_default);
        giftUser.setSelectBackGround(R.mipmap.icon_gift_reword_all_selected);
        return giftUser;
    }


    public static GiftUser createNormalUser(int index,UserBean userBean){
        GiftUser giftUser=new GiftUser();
        giftUser.setUid(userBean.getId());

        giftUser.setSelectBackGround(R.drawable.bound_aval_blue);
        giftUser.setAvator(userBean.getAvatar());
        switch (index){
            case 0:
                giftUser.setMaiXu(WordUtil.getString(R.string.host));
                break;
            case 1:
               giftUser.setMaiXu(WordUtil.getString(R.string.one_wheat));
                break;
            case 2:
                giftUser.setMaiXu(WordUtil.getString(R.string.two_wheat));
                break;
            case 3:
                giftUser.setMaiXu(WordUtil.getString(R.string.three_wheat));
                break;
            case 4:
                giftUser.setMaiXu(WordUtil.getString(R.string.four_wheat));
                break;
            case 5:
                giftUser.setMaiXu(WordUtil.getString(R.string.five_wheat));
                break;
            case 6:
                giftUser.setMaiXu(WordUtil.getString(R.string.six_wheat));
                break;
            case 7:
                giftUser.setMaiXu(WordUtil.getString(R.string.seven_wheat));
                break;
            case 8:
                giftUser.setMaiXu(WordUtil.getString(R.string.eight_wheat));
                break;
            default:
                break;
        }
        return giftUser;
    }
}
