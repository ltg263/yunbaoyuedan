package com.yunbao.chatroom.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.ChatReceiveGiftBean;
import com.yunbao.common.bean.LiveChatBean;
import com.yunbao.common.bean.LiveInfo;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.business.liveobsever.DataChangeListner;
import com.yunbao.common.business.liveobsever.LifeObjectHolder;
import com.yunbao.common.custom.CheckImageView;
import com.yunbao.common.custom.DrawableTextView;
import com.yunbao.common.custom.ValueFrameAnimator;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.event.ShowLiveRoomFloatEvent;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.KeyBoardHeightChangeListener;
import com.yunbao.common.presenter.GiftAnimViewHolder;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.server.observer.LockClickObserver;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.FormatUtil;
import com.yunbao.common.utils.KeyBoardHeightUtil2;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ResourceUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.SystemUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.config.CallConfig;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.adapter.LiveChatAdapter;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.bean.LiveEndResultBean;
import com.yunbao.chatroom.business.behavior.factory.AbsBehaviorFactory;
import com.yunbao.chatroom.business.behavior.factory.CacheBehaviorFactory;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.business.live.presenter.LivePresenter;
import com.yunbao.chatroom.business.live.view.ILiveView;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.chatroom.business.socket.SocketProxy;
import com.yunbao.chatroom.business.socket.base.BaseSocketMessageLisnerImpl;
import com.yunbao.chatroom.business.socket.base.callback.BaseSocketMessageListner;
import com.yunbao.chatroom.business.socket.base.mannger.ChatMannger;
import com.yunbao.chatroom.event.LiveChatRoomBossPlaceOrderEvent;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.chatroom.ui.dialog.LiveBillboardDialogFragment;
import com.yunbao.chatroom.ui.dialog.LiveChatRoomDialogFragment;
import com.yunbao.chatroom.ui.dialog.LiveRoomDialogFragment;
import com.yunbao.chatroom.ui.dialog.LiveUserDialogFragment;
import com.yunbao.chatroom.ui.view.LiveEndViewHolder;
import com.yunbao.chatroom.ui.view.seat.AbsLiveSeatViewHolder;
import com.yunbao.chatroom.widet.BubbleLayout;
import com.yunbao.chatroom.widet.CustomPopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class LiveActivity<T extends BaseSocketMessageLisnerImpl, E extends SocketProxy, H extends AbsLiveSeatViewHolder> extends AbsActivity implements ILiveView, View.OnClickListener, BaseSocketMessageListner, KeyBoardHeightChangeListener {
    private static final String TAG = "LiveActivity";
    protected TextView mTvTitle;
    protected DrawableTextView mTvOnlineNum;
    protected TextView mTvId;
    protected ImageView mBtnNotice;
    protected LinearLayout mBtnCharm;
    protected TextView mCharmName;
    protected TextView mCharm;
    protected RoundedImageView mImgHostAvator;
    protected DrawableTextView mTvStatusHost;
    protected TextView mTvHostName;
    protected RecyclerView mReclyChat;
    protected FrameLayout mVpBottom;
    protected FrameLayout mVpGiftContainer;
    private ViewGroup mRootView;
    private ImageView mImgAvatorBg;
    private ImageView mImgRoomCover;
    private FrameLayout mVpSeatContainer;
    private CheckImageView mBtnCollect;


    protected LiveActivityLifeModel mLiveActivityLifeModel;
    private GiftAnimViewHolder mGiftAnimViewHolder;
    protected H mLiveSeatViewHolder;
    protected LiveChatAdapter mLiveChatAdapter; //聊天框
    protected LivePresenter mLivePresenter;
    protected E mSocketProxy;

    protected boolean mIsHost;
    protected int mLiveRole;
    protected LiveBean mLiveBean;  //全局操作一个LiveBean,通过合并进行数据更新
    protected T mSocketMessageBrider;

    private boolean mChatRoomOpened;//判断私信聊天窗口是否打开
    private LiveChatRoomDialogFragment mLiveChatRoomDialogFragment;//私信聊天窗口

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

    }


    @Override
    public void main() {
        super.main();
        CacheBehaviorFactory.getInstance().setAbsBehaviorFactory(onCreateBehaviorFactory());
        mLiveBean = getIntent().getParcelableExtra(Constants.DATA);
        if (mLiveBean == null) {
            finish();
        }
        /*一个LifecycleOwner对应一个LifeHolder 但是如果owner对象传入的是fragment就是另一个缓存了*/
        mLiveActivityLifeModel = LifeObjectHolder.getByContext(this, LiveActivityLifeModel.class);
        mLiveActivityLifeModel.setLiveBean(mLiveBean);
        mLiveActivityLifeModel.getSpeakStateObsever().addObsever(new DataChangeListner<Boolean>() {
            @Override
            public void change(Boolean aBoolean) {
                if (mLivePresenter != null) {
                    mLivePresenter.openSpeak(aBoolean);
                }
            }
        });

        mLiveActivityLifeModel.getLiveOnlineNumObserver().addObsever(new DataChangeListner<Integer>() {
            @Override
            public void change(Integer integer) {
                mTvOnlineNum.setText(WordUtil.getString(R.string.online_num, Integer.toString(integer)));
            }
        });
        initAndConnectSocket();
        initAndEnterSdkRoom();
        initView();
        setData();
            if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
    }

    private void setOnlineNum(){
        String liveUid=mLiveBean==null?null:mLiveBean.getUid();
        String steam=mLiveBean==null?null:mLiveBean.getStream();
        ChatRoomHttpUtil.getUserNums(liveUid,steam).subscribe(new DefaultObserver<Integer>() {
            @Override
            public void onNext(Integer integer) {
                L.e(TAG,"getUserNums--->"+integer);
                if (mLiveBean != null){
                    mLiveBean.setNums(integer);
                }
                mTvOnlineNum.setText(WordUtil.getString(R.string.online_num, Integer.toString(integer)));
            }
        });


    }

    /*初始化并且链接socket*/
    protected void initAndConnectSocket() {
        mSocketMessageBrider = initSocketMessageBride();
        mSocketProxy = onCreateSocketProxy(mLiveBean.getChatserver(), mSocketMessageBrider, mLiveBean.parseLiveInfo());
        mLiveActivityLifeModel.setSocketProxy(mSocketProxy);
        mSocketProxy.connect();
    }

    /*初始化Sdk并且进入sdkroom*/
    protected void initAndEnterSdkRoom() {
        mLiveRole = getRole();
        if (mLiveRole == Constants.ROLE_HOST) {
            mIsHost = true;
        }
        String pull = mLiveBean == null ? null : mLiveBean.getPull();
        mLivePresenter = new LivePresenter(this, pull, getRole());
        mLivePresenter.init();
        mLivePresenter.enterRoom(mLiveBean.getRoomId());
        if (mLiveRole == Constants.ROLE_AUDIENCE && !TextUtils.isEmpty(pull)) {
            EventBus.getDefault().post(new ShowLiveRoomFloatEvent(mLiveBean));
        }

    }

    protected void initView() {
        mRootView = (ViewGroup) findViewById(R.id.rootView);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvOnlineNum = (DrawableTextView) findViewById(R.id.tv_online_num);
        mTvId = (TextView) findViewById(R.id.tv_id);
        mBtnNotice = (ImageView) findViewById(R.id.btn_notice);
        mBtnCharm = (LinearLayout) findViewById(R.id.btn_charm);
        mCharmName = (TextView) findViewById(R.id.charm_name);
        mCharm = (TextView) findViewById(R.id.charm);
        mImgHostAvator = (RoundedImageView) findViewById(R.id.img_host_avator);
        mTvStatusHost = (DrawableTextView) findViewById(R.id.tv_status_host);
        mTvHostName = (TextView) findViewById(R.id.tv_host_name);
        mReclyChat = (RecyclerView) findViewById(R.id.recly_chat);
        mVpBottom = (FrameLayout) findViewById(R.id.vp_bottom);
        mVpGiftContainer = (FrameLayout) findViewById(R.id.vp_gift_container);
        mImgAvatorBg = (ImageView) findViewById(R.id.img_avator_bg);
        mVpSeatContainer = (FrameLayout) findViewById(R.id.vp_seat_container);
        mBtnCollect = (CheckImageView) findViewById(R.id.btn_collect);

        if (mIsHost) {
            mBtnCollect.setVisibility(View.INVISIBLE);
        } else {
            mBtnCollect.setOnClickListener(this);
        }

        mCharmName.setText(WordUtil.getString(R.string.charm_value));
        mImgHostAvator.setOnClickListener(this);
        mBtnCharm.setOnClickListener(this);
        mBtnNotice.setOnClickListener(this);
        mTvTitle.setOnClickListener(this);
        mImgRoomCover = (ImageView) findViewById(R.id.img_room_cover);
        if (CommonAppConfig.getInstance().getIsState()==1||CommonAppConfig.getInstance().getConfig().getLeaderBoardSwitch()==0){
            mBtnCharm.setVisibility(View.GONE);
        }

        setOnClickListener(R.id.btn_close, this);
        mLiveSeatViewHolder = initSeatViewHolder(mVpSeatContainer);
        mLiveSeatViewHolder.addToParent();
        mLiveSeatViewHolder.subscribeActivityLifeCycle();
        ValueFrameAnimator valueFrameAnimator = mLiveSeatViewHolder.getValueFrameAnimator();
        if (valueFrameAnimator != null) {
            valueFrameAnimator.addAnim(mImgAvatorBg);
        }
        mLiveChatAdapter = new LiveChatAdapter(null,mLiveBean.getUid());
        mLiveChatAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LiveChatBean liveBean = mLiveChatAdapter.getItem(position);
                itemClicker(liveBean);
            }
        });
        mReclyChat.setAdapter(mLiveChatAdapter);
        RxRefreshView.ReclyViewSetting.createLinearSetting(this)
                .settingRecyclerView(mReclyChat);
    }


    private void itemClicker(LiveChatBean item) {
        int type = item.getType();
        switch (type) {
            case LiveChatBean.NORMAL:
                openUserDialog(item.convert());
                break;
        }
    }

    private void openUserDialog(UserBean userBean) {
        LiveUserDialogFragment.showLiveUserFragment(this, userBean);
    }


    private void openLiveUserDialog() {
        if (mLiveBean != null) {
            UserBean userBean = mLiveBean.getLiveUserBean();
            LiveUserDialogFragment.showLiveUserFragment(this, userBean);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LiveActivityLifeModel<E> lifeHolder = LifeObjectHolder.getByContext(this, LiveActivityLifeModel.class);
        mSocketProxy = lifeHolder.getSocketProxy();
        mLiveBean = lifeHolder.getLiveBean();
    }


    protected void setData() {
        if (mLiveBean == null) {
            return;
        }
        ImgLoader.display(this, mLiveBean.getAvatar(), mImgHostAvator);
        mTvTitle.setText(mLiveBean.getTitle());
        mTvTitle.setSelected(true);
        ImgLoader.display(this, mLiveBean.getRoomCover(), mImgRoomCover);
        mTvHostName.setText(mLiveBean.getUserNiceName());
        mTvId.setText(StringUtil.contact("ID :", mLiveBean.getUid()));
//        setOnlineNum();
        mTvOnlineNum.setText(WordUtil.getString(R.string.online_num, Integer.toString(mLiveBean.getNums())));
        mCharm.setText(mLiveBean.getVotestotal());
        if (mLiveActivityLifeModel != null) {
            mLiveActivityLifeModel.setLiveCharm(mLiveBean.getVotestotal());
        }
        mBtnCollect.setChecked(mLiveBean.getIsCollect() == 1);
        mLiveSeatViewHolder.setData(mLiveActivityLifeModel.getSeatList());
        mBtnCollect.setChecked(mLiveBean.getIsattent() == 1);
    }

    @Override
    public Context getContext() {
        return this;
    }

    /*presnter层退出房间成功*/
    @Override
    public void exitSdkRoomSuccess() {
        L.e(TAG,"exitSdkRoomSuccess----->");
        if (mLivePresenter != null) {
            mLivePresenter.release();
        }
        if (mGiftAnimViewHolder != null) {
            mGiftAnimViewHolder.release();
        }
        if (mSocketProxy != null) {
            mSocketProxy.disConnect();
            mSocketProxy = null;
        }
    }

    @Override
    public void enterSdkRoomSuccess() {
        L.e(TAG,"enterSdkRoomSuccess----->");

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_notice) {
            openNotice(v);
        } else if (id == R.id.btn_close) {
            clickClose();
        } else if (id == R.id.tv_title) {
            openLiveRoomDialog();
        } else if (id == R.id.btn_charm) {
            openLiveBillboardDialog();
        } else if (id == R.id.img_host_avator) {
            openLiveUserDialog();
        } else if (id == R.id.btn_collect) {
            collectLiveRoom();
        }
    }

    private void collectLiveRoom() {
        if (mLiveBean == null) {
            return;
        }
        CommonHttpUtil.setAttention(mLiveBean.getUid(), new CommonCallback<Integer>() {
            @Override
            public void callback(Integer isAttention) {
                mLiveBean.setIsattent(isAttention);
                mBtnCollect.setChecked(isAttention == 1);
            }
        });
    }

    /*打开排行榜dialog*/
    private void openLiveBillboardDialog() {
        LiveBillboardDialogFragment liveBillboardDialogFragment = new LiveBillboardDialogFragment();
        liveBillboardDialogFragment.setLiveUid(mLiveBean.getUid());
        liveBillboardDialogFragment.show(getSupportFragmentManager());
    }


    /*打开房间dialog*/
    protected void openLiveRoomDialog() {
        LiveRoomDialogFragment dialogFragment = new LiveRoomDialogFragment();
        dialogFragment.setHost(mIsHost);
        dialogFragment.setLiveBean(mLiveBean);
        dialogFragment.show(getSupportFragmentManager());
    }

    @Override
    public void onBackPressed() {
        if (mShowingEndView) {
            CallConfig.setIsBusy(false);
            exitSdkRoomSuccess();
            finish();
            return;
        }
        clickClose();
    }

    /*打开公告栏*/
    protected void openNotice(final View v) {
        if (mLiveBean == null) {
            return;
        }
        ChatRoomHttpUtil.getLiveInfo(mLiveBean.getUid()).subscribe(new LockClickObserver<LiveBean>(v) {
            @Override
            public void onSucc(LiveBean liveBean) {
                mLiveBean.setDes(liveBean.getDes());
                showNoticeWindow(v);
            }
        });
    }

    /*显示公告提示窗口*/
    private void showNoticeWindow(View v) {
        int width = SystemUtil.getWindowsPixelWidth(this);
        CustomPopupWindow.Builder builder = new CustomPopupWindow.Builder(mContext);
        builder.setContentView(R.layout.item_pop_notice)
                .setFouse(true)
                .setOutSideCancel(true)
                .setwidth(width)
                .setheight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAnimationStyle(R.style.pop_animation);
        CustomPopupWindow customPopupWindow = new CustomPopupWindow(builder);
        customPopupWindow.showAsLaction(v, Gravity.BOTTOM, 0, 0);
        TextView tvContent = customPopupWindow.getItemView(R.id.tv_content);
        BubbleLayout bubbleLayout = customPopupWindow.getItemView(R.id.bubbleLayout);
        int offect = (int) ((float) (width - bubbleLayout.getPaddingLeft()) / 2F) - DpUtil.dp2px(83);
        bubbleLayout.setTriangleOffset(offect);
        tvContent.setText(mLiveBean.getDes());
    }

    /*发送本地系统信息*/
    protected void sendLocalTip(String tip) {
        ChatMannger chatMannger = mSocketProxy == null ? null : mSocketProxy.getChatMannger();
        if (chatMannger != null) {
            chatMannger.sendLocalSystemMessage(tip);
        }
    }

    protected void sendLocalTip(LiveChatBean liveChatBean) {
        ChatMannger chatMannger = mSocketProxy == null ? null : mSocketProxy.getChatMannger();
        if (chatMannger != null) {
            chatMannger.sendLocalMessage(liveChatBean);
        }
    }

    /*展示结束直播的界面*/
    private boolean mShowingEndView = false;//正在显示直播结束画面

    public void showLiveEndViewHolder(LiveEndResultBean liveEndResultBean, View.OnClickListener clickListener) {
//        onPause();
        LiveEndViewHolder liveEndViewHolder = new LiveEndViewHolder(this, mRootView);
        liveEndViewHolder.addToParent();
        liveEndViewHolder.pushData(liveEndResultBean);
        liveEndViewHolder.setOnEndButtonClickListener(clickListener);
        mShowingEndView = true;
    }

    /* *//*是否开启可发言状态*//*
    protected void receiverOpenSpeakState(UserBean userBean, boolean isOpen){

    }*/

    /*行为工厂*/
    protected abstract AbsBehaviorFactory onCreateBehaviorFactory();

    /*socket逻辑代理*/
    protected abstract E onCreateSocketProxy(String chatserver, T socketMessageBrider, LiveInfo parseLiveInfo);

    /*点击关闭按钮*/
    protected abstract void clickClose();

    /*设置进入界面的后的在sdk里面扮演的角色*/
    protected abstract int getRole();

    /*初始化座位*/
    protected abstract H initSeatViewHolder(FrameLayout vpSeatContainer);

    /*初始化需要传入socket里面的listner桥梁*/
    protected abstract T initSocketMessageBride();


    /**
     * 聊天室  收到聊天消息
     *
     * @param bean
     */

    @Override
    public void onChat(LiveChatBean bean) {
        if (mLiveChatAdapter != null) {
            try {
                mLiveChatAdapter.addData(bean);
                int size = mLiveChatAdapter.size();
                int lastPositon = size == 0 ? 0 : size - 1;
                mReclyChat.scrollToPosition(lastPositon);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onSendGift(ChatReceiveGiftBean bean) {
        if (mLiveActivityLifeModel != null) {
            mLiveActivityLifeModel.addLiveCharm(bean.getTouid(), bean.getTotal());
            String coin = FormatUtil.format(mLiveActivityLifeModel.getLiveCharm());
            mCharm.setText(coin);
        }
        if (mGiftAnimViewHolder == null) {
            mGiftAnimViewHolder = new GiftAnimViewHolder(this, mVpGiftContainer);
            mGiftAnimViewHolder.addToParent();
        }
//        if (isDialogFragmentVisible("LiveGiftDialogFragment")){
//            //如果 礼物列表窗口正在显示，普通礼物 动画 显示区域调整
//            mGiftAnimViewHolder.changeNormalGiftDisplayArea(true);
//        }else {
//            mGiftAnimViewHolder.changeNormalGiftDisplayArea(false);
//        }
        mGiftAnimViewHolder.showGiftAnim(bean);
    }


    /**
     * 聊天室  连接成功socket后调用
     *
     * @param successConn
     */
    @Override
    public void onConnect(boolean successConn) {

    }

    /**
     * 聊天室  自己的socket断开
     */
    @Override
    public void onDisConnect() {

    }

    /**
     * 收到结束直播的信息
     */
    @Override
    public void endLive() {
        L.e(TAG,"endLive------>");
        if (mLivePresenter != null) {
            mLivePresenter.onStopLive();
        }
        if (mGiftAnimViewHolder != null) {
            mGiftAnimViewHolder.release();
        }
        if (mSocketProxy != null) {
            mSocketProxy.disConnect();
            mSocketProxy = null;
        }
    }

    @Override
    public void enter(String uid, String uname, boolean isEnter) {
        if (mLiveActivityLifeModel != null) {
            mLiveActivityLifeModel.changeOnLineNum(uid, isEnter);
            if (isEnter) {
                //聊天公屏展示 用户进入聊天室 消息
                LiveChatBean bean = new LiveChatBean();
                bean.setContent(StringUtil.contact(uname,WordUtil.getString(R.string.enter_live_room)));
                bean.setType(LiveChatBean.ENTER_ROOM);
                onChat(bean);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (EventBus.getDefault().isRegistered(this)) {
             if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        }
        if (CacheBehaviorFactory.getCacheBehaviorFactory() != null){
            CacheBehaviorFactory.getCacheBehaviorFactory().clear();
        }
        ResourceUtil.clearDrawable(R.drawable.bg_color_global_radiu_2);
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
             if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        }
        super.onDestroy();
    }

    /**
     * 此fragment是否在显示
     *
     * @return
     */
//    public boolean isDialogFragmentVisible(String className){
//        FragmentManager fragmentManager = this.getSupportFragmentManager();
//        List<Fragment> fragments = fragmentManager.getFragments();
//        for(Fragment fragment : fragments){
//            if(fragment != null && fragment.isMenuVisible() && className.equals(fragment.getClass().getSimpleName())){
//                return true;
//            }
//        }
//        return false;
//    }
//
    private void setChatRoomOpened(LiveChatRoomDialogFragment chatRoomDialogFragment, boolean chatRoomOpened) {
        mChatRoomOpened = chatRoomOpened;
        mLiveChatRoomDialogFragment = chatRoomDialogFragment;
    }


    /**
     * 键盘高度的变化
     * KeyBoardHeightChangeListener
     */
    @Override
    public void onKeyBoardHeightChanged(int visibleHeight, int keyboardHeight) {
        if (mChatRoomOpened) {//判断私信聊天窗口是否打开
            if (mLiveChatRoomDialogFragment != null) {
                mLiveChatRoomDialogFragment.scrollToBottom();
            }
        } else {
//            if (mLiveRoomViewHolder != null) {
//                mLiveRoomViewHolder.onKeyBoardChanged(visibleHeight, keyboardHeight);
//            }
        }
    }

    @Override
    public boolean isSoftInputShowed() {
        if (mKeyBoardHeightUtil != null) {
            return mKeyBoardHeightUtil.isSoftInputShowed();
        }
        return false;
    }


    /**
     * 打开私信聊天窗口
     */
    protected KeyBoardHeightUtil2 mKeyBoardHeightUtil;

    public void openChatRoomWindow(UserBean userBean, boolean following) {
        if (mKeyBoardHeightUtil == null) {
            mKeyBoardHeightUtil = new KeyBoardHeightUtil2(mContext, super.findViewById(android.R.id.content), this);
            mKeyBoardHeightUtil.start();
        }
        LiveChatRoomDialogFragment fragment = new LiveChatRoomDialogFragment();
        fragment.setActionListener(new LiveChatRoomDialogFragment.ActionListener() {
            @Override
            public void onOpened(boolean isOpen, LiveChatRoomDialogFragment dialogFragment) {
                setChatRoomOpened(dialogFragment,isOpen);
            }
        });
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.USER_BEAN, userBean);
        bundle.putBoolean(Constants.FOLLOW, following);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveChatRoomDialogFragment");
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveBossPlaceOrderEvent(LiveChatRoomBossPlaceOrderEvent e) {
        L.e(TAG,"LiveChatRoomBossPlaceOrderEvent----");
        if (e != null && e.getToUserBean() != null){
            ChatMannger chatMannger = mSocketProxy == null ? null : mSocketProxy.getChatMannger();
            if (chatMannger != null) {
                chatMannger.sendPlaceOrderMessage(e.getToUserBean());
            }
        }
    }

}
