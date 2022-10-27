package com.yunbao.common.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.R;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ScreenDimenUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;

import java.lang.ref.WeakReference;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Sky.L on 2020-07-17
 */
public class VoipFloatService extends Service {

    private static final String TAG = "FloatService";

    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mLayoutParams;

    /**
     * float的布局view
     */

    private View mFloatView;

    //计时显示,暂无此功能
    private TextView mTvTimeCountdown;

    private ActionListener mActionListener;

    private int mFloatWinWidth, mFloatWinHeight;//悬浮窗的宽高

    private int mFloatWinMarginTop, mFloatWinMarginRight, mFloatWinMarginBottom;

    private int mLastX = 0, mLastY = 0;

    private int mStartX = 0, mStartY = 0;

    private long mTimeCountdown = 0;

    private boolean mFloatWindowShowing;

    //头像/昵称/id
    private ImageView iv_avatar;
    private TextView tv_nickname;
    private TextView tv_uid;
    //播流地址
    private String mPlayUrl;
    private LiveBean mLiveBean;
    private TXLivePlayer mLivePlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        L.e(TAG, "onCreate: ");
        createWindowManager();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (intent != null) {
            mLiveBean = intent.getParcelableExtra(Constants.LIVE_ROOM_BEAN);
            mPlayUrl = mLiveBean.getPull();
        }
        return new MyBinder();
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }


    //播放声音
    public void startPlay() {
        initVideoPlay();
        L.e("mPullUrl==" + mPlayUrl);
        if (!TextUtils.isEmpty(mPlayUrl) && mLivePlayer != null && !mLivePlayer.isPlaying()) {
            mLivePlayer.startPlay(mPlayUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV);
        }
    }

    public void stopPlay() {
        if (mLivePlayer != null && mLivePlayer.isPlaying()) {
            mLivePlayer.stopPlay(true);
        }
    }

    private void initVideoPlay() {
        if (mLivePlayer == null) {
            //创建 player 对象
            mLivePlayer = new TXLivePlayer(this);
            TXLivePlayConfig playConfig = new TXLivePlayConfig();
            playConfig.setAutoAdjustCacheTime(true);
            playConfig.setMinAutoAdjustCacheTime(1);
            playConfig.setMaxAutoAdjustCacheTime(1);
            mLivePlayer.setConfig(playConfig);
            mLivePlayer.setPlayListener(new ITXLivePlayListener() {
                @Override
                public void onPlayEvent(int i, Bundle bundle) {
                    L.e("error_code=" + i + "bundle==" + bundle.toString());
                    switch (i) {
                        case TXLiveConstants.PLAY_EVT_PLAY_BEGIN:
                            //视频播放开始
                            if (mActionListener != null) {
                                mActionListener.onPlayStart();
                            }
                            break;
                        case TXLiveConstants.PLAY_EVT_PLAY_LOADING:
                            //视频播放 loading
                            break;
                        case TXLiveConstants.PLAY_WARNING_RECONNECT:
                            //ToastUtil.show("聊天室小窗口正在启动网络重连~~");
                            //启动网络重连
                            L.e(TAG,"聊天室小窗口正在启动网络重连~~");
                            break;
                        case TXLiveConstants.PLAY_ERR_NET_DISCONNECT:
                            //经多次自动重连失败，放弃连接
                            L.e(TAG,"聊天室小窗口经多次自动重连失败，放弃连接");
                            if (mLiveBean != null && "1".equals(mLiveBean.getIsvideo())){
                                //如果当前聊天室播放的假视频流，则认为是假直播间，悬浮窗口不根据播流失败关闭
                                L.e(TAG,"当前聊天室播放的假视频流---");
                                stopPlay();
                                return;
                            }
                            if (mActionListener != null) {
                                mActionListener.onPlayStop();
                            }
                            ToastUtil.show(WordUtil.getString(R.string.live_room_over));
                            closeFloatWindow();
                            break;
                    }
                }

                @Override
                public void onNetStatus(Bundle bundle) {
                }
            });
        }
    }

    public void showFloatWindow() {
        if (!CommonAppConfig.getInstance().isFloatButtonShowing() && !mFloatWindowShowing) {
            if (canDrawOverlays(this, true)) {
                createFloatView();
                startPlay();
            }
        }
    }

    public void setLiveData(LiveBean liveData){
        mLiveBean = liveData;
        mPlayUrl = mLiveBean.getPull();
    }


    public class MyBinder extends Binder {
        public VoipFloatService getService() {
            return VoipFloatService.this;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        L.e(TAG, "onDestroy: ");
        removeFloatView();
        if (mTimeHandler != null) {
            mTimeHandler.release();
        }

    }


    private void createWindowManager() {

        L.e(TAG, "createWindowManager: ");

        // 取得系统窗体
        mWindowManager = (WindowManager) CommonAppContext.sInstance.getSystemService(CommonAppContext.WINDOW_SERVICE);
        //计算得出悬浮窗口的宽高
        DisplayMetrics metric = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metric);
        mFloatWinWidth = DpUtil.dp2px(150);
        mFloatWinHeight = DpUtil.dp2px(60);
        mFloatWinMarginTop = 0;
        mFloatWinMarginBottom = DpUtil.dp2px(150);
        mFloatWinMarginRight = DpUtil.dp2px(0);

        // 窗体的布局样式

        // 获取LayoutParams对象

        mLayoutParams = new WindowManager.LayoutParams();

        // 确定爱悬浮窗类型，表示在所有应用程序之上，但在状态栏之下

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//6.0+
//            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
//        }
//        else {
//            mLayoutParams.type =  WindowManager.LayoutParams.TYPE_TOAST;
//        }

        //   mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;//TYPE_PHONE

        mLayoutParams.format = PixelFormat.RGBA_8888;

        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        // 悬浮窗的对齐方式

        mLayoutParams.gravity = Gravity.RIGHT | Gravity.TOP;

        // 悬浮窗的位置

        mLayoutParams.x = mFloatWinMarginRight;

        //  mLayoutParams.y = mFloatWinMarginTop;
        mLayoutParams.y = ScreenDimenUtil.getInstance().getScreenHeight() - mFloatWinMarginBottom;

        mLayoutParams.width = mFloatWinWidth;

        mLayoutParams.height = mFloatWinHeight;

    }


    /**
     * 判断是否拥有悬浮窗权限
     *
     * @param isApplyAuthorization 是否申请权限
     */
    public static boolean canDrawOverlays(Context context, boolean isApplyAuthorization) {
        //Android 6.0 以下无需申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //判断是否拥有悬浮窗权限，无则跳转悬浮窗权限授权页面
            if (Settings.canDrawOverlays(context)) {
                return true;
            } else {
                ToastUtil.show("请开启悬浮窗权限后尝试");
                if (isApplyAuthorization) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                    if (context instanceof Service) {
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    }
                    context.startActivity(intent);
                    return false;
                } else {
                    return false;
                }
            }
        } else {
            return true;
        }
    }

    /**
     * 创建悬浮窗
     * 悬浮窗可上下滑动，不可左右滑动；
     * 若想左右滑动，把x坐标注释去掉
     */
    private void createFloatView() {
        L.e(TAG, "createFloatView----创建悬浮窗");
        LayoutInflater inflater = LayoutInflater.from(VoipFloatService.this);
        mFloatView = inflater.inflate(R.layout.voip_float_layout, null);
        //  mTvTimeCountdown = mFloatView.findViewById(R.id.tv_time_countdown);
        iv_avatar = mFloatView.findViewById(R.id.iv_avatar);
        tv_nickname = mFloatView.findViewById(R.id.tv_nickname);
        tv_uid = mFloatView.findViewById(R.id.tv_uid);
        tv_nickname.setText(mLiveBean.getUserNiceName());
        tv_uid.setText("ID:"+mLiveBean.getUid());
        mNextTimeMillis = SystemClock.uptimeMillis();
        ImgLoader.displayAvatar(this, mLiveBean.getAvatar(), iv_avatar);
//        if (mTimeHandler == null) {
//            mTimeHandler = new TimeHandler(this);
//        }
//        if (mTimeHandler != null) {
//            mTimeHandler.sendEmptyMessage(TimeHandler.WHAT_CHAT_TIME_CHANGED);
//        }
        mFloatWindowShowing = true;
        CommonAppConfig.getInstance().setFloatButtonShowing(true);
        mWindowManager.addView(mFloatView, mLayoutParams);
        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (MotionEvent.ACTION_DOWN == action) {
                    mStartX = mLastX = (int) event.getRawX();
                    mStartY = mLastY = (int) event.getRawY();
                } else if (MotionEvent.ACTION_UP == action) {
                    int dx = (int) event.getRawX() - mStartX;
                    int dy = (int) event.getRawY() - mStartY;
                    if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
                        return true;
                    }
                } else if (MotionEvent.ACTION_MOVE == action) {
                    int dx = (int) event.getRawX() - mLastX;
                    int dy = (int) event.getRawY() - mLastY;
                    //    mLayoutParams.x = mLayoutParams.x - dx;
                    mLayoutParams.y = mLayoutParams.y + dy;
                    mWindowManager.updateViewLayout(mFloatView, mLayoutParams);
                    //     mLastX = (int) event.getRawX();
                    mLastY = (int) event.getRawY();
                }
                return false;
            }
        });
        mFloatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.e(TAG, "点击了悬浮按钮");
                closeFloatWindow();
                if (mActionListener != null) {
                    mActionListener.onClickFloatButton();
                }
            }
        });

    }

    public void closeFloatWindow() {
        L.e(TAG,"closeFloatWindow------>");
        stopPlay();
        if (mFloatView != null) {
            removeFloatView();
        }
        //  VoipFloatService.this.stopSelf();
    }

    private void removeFloatView() {
        L.e(TAG, "removeFloatView: ");
        if (mFloatView != null && mWindowManager != null && mFloatWindowShowing) {
            mWindowManager.removeView(mFloatView);
            mFloatWindowShowing = false;
            CommonAppConfig.getInstance().setFloatButtonShowing(false);
        }
    }


    protected static class TimeHandler extends Handler {

        private VoipFloatService mVoipFloatService;
        private static final int WHAT_CHAT_TIME_CHANGED = 0;//通话时间读秒
        private static final int WHAT_PAUSE = 1;//切后台

        public TimeHandler(VoipFloatService service) {
            mVoipFloatService = new WeakReference<>(service).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mVoipFloatService != null) {
                switch (msg.what) {
                    case WHAT_CHAT_TIME_CHANGED:
                        mVoipFloatService.onChatTimeChanged();
                        break;
                }
            }
        }

        public void release() {
            removeCallbacksAndMessages(null);
            mVoipFloatService = null;
        }
    }

    /**
     * 通话计时
     */
    protected TimeHandler mTimeHandler;
    protected long mNextTimeMillis;//下次计时的时间点
    private void onChatTimeChanged() {
        if (mTvTimeCountdown != null) {
            mTvTimeCountdown.setText(StringUtil.getDurationText2(mTimeCountdown * 1000));
        }
        if (mTimeHandler != null) {
            mNextTimeMillis += 1000;
            mTimeCountdown += 1;
            mTimeHandler.sendEmptyMessageAtTime(TimeHandler.WHAT_CHAT_TIME_CHANGED, mNextTimeMillis);
        }
    }


    public interface ActionListener {
        void onClickFloatButton();

        void onPlayStart();

        void onPlayFailure();

        void onPlayStop();
    }


    public LiveBean getLiveBean() {
        return mLiveBean;
    }

    public void showFloatView(){
        startPlay();
        if (mFloatView != null) {
            mFloatView.setVisibility(View.VISIBLE);
        }
    }

    public void hideFloatView(){
        stopPlay();
        if (mFloatView != null) {
            mFloatView.setVisibility(View.INVISIBLE);
        }
    }
}