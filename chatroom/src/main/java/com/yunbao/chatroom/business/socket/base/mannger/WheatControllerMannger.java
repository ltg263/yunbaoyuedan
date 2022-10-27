package com.yunbao.chatroom.business.socket.base.mannger;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.bean.SocketSendBean;
import com.yunbao.chatroom.business.socket.ILiveSocket;
import com.yunbao.chatroom.business.socket.base.callback.WheatControllListner;

/*控制用户麦的相关逻辑*/
public class WheatControllerMannger extends SocketManager {
    private static final int OPEN=1;
    private static final int CLOSE=2;
    private WheatControllListner mWheatControllListner;

    public WheatControllerMannger(ILiveSocket liveSocket, WheatControllListner wheatControllListner) {
        super(liveSocket);
        mWheatControllListner=wheatControllListner;
    }

    @Override
    public void handle(JSONObject jsonObject) {
        String method=getMethod(jsonObject);
        switch (method){
            case Constants.SOCKET_CONTROLMIC: //控麦
                handleControll(jsonObject);
                break;
            case Constants.SOCKET_TALK:         //发言状态监听
                handAudioState(jsonObject);
                break;
            default:
                break;
        }
    }

    private void handAudioState(JSONObject jsonObject) {
        String uid=jsonObject.getString("touid");
        boolean isOpen=getAction(jsonObject)==1;
        if(mWheatControllListner!=null){
           mWheatControllListner.userAudioOpen(uid,isOpen);
        }
    }

    private void handleControll(JSONObject jsonObject) {
        int action=getAction(jsonObject);
        UserBean userBean=SocketSendBean.parseToUserBean(jsonObject);
        switch (action){
            case OPEN:
                if(mWheatControllListner!=null){
                    mWheatControllListner.openSpeak(userBean,true);
                }
                break;
            case CLOSE:
                if(mWheatControllListner!=null){
                    mWheatControllListner.openSpeak(userBean,false);
                }
                break;
            default:
                break;
        }
    }

    /*控制用户是否允许发言*/
    public void sendWheatIsOpen(UserBean userBean,boolean isOpen){
        sendSocketWheatIsOpen(userBean,isOpen);
    }

    /*发送用户发言的状态*/
    public void sendWheatIsOpenState(String uid,boolean isOpen){
        int action=isOpen?1:0;
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_TALK)
                .param("action", action)
                .param("touid",uid));
    }

    //发送控制是否能发言的socket
    private void sendSocketWheatIsOpen(UserBean userBean, boolean isOpen) {
        int action=isOpen?OPEN:CLOSE;
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_CONTROLMIC)
                .param("action", action)
                .paramToUser(userBean)
        );
    }

}
