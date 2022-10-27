package com.yunbao.chatroom.business.socket.dispatch;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LiveInfo;
import com.yunbao.chatroom.business.socket.SocketProxy;
import com.yunbao.chatroom.business.socket.base.mannger.WheatControllerMannger;
import com.yunbao.chatroom.business.socket.dispatch.impl.DispatchSmsListnerImpl;
import com.yunbao.chatroom.business.socket.dispatch.mannger.OrderMessageMannger;
import com.yunbao.chatroom.business.socket.dispatch.mannger.WheatMannger;
import com.yunbao.chatroom.business.socket.dispatch.mannger.WheeledWheatMannger;

/*派单socket的代理类*/
public class DispatchSocketProxy extends SocketProxy<DispatchSmsListnerImpl> {
    private WheatMannger mWheatMannger;  /*WheatMannger 里面的监听器是集合可以通过liftHoler获取对象添加监听,但是不要忘记结束的时候移除监听*/
    private WheatControllerMannger mWheatControllerMannger;
    private WheeledWheatMannger mWheeledWheatMannger;
    private OrderMessageMannger mOrderMessageMannger;

    public DispatchSocketProxy(String url, DispatchSmsListnerImpl socketMessageListner, LiveInfo liveInfo) {
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
            case Constants.SOCKET_TURNTALK:
                getWheeledWheatMannger().handle(jsonObject);
                break;
            case Constants.SOCKET_DISPATCH:
                getOrderMessageMannger().handle(jsonObject);
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

    public WheeledWheatMannger getWheeledWheatMannger() {
        if(mWheeledWheatMannger==null){
            mWheeledWheatMannger=new WheeledWheatMannger(mILiveSocket,mSocketMessageListner) ;
        }
        return mWheeledWheatMannger;
    }

    public WheatMannger getWheatMannger() {
        if(mWheatMannger==null){
            mWheatMannger=new WheatMannger(mILiveSocket,mSocketMessageListner) ;
        }
        return mWheatMannger;
    }

    public OrderMessageMannger getOrderMessageMannger() {
        if(mOrderMessageMannger==null){
            mOrderMessageMannger=new OrderMessageMannger(mILiveSocket,mSocketMessageListner);
        }
        return mOrderMessageMannger;
    }

    @Override
    public void release() {
        super.release();
        if(mWheatMannger!=null){
            mWheatMannger.removeWheatListner(mSocketMessageListner);
            mWheatMannger.release();
            mWheatMannger=null;
        }
        if(mWheeledWheatMannger!=null){
            mWheeledWheatMannger.removeWheeledListner(mSocketMessageListner);
            mWheeledWheatMannger.release();
            mWheeledWheatMannger=null;
        }
        if(mWheatControllerMannger!=null){
           mWheatControllerMannger.release();
            mWheatControllerMannger=null;
        }

        if(mOrderMessageMannger!=null){
           mOrderMessageMannger.release();
            mOrderMessageMannger=null;
        }
    }
}
