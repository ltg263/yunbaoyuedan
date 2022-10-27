package com.yunbao.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.Constants;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.OrderPayBean;

import java.util.List;

/**
 * Created by cxf on 2019/7/4.
 */

public class OrderPayAdapter extends RecyclerView.Adapter<OrderPayAdapter.Vh> {

    private static final int COIN_TYPE = 1;
    private List<OrderPayBean> mList;
    private long mCoinValue;
    private String mCoinName;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private int mCheckedPosition;
    private Drawable mCheckedDrawable;
    private Drawable mUnCheckedDrawable;
    private Context mContext;
    private OnItemClickListener<OrderPayBean> mOnItemClickListener;

    public OrderPayAdapter(Context context, List<OrderPayBean> list, String coinName, long coinValue) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList = list;
        mCoinName = coinName;
        mCoinValue = coinValue;
        mCheckedDrawable = ContextCompat.getDrawable(context, R.mipmap.icon_pay_checked_1);
        mUnCheckedDrawable = ContextCompat.getDrawable(context, R.mipmap.icon_pay_checked_0);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList == null || mList.size() == 0) {
                    return;
                }
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                if (mCheckedPosition == position) {
                    return;
                }
                if (mCheckedPosition >= 0 && mCheckedPosition < mList.size()) {
                    mList.get(mCheckedPosition).setChecked(false);
                    notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
                }
                OrderPayBean bean = mList.get(position);
                bean.setChecked(true);
                notifyItemChanged(position, Constants.PAYLOAD);
                mCheckedPosition = position;
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(bean, 0);
                }
            }
        };
    }

    public void updateCoinValue(long coinValue) {
        mCoinValue = coinValue;
        for (int i = 0, size = mList.size(); i < size; i++) {
            if (Constants.PAY_TYPE_COIN.equals(mList.get(i).getId())) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener<OrderPayBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (Constants.PAY_TYPE_COIN.equals(mList.get(position).getId())) {
            return COIN_TYPE;
        }
        return 0;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == COIN_TYPE) {
            return new CoinVh(mInflater.inflate(R.layout.item_order_pay_coin, parent, false));
        }
        return new Vh(mInflater.inflate(R.layout.item_order_pay, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        TextView mName;
        ImageView mCheckImg;

        public Vh(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mName = itemView.findViewById(R.id.name);
            mCheckImg = itemView.findViewById(R.id.check_img);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(OrderPayBean bean, int position, Object payload) {
            itemView.setTag(position);
            if (payload == null) {
                ImgLoader.display(mContext, bean.getThumb(), mImg);
                mName.setText(bean.getName());
            }
            mCheckImg.setImageDrawable(bean.isChecked() ? mCheckedDrawable : mUnCheckedDrawable);
        }
    }

    class CoinVh extends Vh {

        TextView mCoin;

        public CoinVh(View itemView) {
            super(itemView);
            mCoin = itemView.findViewById(R.id.coin);
        }

        void setData(OrderPayBean bean, int position, Object payload) {
            if (payload == null) {
                ImgLoader.display(mContext, bean.getThumb(), mImg);
                mName.setText(bean.getName());
            }
            mCoin.setText(StringUtil.contact("(", String.valueOf(mCoinValue), mCoinName, ")"));
            if (mCoinValue > 0) {
                itemView.setTag(position);
                mCheckImg.setImageDrawable(bean.isChecked() ? mCheckedDrawable : mUnCheckedDrawable);
            } else {
                itemView.setTag(null);
                mCheckImg.setImageDrawable(null);
            }
        }
    }


}
