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
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.main.R;
import com.yunbao.main.bean.SkillHomeBean;

/**
 * Created by cxf on 2019/7/22.
 */

public class UserHomeSkillAdapter extends RefreshAdapter<SkillHomeBean> {

    private View.OnClickListener mItemClickListener;
    private View.OnClickListener mOrderClickListener;
    private ActionListener mActionListener;
    private String mCoinName;
    private boolean mSelf;

    public UserHomeSkillAdapter(Context context, boolean self) {
        super(context);
        mSelf = self;
        mItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mActionListener != null) {
                    mActionListener.onItemClick((SkillHomeBean) tag);
                }
            }
        };
        mOrderClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mActionListener != null) {
                    mActionListener.onOrderClick((SkillHomeBean) tag);
                }
            }
        };
        mCoinName = CommonAppConfig.getInstance().getCoinName();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_user_home_skill, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position));
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mThumb;
        TextView mName;
        TextView mPrice;
        View mBtnOrder;

        public Vh(View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mName = itemView.findViewById(R.id.name);
            mPrice = itemView.findViewById(R.id.price);
            mBtnOrder = itemView.findViewById(R.id.btn_order);
            itemView.setOnClickListener(mItemClickListener);
            if (mSelf) {
                if (mBtnOrder.getVisibility() == View.VISIBLE) {
                    mBtnOrder.setVisibility(View.INVISIBLE);
                }
            } else {
                mBtnOrder.setOnClickListener(mOrderClickListener);
            }
        }

        void setData(SkillHomeBean bean) {
            itemView.setTag(bean);
            if (!mSelf) {
                mBtnOrder.setTag(bean);
            }
            ImgLoader.display(mContext, bean.getSkillThumb(), mThumb);
            mName.setText(bean.getSkillName());
            mPrice.setText(bean.getPirceResult(mCoinName));
        }
    }


    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {

        void onItemClick(SkillHomeBean bean);

        void onOrderClick(SkillHomeBean bean);
    }
}
