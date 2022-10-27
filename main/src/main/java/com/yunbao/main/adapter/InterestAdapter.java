package com.yunbao.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.common.Constants;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.InterestBean;

import java.util.List;

/**
 * Created by cxf on 2019/7/24.
 */

public class InterestAdapter extends RefreshAdapter<InterestBean> {

    private View.OnClickListener mOnClickListener;
    private ActionListener mActionListener;
    private Drawable mCheckDrawable;
    private Drawable mUnCheckDrawable;
    private int mCheckColor;
    private int mUnCheckColor;

    public InterestAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int checkedCount = getCheckedCount();
                if (checkedCount < 0) {
                    return;
                }
                int position = (int) tag;
                InterestBean bean = mList.get(position);
                if (!bean.isChecked()) {
                    if (checkedCount >= 5) {
                        ToastUtil.show(R.string.edit_profile_interest_3);
                        return;
                    }
                    bean.setChecked(true);
                } else {
                    bean.setChecked(false);
                }
                notifyItemChanged(position, Constants.PAYLOAD);
                if (mActionListener != null) {
                    mActionListener.onItemSelected(getCheckedCount());
                }
            }
        };
        mCheckDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_item_interest_1);
        mUnCheckDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_item_interest_0);
        mCheckColor = ContextCompat.getColor(mContext, R.color.global);
        mUnCheckColor = ContextCompat.getColor(mContext, R.color.textColor);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public String getResult() {
        if (mList == null || mList.size() == 0) {
            return null;
        }
        StringBuilder sb = null;
        for (InterestBean bean : mList) {
            if (bean.isChecked()) {
                if (sb == null) {
                    sb = new StringBuilder();
                }
                sb.append(bean.getId());
                sb.append(",");
            }
        }
        if (sb != null) {
            String s = sb.toString().trim();
            if (!TextUtils.isEmpty(s) && s.endsWith(",")) {
                s = s.substring(0, s.length() - 1);
            }
            return s;
        }
        return null;
    }

    public int getCheckedCount() {
        if (mList == null || mList.size() == 0) {
            return -1;
        }
        int count = 0;
        for (InterestBean bean : mList) {
            if (bean.isChecked()) {
                count++;
            }
        }
        return count;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_interest, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mText;

        public Vh(View itemView) {
            super(itemView);
            mText = (TextView) itemView;
            mText.setOnClickListener(mOnClickListener);
        }

        void setData(InterestBean bean, int position, Object payload) {
            mText.setTag(position);
            if (payload == null) {
                mText.setText(bean.getName());
            }
            if (bean.isChecked()) {
                mText.setBackground(mCheckDrawable);
                mText.setTextColor(mCheckColor);
            } else {
                mText.setBackground(mUnCheckDrawable);
                mText.setTextColor(mUnCheckColor);
            }
        }
    }


    public interface ActionListener {
        void onItemSelected(int count);
    }
}
