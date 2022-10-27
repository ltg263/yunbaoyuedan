package com.yunbao.chatroom.business.socket.dispatch.mannger;

import android.support.annotation.NonNull;
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
import com.yunbao.chatroom.business.socket.dispatch.callback.WheatLisnter;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import java.util.ArrayList;
import java.util.List;

/*派单上麦下麦相关的管理器*/
public class WheatMannger<T extends WheatLisnter> extends SocketManager {
    /* _method_ ：linkmicaction： 1.申请boss麦;2.取消申请boss麦;3控制4控制下麦5主动下麦6上麦抢单 7拒绝*/
    protected static final int APPLY_BOSS_WHEAT=1;
    protected static final int CANCLE_APPLY_BOSS_WHEAT=2;
    protected static final int CONTROL_UP_BOSS_WHEAT=3;
    protected static final int CONTROL_DOWN_WHEAT=4;
    protected static final int SELF_DOWN_WHEAT=5;
    protected static final int UP_NORMAL_WHEAT=6;
    protected static final int REFUSE_BOSS_WHEAT=7;
    protected List<T>mWheatLisnterList;

    public WheatMannger(ILiveSocket iLiveSocket,@NonNull T wheatLisnter) {
        super(iLiveSocket);
        mWheatLisnterList=new ArrayList<>(2);
        mWheatLisnterList.add(wheatLisnter);
    }
    @Override
    public void handle(JSONObject jsonObject) {
        if(mWheatLisnterList==null||jsonObject==null){
            return;
        }
        int action=getAction(jsonObject);
        String uid=jsonObject.getString("uid");
        UserBean userBean=null;
        switch (action) {
            case APPLY_BOSS_WHEAT:      //申请上老板麦
                for(WheatLisnter wheatLisnter:mWheatLisnterList){
                    wheatLisnter.applyBosssWheat(uid,true);
                }
                break;
            case CANCLE_APPLY_BOSS_WHEAT:  //取消上老板麦的申请
                for(WheatLisnter wheatLisnter:mWheatLisnterList){
                    wheatLisnter.applyBosssWheat(uid,false);
                }
                break;
            case CONTROL_UP_BOSS_WHEAT: //主持人同意上老板麦的申请
                onBossWheat(jsonObject);
                break;
            case UP_NORMAL_WHEAT:       //上普通抢单麦，这个不需要主持人同意
                onUpNormalWheat(jsonObject);
                break;
            case REFUSE_BOSS_WHEAT:    //主持人拒绝上老板麦的申请
                onRefuseBossWheat(SocketSendBean.parseToUserBean(jsonObject).getId());
                break;
            case SELF_DOWN_WHEAT:      //观众自主下麦
                userBean=SocketSendBean.parseToUserBean(jsonObject);
                onDownWheat(userBean,true);
                break;
            case CONTROL_DOWN_WHEAT:   //主播控制用户下麦
                userBean=SocketSendBean.parseToUserBean(jsonObject);
                onDownWheat(userBean,false);
                break;
            default:
        }
    }

    /*----------------------接收socket部分----------------------------------*/

    /*接受到拒绝信号*/
    private void onRefuseBossWheat(String uid) {
        if(mWheatLisnterList==null){
            return;
        }
        for(WheatLisnter wheatLisnter:mWheatLisnterList){
            wheatLisnter.resfuseUpWheat(uid);
        }
    }

    /*接受成功上老板麦socket*/
    private void onBossWheat(JSONObject jsonObject) {
       UserBean userBean= SocketSendBean.parseToUserBean(jsonObject);
        for(WheatLisnter wheatLisnter:mWheatLisnterList){
            wheatLisnter.upBossWheatSuccess(userBean);
        }
    }

    /*接受成功上普通抢单麦socket*/
    private void onUpNormalWheat(JSONObject jsonObject) {
        UserBean userBean=SocketSendBean.parseUserBean(jsonObject);
        int position=jsonObject.getIntValue(Constants.KEY_POSITON);
        for(WheatLisnter wheatLisnter:mWheatLisnterList){
            wheatLisnter.upNormalWheatSuccess(userBean,position);
        }
    }
    /*下麦*/
    private void onDownWheat(UserBean userBean, boolean isSelf) {
        if(mWheatLisnterList==null|| userBean==null){
            return;
        }

        for(WheatLisnter wheatLisnter:mWheatLisnterList){
            wheatLisnter.downWheat(userBean,isSelf);
        }
    }

    /*----------------------接收socket部分----------------------------------*/


    /*----------------------观众处理部分----------------------------------*/

