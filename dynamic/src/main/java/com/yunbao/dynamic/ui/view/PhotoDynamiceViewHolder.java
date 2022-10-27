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
import com.yunbao.common.custom.MyFrameLayout2;
import com.yunbao.common.custom.ViewPagerSnapHelper;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.ViewUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.bean.DynamicBean;
import com.yunbao.dynamic.widet.SimulateReclyViewTouchLisnter;
import com.yunbao.dynamic.widet.StartChangeOffectListner;

public class PhotoDynamiceViewHolder extends AbsDynamicDetailViewHolder implements View.OnClickListener {
    private AppBarLayout mAppBarLayout;
    private MyFrameLayout2 mFlAppbarContainer;
    private TextView mTvPhotoNum;
    private ViewGroup mVpContainer;
    private ViewGroup mLlSexGroup;
    private ImageView mSex;
    private TextView mAge;
    protected ImageView iv_anchor_level;
    protected ImageView iv_level;
    private TextView mTvTitle;
    private DrawableTextView mDtLocation;
    private TextView mTvTimeAddr;
    private RoundedImageView mImgSkill;
    private TextView mTvName;
    private TextView mTvLevel;
    private TextView mTvCoin;
    private DrawableTextView mTvOrderNum;
    private TextView mTvSkillName;
    private ViewGroup mVpUser;
    private ViewGroup mVpSkill;
    private DrawableTextView mTvLocation;
    private ViewGroup mVpTools;

    private GalleryViewHolder mGalleryViewHolder;
    private DynamicCommentViewHolder mDynamicCommentViewHolder;
    private StartChangeOffectListner mStartChangeOffectListner;

    private float mStatChangeOffect;
    private float mScale=0.3F;
    private int mFullLeftMargin;
    private int mNullLeftMargin;
    private int defaultSexGroupHeight=-1;

    @Override
    protected int getLayoutId() {
        return R.layout.view_photo_dynamic;
    }

    public PhotoDynamiceViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    @Override
    public void setData(DynamicBean dynamicBean) {
        this.mDynamicBean=dynamicBean;
        if(mDynamicBean==null){
            return;
        }
          initGallery();
          mGalleryViewHolder.setScaleType(ImageView.ScaleType.CENTER_CROP);
          mGalleryViewHolder.enableZoom(false);
          mGalleryViewHolder.setData(dynamicBean.getThumbs(),0);
          setCurrentPosition(0);
        initDynamicComment();
        if(haveSkill(dynamicBean)&&CommonAppConfig.getInstance().getIsState()!=1){
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

    private void initDynamicComment() {
        if(mDynamicCommentViewHolder==null){
            mDynamicCommentViewHolder=new DynamicCommentViewHolder(mContext,mVpContainer,mDynamicBean,true);
            mDynamicCommentViewHolder.subscribeActivityLifeCycle();
            mDynamicCommentViewHolder.setBackGroundColor(Color.WHITE);
            mDynamicCommentViewHolder.addToParent();
            mVpTools.addView(mDynamicCommentViewHolder.exportToolView());
        }
    }

    private void initGallery() {
        if(mGalleryViewHolder==null){
            mGalleryViewHolder=new GalleryViewHolder(mContext,mFlAppbarContainer);
            mGalleryViewHolder.addToParent(0);
            mGalleryViewHolder.subscribeActivityLifeCycle();
            mGalleryViewHolder.setPageSelectListner(new ViewPagerSnapHelper.OnPageSelectListner() {
                @Override
                public void onPageSelect(int position) {
                    setCurrentPosition(position);
                }
            });
        }
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

    private void setCurrentPosition(int position) {
        mTvPhotoNum.setText(position+1+"/"+mGalleryViewHolder.size());
    }

    @Override
    public void init() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mFlAppbarContainer = (MyFrameLayout2) findViewById(R.id.fl_appbar_container);
        mTvPhotoNum = (TextView) findViewById(R.id.tv_photo_num);
        mVpContainer = (ViewGroup) findViewById(R.id.vp_container);
        mImgAvatar = (ImageView) findViewById(R.id.img_top_avator);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mLlSexGroup = (ViewGroup) findViewById(R.id.ll_sex_group);
        mSex = (ImageView) findViewById(R.id.sex);
        iv_anchor_level = (ImageView) findViewById(R.id.iv_anchor_level);
        iv_level = (ImageView) findViewById(R.id.iv_level);
        mAge = (TextView) findViewById(R.id.age);
        mBtnFollow = (CheckedTextView) findViewById(R.id.btn_follow);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mDtLocation = (DrawableTextView) findViewById(R.id.dt_location);
        mTvTimeAddr = (TextView) findViewById(R.id.tv_time_addr);
        mImgSkill = (RoundedImageView) findViewById(R.id.img_skill);
        mTvSkillName = (TextView) findViewById(R.id.tv_skill_name);
        mTvLevel = (TextView) findViewById(R.id.tv_level);
        mTvCoin = (TextView) findViewById(R.id.tv_coin);
        mTvOrderNum = (DrawableTextView) findViewById(R.id.tv_order_num);
        mTvSkillName = (TextView) findViewById(R.id.tv_skill_name);
        mVpUser = (ViewGroup) findViewById(R.id.vp_user);
        mVpSkill = (ViewGroup) findViewById(R.id.vp_skill);
        mTvLocation = (DrawableTextView) findViewById(R.id.tv_location);
        mVpTools = (FrameLayout) findViewById(R.id.vp_tools);
        mVpSkill.setOnClickListener(this);

        addOnWatchOffectListner(new StartChangeOffectListner.OnWatchOffsetListner() {
            @Override
            public void offect(float rate) {
                offectTabChange(rate);
            }
        });
        mImgAvatar.setOnClickListener(this);
    }

    public PhotoDynamiceViewHolder addOnWatchOffectListner(StartChangeOffectListner.OnWatchOffsetListner watchOffsetListner){
        if(mStartChangeOffectListner==null){
            mStatChangeOffect= DpUtil.dp2px(100);
            mFullLeftMargin=DpUtil.dp2px(46);
            mNullLeftMargin=DpUtil.dp2px(10);
            mStartChangeOffectListner=new StartChangeOffectListner(mStatChangeOffect);
            mAppBarLayout.addOnOffsetChangedListener(mStartChangeOffectListner);
        }
        mStartChangeOffectListner.addOnWatchOffectListner(watchOffsetListner);
        return this;
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
    public void onDestroy() {
        super.onDestroy();
        if(mStartChangeOffectListner!=null){
            mStartChangeOffectListner.release();
        }
    }

    @Override
    public float defaultColorRate() {
        return 1;
    }
}
