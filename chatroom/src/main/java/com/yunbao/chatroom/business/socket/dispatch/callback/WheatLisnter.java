package com.yunbao.chatroom.business.socket.dispatch.callback;

import com.yunbao.common.bean.UserBean;
public interface WheatLisnter  {
    /*申请上老板麦*/
    public void applyBosssWheat(String uid,boolean isUp);
    /*同意上普通麦*/
    public void upNormalWheatSuccess(UserBean userBean,int sitId);
    /*同意上老板麦*/
    public void upBossWheatSuccess(UserBean userBean);
    /*拒绝上麦申请*/
    public void resfuseUpWheat(String uid);
    /*下麦*/
    public int downWheat(UserBean userBean,boolean isSelf);

}
