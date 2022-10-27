package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.event.OrderStatusEvent;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.activity.ChatRoomActivity;
import com.yunbao.main.R;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.event.OrderChangedEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by cxf on 2019/8/6.
 * 接单详情
 */

public class OrderAccpetDetailActivity extends AbsActivity implements View.OnClickListener {

    private boolean mNeedRefresh;

    public static void forward(Context context, OrderBean orderBean) {
        Intent intent = new Intent(context, OrderAccpetDetailActivity.class);
        intent.putExtra(Constants.ORDER_BEAN, orderBean);
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
    private TextView mFee;
    private TextView mTip;
    private TextView mBtnRefuse;
    private TextView mBtnOrder;
    private ViewGroup mVpApplyQuick;
    private TextView mBtnApplyImmediate;
    private TextView mBtnRefund;




    private String mCoinName;
    private OrderBean mOrderBean;
    private TextView tv_order_num;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_accpet_detail;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.order_get_detail));
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
        mOrderBean = getIntent().getParcelableExtra(Constants.ORDER_BEAN);
        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        mSexGroup = findViewById(R.id.sex_group);
        mSex = findViewById(R.id.sex);
        mAge = findViewById(R.id.age);
        mStar = findViewById(R.id.star);
        mSkillName = findViewById(R.id.skill_name);
        tv_order_num = findViewById(R.id.tv_order_num);
        mServiceTime = findViewById(R.id.service_time);
        tv_order_num.setOnClickListener(this);
        mDes = findViewById(R.id.des);
        mPrice = findViewById(R.id.price);
        mNum = findViewById(R.id.num);
        mTotal = findViewById(R.id.total);
        mFee = findViewById(R.id.fee);
        mTip = findViewById(R.id.tip);
        mBtnRefuse = (TextView) findViewById(R.id.btn_refuse);
        mBtnOrder = (TextView) findViewById(R.id.btn_order);
        mVpApplyQuick = (ViewGroup) findViewById(R.id.vp_apply_quick);
        mBtnApplyImmediate = (TextView) findViewById(R.id.btn_apply_immediate);
        mBtnApplyImmediate.setOnClickListener(this);
        mBtnRefund = (TextView) findViewById(R.id.btn_refund);
        mBtnRefund.setOnClickListener(this);
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        findViewById(R.id.btn_chat).setOnClickListener(this);
        findViewById(R.id.btn_refuse).setOnClickListener(this);
        findViewById(R.id.btn_order).setOnClickListener(this);
        getData();
    }

    private void getData() {
        if (mOrderBean == null) {
            return;
        }
        UserBean u = mOrderBean.getLiveUserInfo();
        if (u != null) {
            if (mAvatar != null) {
                ImgLoader.display(mContext, u.getAvatar(), mAvatar);
            }
            if (mName != null) {
                mName.setText(u.getUserNiceName());
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
        if (tv_order_num != null){
            tv_order_num.setText(mOrderBean.getOrderNo());
        }
        SkillBean skillBean = mOrderBean.getSkillBean();
        if (skillBean != null) {
            if (mSkillName != null) {
                mSkillName.setText(skillBean.getSkillName());
            }
            if (mServiceTime != null) {
                mServiceTime.setText(StringUtil.contact(mOrderBean.getServiceTime(), " ", mOrderBean.getOrderNum(), "*", skillBean.getUnit()));
            }
        }
        if (mDes != null) {
            mDes.setText(mOrderBean.getDes());
        }
        if (mPrice != null) {
            OrderBean.AuthBean authBean = mOrderBean.getAuth();
            if (authBean != null){
                mPrice.setText(StringUtil.contact(authBean.getCoin(), mCoinName));
            }

        }
        if (mTotal != null) {
            mTotal.setText(StringUtil.contact(mOrderBean.getProfit(), mCoinName));
        }
        if (mNum != null) {
            mNum.setText(StringUtil.contact("x", mOrderBean.getOrderNum()));
        }
        if (mFee != null) {
            mFee.setText(StringUtil.contact(WordUtil.getString(R.string.order_fee), ":", mOrderBean.getFee()));
        }

        setOrderStatus();
    }

    private void setOrderStatus() {
        mTip.setText(mOrderBean.getStatusString());
        if (mOrderBean.getStatus() == OrderBean.STATUS_DOING) {
            findViewById(R.id.bottom).setVisibility(View.INVISIBLE);
            setReceptButtonStatus();
        } else if (mOrderBean.getStatus() == OrderBean.STATUS_WAIT) {
            mTip.setText(StringUtil.contact(StringUtil.getDurationText4(mOrderBean.getLastWaitTime()), WordUtil.getString(R.string.order_accpet_tip)));
        }else if (mOrderBean.getStatus() == OrderBean.STATUS_WAIT_REFUND){
            findViewById(R.id.bottom).setVisibility(View.INVISIBLE);
            findViewById(R.id.vp_apply_quick).setVisibility(View.INVISIBLE);
            mBtnRefund.setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.bottom).setVisibility(View.INVISIBLE);
            findViewById(R.id.vp_apply_quick).setVisibility(View.INVISIBLE);
            mBtnRefund.setVisibility(View.INVISIBLE);
        }
    }


    private void setReceptButtonStatus() {
        int receptStautus=mOrderBean.getReceptStatus();
        if(receptStautus==OrderBean.STATUS_RECEPT_APPLYED){
            mVpApplyQuick.setVisibility(View.VISIBLE);
            mBtnApplyImmediate.setEnabled(false);
        }else if(receptStautus==OrderBean.STATUS_RECEPT_DEFAULT){
            mVpApplyQuick.setVisibility(View.VISIBLE);
            mBtnApplyImmediate.setEnabled(true);
        }else{
            mVpApplyQuick.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_refuse) {
            refuseClick();
        } else if (i == R.id.btn_order) {
            accpetClick();
        } else if (i == R.id.btn_chat) {
            chatClick();
        }else if(i == R.id.btn_apply_immediate){
            applyImmediate();
        }else if(i==R.id.btn_refund){
            refund();
        }else if (i == R.id.tv_order_num){
            StringUtil.copyText(mContext,tv_order_num.getText().toString().trim());
        }
    }


    private void refund() {
        if(mOrderBean==null){
            return;
        }
        RefunDealActivity.forward(this,mOrderBean.getId());
    }

    private void applyImmediate() {
        if(mOrderBean==null||!ClickUtil.canClick()){
            return;
        }
        CommonHttpUtil.updateReceptOrder(mOrderBean.getId()).
                compose(this.<Boolean>bindToLifecycle()).subscribe(new DefaultObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if(aBoolean){
                    mOrderBean.setReceptStatus(OrderBean.STATUS_RECEPT_APPLYED);
                    EventBus.getDefault().post(new OrderChangedEvent(mOrderBean.getId()));
                    setReceptButtonStatus();
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderStatusEvent(OrderChangedEvent orderStatusEvent){
        if(mOrderBean==null||orderStatusEvent==null){
            return;
        }
        int orderStatus=orderStatusEvent.getStatus();
        if(orderStatus==mOrderBean.getStatus()){
            return;
        }
        mOrderBean.setStatus(orderStatus);
        setOrderStatus();
    }

    /**
     * 拒绝
     */

    public void refuseClick() {
        if (mOrderBean == null) {
            return;
        }

        MainHttpUtil.orderRefuse(mOrderBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (mOrderBean != null) {
                        EventBus.getDefault().post(new OrderChangedEvent(mOrderBean.getId()));
                        finish();
                    }
                }
                ToastUtil.show(msg);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderChangedEvent(OrderChangedEvent e) {
         if(e==null||e.getStatus()==-10||mOrderBean==null){
             return;
         }
        mOrderBean.setStatus(e.getStatus());
        setOrderStatus();


    }

    /**
     * 接单
     */


    public void accpetClick() {
        if (mOrderBean == null) {
            return;
        }
        MainHttpUtil.orderAccpet(mOrderBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (mOrderBean != null) {
                        EventBus.getDefault().post(new OrderChangedEvent(mOrderBean.getId()));
                        finish();
                    }
                }
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        MainHttpUtil.cancel(MainHttpConsts.ORDER_REFUSE);
        MainHttpUtil.cancel(MainHttpConsts.ORDER_ACCPET);
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

}
