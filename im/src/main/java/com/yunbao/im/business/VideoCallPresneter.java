package com.yunbao.im.business;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tencent.liteav.TXLiteAVCode;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.L;
import com.yunbao.im.config.CallConfig;
import com.yunbao.im.config.GenerateTestUserSig;
import com.yunbao.im.http.ImHttpUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import static com.tencent.trtc.TRTCCloudDef.TRTCRoleAnchor;

public class VideoCallPresneter implements ICallPresnter {

    private static final String TAG = "VideoCallPresneter";
    private TRTCCloud trtcCloud;
    private TRTCCloudListenerImpl trtcCloudListener;
    private TRTCCloudDef.TRTCParams tRTCParams;
    private IVideoCallView<TXCloudVideoView> iVideoCallView;
    private CallLivingState callState;
    private int tcRole;
    private int mRoomId;
    private int mCallType;

    public VideoCallPresneter(@NonNull IVideoCallView iVideoCallView, int isRole) {
        this.iVideoCallView=iVideoCallView;
        tcRole=parseRole(isRole);
        callState=new CallLivingState();
        initParm();
    }

    public void setRoomInfo(int roomId,int callType){
        mRoomId = roomId;
        mCallType = callType;
    }

    private int parseRole(int isRole) {
       /* if(tcRole== Constants.ROLE_ANTHOR){
            return TRTCCloudDef.TRTCRoleAnchor;
        }else{
            return TRTCCloudDef.TRTCRoleAudience;
        }*/
        return TRTCRoleAnchor;
    }

    private void initParm() {
        String userId= CommonAppConfig.getInstance().getUid();
        String userSig= GenerateTestUserSig.genTestUserSig(userId);
        tRTCParams = new TRTCCloudDef.TRTCParams(GenerateTestUserSig.SDKAPPID,
                userId, userSig, -1, "", "");
        tRTCParams.role = tcRole;
    }


    @Override
    public void init(){
        trtcCloudListener=new TRTCCloudListenerImpl(this,mRoomId,mCallType);
        trtcCloud = TRTCCloud.sharedInstance(iVideoCallView.getContext());
        trtcCloud.setListener(trtcCloudListener);
        Bitmap bitmap = Bitmap.createBitmap(200,400,Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.parseColor("#1a1a1a"));
        trtcCloud.setVideoMuteImage(bitmap,10);
        trtcCloud.setVideoEncoderParam(CallConfig.createDefaultBigEncParam());
    }

    @Override
    public void exitRoom(boolean implicit) {
        exitRoom();
    }

    @Override
    public void release(){
        if (trtcCloud == null){
            return;
        }
        trtcCloud.setListener(null);
        trtcCloud.stopAllRemoteView();
        trtcCloud.stopLocalPreview();
        trtcCloud = null;
        iVideoCallView=null;
        TRTCCloud.destroySharedInstance();
    }


