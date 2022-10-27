package com.yunbao.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.common.custom.DrawableTextView;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.LogoutConditionBean;

import java.util.List;

/**
 * Created by Sky.L on 2020-06-22
 */
public class LogoutConditionAdapter extends RecyclerView.Adapter<LogoutConditionAdapter.Vh> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<LogoutConditionBean> mList;
    private String mPassStr;
    private String mNoPassStr;
    private Drawable mPassDrawable;
    private Drawable mNoPassDrawable;
    private int mPassColor;
    private int mUnPassColor;

    public LogoutConditionAdapter(Context context, List<LogoutConditionBean> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
        mPassStr = WordUtil.getString(R.string.pass);
        mNoPassStr = WordUtil.getString(R.string.no_pass);
        mPassDrawable = mContext.getDrawable(R.drawable.draw_icon_logout_pass);
        mNoPassDrawable = mContext.getDrawable(R.drawable.draw_icon_logout_un_pass);
        mPassColor = mContext.getResources().getColor(R.color.gray1);
        mUnPassColor = mContext.getResources().getColor(R.color.global);
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_logout_condition,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int i) {
        ((Vh)vh).setData(mList.get(i));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder{
        private TextView mTvTitle;
        private DrawableTextView mTvStatus;
        private TextView mTvContent;


        public Vh(@NonNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvStatus = itemView.findViewById(R.id.tv_status);
            mTvContent = itemView.findViewById(R.id.tv_content);
        }

        void setData(LogoutConditionBean bean){
            mTvTitle.setText(bean.getTitle());
            mTvContent.setText(bean.getContent());
            if ("1".equals(bean.getIs_ok())){
                mTvStatus.setLeftDrawable(mPassDrawable);
                mTvStatus.setText(mPassStr);
                mTvStatus.setTextColor(mPassColor);
            }else {
                mTvStatus.setLeftDrawable(mNoPassDrawable);
                mTvStatus.setText(mNoPassStr);
                mTvStatus.setTextColor(mUnPassColor);
            }
        }
    }
}
