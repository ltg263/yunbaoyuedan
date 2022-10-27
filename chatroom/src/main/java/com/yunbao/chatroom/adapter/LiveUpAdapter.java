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

public class LiveUpAdapter extends BaseRecyclerAdapter<UserBean, BaseReclyViewHolder> {

    public LiveUpAdapter(List<UserBean> data) {
        super(data);
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_recly_live_up;
    }

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
        LevelBean levelBean = CommonAppConfig.getInstance().getLevel(item.getLevel());
        helper.setImageUrl(levelBean.getThumb(),R.id.iv_level);
    }



    public int containPositoin(UserBean liveUserBean){
        if(liveUserBean==null||mData==null){
            return -1;
        }
        return mData.indexOf(liveUserBean);
    }
}