    /*腾讯sdk回调监听*/
    static class TRTCCloudListenerImpl extends TRTCCloudListener {
        private WeakReference<VideoCallPresneter> presneterReference;
        private String roomId;
        private int callType;
        public TRTCCloudListenerImpl(VideoCallPresneter presneter,int roomId,int callType) {
            super();
            this.presneterReference=new WeakReference<>(presneter);
            this.roomId = String.valueOf(roomId);
            this.callType = callType;
        }
        // 错误通知是要监听的，错误通知意味着 SDK 不能继续运行了
        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            L.e("sdk callback onError");
            VideoCallPresneter presneter = presneterReference.get();
            if(presneter!=null) {
                if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                    presneter.exitRoom();
                 }
            }
      }
        @Override
        public void onUserExit(String s, int i) {
            super.onUserExit(s, i);
            L.e(TAG,"onUserExit---"+s+"----i--"+s +"----roomId---"+roomId);
            if(i==0){
                VideoCallPresneter presneter = presneterReference.get();
                if(presneter!=null) {
                    presneter.exitRoom();
                }
            }
        }

        @Override
        public void onEnterRoom(long l) {
            L.e(TAG,"onEnterRoom---"+l);
            if(l>=0){
                VideoCallPresneter presneter = presneterReference.get();
                if(presneter!=null){
                   presneter.enterRoomSuccess();
                }
            }
        }


        /**
         *  onEnterRoom---273
         * 2020-07-04 17:53:33.952 9935-9935/com.yunbao.shortvideo E/VideoCallPresneter: onUserEnter---100320
         * 2020-07-04 17:53:33.989 9935-9935/com.yunbao.shortvideo E/VideoCallPresneter: onUserVideoAvailable---100320---available--true
         * 2020-07-04 17:53:55.905 9935-9935/com.yunbao.shortvideo E/VideoCallPresneter: onUserVideoAvailable---100320---available--false
         * 2020-07-04 17:53:55.907 9935-9935/com.yunbao.shortvideo E/VideoCallPresneter: onUserExit---100320----i--100320
         * 2020-07-04 17:53:56.580 9935-9935/com.yunbao.shortvideo E/VideoCallPresneter: onExitRoom---0
         */


        @Override
        public void onUserEnter(String s) {
            super.onUserEnter(s);
            L.e(TAG,"onUserEnter---"+s);
            L.e("用户进入房间了=="+s + "--roomId--"+roomId);
            if (!s.equals(roomId)){
                ImHttpUtil.getLivetalkStart(roomId, s, callType, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {

                    }
                });
            }
        }
        @Override
        public void onExitRoom(int i) {
            super.onExitRoom(i);
            L.e(TAG,"onExitRoom---"+i);
            VideoCallPresneter presneter = presneterReference.get();
            if(presneter!=null&&presneter.iVideoCallView!=null){
                presneter.iVideoCallView.onExitRoom();
                ImHttpUtil.getLivetalkStop(roomId, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {

                    }
                });
            }
        }
        
        @Override
        public void onUserVideoAvailable(String userId, boolean available) {
            L.e(TAG,"onUserVideoAvailable---"+userId+"---available--"+available);
            VideoCallPresneter presneter = presneterReference.get();
            /*当是视频模式的时候进行如下操作*/
            if(presneter!=null&&presneter.getCallState().isVideo){
                if(available&&presneter.callState.isVideo){
                    TXCloudVideoView remoteView =presneter.iVideoCallView.getVideoView(userId);
                    if(remoteView!=null){
                        if(presneter.trtcCloud==null){
                            return;
                        }
                        List<String>userArray=presneter.callState.remoteUserArray;
                        if(userArray==null){
                            synchronized (VideoCallPresneter.class){
                                userArray=new ArrayList<>();
                                presneter.callState.remoteUserArray=userArray;
                            }
                        }
                        if(!userArray.contains(userId)){
                            userArray.add(userId);
                            presneter.trtcCloud.setRemoteViewFillMode(userId, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
                            presneter.trtcCloud.startRemoteView(userId, remoteView);
                        }
                    }
                        presneter.iVideoCallView.ontherOpenVideo(true);
                }else{
                    if(presneter.trtcCloud==null||presneter.iVideoCallView==null){
                        return;
                    }
                        presneter.iVideoCallView.ontherOpenVideo(false);
                        CallLivingState state=presneter.getCallState();
                        /*if(!state.isOpenCamera){
                           presneter.trtcCloud.stopRemoteView(userId);
                        }*/
                }
            }
        }
    }


    /*进入房间成功*/
    private void enterRoomSuccess() {
        L.e(TAG,"enterRoomSuccess------");
        TimeModel.getInstance().start();
        trtcCloud.startLocalAudio();
        openCamera(true);
        getCallState().isEnterRoom=true;
    }

    /*没登录成功的时候直接调用view层的退出方法,登录成功后等待sdk正常退出*/
    @Override
    public void exitRoom(){
        if(getCallState().isEnterRoom){
            trtcCloud.exitRoom();
        }else if(iVideoCallView!=null){
            iVideoCallView.onExitRoom();
        }
    }

    @Override
    public void enterRoom(int roomId) {
        if(tRTCParams==null||trtcCloud==null){
            return;
        }
        tRTCParams.roomId=roomId;
        trtcCloud.enterRoom(tRTCParams,TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL);
    }

    @Override
    public void isFront(boolean isFront) {
        if(trtcCloud==null) {
            return;
        }
        getCallState().isFront=isFront;
        trtcCloud.switchCamera();
    }

    @Override
    public void openCamera(boolean isOpen) {
        if(trtcCloud==null) {
            return;
        }
        CallLivingState callState= getCallState();
        callState.isOpenCamera=isOpen;
        trtcCloud.muteLocalVideo(!isOpen); //相反的
    }

    @Override
    public void isHandsFree(boolean isHandsFree) {
        if(trtcCloud==null) {
            return;
        }
        getCallState().isHandsFree=isHandsFree;
        if (isHandsFree) {
            trtcCloud.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_SPEAKER);
        } else {
            trtcCloud.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_EARPIECE);
        }
    }

    @Override
    public void isMute(boolean isMute) {
        if(trtcCloud==null) {
            return;
        }
        getCallState().isMute=isMute;
        trtcCloud.muteLocalAudio(isMute);
    }

    @Override
    public CallLivingState getCallState() {

        if(callState==null){
            callState=new CallLivingState();
        }
        return callState;
    }

    @Override
    public void startSDKLocalPreview(boolean isPreview) {
        if(trtcCloud==null) {
            return ;
        }
        CallLivingState callState=getCallState();
        callState.isOpenCamera=isPreview;
        callState.isPreView=isPreview;
        if (isPreview){
            TXCloudVideoView txCloudVideoView= iVideoCallView.getMainVideoView();
            trtcCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
            trtcCloud.startLocalPreview(callState.isFront, txCloudVideoView);
        }else{
            trtcCloud.stopLocalPreview();
        }
    }
    @Override
    public void setCallView(IVideoCallView callView) {
        this.iVideoCallView=callView;
    }
    @Override
    public void isVideo(boolean isVideo) {
        getCallState().isVideo=isVideo;
    }
}
