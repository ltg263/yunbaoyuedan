package com.yunbao.main.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.ViewUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.VisitBean;
import java.util.List;

public class VisitAdapter extends BaseRecyclerAdapter<VisitBean, BaseReclyViewHolder>{

    public VisitAdapter(List<VisitBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_visit;
    }
    @Override
    protected void convert(BaseReclyViewHolder helper, VisitBean item) {
        VisitBean.UserInfo userBean=item.getUserinfo();
        if(userBean!=null){
            helper.setText(R.id.tv_name,userBean.getUser_nickname());
            helper.setText(R.id.age,userBean.getAge()+"");
            Drawable drawable= CommonIconUtil.getSexDrawable(userBean.getSex());
            helper.setImageDrawable(R.id.sex,drawable);
            View view=helper.getView(R.id.ll_sex_group);
            view.setBackground(CommonIconUtil.getSexBgDrawable(userBean.getSex()));
            helper.setImageUrl(userBean.getAvatar(),R.id.img_avator);
            TextView tvVisitNum=helper.getView(R.id.tv_visit_num);
            ViewUtil.setTextNoContentHide(tvVisitNum, WordUtil.getString(R.string.visit_num,item.getNums()));
        }
        helper.setText(R.id.tv_time,item.getDatetime());
    }
}
