package com.yunbao.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.Constants;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.FansUserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.main.R;

import java.util.List;

/**
 * Created by cxf on 2018/9/29.
 */

public class FansAdapter extends RefreshAdapter<FansUserBean> {

    private View.OnClickListener mClickListener;
    private View.OnClickListener mFollowClickListener;

    public FansAdapter(Context context) {
        super(context);
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick((FansUserBean) tag, 0);
                }
            }
        };
        mFollowClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                CommonHttpUtil.setAttention(((FansUserBean) tag).getId(), null);
            }
        };
    }

    public void setFollow(String toUid, int attention) {
        if (TextUtils.isEmpty(toUid) && mList == null || mList.size() == 0) {
            return;
        }
        int targetPos = -1;
        for (int i = 0, size = mList.size(); i < size; i++) {
            if (toUid.equals(mList.get(i).getId())) {
                targetPos = i;
                break;
            }
        }
        if (targetPos >= 0) {
            mList.get(targetPos).setAttention(attention);
            notifyItemChanged(targetPos, Constants.PAYLOAD);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_fans, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), payload);
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        View mSexGroup;
        ImageView mSex;
        TextView mAge;
        TextView mSign;
        View mBtnFollow;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mSexGroup = itemView.findViewById(R.id.sex_group);
            mSex = itemView.findViewById(R.id.sex);
            mAge = itemView.findViewById(R.id.age);
            mSign = itemView.findViewById(R.id.sign);
            mBtnFollow = itemView.findViewById(R.id.btn_follow);
            mBtnFollow.setOnClickListener(mFollowClickListener);
            itemView.setOnClickListener(mClickListener);
        }

        void setData(FansUserBean bean, Object payload) {
            if (payload == null) {
                itemView.setTag(bean);
                mBtnFollow.setTag(bean);
                ImgLoader.display(mContext, bean.getAvatar(), mAvatar);
                mName.setText(bean.getUserNiceName());
                mSexGroup.setBackground(CommonIconUtil.getSexBgDrawable(bean.getSex()));
                mSex.setImageDrawable(CommonIconUtil.getSexDrawable(bean.getSex()));
                mAge.setText(bean.getAge());
                mSign.setText(bean.getSignature());
            }
            if (bean.isAttent()) {
                if (mBtnFollow.getVisibility() == View.VISIBLE) {
                    mBtnFollow.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mBtnFollow.getVisibility() != View.VISIBLE) {
                    mBtnFollow.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}
