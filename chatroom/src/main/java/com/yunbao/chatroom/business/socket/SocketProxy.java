package com.yunbao.chatroom.business.socket;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LiveInfo;
import com.yunbao.chatroom.bean.SocketSendBean;
import com.yunbao.chatroom.business.socket.base.BaseSocketMessageLisnerImpl;
import com.yunbao.chatroom.business.socket.base.mannger.ChatMannger;
import com.yunbao.chatroom.business.socket.base.mannger.GiftMannger;
import com.yunbao.chatroom.business.socket.base.mannger.SocketManager;
import com.yunbao.chatroom.business.socket.base.mannger.SystemMessageMannger;

/*
 聊天室socket代理类,将业务逻辑与底层IO库进行抽象隔离

*/

public abstract class SocketProxy<T extends BaseSocketMessageLisnerImpl> extends SocketManager implements ILiveSocket{
  private ChatMannger mChatMannger;
  private GiftMannger mGiftMannger;
  protected T mSocketMessageListner;
  private SystemMessageMannger mSystemMessageMannger;


  public SocketProxy(String url, T socketMessageListner, LiveInfo liveInfo) {
    super(null);
    mILiveSocket = new SocketIOImpl(url,this,socketMessageListner,liveInfo);
    mSocketMessageListner=socketMessageListner;
  }

  /*这个是socketMannge的实现方法,通过传入SocketIOImpl接收到数据jsonObject进行处理,
  然后通过范型T实现可拓展的回调接收*/
    @Override
    public void handle(JSONObject jsonObject) {
        String method=jsonObject.getString("_method_");
        if(method==null){
            return;
        }
        switch (method){
            case Constants.SOCKET_SEND_GIFT:
                getGiftMannger().handle(jsonObject);
            case Constants.SOCKET_CHAT:
                getChatMannger().handle(jsonObject);
                break;
            case Constants.SOCKET_PLACE_ORDER:
                getChatMannger().handle(jsonObject);
                break;
            case Constants.SOCKET_SYSTEM_NOT:
                getChatMannger().handle(jsonObject);
                break;
            case Constants.SOCKET_END_LIVE:
                getSystemMessageMannger().handle(jsonObject);
                break;
            case Constants.SOCKET_ROOM_ENTER:
                getSystemMessageMannger().handle(jsonObject);
                break;
            default:
                handle(method,jsonObject);
                break;
        }
    }

    protected abstract void handle(String method, JSONObject jsonObject);

    public ChatMannger getChatMannger() {
        if(mChatMannger==null){
            mChatMannger=new ChatMannger(mILiveSocket,mSocketMessageListner);
        }
        return mChatMannger;
    }

    public GiftMannger getGiftMannger() {
        if(mGiftMannger==null){
           mGiftMannger=new GiftMannger(mILiveSocket,mSocketMessageListner) ;
        }
        return mGiftMannger;
    }


    public SystemMessageMannger getSystemMessageMannger() {
        if(mSystemMessageMannger==null){
            mSystemMessageMannger=new SystemMessageMannger(mILiveSocket,mSocketMessageListner) ;
        }
        return mSystemMessageMannger;
    }

    @Override
    public void connect() {
        if(mILiveSocket!=null){
            mILiveSocket.connect();
        }
    }

    @Override
    public void disConnect() {
        release();
    }

    @Override
    public void release() {
        if(mILiveSocket!=null){
            mILiveSocket.disConnect();
            mILiveSocket=null;
        }
        if(mChatMannger!=null){
            mChatMannger.release();
            mChatMannger=null;
        }
        if(mGiftMannger!=null){
            mGiftMannger.release();
            mGiftMannger=null;
        }
        if(mSocketMessageListner!=null){
           mSocketMessageListner.clear();
           mSocketMessageListner=null;
        }
    }

    @Override
    public void send(SocketSendBean socketSendBean) {
        if(mILiveSocket!=null){
          mILiveSocket.send(socketSendBean);
        }
    }

}
