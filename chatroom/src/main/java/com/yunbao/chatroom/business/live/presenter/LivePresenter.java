package com.yunbao.chatroom.business.live.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.im.config.CallConfig;
import com.yunbao.chatroom.business.live.LiveState;
import com.yunbao.chatroom.business.live.view.ILiveView;
import com.tencent.liteav.TXLiteAVCode;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.utils.L;
import com.yunbao.im.config.GenerateTestUserSig;
import com.yunbao.chatroom.event.AudioChangeEvent;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.ref.WeakReference;

public class LivePresenter implements ILivePresenter {
    private static final String TAG = "LivePresenter";
    private TRTCCloud mTRTCCloud;
    private TRTCCloudDef.TRTCParams mTRTCParams;
    private ILiveView mILiveView;
    private TRTCCloudListenerImpl mTRTCCloudListener;
    private LiveState mLiveState;
    private String mPullUrl;
    private TXLivePlayer mLivePlayer;

    public LivePresenter(@NonNull ILiveView liveView, String pull ,int isRole){
        CallConfig.setIsBusy(true);
        getLiveState().role=isRole;
        this.mILiveView=liveView;
        mPullUrl=pull;
    }

    /*上麦用户在TRTCsdk中扮演的角都是TRTCCloudDef.TRTCRoleAnchor
      非麦上用户不进入sdkRoom,而是播放旁路直播的流
      changeRole方法中进行进退出房间操作
     */


    @Override
    public void changeRole(int newRole) {
        if(getLiveState().role==newRole){
            return;
        }
        getLiveState().role=newRole;
        enterRoom(getLiveState().roomId);
    }

