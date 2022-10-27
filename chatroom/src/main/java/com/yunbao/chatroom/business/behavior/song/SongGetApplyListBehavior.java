package com.yunbao.chatroom.business.behavior.song;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.chatroom.bean.ApplyResult;
import com.yunbao.chatroom.business.behavior.GetApplyListBehavior;
import com.yunbao.chatroom.business.socket.song.SongSocketProxy;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;

import io.reactivex.Observable;

public class SongGetApplyListBehavior extends GetApplyListBehavior<SongSocketProxy> {

    @Override
    public Observable<ApplyResult> getApplyList(LifecycleProvider lifecycleProvider, Object...args) {
        String stream=mLiveBean==null?null:mLiveBean.getStream();
        String liveUid=mLiveBean==null?null:mLiveBean.getUid();
        return ChatRoomHttpUtil.getApplyList(liveUid,stream, (Integer) args[0]).compose(lifecycleProvider.<ApplyResult>bindToLifecycle());
    }
}
