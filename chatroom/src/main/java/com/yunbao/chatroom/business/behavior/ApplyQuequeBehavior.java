package com.yunbao.chatroom.business.behavior;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.chatroom.business.socket.SocketProxy;
import com.yunbao.chatroom.business.socket.SuccessListner;

/*申请上麦的行为*/
public abstract class ApplyQuequeBehavior<T extends SocketProxy> extends BaseBehavior<T>{
    private boolean mIsApplying;
    public abstract void applyUpWheat(LifecycleProvider lifecycleProvider, SuccessListner successListner);

    public boolean isApplying() {
        return mIsApplying;
    }

    public void setApplying(boolean applying) {
        mIsApplying = applying;
    }
}
