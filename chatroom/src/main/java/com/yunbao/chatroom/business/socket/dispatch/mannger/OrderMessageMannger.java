package com.yunbao.chatroom.business.socket.dispatch.mannger;

import android.view.View;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.common.Constants;
import com.yunbao.common.server.observer.LockClickObserver;
import com.yunbao.chatroom.bean.LiveOrderCommitBean;
import com.yunbao.chatroom.bean.SocketSendBean;
import com.yunbao.chatroom.business.socket.ILiveSocket;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.base.mannger.SocketManager;
import com.yunbao.chatroom.business.socket.dispatch.callback.OrderMessageListner;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;

/*发送接收订单相关的数据*/
public class OrderMessageMannger extends SocketManager {
    private OrderMessageListner mOrderMessageListner;
    public OrderMessageMannger(ILiveSocket liveSocket,OrderMessageListner orderMessageListner) {
        super(liveSocket);
        mOrderMessageListner=orderMessageListner;
    }

    @Override
    public void handle(JSONObject jsonObject) {
       if(mOrderMessageListner!=null){
          mOrderMessageListner.onOrderUpDate(jsonObject.getString("skillid"));
       }
    }


    /*发布订单*/
    public void dispatchOrder(final LiveOrderCommitBean commitBean, String stream, LifecycleProvider lifecycleProvider, View view, final SuccessListner successListner){
        ChatRoomHttpUtil.sendDispatchOrder(commitBean,stream).compose(lifecycleProvider.<Boolean>bindToLifecycle())
          .subscribe(new LockClickObserver<Boolean>(view) {
              @Override
              public void onSucc(Boolean aBoolean) {
                  if(aBoolean){
                     sendDisPatchOrder(commitBean.getSkillId());
                     successListner.success();
                  }
              }
          });
    }

    /*发布订单的socket*/
    public void sendDisPatchOrder(String skillId){
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_DISPATCH)
                .param("skillid",skillId)
        );
    }
}
