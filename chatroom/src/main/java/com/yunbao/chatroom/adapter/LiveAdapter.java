package com.yunbao.chatroom.adapter;

import android.view.View;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.common.Constants;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.LiveType;
import com.yunbao.common.glide.ImgLoader;

import java.util.List;

public class LiveAdapter extends BaseRecyclerAdapter<LiveBean, BaseReclyViewHolder> {


    public LiveAdapter(List<LiveBean> data) {
        super(data);
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_recly_live;
    }


    @Override
    protected void convert(BaseReclyViewHolder helper, LiveBean item) {
        helper.setImageUrl(item.getThumb(),R.id.img_avator);
        helper.setText(R.id.tv_live_num,Integer.toString(item.getNums()));
        helper.setText(R.id.tv_title,item.getTitle())
        .setText(R.id.tv_name,item.getUserNiceName());
        TextView tvLiveType=helper.getView(R.id.tv_live_type);
        View background=helper.getView(R.id.background);
        RoundedImageView img=helper.getView(R.id.avatar);
        View line=helper.getView(R.id.line);
        int type=item.getType();
        if(type!= Constants.LIVE_TYPE_DISPATCH){
            tvLiveType.setText(item.getTypeName());
        }else{
            tvLiveType.setText(null);
        }
        background.setBackground(LiveType.getTagBgDrawable(type));
        ImgLoader.displayAvatar(mContext,item.getAvatar(),img);
        if (mData.get(mData.size()-1).getUid().equals(item.getUid())){
            line.setVisibility(View.GONE);
        }
    }


}
