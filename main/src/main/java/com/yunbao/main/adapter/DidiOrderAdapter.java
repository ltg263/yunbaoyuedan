package com.yunbao.main.adapter;

import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.SnapOrderBean;
import java.util.List;

public class DidiOrderAdapter extends BaseRecyclerAdapter<SnapOrderBean, BaseReclyViewHolder> {
    public DidiOrderAdapter(List<SnapOrderBean> data) {
        super(data);
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_recly_di_di_order;
    }
    @Override
    protected void convert(BaseReclyViewHolder helper, SnapOrderBean item) {

        SkillBean skillBean=item.getSkillBean();
        if(skillBean!=null){
            helper.setImageUrl(skillBean.getSkillThumb(),R.id.img_skill_avator);
            helper.setText(R.id.tv_skill_name,skillBean.getSkillName());
         //   helper.setText(R.id.tv_time,WordUtil.getString(R.string.time)+": "+item.getAppointmentTime()+"\t"+item.getTotalUnit());
            helper.setText(R.id.tv_time,WordUtil.getString(R.string.time)+": "+item.getServiceTimeFormat()+"\t"+item.getTotalUnit());
        }

        int status=item.getStatus();
        if(status==SnapOrderBean.STATUS_GRAB_TICKET){
            helper.setVisible(R.id.btn_container,true);
            helper.setText(R.id.tv_state, WordUtil.getString(R.string.how_people_snap,item.getCount()));
        }else if(status==SnapOrderBean.STATUS_RECEIVED_ORDERS){
            helper.setVisible(R.id.btn_container,false);
            helper.setText(R.id.tv_state, WordUtil.getString(R.string.received_orders));

        }else if(status==SnapOrderBean.STATUS_CANCEL){
            helper.setText(R.id.tv_state, WordUtil.getString(R.string.order_status_cancel));
            helper.setVisible(R.id.btn_container,false);
        }else if(status==SnapOrderBean.STATUS_TIME_OUT){
            helper.setText(R.id.tv_state, WordUtil.getString(R.string.order_status_time_out));
            helper.setVisible(R.id.btn_container,false);
        }
        helper.addOnClickListener(R.id.btn_confirm);

    }
}