    /*发送主动下麦的socket*/
    public void sendSocketSelfDownWheat(){
        mILiveSocket.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_LINKMIC)
                .param("action", SELF_DOWN_WHEAT)
                .param(CommonAppConfig.getInstance().getUserBean()));
    }


    /*申请上老板麦*/
    public void applyBossUpWheat(String liveUid, String stream, LifecycleProvider lifecycleProvider, final SuccessListner successListner){
        ChatRoomHttpUtil.applyLinkmic(liveUid,stream).compose(lifecycleProvider.<BaseResponse<JSONObject>>bindToLifecycle()).subscribe(new DefaultObserver<BaseResponse>() {
            @Override
            public void onNext(BaseResponse baseResponse) {
                Data data=baseResponse.getData();
                ToastUtil.show(data.getMsg());
                int code=data.getCode();
                if (data != null && data.getCode() == 700){
                    RouteUtil.forwardLoginInvalid(data.getMsg());
                }
                if(code==0||code==1002){
                  sendBossJudgeUpWheat(true);
                  successListner.success();
                }
            }
        });
    }


    /*取消老板连麦*/
    public void cancleBossUpWheat(String liveUid, String stream,LifecycleProvider lifecycleProvider,View view,final SuccessListner successListner){
        ChatRoomHttpUtil.cancelLinkmic(liveUid,stream).compose(lifecycleProvider.<Boolean>bindToLifecycle()).subscribe(new LockClickObserver<Boolean>(view) {
            @Override
            public void onSucc(Boolean aBoolean) {
                if(aBoolean){
                    sendBossJudgeUpWheat(false);
                    successListner.success();
                }
            }
        });
    }

    /*发送申请还是取消上麦老板信息*/
    private void sendBossJudgeUpWheat(boolean isUP) {
        int action=isUP?APPLY_BOSS_WHEAT:CANCLE_APPLY_BOSS_WHEAT;
        mILiveSocket.send(new SocketSendBean()
                .param("_method_", Constants.SOCKET_LINKMIC)
                .param("action", action)
                .param("uid", CommonAppConfig.getInstance().getUid()));
    }

    /*申请上普通麦*/
    public void upNormalWheat(String liveUid,String stream,LifecycleProvider lifecycleProvider,View view){
        ChatRoomHttpUtil.upNormalMic(liveUid,stream).compose(lifecycleProvider.<Integer>bindToLifecycle()).subscribe(new LockClickObserver<Integer>(view) {
            @Override
            public void onSucc(Integer position) {
                if(position!=0){
                   sendSocketUpNormalWheat(position);
                }
            }
        });
    }


    /*用户上普通的抢单麦发送socket*/
    private void sendSocketUpNormalWheat(int position) {
        UserBean userBean=CommonAppConfig.getInstance().getUserBean();
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_LINKMIC)
                .param("action", UP_NORMAL_WHEAT)
                .param(userBean)
                .param(Constants.KEY_POSITON,position)
        );
    }

    /*----------------------观众处理部分----------------------------------*/


    /*----------------------主播控制的部分----------------------------------*/
    /*同意上老板麦*/
    public void agreeUserUpBossWheat(final UserBean userBean, String stream, View view,LifecycleProvider lifecycleProvider,final SuccessListner successListner){
        ChatRoomHttpUtil.setUserUpMic(userBean.getId(),stream).compose(lifecycleProvider.<Boolean>bindToLifecycle()).subscribe(new LockClickObserver<Boolean>(view) {
            @Override
            public void onSucc(Boolean aBoolean) {
                if(aBoolean){
                    sendSocketHostAgreeBossUpWheat(userBean);
                    if(successListner!=null){
                       successListner.success();
                    }
                }
            }
        });
    }

    /*拒绝上老板麦的申请*/
    public void refuseUserUpBossWheat(final UserBean userBean){
        sendSocketHostRefuseBossUpWheat(userBean);
    }

    /*主播发送同意上老板麦申请*/
    private void sendSocketHostAgreeBossUpWheat(UserBean userBean) {
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_LINKMIC)
        .param("action", CONTROL_UP_BOSS_WHEAT)
        .param(Constants.KEY_POSITON, 8)
         .paramToUser(userBean)
        );
    }

    /*主播发送拒绝上老板麦申请*/
    private void sendSocketHostRefuseBossUpWheat(UserBean userBean) {
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_LINKMIC)
                .param("action", REFUSE_BOSS_WHEAT)
                .paramToUser(userBean)
        );
    }

    /*控制下麦*/
    public void controllDownWheat(UserBean userBean,int sitId){
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_LINKMIC)
                .param("action", CONTROL_DOWN_WHEAT)
                .paramToUser(userBean)
                .param("sitid",sitId)
        );
    }

    /*----------------------主播控制的部分----------------------------------*/

    /*添加监听*/
    public void addWheatListner(@NonNull  T wheatLisnter){
        if(mWheatLisnterList==null){
            mWheatLisnterList=new ArrayList<>();
        }
        mWheatLisnterList.add(wheatLisnter);
    }

    /*移除监听*/
    public void removeWheatListner(@NonNull  WheatLisnter wheatLisnter){
        if(mWheatLisnterList==null||wheatLisnter==null){
            return;
        }
        mWheatLisnterList.remove(wheatLisnter);
    }

    @Override
    public void release() {
        super.release();
        mWheatLisnterList=null;
    }
}
