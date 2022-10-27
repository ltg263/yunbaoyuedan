package com.yunbao.chatroom.business.behavior.factory;

import android.arch.lifecycle.LifecycleOwner;
import com.yunbao.chatroom.business.behavior.ApplyQuequeBehavior;
import com.yunbao.chatroom.business.behavior.ApplyResultBehavior;
import com.yunbao.chatroom.business.behavior.CancleQueBehavior;
import com.yunbao.chatroom.business.behavior.GetApplyListBehavior;
import com.yunbao.chatroom.business.behavior.SkPowerBehavior;
import com.yunbao.chatroom.business.behavior.WatchApplyBehavior;
import com.yunbao.chatroom.business.behavior.song.SongApplyQuequeBehavior;
import com.yunbao.chatroom.business.behavior.song.SongApplyResultBehavior;
import com.yunbao.chatroom.business.behavior.song.SongCancleQueBeHavior;
import com.yunbao.chatroom.business.behavior.song.SongGetApplyListBehavior;
import com.yunbao.chatroom.business.behavior.song.SongSkPowerBehavior;
import com.yunbao.chatroom.business.behavior.song.SongWatchApplyBehavior;


public class SongBehaviorFactory extends AbsBehaviorFactory {
    @Override
    public CancleQueBehavior getCancleQueBehavior(LifecycleOwner lifecycleOwner) {
        return new SongCancleQueBeHavior();
    }

    @Override
    public ApplyQuequeBehavior getApplyQueBehavior(LifecycleOwner lifecycleOwner) {
        return new SongApplyQuequeBehavior();
    }

    @Override
    public ApplyResultBehavior getApplyResultBehavior(LifecycleOwner lifecycleOwner) {
        return new SongApplyResultBehavior();
    }

    @Override
    public GetApplyListBehavior getApplyListBehavior(LifecycleOwner lifecycleOwner) {
        return new SongGetApplyListBehavior();
    }

    @Override
    public SkPowerBehavior getSkPowerBehavior(LifecycleOwner lifecycleOwner) {
        return new SongSkPowerBehavior();
    }

    @Override
    public WatchApplyBehavior getWatchApplyBehavior(LifecycleOwner lifecycleOwner) {
        return new SongWatchApplyBehavior();
    }
}