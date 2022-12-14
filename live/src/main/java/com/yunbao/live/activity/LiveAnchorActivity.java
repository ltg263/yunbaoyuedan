package com.yunbao.live.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.opensource.svgaplayer.SVGAImageView;
import com.yunbao.beauty.interfaces.IBeautyViewHolder;
import com.yunbao.beauty.views.BeautyViewHolder;
import com.yunbao.beauty.views.SimpleBeautyViewHolder;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.GoodsBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.NotCancelableDialog;
import com.yunbao.common.event.LoginInvalidEvent;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DateFormatUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.bean.LiveBeanReal;
import com.yunbao.live.bean.LiveConfigBean;
import com.yunbao.live.bean.LiveGuardInfo;
import com.yunbao.live.bean.LiveReceiveGiftBean;
import com.yunbao.live.dialog.LiveFunctionDialogFragment;
import com.yunbao.live.dialog.LiveLinkMicListDialogFragment;
import com.yunbao.live.dialog.LiveShopDialogFragment;
import com.yunbao.live.dialog.LiveVoiceControlFragment;
import com.yunbao.live.event.LinkMicTxMixStreamEvent;
import com.yunbao.live.event.LiveVoiceMicStatusEvent;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.interfaces.LiveFunctionClickListener;
import com.yunbao.live.interfaces.LivePushListener;
import com.yunbao.live.music.LiveMusicDialogFragment;
import com.yunbao.live.presenter.LiveLinkMicAnchorPresenter;
import com.yunbao.live.presenter.LiveLinkMicPkPresenter;
import com.yunbao.live.presenter.LiveLinkMicPresenter;
import com.yunbao.live.socket.SocketChatUtil;
import com.yunbao.live.socket.SocketClient;
import com.yunbao.live.socket.SocketVoiceRoomUtil;
import com.yunbao.live.utils.LogUtil;
import com.yunbao.live.views.AbsLivePushViewHolder;
import com.yunbao.live.views.LiveAnchorViewHolder;
import com.yunbao.live.views.LiveEndViewHolder;
import com.yunbao.live.views.LiveGoodsAddViewHolder;
import com.yunbao.live.views.LiveMusicViewHolder;
import com.yunbao.live.views.LivePlatGoodsAddViewHolder;
import com.yunbao.live.views.LivePushKsyViewHolder;
import com.yunbao.live.views.LivePushTxViewHolder;
import com.yunbao.live.views.LiveReadyViewHolder;
import com.yunbao.live.views.LiveRoomViewHolder;
import com.yunbao.live.views.LiveVoiceAnchorViewHolder;
import com.yunbao.live.views.LiveVoiceLinkMicViewHolder;
import com.yunbao.live.views.LiveVoicePushTxViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

//import com.yunbao.game.bean.GameParam;
//import com.yunbao.game.dialog.GameDialogFragment;
//import com.yunbao.game.event.GameWindowChangedEvent;
//import com.yunbao.game.util.GamePresenter;
//import com.yunbao.im.utils.ImMessageUtil;
//import com.yunbao.im.utils.ImPushUtil;

/**
 * Created by cxf on 2018/10/7.
 * ???????????????
 */

public class LiveAnchorActivity extends LiveActivity implements LiveFunctionClickListener {

    private static final String TAG = "LiveAnchorActivity";
    private LiveGoodsAddViewHolder mLiveGoodsAddViewHolder;
    private LivePlatGoodsAddViewHolder mLivePlatGoodsAddViewHolder;

    public static void forward(Context context, int liveSdk, LiveConfigBean bean, int haveStore, boolean isVoiceChatRoom,String agentCode) {
        Intent intent = new Intent(context, LiveAnchorActivity.class);
        intent.putExtra(Constants.LIVE_SDK, liveSdk);
        intent.putExtra(Constants.LIVE_CONFIG, bean);
        intent.putExtra(Constants.HAVE_STORE, haveStore);
        intent.putExtra(Constants.VOICE_CHAT_ROOM, isVoiceChatRoom);
        intent.putExtra(Constants.AGENT_CODE, agentCode);
        context.startActivity(intent);
    }

