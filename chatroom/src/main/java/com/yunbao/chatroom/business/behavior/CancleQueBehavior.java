package com.yunbao.chatroom.business.behavior;

import android.view.View;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.chatroom.business.socket.SocketProxy;
import com.yunbao.chatroom.business.socket.SuccessListner;


/*抽象的取消排队的行为*/
public abstract class CancleQueBehavior<T extends SocketProxy> extends BaseBehavior<T>{
    public abstract void cancleApplyQueue(View view, LifecycleProvider lifecycleProvider, SuccessListner successListner);

}
