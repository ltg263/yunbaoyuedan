package com.yunbao.chatroom.business.socket.song.mannger;

import android.support.annotation.NonNull;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.chatroom.bean.SocketSendBean;
import com.yunbao.chatroom.business.socket.ILiveSocket;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.dispatch.mannger.WheatMannger;
import com.yunbao.chatroom.business.socket.song.callback.SongWheatListner;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;

public class SongWheatMannger extends WheatMannger<SongWheatListner> {
    protected static final int SINGER_APPLY=8; //申请歌手麦
    protected static final int REFUSE_SINGER_APPLY=9; //拒绝歌手麦
    protected static final int AGREE_SINGER_APPLY=10; //同意歌手麦

    public SongWheatMannger(ILiveSocket iLiveSocket, @NonNull SongWheatListner wheatLisnter) {
        super(iLiveSocket, wheatLisnter);
    }

    @Override
    public void handle(JSONObject jsonObject) {
        super.handle(jsonObject);
        int action=getAction(jsonObject);
        switch (action) {
            case SINGER_APPLY:
            applySinger(jsonObject);
                break;
            case REFUSE_SINGER_APPLY:
               applySingerResult(jsonObject,false);
                break;
            case AGREE_SINGER_APPLY:
                applySingerResult(jsonObject,true);
                break;
        }
    }

    private void applySingerResult(JSONObject jsonObject,boolean isAgree) {
        UserBean userBean= SocketSendBean.parseToUserBean(jsonObject);
        int sitId=jsonObject.getIntValue(Constants.KEY_POSITON);
        for(SongWheatListner wheatLisnter:mWheatLisnterList){
            wheatLisnter.applySingerResult(userBean,sitId,isAgree);
            // TODO: 2020-06-12 20200612 注释 
            //wheatLisnter.resfuseUpWheat(userBean.getId());
        }
    }

    private void applySinger(JSONObject jsonObject) {
        UserBean userBean= SocketSendBean.parseUserBean(jsonObject);
        int sitId=jsonObject.getIntValue(Constants.KEY_POSITON);
        for(SongWheatListner wheatLisnter:mWheatLisnterList){
            wheatLisnter.applySinger(userBean,sitId);
        }
    }
    /*处理歌手上麦的申请结果*/
  public void agreeSingerApply(LifecycleProvider lifecycleProvider, final  UserBean userBean, final String sitId, String stream, boolean isAgree,final SuccessListner successListner){
        if(userBean==null){
            return;
        }
       if(isAgree){
           ChatRoomHttpUtil.songSetMic(userBean.getId(),sitId,stream).compose(lifecycleProvider.<Boolean>bindToLifecycle()).subscribe(new DefaultObserver<Boolean>() {
               @Override
               public void onNext(Boolean aBoolean) {
                   if(aBoolean){
                      sendSocketSingerApplyResult(userBean,sitId,true);
                      if(successListner!=null){
                         successListner.success();
                      }
                   }
               }
           });
       }else{
           sendSocketSingerApplyResult(userBean,sitId,isAgree);
           if(successListner!=null){
              successListner.success();
           }
       }
  }

  /*发送申请上歌手麦*/
    public void sendSocketSingerApply(String sitId) {
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_LINKMIC)
                .param("action",SINGER_APPLY)
                .param(Constants.KEY_POSITON, sitId)
                .param(CommonAppConfig.getInstance().getUserBean())
        );
    }
    /*主持处理结果*/
    private void sendSocketSingerApplyResult(UserBean userBean,String sitId, boolean isAgree) {
        int action=isAgree?AGREE_SINGER_APPLY:REFUSE_SINGER_APPLY;
        mILiveSocket.send(new SocketSendBean().param("_method_", Constants.SOCKET_LINKMIC)
                .param("action", action)
                .param(Constants.KEY_POSITON, sitId)
                .paramToUser(userBean)
        );
    }
}
