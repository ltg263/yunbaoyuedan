package com.yunbao.common.event;

import com.yunbao.common.bean.LiveBean;

/**
 * Created by Sky.L on 2020-09-28
 */
public class ShowLiveRoomFloatEvent {
    private LiveBean mLiveBean;

    public ShowLiveRoomFloatEvent(LiveBean liveBean) {
        mLiveBean = liveBean;
    }


    public LiveBean getLiveBean() {
        return mLiveBean;
    }

    public void setLiveBean(LiveBean liveBean) {
        mLiveBean = liveBean;
    }
}
