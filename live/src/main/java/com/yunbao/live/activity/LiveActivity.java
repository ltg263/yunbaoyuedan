package com.yunbao.live.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.chatroom.ui.dialog.LiveBillboardDialogFragment;
import com.yunbao.chatroom.ui.dialog.LiveChatListDialogFragment;
import com.yunbao.chatroom.ui.dialog.LiveChatRoomDialogFragment;
import com.yunbao.chatroom.ui.dialog.LiveGiftDialogFragment;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.ChatGiftBean;
import com.yunbao.common.bean.ChatReceiveGiftBean;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.GoodsBean;
import com.yunbao.common.bean.LiveGiftBean;
import com.yunbao.common.bean.UserBean;
//import com.yunbao.common.dialog.LiveChargeDialogFragment;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.event.CoinChangeEvent;
import com.yunbao.common.event.FollowEvent;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.mob.MobCallback;
import com.yunbao.common.mob.MobShareUtil;
import com.yunbao.common.mob.ShareData;
import com.yunbao.common.pay.PayPresenter;
import com.yunbao.common.presenter.GiftAnimViewHolder;
import com.yunbao.common.utils.KeyBoardUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordFilterUtil;
import com.yunbao.common.utils.WordUtil;
//import com.yunbao.game.util.GamePresenter;
//import com.yunbao.im.event.ImUnReadCountEvent;
//import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.im.bean.ImUserBean;
import com.yunbao.im.event.ImUnReadCountEvent;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.live.R;
import com.yunbao.live.bean.GlobalGiftBean;
import com.yunbao.live.bean.LiveBeanReal;
import com.yunbao.live.bean.LiveBuyGuardMsgBean;
import com.yunbao.common.bean.LiveChatBean;
import com.yunbao.live.bean.LiveDanMuBean;
import com.yunbao.live.bean.LiveEnterRoomBean;
import com.yunbao.live.bean.LiveGiftPrizePoolWinBean;
import com.yunbao.live.bean.LiveGuardInfo;
import com.yunbao.live.bean.LiveLuckGiftWinBean;
import com.yunbao.live.bean.LiveReceiveGiftBean;
import com.yunbao.live.bean.LiveUserGiftBean;
import com.yunbao.live.bean.LiveVoiceGiftBean;
import com.yunbao.live.bean.LiveVoiceLinkMicBean;
import com.yunbao.live.bean.TurntableGiftBean;
import com.yunbao.live.dialog.DailyTaskDialogFragment;
import com.yunbao.live.dialog.GiftPrizePoolFragment;
import com.yunbao.live.dialog.LiveGuardBuyDialogFragment;
import com.yunbao.live.dialog.LiveGuardDialogFragment;
import com.yunbao.live.dialog.LiveInputDialogFragment;
import com.yunbao.live.dialog.LiveRedPackListDialogFragment;
import com.yunbao.live.dialog.LiveRedPackSendDialogFragment;
import com.yunbao.live.dialog.LiveShareDialogFragment;
import com.yunbao.live.dialog.LiveVoiceApplyUpFragment;
import com.yunbao.live.dialog.LuckPanDialogFragment;
import com.yunbao.live.dialog.LuckPanRecordDialogFragment;
import com.yunbao.live.dialog.LuckPanTipDialogFragment;
import com.yunbao.live.dialog.LuckPanWinDialogFragment;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.presenter.LiveLinkMicAnchorPresenter;
import com.yunbao.live.presenter.LiveLinkMicPkPresenter;
import com.yunbao.live.presenter.LiveLinkMicPresenter;
import com.yunbao.live.socket.SocketChatUtil;
import com.yunbao.live.socket.SocketClient;
import com.yunbao.live.socket.SocketMessageListener;
import com.yunbao.live.socket.SocketVoiceRoomUtil;
import com.yunbao.live.views.AbsLiveViewHolder;
import com.yunbao.live.views.LiveAddImpressViewHolder;
import com.yunbao.live.views.LiveAdminListViewHolder;
import com.yunbao.live.views.LiveContributeViewHolder;
import com.yunbao.live.views.LiveEndViewHolder;
import com.yunbao.live.views.LiveRoomViewHolder;
import com.yunbao.live.views.LiveVoiceLinkMicViewHolder;
import com.yunbao.live.views.LiveWebViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/7.
 */

public abstract class LiveActivity extends AbsActivity implements SocketMessageListener, LiveShareDialogFragment.ActionListener, KeyBoardUtil.KeyBoardHeightListener {

    protected ViewGroup mContainer;
    protected ViewGroup mPageContainer;
    protected LiveRoomViewHolder mLiveRoomViewHolder;
    protected AbsLiveViewHolder mLiveBottomViewHolder;
    protected LiveAddImpressViewHolder mLiveAddImpressViewHolder;
    protected LiveContributeViewHolder mLiveContributeViewHolder;
    protected LiveWebViewHolder mLiveLuckGiftTipViewHolder;
    protected LiveWebViewHolder mLiveDaoGiftTipViewHolder;
    protected LiveAdminListViewHolder mLiveAdminListViewHolder;
    protected LiveEndViewHolder mLiveEndViewHolder;
    protected LiveLinkMicPresenter mLiveLinkMicPresenter;//???????????????????????????
    protected LiveLinkMicAnchorPresenter mLiveLinkMicAnchorPresenter;//???????????????????????????
    protected LiveLinkMicPkPresenter mLiveLinkMicPkPresenter;//???????????????PK??????
    //    protected GamePresenter mGamePresenter;
    protected SocketClient mSocketClient;
    protected LiveBeanReal mLiveBean;
    protected int mLiveSDK;//sdk??????  0??????  1??????
    protected String mTxAppId;//??????sdkAppId
    protected boolean mIsAnchor;//???????????????
    protected int mSocketUserType;//socket????????????  30 ????????????  40 ?????????  50 ??????  60??????
    protected String mStream;
    protected String mLiveUid;
    protected String mDanmuPrice;//????????????
    protected String mCoinName;//????????????
    protected int mLiveType;//??????????????????  ?????? ?????? ?????? ?????????
    protected int mLiveTypeVal;//????????????,??????????????????????????????
    protected KeyBoardUtil mKeyBoardUtil;
    protected int mChatLevel;//??????????????????
    protected int mDanMuLevel;//??????????????????
    private MobShareUtil mMobShareUtil;
    private boolean mFirstConnectSocket;//??????????????????????????????socket
    private boolean mGamePlaying;//??????????????????
    private boolean mChatRoomOpened;//????????????????????????????????????
    private LiveChatRoomDialogFragment mLiveChatRoomDialogFragment;//??????????????????
    protected LiveGuardInfo mLiveGuardInfo;
    //    private HashSet<DialogFragment> mDialogFragmentSet;
    private View mPkBg;
    private MobCallback mShareLiveCallback;
    protected boolean mVoiceChatRoom;//????????????????????????
    protected LiveVoiceLinkMicViewHolder mLiveVoiceLinkMicViewHolder;
    private PayPresenter mPayPresenter;
    protected boolean mTaskSwitch;
    protected String mAgentCode;



