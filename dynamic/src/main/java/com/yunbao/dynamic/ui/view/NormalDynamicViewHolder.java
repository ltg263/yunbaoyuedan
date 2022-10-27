package com.yunbao.dynamic.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.custom.DrawableTextView;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.ViewUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.bean.DynamicBean;
import com.yunbao.dynamic.widet.SimulateReclyViewTouchLisnter;

public class NormalDynamicViewHolder extends AbsDynamicDetailViewHolder implements View.OnClickListener {
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
    protected DrawableTextView mTvOrderNum;
    protected LinearLayout mVpContainer;
    protected FrameLayout mFlOntherContainer;

    protected DynamicCommentViewHolder mDynamicCommentViewHolder;

    public NormalDynamicViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void setData(DynamicBean dynamicBean) {
       this.mDynamicBean=dynamicBean;
        initDynamicComment();
        if(haveSkill(dynamicBean)){
            mVpSkill.setVisibility(View.VISIBLE);
            SkillBean skillBean=mDynamicBean.getSkillinfo();
            if(skillBean!=null){
                this.skillBean=skillBean;
                ImgLoader.display(mContext,skillBean.getSkillThumb(),mImgSkill);
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
        return R.layout.view_dynamic_normal;
    }
    @Override
    public void init() {
        mVpUser = (LinearLayout) findViewById(R.id.vp_user);
        mImgAvatar =  findViewById(R.id.img_top_avator);
        mVpUserContainer = (LinearLayout) findViewById(R.id.vp_user_container);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mLlSexGroup = (LinearLayout) findViewById(R.id.ll_sex_group);
        mSex = (ImageView) findViewById(R.id.sex);
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
        mBtnFollow.setOnClickListener(this);
        mFlOntherContainer = (FrameLayout) findViewById(R.id.fl_onther_container);
        mImgAvatar.setOnClickListener(this);
    }

    protected void initDynamicComment() {
        if(mDynamicCommentViewHolder==null){
            mDynamicCommentViewHolder=new DynamicCommentViewHolder(mContext,mVpContainer,mDynamicBean,true);
            mDynamicCommentViewHolder.subscribeActivityLifeCycle();
            mDynamicCommentViewHolder.setBackGroundColor(Color.WHITE);
            mDynamicCommentViewHolder.addToParent();
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_follow){
            follow(v);
        }else if(id==R.id.vp_skill){
            toPlaceAnOrder();
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
