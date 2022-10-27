package com.yunbao.chatroom.business.socket.gossip.callback;
import com.yunbao.common.bean.UserBean;

public interface GossipWheatLisnter {
    /*申请申请或者取消上麦申请*/
    public void applyWheat(String uid,boolean isUp);
    /*同意上麦*/
    public void argreeUpWheat(UserBean userBean,int position);
    /*拒绝上麦*/
    public void refuseUpWheat(UserBean userBean);
    /*是否是本人意愿下麦*/
    public int downWheat(UserBean userBean,boolean isSelf);

}
