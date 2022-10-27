package com.yunbao.chatroom.adapter;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.chatroom.R;
import com.yunbao.common.bean.LiveAnthorBean;

import java.util.List;

public class WheatManangerAdapter extends BaseRecyclerAdapter<LiveAnthorBean, BaseReclyViewHolder> {
    private boolean mNotCanSwitchWheat;

    public WheatManangerAdapter(List<LiveAnthorBean> data) {
        super(data);
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_recly_wheat_controll_mannger;
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param liveAnthorBean   The item that needs to be displayed.
     */

    @Override
    protected void convert(BaseReclyViewHolder helper, LiveAnthorBean liveAnthorBean) {
        UserBean item=liveAnthorBean.getUserBean();
        View sexGroup=helper.getView(R.id.sex_group);
        ImageView imgSex=helper.getView(R.id.sex);
        ImageView imgAvatar=helper.getView(R.id.img_avator);
        helper.setText(R.id.tv_serial_number,Integer.toString(helper.getLayoutPosition()+1));
        if(item!=null){
            helper.setImageUrl(item.getAvatar(),R.id.img_avator);
            helper.setText(R.id.tv_name,item.getUserNiceName());
            helper.setText(R.id.age,item.getAge());
            int sex= item.getSex();
            helper.setImageDrawable(R.id.sex, CommonIconUtil.getSexDrawable(sex));
            sexGroup.setBackground(CommonIconUtil.getSexBgDrawable(sex));
            helper.setVisible(R.id.btn_wheat_control,true);
            helper.setVisible(R.id.btn_close_wheat,true);
            if(mNotCanSwitchWheat){
                helper.setVisible(R.id.btn_wheat_control,false);
            }else{
                helper.setVisible(R.id.btn_wheat_control,true);
            }
            LevelBean levelBean = CommonAppConfig.getInstance().getLevel(item.getLevel());
            helper.setImageUrl(levelBean.getThumb(),R.id.iv_level);
        }else/*空位状态处理*/{
            imgAvatar.setImageResource(R.mipmap.icon_live_avatar);
            sexGroup.setBackground(null);
            imgSex.setImageResource(0);
            helper.setText(R.id.age,null);
            helper.setText(R.id.tv_name,null);
            helper.setVisible(R.id.btn_wheat_control,false);
            helper.setVisible(R.id.btn_close_wheat,false);
        }
        if(liveAnthorBean.isOpenWheat()){
            helper.setText(R.id.btn_wheat_control,"闭麦");
        }else{
            helper.setText(R.id.btn_wheat_control,"开麦");
        }
        helper.setChecked(R.id.btn_wheat_control,liveAnthorBean.isOpenWheat());
        helper.setOnCheckedChangeListener(R.id.btn_wheat_control,mChecKListner);
        helper.addOnClickListener(R.id.btn_wheat_control);
        helper.addOnClickListener(R.id.btn_close_wheat);
    }

    private CompoundButton.OnCheckedChangeListener mChecKListner=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if(isChecked){
                buttonView.setText("闭麦");
            }else{
                buttonView.setText("开麦");
            }

        }
    };



    public void setNotCanSwitchWheat(boolean notCanSwitchWheat) {
        mNotCanSwitchWheat = notCanSwitchWheat;
        notifyReclyDataChange();
    }
}
