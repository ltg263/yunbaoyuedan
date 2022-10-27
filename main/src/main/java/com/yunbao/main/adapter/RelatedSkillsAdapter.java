package com.yunbao.main.adapter;

import android.text.TextUtils;

import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.main.bean.MySkillBean;

import java.util.List;

public class RelatedSkillsAdapter extends BaseRecyclerAdapter<MySkillBean, BaseReclyViewHolder> {

    public RelatedSkillsAdapter(List<MySkillBean> data) {
        super(data);
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_recly_single_textview;
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, MySkillBean item) {
        if(item.getSwitchX()==1 ||TextUtils.equals(MySkillBean.EMPTY_ID,item.getId())){
            helper.setText(R.id.text_view,item.getSkillname());
        }else{
            helper.setText(R.id.text_view,item.getSkillname()+WordUtil.getString(R.string.not_open));
        }
    }

    @Override
    public void setData(List<MySkillBean> data) {
        if(data!=null){
            MySkillBean skillBean=new MySkillBean();
            skillBean.setId(MySkillBean.EMPTY_ID);
            skillBean.setSkillname(WordUtil.getString(R.string.no_choose));
            data.add(0,skillBean);
        }
        super.setData(data);
    }
}
