package com.yunbao.chatroom.adapter;

import android.text.TextUtils;
import android.view.View;


import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.adapter.base.BaseMutiRecyclerAdapter;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.ListBean;
import java.util.List;

public class LiveBillboardAdapter extends BaseMutiRecyclerAdapter<ListBean, BaseReclyViewHolder> {
    private String unit;

    public LiveBillboardAdapter(List<ListBean> data) {
        super(data);
        addItemType(ListBean.TOP_TYPE,R.layout.item_relcy_live_top_billboard);
        addItemType(ListBean.NORMAL_TYPE,R.layout.item_recly_live_billboard);
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */

    @Override
    protected void convert(BaseReclyViewHolder helper, ListBean item) {
        int itemType=helper.getItemViewType();
        L.e("itemType=="+itemType);
        switch (helper.getItemViewType()){
            case ListBean.TOP_TYPE:
                convertTop(helper,item);
                break;
            case ListBean.NORMAL_TYPE:
                convertNormal(helper,item);
                break;
            default:
                break;
        }
    }

    @Override
    public void setData(List<ListBean> data) {
        setPosition(0,data);
        super.setData(data);
    }


    @Override
    public void appendData(List<ListBean> data) {
        setPosition(size(),data);
        super.appendData(data);
    }

    /*为每个实体类设置相应的序号，因为布局类型是根据序号确定*/
    private void setPosition(int start, List<ListBean> data){
        if(data!=null){
            int size=data.size();
            for(int i=0;i<size;i++){
                ListBean bean=data.get(i);
                bean.setPosition(start+i);
            }
        }
    }


    private void convertNormal(BaseReclyViewHolder helper, ListBean item) {
        int position=helper.getLayoutPosition();
        helper.setText(R.id.tv_serial_number,Integer.toString(position+1));
        helper.setImageUrl(item.getAvatarThumb(),R.id.img_avator);
        helper.setText(R.id.tv_name,item.getUserNiceName());
        helper.setText(R.id.tv_user_data,item.getTotalCoinFormat(unit));
        helper.setText(R.id.age,Integer.toString(item.getAge()));
        View sexGroup=helper.getView(R.id.sex_group);
        int sex= item.getSex();
        helper.setImageDrawable(R.id.sex, CommonIconUtil.getSexDrawable(sex));
        sexGroup.setBackground(CommonIconUtil.getSexBgDrawable(sex));
        if (WordUtil.getString(R.string.charm_value).equals(unit)){
            //主播榜
            LevelBean anchorBean = CommonAppConfig.getInstance().getAnchorLevel(item.getAnchorLevel());
            helper.setImageUrl(anchorBean.getThumb(),R.id.iv_level);
        }else {
            LevelBean levelBean = CommonAppConfig.getInstance().getLevel(item.getLevel());
            helper.setImageUrl(levelBean.getThumb(),R.id.iv_level);
        }
    }


    private void convertTop(BaseReclyViewHolder helper, ListBean item) {
       int positon= helper.getLayoutPosition();
       int frameResource=0;
       float scale=1.0F;
       switch (positon){
           case 0:
               frameResource=R.mipmap.icon_main_list_head_2;
               scale=0.9F;
           break;
           case 1:
               scale=1.00F;
               frameResource=R.mipmap.icon_main_list_head_1;
               break;
           case 2:
               scale=0.9F;
               frameResource=R.mipmap.icon_main_list_head_3;
               break;
           default:
               break;
        }


        View itemView=helper.getView(R.id.itemview);
        itemView.setScaleX(scale);
        itemView.setScaleY(scale);

        if(item.isEmpty()){
            helper.setText(R.id.tv_name, WordUtil.getString(R.string.wait_for_nothing));
            helper.setTextColor(R.id.tv_name, CommonAppContext.sInstance.getResourceColor(R.color.gray1));
            helper.setImageResouceId(R.mipmap.icon_main_list_no_data,R.id.img_avator);
            helper.setImageResouceId(0,R.id.img_frame);
            helper.setImageResouceId(0,R.id.iv_level);
            helper.setText(R.id.age,null);
            helper.setBackgroundRes(R.id.sex,0);
            helper.setText(R.id.tv_user_data,null);
            return;
        }
        helper.setText(R.id.tv_name,item.getUserNiceName());
        helper.setImageUrl(item.getAvatarThumb(),R.id.img_avator);
        helper.setImageResouceId(frameResource,R.id.img_frame);
        helper.setText(R.id.tv_user_data,item.getTotalCoinFormat(unit));
        helper.setText(R.id.age,Integer.toString(item.getAge()));
        if (WordUtil.getString(R.string.charm_value).equals(unit)){
            //主播榜
            LevelBean anchorBean = CommonAppConfig.getInstance().getAnchorLevel(item.getAnchorLevel());
            helper.setImageUrl(anchorBean.getThumb(),R.id.iv_level);
        }else {
            LevelBean levelBean = CommonAppConfig.getInstance().getLevel(item.getLevel());
            helper.setImageUrl(levelBean.getThumb(),R.id.iv_level);
        }
        View sexGroup=helper.getView(R.id.sex_group);
        int sex= item.getSex();
        helper.setImageDrawable(R.id.sex, CommonIconUtil.getSexDrawable(sex));
        sexGroup.setBackground(CommonIconUtil.getSexBgDrawable(sex));
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
