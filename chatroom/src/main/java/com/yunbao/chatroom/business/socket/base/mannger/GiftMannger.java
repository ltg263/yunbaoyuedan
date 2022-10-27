package com.yunbao.chatroom.business.socket.base.mannger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.ChatReceiveGiftBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.bean.SocketSendBean;
import com.yunbao.chatroom.business.socket.ILiveSocket;
import com.yunbao.chatroom.business.socket.base.callback.GiftMessageListner;

/*礼物相关*/
  public  class GiftMannger extends SocketManager {
    private GiftMessageListner mGiftMessageListner;

    public GiftMannger(ILiveSocket iLiveSocket,GiftMessageListner giftMessageListner) {
        super(iLiveSocket);
        mGiftMessageListner = giftMessageListner;
    }
      public  void sendGiftMessage( int giftType, String giftToken, String liveUid) {
          if (mILiveSocket == null) {
              return;
          }
          UserBean u = CommonAppConfig.getInstance().getUserBean();
          if (u == null) {
              return;
          }
          mILiveSocket.send(new SocketSendBean()
                  .param("_method_",Constants.SOCKET_SEND_GIFT)
                  .param("action", 0)
                  .param("msgtype", 1)
                  .param("uname", u.getUserNiceName())
                  .param("uid", u.getId())
                  .param("uhead", u.getAvatar())
                  .param("evensend", giftType)
                  .param("ct", giftToken)
                  .param("roomnum", liveUid));
      }
      @Override
      public void handle(JSONObject map) {
          ChatReceiveGiftBean receiveGiftBean = JSON.parseObject(map.getString("ct"), ChatReceiveGiftBean.class);
          receiveGiftBean.setAvatar(map.getString("uhead"));
          receiveGiftBean.setUserNiceName(map.getString("uname"));
          receiveGiftBean.setUid(map.getString("uid"));

          if(mGiftMessageListner!=null){
             mGiftMessageListner.onSendGift(receiveGiftBean);
          }
      }
  }