    @Override
    protected void main() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mIsAnchor = this instanceof LiveAnchorActivity;
        mPageContainer = (ViewGroup) findViewById(R.id.page_container);
        if(!EventBus.getDefault().isRegistered(this)){//????????????
            EventBus.getDefault().register(this);
        }
//        mDialogFragmentSet = new HashSet<>();
        mPkBg = findViewById(R.id.pk_bg);
    }

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }

    public ViewGroup getPageContainer() {
        return mPageContainer;
    }


//    @Override
//    public void onDialogFragmentShow(AbsDialogFragment dialogFragment) {
//        if (mDialogFragmentSet != null && dialogFragment != null) {
//            mDialogFragmentSet.add(dialogFragment);
//        }
//    }
//
//    @Override
//    public void onDialogFragmentHide(AbsDialogFragment dialogFragment) {
//        if (mDialogFragmentSet != null && dialogFragment != null) {
//            mDialogFragmentSet.remove(dialogFragment);
//        }
//    }

    private void hideDialogs() {
//        if (mDialogFragmentSet != null) {
//            for (DialogFragment dialogFragment : mDialogFragmentSet) {
//                if (dialogFragment != null) {
//                    dialogFragment.dismissAllowingStateLoss();
//                }
//            }
//        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> list = fragmentManager.getFragments();
        if (list != null && list.size() > 0) {
            for (Fragment fragment : list) {
                if (fragment != null && fragment instanceof AbsDialogFragment) {
                    ((AbsDialogFragment) fragment).dismissAllowingStateLoss();
                }
            }
        }
    }


    /**
     * ????????????socket?????????
     */
    @Override
    public void onConnect(boolean successConn) {
        if (successConn) {
            if (!mFirstConnectSocket) {
                mFirstConnectSocket = true;
                if (mLiveType == Constants.LIVE_TYPE_PAY || mLiveType == Constants.LIVE_TYPE_TIME) {
                    SocketChatUtil.sendUpdateVotesMessage(mSocketClient, mLiveTypeVal, 1);
                }
                SocketChatUtil.getFakeFans(mSocketClient);
            }
        }
    }

    /**
     * ?????????socket??????
     */
    @Override
    public void onDisConnect() {

    }

    /**
     * ??????????????????
     */
    @Override
    public void onChat(LiveChatBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.insertChat(bean);
        }
        if (bean.getType() == LiveChatBean.LIGHT) {
            onLight();
        }
    }

    /**
     * ??????????????????
     */
    @Override
    public void onLight() {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.playLightAnim();
        }
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void onEnterRoom(LiveEnterRoomBean bean) {
        if (mLiveRoomViewHolder != null) {
            LiveUserGiftBean u = bean.getUserBean();
            mLiveRoomViewHolder.insertUser(u);
            mLiveRoomViewHolder.insertChat(bean.getLiveChatBean());
//            mLiveRoomViewHolder.onEnterRoom(bean);
        }
    }

    /**
     * ??????????????????????????????
     */
    @Override
    public void onLeaveRoom(UserBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.removeUser(bean.getId());
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceLeaveRoom(bean);
        }
    }

    /**
     * ??????????????????
     */
    @Override
    public void onSendGift(ChatReceiveGiftBean bean, LiveChatBean chatBean) {
        if(mLiveRoomViewHolder!=null){
            mLiveRoomViewHolder.showGiftMessage(bean);
        }
        //?????????????????????????????????
        if (chatBean != null) {
//            if (isVoiceChatRoom()) {
//                chatBean.setContent(String.format(WordUtil.getString(R.string.live_send_gift_6), bean.getToName(), bean.getGiftCount(), bean.getGiftName()));
//            } else {
//                chatBean.setContent(String.format(WordUtil.getString(R.string.live_send_gift_1), bean.getGiftCount(), bean.getGiftName()));
//            }
            onChat(chatBean);
        }
    }

    /**
     * pk ??????????????????
     *
     * @param leftGift  ??????????????????
     * @param rightGift ??????????????????
     */
    @Override
    public void onSendGiftPk(long leftGift, long rightGift) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onPkProgressChanged(leftGift, rightGift);
        }
    }

    /**
     * ??????????????????
     */
    @Override
    public void onSendDanMu(LiveDanMuBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.showDanmu(bean);
        }
    }

    /**
     * ??????????????????????????????
     */
    @Override
    public void onLiveEnd() {
        hideDialogs();
    }

    /**
     * ?????????  ??????????????????
     */
    @Override
    public void onAnchorInvalid() {
        hideDialogs();
    }

    /**
     * ?????????????????????
     */
    @Override
    public void onSuperCloseLive() {
        hideDialogs();
    }

    /**
     * ??????
     */
    @Override
    public void onKick(String touid) {

    }

    /**
     * ??????
     */
    @Override
    public void onShutUp(String touid, String content) {

    }

    /**
     * ????????????????????????
     */
    @Override
    public void onSetAdmin(String toUid, int isAdmin) {
        if (!TextUtils.isEmpty(toUid) && toUid.equals(CommonAppConfig.getInstance().getUid())) {
            mSocketUserType = isAdmin == 1 ? Constants.SOCKET_USER_TYPE_ADMIN : Constants.SOCKET_USER_TYPE_NORMAL;
        }
    }

    /**
     * ??????????????????????????????????????????????????????????????????
     */
    @Override
    public void onChangeTimeCharge(int typeVal) {

    }

    /**
     * ??????????????????????????????????????????
     */
    @Override
    public void onUpdateVotes(String uid, String deltaVal, int first) {
        if (!CommonAppConfig.getInstance().getUid().equals(uid) || first != 1) {
            if (mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.updateVotes(deltaVal);
            }
        }
    }

    /**
     * ???????????????
     */
    @Override
    public void addFakeFans(List<LiveUserGiftBean> list) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.insertUser(list);
        }
    }

    /**
     * ?????????  ????????????????????????
     */
    @Override
    public void onBuyGuard(LiveBuyGuardMsgBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onGuardInfoChanged(bean);
            LiveChatBean chatBean = new LiveChatBean();
//            chatBean.setContent(bean.getUserName() + WordUtil.getString(R.string.guard_buy_msg));
            chatBean.setType(LiveChatBean.SYSTEM);
            mLiveRoomViewHolder.insertChat(chatBean);
        }
    }

    /**
     * ????????? ??????????????????
     */
    @Override
    public void onRedPack(LiveChatBean liveChatBean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setRedPackBtnVisible(true);
            mLiveRoomViewHolder.insertChat(liveChatBean);
        }
    }

    /**
     * ?????????????????????  ?????????????????????????????????
     */
    @Override
    public void onAudienceApplyLinkMic(UserBean u) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceApplyLinkMic(u);
        }
    }

    /**
     * ?????????????????????  ?????????????????????????????????socket
     */
    @Override
    public void onAnchorAcceptLinkMic() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorAcceptLinkMic();
        }
    }

    /**
     * ?????????????????????  ?????????????????????????????????socket
     */
    @Override
    public void onAnchorRefuseLinkMic() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorRefuseLinkMic();
        }
    }

    /**
     * ?????????????????????  ???????????????????????????????????????
     */
    @Override
    public void onAudienceSendLinkMicUrl(String uid, String uname, String playUrl) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceSendLinkMicUrl(uid, uname, playUrl);
        }

    }

    /**
     * ?????????????????????  ???????????????????????????
     */
    @Override
    public void onAnchorCloseLinkMic(String touid, String uname) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorCloseLinkMic(touid, uname);
        }
    }

    /**
     * ?????????????????????  ????????????????????????
     */
    @Override
    public void onAudienceCloseLinkMic(String uid, String uname) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceCloseLinkMic(uid, uname);
        }
    }

    /**
     * ?????????????????????  ?????????????????????
     */
    @Override
    public void onAnchorNotResponse() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorNotResponse();
        }
    }

    /**
     * ?????????????????????  ???????????????
     */
    @Override
    public void onAnchorBusy() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorBusy();
        }
    }

    /**
     * ?????????????????????  ?????????????????????????????????????????????????????????
     */
    @Override
    public void onLinkMicAnchorApply(UserBean u, String stream) {
        //??????????????????????????????
    }

    /**
     * ?????????????????????  ???????????????????????????????????????????????????
     *
     * @param playUrl ???????????????????????????
     * @param pkUid   ???????????????uid
     */
    @Override
    public void onLinkMicAnchorPlayUrl(String pkUid, String playUrl) {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorPlayUrl(pkUid, playUrl);
        }
        if (this instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) this).onLinkMicTxAnchor(true);
        }
        setPkBgVisible(true);
    }

    /**
     * ?????????????????????  ?????????????????????
     */
    @Override
    public void onLinkMicAnchorClose() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorClose();
        }
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkClose();
        }
        if (this instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) this).onLinkMicTxAnchor(false);
        }
        setPkBgVisible(false);
    }

    /**
     * ?????????????????????  ?????????????????????????????????
     */
    @Override
    public void onLinkMicAnchorRefuse() {
        //??????????????????????????????
    }

    /**
     * ?????????????????????  ??????????????????????????????
     */
    @Override
    public void onLinkMicAnchorNotResponse() {
        //??????????????????????????????
    }

    /**
     * ?????????????????????  ????????????????????????
     */
    @Override
    public void onlinkMicPlayGaming() {
        //??????????????????????????????
    }

    /**
     * ?????????????????????  ??????????????????????????????
     */
    @Override
    public void onLinkMicAnchorBusy() {
        //??????????????????????????????
    }

    /**
     * ???????????????PK  ????????????????????????????????????PK???????????????
     *
     * @param u      ?????????????????????
     * @param stream ???????????????stream
     */
    @Override
    public void onLinkMicPkApply(UserBean u, String stream) {
        //??????????????????????????????
    }

    /**
     * ???????????????PK ???????????????PK???????????????
     */
    @Override
    public void onLinkMicPkStart(String pkUid) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkStart(pkUid);
        }
    }

    /**
     * ???????????????PK  ???????????????????????????pk?????????
     */
    @Override
    public void onLinkMicPkClose() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkClose();
        }
    }

    /**
     * ???????????????PK  ??????????????????pk?????????
     */
    @Override
    public void onLinkMicPkRefuse() {
        //??????????????????????????????
    }

    /**
     * ???????????????PK   ??????????????????????????????
     */
    @Override
    public void onLinkMicPkBusy() {
        //??????????????????????????????
    }

    /**
     * ???????????????PK   ??????????????????????????????
     */
    @Override
    public void onLinkMicPkNotResponse() {
        //??????????????????????????????
    }

    /**
     * ???????????????PK   ???????????????PK???????????????
     */
    @Override
    public void onLinkMicPkEnd(String winUid) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkEnd(winUid);
        }
    }


    /**
     * ???????????????????????????
     */
    @Override
    public void onAudienceLinkMicExitRoom(String touid) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceLinkMicExitRoom(touid);
        }
    }


    /**
     * ??????????????????
     */
    @Override
    public void onLuckGiftWin(LiveLuckGiftWinBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onLuckGiftWin(bean);
        }
    }

    /**
     * ????????????
     */
    @Override
    public void onPrizePoolWin(LiveGiftPrizePoolWinBean bean) {
        if (!isVoiceChatRoom() && mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onPrizePoolWin(bean);
        }
    }


    /**
     * ????????????
     */
    @Override
    public void onPrizePoolUp(String level) {
        if (!isVoiceChatRoom() && mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onPrizePoolUp(level);
        }
    }

    /**
     * ????????????
     */
    @Override
    public void onGlobalGift(GlobalGiftBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onGlobalGift(bean);
        }
    }

    /**
     * ?????????????????????
     */
    @Override
    public void onLiveGoodsShow(GoodsBean bean) {
        if (!mIsAnchor && mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setShowGoodsBean(bean);
        }
    }


    /**
     * ?????????????????????
     */
    @Override
    public void onLiveGoodsFloat(String userName) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onLiveGoodsFloat(userName);
        }
    }


    /**
     * ???????????????--??????????????????????????????
     */
    @Override
    public void onVoiceRoomApplyUpMic() {
        //??????????????????????????????
    }


    /**
     * ???????????????--????????????????????????????????????????????????
     *
     * @param toUid    ???????????????uid
     * @param toName   ???????????????name
     * @param toAvatar ?????????????????????
     * @param position ???????????????????????????0?????? -1??????????????????
     */
    @Override
    public void onVoiceRoomHandleApply(String toUid, String toName, String toAvatar, int position) {
        if (position >= 0) {
            if (mLiveVoiceLinkMicViewHolder != null) {
                mLiveVoiceLinkMicViewHolder.onUserUpMic(toUid, toName, toAvatar, position);
            }
        }
    }


    /**
     * ???????????????--????????????????????????????????????
     *
     * @param uid  ???????????????uid
     * @param type 0??????????????????  1???????????????  2??????????????????
     */
    @Override
    public void onVoiceRoomDownMic(String uid, int type) {
        //????????????
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
        if (mLiveVoiceLinkMicViewHolder != null) {
            mLiveVoiceLinkMicViewHolder.onControlMicPosition(position, status);
        }
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
        //????????????
    }

    /**
     * ???????????????--???????????????????????????????????????
     *
     * @param uid       ???????????????uid
     * @param faceIndex ????????????
     */
    @Override
    public void onVoiceRoomFace(String uid, int faceIndex) {
        if (mLiveVoiceLinkMicViewHolder != null) {
            mLiveVoiceLinkMicViewHolder.onVoiceRoomFace(uid, faceIndex);
        }
    }


    @Override
    public void onGameZjh(JSONObject obj) {
//        if (mGamePresenter != null) {
//            mGamePresenter.onGameZjhSocket(obj);
//        }
    }

    @Override
    public void onGameHd(JSONObject obj) {
//        if (mGamePresenter != null) {
//            mGamePresenter.onGameHdSocket(obj);
//        }
    }

    @Override
    public void onGameZp(JSONObject obj) {
//        if (mGamePresenter != null) {
//            mGamePresenter.onGameZpSocket(obj);
//        }
    }

    @Override
    public void onGameNz(JSONObject obj) {
//        if (mGamePresenter != null) {
//            mGamePresenter.onGameNzSocket(obj);
//        }
    }

    @Override
    public void onGameEbb(JSONObject obj) {
//        if (mGamePresenter != null) {
//            mGamePresenter.onGameEbbSocket(obj);
//        }
    }

    /**
     * ?????????????????????
     */
    public void openChatWindow(String atName) {
        if (mKeyBoardUtil == null) {
            mKeyBoardUtil = new KeyBoardUtil(super.findViewById(android.R.id.content), this);
        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.chatScrollToBottom();
        }
        LiveInputDialogFragment fragment = new LiveInputDialogFragment();
//        fragment.setLifeCycleListener(this);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_DANMU_PRICE, mDanmuPrice);
        bundle.putString(Constants.COIN_NAME, mCoinName);
        bundle.putString(Constants.AT_NAME, atName);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveInputDialogFragment");
    }

    /**
     * ????????????????????????
     */
    public void openChatListWindow() {

        LiveChatListDialogFragment fragment = new LiveChatListDialogFragment();
        fragment.setClickCallback(new LiveChatListDialogFragment.ClickCallback() {
            @Override
            public void openChatRoomWindow(ImUserBean bean) {
                openChatRoomDialog(bean, bean.getIsFollow() == 1);
            }
        });
        fragment.show(getSupportFragmentManager(), "LiveChatListDialogFragment");
    }

    public String getLiveUid() {
        return mLiveUid;
    }

    /**
     * ????????????????????????
     */
    public void openChatRoomDialog(UserBean userBean, boolean following) {
        if (mKeyBoardUtil == null) {
            mKeyBoardUtil = new KeyBoardUtil(super.findViewById(android.R.id.content), this);
        }
        LiveChatRoomDialogFragment fragment = new LiveChatRoomDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.USER_BEAN, userBean);
        bundle.putBoolean(Constants.FOLLOW, following);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveChatRoomDialogFragment");
    }

    /**
     * ??? ?????? ??????
     */
    public void sendDanmuMessage(String content) {
        if (!mIsAnchor) {
            UserBean u = CommonAppConfig.getInstance().getUserBean();
            if (u != null && u.getLevel() < mDanMuLevel) {
                ToastUtil.show(String.format(WordUtil.getString(R.string.live_level_danmu_limit), mDanMuLevel));
                return;
            }
        }
//        content = WordFilterUtil.getInstance().filter(content);
        LiveHttpUtil.sendDanmu(content, mLiveUid, mStream, mDanmuCallback);
    }

    private HttpCallback mDanmuCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                UserBean u = CommonAppConfig.getInstance().getUserBean();
                if (u != null) {
                    u.setLevel(obj.getIntValue("level"));
                    String coin = obj.getString("coin");
                    u.setCoin(coin);
                    onCoinChanged(coin);
                }
                SocketChatUtil.sendDanmuMessage(mSocketClient, obj.getString("barragetoken"));
            } else {
                ToastUtil.show(msg);
            }
        }
    };


    /**
     * ??? ?????? ??????
     */
    public void sendChatMessage(String content) {
        if (!mIsAnchor) {
            UserBean u = CommonAppConfig.getInstance().getUserBean();
            if (u != null && u.getLevel() < mChatLevel) {
                ToastUtil.show(String.format(WordUtil.getString(R.string.live_level_chat_limit), mChatLevel));
                return;
            }
        }
        int guardType = mLiveGuardInfo != null ? mLiveGuardInfo.getMyGuardType() : Constants.GUARD_TYPE_NONE;
        SocketChatUtil.sendChatMessage(mSocketClient, content, mIsAnchor, mSocketUserType, guardType);
    }

    /**
     * ??? ?????? ??????
     */
    public void sendSystemMessage(String content) {
        SocketChatUtil.sendSystemMessage(mSocketClient, content);
    }

    /**
     * ??? ????????? ??????
     */
    public void sendGiftMessage(LiveGiftBean giftBean, String giftToken, String paintedPath, int paintedWidth, int paintedHeight) {
        String liveName = "";
        if (mLiveBean != null) {
            liveName = mLiveBean.getUserNiceName();
        }
        int type = giftBean.getType();
        if (type == LiveGiftBean.TYPE_DRAW) {
            SocketChatUtil.sendGiftMessage(mSocketClient, type, giftToken, mLiveUid, liveName, paintedPath, paintedWidth, paintedHeight);
        } else {
            SocketChatUtil.sendGiftMessage(mSocketClient, type, giftToken, mLiveUid, liveName);
        }

    }

    /**
     * ??? ????????? ??????
     */
    private void sendGiftMessage(int giftType, String giftToken) {
        String liveName = "";
        if (mLiveBean != null) {
            liveName = mLiveBean.getUserNiceName();
        }
        SocketChatUtil.sendGiftMessage(mSocketClient, giftType, giftToken, mLiveUid, liveName);
    }

    /**
     * ????????????????????????
     */
    public void kickUser(String toUid, String toName) {
        SocketChatUtil.sendKickMessage(mSocketClient, toUid, toName);
    }

    /**
     * ??????
     */
    public void setShutUp(String toUid, String toName, int type) {
        SocketChatUtil.sendShutUpMessage(mSocketClient, toUid, toName, type);
    }

    /**
     * ??????????????????????????????
     */
    public void sendSetAdminMessage(int action, String toUid, String toName) {
        SocketChatUtil.sendSetAdminMessage(mSocketClient, action, toUid, toName);
    }


    /**
     * ?????????????????????
     */
    public void superCloseRoom() {
        SocketChatUtil.superCloseRoom(mSocketClient);
    }

    /**
     * ?????????????????????
     */
    public void sendUpdateVotesMessage(int deltaVal) {
        SocketChatUtil.sendUpdateVotesMessage(mSocketClient, deltaVal);
    }


    /**
     * ??????????????????????????????
     */
    public void sendBuyGuardMessage(String votes, int guardNum, int guardType) {
        SocketChatUtil.sendBuyGuardMessage(mSocketClient, votes, guardNum, guardType);
    }

    /**
     * ???????????????????????????
     */
    public void sendRedPackMessage() {
        SocketChatUtil.sendRedPackMessage(mSocketClient);
    }


    /**
     * ????????????????????????
     */
    public void openAddImpressWindow(String toUid) {
        if (mLiveAddImpressViewHolder == null) {
            mLiveAddImpressViewHolder = new LiveAddImpressViewHolder(mContext, mPageContainer);
            mLiveAddImpressViewHolder.subscribeActivityLifeCycle();
        }
        mLiveAddImpressViewHolder.addToParent();
        mLiveAddImpressViewHolder.setToUid(toUid);
        mLiveAddImpressViewHolder.show();
    }

    /**
     * ????????????????????????
     */
    public void openContributeWindow() {
//        if (mLiveContributeViewHolder == null) {
//            mLiveContributeViewHolder = new LiveContributeViewHolder(mContext, mPageContainer);
//            mLiveContributeViewHolder.subscribeActivityLifeCycle();
//            mLiveContributeViewHolder.addToParent();
//        }
//        mLiveContributeViewHolder.show();
//        if (CommonAppConfig.LIVE_ROOM_SCROLL && !mIsAnchor) {
//            ((LiveAudienceActivity) this).setScrollFrozen(true);
//        }
        LiveBillboardDialogFragment liveBillboardDialogFragment = new LiveBillboardDialogFragment();
        liveBillboardDialogFragment.setLiveUid(mLiveUid);
        liveBillboardDialogFragment.setStream(mStream);
        liveBillboardDialogFragment.setIsLive(true);
        liveBillboardDialogFragment.show(getSupportFragmentManager());

    }


    /**
     * ????????????????????????
     */
    public void openAdminListWindow() {
        if (mLiveAdminListViewHolder == null) {
            mLiveAdminListViewHolder = new LiveAdminListViewHolder(mContext, mPageContainer, mLiveUid);
            mLiveAdminListViewHolder.subscribeActivityLifeCycle();
            mLiveAdminListViewHolder.addToParent();
        }
        mLiveAdminListViewHolder.show();
    }

    /**
     * ??????????????????
     */
    protected boolean canBackPressed() {
        if (mLiveContributeViewHolder != null && mLiveContributeViewHolder.isShowed()) {
            mLiveContributeViewHolder.hide();
            return false;
        }
        if (mLiveAddImpressViewHolder != null && mLiveAddImpressViewHolder.isShowed()) {
            mLiveAddImpressViewHolder.hide();
            return false;
        }
        if (mLiveAdminListViewHolder != null && mLiveAdminListViewHolder.isShowed()) {
            mLiveAdminListViewHolder.hide();
            return false;
        }
        if (mLiveLuckGiftTipViewHolder != null && mLiveLuckGiftTipViewHolder.isShowed()) {
            mLiveLuckGiftTipViewHolder.hide();
            return false;
        }
        if (mLiveDaoGiftTipViewHolder != null && mLiveDaoGiftTipViewHolder.isShowed()) {
            mLiveDaoGiftTipViewHolder.hide();
            return false;
        }
        return true;
    }

    /**
     * ??????????????????
     */
    public void openShareWindow() {
        LiveShareDialogFragment fragment = new LiveShareDialogFragment();
//        fragment.setLifeCycleListener(this);
        fragment.setActionListener(this);
        fragment.show(getSupportFragmentManager(), "LiveShareDialogFragment");
    }

    /**
     * ????????????????????????
     */
    @Override
    public void onItemClick(String type) {
        if (Constants.LINK.equals(type)) {
            copyLink();
        } else {
            if (mShareLiveCallback == null) {
                mShareLiveCallback = new MobCallback() {
                    @Override
                    public void onSuccess(Object data) {
                        LiveHttpUtil.dailyTaskShareLive();
                    }

                    @Override
                    public void onError() {
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onFinish() {
                    }
                };
            }
            shareLive(type, mShareLiveCallback);
        }
    }

    /**
     * ?????????????????????
     */
    private void copyLink() {
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean == null) {
            return;
        }
        String link = null;
        if (((LiveActivity) mContext).isVoiceChatRoom()) {
            link = configBean.getDownloadApkUrl();
        } else {
            link = StringUtil.contact(configBean.getLiveWxShareUrl(), mLiveUid);
        }
        if (TextUtils.isEmpty(link)) {
            return;
        }
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", link);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(R.string.copy_success);
    }


    /**
     * ???????????????
     */
    public void shareLive(String type, MobCallback callback) {
        if (mLiveBean == null) {
            return;
        }
        shareLive(type, mLiveBean.getTitle(), callback);
    }


    /**
     * ???????????????
     */
    public void shareLive(String type, String liveTitle, MobCallback callback) {
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean == null) {
            return;
        }
        if (mMobShareUtil == null) {
            mMobShareUtil = new MobShareUtil();
        }
        ShareData data = new ShareData();

        data.setTitle(configBean.getAgentShareTitle());
        data.setDes(configBean.getAgentShareDes());

