package com.yunbao.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.main.R;
import com.yunbao.main.bean.SkillUserBean;
import com.yunbao.main.custom.TagGroup2;

/**
 * Created by cxf on 2018/9/26.
 */

public class SkillUserAdapter extends RefreshAdapter<SkillUserBean> {

    private View.OnClickListener mOnClickListener;
    private String mCoinName;

    public SkillUserAdapter(Context context) {
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
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
        mCoinName = CommonAppConfig.getInstance().getCoinName();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_game, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position), position);
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        TextView mGameLevel;
        TextView mStarLevel;
        TextView mOrderNum;
        TextView mPrice;
        TextView tv_introduce;
        TagGroup2 mTags;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mGameLevel = itemView.findViewById(R.id.game_level);
            mStarLevel = itemView.findViewById(R.id.star_level);
            mOrderNum = itemView.findViewById(R.id.order_num);
            mPrice = itemView.findViewById(R.id.price);
            tv_introduce = itemView.findViewById(R.id.tv_introduce);
            mTags = itemView.findViewById(R.id.tags);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(SkillUserBean bean, int position) {
            itemView.setTag(position);
            mGameLevel.setText(bean.getSkillLevel());
            mStarLevel.setText(bean.getStarLevel());
            mOrderNum.setText(bean.getOrderNum());
            mPrice.setText(bean.getPirceResult(mCoinName));
            UserBean u = bean.getUserBean();
            if (u != null) {
                ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
                mName.setText(u.getUserNiceName());
                tv_introduce.setText(bean.getDes());
            }
            mTags.setTags(bean.getLabels());
        }

    }

}
