package com.yunbao.chatroom.business.behavior;

import android.view.View;

import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.chatroom.bean.LiveEndResultBean;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.chatroom.ui.activity.LiveActivity;

/*主播结束直播*/
public class StopLiveBehavior extends BaseBehavior {
    public void stopLive(LiveBean liveBean,final LiveActivity liveActivity){
           if(liveBean!=null&&liveActivity!=null)
            ChatRoomHttpUtil.endRoom(liveBean.getStream()).subscribe(new DefaultObserver<LiveEndResultBean>() {
                @Override
                public void onNext(LiveEndResultBean liveEndResultBean) {
                    liveActivity.showLiveEndViewHolder(liveEndResultBean, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          liveActivity.finish();
                        }
                    });
                }
            });

    }
}