    private ViewGroup mRoot;
    private ViewGroup mContainerWrap;
    private AbsLivePushViewHolder mLivePushViewHolder;
    private LiveReadyViewHolder mLiveReadyViewHolder;
    private IBeautyViewHolder mLiveBeautyViewHolder;
    private LiveAnchorViewHolder mLiveAnchorViewHolder;
    private LiveVoiceAnchorViewHolder mLiveVoiceAnchorViewHolder;
    private LiveMusicViewHolder mLiveMusicViewHolder;
    private boolean mStartPreview;//??????????????????
    private boolean mStartLive;//?????????????????????
    private List<Integer> mGameList;//????????????
    private boolean mBgmPlaying;//???????????????????????????
    private LiveConfigBean mLiveConfigBean;
    private HttpCallback mCheckLiveCallback;
    private File mLogFile;
    private int mReqCount;
    private boolean mPaused;
    private boolean mNeedCloseLive = true;
    private boolean mSuperCloseLive;//???????????????????????????
    private boolean mLoginInvalid;//??????????????????
    private boolean mEnd;
    private LiveLinkMicListDialogFragment mLiveLinkMicListDialogFragment;
    private LiveVoicePushTxViewHolder mLiveVoicePushTxViewHolder;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_anchor;
    }

    @Override
    protected void main() {
        super.main();
        Intent intent = getIntent();
        mLiveSDK = intent.getIntExtra(Constants.LIVE_SDK, Constants.LIVE_SDK_TX);
        mLiveConfigBean = intent.getParcelableExtra(Constants.LIVE_CONFIG);
        int haveStore = intent.getIntExtra(Constants.HAVE_STORE, 0);
        mVoiceChatRoom = intent.getBooleanExtra(Constants.VOICE_CHAT_ROOM, false);
        mAgentCode = intent.getStringExtra(Constants.AGENT_CODE);

//        L.e(TAG, "??????sdk----->" + (mLiveSDK == Constants.LIVE_SDK_KSY ? "?????????" : "?????????"));
        mRoot = (ViewGroup) findViewById(R.id.root);
        mSocketUserType = Constants.SOCKET_USER_TYPE_ANCHOR;
        UserBean u = CommonAppConfig.getInstance().getUserBean();
        mLiveUid = u.getId();
        mLiveBean = new LiveBeanReal();
        mLiveBean.setUid(mLiveUid);
        mLiveBean.setUserNiceName(u.getUserNiceName());
        mLiveBean.setAvatar(u.getAvatar());
        mLiveBean.setAvatarThumb(u.getAvatarThumb());
        mLiveBean.setLevelAnchor(u.getAnchorLevel());
        mLiveBean.setGoodNum(u.getGoodName());
        mLiveBean.setCity(u.getCity());

        if (isVoiceChatRoom()) {
            mLiveVoicePushTxViewHolder = new LiveVoicePushTxViewHolder(mContext, (ViewGroup) findViewById(R.id.preview_container));
            mLiveVoiceLinkMicViewHolder = new LiveVoiceLinkMicViewHolder(mContext, mLiveVoicePushTxViewHolder.getContainer());
            mLiveVoiceLinkMicViewHolder.addToParent();
            mLiveVoiceLinkMicViewHolder.subscribeActivityLifeCycle();
            mLivePushViewHolder = mLiveVoicePushTxViewHolder;
        } else {
            if (mLiveSDK == Constants.LIVE_SDK_TX) {
                //????????????????????????
                mLivePushViewHolder = new LivePushTxViewHolder(mContext, (ViewGroup) findViewById(R.id.preview_container), mLiveConfigBean);
            } else {
                mLivePushViewHolder = new LivePushKsyViewHolder(mContext, (ViewGroup) findViewById(R.id.preview_container), mLiveConfigBean);
            }
        }

        mLivePushViewHolder.setLivePushListener(new LivePushListener() {
            @Override
            public void onPreviewStart() {
                //??????????????????
                mStartPreview = true;
            }

            @Override
            public void onPushStart() {
                //??????????????????
                LiveHttpUtil.changeLive(mStream);
            }

            @Override
            public void onPushFailed() {
                //??????????????????
                ToastUtil.show(R.string.live_push_failed);
            }
        });
        mLivePushViewHolder.addToParent();
        mLivePushViewHolder.subscribeActivityLifeCycle();
        mContainerWrap = (ViewGroup) findViewById(R.id.container_wrap);
        mContainer = (ViewGroup) findViewById(R.id.container);
        //???????????????????????????
        mLiveReadyViewHolder = new LiveReadyViewHolder(mContext, mContainer, mLiveSDK, haveStore);
        mLiveReadyViewHolder.addToParent();
        mLiveReadyViewHolder.subscribeActivityLifeCycle();
        if (!isVoiceChatRoom()) {
            mLiveLinkMicPresenter = new LiveLinkMicPresenter(mContext, mLivePushViewHolder, true, mLiveSDK, mContainer);
            mLiveLinkMicPresenter.setLiveUid(mLiveUid);
            mLiveLinkMicAnchorPresenter = new LiveLinkMicAnchorPresenter(mContext, mLivePushViewHolder, true, mLiveSDK, mContainer);
            mLiveLinkMicPkPresenter = new LiveLinkMicPkPresenter(mContext, mLivePushViewHolder, true, mContainer);
        }
    }

    public boolean isStartPreview() {
        return mStartPreview;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param functionID
     */
    @Override
    public void onClick(int functionID) {
        switch (functionID) {
            case Constants.LIVE_FUNC_BEAUTY://??????
                beauty();
                break;
            case Constants.LIVE_FUNC_CAMERA://????????????
                toggleCamera();
                break;
            case Constants.LIVE_FUNC_FLASH://???????????????
                toggleFlash();
                break;
            case Constants.LIVE_FUNC_MUSIC://??????
                openMusicWindow();
                break;
            case Constants.LIVE_FUNC_SHARE://??????
                openShareWindow();
                break;
            case Constants.LIVE_FUNC_GAME://??????
                openGameWindow();
                break;
            case Constants.LIVE_FUNC_RED_PACK://??????
                openRedPackSendWindow();
                break;
            case Constants.LIVE_FUNC_LINK_MIC://??????
                openLinkMicAnchorWindow();
                break;
            case Constants.LIVE_FUNC_MIRROR://??????
                togglePushMirror();
                break;
            case Constants.LIVE_FUNC_TASK://????????????
                openDailyTaskWindow();
                break;
            case Constants.LIVE_FUNC_LUCK://????????????
                openPrizePoolWindow();
                break;
        }
    }

    /**
     * ????????????
     */
    private void togglePushMirror() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.togglePushMirror();
        }
    }


    public void openShop(boolean isOpen) {
        if (mLiveAnchorViewHolder != null) {
            mLiveAnchorViewHolder.setShopBtnVisible(isOpen);
        }
    }


    //?????????????????????
    public void beforeCamera() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.setOpenCamera(true);
        }
    }


    /**
     * ????????????
     */
    public void toggleCamera() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.toggleCamera();
        }
    }

    /**
     * ???????????????
     */
    public void toggleFlash() {
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.toggleFlash();
        }
    }

    /**
     * ????????????
     */

    public void beauty() {
        if (mLiveBeautyViewHolder == null) {
            if (CommonAppConfig.getInstance().isMhBeautyEnable()) {
                mLiveBeautyViewHolder = new BeautyViewHolder(mContext, mRoot);
            } else {
                mLiveBeautyViewHolder = new SimpleBeautyViewHolder(mContext, mRoot);
            }
            mLiveBeautyViewHolder.setVisibleListener(new IBeautyViewHolder.VisibleListener() {
                @Override
                public void onVisibleChanged(boolean visible) {
                    if (mLiveReadyViewHolder != null) {
                        if (visible) {
                            mLiveReadyViewHolder.hide();
                        } else {
                            mLiveReadyViewHolder.show();
                        }
                    }
                }
            });
        }
        mLiveBeautyViewHolder.show();
    }

    /**
     * ??????
     */
    public void light() {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.playLightAnim();
        }
    }

    /**
     * ??????????????????
     */
    private void openMusicWindow() {
        if (isLinkMic() || isLinkMicAnchor()) {
            ToastUtil.show(R.string.link_mic_not_bgm);
            return;
        }
        LiveMusicDialogFragment fragment = new LiveMusicDialogFragment();
//        fragment.setLifeCycleListener(this);
        fragment.setActionListener(new LiveMusicDialogFragment.ActionListener() {
            @Override
            public void onChoose(String musicId) {
                if (mLivePushViewHolder != null) {
                    if (mLiveMusicViewHolder == null) {
                        mLiveMusicViewHolder = new LiveMusicViewHolder(mContext, mContainer, mLivePushViewHolder);
                        mLiveMusicViewHolder.subscribeActivityLifeCycle();
                        mLiveMusicViewHolder.addToParent();
                    }
                    mLiveMusicViewHolder.play(musicId);
                    mBgmPlaying = true;
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "LiveMusicDialogFragment");
    }

    /**
     * ??????????????????
     */
    public void stopBgm() {
        if (mLiveMusicViewHolder != null) {
            mLiveMusicViewHolder.release();
        }
        mLiveMusicViewHolder = null;
        mBgmPlaying = false;
    }

    public boolean isBgmPlaying() {
        return mBgmPlaying;
    }


    /**
     * ??????????????????
     */
    public void showFunctionDialog() {
        LiveFunctionDialogFragment fragment = new LiveFunctionDialogFragment();
//        fragment.setLifeCycleListener(this);
        Bundle bundle = new Bundle();
        boolean hasGame = false;
        if (CommonAppConfig.GAME_ENABLE && mGameList != null) {
            hasGame = mGameList.size() > 0;
        }
        bundle.putBoolean(Constants.HAS_GAME, hasGame);
        bundle.putBoolean(Constants.OPEN_FLASH, mLivePushViewHolder != null && mLivePushViewHolder.isFlashOpen());
        bundle.putBoolean("TASK", mTaskSwitch);
        fragment.setArguments(bundle);
        fragment.setFunctionClickListener(this);
        fragment.show(getSupportFragmentManager(), "LiveFunctionDialogFragment");
    }

    public void hideLinkMicAnchorWindow() {
        if (mLiveLinkMicListDialogFragment != null) {
            mLiveLinkMicListDialogFragment.dismissAllowingStateLoss();
        }
        mLiveLinkMicListDialogFragment = null;
    }

    public void hideLinkMicAnchorWindow2() {
        mLiveLinkMicListDialogFragment = null;
    }

    /**
     * ????????????????????????
     */
    private void openLinkMicAnchorWindow() {
        if (mLiveLinkMicAnchorPresenter != null && !mLiveLinkMicAnchorPresenter.canOpenLinkMicAnchor()) {
            return;
        }
        LiveLinkMicListDialogFragment fragment = new LiveLinkMicListDialogFragment();
//        fragment.setLifeCycleListener(this);
        mLiveLinkMicListDialogFragment = fragment;
        fragment.show(getSupportFragmentManager(), "LiveLinkMicListDialogFragment");
    }


    /**
     * ????????????????????????
     */
    private void openGameWindow() {
//        if (isLinkMic() || isLinkMicAnchor()) {
//            ToastUtil.show(R.string.live_link_mic_cannot_game);
//            return;
//        }
//        if (mGamePresenter != null) {
//            GameDialogFragment fragment = new GameDialogFragment();
//            fragment.setLifeCycleListener(this);
//            fragment.setGamePresenter(mGamePresenter);
//            fragment.show(getSupportFragmentManager(), "GameDialogFragment");
//        }
    }

    /**
     * ????????????
     */
    public void closeGame() {
//        if (mGamePresenter != null) {
//            mGamePresenter.closeGame();
//        }
    }

    /**
     * ????????????
     *
     * @param data createRoom???????????????
     */
    public void startLiveSuccess(String data, int liveType, int liveTypeVal, String title) {
        mLiveType = liveType;
        mLiveTypeVal = liveTypeVal;
        if (mLiveBean != null) {
            mLiveBean.setTitle(title);
        }
        //??????createRoom???????????????
        JSONObject obj = JSON.parseObject(data);
        mStream = obj.getString("stream");
        mDanmuPrice = obj.getString("barrage_fee");
        mAgentCode=obj.getString("agentcode");
        String playUrl = obj.getString("pull");
        L.e("createRoom----????????????--->" + playUrl);
        mLiveBean.setPull(playUrl);
        mTxAppId = obj.getString("tx_appid");
        //??????????????????????????????????????????????????????
        if (mLiveReadyViewHolder != null) {
            mLiveReadyViewHolder.removeFromParent();
            mLiveReadyViewHolder.release();
        }
        mLiveReadyViewHolder = null;
        if (mLiveRoomViewHolder == null) {
            mLiveRoomViewHolder = new LiveRoomViewHolder(mContext, mContainer, (GifImageView) findViewById(R.id.gift_gif), (SVGAImageView) findViewById(R.id.gift_svga), mContainerWrap);
            mLiveRoomViewHolder.addToParent();
            mLiveRoomViewHolder.subscribeActivityLifeCycle();
            mLiveRoomViewHolder.setLiveInfo(mLiveUid, mStream, obj.getIntValue("userlist_time") * 1000);
            mLiveRoomViewHolder.setVotes(obj.getString("votestotal"));
            UserBean u = CommonAppConfig.getInstance().getUserBean();
            if (u != null) {
                mLiveRoomViewHolder.setRoomNum(u.getId());
                mLiveRoomViewHolder.setName(u.getUserNiceName());
                mLiveRoomViewHolder.setAvatar(u.getAvatar());
                mLiveRoomViewHolder.setAnchorLevel(u.getAnchorLevel());
            }
            mLiveRoomViewHolder.startAnchorLight();
        }

        //????????????
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.startPush(obj.getString("push"));
        }
        //????????????????????????
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.startAnchorLiveTime();
            mLiveRoomViewHolder.startAnchorCheckLive();
        }
        mStartLive = true;
        mLiveRoomViewHolder.startRefreshUserList();

//        //????????????
//        mLiveGuardInfo = new LiveGuardInfo();
//        int guardNum = obj.getIntValue("guard_nums");
//        mLiveGuardInfo.setGuardNum(guardNum);
//        if (mLiveRoomViewHolder != null) {
//            mLiveRoomViewHolder.setGuardNum(guardNum);
//        }

        //??????socket
        if (mSocketClient == null) {
            mSocketClient = new SocketClient(obj.getString("chatserver"), this);
        }
        mSocketClient.connect(mLiveUid, mStream);

//        int dailytask_switch = obj.getIntValue("dailytask_switch");
//        mTaskSwitch = dailytask_switch == 1;
        if (!isVoiceChatRoom()) {
            if (mLiveAnchorViewHolder == null) {
                mLiveAnchorViewHolder = new LiveAnchorViewHolder(mContext, mContainer);
                mLiveAnchorViewHolder.addToParent();
                mLiveAnchorViewHolder.setUnReadCount(((LiveActivity) mContext).getImUnReadCount());
            }
            mLiveBottomViewHolder = mLiveAnchorViewHolder;

            if (mLiveLinkMicPresenter != null) {
                mLiveLinkMicPresenter.setSocketClient(mSocketClient);
            }
            if (mLiveLinkMicAnchorPresenter != null) {
                mLiveLinkMicAnchorPresenter.setSocketClient(mSocketClient);
                mLiveLinkMicAnchorPresenter.setPlayUrl(playUrl);
                mLiveLinkMicAnchorPresenter.setSelfStream(mStream);
            }
            if (mLiveLinkMicPkPresenter != null) {
                mLiveLinkMicPkPresenter.setSocketClient(mSocketClient);
                mLiveLinkMicPkPresenter.setLiveUid(mLiveUid);
                mLiveLinkMicPkPresenter.setSelfStream(mStream);
            }

//            //????????????
//            int giftPrizePoolLevel = obj.getIntValue("jackpot_level");
//
//            if (mLiveRoomViewHolder != null) {
//                mLiveRoomViewHolder.showBtn(false, giftPrizePoolLevel, dailytask_switch);
//            }
//            //????????????
//            if (CommonAppConfig.GAME_ENABLE) {
//                mGameList = JSON.parseArray(obj.getString("game_switch"), Integer.class);
//                GameParam param = new GameParam();
//                param.setContext(mContext);
//                param.setParentView(mContainerWrap);
//                param.setTopView(mContainer);
//                param.setInnerContainer(mLiveRoomViewHolder.getInnerContainer());
//                param.setGameActionListener(new GameActionListenerImpl(LiveAnchorActivity.this, mSocketClient));
//                param.setLiveUid(mLiveUid);
//                param.setStream(mStream);
//                param.setAnchor(true);
//                param.setCoinName(CommonAppConfig.getInstance().getScoreName());
//                param.setObj(obj);
//                mGamePresenter = new GamePresenter(param);
//                mGamePresenter.setGameList(mGameList);
//            }

        } else {
            if (mLiveVoiceAnchorViewHolder == null) {
                mLiveVoiceAnchorViewHolder = new LiveVoiceAnchorViewHolder(mContext, mContainer);
                mLiveVoiceAnchorViewHolder.addToParent();
                mLiveVoiceAnchorViewHolder.setUnReadCount(((LiveActivity) mContext).getImUnReadCount());
            }
            mLiveBottomViewHolder = mLiveVoiceAnchorViewHolder;
        }
    }

    /**
     * ????????????
     */
    public void closeLive() {
        if (isVoiceChatRoom()) {
//            DialogUitl.showStringArrayDialog(mContext,
//                    new Integer[][]{
//                            {R.string.a_057, ContextCompat.getColor(mContext, R.color.red)}},
//                    new DialogUitl.StringArrayDialogCallback() {
//                        @Override
//                        public void onItemClick(String text, int tag) {
//                            if (tag == R.string.a_057) {
//                                endLive();
//                            }
//                        }
//                    });
        } else {
            DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.live_end_live), new DialogUitl.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {
                    endLive();
                }
            });
        }
    }


    /**
     * ????????????
     */
    public void endLive() {
        if (mEnd) {
            return;
        }
        mEnd = true;
        //?????????????????????
        LiveHttpUtil.stopLive(mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    //??????socket
                    if (mSocketClient != null) {
                        mSocketClient.disConnect();
                        mSocketClient = null;
                    }

                    if (mLiveEndViewHolder == null) {
                        mLiveEndViewHolder = new LiveEndViewHolder(mContext, mRoot);
                        mLiveEndViewHolder.subscribeActivityLifeCycle();
                        mLiveEndViewHolder.addToParent();
                        mLiveEndViewHolder.showData(mLiveBean, mStream);
                    }
                    release();
                    mStartLive = false;
                    if (mSuperCloseLive) {
                        DialogUitl.showSimpleTipDialog(mContext, WordUtil.getString(R.string.live_illegal));
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext, WordUtil.getString(R.string.live_end_ing));
            }

            @Override
            public void onLoginInvalid() {
                mStartLive = false;
                release();
                finish();
                CommonAppConfig.getInstance().clearLoginInfo();
                //????????????
//                ImMessageUtil.getInstance().logoutImClient();
//                ImPushUtil.getInstance().logout();
                if (mSuperCloseLive) {
//                    RouteUtil.forwardLogin(mContext, WordUtil.getString(R.string.live_illegal));
                } else if (mLoginInvalid) {
//                    RouteUtil.forwardLogin(mContext, WordUtil.getString(R.string.login_tip_0));
                } else {
//                    RouteUtil.forwardLogin(mContext);
                }
            }

            @Override
            public boolean isUseLoginInvalid() {
                return true;
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mLiveBeautyViewHolder != null && mLiveBeautyViewHolder.isShowed()) {
            mLiveBeautyViewHolder.hide();
            return;
        }
        if (mStartLive) {
            if (!canBackPressed()) {
                return;
            }
            closeLive();
        } else {
            if (mLivePushViewHolder != null) {
                mLivePushViewHolder.release();
            }
            if (mLiveLinkMicPresenter != null) {
                mLiveLinkMicPresenter.release();
            }
            mLivePushViewHolder = null;
            mLiveLinkMicPresenter = null;
            superBackPressed();
        }
    }


    public void superBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void release() {
        if (mSocketClient != null) {
            mSocketClient.disConnect();
            mSocketClient = null;
        }
        LiveHttpUtil.cancel(LiveHttpConsts.CHANGE_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.STOP_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.LIVE_PK_CHECK_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.SET_LINK_MIC_ENABLE);
        if (mLiveReadyViewHolder != null) {
            mLiveReadyViewHolder.release();
        }
        if (mLiveMusicViewHolder != null) {
            mLiveMusicViewHolder.release();
        }
        if (mLivePushViewHolder != null) {
            mLivePushViewHolder.release();
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.release();
        }
//        if (mGamePresenter != null) {
//            mGamePresenter.release();
//        }
        mLiveMusicViewHolder = null;
        mLiveReadyViewHolder = null;
        mLivePushViewHolder = null;
        mLiveLinkMicPresenter = null;
        mLiveBeautyViewHolder = null;
//        mGamePresenter = null;
        super.release();
    }

    @Override
    protected void onDestroy() {
        LiveHttpUtil.cancel(LiveHttpConsts.ANCHOR_CHECK_LIVE);
        super.onDestroy();
        L.e("LiveAnchorActivity-------onDestroy------->");
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (!isVoiceChatRoom()) {
            if (mNeedCloseLive && mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.anchorPause();
            }
            sendSystemMessage(WordUtil.getString(R.string.live_anchor_leave));
        }
        mPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isVoiceChatRoom() && mPaused) {
            if (mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.anchorResume();
            }
            sendSystemMessage(WordUtil.getString(R.string.live_anchor_come_back));
//            CommonHttpUtil.checkTokenInvalid();
        }
        mPaused = false;
        mNeedCloseLive = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginInvalidEvent(LoginInvalidEvent e) {
        onAnchorInvalid();
    }

    /**
     * ?????????  ??????????????????
     */
    @Override
    public void onAnchorInvalid() {
        mLoginInvalid = true;
        super.onAnchorInvalid();
        endLive();
    }

    /**
     * ?????????????????????
     */
    @Override
    public void onSuperCloseLive() {
        mSuperCloseLive = true;
        super.onAnchorInvalid();
        endLive();
    }


