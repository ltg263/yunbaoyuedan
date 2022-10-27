package com.yunbao.chatroom.business.behavior.factory;

import android.arch.lifecycle.LifecycleOwner;

import com.yunbao.chatroom.business.behavior.GetApplyListBehavior;
import com.yunbao.chatroom.business.behavior.ApplyQuequeBehavior;
import com.yunbao.chatroom.business.behavior.ApplyResultBehavior;
import com.yunbao.chatroom.business.behavior.CancleQueBehavior;
import com.yunbao.chatroom.business.behavior.SkPowerBehavior;
import com.yunbao.chatroom.business.behavior.WatchApplyBehavior;

public abstract class AbsBehaviorFactory {

    public  abstract CancleQueBehavior   getCancleQueBehavior(LifecycleOwner lifecycleOwner);
    public  abstract ApplyQuequeBehavior getApplyQueBehavior(LifecycleOwner lifecycleOwner);
    public  abstract ApplyResultBehavior getApplyResultBehavior(LifecycleOwner lifecycleOwner);
    public  abstract GetApplyListBehavior getApplyListBehavior(LifecycleOwner lifecycleOwner);
    public  abstract SkPowerBehavior getSkPowerBehavior(LifecycleOwner lifecycleOwner);
    public  abstract WatchApplyBehavior getWatchApplyBehavior(LifecycleOwner lifecycleOwner);


}
