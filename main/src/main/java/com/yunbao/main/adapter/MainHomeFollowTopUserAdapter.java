package com.yunbao.main.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.dynamic.bean.DynamicUserBean;
import com.yunbao.dynamic.bean.UserLabelInfoBean;
import com.yunbao.main.R;

import java.util.List;

/**
 * Created by Sky.L on 2021-07-19
 */
public class MainHomeFollowTopUserAdapter extends RecyclerView.Adapter<MainHomeFollowTopUserAdapter.Vh> {

    private Context mContext;
    private List<DynamicUserBean> mList;
    private LayoutInflater mInflater;
    private String mCoinName;
    private OnItemClickListener<DynamicUserBean> mOnItemClickListener;



    public MainHomeFollowTopUserAdapter(Context context, List<DynamicUserBean> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
        mCoinName = CommonAppConfig.getInstance().getCoinName();
    }

    public void setOnItemClickListener(OnItemClickListener<DynamicUserBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setData(List<DynamicUserBean> list){
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_main_home_follow_top_user, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int i) {
        vh.setData(mList.get(i),i);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class Vh extends RecyclerView.ViewHolder {
        DynamicUserBean mBean;
        int mPosition;
        ImageView mIvAvatar;
        TextView mTvPrice;
        TextView mTvGameName;
        TextView mTvUserName;
        View ll_tips;
        TextView tv_label;


        public Vh(@NonNull View itemView) {
            super(itemView);
            mIvAvatar = itemView.findViewById(R.id.iv_avatar);
            mTvPrice = itemView.findViewById(R.id.tv_price);
            mTvGameName = itemView.findViewById(R.id.tv_game_name);
            mTvUserName = itemView.findViewById(R.id.tv_user_name);
            tv_label = itemView.findViewById(R.id.tv_label);
            ll_tips = itemView.findViewById(R.id.ll_tips);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null){
                        mOnItemClickListener.onItemClick(mBean,mPosition);
                    }
                }
            });
        }

        void setData(DynamicUserBean bean,int position){
            mBean = bean;
            mPosition = position;
            ImgLoader.display(mContext,bean.getAvatar(),mIvAvatar);
            SkillBean skillBean = bean.getSkillinfo();
            if (skillBean != null && !TextUtils.isEmpty(skillBean.getId())){
                mTvGameName.setText(skillBean.getSkillName());
                mTvPrice.setText(skillBean.getSkillPrice() + mCoinName+"/"+skillBean.getUnit());
                ll_tips.setVisibility(View.VISIBLE);
            }else {
                mTvGameName.setText("");
                mTvPrice.setText("");
                ll_tips.setVisibility(View.INVISIBLE);
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
            mTvUserName.setText(bean.getUser_nickname());
        }

    }
}
