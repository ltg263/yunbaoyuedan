package com.yunbao.main.activity;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.ViewPagerAdapter;
import com.yunbao.common.bean.ChatReceiveGiftBean;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.DrawableTextView;
import com.yunbao.common.custom.MyViewPager;
import com.yunbao.common.dialog.BottomDealFragment;
import com.yunbao.common.event.BlackEvent;
import com.yunbao.common.event.FollowEvent;
import com.yunbao.common.event.UpdateFieldEvent;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.presenter.GiftAnimViewHolder;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DownloadUtil;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ScreenDimenUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.bean.MyDynamicBean;
import com.yunbao.dynamic.http.DynamicHttpUtil;
import com.yunbao.dynamic.ui.view.MysDynamicVIewHolder;
import com.yunbao.im.activity.ChatRoomActivity;
import com.yunbao.common.utils.VoiceMediaPlayerUtil;
import com.yunbao.im.bean.ImMessageBean;
import com.yunbao.im.dialog.ChatGiftDialogFragment;
import com.yunbao.main.R;
import com.yunbao.main.bean.PhotoBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.main.views.MyPhotoViewHolder;
import com.yunbao.main.views.UserHomeProfileViewHolder;
import com.yunbao.main.views.UserHomeSkillViewHolder;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by cxf on 2019/7/22.
 */
