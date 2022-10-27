package com.yunbao.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.Constants;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.chatroom.business.LiveType;
import com.yunbao.main.R;

/**
 * Created by Sky.L on 2021-07-13
 */
public class ChatRoomMoreListAdapter extends RefreshAdapter<LiveBean> {
    private View.OnClickListener mOnClickListener;


    public ChatRoomMoreListAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveBean bean = (LiveBean) v.getTag();
                if (bean != null){
                    if (mOnItemClickListener != null){
                        mOnItemClickListener.onItemClick(bean,0);
                    }
                }
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_chat_room_more_list,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((Vh)viewHolder).setData(mList.get(i),i);
    }

    class Vh extends RecyclerView.ViewHolder{
        ImageView mIvThumb;
        ImageView mIvRoomType;
        TextView mTvNum;
        TextView mTvTitle;
        TextView mTvuserName;
        TextView mTvLiveType;

        public Vh(@NonNull View itemView) {
            super(itemView);
            mIvThumb = itemView.findViewById(R.id.iv_thumb);
            mIvRoomType = itemView.findViewById(R.id.iv_room_type);
            mTvNum = itemView.findViewById(R.id.tv_num);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvuserName = itemView.findViewById(R.id.tvuser_name);
            mTvLiveType = itemView.findViewById(R.id.tv_live_type);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveBean liveBean,int position){
            itemView.setTag(liveBean);
            ImgLoader.display(mContext,liveBean.getThumb(),mIvThumb);
            mTvNum.setText(String.valueOf(liveBean.getNums()));
            mTvTitle.setText(liveBean.getTitle());
            mTvuserName.setText(liveBean.getUserNiceName());
            int type=liveBean.getType();
            if(type!= Constants.LIVE_TYPE_DISPATCH){
                mTvLiveType.setText(liveBean.getTypeName());
            }else{
                mTvLiveType.setText(null);
            }
            mTvLiveType.setBackground(LiveType.getTagBgDrawable(type));
        }
    }

}
