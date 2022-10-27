package com.yunbao.dynamic.adapter;

import android.widget.ImageView;

import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.VideoChooseBean;
import com.yunbao.dynamic.R;
import java.util.List;

public class SelectVideoAdapter  extends BaseRecyclerAdapter<VideoChooseBean, BaseReclyViewHolder> {
    public SelectVideoAdapter(List<VideoChooseBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_select_video;
    }
    @Override
    protected void convert(BaseReclyViewHolder helper, VideoChooseBean item) {
        ImgLoader.displayVideoThumb(mContext,item.getVideoPath(), (ImageView) helper.getView(R.id.img_cover));
        helper.setText(R.id.tv_duration,item.getDurationString());
    }

}
