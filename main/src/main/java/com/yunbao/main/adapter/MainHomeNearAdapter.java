package com.yunbao.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.main.R;

/**
 * Created by cxf on 2018/9/26.
 */

public class MainHomeNearAdapter extends RefreshAdapter<UserBean> {

    private View.OnClickListener mOnClickListener;

    public MainHomeNearAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_main_home_near, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position), position);
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImageView mSex;
        TextView mAge;
        TextView mCity;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mSex = itemView.findViewById(R.id.sex);
            mAge = itemView.findViewById(R.id.age);
            mCity = itemView.findViewById(R.id.city);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(UserBean bean, int position) {
            itemView.setTag(position);

        }

    }

}
