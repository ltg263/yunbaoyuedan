package com.yunbao.common.server.observer;

import com.yunbao.common.utils.L;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/*create by chenfangwei
 * */

public   abstract class DefaultObserver<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {

    }


    @Override
    public void onError(Throwable e) {
        if(e!=null){
           // ToastUtil.show(e.getMessage());
            L.e("网络请求错误=="+e.getMessage());
        }
    }

    @Override
    public void onComplete() {

    }
}
