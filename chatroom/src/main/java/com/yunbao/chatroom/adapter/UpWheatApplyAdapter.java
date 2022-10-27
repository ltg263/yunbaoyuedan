package com.yunbao.chatroom.adapter;

import android.view.View;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.chatroom.R;
import java.util.List;

public class UpWheatApplyAdapter extends BaseRecyclerAdapter<UserBean, BaseReclyViewHolder> {
    public UpWheatApplyAdapter(List<UserBean> data) {
        super(data);
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_recly_up_wheat_apply;
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseReclyViewHolder helper, UserBean item) {
        helper.setText(R.id.tv_serial_number,Integer.toString(helper.getLayoutPosition()+1));
        helper.setImageUrl(item.getAvatar(),R.id.img_avator);
        helper.setText(R.id.tv_name,item.getUserNiceName());
        helper.setText(R.id.age,item.getAge());
        View sexGroup=helper.getView(R.id.sex_group);
        int sex= item.getSex();
        helper.setImageDrawable(R.id.sex, CommonIconUtil.getSexDrawable(sex));
        sexGroup.setBackground(CommonIconUtil.getSexBgDrawable(sex));
        helper.addOnClickListener(R.id.btn_wheat_agree);
        helper.addOnClickListener(R.id.btn_wheat_refuse);
        LevelBean levelBean = CommonAppConfig.getInstance().getLevel(item.getLevel());
        helper.setImageUrl(levelBean.getThumb(),R.id.iv_level);
    }
}
