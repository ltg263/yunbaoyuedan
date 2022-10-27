package com.yunbao.chatroom.business.behavior.factory;

import android.arch.lifecycle.LifecycleOwner;
import com.yunbao.chatroom.business.behavior.ApplyQuequeBehavior;
import com.yunbao.chatroom.business.behavior.ApplyResultBehavior;
import com.yunbao.chatroom.business.behavior.CancleQueBehavior;
import com.yunbao.chatroom.business.behavior.GetApplyListBehavior;
import com.yunbao.chatroom.business.behavior.SkPowerBehavior;
import com.yunbao.chatroom.business.behavior.WatchApplyBehavior;
import com.yunbao.chatroom.business.behavior.gossip.GpApplyQuequeBehavior;
import com.yunbao.chatroom.business.behavior.gossip.GpApplyResultBehavior;
import com.yunbao.chatroom.business.behavior.gossip.GpCancleQueBeHavior;
import com.yunbao.chatroom.business.behavior.gossip.GpGetApplyListBehavior;
import com.yunbao.chatroom.business.behavior.gossip.GpSkPowerBehavior;
import com.yunbao.chatroom.business.behavior.gossip.GpWatchApplyBehavior;

public class GossipBehaviorFactory extends AbsBehaviorFactory {
    @Override
    public CancleQueBehavior getCancleQueBehavior(LifecycleOwner lifecycleOwner) {
        return new GpCancleQueBeHavior();
    }
    @Override
    public ApplyQuequeBehavior getApplyQueBehavior(LifecycleOwner lifecycleOwner) {
        return new GpApplyQuequeBehavior();
    }
    @Override
    public ApplyResultBehavior getApplyResultBehavior(LifecycleOwner lifecycleOwner) {
        return new GpApplyResultBehavior();
    }
    @Override
    public GetApplyListBehavior getApplyListBehavior(LifecycleOwner lifecycleOwner) {
        return new GpGetApplyListBehavior();
    }
    @Override
    public SkPowerBehavior getSkPowerBehavior(LifecycleOwner lifecycleOwner) {
        return new GpSkPowerBehavior();
    }
    @Override
    public WatchApplyBehavior getWatchApplyBehavior(LifecycleOwner lifecycleOwner) {
        return new GpWatchApplyBehavior();
    }
}
