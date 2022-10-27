package com.yunbao.chatroom.ui.view.apply;

import android.content.Context;
import android.view.ViewGroup;
import com.yunbao.common.views.AbsViewHolder2;

public abstract class AbsApplyHostViewHolder extends AbsViewHolder2 {
    private OnChangeStateListner mOnChangeStateListner;
    public static final int NOAPPLY=-1;   //没有申请
    public static final int APPLYING=3; //正在申请页面
    public static final int REVIEW_ING=0; //审核中
    public static final int REVIEW_ERROR=2; //审核失败
    public static final int REVIEW_SUCCESS=1;//审核成功

    public AbsApplyHostViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    public AbsApplyHostViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    public void notifyState(int state){
        if(mOnChangeStateListner!=null){
            mOnChangeStateListner.change(state);
        }
    }

    public void notifyRefresh(){
        if(mOnChangeStateListner!=null){
            mOnChangeStateListner.refresh();
        }
    }

    public void setOnChangeStateListner(OnChangeStateListner onChangeStateListner) {
        mOnChangeStateListner = onChangeStateListner;
    }

    public interface OnChangeStateListner{
        public void change(int state);
        public void refresh();
    }

    @Override
    public void addToParent() {
        if (mParentView != null && mContentView != null&&!isContain()) {
            mParentView.addView(mContentView);
        }
    }

    private boolean isContain() {
       int childCount= mParentView.getChildCount();
       boolean isContain=false;
       for(int i=0;i<childCount;i++){
           if(mParentView.getChildAt(i)==mContentView){
               isContain=true;
               return isContain;
           }
       }
       return isContain;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mOnChangeStateListner=null;
    }
}
