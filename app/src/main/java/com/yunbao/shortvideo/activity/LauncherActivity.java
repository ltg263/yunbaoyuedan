package com.yunbao.shortvideo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.video.ui.view.AbsPlayViewHolder;
import com.example.video.ui.view.IjkViewPlayer;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.bean.AdBean;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.CircleProgress;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.chatroom.ui.view.LauncherAdViewHolder;
import com.yunbao.main.activity.LoginActivity;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.shortvideo.AppContext;
import com.yunbao.shortvideo.R;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/17.
 */

@Route(path = RouteUtil.PATH_LAUNCHER)
public class LauncherActivity extends AppCompatActivity implements View.OnClickListener {

    private Handler mHandler;
    protected Context mContext;

    private ViewGroup mRoot;
    private ImageView mCover;
    private ViewGroup mContainer;
    private static final String TAG = "LauncherActivity";
    private static final int WHAT_GET_CONFIG = 0;
    private static final int WHAT_COUNT_DOWN = 1;
    private CircleProgress mCircleProgress;
    private List<AdBean> mAdList;
    private List<ImageView> mImageViewList;
    private int mMaxProgressVal;
    private int mCurProgressVal;
    private int mAdIndex;
    private int mInterval = 2000;
    private View mBtnSkipImage;
    private View mBtnSkipVideo;
    private LauncherAdViewHolder mLauncherAdViewHolder;
    private boolean mForward;
    private boolean mWaitEnd;
    protected AbsPlayViewHolder playViewHolder;

