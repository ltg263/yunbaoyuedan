package com.yunbao.chatroom.business.behavior;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.chatroom.bean.ApplyResult;
import com.yunbao.chatroom.business.socket.SocketProxy;
import io.reactivex.Observable;

/*获取申请列表的行为*/
public abstract class GetApplyListBehavior<T extends SocketProxy> extends BaseBehavior<T> {
    public abstract Observable<ApplyResult> getApplyList(LifecycleProvider lifecycleProvider,Object...args);
}
