package com.yunbao.main.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.main.R;

import java.util.List;

/**
 * Created by Sky.L on 2021-07-19
 */
public class MainHomeRecommendHeadLiveAdapter extends RecyclerView.Adapter<MainHomeRecommendHeadLiveAdapter.Vh> {

    private Context mContext;
    private List<LiveBean> mList;
    private LayoutInflater mInflater;
    private OnItemClickListener<LiveBean> mOnItemClickListener;
    private int width;

    public MainHomeRecommendHeadLiveAdapter(Context context, List<LiveBean> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<LiveBean> list){
        mList = list;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<LiveBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        width= viewGroup.getWidth();
        return new Vh(mInflater.inflate(R.layout.item_main_home_recommend_head_live, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int i) {
        ((Vh) vh).setData(mList.get(i), i);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class Vh extends RecyclerView.ViewHolder {
        LiveBean mLiveBean;
        int mPosition;
        ImageView mIvThumb;
        TextView mTvTitle;
        TextView mTvUserName;
        RoundedImageView mAvatar;


        public Vh(@NonNull View itemView) {
            super(itemView);
            mIvThumb = itemView.findViewById(R.id.iv_thumb);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvUserName = itemView.findViewById(R.id.tv_user_name);
            mAvatar = itemView.findViewById(R.id.avatar);
            ViewGroup.LayoutParams layoutParams = mIvThumb.getLayoutParams();
            layoutParams.width = (width-30) / 2;
            layoutParams.height = (width-30) / 2;
            itemView.setLayoutParams(layoutParams);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mLiveBean, mPosition);
                    }
                }
            });
        }

        void setData(LiveBean bean, int position) {
            mLiveBean = bean;
            mPosition = position;
            ImgLoader.display(mContext,bean.getThumb(),mIvThumb);
            mTvTitle.setText(bean.getTitle());
            mTvUserName.setText(bean.getUserNiceName());
            ImgLoader.display(mContext,bean.getAvatar(),mAvatar);
        }
    }
}
