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
import com.yunbao.common.bean.CoinPayBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/4/11.
 */

public class CoinPayAdapter extends RecyclerView.Adapter<CoinPayAdapter.Vh> {

    private Context mContext;
    private List<CoinPayBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private int mCheckedPosition;
    private Drawable mCheckedDrawable;
    private Drawable mUnCheckedDrawable;
    private OnItemClickListener<CoinPayBean> mOnItemClickListener;

    public CoinPayAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList = new ArrayList<>();
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                if (mCheckedPosition != position) {
                    if (mCheckedPosition >= 0 && mCheckedPosition < mList.size()) {
                        mList.get(mCheckedPosition).setChecked(false);
                        notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
                    }
                    mList.get(position).setChecked(true);
                    notifyItemChanged(position, Constants.PAYLOAD);
                    mCheckedPosition = position;
                    if (mOnItemClickListener != null){
                        mOnItemClickListener.onItemClick(mList.get(position),position);
                    }
                }
            }
        };
        mCheckedDrawable = ContextCompat.getDrawable(context, R.drawable.bg_coin_item_1);
        mUnCheckedDrawable = ContextCompat.getDrawable(context, R.drawable.bg_coin_item_0);
    }

    public void setOnItemClickListener(OnItemClickListener<CoinPayBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setList(List<CoinPayBean> list) {
        if (list != null && list.size() > 0) {
            mList.clear();
            if (mCheckedPosition >= 0 && mCheckedPosition < list.size()) {
                list.get(mCheckedPosition).setChecked(true);
            }
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_coin_pay, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {

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

        TextView mName;
        ImageView mThumb;
        View mBg;

        public Vh(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mThumb = itemView.findViewById(R.id.thumb);
            mBg = itemView.findViewById(R.id.bg);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(CoinPayBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                mName.setText(bean.getName());
                ImgLoader.display(mContext, bean.getThumb(), mThumb);
            }
            mBg.setBackground(bean.isChecked() ? mCheckedDrawable : mUnCheckedDrawable);
        }
    }


    public String getPayType() {
        if (mList != null && mList.size() > 0) {
            if (mCheckedPosition >= 0 && mCheckedPosition < mList.size()) {
                CoinPayBean bean = mList.get(mCheckedPosition);
                if (bean != null) {
                    return bean.getId();
                }
            }
        }
        return null;
    }
}
