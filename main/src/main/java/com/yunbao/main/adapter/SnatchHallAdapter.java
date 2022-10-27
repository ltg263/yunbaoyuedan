package com.yunbao.main.adapter;

import android.graphics.Color;
import android.widget.TextView;

import com.yunbao.common.CommonAppContext;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ViewUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.SnapOrderBean;

import java.util.List;

public class SnatchHallAdapter extends BaseRecyclerAdapter<SnapOrderBean, BaseReclyViewHolder> {
    private int mColorGlobal;
    private int mColorGray;

    public SnatchHallAdapter(List<SnapOrderBean> data) {
        super(data);
        mColorGlobal = CommonAppContext.sInstance.getResourceColor(R.color.global);
        mColorGray = CommonAppContext.sInstance.getResourceColor(R.color.gray1);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_snatch_hall;
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, SnapOrderBean item) {
        TextView tvDes = helper.getView(R.id.tv_des);
        ViewUtil.setTextNoContentGone(tvDes, item.getDes());
        helper.setText(R.id.tv_time, StringUtil.contact(WordUtil.getString(R.string.time)
                , ":\t"
                , item.getAppointmentTime()
                , "\t", item.getTotalUnit()
                )
        );
        UserBean userBean = item.getLiveUserInfo();
        if (userBean != null) {
            helper.setText(R.id.tv_user, WordUtil.getString(R.string.user) + ": " + userBean.getUserNiceName());
        }
        SkillBean skillBean = item.getSkillBean();
        if (skillBean != null) {
            helper.setImageUrl(skillBean.getSkillThumb(), R.id.img_skill_avator);
            helper.setText(R.id.tv_skill_name, skillBean.getSkillName());
            helper.setText(R.id.tv_coin, item.getTotalCoinWithUnit());
        }
        // isgrap： 状态：0：立即抢单；1：抢单中；2：抢单成功；-3：抢单失败; -1：已取消，-2:已超时, -4未抢单）
        if (item.getIsgrap() == 0) {
            helper.setVisible(R.id.btn_container, true);
            helper.setVisible(R.id.tv_state, false);
        } else if (item.getIsgrap() == 1) {
            helper.setVisible(R.id.btn_container, false);
            helper.setVisible(R.id.tv_state, true);
            helper.setText(R.id.tv_state, WordUtil.getString(R.string.snaping));
            helper.setTextColor(R.id.tv_state, mColorGlobal);
        } else if (item.getIsgrap() == 2) {
            helper.setVisible(R.id.btn_container, false);
            helper.setVisible(R.id.tv_state, true);
            helper.setText(R.id.tv_state, WordUtil.getString(R.string.get_order_success));
            helper.setTextColor(R.id.tv_state, mColorGray);
        } else if (item.getIsgrap() == -1) {
            helper.setVisible(R.id.btn_container, false);
            helper.setVisible(R.id.tv_state, true);
            helper.setText(R.id.tv_state, WordUtil.getString(R.string.order_status_cancel));
            helper.setTextColor(R.id.tv_state, mColorGray);
        } else if (item.getIsgrap() == -2) {
            helper.setVisible(R.id.btn_container, false);
            helper.setVisible(R.id.tv_state, true);
            helper.setText(R.id.tv_state, WordUtil.getString(R.string.order_status_time_out));
            helper.setTextColor(R.id.tv_state, mColorGray);
        } else if (item.getIsgrap() == -3) {
            helper.setVisible(R.id.btn_container, false);
            helper.setVisible(R.id.tv_state, true);
            helper.setText(R.id.tv_state, WordUtil.getString(R.string.get_order_failure));
            helper.setTextColor(R.id.tv_state, mColorGray);
        } else if (item.getIsgrap() == -4) {
            helper.setVisible(R.id.btn_container, false);
            helper.setVisible(R.id.tv_state, true);
            helper.setText(R.id.tv_state, WordUtil.getString(R.string.do_not_get_order));
            helper.setTextColor(R.id.tv_state, mColorGray);
        } else{
            helper.setVisible(R.id.btn_container, false);
            helper.setVisible(R.id.tv_state, false);
        }
        helper.addOnClickListener(R.id.btn_detail);
        helper.addOnClickListener(R.id.btn_confirm);
    }
}
