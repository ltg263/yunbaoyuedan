package com.yunbao.chatroom.business.socket.dispatch.impl;

import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.business.socket.base.BaseSocketMessageLisnerImpl;
import com.yunbao.chatroom.business.socket.base.callback.BaseSocketMessageListner;
import com.yunbao.chatroom.business.socket.base.callback.WheatControllListner;
import com.yunbao.chatroom.business.socket.dispatch.callback.OrderMessageListner;
import com.yunbao.chatroom.business.socket.dispatch.callback.WheatLisnter;
import com.yunbao.chatroom.business.socket.dispatch.callback.WheeledWheatListner;

/*派单实现类桥梁,你可以不必在一个类里面实现所有的接口方法,哪里需要set哪里*/
public class DispatchSmsListnerImpl extends BaseSocketMessageLisnerImpl implements OrderMessageListner, WheatLisnter, WheeledWheatListner, WheatControllListner {
    private OrderMessageListner mOrderMessageListner;
    private WheatLisnter mWheatLisnter;
    private WheeledWheatListner mWheeledWheatListner;
    private WheatControllListner mWheatControllListner;

    public DispatchSmsListnerImpl(BaseSocketMessageListner baseSocketMessageListner) {
        super(baseSocketMessageListner);
    }

    @Override
    public void onOrderUpDate(String skillid) {
        if(mOrderMessageListner!=null){
           mOrderMessageListner.onOrderUpDate(skillid);
        }
    }
    @Override
    public void applyBosssWheat(String uid, boolean isUp) {
        if(mWheatLisnter!=null){
            mWheatLisnter.applyBosssWheat(uid,isUp);
        }
    }
    @Override
    public void upNormalWheatSuccess(UserBean userBean, int positon) {
        if(mWheatLisnter!=null){
           mWheatLisnter.upNormalWheatSuccess(userBean,positon);
        }
    }
    @Override
    public void upBossWheatSuccess(UserBean userBean) {
        if(mWheatLisnter!=null){
            mWheatLisnter.upBossWheatSuccess(userBean);
        }
    }
    @Override
    public void resfuseUpWheat(String uid) {
        if(mWheatLisnter!=null){
            mWheatLisnter.resfuseUpWheat(uid);
        }
    }
    @Override
    public int downWheat(UserBean userBean, boolean isSelf) {
        if(mWheatLisnter!=null){
          return mWheatLisnter.downWheat(userBean,isSelf);
        }
        return -1;
    }
    @Override
    public void openWheeledWheat(boolean isOpen) {
        if(mWheeledWheatListner!=null){
           mWheeledWheatListner.openWheeledWheat(isOpen);
        }
    }
    @Override
    public void changeSpeakUser(UserBean userBean) {
        if(mWheeledWheatListner!=null){
            mWheeledWheatListner.changeSpeakUser(userBean);
        }
    }

    public void setOrderMessageListner(OrderMessageListner orderMessageListner) {
        mOrderMessageListner = orderMessageListner;
    }

    public void setWheatLisnter(WheatLisnter wheatLisnter) {
        mWheatLisnter = wheatLisnter;
    }

    public void setWheeledWheatListner(WheeledWheatListner wheeledWheatListner) {
        mWheeledWheatListner = wheeledWheatListner;
    }

    public void setWheatControllListner(WheatControllListner wheatControllListner) {
        mWheatControllListner = wheatControllListner;
    }

    @Override
    public void openSpeak(UserBean userBean, boolean isOpen) {
            if(mWheatControllListner!=null){
               mWheatControllListner.openSpeak(userBean,isOpen);
            }
    }

    @Override
    public void userAudioOpen(String uid, boolean isOpen) {
        if(mWheatControllListner!=null){
            mWheatControllListner.userAudioOpen(uid,isOpen);
        }
    }


    @Override
    public void clear() {
        super.clear();
        mWheatControllListner=null;
        mWheatLisnter=null;
        mOrderMessageListner=null;
        mWheeledWheatListner=null;
    }
}
