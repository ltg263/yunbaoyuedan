package com.yunbao.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.main.R;

/**
 * Created by cxf on 2018/9/29.
 */

public class FollowAdapter extends RefreshAdapter<UserBean> {

    private View.OnClickListener mClickListener;

    public FollowAdapter(Context context) {
        super(context);
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick((UserBean) tag, 0);
                }
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_follow, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position));
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        View mSexGroup;
        ImageView mSex;
        TextView mAge;
        TextView mSign;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mSexGroup = itemView.findViewById(R.id.sex_group);
            mSex = itemView.findViewById(R.id.sex);
            mAge = itemView.findViewById(R.id.age);
            mSign = itemView.findViewById(R.id.sign);
            itemView.setOnClickListener(mClickListener);
        }

        void setData(UserBean bean) {
            itemView.setTag(bean);
            ImgLoader.display(mContext, bean.getAvatar(), mAvatar);
            mName.setText(bean.getUserNiceName());
            mSexGroup.setBackground(CommonIconUtil.getSexBgDrawable(bean.getSex()));
            mSex.setImageDrawable(CommonIconUtil.getSexDrawable(bean.getSex()));
            mAge.setText(bean.getAge());
            mSign.setText(bean.getSignature());
        }
    }


}
