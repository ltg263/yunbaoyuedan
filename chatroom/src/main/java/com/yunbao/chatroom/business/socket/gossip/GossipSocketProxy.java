package com.yunbao.chatroom.business.socket.gossip;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LiveInfo;
import com.yunbao.chatroom.business.socket.SocketProxy;
import com.yunbao.chatroom.business.socket.base.mannger.WheatControllerMannger;
import com.yunbao.chatroom.business.socket.gossip.impl.GossipSmsListnerImpl;
import com.yunbao.chatroom.business.socket.gossip.mannger.GossipWheatMannger;

public class GossipSocketProxy extends SocketProxy<GossipSmsListnerImpl> {
    private WheatControllerMannger mWheatControllerMannger;
    private GossipWheatMannger mGossipWheatMannger;

    public GossipSocketProxy(String url, GossipSmsListnerImpl socketMessageListner, LiveInfo liveInfo) {
        super(url, socketMessageListner, liveInfo);
    }

    public WheatControllerMannger getWheatControllMannger() {
        if(mWheatControllerMannger==null){
            mWheatControllerMannger=new WheatControllerMannger(mILiveSocket,mSocketMessageListner) ;
        }
        return mWheatControllerMannger;
    }

    public GossipWheatMannger getGossipWheatMannger() {
        if(mGossipWheatMannger==null){
           mGossipWheatMannger=new GossipWheatMannger(mILiveSocket,mSocketMessageListner) ;
        }
        return mGossipWheatMannger;
    }
    @Override
    protected void handle(String method, JSONObject jsonObject) {
        switch (method){
            case Constants.SOCKET_LINKMIC:
                getGossipWheatMannger().handle(jsonObject);
                break;
            case Constants.SOCKET_CONTROLMIC:
                getWheatControllMannger().handle(jsonObject);
                break;
            case Constants.SOCKET_TALK:
                getWheatControllMannger().handle(jsonObject);
                break;
            default:
                break;
        }
    }

    @Override
    public void release() {
        super.release();
        if(mWheatControllerMannger!=null){
           mWheatControllerMannger.release();
           mWheatControllerMannger=null;
        }
        if(mGossipWheatMannger!=null){
            mGossipWheatMannger.release();
            mWheatControllerMannger=null;
        }
    }
}
