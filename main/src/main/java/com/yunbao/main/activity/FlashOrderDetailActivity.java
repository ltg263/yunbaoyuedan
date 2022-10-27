package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.custom.ItemLinearLayout;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.SnapOrderBean;
import static com.yunbao.common.Constants.DATA;

public class FlashOrderDetailActivity extends AbsActivity {
    private ItemLinearLayout vpSkill;
    private ItemLinearLayout vpLevel;
    private ItemLinearLayout vpSex;
    private ItemLinearLayout vpTime;
    private ItemLinearLayout vpDes;
    private ItemLinearLayout vp_order_num;
    private TextView tvState;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_flash_order_detail;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.order_detail));
        vpSkill = (ItemLinearLayout) findViewById(R.id.vp_skill);
        vpLevel = (ItemLinearLayout) findViewById(R.id.vp_level);
        vpSex = (ItemLinearLayout) findViewById(R.id.vp_sex);
        vpTime = (ItemLinearLayout) findViewById(R.id.vp_time);
        vpDes = (ItemLinearLayout) findViewById(R.id.vp_des);
        vp_order_num = (ItemLinearLayout) findViewById(R.id.vp_order_num);
        tvState = (TextView) findViewById(R.id.tv_state);
        setData();
    }

    private void setData() {
        SnapOrderBean snapOrderBean=getIntent().getParcelableExtra(DATA);
        if(snapOrderBean==null){
            finish();
        }

        SkillBean skillBean=snapOrderBean.getSkillBean();
        if(skillBean!=null){
          vpSkill.setContent(skillBean.getSkillName());
          vpTime.setContent(snapOrderBean.getAppointmentTime()+"\t"+snapOrderBean.getTotalUnit());
        }

        vpLevel.setContent(snapOrderBean.getLevel());
        vpSex.setContent(snapOrderBean.parseSexCondition());
        vpDes.setContent(snapOrderBean.getDes());
        vp_order_num.setContent(snapOrderBean.getOrderNumber());
        int orderState=snapOrderBean.getStatus();

        if(orderState==SnapOrderBean.STATUS_CANCEL){
            tvState.setText(R.string.order_status_cancel);
        }else if(orderState==SnapOrderBean.STATUS_GRAB_TICKET){
            tvState.setText(R.string.order_status_tips);
        }else if(orderState==SnapOrderBean.STATUS_TIME_OUT){
            tvState.setText(R.string.order_status_time_out);
        }else if(orderState==SnapOrderBean.STATUS_RECEIVED_ORDERS){
            tvState.setText(R.string.received_orders);
        }
    }


    public static void forward(Context context, SnapOrderBean orderBean){
        Intent intent=new Intent(context,FlashOrderDetailActivity.class);
        intent.putExtra(DATA,orderBean);
        context.startActivity(intent);
    }

}