//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onGameWindowChangedEvent(GameWindowChangedEvent e) {
//        if (mLiveRoomViewHolder != null) {
//            mLiveRoomViewHolder.setOffsetY(e.getGameViewHeight());
//        }
//        if (mLiveAnchorViewHolder != null) {
//            mLiveAnchorViewHolder.setGameBtnVisible(e.isOpen());
//        }
//    }

    @Override
    public void setBtnFunctionDark() {
//        if (!isVoiceChatRoom()) {
//            if (mLiveAnchorViewHolder != null) {
//                mLiveAnchorViewHolder.setBtnFunctionDark();
//            }
//        } else {
//            if (mLiveVoiceAnchorViewHolder != null) {
//                mLiveVoiceAnchorViewHolder.setBtnFunctionDark();
//            }
//        }
    }

    /**
     * ?????????????????????  ????????????????????????????????????????????????
     */
    @Override
    public void onLinkMicAnchorApply(UserBean u, String stream) {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorApply(u, stream);
        }
    }

    /**
     * ?????????????????????  ?????????????????????????????????
     */
    @Override
    public void onLinkMicAnchorRefuse() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorRefuse();
        }
    }

    /**
     * ?????????????????????  ??????????????????????????????
     */
    @Override
    public void onLinkMicAnchorNotResponse() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicNotResponse();
        }
    }

    /**
     * ?????????????????????  ????????????????????????
     */
    @Override
    public void onlinkMicPlayGaming() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onlinkMicPlayGaming();
        }
    }


    /**
     * ?????????????????????  ??????????????????????????????
     */
    @Override
    public void onLinkMicAnchorBusy() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorBusy();
        }
    }

    /**
     * ????????????????????????
     *
     * @param pkUid  ???????????????uid
     * @param stream ???????????????stream
     */
    public void linkMicAnchorApply(final String pkUid, final String stream) {
        if (isBgmPlaying()) {
            DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.link_mic_close_bgm), new DialogUitl.SimpleCallback() {
                @Override
                public void onConfirmClick(Dialog dialog, String content) {
                    stopBgm();
                    checkLiveAnchorMic(pkUid, stream);
                }
            });
        } else {
            checkLiveAnchorMic(pkUid, stream);
        }
    }

    /**
     * ????????????????????????
     *
     * @param pkUid  ???????????????uid
     * @param stream ???????????????stream
     */
    private void checkLiveAnchorMic(final String pkUid, String stream) {
        LiveHttpUtil.livePkCheckLive(pkUid, stream, mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mLiveSDK == Constants.LIVE_SDK_TX) {
                        String playUrl = null;
                        JSONObject obj = JSON.parseObject(info[0]);
                        if (obj != null) {
                            String accUrl = obj.getString("pull");//???????????????????????????
                            if (!TextUtils.isEmpty(accUrl)) {
                                playUrl = accUrl;
                            }
                        }
                        if (mLiveLinkMicAnchorPresenter != null) {
                            mLiveLinkMicAnchorPresenter.applyLinkMicAnchor(pkUid, playUrl, mStream);
                        }
                    } else {
                        if (mLiveLinkMicAnchorPresenter != null) {
                            mLiveLinkMicAnchorPresenter.applyLinkMicAnchor(pkUid, null, mStream);
                        }
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    /**
     * ????????????pk??????????????????
     */
    public void setPkBtnVisible(boolean visible) {
        if (mLiveAnchorViewHolder != null) {
            if (visible) {
                if (mLiveLinkMicAnchorPresenter.isLinkMic()) {
                    mLiveAnchorViewHolder.setPkBtnVisible(true);
                }
            } else {
                mLiveAnchorViewHolder.setPkBtnVisible(false);
            }
        }
    }

    /**
     * ??????????????????pk
     */
    public void applyLinkMicPk() {
        String pkUid = null;
        if (mLiveLinkMicAnchorPresenter != null) {
            pkUid = mLiveLinkMicAnchorPresenter.getPkUid();
        }
        if (!TextUtils.isEmpty(pkUid) && mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.applyLinkMicPk(pkUid, mStream);
        }
    }

    /**
     * ???????????????PK  ????????????????????????????????????PK???????????????
     *
     * @param u      ?????????????????????
     * @param stream ???????????????stream
     */
    @Override
    public void onLinkMicPkApply(UserBean u, String stream) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkApply(u, stream);
        }
    }

    /**
     * ???????????????PK  ??????????????????pk?????????
     */
    @Override
    public void onLinkMicPkRefuse() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkRefuse();
        }
    }

    /**
     * ???????????????PK   ??????????????????????????????
     */
    @Override
    public void onLinkMicPkBusy() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkBusy();
        }
    }

    /**
     * ???????????????PK   ??????????????????????????????
     */
    @Override
    public void onLinkMicPkNotResponse() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkNotResponse();
        }
    }

    /**
     * ??????sdk????????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLinkMicTxMixStreamEvent(LinkMicTxMixStreamEvent e) {
        if (mLivePushViewHolder != null && mLivePushViewHolder instanceof LivePushTxViewHolder) {
            ((LivePushTxViewHolder) mLivePushViewHolder).onLinkMicTxMixStreamEvent(e.getType(), e.getToStream());
        }
    }

    /**
     * ??????checkLive
     */
    public void checkLive() {
        if (mCheckLiveCallback == null) {
            mCheckLiveCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        int status = JSON.parseObject(info[0]).getIntValue("status");
                        printLog(DateFormatUtil.getCurTimeString2() + " <=== " + mReqCount + "----status=" + status + "\n");
                        if (status == 0) {
                            NotCancelableDialog dialog = new NotCancelableDialog();
                            dialog.setContent(WordUtil.getString(R.string.live_anchor_error));
                            dialog.setActionListener(new NotCancelableDialog.ActionListener() {
                                @Override
                                public void onConfirmClick(Context context, DialogFragment dialog) {
                                    dialog.dismiss();
                                    release();
                                    superBackPressed();
                                }
                            });
                            dialog.show(getSupportFragmentManager(), "VersionUpdateDialog");
                        }
                    }
                }

            };
        }
        mReqCount++;
        printLog(DateFormatUtil.getCurTimeString2() + " ===> " + mReqCount + "\n");
        LiveHttpUtil.anchorCheckLive(mLiveUid, mStream, mCheckLiveCallback);
    }


    private void printLog(String content) {
        if (mLogFile == null) {
            File dir = new File(CommonAppConfig.LOG_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            mLogFile = new File(dir, DateFormatUtil.getCurTimeString2() + "_" + mLiveUid + "_" + mStream + ".txt");
        }
//        L.e(TAG, content);
        LogUtil.print(mLogFile, content);
    }

    /**
     * ??????????????????
     */
    public void openGoodsWindow() {
        LiveShopDialogFragment fragment = new LiveShopDialogFragment();
//        fragment.setLifeCycleListener(this);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveShopDialogFragment");
    }


    public void forwardAddGoods() {
        if (mLiveGoodsAddViewHolder == null) {
            mLiveGoodsAddViewHolder = new LiveGoodsAddViewHolder(mContext, mPageContainer);
            mLiveGoodsAddViewHolder.subscribeActivityLifeCycle();
            mLiveGoodsAddViewHolder.addToParent();
        }
        mLiveGoodsAddViewHolder.show();
    }

    /**
     * ???????????????????????????
     */
    public void forwardAddPlatGoods() {
        if (mLivePlatGoodsAddViewHolder == null) {
            mLivePlatGoodsAddViewHolder = new LivePlatGoodsAddViewHolder(mContext, mPageContainer);
            mLivePlatGoodsAddViewHolder.subscribeActivityLifeCycle();
            mLivePlatGoodsAddViewHolder.addToParent();
        }
        mLivePlatGoodsAddViewHolder.show();
    }


    @Override
    protected boolean canBackPressed() {
        if (mLiveGoodsAddViewHolder != null && mLiveGoodsAddViewHolder.isShowed()) {
            mLiveGoodsAddViewHolder.hide();
            return false;
        }
        if (mLivePlatGoodsAddViewHolder != null && mLivePlatGoodsAddViewHolder.isShowed()) {
            mLivePlatGoodsAddViewHolder.hide();
            return false;
        }
        return super.canBackPressed();
    }

    /**
     * ??????????????????
     */
    public void showStickerGift(LiveReceiveGiftBean bean) {
        if (CommonAppConfig.getInstance().isMhBeautyEnable() && mLivePushViewHolder != null) {
            String stickerGiftId = bean.getStickerId();
            float playTime = bean.getPlayTime();
            if (!TextUtils.isEmpty(stickerGiftId) && playTime > 0) {
                mLivePushViewHolder.setLiveStickerGift(stickerGiftId, (long) (playTime * 1000));
            }
        }
    }


    /**
     * ????????????????????????????????????
     */
    public void sendLiveGoodsShow(GoodsBean goodsBean) {
        SocketChatUtil.sendLiveGoodsShow(mSocketClient, goodsBean);
    }


    /**
     * ???????????????--????????????????????????
     */
    public void controlMic() {
        LiveVoiceControlFragment fragment = new LiveVoiceControlFragment();
//        fragment.setLifeCycleListener(this);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_STREAM, mStream);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveVoiceControlFragment");
    }


    /**
     * ???????????????--??????????????????????????????
     */
    @Override
    public void onVoiceRoomApplyUpMic() {
        if (mLiveVoiceAnchorViewHolder != null) {
            mLiveVoiceAnchorViewHolder.setApplyUpMicTipShow(true);
        }
    }


    /**
     * ???????????????--??????????????????????????????????????????
     *
     * @param position ??????????????? ???0?????? -1????????????
     */
    public void handleMicUpApply(UserBean bean, int position) {
        SocketVoiceRoomUtil.handleMicUpApply(mSocketClient, bean, position);
    }


    /**
     * ???????????????--????????????????????????????????????
     *
     * @param uid ???????????????uid
     */
    @Override
    public void onVoiceRoomDownMic(String uid, int type) {
        if (mLiveVoiceLinkMicViewHolder != null) {
            int position = mLiveVoiceLinkMicViewHolder.getUserPosition(uid);
            if (position != -1) {
                mLiveVoiceLinkMicViewHolder.onUserDownMic(position);
                mLiveVoiceLinkMicViewHolder.stopPlay(position);//?????????????????????????????????
                EventBus.getDefault().post(new LiveVoiceMicStatusEvent(position, Constants.VOICE_CTRL_EMPTY));
                voiceRoomAnchorMix();//????????????
            }
        }
    }


    /**
     * ???????????????--?????????????????? ?????????????????????
     *
     * @param uid      ???????????????uid
     * @param position ??????
     * @param status   ??????????????? -1 ?????????  0????????? 1?????? ??? 2 ?????????
     */
    @Override
    public void onControlMicPosition(String uid, int position, int status) {
        super.onControlMicPosition(uid, position, status);
        EventBus.getDefault().post(new LiveVoiceMicStatusEvent(position, status));
    }


    /**
     * ???????????????--????????????????????????????????????????????????????????????????????????
     *
     * @param uid        ???????????????uid
     * @param pull       ???????????????????????????
     * @param userStream ???????????????????????????????????????
     */
    @Override
    public void onVoiceRoomPushSuccess(String uid, String pull, String userStream) {
        if (mLiveVoiceLinkMicViewHolder != null) {
            mLiveVoiceLinkMicViewHolder.playAccStream(uid, pull, userStream);
        }
        voiceRoomAnchorMix();//????????????
    }

    /**
     * ???????????????????????????
     */
    private void voiceRoomAnchorMix() {
        if (mLiveVoiceLinkMicViewHolder != null) {
            List<String> userStreamList = mLiveVoiceLinkMicViewHolder.getUserStreamForMix();
            if (mLiveVoicePushTxViewHolder != null) {
                mLiveVoicePushTxViewHolder.voiceRoomAnchorMix(userStreamList);
            }
        }
    }

}
