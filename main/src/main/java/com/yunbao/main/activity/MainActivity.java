package com.yunbao.main.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.adapter.ViewPagerAdapter;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.business.acmannger.ActivityMannger;
import com.yunbao.common.custom.TabButtonGroup;
import com.yunbao.common.event.ReduceEvent;
import com.yunbao.common.event.ShowLiveRoomFloatEvent;
import com.yunbao.common.event.ShowLiveRoomFloatWindowEvent;
import com.yunbao.common.event.ShowOrHideLiveRoomFloatWindowEvent;
import com.yunbao.common.event.TeenagerEvent;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.PermissionCallback;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.server.observer.DialogObserver;
import com.yunbao.common.service.VoipFloatService;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.LocationUtil;
import com.yunbao.common.utils.PermissionUtil;
import com.yunbao.common.utils.ProcessResultUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.VersionUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.im.event.ImUnReadCountEvent;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.live.activity.LiveAnchorActivity;
import com.yunbao.live.bean.LiveBeanReal;
import com.yunbao.live.bean.LiveConfigBean;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.BonusBean;
import com.yunbao.main.bean.commit.DressingCommitBean;
import com.yunbao.main.dialog.AgentDialogFragment;
import com.yunbao.main.dialog.YoungDialogFragment;
import com.yunbao.main.event.OpenDrawEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.presenter.CheckLivePresenter;
import com.yunbao.main.views.BonusViewHolder;
import com.yunbao.main.views.MainHomeParentDynamicViewHolder;
import com.yunbao.main.views.MainHomeViewHolder;
import com.yunbao.main.views.MainLiveViewHolder;
import com.yunbao.main.views.MainMeViewHolder2;
import com.yunbao.main.views.MainMessageViewHolder;
import com.yunbao.main.views.SelectConditionViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.net.shoot.sharetracesdk.AppData;
import cn.net.shoot.sharetracesdk.ShareTrace;
import cn.net.shoot.sharetracesdk.ShareTraceInstallListener;

@Route(path = RouteUtil.PATH_MAIN)
public class MainActivity extends AbsActivity {
    private static final String TAG = "MainActivity";
    private static final int PAGE_COUNT = 5;
    private TabButtonGroup mTabButtonGroup;
    private ViewPager mViewPager;
    private TextView mRedPoint;
    private List<FrameLayout> mViewList;
    private MainHomeViewHolder mHomeViewHolder;
    private MainHomeParentDynamicViewHolder mDynamicViewHolder;
    private MainMessageViewHolder mMessageViewHolder;
    private MainLiveViewHolder mMainLiveViewHolder;
    private MainMeViewHolder2 mMeViewHolder;
    private AbsMainViewHolder[] mViewHolders;
    private ProcessResultUtil mProcessResultUtil;
    private boolean mFristLoad;
    private long mLastClickBackTime;//上次点击back键的时间
    private ViewGroup mDrawlayoutContainer;

