package com.yunbao.main.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.FootBean;
import com.yunbao.main.bean.VisitBean;

import java.util.List;

public class FootAdapter  extends BaseRecyclerAdapter<FootBean, BaseReclyViewHolder> {
   private String manOnther;
   private String womanOnther;

    public FootAdapter(List<FootBean> data) {
        super(data);
        manOnther=WordUtil.getString(R.string.man_onther);
        womanOnther=WordUtil.getString(R.string.woman_onther);
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_recly_foot;
    }
    @Override
    protected void convert(BaseReclyViewHolder helper, FootBean item) {
        VisitBean.UserInfo userBean=item.getUserinfo();
        if(userBean!=null){
           int sex= userBean.getSex();
            helper.setText(R.id.tv_name,userBean.getUser_nickname());
            helper.setText(R.id.age,userBean.getAge()+"");
            Drawable sexdrawable= CommonIconUtil.getSexDrawable(sex);
            View sexView=helper.getView(R.id.ll_sex_group);
            helper.setImageDrawable(R.id.sex,sexdrawable);
            sexView.setBackground(CommonIconUtil.getSexBgDrawable(userBean.getSex()));
            helper.setImageUrl(userBean.getAvatar(),R.id.img_avator);
            if(sex==1){
                helper.setText(R.id.tv_foot_tip, WordUtil.getString(R.string.foot_onther_page,manOnther));
            }else{
                helper.setText(R.id.tv_foot_tip, WordUtil.getString(R.string.foot_onther_page,womanOnther));
             }
        }
             helper.setText(R.id.tv_time,item.getDatetime());
    }
}
