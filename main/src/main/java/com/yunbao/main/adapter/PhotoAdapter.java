package com.yunbao.main.adapter;


import android.graphics.Color;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.PhotoBean;
import java.util.List;

public class PhotoAdapter extends BaseRecyclerAdapter<PhotoBean, BaseReclyViewHolder> {
    public static final int STAUTS_WAIT=0;
    public static final int STAUTS_SUCCESS=1;
    public static final int STAUTS_FAILED=-1;


    public PhotoAdapter(List<PhotoBean> data) {
        super(data);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_recly_photo;
    }
    @Override
    protected void convert(BaseReclyViewHolder helper, PhotoBean item) {
        helper.setImageUrl(item.getThumb(),R.id.image_thumb);
        int status=item.getStatus();
        if(status==STAUTS_WAIT){
          helper.setTextColor(R.id.tv_status, Color.WHITE);
          helper.setText(R.id.tv_status, WordUtil.getString(R.string.examing));
            helper.setVisible(R.id.tv_status,true);
        }else if(status==STAUTS_FAILED){
            helper.setTextColor(R.id.tv_status, Color.parseColor("#FF4E00"));
            helper.setText(R.id.tv_status, WordUtil.getString( R.string.un_cross));
            helper.setVisible(R.id.tv_status,true);
        }else{
            helper.setVisible(R.id.tv_status,false);
        }
    }

}
