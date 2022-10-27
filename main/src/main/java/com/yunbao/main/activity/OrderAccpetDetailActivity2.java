package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.activity.ChatRoomActivity;
import com.yunbao.main.R;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

/**
 * Created by cxf on 2019/8/5.
 */

public class OrderAccpetDetailActivity2 extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context, String orderId) {
        Intent intent = new Intent(context, OrderAccpetDetailActivity2.class);
        intent.putExtra(Constants.ORDER_ID, orderId);
        context.startActivity(intent);
    }

    private ImageView mAvatar;
    private TextView mName;
    private View mSexGroup;
    private ImageView mSex;
    private TextView mAge;
    private TextView mStar;
    private TextView mSkillName;
    private TextView mServiceTime;
    private TextView mDes;
    private TextView mPrice;
    private TextView mNum;
    private TextView mTotal;
    private TextView tv_handling_fee;
    private TextView mStatus;
    private String mCoinName;
    private String mOrderId;
    private OrderBean mOrderBean;

    private TextView tv_order_num;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_accpet_detail_2;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.order_get_detail));
        mOrderId = getIntent().getStringExtra(Constants.ORDER_ID);
        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        mSexGroup = findViewById(R.id.sex_group);
        mSex = findViewById(R.id.sex);
        mAge = findViewById(R.id.age);
        mStar = findViewById(R.id.star);
        mSkillName = findViewById(R.id.skill_name);
        tv_order_num = findViewById(R.id.tv_order_num);
        tv_order_num.setOnClickListener(this);
        mServiceTime = findViewById(R.id.service_time);
        mDes = findViewById(R.id.des);
        mPrice = findViewById(R.id.price);
        mNum = findViewById(R.id.num);
        mTotal = findViewById(R.id.total);
        tv_handling_fee = findViewById(R.id.tv_handling_fee);
        mStatus = findViewById(R.id.status);
        findViewById(R.id.btn_chat).setOnClickListener(this);
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        getData();
    }


    private void getData() {
        MainHttpUtil.getOrderDetail(mOrderId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    OrderBean orderBean = JSON.parseObject(info[0], OrderBean.class);
                    mOrderBean = orderBean;
                    UserBean u = orderBean.getLiveUserInfo();
                    if (u != null) {
                        if (mAvatar != null) {
                            ImgLoader.display(mContext, u.getAvatar(), mAvatar);
                        }
                        if (mName != null) {
                            mName.setText(u.getUserNiceName());
                        }
                        if (tv_order_num != null){
                            tv_order_num.setText(mOrderBean.getOrderNo());
                        }
                        if (mSexGroup != null) {
                            mSexGroup.setBackground(CommonIconUtil.getSexBgDrawable(u.getSex()));
                        }
                        if (mSex != null) {
                            mSex.setImageDrawable(CommonIconUtil.getSexDrawable(u.getSex()));
                        }
                        if (mAge != null) {
                            mAge.setText(u.getAge());
                        }
                        if (mStar != null) {
                            mStar.setText(u.getStar());
                        }
                    }
                    SkillBean skillBean = orderBean.getSkillBean();
                    if (skillBean != null) {
                        if (mSkillName != null) {
                            mSkillName.setText(skillBean.getSkillName());
                        }
                        if (mServiceTime != null) {
                            mServiceTime.setText(StringUtil.contact(orderBean.getServiceTime(), " ", orderBean.getOrderNum(), "*", skillBean.getUnit()));
                        }
                    }
                    if (mDes != null) {
                        mDes.setText(orderBean.getDes());
                    }
                    String total = StringUtil.contact(orderBean.getTotal(), mCoinName);
                    if (mPrice != null) {
                        OrderBean.AuthBean authBean = mOrderBean.getAuth();
                        if (authBean != null){
                            mPrice.setText(StringUtil.contact(authBean.getCoin(), mCoinName));
                        }

                    }
                    if (mTotal != null) {
                        mTotal.setText(StringUtil.contact(mOrderBean.getProfit(), mCoinName));
                    }
                    if (tv_handling_fee != null){
                        tv_handling_fee.setText(StringUtil.contact(WordUtil.getString(R.string.platform_handling_fee),
                                mOrderBean.getFee()
                                ,"\t\t\t",
                                WordUtil.getString(R.string.estimated_revenue)
                        ));
                    }
                    if (mNum != null) {
                        mNum.setText(StringUtil.contact("x", orderBean.getOrderNum()));
                    }
                    if (mStatus != null) {
                        mStatus.setText(orderBean.getStatusString());
                    }
                }else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_chat) {
            chatClick();
        }else if (i == R.id.tv_order_num){
            StringUtil.copyText(mContext,tv_order_num.getText().toString().trim());
        }
    }


    /**
     * 私信
     */
    private void chatClick() {
        if (mOrderBean == null) {
            return;
        }
        UserBean u = mOrderBean.getLiveUserInfo();
        if (u == null) {
            return;
        }
        ChatRoomActivity.forward(mContext, u, u.getIsFollow() == 1, false, true, false);
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_ORDER_DETAIL);
        super.onDestroy();
    }

}
