package com.yunbao.live.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.opensource.svgaplayer.SVGAImageView;
import com.yunbao.chatroom.ui.dialog.LiveUserDialogFragment2;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.ChatReceiveGiftBean;
import com.yunbao.common.bean.GoodsBean;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.LiveChatBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.presenter.GiftAnimViewHolder;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ScreenDimenUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.live.R;
import com.yunbao.live.activity.LiveActivity;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.live.adapter.LiveChatAdapter;
import com.yunbao.live.adapter.LiveUserAdapter;
import com.yunbao.live.bean.GlobalGiftBean;
import com.yunbao.live.bean.LiveBuyGuardMsgBean;
import com.yunbao.live.bean.LiveDanMuBean;
import com.yunbao.live.bean.LiveEnterRoomBean;
import com.yunbao.live.bean.LiveGiftPrizePoolWinBean;
import com.yunbao.live.bean.LiveLuckGiftWinBean;
import com.yunbao.live.bean.LiveUserGiftBean;
import com.yunbao.live.custom.LiveLightView;
import com.yunbao.live.custom.TopGradual;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.presenter.LiveDanmuPresenter;
import com.yunbao.live.presenter.LiveEnterRoomAnimPresenter;
import com.yunbao.live.presenter.LiveGiftAnimPresenter;
import com.yunbao.live.presenter.LiveLightAnimPresenter;

import java.lang.ref.WeakReference;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

import static android.os.Build.ID;

/**
 * Created by cxf on 2018/10/9.
 * ?????????????????????
 */

public class LiveRoomViewHolder extends AbsViewHolder implements View.OnClickListener {

    private int mOffsetY;
    private ViewGroup mRoot;
    private ImageView mAvatar;
    private ImageView mLevelAnchor;
    private TextView mName;
    private TextView mID;
    private View mBtnFollow;
    private TextView mVotesName;//????????????
    private TextView mVotes;
    private TextView mGuardNum;//????????????
    private RecyclerView mUserRecyclerView;
    private RecyclerView mChatRecyclerView;
    private LiveUserAdapter mLiveUserAdapter;
    private LiveChatAdapter mLiveChatAdapter;
    private View mBtnRedPack;
    private String mLiveUid;
    private String mStream;
    private LiveLightAnimPresenter mLightAnimPresenter;
    private LiveEnterRoomAnimPresenter mLiveEnterRoomAnimPresenter;
    private LiveDanmuPresenter mLiveDanmuPresenter;
    private LiveGiftAnimPresenter mLiveGiftAnimPresenter;
    private GiftAnimViewHolder mGiftAnimViewHolder;
    private LiveRoomHandler mLiveRoomHandler;
    private HttpCallback mRefreshUserListCallback;
    private HttpCallback mTimeChargeCallback;
    protected int mUserListInterval;//?????????????????????????????????
    private GifImageView mGifImageView;
    private SVGAImageView mSVGAImageView;
    private ViewGroup mLiveGiftPrizePoolContainer;
    private TextView mLiveTimeTextView;//?????????????????????
    private long mAnchorLiveTime;//??????????????????
    private Handler mAnchorTimeHandler;
    private View mBtnVotes;
    private View mBtnGuard;
    private View mGroupGoods;
    private ImageView mGoodsThumb;
    private TextView mGoodsName;
    private TextView mGoodsPrice;
    private GoodsBean mShowGoodsBean;//??????????????????????????????
    private LiveRoomBtnViewHolder mBtnViewHolder;

