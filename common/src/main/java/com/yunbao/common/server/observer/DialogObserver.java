package com.yunbao.common.server.observer;

import android.app.Dialog;
import android.content.Context;
import com.yunbao.common.utils.DialogUitl;
import io.reactivex.disposables.Disposable;

public abstract class DialogObserver<T> extends DefaultObserver<T> {
    private Context context;
    private Dialog dialog;
    public DialogObserver(Context context){
        this.context=context;
    }
    @Override
    public void onSubscribe(Disposable d) {
        showDialog();
    }

    protected  void showDialog(){
        dialog=DialogUitl.loadingDialog(context);
        if(dialog!=null) {
            dialog.show();
        }
    }
    @Override
    public void onComplete() {
        super.onComplete();
        disMissDialog();
    }
    @Override
    public void onError(Throwable e) {
        super.onError(e);
        disMissDialog();
    }

    protected  void disMissDialog(){
        if(dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
        }
        dialog=null;
        context=null;
    }
}
