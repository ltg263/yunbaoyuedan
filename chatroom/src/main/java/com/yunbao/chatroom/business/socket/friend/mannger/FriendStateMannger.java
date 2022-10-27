package com.yunbao.chatroom.business.socket.friend.mannger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LiveChatBean;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.MakePairBean;
import com.yunbao.chatroom.bean.SocketSendBean;
import com.yunbao.chatroom.business.socket.ILiveSocket;
import com.yunbao.chatroom.business.socket.base.mannger.SocketManager;
import com.yunbao.chatroom.business.socket.friend.callback.FriendStateListner;

import java.util.ArrayList;
import java.util.List;

public class FriendStateMannger extends SocketManager {
    private static final int CARDIAC_SELECTION=2; //心动选择
    private static final int ANNOUNCE_HEARTBEAT=3;//心动发布
    private static final int NEXT_GAME=4; //下一场
    private FriendStateListner mFriendStateListner;
    public FriendStateMannger(ILiveSocket liveSocket,FriendStateListner friendStateListner) {
        super(liveSocket);
        mFriendStateListner=friendStateListner;
    }

    @Override
    public void handle(JSONObject jsonObject) {
      int stateAction= getAction(jsonObject);
       switch (stateAction){
           case CARDIAC_SELECTION:
               if(mFriendStateListner!=null){
                  mFriendStateListner.changeState(Constants.STATE_CARDIAC_SELECTION);
               }
               break;
           case ANNOUNCE_HEARTBEAT:
               handleAnnounceHeartbeat(jsonObject);

               break;
           case NEXT_GAME:
               if(mFriendStateListner!=null){
                  mFriendStateListner.changeState(Constants.STATE_PREPARATION_LINK);
               }
               break;
       }
    }

    private void handleAnnounceHeartbeat(JSONObject jsonObject) {
        if(mFriendStateListner==null){
           return;
        }
        mFriendStateListner.changeState(Constants.STATE_ANNOUNCE_HEARTBEAT);
        JSONArray jsonArray=jsonObject.getJSONArray("heart");
        int size=jsonArray.size();
        List<LiveChatBean>chatArray=new ArrayList<>(size);
        for(int i=0;i<size;i++){
            LiveChatBean liveChatBean=new LiveChatBean();
            JSONObject jsonObjectTemp= jsonArray.getJSONObject(i);
            liveChatBean.setType(LiveChatBean.FRIEND);
            String userNickName=jsonObjectTemp.getString("u_user_nickname");
            String userToName=jsonObjectTemp.getString("to_user_nickname");
            liveChatBean.setUserNiceName(userNickName);
            liveChatBean.setToUserNiceName(userToName);
            String content= WordUtil.getString(R.string.friend_result,userNickName,userToName);
            liveChatBean.setContent(content);
            chatArray.add(liveChatBean);
        }
        String json=jsonObject.getString("hand");
        List<MakePairBean>makePairBeanArrayList=JSON.parseArray(json,MakePairBean.class);
        mFriendStateListner.heartBeatResult(chatArray,makePairBeanArrayList);
    }

    /*发送socket心动选择*/
    public void sendSocketCardiacSelection(){
        sendChangeState(CARDIAC_SELECTION);
    }

    /*发送socket公布心动结果 */
    public void sendSocketHeartBeat(){
        sendChangeState(ANNOUNCE_HEARTBEAT);
    }
    /*发送socket下一场*/
    public void sendSocketNextGame(){
        sendChangeState(NEXT_GAME);
    }

    private void sendChangeState(int action){
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_FRIEND)
                .param("action", action)
        );
    }

}
