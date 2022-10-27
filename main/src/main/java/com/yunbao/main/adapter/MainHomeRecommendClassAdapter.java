package com.yunbao.main.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.bean.DynamicUserBean;
import com.yunbao.dynamic.bean.UserLabelInfoBean;
import com.yunbao.main.R;
import com.yunbao.main.bean.ClassBean;

import java.util.List;

/**
 * 首页 游戏分类
 * @author apple
 */

public class MainHomeRecommendClassAdapter extends RefreshAdapter<ClassBean> {
    private View.OnClickListener mOnClickListener;
    private int width;

    public MainHomeRecommendClassAdapter(Context context, List<ClassBean> list) {
        super(context,list);
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
    }

    public void setData(List<ClassBean> list){
        mList = list;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        width= parent.getWidth();
        return new Vh(mInflater.inflate(R.layout.item_class, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof Vh){
            ((Vh) vh).setData(mList.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() ;
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView mAvatar;
        TextView mName;


        public Vh(View itemView) {
            super(itemView);
                ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
                layoutParams.width = width / 5;
                itemView.setLayoutParams(layoutParams);
            mAvatar = itemView.findViewById(R.id.thumb);
            mName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(ClassBean bean, int position) {
            itemView.setTag(position);
            ImgLoader.display(mContext, bean.getThumb(), mAvatar);
            mName.setText(bean.getName());

        }
    }

}