    public LiveRoomViewHolder(Context context, ViewGroup parentView, GifImageView gifImageView, SVGAImageView svgaImageView, ViewGroup liveGiftPrizePoolContainer) {
        super(context, parentView);
        mGifImageView = gifImageView;
        mSVGAImageView = svgaImageView;
        mLiveGiftPrizePoolContainer = liveGiftPrizePoolContainer;
        View group_1 = findViewById(R.id.group_1);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) group_1.getLayoutParams();
        params.topMargin = ScreenDimenUtil.getInstance().getStatusBarHeight() + DpUtil.dp2px(5);
        group_1.requestLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_room;
    }

    @Override
    public void init() {
        mRoot = (ViewGroup) findViewById(R.id.root);
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mLevelAnchor = (ImageView) findViewById(R.id.level_anchor);
        mName = (TextView) findViewById(R.id.name);
        mID = (TextView) findViewById(R.id.id_val);
        mBtnFollow = findViewById(R.id.btn_follow);
        mVotesName = (TextView) findViewById(R.id.votes_name);
        mVotes = (TextView) findViewById(R.id.votes);
        mBtnVotes = findViewById(R.id.btn_votes);
        mBtnGuard = findViewById(R.id.btn_guard);
        mGuardNum = (TextView) findViewById(R.id.guard_num);
        if (CommonAppConfig.getInstance().getIsState()==1||CommonAppConfig.getInstance().getConfig().getLeaderBoardSwitch()==0){
            mBtnVotes.setVisibility(View.GONE);
        }
        //??????????????????
        mUserRecyclerView = (RecyclerView) findViewById(R.id.user_recyclerView);
        mUserRecyclerView.setHasFixedSize(true);
        mUserRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mLiveUserAdapter = new LiveUserAdapter(mContext);
        mLiveUserAdapter.setOnItemClickListener(new OnItemClickListener<UserBean>() {
            @Override
            public void onItemClick(UserBean bean, int position) {
                if(!canClick()){
                    return;
                }
                showUserDialog(bean.getId());
            }
        });
        mUserRecyclerView.setAdapter(mLiveUserAdapter);
        //?????????
        mChatRecyclerView = (RecyclerView) findViewById(R.id.chat_recyclerView);
        mChatRecyclerView.setHasFixedSize(true);
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mChatRecyclerView.addItemDecoration(new TopGradual());
        mLiveChatAdapter = new LiveChatAdapter(mContext);
        mLiveChatAdapter.setOnItemClickListener(new OnItemClickListener<LiveChatBean>() {
            @Override
            public void onItemClick(LiveChatBean bean, int position) {
                if(!canClick()){
                    return;
                }
                showUserDialog(bean.getId());
            }
        });
        mChatRecyclerView.setAdapter(mLiveChatAdapter);
        mVotesName.setText(R.string.charm_value);

        mGroupGoods = findViewById(R.id.group_goods);
        mGoodsThumb = findViewById(R.id.goods_thumb);
        mGoodsName = findViewById(R.id.goods_name);
        mGoodsPrice = findViewById(R.id.goods_price);
        findViewById(R.id.btn_goods_detail).setOnClickListener(this);

        mBtnFollow.setOnClickListener(this);
        mAvatar.setOnClickListener(this);
        mBtnVotes.setOnClickListener(this);
        mBtnGuard.setOnClickListener(this);
        mBtnRedPack = findViewById(R.id.btn_red_pack);
        mBtnRedPack.setOnClickListener(this);
        if (mContext instanceof LiveAudienceActivity) {
            if (!((LiveActivity) mContext).isVoiceChatRoom()) {
                mRoot.setOnClickListener(this);
            }
        } else {
            mLiveTimeTextView = (TextView) findViewById(R.id.live_time);
            mLiveTimeTextView.setVisibility(View.VISIBLE);
        }
        mLightAnimPresenter = new LiveLightAnimPresenter(mContext, mParentView);
        mLiveEnterRoomAnimPresenter = new LiveEnterRoomAnimPresenter(mContext, mContentView);
        mLiveRoomHandler = new LiveRoomHandler(this);
        mRefreshUserListCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mLiveUserAdapter != null) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        List<LiveUserGiftBean> list = JSON.parseArray(obj.getString("userlist"), LiveUserGiftBean.class);
                        mLiveUserAdapter.refreshList(list);
                    }
                }
            }
        };
        mTimeChargeCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (mContext instanceof LiveAudienceActivity) {
                    final LiveAudienceActivity liveAudienceActivity = (LiveAudienceActivity) mContext;
                    if (code == 0) {
                        liveAudienceActivity.roomChargeUpdateVotes();
                    } else {
                        if (mLiveRoomHandler != null) {
                            mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_TIME_CHARGE);
                        }
                        liveAudienceActivity.pausePlay();
                        if (code == 1008) {//????????????
                            liveAudienceActivity.setCoinNotEnough(true);
                            DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.live_coin_not_enough), false,
                                    new DialogUitl.SimpleCallback2() {
                                        @Override
                                        public void onConfirmClick(Dialog dialog, String content) {
                                            RouteUtil.forwardMyCoin(mContext);
                                        }

                                        @Override
                                        public void onCancelClick() {
                                            liveAudienceActivity.exitLiveRoom();
                                        }
                                    });
                        }
                    }
                }
            }
        };


        if (((LiveActivity) mContext).isVoiceChatRoom()) {
            View btnCloseVoice = findViewById(R.id.btn_close_voice);
            btnCloseVoice.setVisibility(View.VISIBLE);
            btnCloseVoice.setOnClickListener(this);
        }
    }


    /**
     * ??????????????????????????????
     *
     * @param showPan        ??????????????????
     * @param prizePoolLevel ????????????
     */
    public void showBtn(boolean showPan, int prizePoolLevel, int dailyTaskSwitch) {
        if (true) {
            return;
        }
        if (!showPan && prizePoolLevel < 0 && dailyTaskSwitch != 1) {
            return;
        }
        if (mBtnViewHolder == null) {
            mBtnViewHolder = new LiveRoomBtnViewHolder(mContext, (ViewGroup) findViewById(R.id.btn_container), showPan, prizePoolLevel, dailyTaskSwitch);
            mBtnViewHolder.addToParent();
            mBtnViewHolder.subscribeActivityLifeCycle();
        }
        mBtnViewHolder.showPrizeLevel(prizePoolLevel);
    }


    /**
     * ??????????????????
     */
    public void setAvatar(String url) {
        if (mAvatar != null) {
            ImgLoader.displayAvatar(mContext, url, mAvatar);
        }
    }

    /**
     * ??????????????????
     */
    public void setAnchorLevel(int anchorLevel) {
        if (mLevelAnchor != null) {
            LevelBean levelBean = CommonAppConfig.getInstance().getAnchorLevel(anchorLevel);
            if (levelBean != null) {
                ImgLoader.display(mContext, levelBean.getThumbIcon(), mLevelAnchor);
            }
        }
    }

    /**
     * ???????????????
     */
    public void setName(String name) {
        if (mName != null) {
            mName.setText(name);
        }
    }

    /**
     * ???????????????
     */
    public void setRoomNum(String roomNum) {
        if (mID != null) {
            mID.setText(roomNum);
        }
    }

    /**
     * ??????????????????
     */
    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            if (mLiveGiftAnimPresenter == null) {
                mLiveGiftAnimPresenter = new LiveGiftAnimPresenter(mContext, mContentView, mGifImageView, mSVGAImageView, mLiveGiftPrizePoolContainer);
            }
            mLiveGiftAnimPresenter.showLiveTitleAnim(title);
        }
    }


    /**
     * ??????????????????
     */
    public void setAttention(int attention) {
        if (mBtnFollow != null) {
            if (attention == 0) {
                if (mBtnFollow.getVisibility() != View.VISIBLE) {
                    mBtnFollow.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnFollow.getVisibility() == View.VISIBLE) {
                    mBtnFollow.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * ?????????????????????????????????
     */
    public void setUserList(List<LiveUserGiftBean> list) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.refreshList(list);
        }
    }

    /**
     * ?????????????????????
     */
    public void setVotes(String votes) {
        if (mVotes != null) {
            mVotes.setText(votes);
        }
    }

    /**
     * ????????????????????????
     */
    public void setGuardNum(int guardNum) {
        if (mGuardNum != null) {
            if (guardNum > 0) {
                mGuardNum.setText(guardNum + WordUtil.getString(R.string.ren));
            } else {
                mGuardNum.setText(R.string.main_list_no_data);
            }
        }
    }

    public void setLiveInfo(String liveUid, String stream, int userListInterval) {
        mLiveUid = liveUid;
        mStream = stream;
        mUserListInterval = userListInterval;
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.setLiveUid(liveUid);
        }
    }

    /**
     * ????????????????????????
     */
    public void onGuardInfoChanged(LiveBuyGuardMsgBean bean) {
        setGuardNum(bean.getGuardNum());
        setVotes(bean.getVotes());
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.onGuardChanged(bean.getUid(), bean.getGuardType());
        }
    }

    /**
     * ??????????????????????????????
     */
    public void setRedPackBtnVisible(boolean visible) {
        if (true) {
            return;
        }
        if (mBtnRedPack != null) {
            if (visible) {
                if (mBtnRedPack.getVisibility() != View.VISIBLE) {
                    mBtnRedPack.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnRedPack.getVisibility() == View.VISIBLE) {
                    mBtnRedPack.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.root) {
            light();
        }
        if (!canClick()) {
            return;
        }
        if (i == R.id.avatar) {
            showAnchorUserDialog();

        } else if (i == R.id.btn_follow) {
            follow();

        } else if (i == R.id.btn_votes) {
            if (!((AbsActivity) mContext).checkLogin()) {
                return;
            }
            openContributeWindow();

        } else if (i == R.id.btn_guard) {
            if (!((AbsActivity) mContext).checkLogin()) {
                return;
            }
            ((LiveActivity) mContext).openGuardListWindow();

        } else if (i == R.id.btn_red_pack) {
            if (!((AbsActivity) mContext).checkLogin()) {
                return;
            }
            ((LiveActivity) mContext).openRedPackListWindow();

        } else if (i == R.id.btn_goods_detail) {
            forwardGoods();
        } else if (i == R.id.btn_close_voice) {
            if (mContext instanceof LiveAnchorActivity) {
                ((LiveAnchorActivity) mContext).closeLive();
            } else if (mContext instanceof LiveAudienceActivity) {
                ((LiveAudienceActivity) mContext).onBackPressed();
            }
        }
    }

    /**
     * ???????????????????????????
     */
    private void forwardGoods() {
        if (mContext != null && mContext instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) mContext).liveGoodsFloat();
        }
        if (mShowGoodsBean != null) {
//            if (mShowGoodsBean.getType() == 1) {
//                RouteUtil.forwardGoodsDetailOutSide(mContext, mShowGoodsBean.getId(), false);
//            } else {
//                RouteUtil.forwardGoodsDetail(mContext, mShowGoodsBean.getId(), false, mLiveUid,null);
//            }
        }
    }

    /**
     * ????????????
     */
    public void follow() {
        if (!((AbsActivity) mContext).checkLogin()) {
            return;
        }
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        CommonHttpUtil.setAttention(mLiveUid, new CommonCallback<Integer>() {
            @Override
            public void callback(Integer isAttention) {
                if (isAttention == 1) {
                    ((LiveActivity) mContext).sendSystemMessage(
                            CommonAppConfig.getInstance().getUserBean().getUserNiceName() + WordUtil.getString(R.string.live_follow_anchor));
                }
            }
        });
    }

    /**
     * ????????????????????????????????????????????????
     */
    public void insertUser(LiveUserGiftBean bean) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.insertItem(bean);
        }
    }

    /**
     * ????????????????????????????????????
     */
    public void insertUser(List<LiveUserGiftBean> list) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.insertList(list);
        }
    }

    /**
     * ????????????????????????????????????????????????
     */
    public void removeUser(String uid) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.removeItem(uid);
        }
    }

    /**
     * ??????????????????
     */
    private void refreshUserList() {
        if (!TextUtils.isEmpty(mLiveUid) && mRefreshUserListCallback != null && mLiveUserAdapter != null) {
            LiveHttpUtil.cancel(LiveHttpConsts.GET_USER_LIST);
            LiveHttpUtil.getUserList(mLiveUid, mStream, mRefreshUserListCallback);
            startRefreshUserList();
        }
    }

    /**
     * ????????????????????????
     */
    public void startRefreshUserList() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageAtTime(LiveRoomHandler.WHAT_REFRESH_USER_LIST, getNextTime(mUserListInterval > 0 ? mUserListInterval : 60000));
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void requestTimeCharge() {
        if (!TextUtils.isEmpty(mLiveUid) && mTimeChargeCallback != null) {
            LiveHttpUtil.cancel(LiveHttpConsts.TIME_CHARGE);
            LiveHttpUtil.timeCharge(mLiveUid, mStream, mTimeChargeCallback);
            startRequestTimeCharge();
        }
    }

    /**
     * ???????????????????????????????????????
     */
    public void startRequestTimeCharge() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageAtTime(LiveRoomHandler.WHAT_TIME_CHARGE, getNextTime(60000));
        }
    }


    /**
     * ??????????????????????????????
     */
    public void insertChat(LiveChatBean bean) {
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.insertItem(bean);
        }
    }

    /**
     * ??????????????????
     */
    public void playLightAnim() {
        if (mLightAnimPresenter != null) {
            mLightAnimPresenter.play();
        }
    }

    /**
     * ??????
     */
    private void light() {
        ((LiveAudienceActivity) mContext).light();
    }

    public void setOffsetY(int offsetY) {
        LiveLightView.sOffsetY = offsetY;
        mOffsetY = offsetY;
    }


    /**
     * ??????????????????
     */
    public void onKeyBoardChanged(int keyBoardHeight) {
        if (mRoot != null) {
            if (keyBoardHeight == 0 || keyBoardHeight < 200) {
                mRoot.setTranslationY(0);
                return;
            }
            if (mOffsetY == 0) {
                mRoot.setTranslationY(-keyBoardHeight);
                return;
            }
            if (mOffsetY > 0 && mOffsetY < keyBoardHeight) {
                mRoot.setTranslationY(mOffsetY - keyBoardHeight);
            }
        }
    }

    /**
     * ????????????????????????
     */
    public void chatScrollToBottom() {
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.scrollToBottom();
        }
    }

    /**
     * ?????????????????? ????????????,????????????
     */
    public void onEnterRoom(LiveEnterRoomBean bean) {
        if (bean == null) {
            return;
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.enterRoom(bean);
        }
    }

    /**
     * ????????????
     */
    public void showDanmu(LiveDanMuBean bean) {
        if (mVotes != null) {
            mVotes.setText(bean.getVotes());
        }
        if (mLiveDanmuPresenter == null) {
            mLiveDanmuPresenter = new LiveDanmuPresenter(mContext, mParentView);
        }
        mLiveDanmuPresenter.showDanmu(bean);
    }

    /**
     * ?????????????????????????????????
     */
    private void showAnchorUserDialog() {
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        showUserDialog(mLiveUid);
    }

    /**
     * ????????????????????????
     */
    public void showUserDialog(final String toUid) {
        if(CommonAppConfig.getInstance().getUid().equals(toUid)){
            return;
        }
        LiveUserDialogFragment2 fragment = new LiveUserDialogFragment2();
        fragment.setToUid(toUid);
        fragment.setLiveUid(mLiveUid);
        fragment.setActionListener(new LiveUserDialogFragment2.ActionListener() {
            @Override
            public void requestData(HttpCallback httpCallback) {
                LiveHttpUtil.getLiveUser(toUid, mLiveUid, httpCallback);
            }

            @Override
            public void clickGift() {
                ((LiveActivity) mContext).openGiftWindow();
            }


            @Override
            public void clickMessage(UserBean bean) {
                ((LiveActivity) mContext). openChatRoomDialog(bean, bean.getIsFollow() == 1);
            }

            @Override
            public void setAdmin(String toUid, HttpCallback httpCallback) {
                LiveHttpUtil.setAdmin(mLiveUid, toUid, httpCallback);
            }

            @Override
            public void onSetAdmin(int res, String toUid, String toName) {
                ((LiveActivity) mContext).sendSetAdminMessage(res, toUid, toName);
            }

            @Override
            public void shutUp(final String toUid, final String toName) {
                LiveHttpUtil.setShutUp(mLiveUid, mStream, 1, toUid, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            ((LiveActivity) mContext).setShutUp(toUid, toName, 1);
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }

            @Override
            public void shutUpForever(final String toUid, final String toName) {
                LiveHttpUtil.setShutUp(mLiveUid, "0", 0, toUid, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            ((LiveActivity) mContext).setShutUp(toUid, toName, 0);
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }

            @Override
            public void kick(final String toUid, final String toName) {
                LiveHttpUtil.kicking(mLiveUid, toUid, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            ((LiveActivity) mContext).kickUser(toUid, toName);
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }

            @Override
            public void openAdminList() {
                ((LiveActivity) mContext).openAdminListWindow();
            }

            @Override
            public void report(String toUid) {
                Intent intent=new Intent();
                intent.setClassName(CommonAppConfig.PACKAGE_NAME, "com.yunbao.main.activity.UserReportActivity");
                intent.putExtra(ID,toUid);
                mContext.startActivity(intent);
            }

            @Override
            public void closeLive() {
                LiveHttpUtil.superCloseRoom(mLiveUid, 0, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                            ((LiveActivity) mContext).superCloseRoom();
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }

            @Override
            public void closeForbidLive() {
                LiveHttpUtil.superCloseRoom(mLiveUid, 1, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                            ((LiveActivity) mContext).superCloseRoom();
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }

            @Override
            public void closeForbidAccount() {
                LiveHttpUtil.superCloseRoom(mLiveUid, 2, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                            ((LiveActivity) mContext).superCloseRoom();
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }
        });
        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "LiveUserDialogFragment2");
    }

    /**
     * ????????????????????????
     */
    private void openContributeWindow() {
        ((LiveActivity) mContext).openContributeWindow();
    }


    /**
     * ??????????????????
     */
    public void showGiftMessage(ChatReceiveGiftBean bean) {
        mVotes.setText(bean.getVotes());
        if (mGiftAnimViewHolder == null) {
            mGiftAnimViewHolder = new GiftAnimViewHolder(mContext, (ViewGroup) findViewById(R.id.draw_gift_container));
            mGiftAnimViewHolder.addToParent();
        }
        mGiftAnimViewHolder.showGiftAnim(bean);
    }

    /**
     * ?????????????????????
     *
     * @param deltaVal ?????????????????????
     */
    public void updateVotes(String deltaVal) {
        if (mVotes == null) {
            return;
        }
        String votesVal = mVotes.getText().toString().trim();
        if (TextUtils.isEmpty(votesVal)) {
            return;
        }
        try {
            double votes = Double.parseDouble(votesVal);
            double addVotes = Double.parseDouble(deltaVal);
            votes += addVotes;
            mVotes.setText(StringUtil.format(votes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ViewGroup getInnerContainer() {
        return (ViewGroup) findViewById(R.id.inner_container);
    }


    /**
     * ????????????????????????
     */
    public void startAnchorLiveTime() {
        if (mAnchorTimeHandler == null) {
            mAnchorTimeHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    mAnchorLiveTime += 1000;
                    if (mLiveTimeTextView != null) {
                        mLiveTimeTextView.setText(StringUtil.getDurationText(mAnchorLiveTime));
                    }
                    if (mAnchorTimeHandler != null) {
                        long now = SystemClock.uptimeMillis();
                        long next = now + (1000 - now % 1000);
                        mAnchorTimeHandler.sendEmptyMessageAtTime(0, next);
                    }
                }
            };
        }
        long now = SystemClock.uptimeMillis();
        long next = now + (1000 - now % 1000);
        mAnchorTimeHandler.sendEmptyMessageAtTime(0, next);
    }


    /**
     * ??????????????????
     */
    public void startAnchorLight() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_ANCHOR_LIGHT);
            mLiveRoomHandler.sendEmptyMessageAtTime(LiveRoomHandler.WHAT_ANCHOR_LIGHT, getNextTime(500));
        }
    }


    /**
     * ??????checkLive
     */
    public void startAnchorCheckLive() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_ANCHOR_CHECK_LIVE);
            mLiveRoomHandler.sendEmptyMessageAtTime(LiveRoomHandler.WHAT_ANCHOR_CHECK_LIVE, getNextTime(60000));
        }
    }

    /**
     * ??????checkLive
     */
    private void anchorCheckLive() {
        if (mContext instanceof LiveAnchorActivity) {
            ((LiveAnchorActivity) mContext).checkLive();
            startAnchorCheckLive();
        }
    }


    /**
     * ??????????????????50??????????????????
     */
    public void anchorPause() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageAtTime(LiveRoomHandler.WHAT_ANCHOR_PAUSE, getNextTime(50000));
        }
    }

    private long getNextTime(int time) {
        long now = SystemClock.uptimeMillis();
        if (time < 1000) {
            return now + time;
        }
        return now + time + -now % 1000;
    }


    /**
     * ?????????????????????????????????
     */
    public void anchorResume() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_ANCHOR_PAUSE);
        }
    }

    /**
     * ??????????????????
     */
    private void anchorEndLive() {
        if (mContext != null && mContext instanceof LiveAnchorActivity) {
            ((LiveAnchorActivity) mContext).endLive();
        }
    }

    /**
     * ????????????
     */
    private void anchorLight() {
        if (mContext instanceof LiveAnchorActivity) {
            ((LiveAnchorActivity) mContext).light();
            startAnchorLight();
        }
    }


    /**
     * ??????????????????
     */
    public void onLuckGiftWin(LiveLuckGiftWinBean bean) {
        if (mLiveGiftAnimPresenter == null) {
            mLiveGiftAnimPresenter = new LiveGiftAnimPresenter(mContext, mContentView, mGifImageView, mSVGAImageView, mLiveGiftPrizePoolContainer);
        }
        mLiveGiftAnimPresenter.showLuckGiftWinAnim(bean);
    }


    /**
     * ????????????
     */
    public void onPrizePoolWin(LiveGiftPrizePoolWinBean bean) {
        if (mLiveGiftAnimPresenter == null) {
            mLiveGiftAnimPresenter = new LiveGiftAnimPresenter(mContext, mContentView, mGifImageView, mSVGAImageView, mLiveGiftPrizePoolContainer);
        }
        mLiveGiftAnimPresenter.showPrizePoolWinAnim(bean);
    }

    /**
     * ????????????
     */
    public void onPrizePoolUp(String level) {
//        if (mLiveGiftAnimPresenter == null) {
//            mLiveGiftAnimPresenter = new LiveGiftAnimPresenter(mContext, mContentView, mGifImageView, mSVGAImageView, mLiveGiftPrizePoolContainer);
//        }
//        mLiveGiftAnimPresenter.showPrizePoolUp(level);
    }


    /**
     * ????????????
     */
    public void onGlobalGift(GlobalGiftBean bean) {
        if (mLiveGiftAnimPresenter == null) {
            mLiveGiftAnimPresenter = new LiveGiftAnimPresenter(mContext, mContentView, mGifImageView, mSVGAImageView, mLiveGiftPrizePoolContainer);
        }
        mLiveGiftAnimPresenter.showGlobalGift(bean);
    }


    /**
     * ????????????????????????????????????
     */
    public void setShowGoodsBean(GoodsBean bean) {
        if (true) {
            return;
        }
        mShowGoodsBean = bean;
        if (bean == null) {
            if (mGoodsThumb != null) {
                mGoodsThumb.setImageDrawable(null);
            }
            if (mGoodsName != null) {
                mGoodsName.setText(null);
            }
            if (mGoodsPrice != null) {
                mGoodsPrice.setText(null);
            }
            if (mGroupGoods != null && mGroupGoods.getVisibility() != View.GONE) {
                mGroupGoods.setVisibility(View.GONE);
            }
        } else {
            if (mGroupGoods != null && mGroupGoods.getVisibility() != View.VISIBLE) {
                mGroupGoods.setVisibility(View.VISIBLE);
            }
            if (mGoodsThumb != null) {
                ImgLoader.display(mContext, bean.getThumb(), mGoodsThumb);
            }
            if (mGoodsName != null) {
                mGoodsName.setText(bean.getName());
            }
            if (mGoodsPrice != null) {
                mGoodsPrice.setText(bean.getPriceNow());
            }
        }
    }

    /**
     * ?????????????????????
     */
    public void onLiveGoodsFloat(String userName) {
        if (mLiveGiftAnimPresenter == null) {
            mLiveGiftAnimPresenter = new LiveGiftAnimPresenter(mContext, mContentView, mGifImageView, mSVGAImageView, mLiveGiftPrizePoolContainer);
        }
        mLiveGiftAnimPresenter.onLiveGoodsFloat(userName);
    }

    @Override
    public void release() {
        LiveHttpUtil.cancel(LiveHttpConsts.GET_USER_LIST);
        LiveHttpUtil.cancel(LiveHttpConsts.TIME_CHARGE);
        CommonHttpUtil.cancel(CommonHttpConsts.SET_ATTENTION);
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.release();
        }
        mLiveRoomHandler = null;
        if (mAnchorTimeHandler != null) {
            mAnchorTimeHandler.removeCallbacksAndMessages(null);
        }
        mAnchorTimeHandler = null;
        if (mLightAnimPresenter != null) {
            mLightAnimPresenter.release();
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.release();
        }
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.release();
        }
        if (mGiftAnimViewHolder != null) {
            mGiftAnimViewHolder.release();
        }
        mRefreshUserListCallback = null;
        mTimeChargeCallback = null;
        if (mBtnViewHolder != null) {
            mBtnViewHolder.release();
        }
        mBtnViewHolder = null;
    }

    public void clearData() {
        LiveHttpUtil.cancel(LiveHttpConsts.GET_USER_LIST);
        LiveHttpUtil.cancel(LiveHttpConsts.TIME_CHARGE);
        CommonHttpUtil.cancel(CommonHttpConsts.SET_ATTENTION);
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.removeCallbacksAndMessages(null);
        }
        if (mAvatar != null) {
            mAvatar.setImageDrawable(null);
        }
        if (mLevelAnchor != null) {
            mLevelAnchor.setImageDrawable(null);
        }
        if (mName != null) {
            mName.setText("");
        }
        if (mID != null) {
            mID.setText("");
        }
        if (mVotes != null) {
            mVotes.setText("");
        }
        if (mGuardNum != null) {
            mGuardNum.setText("");
        }
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.clear();
        }
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.clear();
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.cancelAnim();
            mLiveEnterRoomAnimPresenter.resetAnimView();
        }
        if (mLiveDanmuPresenter != null) {
            mLiveDanmuPresenter.release();
            mLiveDanmuPresenter.reset();
        }
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.cancelAllAnim();
        }
        if (mGiftAnimViewHolder != null) {
            mGiftAnimViewHolder.cancelAllAnim();
        }
        setShowGoodsBean(null);
    }


    private static class LiveRoomHandler extends Handler {

        private LiveRoomViewHolder mLiveRoomViewHolder;
        private static final int WHAT_REFRESH_USER_LIST = 1;
        private static final int WHAT_TIME_CHARGE = 2;//??????????????????????????????????????????
        private static final int WHAT_ANCHOR_PAUSE = 4;//???????????????
        private static final int WHAT_ANCHOR_LIGHT = 5;//????????????
        private static final int WHAT_ANCHOR_CHECK_LIVE = 6;//??????checkLive

        public LiveRoomHandler(LiveRoomViewHolder liveRoomViewHolder) {
            mLiveRoomViewHolder = new WeakReference<>(liveRoomViewHolder).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mLiveRoomViewHolder != null) {
                switch (msg.what) {
                    case WHAT_REFRESH_USER_LIST:
                        mLiveRoomViewHolder.refreshUserList();
                        break;
                    case WHAT_TIME_CHARGE:
                        mLiveRoomViewHolder.requestTimeCharge();
                        break;
                    case WHAT_ANCHOR_PAUSE:
                        mLiveRoomViewHolder.anchorEndLive();
                        break;
                    case WHAT_ANCHOR_LIGHT:
                        mLiveRoomViewHolder.anchorLight();
                        break;
                    case WHAT_ANCHOR_CHECK_LIVE:
                        mLiveRoomViewHolder.anchorCheckLive();
                        break;
                }
            }
        }

        public void release() {
            removeCallbacksAndMessages(null);
            mLiveRoomViewHolder = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LiveHttpUtil.cancel(LiveHttpConsts.GET_USER_LIST);
        LiveHttpUtil.cancel(LiveHttpConsts.TIME_CHARGE);
        CommonHttpUtil.cancel(CommonHttpConsts.SET_ATTENTION);
    }


}
