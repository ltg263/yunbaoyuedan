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
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.common.bean.OrderCommentBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.main.custom.StarCountView;

import java.util.List;

/**
 * Created by cxf on 2019/8/5.
 */

public class OrderCenterAdapter extends RefreshAdapter<OrderBean> {

    private View.OnClickListener mNextClickListener;
    private View.OnClickListener mItemClickListener;
    private String mPayString;
    private String mPayBackString;
    private String mCoinName;
    private ActionListener mActionListener;

    public OrderCenterAdapter(Context context) {
        super(context);
        mPayString = WordUtil.getString(R.string.order_status_pay);
        mPayBackString = WordUtil.getString(R.string.order_status_payback);
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mNextClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mActionListener != null) {
                    mActionListener.onNextClick((OrderBean) tag);
                }
            }
        };
        mItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mActionListener != null) {
                    mActionListener.onItemClick((OrderBean) tag);
                }
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        OrderBean orderBean = mList.get(position);
        int status = orderBean.getStatus();
        if (status ==  OrderBean.STATUS_WAIT
                || status == OrderBean.STATUS_DOING
                || status == OrderBean.STATUS_WAIT_REFUND
                || status == OrderBean.STATUS_WAIT_PLATFORM
                || status == OrderBean.STATUS_REFUSE_REFUND
        ) {
            return -1;
        }
        return 0;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == -1) {
            return new Vh(mInflater.inflate(R.layout.item_order_0, parent, false));
        }
        return new Vh2(mInflater.inflate(R.layout.item_order_1, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position), position);
    }

    @Override
    public void insertList(List<OrderBean> list) {
        super.insertList(list);
        notifyDataSetChanged();
    }

    class Vh extends RecyclerView.ViewHolder {

        View mTitle;
        TextView mStatus;
        ImageView mAvatar;
        ImageView mSkillThumb;
        TextView mSkillName;
        TextView mServiceTime;
        TextView mPayTip;
        TextView mTotal;
        View mBtnComplete;


        public Vh(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            mStatus = itemView.findViewById(R.id.status);
            mAvatar = itemView.findViewById(R.id.avatar);
            mSkillThumb = itemView.findViewById(R.id.skill_thumb);
            mSkillName = itemView.findViewById(R.id.skill_name);
            mServiceTime = itemView.findViewById(R.id.service_time);
            mPayTip = itemView.findViewById(R.id.pay_tip);
            mTotal = itemView.findViewById(R.id.total);
            mBtnComplete = itemView.findViewById(R.id.btn_complete);
            itemView.setOnClickListener(mItemClickListener);
        }

        void setData(OrderBean bean, int position) {
            itemView.setTag(bean);
            if (bean.isHasTitile()) {
                if (mTitle.getVisibility() != View.VISIBLE) {
                    mTitle.setVisibility(View.VISIBLE);
                }
            } else {
                if (mTitle.getVisibility() != View.GONE) {
                    mTitle.setVisibility(View.GONE);
                }
            }
            mStatus.setText(bean.getStatusString());
            UserBean u = bean.getLiveUserInfo();
            if (u != null) {
                ImgLoader.display(mContext, u.getAvatar(), mAvatar);
            }
            SkillBean skillBean = bean.getSkillBean();
            if (skillBean != null) {
                ImgLoader.display(mContext, skillBean.getSkillThumb(), mSkillThumb);
                mSkillName.setText(skillBean.getSkillName());
                mServiceTime.setText(StringUtil.contact(bean.getServiceTime(), " ", bean.getOrderNum(), "*", skillBean.getUnit()));
            }
            mPayTip.setText(bean.getStatus() == OrderBean.STATUS_CANCEL ? mPayBackString : mPayString);
            mTotal.setText(StringUtil.contact(bean.getTotal(), mCoinName));
            if (mBtnComplete != null && !bean.isMyAnchor()) {
                if (bean.getStatus() == OrderBean.STATUS_DOING && bean.getShouldHide()==0 ) {
                    if (mBtnComplete.getVisibility() != View.VISIBLE) {
                        mBtnComplete.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mBtnComplete.getVisibility() == View.VISIBLE) {
                        mBtnComplete.setVisibility(View.INVISIBLE);
                    }
                }
            }

        }
    }

    class Vh2 extends Vh {

        StarCountView mStar;
        View mBtnNext;
        View mLast;

        public Vh2(View itemView) {
            super(itemView);
            mStar = itemView.findViewById(R.id.star);
            mBtnNext = itemView.findViewById(R.id.btn_next);
            mLast = itemView.findViewById(R.id.last_tip);
            mBtnNext.setOnClickListener(mNextClickListener);
        }

        void setData(OrderBean bean, int position) {
            super.setData(bean, position);
            mBtnNext.setTag(bean);
            boolean showBtnNext = false;
            if (bean.getAuth() != null && "1".equals(bean.getAuth().getSkillSwitch())){
                showBtnNext = true;
            }
            if (showBtnNext){
                if (!bean.isMyAnchor()) {
                    if (mBtnNext.getVisibility() != View.VISIBLE) {
                        mBtnNext.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mBtnNext.getVisibility() == View.VISIBLE) {
                        mBtnNext.setVisibility(View.INVISIBLE);
                    }
                }
            }else {
                if (mBtnNext.getVisibility() == View.VISIBLE) {
                    mBtnNext.setVisibility(View.INVISIBLE);
                }
            }
            if (bean.getStatus() == OrderBean.STATUS_DONE) {
                if (mStar.getVisibility() != View.VISIBLE) {
                    mStar.setVisibility(View.VISIBLE);
                }
                OrderCommentBean commentBean = bean.getCommentBean();
                if (commentBean != null) {
                    mStar.setFillCount(commentBean.getStar());
                }
            } else {
                if (mStar.getVisibility() == View.VISIBLE) {
                    mStar.setVisibility(View.INVISIBLE);
                }
            }
            if (position == mList.size() - 1) {
                if (mLast.getVisibility() != View.VISIBLE) {
                    mLast.setVisibility(View.VISIBLE);
                }
            } else {
                if (mLast.getVisibility() != View.GONE) {
                    mLast.setVisibility(View.GONE);
                }
            }
        }
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onNextClick(OrderBean orderBean);

        void onItemClick(OrderBean orderBean);
    }

}