//        String liveShareTitle = configBean.getLiveShareTitle();
//        if (!TextUtils.isEmpty(liveShareTitle) && liveShareTitle.contains("{username}")) {
//            liveShareTitle = liveShareTitle.replace("{username}", mLiveBean.getUserNiceName());
//        }
//        data.setTitle(liveShareTitle);
//        if (TextUtils.isEmpty(liveTitle)) {
//            data.setDes(configBean.getLiveShareDes());
//        } else {
//            data.setDes(liveTitle);
//        }
        data.setImgUrl(mLiveBean.getAvatarThumb());
        String webUrl = HtmlConfig.MAKE_MONEY + mAgentCode;
//        String webUrl = configBean.getDownloadApkUrl();
//        if (!isVoiceChatRoom()) {
//            if (Constants.MOB_WX.equals(type) || Constants.MOB_WX_PYQ.equals(type)) {
//                webUrl = StringUtil.contact(configBean.getLiveWxShareUrl(), mLiveUid);
//            }
//        }
        data.setWebUrl(webUrl);
        mMobShareUtil.execute(type, data, callback);
    }

    /**
     * ????????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (!TextUtils.isEmpty(mLiveUid) && mLiveUid.equals(e.getToUid())) {
            showFollow(e.getAttention());
        }
    }


    public void showFollow(int isAttention) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setAttention(isAttention);
        }
    }


    //    /**
//     * ???????????????????????????????????????
//     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(ImUnReadCountEvent e) {
        String unReadCount = e.getUnReadCount();
        if (e.isLiveChatRoom() && !TextUtils.isEmpty(unReadCount) && mLiveBottomViewHolder != null) {
            mLiveBottomViewHolder.setUnReadCount(unReadCount);
        }
    }
//

    /**
     * ??????????????????????????????
     */
    protected String getImUnReadCount() {
        return ImMessageUtil.getInstance().getLiveAllUnReadMsgCount();
//        return "0";
    }

    /**
     * ??????????????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinChangeEvent(CoinChangeEvent e) {
        onCoinChanged(e.getCoin());
        if (e.isChargeSuccess() && this instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) this).onChargeSuccess();
        }
    }

    public void onCoinChanged(String coin) {
//        if (mGamePresenter != null) {
//            mGamePresenter.setLastCoin(coin);
//        }
    }


    /**
     * ??????????????????
     */
    public void openGuardListWindow() {
        LiveGuardDialogFragment fragment = new LiveGuardDialogFragment();
//        fragment.setLifeCycleListener(this);
        fragment.setLiveGuardInfo(mLiveGuardInfo);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putBoolean(Constants.ANCHOR, mIsAnchor);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGuardDialogFragment");
    }

    /**
     * ???????????????????????????
     */
    public void openBuyGuardWindow() {
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream) || mLiveGuardInfo == null) {
            return;
        }
        LiveGuardBuyDialogFragment fragment = new LiveGuardBuyDialogFragment();
