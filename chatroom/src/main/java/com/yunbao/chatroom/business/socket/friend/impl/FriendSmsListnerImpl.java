package com.yunbao.chatroom.business.socket.friend.impl;

import com.yunbao.common.bean.LiveChatBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.bean.MakePairBean;
import com.yunbao.chatroom.business.socket.base.BaseSocketMessageLisnerImpl;
import com.yunbao.chatroom.business.socket.base.callback.BaseSocketMessageListner;
import com.yunbao.chatroom.business.socket.base.callback.WheatControllListner;
import com.yunbao.chatroom.business.socket.friend.callback.FriendStateListner;
import com.yunbao.chatroom.business.socket.gossip.callback.GossipWheatLisnter;

import java.util.List;

public class FriendSmsListnerImpl extends BaseSocketMessageLisnerImpl implements FriendStateListner
, WheatControllListner, GossipWheatLisnter

{
    private FriendStateListner mFriendStateListner ;
    private WheatControllListner mWheatControllListner;
    private GossipWheatLisnter mGossipWheatLisnter;

    public FriendSmsListnerImpl(BaseSocketMessageListner baseSocketMessageListner) {
        super(baseSocketMessageListner);
    }

    @Override
    public void changeState(int state) {
     if(mFriendStateListner!=null){
        mFriendStateListner.changeState(state);
     }
    }

    @Override
    public void heartBeatResult(List<LiveChatBean> chatArray, List<MakePairBean> makePairBeanList) {
        if(mFriendStateListner!=null){
            mFriendStateListner.heartBeatResult(chatArray,makePairBeanList);
        }
    }

    public void setWheatControllListner(WheatControllListner wheatControllListner) {
        mWheatControllListner = wheatControllListner;
    }

    public void setGossipWheatLisnter(GossipWheatLisnter gossipWheatLisnter) {
        mGossipWheatLisnter = gossipWheatLisnter;
    }

    public void setFriendStateListner(FriendStateListner friendStateListner) {
        mFriendStateListner = friendStateListner;
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
    public void clear() {
        super.clear();
        mFriendStateListner=null;
        mWheatControllListner=null;
        mGossipWheatLisnter=null;
    }
}
