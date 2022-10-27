package com.yunbao.main.business;

import com.yunbao.common.utils.L;
import com.yunbao.common.utils.Parser;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class OrderCutDownModel  {
    public static final int MAXIMUM=1000000000;
    private Disposable disposable;
    private Parser parser;
    private TimeListner timeListner;

    public OrderCutDownModel(){
        parser=new Parser();
    }

    public  interface TimeListner{
        public void time(StringBuilder string);
        public void compelete();
    }

    public void start(final long time){
        dispose();
        if(time<=0){
          return;
        }
        L.e("time=="+time);
        final long totalTime=time;
        disposable= Observable.interval(0, 1000, TimeUnit.MILLISECONDS).take(MAXIMUM).
                subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        StringBuilder stringBuilder= parser.parseToDayHourMinute(totalTime-aLong);
                        if(timeListner!=null){
                            if(totalTime==aLong){
                                timeListner.compelete();
                            }
                           timeListner.time(stringBuilder);
                        }
                    }
                });

    }


    public void release(){
        dispose();
        timeListner=null;
    }
    private void dispose() {
        if(disposable!=null&&!disposable.isDisposed()){
            disposable.dispose();
        }
    }

    public void setTimeListner(TimeListner timeListner) {
        this.timeListner = timeListner;
    }
}
