package com.yunbao.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.common.Constants;
import com.yunbao.main.R;
import com.yunbao.main.bean.SkillLabelBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/7/26.
 */

public class SkillLabelAdapter extends RecyclerView.Adapter<SkillLabelAdapter.Vh> {

    private List<SkillLabelBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private Drawable mCheckDrawable;
    private Drawable mUnCheckDrawable;
    private int mCheckColor;
    private int mUnCheckColor;

    public SkillLabelAdapter(Context context, List<SkillLabelBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
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
                SkillLabelBean bean = mList.get(position);
                if (!bean.isChecked()) {
                    if (checkedCount >= 3) {
                        return;
                    }
                    bean.setChecked(true);
                } else {
                    bean.setChecked(false);
                }
                notifyItemChanged(position, Constants.PAYLOAD);
            }
        };
        mCheckDrawable = ContextCompat.getDrawable(context, R.drawable.bg_item_skill_label_1);
        mUnCheckDrawable = ContextCompat.getDrawable(context, R.drawable.bg_item_skill_label_0);
        mCheckColor = ContextCompat.getColor(context, R.color.global);
        mUnCheckColor = ContextCompat.getColor(context, R.color.gray1);
    }

    private int getCheckedCount() {
        if (mList == null || mList.size() == 0) {
            return -1;
        }
        int count = 0;
        for (SkillLabelBean bean : mList) {
            if (bean.isChecked()) {
                count++;
            }
        }
        return count;
    }

    public List<SkillLabelBean> getCheckedList() {
        if (mList == null || mList.size() == 0) {
            return null;
        }
        List<SkillLabelBean> list = null;
        for (SkillLabelBean bean : mList) {
            if (bean.isChecked()) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(bean);
            }
        }
        return list;
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_skill_label, parent, false));
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

        TextView mText;

        public Vh(View itemView) {
            super(itemView);
            mText = (TextView) itemView;
            mText.setOnClickListener(mOnClickListener);
        }

        void setData(SkillLabelBean bean, int position, Object payload) {
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
}
