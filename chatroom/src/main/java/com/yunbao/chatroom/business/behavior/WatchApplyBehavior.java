package com.yunbao.chatroom.business.behavior;

import com.yunbao.chatroom.business.socket.SocketProxy;


/*观察是否有上麦用户申请*/
public abstract class WatchApplyBehavior <T extends SocketProxy> extends BaseBehavior<T> {
    protected WatchApplyListner mWatchApplyListner;
    public abstract void watch(WatchApplyListner watchApplyListner);

    public static interface WatchApplyListner{
        public void watch(boolean isUp);
    }
    @Override
    public void unSubscribe() {
        super.unSubscribe();
        mWatchApplyListner=null;
    }
}
