package com.yunbao.dynamic.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.custom.DrawableTextView;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.ViewUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.bean.DynamicBean;
import com.yunbao.dynamic.bean.DynamicSkillBean;
import com.yunbao.dynamic.widet.SimulateReclyViewTouchLisnter;
import com.yunbao.dynamic.widet.StartChangeOffectListner;

public class NormalScrollDynamicViewHolder extends AbsDynamicDetailViewHolder implements View.OnClickListener {
    protected LinearLayout mVpUser;
    protected LinearLayout mVpUserContainer;
    protected TextView mTvName;
    protected LinearLayout mLlSexGroup;
    protected ImageView mSex;
    protected TextView mAge;
    protected TextView mTvTitle;
    protected DrawableTextView mTvLocation;
    protected TextView mTvTimeAddr;
    protected FrameLayout mVpSkill;
    protected RoundedImageView mImgSkill;
    protected TextView mTvSkillName;
    protected TextView mTvLevel;
    protected TextView mTvCoin;
    protected ImageView iv_anchor_level;
    protected ImageView iv_level;
    protected DrawableTextView mTvOrderNum;
    protected LinearLayout mVpContainer;
    protected FrameLayout mFlOntherContainer;
    private AppBarLayout mAppBarLayout;
    protected DynamicCommentViewHolder mDynamicCommentViewHolder;
    private StartChangeOffectListner mStartChangeOffectListner;

    private float mScale=0.3F;
    private int mStatChangeOffect;
    private int mFullLeftMargin;
    private int mNullLeftMargin;
    private int defaultSexGroupHeight=-1;
    private ViewGroup mVpTools;

