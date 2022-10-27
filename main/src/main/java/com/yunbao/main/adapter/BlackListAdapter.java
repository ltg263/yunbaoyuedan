package com.yunbao.main.adapter;

import android.view.View;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.main.R;
import java.util.List;

public class BlackListAdapter extends BaseRecyclerAdapter<UserBean, BaseReclyViewHolder> {


    public BlackListAdapter(List<UserBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_black_list;
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    @Override
    protected void convert(BaseReclyViewHolder helper, UserBean item) {
        helper.setImageUrl(item.getAvatar(), R.id.img_avatar);
        helper.setText(R.id.tv_name,item.getUserNiceName());
        helper.setText(R.id.age,item.getAge());
        helper.setText(R.id.tv_hint,item.getSignature());
        View sexGroup=helper.getView(R.id.ll_sex_group);
        int sex= item.getSex();
        helper.setImageDrawable(R.id.sex, CommonIconUtil.getSexDrawable(sex));
        sexGroup.setBackground(CommonIconUtil.getSexBgDrawable(sex));
        helper.addOnClickListener(R.id.btn_remove);
    }


    public int contain(String toUid){
        int index=-1;
        if(mData==null){
         return index;
        }
        int size=mData.size();
        for(int i=0;i<size;i++){
            UserBean userBean=mData.get(i);
            String uid=userBean.getId();
            if(StringUtil.equals(uid,toUid)){
                index=i;
                break;
            }
        }
        return index;
    }
}
