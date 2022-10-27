package com.yunbao.chatroom.business.socket.base.mannger;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.chatroom.business.socket.ILiveSocket;

/*持有底层数据的操作类,管理接收和发送socket的逻辑*/
public  abstract class SocketManager{
    protected ILiveSocket mILiveSocket;

    public SocketManager(ILiveSocket liveSocket) {
        mILiveSocket = liveSocket;
    }
    public abstract  void handle(JSONObject jsonObject);

    public void release(){
        mILiveSocket=null;

    }

    public int getAction(JSONObject jsonObject){
        return jsonObject.getIntValue("action");
    }
    public String getMethod(JSONObject jsonObject){
        return jsonObject.getString("_method_");
    }
  }