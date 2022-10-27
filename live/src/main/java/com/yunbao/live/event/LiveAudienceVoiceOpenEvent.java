package com.yunbao.live.event;

import com.yunbao.live.bean.LiveBeanReal;

public class LiveAudienceVoiceOpenEvent {

    private LiveBeanReal mLiveBean;

    public LiveAudienceVoiceOpenEvent(LiveBeanReal liveBean) {
        mLiveBean = liveBean;
    }

    public LiveBeanReal getLiveBean() {
        return mLiveBean;
    }

    public void setLiveBean(LiveBeanReal liveBean) {
        mLiveBean = liveBean;
    }
}
