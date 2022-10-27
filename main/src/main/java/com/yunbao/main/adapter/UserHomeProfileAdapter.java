package com.yunbao.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.main.R;

/**
 * Created by cxf on 2018/9/26.
 * 个人主页 资料
 */

public class UserHomeProfileAdapter extends RecyclerView.Adapter<UserHomeProfileAdapter.Vh> {

    private LayoutInflater mInflater;
    private String mIntroVal;
    private String mNameVal;
    private String mIDVal;
    private String mAgeVal;


    public UserHomeProfileAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_user_home_profile, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData();
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mIntro;
        TextView mName;
        TextView mID;
        TextView mAge;

        public Vh(View itemView) {
            super(itemView);
            mIntro = itemView.findViewById(R.id.intro);
            mName = itemView.findViewById(R.id.name);
            mID = itemView.findViewById(R.id.id_val);
            mAge = itemView.findViewById(R.id.age);
        }

        void setData() {
            mIntro.setText(mIntroVal);
            mName.setText(mNameVal);
            mID.setText(mIDVal);
            mAge.setText(mAgeVal);
        }
    }


    public void setData(String intro, String name, String id, String age) {
        mIntroVal = intro;
        mNameVal = name;
        mIDVal = id;
        mAgeVal = age;
        notifyItemChanged(0);
    }
}
