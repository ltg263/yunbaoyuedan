package com.yunbao.chatroom.business.socket.friend.mannger;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.server.entity.BaseResponse;
import com.yunbao.chatroom.business.socket.ILiveSocket;
import com.yunbao.chatroom.business.socket.gossip.callback.GossipWheatLisnter;
import com.yunbao.chatroom.business.socket.gossip.mannger.GossipWheatMannger;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import io.reactivex.Observable;

public class FriendWheatMannger extends GossipWheatMannger {

    public FriendWheatMannger(ILiveSocket liveSocket, GossipWheatLisnter gossipWheatLisnter) {
        super(liveSocket, gossipWheatLisnter);
    }
    @Override
    protected Observable<BaseResponse<JSONObject>> requestHttpApplyUpWheat(String liveUid, String stream) {
        return ChatRoomHttpUtil.friendApply(liveUid,stream);
    }
    @Override
    protected Observable<Boolean> requestHttpCancleApplyUpWheat(String liveUid, String stream) {
        return ChatRoomHttpUtil.friendCancel(liveUid,stream);
    }
    @Override
    protected Observable<JSONObject> requestHttpAgreeUserUpWheat(String liveUid, String stream) {
        return ChatRoomHttpUtil.friendSetMic(liveUid,stream);
    }
}
