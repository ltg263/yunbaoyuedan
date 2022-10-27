package com.yunbao.chatroom.business.behavior.factory;

import android.arch.lifecycle.LifecycleOwner;

import com.yunbao.chatroom.business.behavior.GetApplyListBehavior;
import com.yunbao.chatroom.business.behavior.ApplyQuequeBehavior;
import com.yunbao.chatroom.business.behavior.ApplyResultBehavior;
import com.yunbao.chatroom.business.behavior.CancleQueBehavior;
import com.yunbao.chatroom.business.behavior.SkPowerBehavior;
import com.yunbao.chatroom.business.behavior.WatchApplyBehavior;
import com.yunbao.chatroom.business.behavior.spatch.SpApplyQuequeBehavior;
import com.yunbao.chatroom.business.behavior.spatch.SpApplyResultBehavior;
import com.yunbao.chatroom.business.behavior.spatch.SpCancleQueBeHavior;
import com.yunbao.chatroom.business.behavior.spatch.SpGetApplyListBehavior;
import com.yunbao.chatroom.business.behavior.spatch.SpSkPowerBehavior;
import com.yunbao.chatroom.business.behavior.spatch.SpWatchApplyBehavior;

public class DisPatchBehaviorFactory extends AbsBehaviorFactory{

    @Override
    public CancleQueBehavior getCancleQueBehavior(LifecycleOwner lifecycleOwner) {
        return new SpCancleQueBeHavior();
    }

    @Override
    public ApplyQuequeBehavior getApplyQueBehavior(LifecycleOwner lifecycleOwner) {
        return new SpApplyQuequeBehavior();
    }

    @Override
    public ApplyResultBehavior getApplyResultBehavior(LifecycleOwner lifecycleOwner) {
        return new SpApplyResultBehavior();
    }

    @Override
    public GetApplyListBehavior getApplyListBehavior(LifecycleOwner lifecycleOwner) {
        return new SpGetApplyListBehavior();
    }

    @Override
    public SkPowerBehavior getSkPowerBehavior(LifecycleOwner lifecycleOwner) {
        return new SpSkPowerBehavior();
    }
    @Override
    public WatchApplyBehavior getWatchApplyBehavior(LifecycleOwner lifecycleOwner) {
        return new SpWatchApplyBehavior();
    }
}