@Route(path = RouteUtil.PATH_USER_HOME)
public class UserHomeActivity extends AbsActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private  int PAGE_COUNT;
    private AppBarLayout mAppBarLayout;
    private ImageView mAvatar1;
    private ImageView mAvatar2;
    private TextView mName1;
    private TextView mName2;
    private TextView mCity1;
    private TextView mCity2;
    private TextView mSign;
    private TextView mFans;
    private View mBtnVoice;
    private ImageView mVoiceImage;
    private TextView mVoiceTime;
    private DrawableTextView mBtnFollow;
    private TextView mBtnFollow2;
    private String mFollowString;
    private String mFollowingString;
    private MyViewPager mViewPager;
    private AbsMainViewHolder[] mViewHolders;
    private List<FrameLayout> mViewList;

    private UserHomeProfileViewHolder mProfileViewHolder;
    private MysDynamicVIewHolder mysDynamicVIewHolder;
    private MyPhotoViewHolder myPhotoViewHolder;
    private UserHomeSkillViewHolder mSkillViewHolder;
    private FrameLayout mRoot;


    private float mRate;
    private int[] mWhiteColorArgb;
    private int[] mBlackColorArgb;
    private ImageView mBtnBack;
    private View mBtnGroup;
    private View mTopGroup;
    private int mDp40;
    private View mTopHeadGroup;
    private View mTopBtnGroup;
    private View mTopBg;
    private View mBtnEdit;

    private ImageView mBtnMore;
    private int mOffest;
    private Handler mHandler;
    private boolean mSelf;
    private int mScreenWidth;
    private String mToUid;
    private JSONObject mUserObj;
    private UserBean mUserBean;
    private VoiceMediaPlayerUtil mVoiceMediaPlayerUtil;
    private Drawable[] mVoiceDrawables;
    private Drawable mVoiceEndDrawable;
    private ValueAnimator mAnimator;
    private boolean mNeedRefresh;
    private boolean mPaused;
    private View mSkillTextView;
    private ImageView mBtnReward;
    private GiftAnimViewHolder mGiftAnimViewHolder;

    private Drawable mFollowDrawable0;//未关注
    private Drawable mFollowDrawable1;//已关注
    private Drawable mFollowHeartDrawable0;
    private Drawable mFollowHeartDrawable1;
    private int mFollowColor0;//未关注
    private int mFollowColor1;//已关注

    //语音播放相关新改动
    private File mRecordVoiceFile;//录音文件
    private static final int WHAT_PLAY = 1;
    private boolean mPlayStarted;//是否正在播放语音介绍
    private boolean mPlayPaused;//是否播放语音介绍 已暂停
    private int mTotalVoiceLength;//语音总时长

    LinearLayout ll_SexGroup;
    ImageView iv_sex;
    TextView tv_age;
    ImageView iv_anchor_level;
    ImageView iv_level;
    private View mLLGroupFollowChat;
    private View fl_bottom_follow;//下方关注按钮背景设置
    private String[] titles;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_home;
    }


    @Override
    protected void main() {
        mToUid = getIntent().getStringExtra(Constants.TO_UID);
        mSelf = !TextUtils.isEmpty(mToUid) && mToUid.equals(CommonAppConfig.getInstance().getUid());
        mAppBarLayout = findViewById(R.id.appBarLayout);
        mRoot = (FrameLayout) findViewById(R.id.root);
        mAvatar1 = findViewById(R.id.avatar_1);
        mAvatar2 = findViewById(R.id.avatar_2);
        mName1 = findViewById(R.id.name_1);
        mName2 = findViewById(R.id.name_2);
        mCity1 = findViewById(R.id.city_1);
        mCity2 = findViewById(R.id.city_2);
        mSign = findViewById(R.id.sign);
        mFans = findViewById(R.id.fans);
        fl_bottom_follow = findViewById(R.id.fl_bottom_follow);
        mLLGroupFollowChat = findViewById(R.id.ll_group_follow_and_chat);
        ll_SexGroup = findViewById(R.id.ll_sex_group);
        iv_sex = ll_SexGroup.findViewById(R.id.sex);
        tv_age = ll_SexGroup.findViewById(R.id.age);
        iv_anchor_level = findViewById(R.id.iv_anchor_level);
        iv_level = findViewById(R.id.iv_level);
        mBtnVoice = findViewById(R.id.btn_voice);
        mVoiceImage = findViewById(R.id.voice_img);
        mVoiceTime = findViewById(R.id.voice_time);
        mBtnFollow = findViewById(R.id.btn_follow);
        mBtnFollow2 = findViewById(R.id.btn_follow_2);
        mBtnReward = (ImageView) findViewById(R.id.btn_reward);
        mFollowString = WordUtil.getString(R.string.follow);
        mFollowingString = WordUtil.getString(R.string.following);
        mWhiteColorArgb = getColorArgb(0xffffffff);
        mBlackColorArgb = getColorArgb(0xff323232);
        mBtnBack = findViewById(R.id.btn_back);
        mBtnMore = findViewById(R.id.btn_more);
        mBtnGroup = findViewById(R.id.btn_group);
        mTopGroup = findViewById(R.id.top_group);
        mDp40 = -DpUtil.dp2px(40);
        mTopHeadGroup = findViewById(R.id.top_head_group);
        mTopBtnGroup = findViewById(R.id.top_btn_group);
        mTopBg = findViewById(R.id.top_bg);
        mBtnEdit = findViewById(R.id.btn_edit);
        mScreenWidth = ScreenDimenUtil.getInstance().getScreenWdith();
        if (mSelf) {
            mBtnEdit.setVisibility(View.VISIBLE);
            mBtnEdit.setOnClickListener(this);
            mLLGroupFollowChat.setVisibility(View.INVISIBLE);
            mCity1.setVisibility(View.GONE);
        }
        fl_bottom_follow.setOnClickListener(this);
        //  mBtnFollow2.setOnClickListener(this);
        findViewById(R.id.btn_chat).setOnClickListener(this);
        //findViewById(R.id.btn_chat_2).setOnClickListener(this);
        mBtnVoice.setOnClickListener(this);
        mVoiceEndDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_user_home_voice_2);
        mViewList = new ArrayList<>();
        if (CommonAppConfig.getInstance().getIsState()==1){
            PAGE_COUNT=2;
        }else{
            PAGE_COUNT=4;
        }
        for (int i = 0; i < PAGE_COUNT; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewHolders = new AbsMainViewHolder[PAGE_COUNT];
        mViewPager = findViewById(R.id.viewPager);
        if (PAGE_COUNT > 1) {
            mViewPager.setOffscreenPageLimit(PAGE_COUNT - 1);
        }
        mViewPager.setAdapter(new ViewPagerAdapter(mViewList));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mSelf && position == 0 && mBtnEdit != null) {
                    // L.e("onPageScrolled---->"+position+"--positionOffset---"+positionOffset+"---positionOffsetPixels--"+positionOffsetPixels+"--mScreenWidth--"+mScreenWidth+"---mScreenWidth * positionOffset--"+(-mScreenWidth * positionOffset));
                    // mBtnEdit.setTranslationX(-mScreenWidth * positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
                loadPageData(position);
                if (mSelf && position == 0 && mBtnEdit != null) {
                    mBtnEdit.setVisibility(View.VISIBLE);
                } else {
                    if (mBtnEdit.getVisibility() == View.VISIBLE) {
                        mBtnEdit.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        MagicIndicator indicator = findViewById(R.id.indicator);
        if (CommonAppConfig.getInstance().getIsState()==1){
            titles = new String[]{
                    WordUtil.getString(R.string.user_detail),
                    WordUtil.getString(R.string.dynamic),
            };
        }else {
            titles = new String[]{
                    WordUtil.getString(R.string.user_detail),
                    WordUtil.getString(R.string.dynamic),
                    WordUtil.getString(R.string.alumb),
                    WordUtil.getString(R.string.skill)
            };
        }
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.gray1));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.textColor));
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setTextSize(15);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mViewPager != null) {
                            mViewPager.setCurrentItem(index);
                        }
                    }
                });

                if (index == 3) {
                    mSkillTextView = simplePagerTitleView;
                }

                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setLineWidth(DpUtil.dp2px(15));
                linePagerIndicator.setLineHeight(DpUtil.dp2px(3));
                linePagerIndicator.setRoundRadius(DpUtil.dp2px(2));
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.global));
                return linePagerIndicator;
            }
        });
        indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(indicator, mViewPager);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case WHAT_PLAY:
                        if (mVoiceMediaPlayerUtil != null) {
                            int curPosition = mTotalVoiceLength - mVoiceMediaPlayerUtil.getCurPosition();
                            if (mVoiceTime != null) {
                                mVoiceTime.setText(StringUtil.contact(String.valueOf(curPosition), "\""));
                            }
                            if (mHandler != null) {
                                mHandler.sendEmptyMessageDelayed(WHAT_PLAY, 500);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBtnGroup != null && mAppBarLayout != null) {
                    int[] location1 = new int[2];
                    mBtnGroup.getLocationOnScreen(location1);
                    int[] location2 = new int[2];
                    mTopBtnGroup.getLocationOnScreen(location2);
                    mOffest = location1[1] - location2[1];
                    mAppBarLayout.addOnOffsetChangedListener(UserHomeActivity.this);
                }
            }
        }, 300);
        mFollowDrawable1 = ContextCompat.getDrawable(mContext, R.drawable.bg_user_home_follow_1);
        mFollowDrawable0 = ContextCompat.getDrawable(mContext, R.drawable.bg_user_home_follow_0);
        mFollowHeartDrawable0 = ContextCompat.getDrawable(mContext, R.mipmap.ic_user_home_heart_follow);
        mFollowHeartDrawable1 = ContextCompat.getDrawable(mContext, R.mipmap.ic_user_home_heart_followed);
        mFollowColor0 = ContextCompat.getColor(mContext, R.color.white);
        mFollowColor1 = ContextCompat.getColor(mContext, R.color.white);
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }

        mBtnReward.setOnClickListener(this);
        getUserData();
    }

    private void loadPageData(int position) {
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
                    mProfileViewHolder = new UserHomeProfileViewHolder(mContext, parent);
                    vh = mProfileViewHolder;
                }
                if (position == 1) {
                    mysDynamicVIewHolder = createMysDynamicViewHolder(parent);
                    vh = mysDynamicVIewHolder;
                }

                if (position == 2) {
                    myPhotoViewHolder = createMysPhotoViewHolder(parent);
                    vh = myPhotoViewHolder;
                }

                if (position == 3) {
                    mSkillViewHolder = new UserHomeSkillViewHolder(mContext, parent, mSelf, mToUid);
                    vh = mSkillViewHolder;
                }

                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.subscribeActivityLifeCycle();
            }
        }
        if (vh != null) {
            vh.loadData();
        }
    }

    private MyPhotoViewHolder createMysPhotoViewHolder(FrameLayout parent) {
        MyPhotoViewHolder myPhotoViewHolder = new MyPhotoViewHolder(mContext, parent) {
            @Override
            public Observable<List<PhotoBean>> getData(int p) {
                return MainHttpUtil.getPhotos(mToUid, p);

            }
        };
        if (CommonAppConfig.getInstance().getUid().equals(mToUid)) {
            myPhotoViewHolder.setNoDataTip(WordUtil.getString(R.string.no_photo_tip_1));
        } else {
            myPhotoViewHolder.setNoDataTip(WordUtil.getString(R.string.no_photo_tip));
        }
        myPhotoViewHolder.setOnItemClickListner(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                toAlbumGallery(position);
            }
        });

        return myPhotoViewHolder;
    }

    private void toAlbumGallery(int position) {
        try {
            AlbumGalleryActivity.forwardWatch(this, (ArrayList<PhotoBean>) myPhotoViewHolder.findData(), position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MysDynamicVIewHolder createMysDynamicViewHolder(FrameLayout parent) {
        MysDynamicVIewHolder dynamicVIewHolder = new MysDynamicVIewHolder(mContext, parent) {
            @Override
            public Observable<List<MyDynamicBean>> getData(int p) {
                return DynamicHttpUtil.getDynamics(mToUid, p);
            }
        };
        if (CommonAppConfig.getInstance().getUid().equals(mToUid)) {
            dynamicVIewHolder.setNoDataTip(WordUtil.getString(R.string.no_dynamic_tip_2));
        } else {
            dynamicVIewHolder.setNoDataTip(WordUtil.getString(R.string.no_dynamic_tip));
        }
        return dynamicVIewHolder;
    }

    /**
     * 获取颜色的argb
     */
    private int[] getColorArgb(int color) {
        return new int[]{Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color)};
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        float totalScrollRange = appBarLayout.getTotalScrollRange() - mOffest;
        if (!mSelf) {
            if (verticalOffset != 0 && verticalOffset <= -mOffest) {
//                if (mTopBtnGroup != null && mTopBtnGroup.getVisibility() != View.VISIBLE) {
//                    mTopBtnGroup.setVisibility(View.VISIBLE);
//                }
            } else {
//                if (mTopBtnGroup != null && mTopBtnGroup.getVisibility() == View.VISIBLE) {
//                    mTopBtnGroup.setVisibility(View.INVISIBLE);
//                }
            }
        }
        verticalOffset = -verticalOffset - mOffest;
        if (verticalOffset < 0) {
            verticalOffset = 0;
        }
        float rate = verticalOffset / totalScrollRange;
        if (rate >= 1) {
            rate = 1;
        }
        if (mRate != rate) {
            mRate = rate;
            int a = (int) (mWhiteColorArgb[0] * (1 - rate) + mBlackColorArgb[0] * rate);
            int r = (int) (mWhiteColorArgb[1] * (1 - rate) + mBlackColorArgb[1] * rate);
            int g = (int) (mWhiteColorArgb[2] * (1 - rate) + mBlackColorArgb[2] * rate);
            int b = (int) (mWhiteColorArgb[3] * (1 - rate) + mBlackColorArgb[3] * rate);
            int color = Color.argb(a, r, g, b);
            mBtnBack.setColorFilter(color);
            mBtnMore.setColorFilter(color);
            mTopBg.setAlpha(rate);
            mTopHeadGroup.setAlpha(rate);
            mTopGroup.setTranslationY(rate * mDp40);
            mTopBtnGroup.setScaleX((1 - rate) * 0.2f + 0.8f);
            mTopBtnGroup.setScaleY((1 - rate) * 0.2f + 0.8f);
        }
    }

    private void getUserData() {
        if (!TextUtils.isEmpty(mToUid)) {
            MainHttpUtil.getUserHome(mToUid, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        UserBean u = JSON.toJavaObject(obj, UserBean.class);
                        mUserBean = u;
                        if (mBtnMore != null) {
                            if (CommonAppConfig.getInstance().getUid().equals(mUserBean.getId())) {
                                mBtnMore.setVisibility(View.INVISIBLE);
                            }
                        }
                        if (mAvatar1 != null) {
                            ImgLoader.display(mContext, u.getAvatar(), mAvatar1);
                        }
                        if (mAvatar2 != null) {
                            ImgLoader.display(mContext, u.getAvatarThumb(), mAvatar2);
                        }

                        ll_SexGroup.setBackground(CommonIconUtil.getSexBgDrawable(Integer.valueOf(u.getSex())));
                        iv_sex.setImageDrawable(CommonIconUtil.getSexDrawable(Integer.valueOf(u.getSex())));
                        tv_age.setText(u.getAge());
                        if (u.isShowAnchorLevel()){
                            iv_anchor_level.setVisibility(View.VISIBLE);
                            LevelBean anchorBean = CommonAppConfig.getInstance().getAnchorLevel(u.getAnchorLevel());
                            ImgLoader.display(mContext,anchorBean.getThumb(),iv_anchor_level);
                        }else {
                            if (iv_anchor_level.getVisibility() == View.VISIBLE){
                                iv_anchor_level.setVisibility(View.GONE);
                            }
                        }
                        LevelBean levelBean = CommonAppConfig.getInstance().getLevel(u.getLevel());
                        ImgLoader.display(mContext,levelBean.getThumb(),iv_level);
                        if (mName1 != null) {
                            mName1.setText(u.getUserNiceName());
                        }
                        if (mName2 != null) {
                            mName2.setText(u.getUserNiceName());
                        }
                        if (!mSelf && mCity1 != null) {
                            mCity1.setText(u.getCity());
                        }
                        if (mCity2 != null) {
                            mCity2.setText(u.getCity());
                        }
                        if (mSign != null) {
                            mSign.setText(u.getSignature());
                        }
                        if (mFans != null) {
                            mFans.setText(StringUtil.contact(StringUtil.toWan(mUserBean.getFansNum())
                                    , WordUtil.getString(R.string.fans)));
                        }
                        if (mBtnVoice != null && !TextUtils.isEmpty(u.getVoice())) {
                            mBtnVoice.setVisibility(View.VISIBLE);
                        }
                        if (mVoiceTime != null) {
                            mTotalVoiceLength = u.getVoiceDuration();
                            mVoiceTime.setText(StringUtil.contact(String.valueOf(u.getVoiceDuration()), "\""));
                        }

                        setFollowState(obj.getIntValue("isattent"));
                        mUserBean.setIsFollow(obj.getIntValue("isattent"));


                        mUserObj = obj;
                        if (mProfileViewHolder != null) {
                            mProfileViewHolder.setFirstLoadData(true);
                        }
                        if (mSkillViewHolder != null) {
                            mSkillViewHolder.setFirstLoadData(true);
                        }

                        JSONArray arr = mUserObj.getJSONArray("list");
                        if (arr.size() > 0) {
//                            if (mViewPager != null) {
//                                mViewPager.setCanScroll(true);
//                            }
                           /* if (mSkillTextView != null && mSkillTextView.getVisibility() != View.VISIBLE) {
                                mSkillTextView.setVisibility(View.VISIBLE);
                            }*/
                        } else {
//                            if (mViewPager != null) {
//                                mViewPager.setCanScroll(false);
//                            }
                            /*if (mSkillTextView != null && mSkillTextView.getVisibility() == View.VISIBLE) {
                                mSkillTextView.setVisibility(View.INVISIBLE);
                            }*/
                        }

                        if (mViewPager != null) {
                            loadPageData(mViewPager.getCurrentItem());
                        }

                    }
                }
            });
        }
    }

    private void setFollowState(int isattent) {
        if (isattent == 1) {
            if (mBtnFollow != null) {
                mBtnFollow.setText(mFollowingString);
                mBtnFollow.setTextColor(mFollowColor1);
                fl_bottom_follow.setBackground(mFollowDrawable1);
                mBtnFollow.setLeftDrawable(mFollowHeartDrawable1);
            }
//            if (mBtnFollow2 != null) {
//                mBtnFollow2.setText(mFollowingString);
//                mBtnFollow2.setTextColor(mFollowColor1);
//                mBtnFollow2.setBackground(mFollowDrawable1);
//            }
        } else {
            if (mBtnFollow != null) {
                mBtnFollow.setText(mFollowString);
                mBtnFollow.setTextColor(mFollowColor0);
                fl_bottom_follow.setBackground(mFollowDrawable0);
                mBtnFollow.setLeftDrawable(mFollowHeartDrawable0);
            }
//            if (mBtnFollow2 != null) {
//                mBtnFollow2.setText(mFollowString);
//                mBtnFollow2.setTextColor(mFollowColor0);
//                mBtnFollow2.setBackground(mFollowDrawable0);
//            }
        }
    }


    public JSONObject getUserObj() {
        return mUserObj;
    }


    public UserBean getUserBean() {
        return mUserBean;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBlackEvent(BlackEvent blackEvent) {
        if (blackEvent == null || mUserBean == null) {
            return;
        }
        int isBlack = blackEvent.getIsBlack();
        if (isBlack == 1) {
            mUserBean.setIsFollow(0);
            setFollowState(mUserBean.getIsFollow());
        }
        mUserBean.setIsblack(isBlack);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (e != null && !TextUtils.isEmpty(mToUid) && mToUid.equals(e.getToUid())) {
            if (mysDynamicVIewHolder != null) {
                mysDynamicVIewHolder.setAttention(e.getAttention());
            }
            if (e.getAttention() == 1) {
                if (mBtnFollow != null) {
                    if (mUserBean != null) {
                        mUserBean.setIsFollow(1);
                        mUserBean.setIsblack(0);
                    }
                    mBtnFollow.setText(mFollowingString);
                    mBtnFollow.setTextColor(mFollowColor1);
                    fl_bottom_follow.setBackground(mFollowDrawable1);
                    mBtnFollow.setLeftDrawable(mFollowHeartDrawable1);
                }
//                if (mBtnFollow2 != null) {
//                    mBtnFollow2.setText(mFollowingString);
//                    mBtnFollow2.setTextColor(mFollowColor1);
//                    mBtnFollow2.setBackground(mFollowDrawable1);
//                }
            } else {
                if (mUserBean != null) {
                    mUserBean.setIsFollow(0);
                }
                if (mBtnFollow != null) {
                    mBtnFollow.setText(mFollowString);
                    mBtnFollow.setTextColor(mFollowColor0);
                    fl_bottom_follow.setBackground(mFollowDrawable0);
                    mBtnFollow.setLeftDrawable(mFollowHeartDrawable0);
                }
               /* if (mBtnFollow2 != null) {
                    mBtnFollow2.setText(mFollowString);
                    mBtnFollow2.setTextColor(mFollowColor0);
                    mBtnFollow2.setBackground(mFollowDrawable0);
                }*/
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateFieldEvent(UpdateFieldEvent e) {
        if (mSelf) {
            mNeedRefresh = true;
        }
    }


    @Override
    protected void onDestroy() {
        CommonHttpUtil.cancel(CommonHttpConsts.SET_ATTENTION);
        CommonHttpUtil.cancel(CommonHttpConsts.SET_BLACK);
        MainHttpUtil.cancel(MainHttpConsts.GET_USER_HOME);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.destroy();
        }
        mVoiceMediaPlayerUtil = null;
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        if (mGiftAnimViewHolder != null) {
            mGiftAnimViewHolder.release();
        }
        mAnimator = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_edit) {
            if (CommonAppConfig.getInstance().getIsState()==1){
                ToastUtil.show(R.string.teenager_live_tip);
                return;
            }
            EditProfileActivity.forward(mContext);
        } else if (i == R.id.fl_bottom_follow) {
            clickFollow();
        } else if (i == R.id.btn_chat || i == R.id.btn_chat_2) {
            clickChat();
        } else if (i == R.id.btn_voice) {
            clickVoice();
        } else if (i == R.id.btn_reward) {
            openGiftDialog();
        }
    }

    private void openGiftDialog() {
        if (mUserBean == null) {
            return;
        }
        ChatGiftDialogFragment giftDialogFragment = new ChatGiftDialogFragment();
        giftDialogFragment.setActionListener(new ChatGiftDialogFragment.ActionListener() {
            @Override
            public void onChargeClick() {
                RouteUtil.forwardMyCoin();
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mUserBean.getId());
        bundle.putString(Constants.NICKNAME, mUserBean.getUserNiceName());
        bundle.putString(Constants.CHAT_SESSION_ID, "0");
        giftDialogFragment.setArguments(bundle);

        giftDialogFragment.show(getSupportFragmentManager());
    }

    /**
     * 收到消息的回调
     */


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatReceiveGiftBean(ChatReceiveGiftBean chatReceiveGiftBean) {
        if (mPaused) {
            return;
        }
        if (!TextUtils.isEmpty(mToUid) && mToUid.equals(chatReceiveGiftBean.getTouid())) {
            ChatReceiveGiftBean giftBean = chatReceiveGiftBean;
            if (giftBean != null) {
                showGift(chatReceiveGiftBean);
            }
        }
    }

    private void showGift(ChatReceiveGiftBean giftBean) {
        if (mGiftAnimViewHolder == null) {
            mGiftAnimViewHolder = new GiftAnimViewHolder(this, mRoot);
            mGiftAnimViewHolder.addToParent();
        }
        mGiftAnimViewHolder.showGiftAnim(giftBean);
    }


    private void clickFollow() {
        CommonHttpUtil.setAttention(mToUid, new CommonCallback<Integer>() {
            @Override
            public void callback(Integer bean) {
//                if (bean == 1){
//                    ToastUtil.show(WordUtil.getString(R.string.follow_success));
//                }else {
//                    ToastUtil.show(WordUtil.getString(R.string.follow_fail));
//                }
            }
        });
    }

    private void clickChat() {
        if (mUserBean != null) {
            ChatRoomActivity.forward(mContext, mUserBean, mUserBean.getIsFollow() == 1, false, true, true);
        }
    }


    private void clickVoice() {
        if (mPlayStarted) {
            if (!mPlayPaused) {
                pausePlay();
            } else {
                resumePlay();
            }
        } else {
            playVoice();
        }
    }

    /**
     * 暂停播放录音
     */
    public void pausePlay() {
        mPlayPaused = true;
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        if (mVoiceImage != null) {
            mVoiceImage.setImageDrawable(mVoiceEndDrawable);
        }
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.pausePlay();
        }
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_PLAY);
        }
    }

    /**
     * 恢复播放录音
     */
    public void resumePlay() {
        mPlayPaused = false;
        if (mAnimator != null) {
            mAnimator.start();
        }
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.resumePlay();
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessage(WHAT_PLAY);
        }
    }


    /**
     * 播放声音
     */
    private void playVoice() {
        if (mUserBean == null) {
            return;
        }
        String voice = mUserBean.getVoice();
        if (TextUtils.isEmpty(voice)) {
            return;
        }
        File dir = new File(CommonAppConfig.INNER_PATH + "/voice/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = MD5Util.getMD5(voice);
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        mRecordVoiceFile = new File(dir, fileName);
        if (mRecordVoiceFile.exists()) {
            startPlay(mRecordVoiceFile.getAbsolutePath());
        } else {
            DownloadUtil downloadUtil = new DownloadUtil();
            final Dialog dialog = DialogUitl.loadingDialog(mContext);
            dialog.show();
            downloadUtil.download("voice", dir, fileName, voice, new DownloadUtil.Callback() {
                @Override
                public void onSuccess(File file) {
                    mRecordVoiceFile = file;
                    dialog.dismiss();
                    startPlay(mRecordVoiceFile.getAbsolutePath());
                }

                @Override
                public void onProgress(int progress) {

                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.show(R.string.play_error);
                    dialog.dismiss();
                }
            });
        }
    }


    //点击更多按钮
    public void rightClick(View view) {
        showBottomDialog();
    }

    private void showBottomDialog() {
        if (mUserBean == null) {
            return;
        }
        String btnString1 = null;
        final int isBlack = mUserBean.getIsblack();
        if (isBlack == 1) {
            btnString1 = getString(R.string.black_ing);
        } else {
            btnString1 = getString(R.string.black);
        }
        String btnString2 = getString(R.string.report);
        BottomDealFragment.DialogButton dialogButton = new BottomDealFragment.DialogButton(btnString1, new BottomDealFragment.ClickListnter() {
            @Override
            public void click(View view) {
                if (isBlack == 0) {
                    showBalckDialog();
                } else {
                    CommonHttpUtil.setBlack(mToUid);
                }
            }
        }
        );
        BottomDealFragment.DialogButton dialogButton2 = new BottomDealFragment.DialogButton(btnString2, new BottomDealFragment.ClickListnter() {
            @Override
            public void click(View view) {
                UserReportActivity.forward(UserHomeActivity.this, mToUid);
            }
        }
        );
        BottomDealFragment bottomDealFragment = new BottomDealFragment();
        bottomDealFragment.setDialogButtonArray(dialogButton, dialogButton2);
        bottomDealFragment.show(getSupportFragmentManager());
    }

    private void showBalckDialog() {
        DialogUitl.showSimpleDialog(this, "", getString(R.string.black_tip), false, new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                CommonHttpUtil.setBlack(mToUid);
            }
        });
    }

    /**
     * 播放声音
     */
    private void startPlay(String filePath) {
        if (mVoiceMediaPlayerUtil == null) {
            mVoiceMediaPlayerUtil = new VoiceMediaPlayerUtil(mContext);
            mVoiceMediaPlayerUtil.setActionListener(new VoiceMediaPlayerUtil.ActionListener() {
                @Override
                public void onPrepared() {
                    if (mVoiceDrawables == null) {
                        mVoiceDrawables = new Drawable[3];
                        mVoiceDrawables[0] = ContextCompat.getDrawable(mContext, R.mipmap.icon_user_home_voice_0);
                        mVoiceDrawables[1] = ContextCompat.getDrawable(mContext, R.mipmap.icon_user_home_voice_1);
                        mVoiceDrawables[2] = mVoiceEndDrawable;
                    }
                    if (mAnimator == null) {
                        mAnimator = ValueAnimator.ofFloat(0, 900);
                        mAnimator.setInterpolator(new LinearInterpolator());
                        mAnimator.setDuration(700);
                        mAnimator.setRepeatCount(-1);
                        mAnimator.setRepeatMode(ValueAnimator.RESTART);
                        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float v = (float) animation.getAnimatedValue();
                                if (mVoiceImage != null) {
                                    int index = (int) (v / 300);
                                    if (index > 2) {
                                        index = 2;
                                    }
                                    mVoiceImage.setImageDrawable(mVoiceDrawables[index]);
                                }
                            }
                        });
                    }
                    mAnimator.start();
                }

                @Override
                public void onError() {
                    ToastUtil.show(R.string.play_error);
                    onPlayEnd();
                }

                @Override
                public void onPlayEnd() {
                    mPlayStarted = false;
                    if (mAnimator != null) {
                        mAnimator.cancel();
                    }
                    if (mVoiceImage != null) {
                        mVoiceImage.setImageDrawable(mVoiceEndDrawable);
                    }
                    if (mHandler != null) {
                        mHandler.removeMessages(WHAT_PLAY);
                    }
                    if (mVoiceTime != null) {
                        mVoiceTime.setText(StringUtil.contact(String.valueOf(mTotalVoiceLength), "\""));
                    }
                }
            });
        }
        mVoiceMediaPlayerUtil.startPlay(filePath);
        mPlayStarted = true;
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(WHAT_PLAY, 500);
        }
    }


    @Override
    protected void onPause() {
        mPaused = true;
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused && mNeedRefresh) {
            getUserData();
        }
        mPaused = false;
    }

}
