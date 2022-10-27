package com.yunbao.chatroom.business.socket.base.mannger;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.Constants;
import com.yunbao.chatroom.business.socket.ILiveSocket;
import com.yunbao.chatroom.business.socket.base.callback.SystemMessageListnter;

/*系统消息相关*/
public class SystemMessageMannger extends SocketManager {
    private SystemMessageListnter mSystemMessageListnter;
    public SystemMessageMannger(ILiveSocket ILiveSocket,SystemMessageListnter systemMessageListnter) {
        super(ILiveSocket);
        this.mSystemMessageListnter=systemMessageListnter;
    }
    @Override
    public void handle(JSONObject jsonObject) {
        String method=getMethod(jsonObject);
        switch (method){
            case Constants.SOCKET_END_LIVE:
                if(mSystemMessageListnter!=null){
                   mSystemMessageListnter.endLive();
                }
                break;
            case Constants.SOCKET_ROOM_ENTER:
                if(mSystemMessageListnter!=null){
                   mSystemMessageListnter.enter(jsonObject.getString("uid"),jsonObject.getString("uname"),getAction(jsonObject)==0);
                }
                break;
            default:
                break;
        }
    }

}
