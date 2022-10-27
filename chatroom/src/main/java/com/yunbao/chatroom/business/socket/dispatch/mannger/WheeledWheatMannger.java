package com.yunbao.chatroom.business.socket.dispatch.mannger;

import android.support.annotation.NonNull;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.bean.SocketSendBean;
import com.yunbao.chatroom.business.socket.ILiveSocket;
import com.yunbao.chatroom.business.socket.base.mannger.SocketManager;
import com.yunbao.chatroom.business.socket.dispatch.callback.WheeledWheatListner;
import java.util.LinkedHashSet;
import java.util.Set;

public class WheeledWheatMannger extends SocketManager {
    private static final int START=1;
    private static final int CANCLE=2;
    private static final int TAKING=3;
    private static final int DROP=4;
    private static final int END=5;
    private boolean isStartWheeled;
    private Set<WheeledWheatListner> mWheeledWheatListnerSet;
    public WheeledWheatMannger(ILiveSocket liveSocket, WheeledWheatListner listner) {
        super(liveSocket);
        addWheeledListner(listner);
    }
    
    @Override
    public void handle(JSONObject jsonObject) {
       int action= getAction(jsonObject);
       switch (action){
           case START:
               if(mWheeledWheatListnerSet!=null){
                   for(WheeledWheatListner wheeledWheatListner:mWheeledWheatListnerSet){
                       wheeledWheatListner.openWheeledWheat(true);
                   }
               }
               isStartWheeled=true;
               break;
           case CANCLE:
               if(mWheeledWheatListnerSet!=null){
                   for(WheeledWheatListner wheeledWheatListner:mWheeledWheatListnerSet){
                       wheeledWheatListner.openWheeledWheat(false);
                   }
               }
               isStartWheeled=false;
               break;
           case TAKING:
               UserBean userBean=SocketSendBean.parseToUserBean(jsonObject);
               if(mWheeledWheatListnerSet!=null){
                   for(WheeledWheatListner wheeledWheatListner:mWheeledWheatListnerSet){
                       wheeledWheatListner.changeSpeakUser(userBean);
                   }
               }
               break;
           case DROP:
               break;
           case END:
               isStartWheeled=false;
               if(mWheeledWheatListnerSet!=null){
                   for(WheeledWheatListner wheeledWheatListner:mWheeledWheatListnerSet){
                       wheeledWheatListner.openWheeledWheat(false);
                   }
               }
               break;
            default:
                break;
       }
    }

    /*开始发言*/
    public void startWheelWheet(){
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_TURNTALK)
                .param("action", START)
        );
    }

    /*取消发言*/
    public void cancleWheelWheet(){
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_TURNTALK)
                .param("action",CANCLE)
        );
    }

    /*跳过发言*/
    public void dropWheelWheet(){
        UserBean userBean=CommonAppConfig.getInstance().getUserBean();
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_TURNTALK)
                .param("action",DROP)
                .param(userBean)
        );
    }

    public void addWheeledListner(@NonNull WheeledWheatListner wheeledWheatListner){
        if(wheeledWheatListner==null){
            return;
        }
        if(mWheeledWheatListnerSet==null){
           mWheeledWheatListnerSet=new LinkedHashSet<>();
        }
        mWheeledWheatListnerSet.add(wheeledWheatListner);
    }

    public void removeWheeledListner(@NonNull WheeledWheatListner wheeledWheatListner){
        if(wheeledWheatListner==null){
            return;
        }
        if(mWheeledWheatListnerSet!=null){
           mWheeledWheatListnerSet.remove(wheeledWheatListner);
        }
    }

    @Override
    public void release() {
        super.release();
       if(mWheeledWheatListnerSet!=null){
          mWheeledWheatListnerSet.clear();
          mWheeledWheatListnerSet=null;
       }
    }

    public boolean isStartWheeled() {
        return isStartWheeled;
    }
}
