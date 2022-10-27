package com.yunbao.dynamic.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.custom.ZoomView;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import java.util.List;

public class GalleryAdapter  extends BaseRecyclerAdapter<String, BaseReclyViewHolder> {
    private ImageView.ScaleType scaleType;
    private boolean isCanZoom=true;
    private Context mContext;

    public GalleryAdapter(List<String> data) {
        super(data);
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_recly_gallery;
    }
    @Override
    protected void convert(BaseReclyViewHolder helper, String item) {
        ZoomView imageView=helper.getView(R.id.zoom_view);
        imageView.setTransitionName(WordUtil.getString(R.string.transition_image)+helper.getLayoutPosition());
        imageView.setIsZoomEnabled(isCanZoom);
        if(scaleType!=null){
            imageView.setScaleType(scaleType);
        }
        helper.setImageUrl(item,R.id.zoom_view);
    }

    public boolean removeItem(int position) {
        if(mData==null||mData.size()==position){
            return false;
        }
        super.remove(position);
        return true;
    }

    public void setCanZoom(boolean canZoom) {
        isCanZoom = canZoom;
        if(!ListUtil.haveData(mData)){
           notifyReclyDataChange();
        }

    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
        if(!ListUtil.haveData(mData)){
            notifyReclyDataChange();
        }
    }
}
