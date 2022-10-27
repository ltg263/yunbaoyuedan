package com.yunbao.main.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.LanguageUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.utils.ImDateUtil;
import com.yunbao.main.R;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.common.bean.SkillBean;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by cxf on 2019/8/6.
 */

public class OrderMsgAdapter extends RefreshAdapter<OrderBean> {

    private static final int NORMAL = 1;
    private static final int WAIT = 2;
    private static final int DONE_COMMENT = 3;
    private String mTimeString;
    private String mNumString;
    private String mAccpetOrderString;
    private String mCoinName;
    private ActionListener mActionListener;
    private View.OnClickListener mItemClickListener;
    private View.OnClickListener mRefuseClickListener;
    private View.OnClickListener mGetOrderClickListener;
    private View.OnClickListener mCommentClickListener;
    private MyHandler mMyHandler;
    private boolean mIsEn;


    public OrderMsgAdapter(Context context) {
        super(context);
        mTimeString = WordUtil.getString(R.string.time);
        mNumString = WordUtil.getString(R.string.count);
        mAccpetOrderString = WordUtil.getString(R.string.get_order);
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mActionListener != null) {
                    mActionListener.onItemClick((OrderBean) tag);
                }
            }
        };
        mRefuseClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                OrderBean orderBean = mList.get(position);
                if (mMyHandler != null) {
                    mMyHandler.removeMessages(position);
                }
                if (orderBean != null && mActionListener != null) {
                    mActionListener.onRefuseClick(orderBean);
                }
            }
        };
        mGetOrderClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                OrderBean orderBean = mList.get(position);
                if (orderBean != null && mActionListener != null) {
                    mActionListener.onGetOrderClick(orderBean);
                }
            }
        };
        mCommentClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mActionListener != null) {
                    mActionListener.onCommentClick((OrderBean) tag);
                }
            }
        };
        mMyHandler = new MyHandler(this);
        mIsEn = LanguageUtil.isEn();
    }

    @Override
    public int getItemViewType(int position) {
        OrderBean orderBean = mList.get(position);
        int status = orderBean.getStatus();
        if (status == OrderBean.STATUS_DOING || status == OrderBean.STATUS_CANCEL || status == OrderBean.STATUS_REJECT) {
            return NORMAL;
        } else if (status == OrderBean.STATUS_WAIT) {
            if (orderBean.getLastWaitTime() <= 0) {
                orderBean.setStatus(OrderBean.STATUS_TIMEOUT);
                return NORMAL;
            } else {
                if (orderBean.isMyAnchor()) {
                    if (mMyHandler != null) {
                        mMyHandler.sendEmptyMessageDelayed(position, 1000);
                    }
                }
            }
            return WAIT;
        } else if (status == OrderBean.STATUS_DONE) {
            if (orderBean.isMyAnchor() && !orderBean.hasEvaluate() || !orderBean.isMyAnchor() && !orderBean.hasComment()) {
                return DONE_COMMENT;
            }
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == WAIT) {
            return new VhWait(mInflater.inflate(R.layout.item_order_msg_wait, parent, false));
        } else if (viewType == DONE_COMMENT) {
            return new VhDoneComment(mInflater.inflate(R.layout.item_order_msg_done_comment, parent, false));
        }
        return new VhNormal(mInflater.inflate(R.layout.item_order_msg_normal, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mTime;
        TextView mTips;
        ImageView mSkillThumb;
        TextView mSkillName;
        TextView mServiceTime;

        public Vh(View itemView) {
            super(itemView);
            mTime = itemView.findViewById(R.id.time);
            mTips = itemView.findViewById(R.id.tips);
            mSkillThumb = itemView.findViewById(R.id.skill_thumb);
            mSkillName = itemView.findViewById(R.id.skill_name);
            mServiceTime = itemView.findViewById(R.id.service_time);
            itemView.setOnClickListener(mItemClickListener);
        }

        void setData(OrderBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(bean);
                String timeString = bean.getAddTimeString();
                if (TextUtils.isEmpty(timeString)) {
                    timeString = ImDateUtil.getTimestampString(bean.getAddTime() * 1000);
                    bean.setAddTimeString(timeString);
                }
                mTime.setText(timeString);
                mTips.setText(mIsEn ? bean.getTipsEn() : bean.getTips());
                SkillBean skillBean = bean.getSkillBean();
                if (skillBean != null) {
                    ImgLoader.display(mContext, skillBean.getSkillThumb(), mSkillThumb);
                    mSkillName.setText(skillBean.getSkillName());
                    mServiceTime.setText(StringUtil.contact(bean.getServiceTime(), " ", bean.getOrderNum(), "*", skillBean.getUnit()));
                }
            }
        }
    }

    class VhNormal extends Vh {

        ImageView mAvatar;
        TextView mStatus;

        public VhNormal(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mStatus = itemView.findViewById(R.id.status);
        }

        @Override
        void setData(OrderBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                UserBean u = bean.getLiveUserInfo();
                if (u != null) {
                    ImgLoader.display(mContext, u.getAvatar(), mAvatar);
                }
                mStatus.setText(bean.getStatusString());
            }
        }
    }


    class VhDoneComment extends Vh {

        ImageView mAvatar;
        View mBtnComment;
        TextView mTvStatus;

        public VhDoneComment(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mBtnComment = itemView.findViewById(R.id.btn_comment);
            mBtnComment.setOnClickListener(mCommentClickListener);
            mTvStatus = itemView.findViewById(R.id.status);
        }

        @Override
        void setData(OrderBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                mBtnComment.setTag(bean);
                if (bean.isMyAnchor()){
                    mBtnComment.setVisibility(View.INVISIBLE);
                    if (mTvStatus.getVisibility() != View.VISIBLE){
                        mTvStatus.setVisibility(View.VISIBLE);
                    }
                    if (mBtnComment.getVisibility() == View.VISIBLE){
                        mBtnComment.setVisibility(View.INVISIBLE);
                    }
                    mTvStatus.setText(bean.getStatusString());
                }else {
                    if (mTvStatus.getVisibility() == View.VISIBLE){
                        mTvStatus.setText("");
                        mTvStatus.setVisibility(View.INVISIBLE);
                    }
                    mBtnComment.setVisibility(View.VISIBLE);
                }
                UserBean u = bean.getLiveUserInfo();
                if (u != null) {
                    ImgLoader.display(mContext, u.getAvatar(), mAvatar);
                }
            }
        }
    }


    class VhWait extends Vh {

        TextView mProfit;
        TextView mDes;
        View mBtnRefused;
        TextView mBtnOrder;

        public VhWait(View itemView) {
            super(itemView);
            mProfit = itemView.findViewById(R.id.profit);
            mDes = itemView.findViewById(R.id.des);
            mBtnRefused = itemView.findViewById(R.id.btn_refuse);
            mBtnOrder = itemView.findViewById(R.id.btn_order);
            mBtnRefused.setOnClickListener(mRefuseClickListener);
            mBtnOrder.setOnClickListener(mGetOrderClickListener);
        }

        @Override
        void setData(OrderBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                mBtnRefused.setTag(position);
                mBtnOrder.setTag(position);
                String timeString = bean.getAddTimeString();
                if (TextUtils.isEmpty(timeString)) {
                    timeString = ImDateUtil.getTimestampString(bean.getAddTime() * 1000);
                    bean.setAddTimeString(timeString);
                }
                mTime.setText(timeString);
                mTips.setText(mIsEn ? bean.getTipsEn() : bean.getTips());
                SkillBean skillBean = bean.getSkillBean();
                if (skillBean != null) {
                    ImgLoader.display(mContext, skillBean.getSkillThumb(), mSkillThumb);
                    mSkillName.setText(skillBean.getSkillName());
                    mServiceTime.setText(StringUtil.contact(mTimeString, ":", bean.getServiceTime(), "      ", mNumString, ":", bean.getOrderNum(), "*", skillBean.getUnit()));
                }
                mDes.setText(bean.getDes());
                mProfit.setText(StringUtil.contact(bean.getProfit(), mCoinName));
            }
            long lastWaitTime = bean.getLastWaitTime();
            mBtnOrder.setText(StringUtil.contact(mAccpetOrderString, "(", StringUtil.getDurationText(lastWaitTime), ")"));
        }
    }


    public void release() {
        if (mMyHandler != null) {
            mMyHandler.release();
        }
        mMyHandler = null;
        mActionListener = null;
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onItemClick(OrderBean orderBean);

        void onRefuseClick(OrderBean orderBean);

        void onGetOrderClick(OrderBean orderBean);

        void onCommentClick(OrderBean orderBean);
    }


    private void updateItem(int position) {
        if (position >= 0 && position < mList.size()) {
            OrderBean bean = mList.get(position);
            if (bean != null) {
                if (bean.getStatus() == OrderBean.STATUS_WAIT) {
                    long lastWaitTime = bean.getLastWaitTime();
                    if (lastWaitTime > 0) {
                        notifyItemChanged(position, Constants.PAYLOAD);
                        if (mMyHandler != null) {
                            mMyHandler.sendEmptyMessageDelayed(position, 1000);
                        }
                    } else {
                        notifyItemChanged(position);
                    }
                } else {
                    if (mMyHandler != null) {
                        mMyHandler.removeMessages(position);
                    }
                }
            }
        }
    }

    private static class MyHandler extends Handler {

        private OrderMsgAdapter mOrderMsgAdapter;

        public MyHandler(OrderMsgAdapter adapter) {
            mOrderMsgAdapter = new WeakReference<>(adapter).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mOrderMsgAdapter != null) {
                mOrderMsgAdapter.updateItem(msg.what);
            }
        }

        void release() {
            removeCallbacksAndMessages(null);
            mOrderMsgAdapter = null;
        }
    }
}
