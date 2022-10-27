package com.yunbao.common.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.interfaces.LifeCycleListener;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.L;

/**
 * Created by cxf on 2018/9/22.
 */

public abstract class AbsViewHolder2 implements LifeCycleListener {

    private String mTag;
    protected Context mContext;
    protected ViewGroup mParentView;
    protected View mContentView;
    private boolean isDestoryed;
    protected FragmentActivity mFragmentActivity;
    protected LifecycleProvider<ActivityEvent> mLifecycleProvider;
    public AbsViewHolder2(Context context, ViewGroup parentView) {
        mTag = getClass().getSimpleName();
//        L.e("当前AbsViewHolder="+this.getClass().getName());
        mContext = context;
        if(mContext instanceof FragmentActivity){
           mFragmentActivity= (FragmentActivity) mContext;
        }
        if(mContext instanceof LifecycleProvider){
           mLifecycleProvider= (LifecycleProvider<ActivityEvent>) mContext;
        }
        mParentView = parentView;
        mContentView = LayoutInflater.from(context).inflate(getLayoutId(), mParentView, false);
        init();
    }
    public AbsViewHolder2(Context context, ViewGroup parentView, Object... args) {
        mTag = getClass().getSimpleName();
        processArguments(args);
        mContext = context;
        mParentView = parentView;
        if(mContext instanceof FragmentActivity){
            mFragmentActivity= (FragmentActivity) mContext;
        }
        if(mContext instanceof LifecycleProvider){
            mLifecycleProvider= (LifecycleProvider<ActivityEvent>) mContext;
        }
        mContentView = LayoutInflater.from(context).inflate(getLayoutId(), mParentView, false);
        init();
    }
    protected void processArguments(Object... args) {

    }
    protected abstract int getLayoutId();

    public abstract void init();

    protected <T extends View> T findViewById(int res) {
        return mContentView.findViewById(res);
    }

    public View getContentView() {
        return mContentView;
    }

    protected boolean canClick() {
        return ClickUtil.canClick();
    }

    public void addToParent() {
        if (mParentView != null && mContentView != null) {
            mParentView.addView(mContentView);
        }
    }
    public void addToParent(int index) {
        if (mParentView != null && mContentView != null) {
            mParentView.addView(mContentView,index);
        }
    }

    public void removeFromParent() {
        ViewParent parent = mContentView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(mContentView);
        }
    }


    /**
     * 订阅Activity的生命周期
     */
    public void subscribeActivityLifeCycle() {
        if(mContext instanceof AbsActivity){
           ((AbsActivity)mContext).addLifeCycleListener(this);
        }
    }


    /**
     * 取消订阅Activity的生命周期
     */
    public void unSubscribeActivityLifeCycle() {
        if(mContext instanceof AbsActivity){
            ((AbsActivity)mContext).removeLifeCycleListener(this);
        }
    }


    public  <T extends Activity> void startActivity(Class<T> cs,int...flags) {
        if(mContext!=null&&cs!=null){
            Intent intent=new Intent(mContext,cs);
            if (flags!=null){
                for(int flag:flags){
                    intent.addFlags(flag);
                }
            }
            mContext.startActivity(intent);
        }
    }

    public FragmentActivity getActivity(){
        return (FragmentActivity) mContext;
    }

    public void setOnClickListner(View containView,int id,View.OnClickListener onClickListner){
        if(containView==null){
            return;
        }
        View view=containView.findViewById(id);
        if(view!=null){
           view.setOnClickListener(onClickListner);
        }
    }

    public void setOnClickListner(int id,View.OnClickListener onClickListner){
        View view=findViewById(id);
        if(view!=null){
            view.setOnClickListener(onClickListner);
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        L.e(mTag, "release-------->");
    }

    @Override
    public void onCreate() {
        L.e(mTag, "lifeCycle-----onCreate----->");
    }

    @Override
    public void onStart() {
        L.e(mTag, "lifeCycle-----onStart----->");
    }

    @Override
    public void onReStart() {
        L.e(mTag, "lifeCycle-----onReStart----->");
    }

    @Override
    public void onResume() {
        L.e(mTag, "lifeCycle-----onResume----->");
    }

    @Override
    public void onPause() {
        L.e(mTag, "lifeCycle-----onPause----->");
    }

    @Override
    public void onStop() {
        L.e(mTag, "lifeCycle-----onStop----->");
    }

    @Override
    public void onDestroy() {
        L.e(mTag, "lifeCycle-----onDestroy----->");
        mContext=null;
        isDestoryed=true;
        mFragmentActivity=null;
    }


    public boolean isDestoryed() {
        return isDestoryed;
    }


    public void  setBackGroundColor(int backGroundColor){
        if(mContentView!=null){
            mContentView.setBackgroundColor(backGroundColor);
        }
    }

    public void finish(){
        if(mFragmentActivity!=null){
           mFragmentActivity.finish();
        }
    }
}
