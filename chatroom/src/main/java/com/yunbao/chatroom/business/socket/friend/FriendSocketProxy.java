package com.yunbao.chatroom.business.socket.friend;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LiveInfo;
import com.yunbao.chatroom.business.socket.SocketProxy;
import com.yunbao.chatroom.business.socket.base.mannger.WheatControllerMannger;
import com.yunbao.chatroom.business.socket.friend.impl.FriendSmsListnerImpl;
import com.yunbao.chatroom.business.socket.friend.mannger.FriendStateMannger;
import com.yunbao.chatroom.business.socket.friend.mannger.FriendWheatMannger;

public class FriendSocketProxy extends SocketProxy<FriendSmsListnerImpl> {
    private FriendStateMannger mFriendStateMannger;
    private WheatControllerMannger mWheatControllerMannger;
    private FriendWheatMannger mFriendWheatMannger;

    public FriendSocketProxy(String url, FriendSmsListnerImpl socketMessageListner, LiveInfo liveInfo) {
        super(url, socketMessageListner, liveInfo);
    }

    @Override
    protected void handle(String method, JSONObject jsonObject) {
       switch (method){
           case Constants.SOCKET_FRIEND:
               getFriendStateMannger().handle(jsonObject);
               break;
           case Constants.SOCKET_CONTROLMIC:
               getWheatControllMannger().handle(jsonObject);
               break;
           case Constants.SOCKET_LINKMIC:
               getFriendWheatMannger().handle(jsonObject);
               break;
           case Constants.SOCKET_TALK:
               getWheatControllMannger().handle(jsonObject);
               break;



       }
    }

    public FriendWheatMannger getFriendWheatMannger() {
        if(mFriendWheatMannger==null){
            mFriendWheatMannger=new FriendWheatMannger(mILiveSocket,mSocketMessageListner);
        }
        return mFriendWheatMannger;
    }

    public WheatControllerMannger getWheatControllMannger() {
        if(mWheatControllerMannger==null){
            mWheatControllerMannger=new WheatControllerMannger(mILiveSocket,mSocketMessageListner) ;
        }
        return mWheatControllerMannger;
    }

    public FriendStateMannger getFriendStateMannger() {
        if(mFriendStateMannger==null){
           mFriendStateMannger =new FriendStateMannger(mILiveSocket,mSocketMessageListner);
        }
        return mFriendStateMannger;
    }

    @Override
    public void release() {
        super.release();
        if(mFriendStateMannger!=null){
            mFriendStateMannger.release();
            mFriendStateMannger=null;
        }

        if(mWheatControllerMannger!=null){
            mWheatControllerMannger.release();
            mWheatControllerMannger=null;
        }

        if(mFriendStateMannger!=null){
            mFriendStateMannger.release();
            mFriendStateMannger=null;
        }
    }
}
