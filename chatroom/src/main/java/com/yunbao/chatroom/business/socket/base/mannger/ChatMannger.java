package com.yunbao.chatroom.business.socket.base.mannger;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LiveChatBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.SocketSendBean;
import com.yunbao.chatroom.business.socket.ILiveSocket;
import com.yunbao.chatroom.business.socket.base.callback.ChatMessageListner;

//聊天室聊天
  public  class ChatMannger extends SocketManager {
    private ChatMessageListner mChatMessageListner;
    public ChatMannger(ILiveSocket ILiveSocket,ChatMessageListner chatMessageListner) {
        super(ILiveSocket);
        this.mChatMessageListner=chatMessageListner;
    }

    public void sendChatMessage(String content) {
          if (mILiveSocket == null) {
              return;
          }
          UserBean userBean = CommonAppConfig.getInstance().getUserBean();
          if (userBean == null) {
              return;
          }
        mILiveSocket.send(new SocketSendBean()
                  .param("_method_",Constants.SOCKET_CHAT)
                  .param("action", 0)
                  .param("msgtype", 2)
                  .param(userBean)
                  .param("ct", content));
      }

    public void sendPlaceOrderMessage(UserBean toUserBean) {
        if (mILiveSocket == null) {
            return;
        }
        UserBean userBean = CommonAppConfig.getInstance().getUserBean();
        if (userBean == null) {
            return;
        }
        mILiveSocket.send(new SocketSendBean()
                .param("_method_",Constants.SOCKET_PLACE_ORDER)
                .param("action", 0)
                .param("msgtype", 0)
                .param(userBean)
                .paramToUser(toUserBean)
        );
    }





    @Override
      public void handle(JSONObject jsonObject) {
            String method=jsonObject.getString("_method_");
            switch (method){
                case Constants.SOCKET_SYSTEM_NOT:
                    onSystemMessage(jsonObject);
                    break;
                case  Constants.SOCKET_SEND_GIFT:
                    onGiftMessage(jsonObject);
                    break;
                case  Constants.SOCKET_CHAT:
                    onChatMessage(jsonObject);
                    break;
                case Constants.SOCKET_PLACE_ORDER:
                    sendLocalPlaceOrderMessage(jsonObject);
                    break;
                 default:
                     break;
            }
      }


      /*发送给本地列表，不会向外发送socket*/
      public void sendLocalMessage(LiveChatBean liveChatBean){
        if(liveChatBean==null){
           return;
        }
          if(mChatMessageListner!=null){
             mChatMessageListner.onChat(liveChatBean);
          }
      }

    /*发送文本消息给本地列表*/
      public void sendLocalSystemMessage(String content){
          if(mChatMessageListner==null){
              return;
          }
          LiveChatBean bean = new LiveChatBean();
          bean.setContent(content);
          bean.setType(LiveChatBean.SYSTEM);
          sendLocalMessage(bean);
      }


    /*发送boss下单消息给本地列表*/
    public void sendLocalPlaceOrderMessage(JSONObject jsonObject){
        if(mChatMessageListner==null){
            return;
        }
        LiveChatBean bean = new LiveChatBean();
        bean.setContent(jsonObject.getString("uname")+ " " + WordUtil.getString(R.string.forward)+ " " +jsonObject.getString("toname") + WordUtil.getString(R.string.set_order));
        bean.setType(LiveChatBean.BOSS_PLACE_ORDER);
        mChatMessageListner.onChat(bean);
    }


    /*接收到普通聊天消息*/
    private void onChatMessage(JSONObject jsonObject) {
        LiveChatBean bean = new LiveChatBean();
        bean.setContent(jsonObject.getString("ct"));
        bean.setUserNiceName(jsonObject.getString("uname"));
        bean.setId(jsonObject.getString("uid"));
        bean.setSex(jsonObject.getIntValue("sex"));
        bean.setAge(jsonObject.getString("age"));
        bean.setAvatar(jsonObject.getString("avatar"));
        bean.setLevel(jsonObject.getIntValue("level"));
        bean.setAnchorLevel(jsonObject.getIntValue("level_anchor"));
        bean.setType(LiveChatBean.NORMAL);
        if(mChatMessageListner!=null){
           mChatMessageListner.onChat(bean);
        }
    }
    /*接收到礼物消息*/
    private void onGiftMessage(JSONObject jsonObject) {
        if(mChatMessageListner==null){
            return;
        }
        String uname=jsonObject.getString("uname");
        jsonObject=jsonObject.getJSONObject("ct");
        String toName=jsonObject.getString("toname");
        String giftName=jsonObject.getString("giftname");
        String giftNum=jsonObject.getString("nums");
        String content=WordUtil.getString(R.string.gift_tip,uname,toName,giftName,giftNum);

        LiveChatBean bean = new LiveChatBean();
        bean.setContent(content);
        bean.setUserNiceName(uname);
        bean.setToUserNiceName(toName);
        bean.setType(LiveChatBean.GIFT);
        mChatMessageListner.onChat(bean);
    }

    /*接收到系统消息*/
    private void onSystemMessage(JSONObject jsonObject) {
        if(mChatMessageListner==null){
            return;
        }
        LiveChatBean bean = new LiveChatBean();
        bean.setContent(jsonObject.getString("ct"));
        bean.setType(LiveChatBean.SYSTEM);
        mChatMessageListner.onChat(bean);
    }
}