    private SelectConditionViewHolder selectConditionViewHolder;
    private DrawerLayout mDrawerLayout;
    private CheckLivePresenter mCheckLivePresenter;
    private ViewGroup mRootContainer;
    private Runnable runnable;
    private Handler handlerCount = new Handler();
    private long duration=10;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void main() {
        ActivityMannger.getInstance().setBaseActivity(this);
        mTabButtonGroup = findViewById(R.id.tab_group);
        mDrawlayoutContainer = findViewById(R.id.drawlayout_container);
        mDrawerLayout = findViewById(R.id.drawlayout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mViewPager = findViewById(R.id.viewPager);
        if (PAGE_COUNT > 1) {
            mViewPager.setOffscreenPageLimit(PAGE_COUNT - 1);
        }
        mViewList = new ArrayList<>();
        for (int i = 0; i < PAGE_COUNT; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewPager.setAdapter(new ViewPagerAdapter(mViewList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                loadPageData(position, true);
                if (mViewHolders != null) {
                    for (int i = 0, length = mViewHolders.length; i < length; i++) {
                        AbsMainViewHolder vh = mViewHolders[i];
                        if (vh != null) {
                            vh.setShowed(position == i);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mTabButtonGroup.setViewPager(mViewPager);
        mViewHolders = new AbsMainViewHolder[PAGE_COUNT];
        mRedPoint = findViewById(R.id.red_point);
        mProcessResultUtil = new ProcessResultUtil(this);
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
        checkAgent();
        CommonAppConfig.getInstance().setLaunched(true);
        CommonAppConfig.getInstance().setLaunchTime(System.currentTimeMillis() / 1000);
        mFristLoad = true;
        openSelectSelectConditionViewHolder();
        checkYoung();//青少年模式
        requestBonus();
        runnable=new Runnable() {
            @Override
            public void run() {
                updateCountDown();
            }
        };
    }

    public void openSelectSelectConditionViewHolder() {
        if (selectConditionViewHolder == null) {
            selectConditionViewHolder = new SelectConditionViewHolder(this, mDrawlayoutContainer, mDrawerLayout);
            selectConditionViewHolder.addToParent();
            selectConditionViewHolder.subscribeActivityLifeCycle();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        L.e("onNewIntent==");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 青少年模式
     */
    public void checkYoung(){
        MainHttpUtil.checkYoung(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    CommonAppConfig.getInstance().setIsPwd(obj.getIntValue("is_setpassword"));
                    int isstate=obj.getIntValue("status");
                    CommonAppConfig.getInstance().setIsState(isstate);
                    if (isstate == 1) { //已设置青少年模式
                        if (handlerCount!=null&&runnable!=null) {
                            handlerCount.postDelayed(runnable, duration*1000);
                        }

                    } else {
                        if (CommonAppConfig.getInstance().getIsLogin()==1) {
                            String prompt = CommonAppConfig.getInstance().getConfig().getTeenager_des();
                            YoungDialogFragment fragment = new YoungDialogFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.PROMPT, prompt);
                            fragment.setArguments(bundle);
                            fragment.show(((MainActivity) mContext).getSupportFragmentManager(), "MoreDialogFragment");
                        }
                    }
                }
            }
        });


    }

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        if (!CommonAppConfig.getInstance().isTeenagerChange()) {
            CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
                @Override
                public void callback(ConfigBean configBean) {
                    if (configBean != null) {
                        if (configBean.getMaintainSwitch() == 1) {//开启维护
                            DialogUitl.showSimpleTipDialog(mContext, WordUtil.getString(R.string.main_maintain_notice), configBean.getMaintainTips());
                        }
                        if (!VersionUtil.isLatest(configBean.getVersion())) {
                            VersionUtil.showDialog(mContext, configBean.getUpdateDes(), configBean.getDownloadApkUrl());
                        }
                    }
                }
            });
        }
    }

    /**
     * 检查是否要弹邀请码的弹窗
     */
    private void checkAgent() {
        MainHttpUtil.checkAgent(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    //sharetrace_switch：三方sharetrace下载应用开关
                    boolean shareTraceSwitch = obj.getIntValue("sharetrace_switch") == 1;
                    boolean agentSwitch = obj.getIntValue("agent_switch") == 1;
                    final int agentStatus = obj.getIntValue("agent_status"); // 0 不弹窗 1弹一次 2一直弹
                    if (agentStatus > 0) {
                        if (agentSwitch) {
                            if (shareTraceSwitch) {
                                //shareTrace
                                ShareTrace.getInstallTrace(new ShareTraceInstallListener() {
                                    @Override
                                    public void onInstall(AppData data) {
                                        L.e(TAG, "appData=" + data.toString());
                                        String addData = data.getParamsData();
                                        if (TextUtils.isEmpty(addData) || addData.length() <= 5) {
                                            return;
                                        }
                                        String code = addData.substring(5);
                                        MainHttpUtil.setAgent(code, new HttpCallback() {
                                            @Override
                                            public void onSuccess(int code, String msg, String[] info) {
                                                if (code == 0) {

                                                }
                                            }
                                        });
                                        checkVersion();
                                    }

                                    @Override
                                    public void onError(int code, String msg) {
                                        if (agentStatus == 1) {
                                            showInviteDialogFragment(true);
                                        } else if (agentStatus == 2) {
                                            showInviteDialogFragment(false);
                                        }
                                        L.e(TAG, "Get install trace info error. code=" + code + ",msg=" + msg);
                                    }
                                });
                            } else {
                                //弹窗填写
                                if (agentStatus == 1) {
                                    showInviteDialogFragment(true);
                                } else if (agentStatus == 2) {
                                    showInviteDialogFragment(false);
                                }

                            }
                        }else{
                            checkVersion();
                        }
                    }else{
                        checkVersion();
                    }
                }else{
                    checkVersion();
                }


            }


            @Override
            public void onError() {
                checkVersion();
            }
        });
    }

    private void showInviteDialogFragment(boolean canCancel) {
        AgentDialogFragment fragment = new AgentDialogFragment();
        fragment.setCancelable(canCancel);
        fragment.show(getSupportFragmentManager(), "AgentDialogFragment");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkVersion();
            }
        },500);
    }


    @Override
    protected void onResume() {
        super.onResume();
        L.e(TAG, "----onResume----");
        if (mFristLoad) {
            mFristLoad = false;
            checkPermissions();
            loginIM();
            loadPageData(0, true);
            if (mHomeViewHolder != null) {
                mHomeViewHolder.setShowed(true);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        L.e(TAG, "----onPause----");
    }

    /**
     * 检查权限
     */

    private void checkPermissions() {
        mProcessResultUtil.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        }, new CommonCallback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result) {
                    getLocation();
                }
            }
        });
    }

    /**
     * 获取所在位置, 启动定位
     */
    private void getLocation() {
        LocationUtil.getInstance().startLocation();
    }

    /**
     * 登录IM
     */

    private void loginIM() {
        String uid = CommonAppConfig.getInstance().getUid();
        ImMessageUtil.getInstance().loginImClient(uid);
    }

    @Override
    protected void onDestroy() {
        L.e(TAG, "----onDestroy----");
        if (mVoipFloatService != null) {
            mVoipFloatService.unbindService(mVideoServiceConnection);
            mVoipFloatService.onDestroy();
        }
        ActivityMannger.getInstance().releaseBaseActivity(this);
        if (mTabButtonGroup != null) {
            mTabButtonGroup.cancelAnim();
        }
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        MainHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        MainHttpUtil.cancel(MainHttpConsts.CHECK_AGENT);
        CommonHttpUtil.cancel(CommonHttpConsts.SET_LOCAITON);
        LocationUtil.getInstance().stopLocation();
        if (mProcessResultUtil != null) {
            mProcessResultUtil.release();
        }
        if (mCheckLivePresenter != null) {
            mCheckLivePresenter.cancel();
        }
        CommonAppConfig.getInstance().setLaunched(false);
        super.onDestroy();
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(ImUnReadCountEvent e) {
        L.e(TAG, "onImUnReadCountEvent未读消息总数--->" + e.getUnReadCount());
        String unReadCount = e.getUnReadCount();
        if (e.isLiveChatRoom()) {
            return;
        }
        if (!TextUtils.isEmpty(unReadCount)) {
            setUnReadCount(unReadCount);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenDrawLayout(OpenDrawEvent drawEvent) {
        DressingCommitBean dressingCommitBean = drawEvent.getDressingCommitBean();
        selectConditionViewHolder.setCondition(dressingCommitBean);
        mDrawerLayout.openDrawer(Gravity.RIGHT);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTeenagerEvent(TeenagerEvent e) {
        CommonAppConfig.getInstance().setIsLogin(0);
        CommonAppConfig.getInstance().setTeenagerChange(true);
        finish();
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
    }

    /**
     * 显示未读消息
     */
    private void setUnReadCount(String unReadCount) {
        if (mRedPoint != null) {
            if ("0".equals(unReadCount)) {
                if (mRedPoint.getVisibility() == View.VISIBLE) {
                    mRedPoint.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mRedPoint.getVisibility() != View.VISIBLE) {
                    mRedPoint.setVisibility(View.VISIBLE);
                }
            }
            mRedPoint.setText(unReadCount);
        }
    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        if (curTime - mLastClickBackTime > 2000) {
            mLastClickBackTime = curTime;
            ToastUtil.show(R.string.main_click_next_exit);
            return;
        }
        super.onBackPressed();
    }

    private void loadPageData(int position, boolean needlLoadData) {
        if (mViewHolders == null) {
            return;
        }

        AbsMainViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mHomeViewHolder = new MainHomeViewHolder(mContext, parent);
                    vh = mHomeViewHolder;
                } else if (position == 1) {
                    mDynamicViewHolder = new MainHomeParentDynamicViewHolder(mContext, parent);
                    vh = mDynamicViewHolder;
                } else if (position == 2) {
                    mMainLiveViewHolder = new MainLiveViewHolder(mContext, parent);
                    vh = mMainLiveViewHolder;
                } else if (position == 3) {
                    mMessageViewHolder = new MainMessageViewHolder(mContext, parent);
                    vh = mMessageViewHolder;
                } else if (position == 4) {
                    mMeViewHolder = new MainMeViewHolder2(mContext, parent);
                    vh = mMeViewHolder;
                }
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.subscribeActivityLifeCycle();
            }
        }
        if (needlLoadData && vh != null) {
            vh.loadData();
        }
    }


    /**
     * 进入个人主页
     */
    public void forwardUserHome(String toUid) {
        RouteUtil.forwardUserHome(toUid);
    }


    /*****************聊天室 悬浮小窗口开始**********************/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowLiveRoomFloatEvent(ShowLiveRoomFloatEvent e) {
        if (e != null) {
            initLiveRoomFloatService(e.getLiveBean());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowLiveRoomFloatWindowEvent(ShowLiveRoomFloatWindowEvent e) {
        showFloatWindow();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowOrHideLiveRoomFloatWindowEvent(ShowOrHideLiveRoomFloatWindowEvent e) {
        if (e.getShowStatus() == 1) {
            if (CommonAppConfig.getInstance().isFloatButtonShowing()) {
                if (mVoipFloatService != null) {
                    mVoipFloatService.showFloatView();
                }
            }
        } else if (e.getShowStatus() == 2) {
            if (CommonAppConfig.getInstance().isFloatButtonShowing()) {
                if (mVoipFloatService != null) {
                    mVoipFloatService.hideFloatView();
                }
            }
        } else if (e.getShowStatus() == 0) {
            if (CommonAppConfig.getInstance().isFloatButtonShowing()) {
                closeFloatWindow();
            }
        }
    }

    //初始化悬浮框服务
    public void initLiveRoomFloatService(LiveBean liveBean) {
        if (TextUtils.isEmpty(liveBean.getPull())) {
            return;
        }
        mLiveBean = liveBean;
        //开启服务显示悬浮框
        Intent intent = new Intent(getApplicationContext(), VoipFloatService.class);
        intent.putExtra(Constants.LIVE_ROOM_BEAN, liveBean);
        bindService(intent, mVideoServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private LiveBean mLiveBean;//聊天室数据信息
    protected VoipFloatService mVoipFloatService;

    public void showFloatWindow() {
        if (CommonAppConfig.canDrawOverlays(this, true)) {
            if (mVoipFloatService != null) {
                mVoipFloatService.setLiveData(mLiveBean);
                mVoipFloatService.showFloatWindow();
            }
        }
    }


    public void closeFloatWindow() {
        if (mVoipFloatService != null) {
            mVoipFloatService.closeFloatWindow();
        }
    }

    ServiceConnection mVideoServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            L.e(TAG, "onServiceConnected----" + service.toString());
//            // 获取服务的操作对象
            VoipFloatService.MyBinder binder = (VoipFloatService.MyBinder) service;
            mVoipFloatService = binder.getService();
            mVoipFloatService.setActionListener(new VoipFloatService.ActionListener() {
                @Override
                public void onClickFloatButton() {
                    L.e(TAG, "--onClickFloatButton--->" + !ActivityMannger.getInstance().isBackGround());
                    /*从前台点击*/
                    if (!ActivityMannger.getInstance().isBackGround()) {
                        final LiveBean liveBean = ((VoipFloatService.MyBinder) service).getService().getLiveBean();
                        if (liveBean != null) {
                            ChatRoomHttpUtil.getUserNums(liveBean.getUid(), liveBean.getStream()).subscribe(new DefaultObserver<Integer>() {
                                @Override
                                public void onNext(Integer integer) {
                                    //先设置聊天室房间内 在线用户人数
                                    if (liveBean != null) {
                                        liveBean.setNums(integer);
                                    }
                                    enterRoom(liveBean);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                    L.e(e.getMessage());
//                                    ToastUtil.show(e.getMessage());
                                }
                            });

                        }
                    }
                }

                @Override
                public void onPlayStart() {

                }

                @Override
                public void onPlayFailure() {

                }

                @Override
                public void onPlayStop() {

                }
            });
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            L.e(TAG, "onServiceDisconnected------" + name.toString());
        }
    };

    private void enterRoom(final LiveBean liveBean) {
        CommonHttpUtil.enterRoom(liveBean.getUid(), liveBean.getStream()).subscribe(new DialogObserver<LiveBean>(this) {
            @Override
            public void onNext(LiveBean tempLiveBean) {
                liveBean.setVotestotal(tempLiveBean.getVotestotal());
                liveBean.setChatserver(tempLiveBean.getChatserver());
                liveBean.setIsattent(tempLiveBean.getIsattent());
                liveBean.setSits(tempLiveBean.getSits());
                liveBean.setSkillid(tempLiveBean.getSkillid());
                liveBean.setIsdispatch(tempLiveBean.getIsdispatch());
                liveBean.setInviteCode(tempLiveBean.getInviteCode());
                liveBean.setExpandParm(tempLiveBean.getExpandParm());
                liveBean.setRoomCover(tempLiveBean.getRoomCover());
                RouteUtil.forwardLiveAudience(liveBean, liveBean.getType(), false);
            }
        });
    }

    /*****************聊天室 小窗口结束**********************/


    /**
     * 签到奖励
     */
    private void requestBonus() {
        MainHttpUtil.checkYoung(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (obj.getIntValue("status") == 0&&!CommonAppConfig.getInstance().isTeenagerChange()) {
                        MainHttpUtil.requestBonus(new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0 && info.length > 0) {
                                    JSONObject obj = JSON.parseObject(info[0]);
                                    if (obj.getIntValue("bonus_switch") == 0 || obj.getIntValue("bonus_isday") == 1) {
                                        return;
                                    }
                                    List<BonusBean> list = JSON.parseArray(obj.getString("bonus_list"), BonusBean.class);
                                    BonusViewHolder bonusViewHolder = new BonusViewHolder(mContext, getRootContainer());
                                    bonusViewHolder.setData(list, obj.getIntValue("bonus_day"), obj.getString("count_day"), obj.getIntValue("bonus_isday") == 1);
                                    bonusViewHolder.show();
                                }
                            }
                        });
                    }

               }
            }
        });

    }


    /**
     * 观看直播
     */
    public void watchLive(LiveBeanReal liveBean, String key, int position) {

        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            mCheckLivePresenter.watchLive(liveBean, key, position);
        } else {
            mCheckLivePresenter.watchLive(liveBean);
        }
    }

    /**
     * 开启直播
     */
    public void startLive() {
        if(!canClick()){
            return;
        }
        PermissionUtil.request(MainActivity.this,
                new PermissionCallback() {
                    @Override
                    public void onAllGranted() {
                        LiveHttpUtil.getLiveSdk(new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0 && info.length > 0) {
                                    JSONObject obj = JSON.parseObject(info[0]);
                                    int islive= obj.getIntValue("islive");
                                    if(islive==1){
                                        LiveConfigBean configBean = JSON.parseObject(obj.getString("android_tx"), LiveConfigBean.class);
                                        LiveAnchorActivity.forward(mContext, Constants.LIVE_SDK_TX, configBean, 0, false,obj.getString("agentcode"));
                                    }else{
                                        new DialogUitl.Builder(mContext)
                                                .setContent(WordUtil.getString(R.string.live_start_auth))
                                                .setBackgroundDimEnabled(true)
                                                .setCancelable(true)
                                                .setConfrimString(WordUtil.getString(R.string.to_auth))
                                                .setClickCallback(new DialogUitl.SimpleCallback() {
                                                    @Override
                                                    public void onConfirmClick(Dialog dialog, String content) {
                                                        WebViewActivity.forward(mContext, CommonAppConfig.HOST+"/appapi/auth/index");
                                                    }
                                                })
                                                .build()
                                                .show();
                                    }

                                }
                            }

                            @Override
                            public boolean showLoadingDialog() {
                                return true;
                            }

                            @Override
                            public Dialog createLoadingDialog() {
                                return DialogUitl.loadingDialog(mContext);
                            }
                        });
                    }
                },
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        );

    }


    public ViewGroup getRootContainer() {
        if (mRootContainer == null) {
            mRootContainer = (ViewGroup) findViewById(android.R.id.content);
        }
        return mRootContainer;
    }


    /**
     * 上报青少年使用时长
     */
    private void updateCountDown() {
        reduceTeenagers();
        if (handlerCount != null) {
            handlerCount.postDelayed(runnable,duration*1000);
        }
    }

    private void reduceTeenagers(){
        MainHttpUtil.reduceTeenagers(new HttpCallback() {
            @Override
            public void onSuccess(int code, final String msg, String[] info) {
                if (code==10010||code==10011){
                    if (handlerCount!=null&&runnable!=null){
                        handlerCount.removeCallbacks(runnable);
                    }
                    if (!isFinishing()) {
                        EventBus.getDefault().post(new ReduceEvent(msg));
                    }
                }
            }
        });
    }


}
