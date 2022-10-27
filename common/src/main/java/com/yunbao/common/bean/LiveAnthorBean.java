package com.yunbao.common.bean;

import android.support.annotation.Nullable;

import com.yunbao.common.utils.StringUtil;

public class LiveAnthorBean {
    private boolean isBoss;     //是否是老板？
    private UserBean userBean;
    private boolean isOpenWheat;//是否允许开麦
    private boolean currentSpeak; //当前的说话状态 开麦 or 闭麦？

    public boolean isBoss() {
        return isBoss;
    }
    public void setBoss(boolean boss) {
        isBoss = boss;
    }
    public UserBean getUserBean() {
        return userBean;
    }
    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj==null){
            return false;
        }
        if(obj instanceof LiveAnthorBean){
           LiveAnthorBean liveAnthorBean= (LiveAnthorBean) obj;
            if(userBean!=null&&liveAnthorBean.userBean!=null){
             return StringUtil.equals(userBean.getId(),liveAnthorBean.userBean.getId());
            }

        }else if(obj instanceof UserBean){
            UserBean tempUser= (UserBean) obj;
            boolean isEqual=(userBean==null||tempUser==null)?false:StringUtil.equals(userBean.getId(),tempUser.getId());
            return isEqual;
        }

        return super.equals(obj);
    }

    public void toggle(){
        isOpenWheat=!isOpenWheat;
    }


    public boolean isCurrentSpeak() {
        return currentSpeak;
    }

    public void setCurrentSpeak(boolean currentSpeak) {
        this.currentSpeak = currentSpeak;
    }

    public boolean isOpenWheat() {
        return isOpenWheat;
    }

    public void setOpenWheat(boolean openWheat) {
        isOpenWheat = openWheat;
    }
}
