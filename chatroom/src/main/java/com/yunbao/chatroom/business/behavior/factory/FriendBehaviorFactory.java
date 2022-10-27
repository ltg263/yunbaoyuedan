package com.yunbao.chatroom.business.behavior.factory;

import android.arch.lifecycle.LifecycleOwner;
import com.yunbao.chatroom.business.behavior.ApplyQuequeBehavior;
import com.yunbao.chatroom.business.behavior.ApplyResultBehavior;
import com.yunbao.chatroom.business.behavior.CancleQueBehavior;
import com.yunbao.chatroom.business.behavior.GetApplyListBehavior;
import com.yunbao.chatroom.business.behavior.SkPowerBehavior;
import com.yunbao.chatroom.business.behavior.WatchApplyBehavior;
import com.yunbao.chatroom.business.behavior.friend.FdApplyQuequeBehavior;
import com.yunbao.chatroom.business.behavior.friend.FdApplyResultBehavior;
import com.yunbao.chatroom.business.behavior.friend.FdCancleQueBeHavior;
import com.yunbao.chatroom.business.behavior.friend.FdGetApplyListBehavior;
import com.yunbao.chatroom.business.behavior.friend.FdSkPowerBehavior;
import com.yunbao.chatroom.business.behavior.friend.FdWatchApplyBehavior;

public class FriendBehaviorFactory extends AbsBehaviorFactory{
    @Override
    public CancleQueBehavior getCancleQueBehavior(LifecycleOwner lifecycleOwner) {
        return new FdCancleQueBeHavior();
    }
    @Override
    public ApplyQuequeBehavior getApplyQueBehavior(LifecycleOwner lifecycleOwner) {
        return new FdApplyQuequeBehavior();
    }
    @Override
    public ApplyResultBehavior getApplyResultBehavior(LifecycleOwner lifecycleOwner) {
        return new FdApplyResultBehavior();
    }

    @Override
    public GetApplyListBehavior getApplyListBehavior(LifecycleOwner lifecycleOwner) {
        return new FdGetApplyListBehavior();
    }
    @Override
    public SkPowerBehavior getSkPowerBehavior(LifecycleOwner lifecycleOwner) {
        return new FdSkPowerBehavior();
    }

    @Override
    public WatchApplyBehavior getWatchApplyBehavior(LifecycleOwner lifecycleOwner) {
        return new FdWatchApplyBehavior();
    }
}
