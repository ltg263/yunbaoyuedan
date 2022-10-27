package com.yunbao.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.utils.LanguageUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.CountryCodeBean;

/**
 * Created by Sky.L on 2020-12-21
 */
public class PhoneCountryCodeSearchAdapter extends RefreshAdapter<CountryCodeBean> {
    private View.OnClickListener mOnClickListener;

    public PhoneCountryCodeSearchAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null){
                     Object tag  = v.getTag();
                     if (tag != null){
                         int position = (int)tag;
                         CountryCodeBean bean = mList.get(position);
                         if (bean != null){
                             mOnItemClickListener.onItemClick(bean,position);
                         }
                     }
                }
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_phone_country_code_search,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((Vh)viewHolder).setData(mList.get(i),i);
    }


    class Vh extends RecyclerView.ViewHolder{
        TextView tv_content;

        public Vh(@NonNull View itemView) {
            super(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(CountryCodeBean bean,int position){
            itemView.setTag(position);
            if (LanguageUtil.isEn()){
                tv_content.setText(bean.getNameEn());
            }else {
                tv_content.setText(bean.getName());
            }

        }
    }

}
