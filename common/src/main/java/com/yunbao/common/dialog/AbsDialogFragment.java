package com.yunbao.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import com.trello.rxlifecycle2.components.support.RxDialogFragment;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.L;

import java.lang.ref.WeakReference;

/**
 * Created by cxf on 2018/9/29.
 */

public abstract class AbsDialogFragment extends RxDialogFragment {

    protected Context mContext;
    protected View mRootView;
    private DissMissLisnter mDissMissLisnter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = new WeakReference<>(getActivity()).get();
        mRootView = LayoutInflater.from(mContext).inflate(getLayoutId(), null);
        Dialog dialog =null;
        if(isNeedDialogListner()){
            dialog= new Dialog(mContext, getDialogStyle());
        }else{
            dialog= createNoListnerDialog();
        }
        dialog.setContentView(mRootView);
        dialog.setCancelable(canCancel());
        dialog.setCanceledOnTouchOutside(canCancel());
        setWindowAttributes(dialog.getWindow());
        return dialog;
    }

    private Dialog createNoListnerDialog() {
        return new Dialog(mContext,getDialogStyle()){
            @Override
            public void setOnDismissListener(@Nullable OnDismissListener listener) {
            }
            @Override
            public void setOnCancelListener(@Nullable OnCancelListener listener) {
            }
            @Override
            public void setOnShowListener(@Nullable OnShowListener listener) {
            }
        };
    }

    public boolean isNeedDialogListner(){
        return true;
  }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }
    public void init(){

    }


    public void show(FragmentManager manager) {
         super.show(manager,getName());
    }

    public String getName(){
        return this.getClass().getName();
    }

    protected abstract int getLayoutId();


    protected abstract  int getDialogStyle();

    protected abstract boolean canCancel();

    protected abstract void setWindowAttributes(Window window);

    protected <T extends View>T findViewById(int id) {
        if (mRootView != null) {
            return mRootView.findViewById(id);
        }
        return null;
    }
    public void initData() {
    }

    public void setDissMissLisnter(DissMissLisnter dissMissLisnter) {
        mDissMissLisnter = dissMissLisnter;
    }

    public void setOnClickListener(int id, View.OnClickListener clickListener){
        View view=findViewById(id);
        if(view!=null){
            view.setOnClickListener(clickListener);
        }
    }
    protected boolean canClick() {
        return ClickUtil.canClick();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(mDissMissLisnter!=null){
            mDissMissLisnter.close();
            mDissMissLisnter=null;
        }
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext=null;
        L.e("dialog销毁了=="+this.getName());
    }

    public static interface  DissMissLisnter{
        public void close();
    }
}
