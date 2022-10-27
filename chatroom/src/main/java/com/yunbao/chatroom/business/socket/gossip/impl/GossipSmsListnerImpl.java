package com.yunbao.chatroom.business.socket.gossip.impl;

import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.business.socket.base.BaseSocketMessageLisnerImpl;
import com.yunbao.chatroom.business.socket.base.callback.BaseSocketMessageListner;
import com.yunbao.chatroom.business.socket.base.callback.WheatControllListner;
import com.yunbao.chatroom.business.socket.gossip.callback.GossipWheatLisnter;

public class GossipSmsListnerImpl extends BaseSocketMessageLisnerImpl implements GossipWheatLisnter,WheatControllListner {
    private GossipWheatLisnter mGossipWheatLisnter;
    private WheatControllListner mWheatControllListner;

    public GossipSmsListnerImpl(BaseSocketMessageListner baseSocketMessageListner) {
        super(baseSocketMessageListner);
    }

    public void setGossipWheatLisnter(GossipWheatLisnter gossipWheatLisnter) {
        mGossipWheatLisnter = gossipWheatLisnter;
    }

    public void setWheatControllListner(WheatControllListner wheatControllListner) {
        mWheatControllListner = wheatControllListner;
    }

    @Override
    public void applyWheat(String uid, boolean isUp) {
        if(mGossipWheatLisnter!=null){
            mGossipWheatLisnter.applyWheat(uid,isUp);
        }
    }

    @Override
    public void argreeUpWheat(UserBean userBean, int position) {
        if(mGossipWheatLisnter!=null){
           mGossipWheatLisnter.argreeUpWheat(userBean,position);
        }
    }

    @Override
    public void refuseUpWheat(UserBean userBean) {
        if(mGossipWheatLisnter!=null){
            mGossipWheatLisnter.refuseUpWheat(userBean);
        }
    }

    @Override
    public int downWheat(UserBean userBean, boolean isSelf) {
        if(mGossipWheatLisnter!=null){
         return  mGossipWheatLisnter.downWheat(userBean,isSelf);
        }
        return 0;
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
        mGossipWheatLisnter=null;
        mWheatControllListner=null;
    }
}
