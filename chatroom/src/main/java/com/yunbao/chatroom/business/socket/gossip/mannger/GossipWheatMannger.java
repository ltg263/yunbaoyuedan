package com.yunbao.chatroom.business.socket.gossip.mannger;

import android.view.View;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.server.entity.BaseResponse;
import com.yunbao.common.server.entity.Data;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.server.observer.LockClickObserver;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.chatroom.bean.SocketSendBean;
import com.yunbao.chatroom.business.socket.ILiveSocket;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.base.mannger.SocketManager;
import com.yunbao.chatroom.business.socket.gossip.callback.GossipWheatLisnter;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;

public class GossipWheatMannger extends SocketManager {
    private static final int APPLY_WHEAT=1; //申请上麦
    private static final int APPLY_CANCLE_WHEAT=2; //取消上麦
    private static final int CONTROL_UP_WHEAT=3; //主持人同意申请上麦
    private static final int CONTROL_DOWN_WHEAT=4; //主持人控制下麦
    private static final int DOWN_WHEAT=5; //用户主动下麦
    private static final int REFUSE_WHEAT=7; //拒绝上麦
    private List<GossipWheatLisnter> mGossipWheatLisnterList;
    public GossipWheatMannger(ILiveSocket liveSocket,GossipWheatLisnter gossipWheatLisnter) {
        super(liveSocket);
        addGossipWheatLisnter(gossipWheatLisnter);
    }

    public void addGossipWheatLisnter(GossipWheatLisnter gossipWheatLisnter) {
        if(mGossipWheatLisnterList==null){
           mGossipWheatLisnterList=new ArrayList<>(2);
        }
        if(gossipWheatLisnter==null||mGossipWheatLisnterList.contains(gossipWheatLisnter)){
           return;
       }
        mGossipWheatLisnterList.add(gossipWheatLisnter);
    }

    public void removeGossipWheatLisnter(GossipWheatLisnter gossipWheatLisnter){
        if(gossipWheatLisnter==null||mGossipWheatLisnterList==null){
            return;
        }
        mGossipWheatLisnterList.remove(gossipWheatLisnter);
    }

    @Override
    public void handle(JSONObject jsonObject) {
        if(mGossipWheatLisnterList==null||jsonObject==null){
            return;
        }
        int action=getAction(jsonObject);
        String uid=jsonObject.getString("uid");
        UserBean userBean=null;
        switch (action) {
            case APPLY_WHEAT:
                for (GossipWheatLisnter gossipWheatLisnter:mGossipWheatLisnterList){
                    gossipWheatLisnter.applyWheat(uid,true);
                }
                break;
            case APPLY_CANCLE_WHEAT:
                for (GossipWheatLisnter gossipWheatLisnter:mGossipWheatLisnterList){
                    gossipWheatLisnter.applyWheat(uid,false);
                }
                break;
            case CONTROL_UP_WHEAT:
               userBean= SocketSendBean.parseToUserBean(jsonObject);
                int position=jsonObject.getIntValue(Constants.KEY_POSITON);
                for (GossipWheatLisnter gossipWheatLisnter:mGossipWheatLisnterList){
                    gossipWheatLisnter.argreeUpWheat(userBean,position);
                }
                break;
            case CONTROL_DOWN_WHEAT:
                userBean= SocketSendBean.parseToUserBean(jsonObject);
                for (GossipWheatLisnter gossipWheatLisnter:mGossipWheatLisnterList){
                    gossipWheatLisnter.downWheat(userBean,false);
                }
                break;
            case DOWN_WHEAT:
                userBean= SocketSendBean.parseUserBean(jsonObject);
                for (GossipWheatLisnter gossipWheatLisnter:mGossipWheatLisnterList){
                    gossipWheatLisnter.downWheat(userBean,true);
                }
                break;
            case REFUSE_WHEAT:
                userBean= SocketSendBean.parseToUserBean(jsonObject);
                for (GossipWheatLisnter gossipWheatLisnter:mGossipWheatLisnterList){
                    gossipWheatLisnter.refuseUpWheat(userBean);
                }
                break;
        }
    }
    /*申请上麦*/
    public void applyUpWheat(String liveUid, String stream, LifecycleProvider lifecycleProvider, final SuccessListner successListner){
        requestHttpApplyUpWheat(liveUid,stream).compose(lifecycleProvider.<BaseResponse<JSONObject>>bindToLifecycle()).subscribe(new DefaultObserver<BaseResponse>() {
            @Override
            public void onNext(BaseResponse baseResponse) {
                Data data=baseResponse.getData();
                ToastUtil.show(data.getMsg());
                int code=data.getCode();
                if (data != null && data.getCode() == 700){
                    RouteUtil.forwardLoginInvalid(data.getMsg());
                }
                if(code==0||code==1002){
                    sendApplyUpWheat(true);
                    successListner.success();
                }
            }
        });
    }

