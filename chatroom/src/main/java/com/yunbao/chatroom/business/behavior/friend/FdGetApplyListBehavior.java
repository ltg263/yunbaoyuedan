package com.yunbao.chatroom.business.behavior.friend;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.chatroom.bean.ApplyResult;
import com.yunbao.chatroom.business.behavior.GetApplyListBehavior;
import com.yunbao.chatroom.business.socket.friend.FriendSocketProxy;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;

import io.reactivex.Observable;

public class FdGetApplyListBehavior extends GetApplyListBehavior<FriendSocketProxy> {

    @Override
    public Observable<ApplyResult> getApplyList(LifecycleProvider lifecycleProvider, Object...args) {
        String stream=mLiveBean==null?null:mLiveBean.getStream();
        String liveUid=mLiveBean==null?null:mLiveBean.getUid();
        return ChatRoomHttpUtil.getFriendgApplyList(liveUid,stream, (Integer) args[0],(Integer) args[1]).compose(lifecycleProvider.<ApplyResult>bindToLifecycle());
    }
}