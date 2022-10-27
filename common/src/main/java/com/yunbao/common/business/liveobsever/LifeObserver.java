package com.yunbao.common.business.liveobsever;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;


public class LifeObserver implements LifecycleObserver {
    public int ownerHashCode;
    public LifeObserver(int ownerHashCode){
        this.ownerHashCode=ownerHashCode;
    }

    private static final String TAG="LifeObserver";
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreated(){
        Log.d(TAG, "onCreated: ");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart(){
        Log.d(TAG, "onStart: ");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume(){
        Log.d(TAG, "onResume: ");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(){
        Log.d(TAG, "onPause: ");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop(){
        Log.d(TAG, "onStop: ");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void customMethod(){
        Log.d(TAG, "customMethod: ");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    public void onAny(){//此方法可以有参数，但类型必须如两者之一(LifecycleOwner owner,Lifecycle.Event event)
        Log.d(TAG, "onAny: ");
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestory(){//此方法可以有参数，但类型必须如两者之一(LifecycleOwner owner,Lifecycle.Event event)
        Log.d(TAG, "ONDESTROY: ");
    }
}