package com.yunbao.chatroom.business.behavior;

import android.view.View;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.SystemUtil;
import com.yunbao.chatroom.bean.LiveEndResultBean;
import com.yunbao.chatroom.business.live.presenter.ILivePresenter;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.chatroom.ui.activity.LiveActivity;

/*观众退出直播*/
public class EndLiveBehavior extends BaseBehavior {
    public void  endLive(final LiveActivity activity,final  ILivePresenter livePresenter){
        if(activity==null||livePresenter==null){
            return;
        }
        SystemUtil.disMissAllDialog(activity);
        if(mLiveBean!=null) {
            ChatRoomHttpUtil.getLiveEndInfo(mLiveBean.getStream()).compose(activity.<LiveEndResultBean>bindToLifecycle())
             .subscribe(new DefaultObserver<LiveEndResultBean>() {
                 @Override
                 public void onNext(LiveEndResultBean liveEndResultBean) {
                     activity.showLiveEndViewHolder(liveEndResultBean, new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             if(livePresenter!=null){
                                 livePresenter.exitRoom();
                             }
                         }
                     });
                 }
             });
        }
        }
    }
