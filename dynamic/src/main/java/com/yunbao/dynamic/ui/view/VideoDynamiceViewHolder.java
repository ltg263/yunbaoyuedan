package com.yunbao.dynamic.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.video.ui.view.AbsPlayViewHolder;
import com.example.video.ui.view.IjkViewPlayer;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.custom.DrawableTextView;
import com.yunbao.common.custom.ValueFrameAnimator;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ViewUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.bean.DynamicBean;
import com.yunbao.dynamic.business.AnimHelper;
import com.yunbao.dynamic.event.DynamicCommentNumberEvent;
import com.yunbao.dynamic.event.DynamicLikeEvent;
import com.yunbao.dynamic.http.DynamicHttpUtil;
import com.yunbao.dynamic.ui.dialog.CommentDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class VideoDynamiceViewHolder extends AbsDynamicDetailViewHolder implements View.OnClickListener {

    private TextView mTvName;
    private LinearLayout mLlSexGroup;
    private ImageView mSex;
    private ImageView iv_anchor_level;
    private ImageView iv_level;
    private TextView mTvage;
    private TextView tvTitle;
    private LinearLayout mLlSkill;
    private ImageView mImgSkill;
    private TextView mTvSkillMessage;
    private ImageView mBtnLike;
    private TextView mTvLikeNum;
    private TextView mTvCommentNum;
    private ImageView mBtnSkill;
    private TextView mTvLocation;
    private AbsPlayViewHolder mViewHolder;
    private Drawable[] mLikeAnimDrawables;//点赞帧动画
    private ValueFrameAnimator mValueFrameAnimator;
    private DrawableTextView mTvDetailLocation;

    public VideoDynamiceViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_video_dynamic;
    }

    @Override
    public void init() {
        mLikeAnimDrawables = AnimHelper.createDrawableArray(mContext, AnimHelper.FOLLOW_ANIM_VIDEO_LIST);
        mValueFrameAnimator = ValueFrameAnimator.
                ofFrameAnim(mLikeAnimDrawables)
                .setSingleInterpolator(new OvershootInterpolator())
                .durcation(600);

        mImgAvatar = findViewById(R.id.img_avatar);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mBtnFollow = (CheckedTextView) findViewById(R.id.btn_follow);
        mLlSexGroup = (LinearLayout) findViewById(R.id.ll_sex_group);
        mSex = (ImageView) findViewById(R.id.sex);
        iv_anchor_level = (ImageView) findViewById(R.id.iv_anchor_level);
        iv_level = (ImageView) findViewById(R.id.iv_level);
        mTvage = (TextView) findViewById(R.id.age);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        mLlSkill = (LinearLayout) findViewById(R.id.ll_skill);
        mImgSkill = (ImageView) findViewById(R.id.img_skill);
        mTvSkillMessage = (TextView) findViewById(R.id.tv_skill_message);
        mBtnLike = (ImageView) findViewById(R.id.btn_like);
        mTvLikeNum = (TextView) findViewById(R.id.like_num);
        mTvCommentNum = (TextView) findViewById(R.id.comment_num);
        mBtnSkill = (ImageView) findViewById(R.id.btn_skill);
        mTvLocation = findViewById(R.id.tv_location);
        mTvDetailLocation = (DrawableTextView) findViewById(R.id.tv_detail_location);
        mValueFrameAnimator.anim(mBtnLike);

        mBtnLike.setOnClickListener(this);
        mBtnSkill.setOnClickListener(this);
        mLlSkill.setOnClickListener(this);
        mBtnFollow.setOnClickListener(this);
        mImgAvatar.setOnClickListener(this);

        setOnClickListner(R.id.btn_comment, this);
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void setData(DynamicBean dynamicBean) {
        this.mDynamicBean = dynamicBean;
        if (dynamicBean == null) {
            return;
        }
        mTvCommentNum.setText(dynamicBean.getComments() + "");
        tvTitle.setText(dynamicBean.getContent());
        mSex.setImageDrawable(CommonIconUtil.getSexDrawable(dynamicBean.getSex()));
        if (dynamicBean.showAnchorLevel()){
            iv_anchor_level.setVisibility(View.VISIBLE);
            LevelBean anchorBean = CommonAppConfig.getInstance().getAnchorLevel(dynamicBean.getLevel_anchor());
            ImgLoader.display(mContext,anchorBean.getThumb(),iv_anchor_level);
        }else {
            if (iv_anchor_level.getVisibility() == View.VISIBLE){
                iv_anchor_level.setVisibility(View.GONE);
            }
        }
        LevelBean levelBean = CommonAppConfig.getInstance().getLevel(dynamicBean.getLevel());
        ImgLoader.display(mContext,levelBean.getThumb(),iv_level);
        mLlSexGroup.setBackground(CommonIconUtil.getSexBgDrawable(dynamicBean.getSex()));
        mTvName.setText(dynamicBean.getUser_nickname());
        ImgLoader.display(mContext, dynamicBean.getAvatar(), mImgAvatar);
        mTvage.setText(dynamicBean.getAge() + "");
        setLikes();
        if (dynamicBean.getIslike() == 1) {
            mBtnLike.setImageDrawable(mLikeAnimDrawables[mLikeAnimDrawables.length - 1]);
        } else {
            mBtnLike.setImageDrawable(mLikeAnimDrawables[0]);
        }
        int isAttention = dynamicBean.getIsattent();
        followButtonState(isAttention);

        if (TextUtils.isEmpty(dynamicBean.getAddr())) {
            mLlSkill.setVisibility(View.GONE);
        } else {
            ViewUtil.setTextNoContentGone(mTvLocation, dynamicBean.getAddr());
        }
        SkillBean skillBean = dynamicBean.getSkillinfo();
        if (haveSkill(dynamicBean)&&CommonAppConfig.getInstance().getIsState()!=1) {
            this.skillBean = skillBean;
            ImgLoader.display(mContext, skillBean.getSkillThumb(), mImgSkill);
            ImgLoader.display(mContext, skillBean.getSkillThumb(), mBtnSkill);
            mTvSkillMessage.setText(skillBean.getSkillName2() + " " + skillBean.getPirceResult());
            mLlSkill.setVisibility(View.VISIBLE);
        } else {
            mLlSkill.setVisibility(View.GONE);
        }
        ViewUtil.setTextNoContentGone(mTvDetailLocation, dynamicBean.getLocation());
        mTvLikeNum.setText(dynamicBean.getLikes() + "");
        setVideo(dynamicBean.getVideo());
    }

    private void setLikes() {
        if (mDynamicBean != null) {
            mTvLikeNum.setText(mDynamicBean.getLikes() + "");
        }
    }

    private void setVideo(String videoPath) {
        if (TextUtils.isEmpty(videoPath)) {
            return;
        }
        if (mViewHolder == null) {
            mViewHolder = new IjkViewPlayer(mContext, (ViewGroup) mContentView);
            mViewHolder.addToParent(0);
            mViewHolder.subscribeActivityLifeCycle();
        }
        mViewHolder.play(mDynamicBean.getVideo(), mDynamicBean.getVideo_t());
    }

    public void toggleLike() {
        if (mDynamicBean == null) {
            return;
        }
        DynamicHttpUtil.dynamicAddLikeDefault(mDynamicBean.getId()).subscribe(new DefaultObserver<JSONObject>() {
            @Override
            public void onNext(JSONObject jsonObject) {
                if (jsonObject == null) {
                    return;
                }
                Integer isLike = jsonObject.getInteger("islike");
                int likesNum = jsonObject.getInteger("likes");
                EventBus.getDefault().post(new DynamicLikeEvent(isLike, likesNum, mDynamicBean.getId()));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDynamicLikeEvent(DynamicLikeEvent dynamicLikeEvent) {
        if (mDynamicBean == null || !StringUtil.equals(dynamicLikeEvent.getDynamicId(), mDynamicBean.getId())) {
            return;
        }
        mDynamicBean.setLikes(dynamicLikeEvent.getLikesNum());
        mDynamicBean.setIslike(dynamicLikeEvent.getIsLike());
        if (dynamicLikeEvent.getIsLike() == 0) {
            mValueFrameAnimator.reverse();
        } else {
            mValueFrameAnimator.start();
        }
        setLikes();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDynamicCommentEvent(DynamicCommentNumberEvent dynamicCommentNumberEvent) {
        mTvCommentNum.setText(dynamicCommentNumberEvent.getNum() + "");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_follow) {
            follow(v);
        } else if (id == R.id.btn_like) {
            toggleLike();
        } else if (id == R.id.btn_skill || id == R.id.ll_skill) {
            toPlaceAnOrder();
        } else if (id == R.id.btn_comment) {
            openCommentDialog();
        } else if (id == R.id.img_avatar) {
            toUserHome();
        }
    }

    private void openCommentDialog() {
        CommentDialogFragment commentDialogFragment = new CommentDialogFragment();
        commentDialogFragment.setDynamicBean(mDynamicBean);
        commentDialogFragment.show(getActivity().getSupportFragmentManager());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mValueFrameAnimator != null) {
            mValueFrameAnimator.release();
            mValueFrameAnimator = null;
        }
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public float defaultColorRate() {
        return 1;
    }
}
