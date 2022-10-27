package com.yunbao.chatroom.business.socket.base;

import com.yunbao.common.bean.ChatReceiveGiftBean;
import com.yunbao.common.bean.LiveChatBean;
import com.yunbao.chatroom.business.socket.base.callback.BaseSocketMessageListner;

/*基础聊天功能的桥接模式,方便功能的组合,意义是避免方法的污染*/
public class BaseSocketMessageLisnerImpl implements BaseSocketMessageListner {
    private BaseSocketMessageListner mBaseSocketMessageListner;

    public BaseSocketMessageLisnerImpl(BaseSocketMessageListner baseSocketMessageListner) {
        mBaseSocketMessageListner = baseSocketMessageListner;
    }

    /**
     * 聊天室  收到聊天消息
     *
     * @param bean
     */
    @Override
    public void onChat(LiveChatBean bean) {
        if(mBaseSocketMessageListner!=null){
           mBaseSocketMessageListner.onChat(bean);
        }
    }
    @Override
    public void onSendGift(ChatReceiveGiftBean bean) {
        if(mBaseSocketMessageListner!=null){
            mBaseSocketMessageListner.onSendGift(bean);
        }
    }
    /**
     * 聊天室  连接成功socket后调用
     *
     * @param successConn
     */
    @Override
    public void onConnect(boolean successConn) {
        if(mBaseSocketMessageListner!=null){
           mBaseSocketMessageListner.onConnect(successConn);
        }
    }


    /**
     * 聊天室  自己的socket断开
     */
    @Override
    public void onDisConnect() {
        if(mBaseSocketMessageListner!=null){
            mBaseSocketMessageListner.onDisConnect();
        }
    }



    @Override
    public void endLive() {
        if(mBaseSocketMessageListner!=null){
            mBaseSocketMessageListner.endLive();
        }
    }
    @Override
    public void enter(String uid,String uname, boolean isEnter) {
        if(mBaseSocketMessageListner!=null){
            mBaseSocketMessageListner.enter(uid,uname,isEnter);
        }
    }


    public void clear(){
        mBaseSocketMessageListner=null;
    }

}
