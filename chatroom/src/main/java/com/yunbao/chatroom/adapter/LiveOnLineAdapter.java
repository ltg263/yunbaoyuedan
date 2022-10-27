package com.yunbao.chatroom.adapter;

import android.view.View;
import android.widget.ImageView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.LiveUserBean;
import java.util.List;

public class LiveOnLineAdapter extends BaseRecyclerAdapter<LiveUserBean, BaseReclyViewHolder> {
    private String mStringHost;
    private String mStringMike;


    public LiveOnLineAdapter(List<LiveUserBean> data) {
        super(data);
        mStringHost= WordUtil.getString(R.string.host);
        mStringMike= WordUtil.getString(R.string.on_mike);


    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_live_online;
    }
    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseReclyViewHolder helper, LiveUserBean item) {
        helper.setText(R.id.tv_serial_number,Integer.toString(helper.getLayoutPosition()+1));
        helper.setImageUrl(item.getAvatar(),R.id.img_avator);
        helper.setText(R.id.tv_name,item.getUserNiceName());
        ImageView iv_anchor_level = helper.getView(R.id.iv_anchor_level);
        int status=item.getStatus();
        if(status==1){
            helper.setText(R.id.tv_user_status,mStringMike);
            iv_anchor_level.setVisibility(View.GONE);
        }else if(status==2){
            helper.setText(R.id.tv_user_status,mStringHost);
            LevelBean anchorBean = CommonAppConfig.getInstance().getAnchorLevel(item.getAnchorLevel());
            helper.setImageUrl(anchorBean.getThumb(),R.id.iv_anchor_level);
            iv_anchor_level.setVisibility(View.VISIBLE);

        }else{
            helper.setText(R.id.tv_user_status,null);
            iv_anchor_level.setVisibility(View.GONE);
        }

        helper.setText(R.id.age,item.getAge());
        View sexGroup=helper.getView(R.id.sex_group);
        int sex= item.getSex();
        helper.setImageDrawable(R.id.sex, CommonIconUtil.getSexDrawable(sex));
        sexGroup.setBackground(CommonIconUtil.getSexBgDrawable(sex));
        LevelBean levelBean = CommonAppConfig.getInstance().getLevel(item.getLevel());
        helper.setImageUrl(levelBean.getThumb(),R.id.iv_level);
    }

}
