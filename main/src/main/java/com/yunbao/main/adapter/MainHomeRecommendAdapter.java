package com.yunbao.main.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.custom.LadderTextView;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.bean.DynamicUserBean;
import com.yunbao.dynamic.bean.UserLabelInfoBean;
import com.yunbao.main.R;

import java.util.List;

/**
 * 首页 推荐
 */

public class MainHomeRecommendAdapter extends RefreshAdapter<DynamicUserBean> {
    private View.OnClickListener mOnClickListener;
    private String mCoinName;
    private View mHeadView;
    private static final int HEAD = -1;

    public MainHomeRecommendAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mList.get(position-1), position-1);
                    }
                }
            }
        };
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mHeadView = mInflater.inflate(R.layout.item_view_main_home_recommend_head,null,false);
    }

    public void setData(List<DynamicUserBean> list){
        mList = list;
        notifyDataSetChanged();
    }

    public void addData(List<DynamicUserBean> list){
        if (mList != null){
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            ViewParent viewParent = mHeadView.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(mHeadView);
            }
            HeadVh headVh = new HeadVh(mHeadView);
            headVh.setIsRecyclable(false);
            return headVh;
        }
        return new Vh(mInflater.inflate(R.layout.item_main_home_recommend, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof Vh){
            ((Vh) vh).setData(mList.get(position - 1), position);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    class HeadVh extends RecyclerView.ViewHolder{

        public HeadVh(@NonNull View itemView) {
            super(itemView);
        }
    }

    public View getHeadView(){
        return mHeadView;
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView mAvatar;
        TextView mName;
        View mSexGroup;
        ImageView mSex;
        TextView mAge;
        TextView tv_order_num;
        TextView tv_star_level;
        TextView tv_des;
        TextView tv_price;
        TextView tv_unit;
        TextView tv_label;
        View rl_voice_length;
        TextView tv_voice_length;
        ImageView iv_anchor_level;
        ImageView iv_level;
        View mViewOrder;


        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mSexGroup = itemView.findViewById(R.id.sex_group);
            mSex = itemView.findViewById(R.id.sex);
            mAge = itemView.findViewById(R.id.age);
            tv_order_num = itemView.findViewById(R.id.tv_order_num);
            tv_star_level = itemView.findViewById(R.id.tv_star_level);
            tv_des = itemView.findViewById(R.id.tv_des);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_unit = itemView.findViewById(R.id.tv_unit);
            tv_label = itemView.findViewById(R.id.tv_label);
            iv_level = itemView.findViewById(R.id.iv_level);
            iv_anchor_level = itemView.findViewById(R.id.iv_anchor_level);
            rl_voice_length = itemView.findViewById(R.id.rl_voice_length);
            tv_voice_length = itemView.findViewById(R.id.tv_voice_length);
            mViewOrder = itemView.findViewById(R.id.view_order);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(DynamicUserBean bean, int position) {
            itemView.setTag(position);
            if (CommonAppConfig.getInstance().getIsState()==1){
                mViewOrder.setVisibility(View.INVISIBLE);
            }
            ImgLoader.displayAvatar(mContext, bean.getAvatar(), mAvatar);
            mName.setText(bean.getUser_nickname());
            mSexGroup.setBackground(CommonIconUtil.getSexBgDrawable(Integer.valueOf(bean.getSex())));
            mSex.setImageDrawable(CommonIconUtil.getSexDrawable(Integer.valueOf(bean.getSex())));
            mAge.setText(bean.getAge());
            tv_star_level.setText(bean.getStar());
            tv_des.setText(bean.getSkill_des());
            tv_order_num.setText(WordUtil.getString(R.string.game_order_num)+"  "+bean.getOrders());
            SkillBean skillBean = bean.getSkillinfo();
            if (skillBean != null && !TextUtils.isEmpty(skillBean.getId())){
                tv_price.setText(skillBean.getSkillPrice());
                tv_unit.setText(mCoinName+"/"+skillBean.getUnit());
            }else {
                tv_price.setText("");
                tv_unit.setText("");
            }
            UserLabelInfoBean labelInfoBean = bean.getLabelinfo();
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
            if (!TextUtils.isEmpty(bean.getVoice_l()) && !"0".equals(bean.getVoice_l())){
                rl_voice_length.setVisibility(View.VISIBLE);
                tv_voice_length.setText(bean.getVoice_l() + "\"");
            }else {
                rl_voice_length.setVisibility(View.INVISIBLE);
            }
            if (bean.showAnchorLevel()){
                iv_anchor_level.setVisibility(View.VISIBLE);
                LevelBean anchorBean = CommonAppConfig.getInstance().getAnchorLevel(bean.getLevel_anchor());
                ImgLoader.display(mContext,anchorBean.getThumb(),iv_anchor_level);
            }else {
                if (iv_anchor_level.getVisibility() == View.VISIBLE){
                    iv_anchor_level.setVisibility(View.GONE);
                }
            }
            LevelBean levelBean = CommonAppConfig.getInstance().getLevel(bean.getLevel());
            ImgLoader.display(mContext,levelBean.getThumb(),iv_level);
        }
    }

}
