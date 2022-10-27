package com.yunbao.im.business;


import com.yunbao.common.utils.L;
import com.yunbao.common.utils.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class TimeModel {
    public static final int MAXIMUM = 1000000000;
    private static TimeModel timeModel = null;
    private Disposable disposable;
    private Parser parser;
    private List<TimeListner> timeListnerList;
    private String time;
    private static final String TAG = "TimeModel";
    private boolean mFirstTiming;//第一次 开始计时


    private TimeModel() {
        parser = new Parser();
        timeListnerList = new ArrayList<>();
    }

    public static TimeModel getInstance() {
        if (timeModel == null) {
            synchronized (TimeModel.class) {
                timeModel = new TimeModel();
            }
        }
        return timeModel;
    }

    //目前进入房间回调成功的时候开始进行计时
    public void start() {
        dispose();
        disposable = Observable.interval(0, 1000, TimeUnit.MILLISECONDS).take(MAXIMUM).
                subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        time = parser.parse(aLong);
                        if (mFirstTiming && !"00:00:00".equals(parser.parse(aLong))){
                            mFirstTiming = !mFirstTiming;
                            time = "00:00:00";
                        }
                        if (timeListnerList != null) {
                            for (TimeListner timeListner : timeListnerList) {
                                timeListner.time(time);
                            }
                        }
                    }
                });
    }

    public void addTimeListner(TimeListner timeListner) {
        if (timeListnerList == null) {
            timeListnerList = new ArrayList<>();
        }
        timeListnerList.add(timeListner);
    }

    public void removeTimeListner(TimeListner timeListner) {
        if (timeListner == null || timeListnerList == null) {
            return;
        }
        L.e(TAG, "removeTimeListner----->" + timeListnerList);
        timeListnerList.remove(timeListner);
    }


    public void clear() {
        dispose();
        L.e(TAG, "clear----->");
        time = null;
        mFirstTiming = true;
        if (timeListnerList != null) {
            timeListnerList.clear();
            timeListnerList = null;
        }
    }

    public String getTime() {
        return time;
    }

    public interface TimeListner {
        public void time(String string);
    }

    private void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            L.e(TAG, "dispose---->");
            disposable.dispose();
            disposable = null;
            mFirstTiming = true;
        }
    }
}