//        fragment.setLifeCycleListener(this);
        fragment.setLiveGuardInfo(mLiveGuardInfo);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.COIN_NAME, mCoinName);
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putString(Constants.STREAM, mStream);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGuardBuyDialogFragment");
    }

    /**
     * ????????????????????????
     */
    public void openRedPackSendWindow() {
        LiveRedPackSendDialogFragment fragment = new LiveRedPackSendDialogFragment();
//        fragment.setLifeCycleListener(this);
        fragment.setStream(mStream);
        //fragment.setCoinName(mCoinName);
        fragment.show(getSupportFragmentManager(), "LiveRedPackSendDialogFragment");
    }

    /**
     * ???????????????????????????
     */
    public void openRedPackListWindow() {
        LiveRedPackListDialogFragment fragment = new LiveRedPackListDialogFragment();
//        fragment.setLifeCycleListener(this);
        fragment.setStream(mStream);
        fragment.setCoinName(mCoinName);
        fragment.show(getSupportFragmentManager(), "LiveRedPackListDialogFragment");
    }


    /**
     * ??????????????????
     */
    public void openPrizePoolWindow() {
        GiftPrizePoolFragment fragment = new GiftPrizePoolFragment();
//        fragment.setLifeCycleListener(this);
        fragment.setLiveUid(mLiveUid);
        fragment.setStream(mStream);
        fragment.show(getSupportFragmentManager(), "GiftPrizePoolFragment");
    }

    /**
     * ????????????????????????
     */
    public void openLuckGiftTip() {
        if (mLiveLuckGiftTipViewHolder == null) {
            mLiveLuckGiftTipViewHolder = new LiveWebViewHolder(mContext, mPageContainer, HtmlConfig.LUCK_GIFT_TIP);
            mLiveLuckGiftTipViewHolder.subscribeActivityLifeCycle();
            mLiveLuckGiftTipViewHolder.addToParent();
        }
        mLiveLuckGiftTipViewHolder.show();
        if (CommonAppConfig.LIVE_ROOM_SCROLL && !mIsAnchor) {
            ((LiveAudienceActivity) this).setScrollFrozen(true);
        }
    }


    /**
     * ????????????????????????
     */
    public void openDaoGiftTip() {
        if (mLiveDaoGiftTipViewHolder == null) {
            mLiveDaoGiftTipViewHolder = new LiveWebViewHolder(mContext, mPageContainer, HtmlConfig.DAO_GIFT_TIP);
            mLiveDaoGiftTipViewHolder.subscribeActivityLifeCycle();
            mLiveDaoGiftTipViewHolder.addToParent();
        }
        mLiveDaoGiftTipViewHolder.show();
        if (CommonAppConfig.LIVE_ROOM_SCROLL && !mIsAnchor) {
            ((LiveAudienceActivity) this).setScrollFrozen(true);
        }
    }


    /**
     * ??????????????????
     */
    public void openGiftWindow() {
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream)) {
            return;
        }
