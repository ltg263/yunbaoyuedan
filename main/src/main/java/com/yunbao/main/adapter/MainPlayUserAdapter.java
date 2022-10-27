package com.yunbao.main.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.youth.banner.loader.ImageLoader;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.custom.ValueFrameAnimator;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.GlideCatchUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ViewUtil;
import com.yunbao.dynamic.bean.UserLabelInfoBean;
import com.yunbao.dynamic.http.DynamicHttpUtil;
import com.yunbao.main.R;
import com.yunbao.dynamic.bean.DynamicUserBean;
import com.yunbao.dynamic.business.AnimHelper;
import java.util.List;

//用户列表
public class MainPlayUserAdapter extends BaseRecyclerAdapter<DynamicUserBean, BaseReclyViewHolder> {

    private Drawable[] mLikeAnimDrawables;//点赞帧动画
    ValueFrameAnimator valueFrameAnimator;


    public MainPlayUserAdapter(List<DynamicUserBean> data, Context context) {
        super(data);
        mLikeAnimDrawables= AnimHelper.createDrawableArray(context,AnimHelper.FOLLOW_ANIM_LIST);
        valueFrameAnimator=ValueFrameAnimator.
                ofFrameAnim(mLikeAnimDrawables)
                .setSingleInterpolator(new OvershootInterpolator())
                .durcation(500);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_dynamic;
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, DynamicUserBean item) {
        TextView tv_order_num = helper.getView(R.id.order_num);
//        TextView tv_star_level = helper.getView(R.id.star_level);
        LinearLayout ll_SexGroup  = helper.getView(R.id.ll_sex_group);
        ImageView iv_sex = ll_SexGroup.findViewById(R.id.sex);
        TextView  tv_age = ll_SexGroup.findViewById(R.id.age);
        ImageView iv_game1 = helper.getView(R.id.iv_game1);
        ImageView iv_game2 = helper.getView(R.id.iv_game2);
        ImageView iv_game3 = helper.getView(R.id.iv_game3);
        TextView tv_label = helper.getView(R.id.tv_label);
        UserLabelInfoBean labelInfoBean = item.getLabelinfo();
        if (labelInfoBean != null && !labelInfoBean.isUnable()){
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);//设置线性渐变，除此之外还有：GradientDrawable.SWEEP_GRADIENT（扫描式渐变），GradientDrawable.RADIAL_GRADIENT（圆形渐变）
            gradientDrawable.setOrientation(GradientDrawable.Orientation.BL_TR);
            gradientDrawable.setCornerRadius(DpUtil.dp2px(10));
            gradientDrawable.setColors(new int[]{Color.parseColor(labelInfoBean.getColour()), Color.parseColor(labelInfoBean.getColour2())});//增加渐变效果需要使用setColors方法来设置颜色（中间可以增加多个颜色值）
            tv_label.setBackground(gradientDrawable);
            tv_label.setText(labelInfoBean.getName());
            if (tv_label.getVisibility() != View.VISIBLE){
                tv_label.setVisibility(View.VISIBLE);
            }
        }else {
            tv_label.setVisibility(View.INVISIBLE);
        }
        List<SkillBean>  skillList = item.getSkill_list();
        if (skillList != null && !skillList.isEmpty()){
            if (skillList.size() > 0){
                ImgLoader.display(mContext,skillList.get(0).getSkill_thumb(),iv_game1);
                iv_game1.setVisibility(View.VISIBLE);
            }else {
                if (iv_game1.getVisibility() == View.VISIBLE){
                    iv_game1.setVisibility(View.GONE);
                }
            }
            if (skillList.size() > 1){
                ImgLoader.display(mContext,skillList.get(1).getSkill_thumb(),iv_game2);
                iv_game2.setVisibility(View.VISIBLE);
            }else {
                if (iv_game2.getVisibility() == View.VISIBLE){
                    iv_game2.setVisibility(View.GONE);
                }
            }
            if (skillList.size() > 2){
                ImgLoader.display(mContext,skillList.get(2).getSkill_thumb(),iv_game3);
                iv_game3.setVisibility(View.VISIBLE);
            }else {
                if (iv_game3.getVisibility() == View.VISIBLE){
                    iv_game3.setVisibility(View.GONE);
                }
            }
        }else{
            if (iv_game1.getVisibility() == View.VISIBLE){
                iv_game1.setVisibility(View.GONE);
            }
            if (iv_game2.getVisibility() == View.VISIBLE){
                iv_game2.setVisibility(View.GONE);
            }
            if (iv_game3.getVisibility() == View.VISIBLE){
                iv_game3.setVisibility(View.GONE);
            }
        }

        ll_SexGroup.setBackground(CommonIconUtil.getSexBgDrawable(Integer.valueOf(item.getSex())));
        iv_sex.setImageDrawable(CommonIconUtil.getSexDrawable(Integer.valueOf(item.getSex())));
        tv_age.setText(item.getAge());
//        tv_star_level.setText(item.getStar());
        tv_order_num.setText(item.getOrders());
        helper.setImageUrl(item.getAvatar(),R.id.thumb);
        helper.setText(R.id.tv_name,item.getUser_nickname());
    }


    private void toggleFollow(View view,  boolean isLike) {
        valueFrameAnimator.anim((ImageView) view);
        if(isLike){
        valueFrameAnimator.start();
        }else{
        valueFrameAnimator.reverse();
        }
    }


    private OnItemChildClickListener onItemChildClickListener=new OnItemChildClickListener() {
        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            DynamicUserBean DynamicUserBean= mData.get(position);
            dynamicAddLike(view,DynamicUserBean);
        }
    };


    private void dynamicAddLike(final View view, final DynamicUserBean DynamicUserBean) {
        DynamicHttpUtil.dynamicAddLike(DynamicUserBean.getId()).subscribe(new DefaultObserver<Integer>() {
            @Override
            public void onNext(Integer isLike) {
//                addLikeSuccess(view,DynamicUserBean,isLike);
            }
        });
    }

}
