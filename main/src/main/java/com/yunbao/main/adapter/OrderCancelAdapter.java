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
import com.yunbao.main.R;
import com.yunbao.main.bean.OrderCancelBean;

import java.util.List;

/**
 * Created by cxf on 2019/8/5.
 */

public class OrderCancelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mSubmitClickListener;
    private List<OrderCancelBean> mList;
    private LayoutInflater mInflater;
    private Drawable mCheckDrawable;
    private ActionListener mActionListener;

    private int mLastSelectedPosition = -1;//被选中的item


    public OrderCancelAdapter(Context context, List<OrderCancelBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                if (position != 0 && mList.get(0).isChecked()){
                    mLastSelectedPosition = 0;
                }
                mList.get(position).toggle();
                if (position != mLastSelectedPosition && mLastSelectedPosition != -1){
                    mList.get(mLastSelectedPosition).setChecked(false);
                    notifyItemChanged(mLastSelectedPosition, Constants.PAYLOAD);
                }
                mLastSelectedPosition = position;
                notifyItemChanged(position, Constants.PAYLOAD);
            }
        };
        mSubmitClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = null;
                for (OrderCancelBean bean : mList) {
                    if (bean.isChecked()) {
                        if (sb == null) {
                            sb = new StringBuilder();
                        }
                        sb.append(bean.getId());
                      //  sb.append(",");
                    }
                }
                if (mActionListener != null) {
                    mActionListener.onConfirmClick(sb != null ? sb.toString() : null);
                }
            }
        };
        mCheckDrawable = ContextCompat.getDrawable(context, R.mipmap.icon_pay_checked_1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mList.size()) {
            return -1;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == -1) {
            return new BottomVh(mInflater.inflate(R.layout.item_order_cancel_1, parent, false));
        }
        return new Vh(mInflater.inflate(R.layout.item_order_cancel, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List<Object> payloads) {
        if (vh instanceof Vh) {
            Object payload = payloads.size() > 0 ? payloads.get(0) : null;
            ((Vh) vh).setData(mList.get(position), position, payload);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    class BottomVh extends RecyclerView.ViewHolder {

        public BottomVh(View itemView) {
            super(itemView);
            itemView.setOnClickListener(mSubmitClickListener);
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mName;
        ImageView mImg;

        public Vh(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mImg = itemView.findViewById(R.id.img);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(OrderCancelBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                mName.setText(bean.getName());
            }
            mImg.setImageDrawable(bean.isChecked() ? mCheckDrawable : null);
        }
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onConfirmClick(String ids);
    }
}
