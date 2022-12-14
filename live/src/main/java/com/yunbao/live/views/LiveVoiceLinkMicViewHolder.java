package com.yunbao.live.views;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.yunbao.common.Constants;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.live.adapter.LiveVoiceLinkMicAdapter;
import com.yunbao.live.bean.LiveVoiceGiftBean;
import com.yunbao.live.bean.LiveVoiceLinkMicBean;
import com.yunbao.live.interfaces.LivePushListener;

import java.util.ArrayList;
import java.util.List;

public class LiveVoiceLinkMicViewHolder extends AbsViewHolder implements ITXLivePushListener {

    private static final int USER_COUNT = 8;
    private List<LiveVoiceLinkMicBean> mList;
    private LiveVoiceLinkMicAdapter mAdapter;
    private TXLivePusher mLivePusher;
    private LivePushListener mLivePushListener;
    private boolean mStartPush;
    private TXLivePlayer[] mLivePlayerArr;
    private Handler mHandler;
    private boolean mPushMute;
    private boolean mPaused;


    public LiveVoiceLinkMicViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_voice_link_mic;
    }

    @Override
    public void init() {
        mList = new ArrayList<>();
        mLivePlayerArr = new TXLivePlayer[USER_COUNT];
        for (int i = 0; i < USER_COUNT; i++) {
            mList.add(new LiveVoiceLinkMicBean());
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        mAdapter = new LiveVoiceLinkMicAdapter(mContext, mList);
        recyclerView.setAdapter(mAdapter);
        if (mContext instanceof LiveAudienceActivity) {
            findViewById(R.id.voice_link_mic_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((LiveAudienceActivity) mContext).light();
                }
            });
        }
    }


    /**
     * ????????????
     *
     * @param toUid    ???????????????uid
     * @param toName   ???????????????name
     * @param toAvatar ?????????????????????
     * @param position ?????????????????????
     */
    public void onUserUpMic(String toUid, String toName, String toAvatar, int position) {
        if (TextUtils.isEmpty(toUid)) {
            return;
        }
        LiveVoiceLinkMicBean bean = mList.get(position);
        bean.setUid(toUid);
        bean.setUserName(toName);
        bean.setAvatar(toAvatar);
        bean.setStatus(Constants.VOICE_CTRL_OPEN);
        bean.setFaceIndex(-1);
        bean.setUserStream(null);
        if (mAdapter != null) {
            mAdapter.notifyItemChanged(position);
        }
        if (mHandler != null) {
            mHandler.removeMessages(position);
        }
    }


    /**
     * ????????????
     *
     * @param uid ???????????????uid
     */
    public void onUserDownMic(String uid) {
        onUserDownMic(getUserPosition(uid));
    }

    /**
     * ????????????
     *
     * @param position ???????????????position
     */
    public void onUserDownMic(int position) {
        if (position >= 0 && position < USER_COUNT) {
            LiveVoiceLinkMicBean bean = mList.get(position);
            bean.setUid(null);
            bean.setUserName(null);
            bean.setAvatar(null);
            bean.setStatus(Constants.VOICE_CTRL_EMPTY);
            bean.setFaceIndex(-1);
            bean.setUserStream(null);
            if (mAdapter != null) {
                mAdapter.notifyItemChanged(position);
            }
            if (mHandler != null) {
                mHandler.removeMessages(position);
            }
        }
    }


    /**
     * ???????????????--?????????????????? ?????????????????????
     *
     * @param position ??????
     * @param status   ??????????????? -1 ?????????  0????????? 1?????? ??? 2 ?????????
     */
    public void onControlMicPosition(int position, int status) {
        LiveVoiceLinkMicBean bean = mList.get(position);
        bean.setStatus(status);
        if (mAdapter != null) {
            mAdapter.notifyItemChanged(position, Constants.PAYLOAD);
        }
    }

    /**
     * ???????????????--???????????????????????????????????????
     *
     * @param uid       ???????????????uid
     * @param faceIndex ????????????
     */
    public void onVoiceRoomFace(String uid, int faceIndex) {
        int position = getUserPosition(uid);
        if (position >= 0 && position < USER_COUNT) {
            LiveVoiceLinkMicBean bean = mList.get(position);
            bean.setFaceIndex(faceIndex);
            if (mAdapter != null) {
                mAdapter.notifyItemChanged(position, Constants.VOICE_FACE);
            }
            if (mHandler == null) {
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        int pos = msg.what;
                        LiveVoiceLinkMicBean bean0 = mList.get(pos);
                        bean0.setFaceIndex(-1);
                        if (mAdapter != null) {
                            mAdapter.notifyItemChanged(pos, Constants.VOICE_FACE);
                        }
                    }
                };
            } else {
                mHandler.removeMessages(position);
            }
            mHandler.sendEmptyMessageDelayed(position, 5000);
        }
    }

    /**
     * ????????????
     */
    public void setPushMute(boolean pushMute) {
        if (mPushMute != pushMute) {
            mPushMute = pushMute;
            if (mLivePusher != null) {
                mLivePusher.setMute(pushMute);
            }
        }
    }

    /**
     * ????????????
     */
    public void startPush(String pushUrl, LivePushListener pushListener) {
        mLivePushListener = pushListener;
        if (mLivePusher == null) {
            mLivePusher = new TXLivePusher(mContext);
            TXLivePushConfig livePushConfig = new TXLivePushConfig();
            livePushConfig.enableAEC(true);//??????????????????
//            livePushConfig.enableANS(true);//??????????????????
            livePushConfig.setVolumeType(TXLiveConstants.AUDIO_VOLUME_TYPE_MEDIA);//????????????????????????
//            livePushConfig.enablePureAudioPush(true);//?????????????????????
            mLivePusher.setConfig(livePushConfig);
            mLivePusher.setPushListener(this);
            mLivePusher.startCameraPreview((TXCloudVideoView) findViewById(R.id.camera_preview));
        }
        mLivePusher.setMute(false);
        mLivePusher.startPusher(pushUrl);
    }

    @Override
    public void onPushEvent(int e, Bundle bundle) {
        if (e == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) {
            ToastUtil.show(R.string.live_push_failed_2);
        } else if (e == TXLiveConstants.PUSH_ERR_NET_DISCONNECT || e == TXLiveConstants.PUSH_ERR_INVALID_ADDRESS) {
//            L.e(mTag, "???????????????????????????------>");

        } else if (e == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {//????????????
//            L.e(mTag, "mStearm--->????????????");
            if (!mStartPush) {
                mStartPush = true;
                if (mLivePushListener != null) {
                    mLivePushListener.onPushStart();
                }
            }
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    /**
     * ????????????
     */
    public void stopPush() {
        mStartPush = false;
        if (mLivePusher != null) {
            mLivePusher.stopPusher();
        }
    }

    /**
     * ????????????
     */
    public void stopPlay(String uid) {
        stopPlay(getUserPosition(uid));
    }


    /**
     * ????????????
     */
    public void stopPlay(int position) {
        if (position >= 0 && position < USER_COUNT) {
            TXLivePlayer player = mLivePlayerArr[position];
            if (player != null && player.isPlaying()) {
                player.stopPlay(false);
            }
        }
    }

    /**
     * ??????????????????
     */
    public void stopAllPlay() {
        for (TXLivePlayer player : mLivePlayerArr) {
            if (player != null && player.isPlaying()) {
                player.stopPlay(false);
            }
        }
    }


    /**
     * ???????????????--?????????????????????????????????
     *
     * @param uid        ???????????????uid
     * @param pull       ?????????????????????????????????
     * @param userStream ???????????????????????????????????????
     */
    public void playAccStream(String uid, String pull, String userStream) {
        int position = getUserPosition(uid);
        if (position >= 0 && position < USER_COUNT) {
            LiveVoiceLinkMicBean bean = mList.get(position);
            bean.setUserStream(userStream);
            TXLivePlayer player = mLivePlayerArr[position];
            if (player == null) {
                player = new TXLivePlayer(mContext);
                TXLivePlayConfig playConfig = new TXLivePlayConfig();
                playConfig.enableAEC(true);
                playConfig.setAutoAdjustCacheTime(true);
                playConfig.setMaxAutoAdjustCacheTime(1.0f);
                playConfig.setMinAutoAdjustCacheTime(1.0f);
                player.setConfig(playConfig);
                mLivePlayerArr[position] = player;
            }
            player.startPlay(pull, TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
        }

    }


    /**
     * ??????????????????????????????
     */
    public int getUserPosition(String uid) {
        if (!TextUtils.isEmpty(uid)) {
            for (int i = 0; i < USER_COUNT; i++) {
                LiveVoiceLinkMicBean bean = mList.get(i);
                if (uid.equals(bean.getUid())) {
                    return i;
                }
            }
        }
        return -1;
    }


    /**
     * ????????????
     */
    public LiveVoiceLinkMicBean getUserBean(int position) {
        if (position >= 0 && position < USER_COUNT) {
            return mList.get(position);
        }
        return null;
    }


    /**
     * ????????????
     */
    public LiveVoiceLinkMicBean getUserBean(String toUid) {
        return getUserBean(getUserPosition(toUid));
    }


    /**
     * ???????????????????????????????????????Stream
     */
    public List<String> getUserStreamForMix() {
        List<String> list = null;
        for (int i = 0; i < USER_COUNT; i++) {
            String userStream = mList.get(i).getUserStream();
            if (!TextUtils.isEmpty(userStream)) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(userStream);
            }
        }
        return list;
    }

    /**
     * ????????????????????????
     */
    public void showUserList(JSONArray arr) {
        for (int i = 0; i < USER_COUNT; i++) {
            LiveVoiceLinkMicBean bean = mList.get(i);
            JSONObject obj = arr.getJSONObject(i);
            bean.setUid(obj.getString("id"));
            bean.setUserName(obj.getString("user_nickname"));
            bean.setAvatar(obj.getString("avatar"));
            bean.setStatus(obj.getIntValue("status"));
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }


    public List<LiveVoiceGiftBean> getVoiceGiftUserList() {
        List<LiveVoiceGiftBean> list = null;
        for (int i = 0; i < USER_COUNT; i++) {
            LiveVoiceLinkMicBean bean = mList.get(i);
            if (!bean.isEmpty()) {
                LiveVoiceGiftBean giftUserBean = new LiveVoiceGiftBean();
                giftUserBean.setUid(bean.getUid());
                giftUserBean.setAvatar(bean.getAvatar());
                giftUserBean.setType(i);
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(giftUserBean);
            }
        }
        return list;
    }


    @Override
    public void release() {
        stopAllPlay();
        if (mLivePusher != null) {
            mLivePusher.stopPusher();
            mLivePusher.stopScreenCapture();
            mLivePusher.stopCameraPreview(false);
            mLivePusher.setPushListener(null);
        }
        mLivePusher = null;
        mLivePushListener = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        super.release();
    }

    @Override
    public void onPause() {
        if (!mPushMute && mLivePusher != null) {
            mLivePusher.setMute(true);
        }
        for (TXLivePlayer player : mLivePlayerArr) {
            if (player != null) {
                player.setMute(true);
            }
        }
        mPaused = true;
    }

    @Override
    public void onResume() {
        if (mPaused) {
            if (!mPushMute && mLivePusher != null) {
                mLivePusher.setMute(false);
            }
        }
        for (TXLivePlayer player : mLivePlayerArr) {
            if (player != null) {
                player.setMute(false);
            }
        }
        mPaused = false;
    }
}