    protected Observable<BaseResponse<JSONObject>> requestHttpApplyUpWheat(String liveUid, String stream){
        return ChatRoomHttpUtil.gossipChatApply(liveUid,stream);
    }

    /*取消上麦申请*/
    public void cancleApplyUpWheat(String liveUid, String stream, LifecycleProvider lifecycleProvider, View view, final SuccessListner successListner){
        requestHttpCancleApplyUpWheat(liveUid,stream).compose(lifecycleProvider.<Boolean>bindToLifecycle()).subscribe(new LockClickObserver<Boolean>(view) {
            @Override
            public void onSucc(Boolean aBoolean) {
                if(aBoolean){
                    sendApplyUpWheat(false);
                    successListner.success();
                }
            }
        });
    }

    protected Observable<Boolean> requestHttpCancleApplyUpWheat(String liveUid, String stream){
        return ChatRoomHttpUtil.gossipChatCancel(liveUid,stream);
    }


    /*发送socket 申请还是取消上麦*/
    private void sendApplyUpWheat(boolean isApply) {
        int action=isApply?APPLY_WHEAT:APPLY_CANCLE_WHEAT;
        mILiveSocket.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_LINKMIC)
                .param("action", action)
                .param("uid", CommonAppConfig.getInstance().getUid()));
    }

    /*发送主动下麦的socket*/
    public void sendSocketSelfDownWheat(){
        mILiveSocket.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_LINKMIC)
                .param("action", DOWN_WHEAT)
                .param(CommonAppConfig.getInstance().getUserBean()));
    }


    /*----------------------主持人控制的部分----------------------------------*/

    /*主持人同意上麦*/
    public void agreeUserUpWheat(final UserBean userBean, String stream, View view,LifecycleProvider lifecycleProvider,final SuccessListner successListner){
        requestHttpAgreeUserUpWheat(userBean.getId(),stream).compose(lifecycleProvider.<Boolean>bindToLifecycle()).subscribe(new LockClickObserver<JSONObject>(view){
            @Override
            public void onSucc(JSONObject jsonObject) {
                int position=jsonObject.getIntValue(Constants.KEY_POSITON);
                if(position==0){
                    position=1;
                }
                sendSocketHostAgreeUpWheat(userBean,position);
                if(successListner!=null){
                  successListner.success();
                }
            }
        });
    }

    protected Observable<JSONObject> requestHttpAgreeUserUpWheat(String liveUid, String stream){
        return ChatRoomHttpUtil.gossipChatSetMic(liveUid,stream);
    }

    /*主持人发送socket同意上麦请求*/
    private void sendSocketHostAgreeUpWheat(UserBean userBean,int position) {
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_LINKMIC)
                .param("action", CONTROL_UP_WHEAT)
                .param(Constants.KEY_POSITON,position)
                .paramToUser(userBean)
        );
    }

    /*主持人控制下麦*/
    public void controlDownWheat(UserBean userBean,int sitId){
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_LINKMIC)
                .param("action", CONTROL_DOWN_WHEAT)
                .paramToUser(userBean)
                .param("sitid",sitId)
        );
    }

    /*主持人发送拒绝上麦申请*/
    public void sendSocketHostRefuseBossUpWheat(UserBean userBean) {
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_LINKMIC)
                .param("action", REFUSE_WHEAT)
                .paramToUser(userBean)
        );
    }

    @Override
    public void release() {
        super.release();
        mGossipWheatLisnterList=null;
    }
}
