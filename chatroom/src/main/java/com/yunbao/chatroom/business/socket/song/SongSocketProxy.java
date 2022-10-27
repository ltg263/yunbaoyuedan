package com.yunbao.chatroom.business.socket.song;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LiveInfo;
import com.yunbao.chatroom.business.socket.SocketProxy;
import com.yunbao.chatroom.business.socket.base.mannger.WheatControllerMannger;
import com.yunbao.chatroom.business.socket.song.impl.SongSmsListnerImpl;
import com.yunbao.chatroom.business.socket.song.mannger.SongWheatMannger;

public class SongSocketProxy extends SocketProxy<SongSmsListnerImpl> {
    private SongWheatMannger mWheatMannger;  /*WheatMannger 里面的监听器是集合可以通过liftHoler获取对象添加监听,但是不要忘记结束的时候移除监听*/
    private WheatControllerMannger mWheatControllerMannger;

    public SongSocketProxy(String url, SongSmsListnerImpl socketMessageListner, LiveInfo liveInfo) {
        super(url, socketMessageListner, liveInfo);
    }
    @Override
    protected void handle(String method, JSONObject jsonObject) {
        switch (method){
            case Constants.SOCKET_LINKMIC:
                getWheatMannger().handle(jsonObject);
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

    public WheatControllerMannger getWheatControllMannger() {
        if(mWheatControllerMannger==null){
            mWheatControllerMannger=new WheatControllerMannger(mILiveSocket,mSocketMessageListner) ;
        }
        return mWheatControllerMannger;
    }
    public SongWheatMannger getWheatMannger() {
        if(mWheatMannger==null){
            mWheatMannger=new SongWheatMannger(mILiveSocket,mSocketMessageListner) ;
        }
        return mWheatMannger;
    }

    @Override
    public void release() {
        super.release();
        if(mWheatMannger!=null){
            mWheatMannger.release();
            mWheatMannger=null;
        }
        if( mWheatControllerMannger!=null){
            mWheatControllerMannger.release();
            mWheatControllerMannger=null;
        }


    }
}
