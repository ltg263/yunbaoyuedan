package com.yunbao.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.LanguageUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.CountryCodeBean;

import java.util.List;

/**
 * Created by Sky.L on 2020-12-21
 */
public class PhoneCountryCodeAdapter extends RecyclerView.Adapter<PhoneCountryCodeAdapter.Vh> {
    private Context mContext;
    private List<CountryCodeBean> mList;
    private LayoutInflater mInflater;
    private OnItemClickListener<CountryCodeBean> mOnItemClickListener;

    public PhoneCountryCodeAdapter(Context context, List<CountryCodeBean> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener<CountryCodeBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_phone_country_code,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int i) {
        vh.setData(mList.get(i),i);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class Vh extends RecyclerView.ViewHolder {
        TextView tv_content;
        CountryCodeBean mBean;
        int mPosition;

        public Vh(@NonNull View itemView) {
            super(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mBean.isTitle()){
                        if (mOnItemClickListener != null){
                            mOnItemClickListener.onItemClick(mBean,mPosition);
                        }
                    }
                }
            });
        }

        void setData(CountryCodeBean bean,int position){
            itemView.setTag(bean);
            mBean = bean;
            mPosition = position;
            if (LanguageUtil.isEn()){
                tv_content.setText(bean.getNameEn());
            }else {
                tv_content.setText(bean.getName());
            }
        }
    }
}
