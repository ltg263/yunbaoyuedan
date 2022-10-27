package com.yunbao.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.main.R;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.main.bean.SkillPriceTipBean;

import java.util.List;

/**
 * Created by cxf on 2019/4/17.
 */

public class SkillPriceTipAdapter extends RecyclerView.Adapter<SkillPriceTipAdapter.Vh> {

    private List<SkillPriceTipBean> mList;
    private LayoutInflater mInflater;
    private String mPrefix;

    public SkillPriceTipAdapter(Context context, List<SkillPriceTipBean> list) {
        mInflater = LayoutInflater.from(context);
        mList = list;
        mPrefix = "≤ ";
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_skill_price_tip, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mOrderNum;
        TextView mPrice;

        public Vh(View itemView) {
            super(itemView);
            mOrderNum = itemView.findViewById(R.id.order_num);
            mPrice = itemView.findViewById(R.id.price);
        }

        void setData(SkillPriceTipBean bean) {
            mOrderNum.setText(bean.getOrderNum());
            mPrice.setText(StringUtil.contact(mPrefix, bean.getSkillPrice()));
        }
    }
}