    public NormalScrollDynamicViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void setData(DynamicBean dynamicBean) {
        this.mDynamicBean=dynamicBean;
        initDynamicComment();
        if(haveSkill(dynamicBean)&&CommonAppConfig.getInstance().getIsState()!=1){
            mVpSkill.setVisibility(View.VISIBLE);
            SkillBean skillBean=mDynamicBean.getSkillinfo();
            if(skillBean!=null){
                this.skillBean=skillBean;
                ImgLoader.display(mContext,skillBean.getAuthThumb(),mImgSkill);
                mTvCoin.setText(skillBean.getPirceResult());
                mTvSkillName.setText(skillBean.getSkillName2());
                ViewUtil.setTextNoContentHide(mTvLevel,skillBean.getSkillLevel());
                mTvOrderNum.setText(WordUtil.getString(R.string.received_orders_nums,skillBean.getOrderNum()));
            }
        }else{
            mVpSkill.setVisibility(View.GONE);
        }
        mTvTitle.setText(dynamicBean.getContent());
        mSex.setImageDrawable(CommonIconUtil.getSexDrawable(dynamicBean.getSex()));
        mLlSexGroup.setBackground(CommonIconUtil.getSexBgDrawable(dynamicBean.getSex()));
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
        mTvName.setText(dynamicBean.getUser_nickname());
        ImgLoader.display(mContext,dynamicBean.getAvatar(),mImgAvatar);
        mAge.setText(dynamicBean.getAge()+"");
        ViewUtil.setTextNoContentGone(mTvLocation,dynamicBean.getLocation());
        ViewUtil.setTextNoContentHide(mTvTimeAddr,dynamicBean.getAddrAndTime());
        followButtonState(mDynamicBean.getIsattent());
        mBtnFollow.setOnClickListener(this);
        mVpSkill.setOnTouchListener(new SimulateReclyViewTouchLisnter(mDynamicCommentViewHolder.getRecyclerView()));
        mVpUser.setOnTouchListener(new  SimulateReclyViewTouchLisnter(mDynamicCommentViewHolder.getRecyclerView()));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_dynamic_scroll_normal;
    }
    @Override
    public void init() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mVpUser = (LinearLayout) findViewById(R.id.vp_user);
        mImgAvatar =  findViewById(R.id.img_top_avator);
        mVpUserContainer = (LinearLayout) findViewById(R.id.vp_user_container);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mLlSexGroup = (LinearLayout) findViewById(R.id.ll_sex_group);
        mSex = (ImageView) findViewById(R.id.sex);
        iv_anchor_level = (ImageView) findViewById(R.id.iv_anchor_level);
        iv_level = (ImageView) findViewById(R.id.iv_level);
        mAge = (TextView) findViewById(R.id.age);
        mBtnFollow = (CheckedTextView) findViewById(R.id.btn_follow);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvLocation = (DrawableTextView) findViewById(R.id.tv_location);
        mTvTimeAddr = (TextView) findViewById(R.id.tv_time_addr);
        mVpSkill = (FrameLayout) findViewById(R.id.vp_skill);
        mImgSkill = (RoundedImageView) findViewById(R.id.img_skill);
        mTvSkillName = (TextView) findViewById(R.id.tv_skill_name);
        mTvLevel = (TextView) findViewById(R.id.tv_level);
        mTvCoin = (TextView) findViewById(R.id.tv_coin);
        mTvOrderNum = (DrawableTextView) findViewById(R.id.tv_order_num);
        mVpContainer = (LinearLayout) findViewById(R.id.vp_container);
        mVpTools = (FrameLayout) findViewById(R.id.vp_tools);
        mBtnFollow.setOnClickListener(this);
        mFlOntherContainer = (FrameLayout) findViewById(R.id.fl_onther_container);
        mImgAvatar.setOnClickListener(this);
        mVpSkill.setOnClickListener(this);
        addOnWatchOffectListner(new StartChangeOffectListner.OnWatchOffsetListner() {
            @Override
            public void offect(float rate) {
                offectTabChange(rate);
            }
        });
    }

    protected void initDynamicComment() {
        if(mDynamicCommentViewHolder==null){
            mDynamicCommentViewHolder=new DynamicCommentViewHolder(mContext,mVpContainer,mDynamicBean,true);
            mDynamicCommentViewHolder.subscribeActivityLifeCycle();
            mDynamicCommentViewHolder.setBackGroundColor(Color.WHITE);
            mDynamicCommentViewHolder.addToParent();

            mVpTools.addView(mDynamicCommentViewHolder.exportToolView());
        }
    }

    public NormalScrollDynamicViewHolder addOnWatchOffectListner(StartChangeOffectListner.OnWatchOffsetListner watchOffsetListner){
        if(mStartChangeOffectListner==null){
            mStatChangeOffect= DpUtil.dp2px(65);
            mFullLeftMargin=DpUtil.dp2px(46);
            mNullLeftMargin=DpUtil.dp2px(10);
            mStartChangeOffectListner=new StartChangeOffectListner(mStatChangeOffect);
            mAppBarLayout.addOnOffsetChangedListener(mStartChangeOffectListner);
        }
        mStartChangeOffectListner.addOnWatchOffectListner(watchOffsetListner);
        return this;
    }


    /*rate=0～1，滑到最顶部的时候是1。监听需要变化的时候 在StartChangeOffectListner计算得出*/
    private void offectTabChange(float rate) {
        if(defaultSexGroupHeight==-1||defaultSexGroupHeight==0){
            defaultSexGroupHeight= mLlSexGroup.getHeight();
        }
        LinearLayout.LayoutParams sexLayoutParm= (LinearLayout.LayoutParams) mLlSexGroup.getLayoutParams();
        sexLayoutParm.height= (int) (rate*defaultSexGroupHeight);
        mLlSexGroup.setAlpha(rate);

        LinearLayout.LayoutParams followLayoutParams= (LinearLayout.LayoutParams) mBtnFollow.getLayoutParams();
        followLayoutParams.rightMargin= (int) (mFullLeftMargin-rate*(mFullLeftMargin-mNullLeftMargin));

        LinearLayout.LayoutParams avatorLayoutParams= (LinearLayout.LayoutParams) mImgAvatar.getLayoutParams();
        avatorLayoutParams.leftMargin= (int) (mFullLeftMargin-rate*(mFullLeftMargin-mNullLeftMargin));
        float scale=1+mScale*(rate-1);
        mImgAvatar.setScaleX(scale);
        mImgAvatar.setScaleY(scale);
        mImgAvatar.requestLayout();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mStartChangeOffectListner!=null){
            mStartChangeOffectListner.release();
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_follow){
            follow(v);
        }else if(id==R.id.vp_skill){
            toPlaceAnOrder();
        }else if(id==R.id.img_top_avator){
            toUserHome();
        }
    }

    @Override
    public float defaultColorRate() {
        return 0;
    }
}
