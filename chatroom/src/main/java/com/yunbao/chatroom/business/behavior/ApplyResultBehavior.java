package com.yunbao.chatroom.business.behavior;

import android.view.View;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.business.socket.SocketProxy;
import com.yunbao.chatroom.business.socket.SuccessListner;

/*处理申请的情况*/
public abstract class ApplyResultBehavior<T extends SocketProxy> extends BaseBehavior<T> {
   public abstract void agree(UserBean userBean, LifecycleProvider lifecycleProvider, View view, SuccessListner successListner);
   public abstract void refuse(UserBean userBean,LifecycleProvider lifecycleProvider, SuccessListner successListner);

}
