package com.yunbao.chatroom.business.behavior.factory;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.yunbao.common.utils.L;
import com.yunbao.chatroom.business.behavior.GetApplyListBehavior;
import com.yunbao.chatroom.business.behavior.ApplyQuequeBehavior;
import com.yunbao.chatroom.business.behavior.ApplyResultBehavior;
import com.yunbao.chatroom.business.behavior.CancleQueBehavior;
import com.yunbao.chatroom.business.behavior.SkPowerBehavior;
import com.yunbao.chatroom.business.behavior.WatchApplyBehavior;


/*避免行为对象重复创建

BehaviorFactory可以进行混搭,
只要逻辑合适你可以在交友的factory去引用派单相关Behavior,
工厂类里面的模版方法只是所有聊天室目前所共有的基本行为,
如果新的聊天室有新的行为拓展,建议不要添加工厂公共方法避免方法污染;
*/
public class CacheBehaviorFactory extends AbsBehaviorFactory {
    private static final String TAG = "CacheBehaviorFactory";
    private static CacheBehaviorFactory cacheBehaviorFactory;
    private AbsBehaviorFactory mAbsBehaviorFactory;
    /*取消排队*/
    private CancleQueBehavior mCancleQueBehavior;
    /*申请上麦*/
    private ApplyQuequeBehavior mApplyQuequeBehavior;
    /*处理申请*/
    private ApplyResultBehavior mApplyResultBehavior;
    /*获取申请列表*/
    private GetApplyListBehavior mGetApplyListBehavior;
    /*控制麦*/
    private SkPowerBehavior mSkPowerBehavior;
    /*监听是否有上麦用户申请*/
    private WatchApplyBehavior mSpWatchApplyBehavior;

    private CacheBehaviorFactory(){

    }
    public static CacheBehaviorFactory getInstance(){
        if(cacheBehaviorFactory==null){
          synchronized (CancleQueBehavior.class){
            cacheBehaviorFactory=new CacheBehaviorFactory();
          }
        }
        return cacheBehaviorFactory;
    }
    public void setAbsBehaviorFactory(@NonNull AbsBehaviorFactory absBehaviorFactory) {
        mAbsBehaviorFactory = absBehaviorFactory;
    }

    @Override
    public CancleQueBehavior getCancleQueBehavior(LifecycleOwner lifecycleOwner) {
        if(mCancleQueBehavior==null){
           mCancleQueBehavior=mAbsBehaviorFactory.getCancleQueBehavior(lifecycleOwner);
           mCancleQueBehavior.subscribe(lifecycleOwner);
        }
        return mCancleQueBehavior;
    }
    @Override
    public ApplyQuequeBehavior getApplyQueBehavior(LifecycleOwner lifecycleOwner) {
        if(mApplyQuequeBehavior==null){
           mApplyQuequeBehavior=mAbsBehaviorFactory.getApplyQueBehavior(lifecycleOwner);
           mApplyQuequeBehavior.subscribe(lifecycleOwner);
        }
        return mApplyQuequeBehavior;
    }
    @Override
    public ApplyResultBehavior getApplyResultBehavior(LifecycleOwner lifecycleOwner) {
        if(mApplyResultBehavior==null){
           mApplyResultBehavior=mAbsBehaviorFactory.getApplyResultBehavior(lifecycleOwner);
           mApplyResultBehavior.subscribe(lifecycleOwner);
        }
        return mApplyResultBehavior;
    }

    @Override
    public GetApplyListBehavior getApplyListBehavior(LifecycleOwner lifecycleOwner) {
        if(mGetApplyListBehavior==null){
            mGetApplyListBehavior=mAbsBehaviorFactory.getApplyListBehavior(lifecycleOwner);
            mGetApplyListBehavior.subscribe(lifecycleOwner);
        }
        return mGetApplyListBehavior;
    }
    @Override
    public SkPowerBehavior getSkPowerBehavior(LifecycleOwner lifecycleOwner) {
        if(mSkPowerBehavior==null){
            mSkPowerBehavior =mAbsBehaviorFactory.getSkPowerBehavior(lifecycleOwner);
            mSkPowerBehavior.subscribe(lifecycleOwner);
        }
        return mSkPowerBehavior;
    }

    @Override
    public WatchApplyBehavior getWatchApplyBehavior(LifecycleOwner lifecycleOwner) {
        if(mSpWatchApplyBehavior==null){
            mSpWatchApplyBehavior=mAbsBehaviorFactory.getWatchApplyBehavior(lifecycleOwner);
            mSpWatchApplyBehavior.subscribe(lifecycleOwner);
        }
        return mSpWatchApplyBehavior;
    }

    public void clear(){
        cacheBehaviorFactory=null;
        mAbsBehaviorFactory=null;
        if(mCancleQueBehavior!=null){
           mCancleQueBehavior.unSubscribe();
           mCancleQueBehavior=null;
        }
        if(mApplyQuequeBehavior!=null){
            mApplyQuequeBehavior.unSubscribe();
            mApplyQuequeBehavior=null;
        }
        if( mApplyResultBehavior!=null){
            mApplyResultBehavior.unSubscribe();
            mApplyResultBehavior=null;
        }
        if( mGetApplyListBehavior!=null){
            mGetApplyListBehavior.unSubscribe();
            mGetApplyListBehavior=null;
        }
        if( mSkPowerBehavior!=null){
            mSkPowerBehavior.unSubscribe();
            mSkPowerBehavior=null;
        }
        if( mSpWatchApplyBehavior!=null){
            mSpWatchApplyBehavior.unSubscribe();
            mSpWatchApplyBehavior=null;
        }
    }

    public static CacheBehaviorFactory getCacheBehaviorFactory() {
        return cacheBehaviorFactory;
    }

    // TODO: 2020-06-13  setApplying(boolean isApplying)
    public static void  setApplying(boolean isApplying, Activity activity){
        L.e(TAG,"---activity---"+activity);
        final ApplyQuequeBehavior applyQuequeBehavior= CacheBehaviorFactory.getInstance().getApplyQueBehavior((FragmentActivity)activity);
        if(applyQuequeBehavior!=null){
           applyQuequeBehavior.setApplying(isApplying);
        }
    }
}