//        LiveGiftDialogFragment fragment = new LiveGiftDialogFragment();
////        fragment.setLifeCycleListener(this);
//        fragment.setLiveGuardInfo(mLiveGuardInfo);
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.LIVE_UID, mLiveUid);
//        bundle.putString(Constants.LIVE_STREAM, mStream);
//        fragment.setArguments(bundle);
//        fragment.show(getSupportFragmentManager(), "LiveGiftDialogFragment");


        com.yunbao.chatroom.ui.dialog.LiveGiftDialogFragment chatGiftDialogFragment = new LiveGiftDialogFragment();
        chatGiftDialogFragment.setActionListener(new LiveGiftDialogFragment.ActionListener() {
            @Override
            public void onChargeClick() {
                RouteUtil.forwardMyCoin();
            }
        });
        chatGiftDialogFragment.setSendGiftActionListener(new LiveGiftDialogFragment.SendGiftActionListener() {
            @Override
            public void startSend(int giftId, String giftCount, HttpCallback httpCallback) {
                LiveHttpUtil.sendGift(mLiveUid, mStream, giftId, giftCount, httpCallback);
            }

            @Override
            public void onSendEnd(int giftType, String giftToken) {
                sendGiftMessage(giftType,giftToken);
            }

            @Override
            public boolean isLive() {
                return true;
            }


        });
        chatGiftDialogFragment.show(getSupportFragmentManager());
    }


    /**
     * ??????????????????
     */
    public void openLuckPanWindow() {
        LuckPanDialogFragment fragment = new LuckPanDialogFragment();
//        fragment.setLifeCycleListener(this);
        fragment.show(getSupportFragmentManager(), "LuckPanDialogFragment");
    }


    /**
     * ??????????????????
     *
     * @param winResultGiftBeanList
     */
    public void openLuckPanWinWindow(List<TurntableGiftBean> winResultGiftBeanList) {
        if (winResultGiftBeanList == null || winResultGiftBeanList.size() == 0)
            return;

        LuckPanWinDialogFragment fragment = new LuckPanWinDialogFragment();
        fragment.setTurntableResultGiftBeans(winResultGiftBeanList);
//        fragment.setLifeCycleListener(this);
        fragment.show(getSupportFragmentManager(), "LuckPanWinDialogFragment");
    }

    /**
     * ???????????? ????????????
     */
    public void openLuckPanTipWindow() {
        LuckPanTipDialogFragment fragment = new LuckPanTipDialogFragment();
//        fragment.setLifeCycleListener(this);

        fragment.show(getSupportFragmentManager(), "LuckPanTipDialogFragment");
    }

    /**
     * ????????????????????????
     */
    public void openLuckPanRecordWindow() {
        LuckPanRecordDialogFragment fragment = new LuckPanRecordDialogFragment();
//        fragment.setLifeCycleListener(this);
        fragment.show(getSupportFragmentManager(), "LuckPanRecordDialogFragment");
    }


    /**
     * ?????????????????????
     */
    @Override
    public void onKeyBoardHeightChanged(int keyboardHeight) {
        if (mChatRoomOpened) {//????????????????????????????????????
//            if (mLiveChatRoomDialogFragment != null) {
//                mLiveChatRoomDialogFragment.onKeyBoardHeightChanged(keyboardHeight);
//            }
        } else {
            if (mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.onKeyBoardChanged(keyboardHeight);
            }
        }
    }


    /**
     * ??????????????????
     */
    public void openDailyTaskWindow() {
        DailyTaskDialogFragment fragment = new DailyTaskDialogFragment();
        fragment.setLiveUid(mLiveUid);
//        fragment.setLifeCycleListener(this);
        fragment.show(getSupportFragmentManager(), "DailyTaskDialogFragment");
    }


    public void setChatRoomOpened(LiveChatRoomDialogFragment chatRoomDialogFragment, boolean chatRoomOpened) {
        mChatRoomOpened = chatRoomOpened;
        mLiveChatRoomDialogFragment = chatRoomDialogFragment;
    }

    /**
     * ??????????????????
     */
    public boolean isGamePlaying() {
        return mGamePlaying;
    }

    public void setGamePlaying(boolean gamePlaying) {
        mGamePlaying = gamePlaying;
    }

    /**
     * ??????????????????
     */
    public boolean isLinkMic() {
        return mLiveLinkMicPresenter != null && mLiveLinkMicPresenter.isLinkMic();
    }

    /**
     * ????????????????????????
     */
    public boolean isLinkMicAnchor() {
        return mLiveLinkMicAnchorPresenter != null && mLiveLinkMicAnchorPresenter.isLinkMic();
    }

    /**
     * ??????pk?????????
     */
    public void setPkBgVisible(boolean visible) {
        if (mPkBg != null) {
            if (visible) {
                if (mPkBg.getVisibility() != View.VISIBLE) {
                    mPkBg.setVisibility(View.VISIBLE);
                }
            } else {
                if (mPkBg.getVisibility() == View.VISIBLE) {
                    mPkBg.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.pause();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.resume();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.resume();
        }
    }

    protected void release() {
         if (EventBus.getDefault().isRegistered(this)) {//????????????
            EventBus.getDefault().unregister(this);
        }
        LiveHttpUtil.cancel(LiveHttpConsts.SEND_DANMU);
        if (mLiveVoiceLinkMicViewHolder != null) {
            mLiveVoiceLinkMicViewHolder.release();
        }
        if (mKeyBoardUtil != null) {
            mKeyBoardUtil.release();
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.release();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.release();
        }
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.release();
        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.release();
        }
        if (mLiveAddImpressViewHolder != null) {
            mLiveAddImpressViewHolder.release();
        }
        if (mLiveContributeViewHolder != null) {
            mLiveContributeViewHolder.release();
        }
        if (mLiveLuckGiftTipViewHolder != null) {
            mLiveLuckGiftTipViewHolder.release();
        }
        if (mMobShareUtil != null) {
            mMobShareUtil.release();
        }
        if (mPayPresenter != null) {
            mPayPresenter.release();
        }
        mLiveVoiceLinkMicViewHolder = null;
        mKeyBoardUtil = null;
        mLiveLinkMicPresenter = null;
        mLiveLinkMicAnchorPresenter = null;
        mLiveLinkMicPkPresenter = null;
        mLiveRoomViewHolder = null;
        mLiveAddImpressViewHolder = null;
        mLiveContributeViewHolder = null;
        mLiveLuckGiftTipViewHolder = null;
        mMobShareUtil = null;
        mPayPresenter = null;
        L.e("LiveActivity--------release------>");
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }

    public String getStream() {
        return mStream;
    }

    public String getTxAppId() {
        return mTxAppId;
    }

    /**
     * ????????????????????????
     */
    public boolean isVoiceChatRoom() {
//        return mVoiceChatRoom;
        return false;
    }


    public void setBtnFunctionDark() {
    }


    /**
     * ???????????? ????????????
     */
    public void voiceApplyUpMic() {
        LiveVoiceApplyUpFragment fragment = new LiveVoiceApplyUpFragment();
//        fragment.setLifeCycleListener(this);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_STREAM, mStream);
        bundle.putBoolean(Constants.ANCHOR, mIsAnchor);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveVoiceApplyUpFragment");
    }

    /**
     * ???????????????--?????????????????????????????????????????????
     */
    public List<LiveVoiceGiftBean> getVoiceGiftUserList() {
        List<LiveVoiceGiftBean> list = new ArrayList<>();
        LiveVoiceGiftBean allBean = new LiveVoiceGiftBean();
        allBean.setType(-2);
        list.add(allBean);

        LiveVoiceGiftBean anchorBean = new LiveVoiceGiftBean();
        anchorBean.setType(-1);
        anchorBean.setUid(mLiveUid);
        if (mLiveBean != null) {
            anchorBean.setAvatar(mLiveBean.getAvatar());
        }
        list.add(anchorBean);
        if (mLiveVoiceLinkMicViewHolder != null) {
            List<LiveVoiceGiftBean> userList = mLiveVoiceLinkMicViewHolder.getVoiceGiftUserList();
            if (userList != null && userList.size() > 0) {
                list.addAll(userList);
            }
        }
        return list;
    }


    /**
     * ????????????????????????
     */
    public void showUserDialog(String toUid) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.showUserDialog(toUid);
        }
    }

    /**
     * ???????????????--???????????????????????????
     *
     * @param toUid ??????????????????uid
     */
    public Integer checkVoiceRoomUserUpMic(String toUid) {
        if (mLiveVoiceLinkMicViewHolder != null) {
            LiveVoiceLinkMicBean bean = mLiveVoiceLinkMicViewHolder.getUserBean(toUid);
            if (bean != null) {
                return bean.getStatus();
            }
        }
        return null;
    }

    /**
     * ???????????????--?????????????????? ?????????????????????
     *
     * @param toUid    ???????????????uid
     * @param position ??????
     * @param status   ??????????????? -1 ?????????  0????????? 1?????? ??? 2 ?????????
     */
    public void controlMicPosition(String toUid, int position, int status) {
        SocketVoiceRoomUtil.controlMicPosition(mSocketClient, toUid, position, status);
    }


    /**
     * ???????????????--?????????????????????
     *
     * @param toUid ???????????????uid
     */
    public void changeVoiceMicOpen(final String toUid) {
        if (mLiveVoiceLinkMicViewHolder == null) {
            return;
        }
        final int position = mLiveVoiceLinkMicViewHolder.getUserPosition(toUid);
        if (position == -1) {
            return;
        }
        LiveVoiceLinkMicBean bean = mLiveVoiceLinkMicViewHolder.getUserBean(position);
        if (bean == null) {
            return;
        }
        Boolean isCurOpen = null;
        if (bean.getStatus() == Constants.VOICE_CTRL_OPEN) {
            isCurOpen = true;
        } else if (bean.getStatus() == Constants.VOICE_CTRL_CLOSE) {
            isCurOpen = false;
        }
        if (isCurOpen == null) {
            return;
        }
        //????????????????????????
        final int targetStatus = isCurOpen ? Constants.VOICE_CTRL_CLOSE : Constants.VOICE_CTRL_OPEN;
        LiveHttpUtil.changeVoiceMicOpen(mStream, position, isCurOpen ? 0 : 1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    controlMicPosition(toUid, position, targetStatus);
                    if (mIsAnchor) {
                        ToastUtil.show(msg);
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    /**
     * ???????????????--??????????????????????????????????????????
     */
    public void closeUserVoiceMic(final String toUid, final int type) {
        LiveHttpUtil.closeUserVoiceMic(mStream, toUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    SocketVoiceRoomUtil.userDownMic(mSocketClient, toUid, type);
                }
                ToastUtil.show(msg);
            }
        });
    }


    /**
     * ??????????????????
     */
    public void openChargeWindow() {
//        if (mPayPresenter == null) {
//            mPayPresenter = new PayPresenter(this);
//            mPayPresenter.setServiceNameAli(Constants.PAY_BUY_COIN_ALI);
//            mPayPresenter.setServiceNameWx(Constants.PAY_BUY_COIN_WX);
////            mPayPresenter.setServiceNamePaypal(Constants.PAY_BUY_COIN_PAYPAL);
//            mPayPresenter.setAliCallbackUrl(HtmlConfig.ALI_PAY_COIN_URL);
////            mPayPresenter.setServiceNamePaypal(Constants.PAY_BUY_COIN_PAYPAL);
//            mPayPresenter.setPayCallback(new PayCallback() {
//                @Override
//                public void onSuccess() {
//                    if (mPayPresenter != null) {
//                        mPayPresenter.checkPayResult();
//                    }
//                }
//
//                @Override
//                public void onFailed() {
//
//                }
//            });
//        }
//        LiveChargeDialogFragment fragment = new LiveChargeDialogFragment();
//        fragment.setLifeCycleListener(this);
//        fragment.setPayPresenter(mPayPresenter);
//        fragment.show(getSupportFragmentManager(), "ChatChargeDialogFragment");
    }
}