    private void initParm() {
        JSONObject Str_uc_params = new JSONObject();
        JSONObject pure_audio_push_mod = new JSONObject();
        try {
            pure_audio_push_mod.put("pure_audio_push_mod", 1);
            // 1: 允许纯音频推流，2: 允许纯音频推流+录制
            Str_uc_params.put("Str_uc_params", pure_audio_push_mod);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String userId= CommonAppConfig.getInstance().getUid();
        String userSig= GenerateTestUserSig.genTestUserSig(userId);
        mTRTCParams = new TRTCCloudDef.TRTCParams(GenerateTestUserSig.SDKAPPID,
                userId, userSig, -1, "", Str_uc_params.toString());
        mTRTCParams.role =TRTCCloudDef.TRTCRoleAnchor ;
    }

    /*解析成sdk需要的角色参数*/
    /*private int parseRole(int isRole) {
        if(isRole== Constants.ROLE_ANTHOR){
            return TRTCCloudDef.TRTCRoleAnchor;
        }else{
            return TRTCCloudDef.TRTCRoleAudience;
        }
    }*/

    @Override
    public void openSpeak(boolean openSpeak) {
        if(mTRTCCloud==null&&!getLiveState().isEnterRoom) {
            return;
        }
        getLiveState().isSpeak=openSpeak;
        if(openSpeak){
            mTRTCCloud.startLocalAudio();

        }else{
            mTRTCCloud.stopLocalAudio();
        }
    }
    @Override
    public LiveState getLiveState() {
        if(mLiveState==null){
           mLiveState=new LiveState();
        }
        return mLiveState;
    }

    @Override
    public void release() {
        if (mTRTCCloud == null){
            return;
        }
        mTRTCCloud.setListener(null);
        mTRTCCloud.stopAllRemoteView();
        mTRTCCloud.stopLocalPreview();
        mTRTCCloud = null;
        mILiveView=null;
        if(mLivePlayer!=null){
           mLivePlayer.stopPlay(true);
        }
        TRTCCloud.destroySharedInstance();
    }

    public void onStopLive(){
        if (mTRTCCloud == null){
            return;
        }
        mTRTCCloud.setListener(null);
        mTRTCCloud.stopAllRemoteView();
        mTRTCCloud.stopLocalPreview();
        if(mLivePlayer!=null){
            mLivePlayer.stopPlay(true);
        }
        TRTCCloud.destroySharedInstance();
    }



    @Override
    public void init() {
        initParm();
        mTRTCCloudListener=new TRTCCloudListenerImpl(this);
        mTRTCCloud = TRTCCloud.sharedInstance(mILiveView.getContext());
        mTRTCCloud.setListener(mTRTCCloudListener);
    }


    @Override
    public void exitRoom(boolean implicit) {
        if(implicit){
            implicitExitRoom();
        }else{
            exitRoom();
        }
    }

    /*只退出sdkRoom,不退出聊天室*/
    private void implicitExitRoom() {
        L.e(TAG,"只退出sdkRoom--implicitExitRoom--->");
        openSpeak(false);
        getLiveState().implicit=true;
        if(getLiveState().isEnterRoom&&mTRTCCloud!=null){
            if(mTRTCCloud!=null){
               mTRTCCloud.exitRoom();
            }
        }else{
            startPlay();
        }
    }

    /*真退房间*/
    @Override
    public void exitRoom() {
        L.e(TAG,"真退房间exitRoom----->");
        CallConfig.setIsBusy(false);
        openSpeak(false);
        getLiveState().implicit=false;
        if(getLiveState().isEnterRoom&&mTRTCCloud!=null){
            if(mTRTCCloud!=null){
               mTRTCCloud.setListener(mTRTCCloudListener);
               mTRTCCloud.exitRoom();
            }
        }else if(mILiveView!=null){
            mILiveView.exitSdkRoomSuccess();
        }
    }

    @Override
    public void enterRoom(int roomId) {
        if(!ClickUtil.canClick()){
            return;
        }
        getLiveState().roomId=roomId;
        if(getLiveState().role==Constants.ROLE_AUDIENCE){
            enterRoomIsAudience();
        }else{
            enterRoomIsAnthor(roomId);
        }
    }
    /*以用户身份进入房间就是开启播放器模式*/
    private void enterRoomIsAudience() {
        exitRoom(true); //退出后会在Sdk回调方法onExitRoom自动去拉流
    }

    private void startPlay() {
        initVideoPlay();
        L.e("mPullUrl=="+mPullUrl);
        if(!TextUtils.isEmpty(mPullUrl)&&mLivePlayer!=null&&!mLivePlayer.isPlaying()){
            mLivePlayer.startPlay(mPullUrl,TXLivePlayer.PLAY_TYPE_LIVE_FLV);
        }
    }

    private void initVideoPlay() {
        if(mLivePlayer==null){
            //创建 player 对象
            mLivePlayer = new TXLivePlayer(mILiveView.getContext());
            TXLivePlayConfig playConfig = new TXLivePlayConfig();
            playConfig.setAutoAdjustCacheTime(true);
            playConfig.setMinAutoAdjustCacheTime(1);
            playConfig.setMaxAutoAdjustCacheTime(1);
//            CommonAppConfig.getHost();
//            playConfig.setHeaders(CommonAppConfig.HEADER);
            mLivePlayer.setConfig(playConfig);
            mLivePlayer.setPlayListener(new ITXLivePlayListener() {
                @Override
                public void onPlayEvent(int i, Bundle bundle) {
                    L.e("i=="+i+"bundle=="+bundle.toString());
                }
                @Override
                public void onNetStatus(Bundle bundle) {
                }
            });
        }
    }



    private void enterRoomIsAnthor(int roomId) {
        stopPlayer();
        if(mTRTCParams==null||mTRTCCloud==null){
            return;
        }
        mTRTCParams.roomId=roomId;
        if(getLiveState().role==Constants.ROLE_HOST){
            mTRTCCloud.enableAudioVolumeEvaluation(300);
            mTRTCParams.streamId=createStreamId();

            JSONObject businessInfo = new JSONObject();
            JSONObject pureAudio    = new JSONObject();
            try {
                pureAudio.put("pure_audio_push_mod", 1);
                businessInfo.put("Str_uc_params", pureAudio);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mTRTCParams.businessInfo = businessInfo.toString();
           // gitsetMixConfig();
            openSpeak(true);
        }
        mTRTCCloud.enterRoom(mTRTCParams,TRTCCloudDef.TRTC_APP_SCENE_VOICE_CHATROOM);
    }

    public void setMixConfig() {
        TRTCCloudDef.TRTCTranscodingConfig config=new  TRTCCloudDef.TRTCTranscodingConfig();
        config.appId=GenerateTestUserSig.SDKAPPID;
        config.bizId=CommonAppConfig.TX_TRTC_BIZID;
        config.audioBitrate=64;
        config.audioSampleRate=48000;
        config.audioChannels=2;
        config.mode=2;
        mTRTCCloud.setMixTranscodingConfig(config);
    }

    private String createStreamId() {
        StringBuilder builder=new StringBuilder();
        builder.append(GenerateTestUserSig.SDKAPPID)
        .append("_")
        .append(CommonAppConfig.getInstance().getUid())
        .append("_trtc");
        return builder.toString();
    }

    private void stopPlayer() {
        if(mLivePlayer!=null&&mLivePlayer.isPlaying()){
           mLivePlayer.stopPlay(false);
           L.e("i==视频播放停止了");
        }
    }

    private void enterRoomSuccess(){
        getLiveState().isEnterRoom=true;
        if(mILiveView!=null){
           mILiveView.enterSdkRoomSuccess();
        }
    }

    static class TRTCCloudListenerImpl extends TRTCCloudListener {
        private WeakReference<LivePresenter> presneterReference;
        public TRTCCloudListenerImpl(LivePresenter presneter) {
            super();
            this.presneterReference=new WeakReference<>(presneter);
        }
        // 错误通知是要监听的，错误通知意味着 SDK 不能继续运行了
        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            L.e("sdk errCode=="+errCode+"errMsg=="+errMsg);
            LivePresenter presneter = presneterReference.get();
            if(presneter!=null) {
                if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                    presneter.exitRoom();
                }
            }
        }

        @Override
        public void onUserAudioAvailable(String uid, boolean b) {
            super.onUserAudioAvailable(uid, b);

            L.e("onUserAudioAvailable=="+uid+"&&&b=="+b);
            LivePresenter presneter = presneterReference.get();
            if(presneter!=null&&presneter.mILiveView!=null){
               EventBus.getDefault().post(new AudioChangeEvent(uid,b));
            }
        }


        @Override
        public void onUserExit(String s, int i) {
            super.onUserExit(s, i);
            if(i==0){

            }
        }


        @Override
        public void onEnterRoom(long l) {
            if(l>=0){
                LivePresenter presneter = presneterReference.get();
                if(presneter!=null&&presneter.mLiveState.role==Constants.ROLE_HOST){
                   presneter.setMixConfig();
                   presneter.enterRoomSuccess();
                }
            }
        }
        @Override
        public void onUserEnter(String s) {
            super.onUserEnter(s);
            L.e("用户进入房间了=="+s);
        }
        @Override
        public void onExitRoom(int i) {
            super.onExitRoom(i);
            LivePresenter presneter = presneterReference.get();
            if(presneter!=null){
                presneter.getLiveState().isEnterRoom=false;
                if(presneter.mILiveView!=null&&!presneter.getLiveState().implicit) {
                   presneter.mILiveView.exitSdkRoomSuccess();
                }else if(presneter.getLiveState().implicit){
                  presneter.startPlay();
                }
            }

        }
    }
}