    private List<View> mBtnToAdList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //下面的代码是为了防止一个bug:
        // 收到极光通知后，点击通知，如果没有启动app,则启动app。然后切后台，再次点击桌面图标，app会重新启动，而不是回到前台。
        Intent intent = getIntent();
        if (!isTaskRoot()
                && intent != null
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && intent.getAction() != null
                && intent.getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }
        //android O  fix bug orientation
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setStatusBar();
        setContentView(R.layout.activity_launcher);
        mContext = this;
        mRoot = findViewById(R.id.root);
        mCover = findViewById(R.id.img);
        //ImgLoader.display(mContext, R.mipmap.screen, mCover);
        mCircleProgress = findViewById(R.id.progress);
        mContainer = findViewById(R.id.container);
        mCircleProgress = findViewById(R.id.progress);
        mBtnSkipImage = findViewById(R.id.btn_skip_img);
        mBtnSkipVideo = findViewById(R.id.btn_skip_video);
        mBtnSkipImage.setOnClickListener(this);
        mBtnSkipVideo.setOnClickListener(this);
        playViewHolder=new IjkViewPlayer(this,mContainer);
        playViewHolder.addToParent();
        playViewHolder.setVideoListner(new AbsPlayViewHolder.VideoListner() {
            @Override
            public void state(int state, Bundle bundle) {
                switch (state){
                    case PREPARED:
                        L.e(TAG, "视频准备完成------>");
                        if (mBtnSkipVideo != null && mBtnSkipVideo.getVisibility() != View.VISIBLE) {
                            mBtnSkipVideo.setVisibility(View.VISIBLE);
                        }
                        if (mCover != null && mCover.getVisibility() == View.VISIBLE) {
                            mCover.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case END:
                        L.e(TAG, "视频播放结束------>");
                        checkUidAndToken();
                        break;
                        default:
                            break;
                }
            }
        });
        playViewHolder.subscribeActivityLifeCycle();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_GET_CONFIG:
                        mWaitEnd = true;
                        getConfig();
                        break;
                    case WHAT_COUNT_DOWN:
                        updateCountDown();
                        break;
                }
            }
        };
        mHandler.sendEmptyMessageDelayed(WHAT_GET_CONFIG, 1000);
    }


    /**
     * 图片倒计时
     */
    private void updateCountDown() {
        mCurProgressVal += 100;
        if (mCurProgressVal > mMaxProgressVal) {
            return;
        }
        if (mCircleProgress != null) {
            mCircleProgress.setCurProgress(mCurProgressVal);
        }
        int index = mCurProgressVal / mInterval;
        if (index < mAdList.size() && mAdIndex != index) {
            View v = mImageViewList.get(mAdIndex);
            View btnToAd = mBtnToAdList.get(mAdIndex);
            if (v != null && v.getVisibility() == View.VISIBLE) {
                v.setVisibility(View.INVISIBLE);
            }
            if (btnToAd != null && btnToAd.getVisibility() == View.VISIBLE) {
                btnToAd.setVisibility(View.INVISIBLE);
            }
            mAdIndex = mCurProgressVal / mInterval;
        }
        if (mCurProgressVal < mMaxProgressVal) {
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_COUNT_DOWN, 100);
            }
        } else if (mCurProgressVal == mMaxProgressVal) {
            checkUidAndToken();
        }
    }

    /**
     * 获取Config信息
     */
    private void getConfig() {
        CommonHttpUtil.getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                if (bean != null) {
                    ((AppContext) CommonAppContext.sInstance).initBeautySdk(bean.getBeautyAppId(),bean.getBeautyKey());
                    String adInfo = bean.getAdInfo();
                    if (!TextUtils.isEmpty(adInfo)) {
                        JSONObject obj = JSON.parseObject(adInfo);
                        if (obj.getIntValue("switch") == 1) {
                            List<AdBean> list = JSON.parseArray(obj.getString("list"), AdBean.class);
                            if (list != null && list.size() > 0) {
                                mAdList = list;
                                mInterval = obj.getIntValue("time") * 1000;
                                if (mContainer != null) {
                                    mContainer.setOnClickListener(LauncherActivity.this);
                                }
                                playAD(obj.getIntValue("type") == 0);
                            } else {
                                checkUidAndToken();
                            }
                        } else {
                            checkUidAndToken();
                        }
                    } else {
                        checkUidAndToken();
                    }
                }
            }
        });
    }

    /**
     * 检查uid和token是否存在
     */

    private void checkUidAndToken() {
        if (mForward) {
            return;
        }
        mForward = true;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        String[] uidAndToken = SpUtil.getInstance().getMultiStringValue(
                new String[]{SpUtil.UID, SpUtil.TOKEN});
        final String uid = uidAndToken[0];
        final String token = uidAndToken[1];
        if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
            MainHttpUtil.getBaseInfo(uid, token, new CommonCallback<UserBean>() {
                @Override
                public void callback(UserBean bean) {
                    if (bean != null) {
                        CommonAppConfig.getInstance().setLoginInfo(uid, token, false);
                        forwardMainActivity();
                    }
                }
            });
        } else {
            releaseVideo();
            LoginActivity.forward();
        }
    }
    /**
     * 跳转到首页
     */

    private void forwardMainActivity() {
        CommonAppConfig.getInstance().setIsLogin(0);
        MainActivity.forward(mContext);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        releaseVideo();
        if (mLauncherAdViewHolder != null) {
            mLauncherAdViewHolder.release();
        }
        mLauncherAdViewHolder = null;
        super.onDestroy();
    }

    /**
     * 设置透明状态栏
     */
    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_skip_img || i == R.id.btn_skip_video) {
            if (mBtnSkipImage != null) {
                mBtnSkipImage.setClickable(false);
            }
            if (mBtnSkipVideo != null) {
                mBtnSkipVideo.setClickable(false);
            }
            checkUidAndToken();
        } else if (i == R.id.container) {
            clickAD();
        }
    }


    /**
     * 点击广告
     */
    private void clickAD() {
        if (mAdList != null && mAdList.size() > mAdIndex) {
            AdBean adBean = mAdList.get(mAdIndex);
            if (adBean != null) {
                String link = adBean.getLink();
                if (!TextUtils.isEmpty(link)) {
                    if (mBtnSkipImage != null && mBtnSkipImage.getVisibility() == View.VISIBLE){
                        mBtnSkipImage.setVisibility(View.INVISIBLE);
                    }
                    if (mBtnSkipVideo != null && mBtnSkipVideo.getVisibility() == View.VISIBLE){
                        mBtnSkipVideo.setVisibility(View.INVISIBLE);
                    }
                    if (mHandler != null) {
                        mHandler.removeCallbacksAndMessages(null);
                    }
                    if (mContainer != null) {
                        mContainer.setClickable(false);
                    }
                    releaseVideo();
                    if (mLauncherAdViewHolder == null) {
                        mLauncherAdViewHolder = new LauncherAdViewHolder(mContext, mContainer, link);
                        mLauncherAdViewHolder.addToParent();
                        mLauncherAdViewHolder.loadData();
                        mLauncherAdViewHolder.show();
                        mLauncherAdViewHolder.setActionListener(new LauncherAdViewHolder.ActionListener() {
                            @Override
                            public void onHideClick() {
                                checkUidAndToken();
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mLauncherAdViewHolder.isShowed()){
            mLauncherAdViewHolder.hide();
            return;
        }
        super.onBackPressed();
    }

    private void releaseVideo() {
        if (playViewHolder != null){
            playViewHolder.stop();
            playViewHolder.release();
            playViewHolder.removeFromParent();
            playViewHolder = null;
        }
    }


    /**
     * 播放广告
     */
    private void playAD(boolean isImage) {
        if (mContainer == null) {
            return;
        }
        if (isImage) {
            int imgSize = mAdList.size();
            if (imgSize > 0) {
                mImageViewList = new ArrayList<>();
                mBtnToAdList = new ArrayList<>();
                for (int i = 0; i < imgSize; i++) {
                    //跳转广告详情页面的按钮
                    View btnToAd = LayoutInflater.from(mContext).inflate(R.layout.view_launcher_btn_to_ad, null);
                    final ImageView imageView = new ImageView(mContext);
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setBackgroundColor(0xffffffff);
                    mImageViewList.add(imageView);
                    mBtnToAdList.add(btnToAd);
                  //  ImgLoader.display(mContext, mAdList.get(i).getUrl(), imageView);
                    ImgLoader.displayDrawable(mContext, mAdList.get(i).getUrl(), new ImgLoader.DrawableCallback() {
                        @Override
                        public void onLoadSuccess(Drawable drawable) {
                            imageView.setImageDrawable(drawable);
                            if (mCover != null && mCover.getVisibility() == View.VISIBLE) {
                                mCover.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onLoadFailed() {
                            if (mCover != null && mCover.getVisibility() != View.VISIBLE) {
                                mCover.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    if (TextUtils.isEmpty(mAdList.get(i).getLink())) {
                        btnToAd.setVisibility(View.INVISIBLE);
                    }
                }
                for (int i = imgSize - 1; i >= 0; i--) {
                    mContainer.addView(mImageViewList.get(i));
                    mContainer.addView(mBtnToAdList.get(i));
                }
                if (mBtnSkipImage != null && mBtnSkipImage.getVisibility() != View.VISIBLE) {
                    mBtnSkipImage.setVisibility(View.VISIBLE);
                }
                mMaxProgressVal = imgSize * mInterval;
                if (mCircleProgress != null) {
                    mCircleProgress.setMaxProgress(mMaxProgressVal);
                }
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(WHAT_COUNT_DOWN, 100);
                }

            } else {
                checkUidAndToken();
            }
        } else {
            if (mAdList == null || mAdList.size() == 0) {
                checkUidAndToken();
                return;
            }
            String videoUrl = mAdList.get(0).getUrl();
            if (TextUtils.isEmpty(videoUrl)) {
                checkUidAndToken();
                return;
            }
            String videoFileName = MD5Util.getMD5(videoUrl);
            if (TextUtils.isEmpty(videoFileName)) {
                checkUidAndToken();
                return;
            }
            if(playViewHolder!=null){
                playViewHolder.setLoop(false);
                playViewHolder.setFullScreen(true);
                playViewHolder.play(videoUrl,"");
            }
            //跳转广告详情页面的按钮
            View btnToAd = LayoutInflater.from(mContext).inflate(R.layout.view_launcher_btn_to_ad,null);
            if (!TextUtils.isEmpty(mAdList.get(0).getLink())){
                mContainer.addView(btnToAd);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
