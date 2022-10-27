package com.yunbao.main.activity;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.ViewPagerAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.event.FollowEvent;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DownloadUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.activity.ChatRoomActivity;
import com.yunbao.common.utils.VoiceMediaPlayerUtil;
import com.yunbao.main.R;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.main.bean.TagBean;
import com.yunbao.main.custom.StarCountView;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.main.views.SkillCommentViewHolder;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/7/20.
 */

@Route(path = RouteUtil.PATH_SKILL_HOME)
public class SkillHomeActivity extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context, String toUid, String skillId) {
        Intent intent = new Intent(context, SkillHomeActivity.class);
        intent.putExtra(Constants.TO_UID, toUid);
        intent.putExtra(Constants.SKILL_ID, skillId);
        context.startActivity(intent);
    }

    private static final int PAGE_COUNT = 1;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mCity;
    private View mSexGroup;
    private ImageView mSex;
    private TextView mAge;
    private TextView mBtnFollow;
    private Drawable mFollowDrawable0;//未关注
    private Drawable mFollowDrawable1;//已关注
    private int mFollowColor0;//未关注
    private int mFollowColor1;//已关注
    private ImageView mThumb;
    private TextView mSkillName;
    private TextView mSkillLevel;
    private TextView mPrice;
    private TextView mTags;
    private TextView mOrderNum;
    private StarCountView mStar;
    private TextView mScore;
    private View mBtnVoice;
    private ImageView mVoiceImage;
    private TextView mVoiceTime;
    private String mGameVoice;
    private TextView mDes;
    private ViewPager mViewPager;
    private MagicIndicator mIndicator;
    private AbsMainViewHolder[] mViewHolders;
    private List<FrameLayout> mViewList;
    private SkillCommentViewHolder mCommentViewHolder;
    private TextView mCommentTextView;
    private String mCommentString;
    private String mToUid;
    private String mSkillId;
    private boolean mSelf;
    private String mCoinName;
    private VoiceMediaPlayerUtil mVoiceMediaPlayerUtil;
    private Drawable[] mVoiceDrawables;
    private Drawable mVoiceEndDrawable;
    private ValueAnimator mAnimator;
    private UserBean mUserBean;
    private SkillBean mSkillBean;
    private List<TagBean> mTagList;


    private int mVoiceLength;//语音时间
    private int mVoiceCurrentTime;//当前正显示的语音时长
    private Handler mHandler;
    private static final int WHAT_VOICE_PROCESS = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_skill;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.game_skill));
        Intent intent = getIntent();
        mFollowDrawable1 = ContextCompat.getDrawable(mContext, R.drawable.bg_game_follow_1);
        mFollowDrawable0 = ContextCompat.getDrawable(mContext, R.drawable.bg_game_follow_0);
        mFollowColor0 = ContextCompat.getColor(mContext, R.color.global);
        mFollowColor1 = ContextCompat.getColor(mContext, R.color.gray3);
        mToUid = intent.getStringExtra(Constants.TO_UID);
        mSkillId = intent.getStringExtra(Constants.SKILL_ID);
        mSelf = !TextUtils.isEmpty(mToUid) && mToUid.equals(CommonAppConfig.getInstance().getUid());
        mAvatar = findViewById(R.id.avatar);
        mAvatar.setOnClickListener(this);
        mName = findViewById(R.id.name);
        mCity = findViewById(R.id.city);
        mSexGroup = findViewById(R.id.sex_group);
        mSex = findViewById(R.id.sex);
        mAge = findViewById(R.id.age);
        mBtnFollow = findViewById(R.id.btn_follow);
        mThumb = findViewById(R.id.thumb);
        mSkillName = findViewById(R.id.game_name);
        mSkillLevel = findViewById(R.id.game_level);
        mPrice = findViewById(R.id.price);
        mTags = findViewById(R.id.tags);
        mOrderNum = findViewById(R.id.order_num);
        mStar = findViewById(R.id.star);
        mScore = findViewById(R.id.score);
        mBtnVoice = findViewById(R.id.btn_voice);
        mVoiceImage = findViewById(R.id.voice_img);
        mVoiceTime = findViewById(R.id.voice_time);
        mDes = findViewById(R.id.des);
        if (!mSelf) {
            mBtnFollow.setVisibility(View.VISIBLE);
            mBtnFollow.setOnClickListener(this);
        } else {
            findViewById(R.id.bottom).setVisibility(View.GONE);
        }
        findViewById(R.id.btn_chat).setOnClickListener(this);
        findViewById(R.id.btn_order).setOnClickListener(this);
        mViewList = new ArrayList<>();
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

            }

            @Override
            public void onPageSelected(int position) {
                loadPageData(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mIndicator = (MagicIndicator) findViewById(R.id.indicator);
        mCommentString = WordUtil.getString(R.string.game_comment);
        final String[] titles = new String[]{
                mCommentString
        };
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.textColor));
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
                if (index == 0) {
                    mCommentTextView = simplePagerTitleView;
                }
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {

//                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
//                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
//                linePagerIndicator.setLineWidth(DpUtil.dp2px(15));
//                linePagerIndicator.setLineHeight(DpUtil.dp2px(3));
//                linePagerIndicator.setRoundRadius(DpUtil.dp2px(2));
//                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.global));
//                return linePagerIndicator;
                return null;
            }
        });
        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
        getData();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == WHAT_VOICE_PROCESS){
                    getNextTime();
                }
            }
        };
    }


    private void getNextTime(){
        if (mVoiceCurrentTime > 0){
            mVoiceCurrentTime--;
            updateVoicePlayTime();
            mHandler.sendEmptyMessageDelayed(WHAT_VOICE_PROCESS,1000);
        }else {
            mVoiceCurrentTime = mVoiceLength;
            updateVoicePlayTime();
            mHandler.removeMessages(WHAT_VOICE_PROCESS);
        }

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
                    mCommentViewHolder = new SkillCommentViewHolder(mContext, parent, mToUid, mSkillId);
                    vh = mCommentViewHolder;
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

    private void getData() {
        MainHttpUtil.getSkillHome(mToUid, mSkillId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    UserBean u = obj.toJavaObject(UserBean.class);
                    mUserBean = u;
                    if (mAvatar != null) {
                        ImgLoader.display(mContext, u.getAvatar(), mAvatar);
                    }
                    if (mName != null) {
                        mName.setText(u.getUserNiceName());
                    }
                    if (mSexGroup != null) {
                        mSexGroup.setBackground(CommonIconUtil.getSexBgDrawable(u.getSex()));
                    }
                    if (mSex != null) {
                        mSex.setImageDrawable(CommonIconUtil.getSexDrawable(u.getSex()));
                    }
                    if (mAge != null) {
                        mAge.setText(u.getAge());
                    }
                    if (mCity != null) {
                        mCity.setText(u.getCity());
                    }
                    SkillBean skillBean = JSON.parseObject(obj.getString("authinfo"), SkillBean.class);
                    mSkillBean = skillBean;
                    if (mThumb != null) {
                        ImgLoader.display(mContext, skillBean.getSkillThumb(), mThumb);
                    }
                    if (mSkillName != null) {
                        mSkillName.setText(skillBean.getSkillName());
                    }
                    if (mSkillLevel != null) {
                        mSkillLevel.setText(skillBean.getSkillLevel());
                    }
                    if (mPrice != null) {
                        mPrice.setText(skillBean.getPirceResult(mCoinName));
                    }
                    String[] labels = skillBean.getLabels();
                    if (labels != null && labels.length > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (String s : labels) {
                            sb.append(s);
                            sb.append(" ");
                        }
                        String tag = sb.toString().trim();
                        if (mTags != null) {
                            mTags.setText(tag);
                        }
                    }
                    if (mOrderNum != null) {
                        mOrderNum.setText(WordUtil.getString(R.string.game_order_num_1,skillBean.getOrderNum()));
                    }

                    if (mStar != null) {
                        mStar.setFillCount(skillBean.getStarCount());
                    }
                    if (mScore != null) {
                        mScore.setText(skillBean.getStarLevel());
                    }
                    String voice = skillBean.getSkillVoice();
                    if (!TextUtils.isEmpty(voice)) {
                        if (mBtnVoice != null) {
                            mBtnVoice.setVisibility(View.VISIBLE);
                            mBtnVoice.setOnClickListener(SkillHomeActivity.this);
                            mVoiceEndDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_skill_voice_2);
                        }
                        if (mVoiceTime != null) {
                            mVoiceLength = skillBean.getSkillVoiceDuration();
                            mVoiceCurrentTime = mVoiceLength;
                            mVoiceTime.setText(StringUtil.contact(String.valueOf(skillBean.getSkillVoiceDuration()), "\""));
                        }
                        mGameVoice = voice;
                    }

                    if (mDes != null) {
                        mDes.setText(skillBean.getDes());
                    }
                    if (mBtnFollow != null) {
                      //  mBtnFollow.setText(obj.getIntValue("isattent") == 1 ? WordUtil.getString(R.string.following) : WordUtil.getString(R.string.follow));
                        if (obj.getIntValue("isattent") == 1){
                            mBtnFollow.setText(WordUtil.getString(R.string.following));
                            mBtnFollow.setTextColor(mFollowColor1);
                            mBtnFollow.setBackground(mFollowDrawable1);
                        }else {
                            if (mBtnFollow != null) {
                                mBtnFollow.setText(WordUtil.getString(R.string.follow));
                                mBtnFollow.setTextColor(mFollowColor0);
                                mBtnFollow.setBackground(mFollowDrawable0);
                            }
                        }
                    }
                    if (mCommentTextView != null) {
                        mCommentTextView.setText(StringUtil.contact(mCommentString, "(", obj.getString("comment_nums"), ")"));
                    }

                    mTagList = new ArrayList<TagBean>();
                    JSONArray array = obj.getJSONArray("label_list");
                    for (int i = 0, size = array.size(); i < size; i++) {
                        JSONObject labelObj = array.getJSONObject(i);
                        TagBean tagBean = new TagBean(StringUtil.contact(labelObj.getString("label"), "(", labelObj.getString("nums"), ")"), "#969696", "#969696", "#F3F8FE");
                        mTagList.add(tagBean);
                    }
                    loadPageData(0);
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (!mSelf && e != null && mBtnFollow != null) {
           // mBtnFollow.setText(e.getAttention() == 1 ? WordUtil.getString(R.string.following) : WordUtil.getString(R.string.follow));
            if (e.getAttention() == 1){
                mBtnFollow.setText(WordUtil.getString(R.string.following));
                mBtnFollow.setTextColor(mFollowColor1);
                mBtnFollow.setBackground(mFollowDrawable1);
            }else {
                if (mBtnFollow != null) {
                    mBtnFollow.setText(WordUtil.getString(R.string.follow));
                    mBtnFollow.setTextColor(mFollowColor0);
                    mBtnFollow.setBackground(mFollowDrawable0);
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        CommonHttpUtil.cancel(CommonHttpConsts.SET_ATTENTION);
        MainHttpUtil.cancel(MainHttpConsts.GET_SKILL_HOME);
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.destroy();
        }
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mVoiceMediaPlayerUtil = null;
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_voice) {
            clickVoice();
        } else if (i == R.id.btn_chat) {
            if (mUserBean != null) {
                ChatRoomActivity.forward(mContext, mUserBean, mUserBean.getIsFollow() == 1, false, true, false);
            }
        } else if (i == R.id.btn_order) {
            if (mUserBean != null && mSkillBean != null) {
                OrderMakeActivity.forward(mContext, mUserBean, mSkillBean);
            }
        } else if (i == R.id.btn_follow) {
            CommonHttpUtil.setAttention(mToUid, null);
        } else if (i == R.id.avatar){
            RouteUtil.forwardUserHome(mToUid);
        }
    }

    //更新语音播放时间
    private void updateVoicePlayTime(){
        if (mVoiceTime != null) {
            mVoiceTime.setText(StringUtil.contact(String.valueOf(mVoiceCurrentTime), "\""));
        }
    }

    private void clickVoice() {
        if (mVoiceMediaPlayerUtil != null && mVoiceMediaPlayerUtil.isPaused()){
            mVoiceMediaPlayerUtil.resumePlay();
            mAnimator.resume();
            //剩余未播放时长
            int voiceResidue = mVoiceMediaPlayerUtil.getMSDuration() - mVoiceMediaPlayerUtil.getMSCurPosition();
            //控件显示的剩余时长 跟 实际语音的剩余时长
            int nextInterval = (mVoiceCurrentTime*1000 - voiceResidue);
            L.e("---clickVoice-1-->"+ voiceResidue);
            L.e("---clickVoice-2-->"+ nextInterval);
            if (nextInterval > 0 ){
                mHandler.sendEmptyMessageDelayed(WHAT_VOICE_PROCESS,(1-nextInterval));
            }else {
                mHandler.sendEmptyMessageDelayed(WHAT_VOICE_PROCESS,1000);
            }
            if (nextInterval > 1000){
                mVoiceCurrentTime = voiceResidue/1000;
                updateVoicePlayTime();
            }
            return;
        }
        if (mVoiceMediaPlayerUtil != null && mVoiceMediaPlayerUtil.isStarted()) {
            mVoiceMediaPlayerUtil.pausePlay();
            mHandler.removeMessages(WHAT_VOICE_PROCESS);
            if (mAnimator != null) {
                mAnimator.pause();
            }
            if (mVoiceImage != null) {
                mVoiceImage.setImageDrawable(mVoiceEndDrawable);
            }
        } else {
            playVoice();
        }
    }

    /**
     * 播放声音
     */
    private void playVoice() {
        if (TextUtils.isEmpty(mGameVoice)) {
            return;
        }
        File dir = new File(CommonAppConfig.INNER_PATH + "/voice/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = MD5Util.getMD5(mGameVoice);
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        File voiceFile = new File(dir, fileName);
        if (voiceFile.exists()) {
            startPlay(voiceFile.getAbsolutePath());
        } else {
            DownloadUtil downloadUtil = new DownloadUtil();
            final Dialog dialog = DialogUitl.loadingDialog(mContext);
            dialog.show();
            downloadUtil.download("voice", dir, fileName, mGameVoice, new DownloadUtil.Callback() {
                @Override
                public void onSuccess(File file) {
                    dialog.dismiss();
                    startPlay(file.getAbsolutePath());
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
                        mVoiceDrawables[0] = ContextCompat.getDrawable(mContext, R.mipmap.icon_skill_voice_0);
                        mVoiceDrawables[1] = ContextCompat.getDrawable(mContext, R.mipmap.icon_skill_voice_1);
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
                    mHandler.sendEmptyMessageDelayed(WHAT_VOICE_PROCESS,1000);
                }

                @Override
                public void onError() {
                    ToastUtil.show(R.string.play_error);
                    mVoiceCurrentTime = mVoiceLength;
                    updateVoicePlayTime();
                    mHandler.removeMessages(WHAT_VOICE_PROCESS);
                    onPlayEnd();
                }

                @Override
                public void onPlayEnd() {
                    mHandler.removeMessages(WHAT_VOICE_PROCESS);
                    mVoiceCurrentTime = mVoiceLength;
                    updateVoicePlayTime();
                    if (mAnimator != null) {
                        mAnimator.cancel();
                    }
                    if (mVoiceImage != null) {
                        mVoiceImage.setImageDrawable(mVoiceEndDrawable);
                    }
                }
            });
        }
        mVoiceMediaPlayerUtil.startPlay(filePath);
    }

    public List<TagBean> getTagList() {
        return mTagList;
    }


}
