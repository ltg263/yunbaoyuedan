package com.yunbao.live.event;

import com.yunbao.live.bean.LiveBeanReal;

public class LiveAudienceVoiceExitEvent {

    private LiveBeanReal mLiveBean;

    public LiveAudienceVoiceExitEvent(LiveBeanReal liveBean) {
        mLiveBean = liveBean;
    }

    public LiveBeanReal getLiveBean() {
        return mLiveBean;
    }

    public void setLiveBean(LiveBeanReal liveBean) {
        mLiveBean = liveBean;
    }
}
