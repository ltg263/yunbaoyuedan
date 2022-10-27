package com.yunbao.main.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.BottomDealFragment;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.ViewUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.activity.ChatRoomActivity;
import com.yunbao.im.bean.OrderTipBean;
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
 * Created by cxf on 2019/8/5.
 */

public class OrderDetailActivity extends AbsActivity implements View.OnClickListener {
    private String mFrom;//跳转来源页面

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_detail;
    }

    public static void forward(Context context, String orderId) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(Constants.ORDER_ID, orderId);
        context.startActivity(intent);
    }

    public static void forwardWithFrom(Context context, String orderId,String from) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(Constants.ORDER_ID, orderId);
        intent.putExtra(Constants.FROM, from);
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
    private TextView mStatus;
    private View mTip;
    private View mBtnMore;
    private View mBtnDone;
    private String mCoinName;
    private String mOrderId;
    private OrderBean mOrderBean;
    private boolean mNeedRefresh;
    private boolean mPaused;
    private String mToUid;

    private TextView tv_order_num;

    @Override
    protected void main() {
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
        mStatus = findViewById(R.id.status);
        mTip = findViewById(R.id.tip);
        mBtnMore = findViewById(R.id.btn_more);
        mBtnMore.setOnClickListener(this);
        findViewById(R.id.btn_chat).setOnClickListener(this);
        findViewById(R.id.btn_user_group).setOnClickListener(this);
        mBtnDone = findViewById(R.id.btn_done);
        mBtnDone.setOnClickListener(this);
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
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
                        mToUid = u.getId();
                        if (mAvatar != null) {
                            ImgLoader.display(mContext, u.getAvatar(), mAvatar);
                        }
                        if (mName != null) {
                            mName.setText(u.getUserNiceName());
                        }
                        if (mSexGroup != null) {
                            mSexGroup.setBackground(CommonIconUtil.getSexBgDrawable(u.getSex()));
                        }
                        if (tv_order_num != null){
                            tv_order_num.setText(mOrderBean.getOrderNo());
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
                    String coin = StringUtil.contact("0", mCoinName);
                    OrderBean.AuthBean authBean = orderBean.getAuth();
                    if (authBean != null){
                        coin = StringUtil.contact(authBean.getCoin(), mCoinName);
                    }
                    if (mPrice != null) {
                        mPrice.setText(coin);
                    }
                    if (mTotal != null) {
                        mTotal.setText(total);
                    }
                    if (mNum != null) {
                        mNum.setText(StringUtil.contact("x", orderBean.getOrderNum()));
                    }
                    int status=orderBean.getStatus();
                    checkStatus(status);
                }
            }
        });
    }

    private void checkStatus(int status) {
        if (mStatus != null) {
            mStatus.setText(mOrderBean.getStatusString());
        }
        if (status == OrderBean.STATUS_WAIT) {
            if (mTip != null && mTip.getVisibility() != View.VISIBLE) {
                mTip.setVisibility(View.VISIBLE);
            }
            if (mBtnMore != null && mBtnMore.getVisibility() != View.VISIBLE) {
                mBtnMore.setVisibility(View.VISIBLE);
            }
        } else if(status==OrderBean.STATUS_DOING||status==OrderBean.STATUS_REFUSE_REFUND){
            ViewUtil.setVisibility(mBtnMore,View.VISIBLE);
            String orderUid=mOrderBean.getUid();
            String selfUid=CommonAppConfig.getInstance().getUid();
            if (status == OrderBean.STATUS_DOING && StringUtil.equals(orderUid,selfUid)&&mOrderBean.getShouldHide()==0) {
                mBtnDone.setVisibility(View.VISIBLE);
            }
        }else {
            if (mTip != null && mTip.getVisibility() == View.VISIBLE) {
                mTip.setVisibility(View.INVISIBLE);
            }
            if (mBtnMore.getVisibility() == View.VISIBLE) {
                mBtnMore.setVisibility(View.INVISIBLE);
            }
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_more) {
            moreClick();
        } else if (i == R.id.btn_chat) {
            chatClick();
        } else if (i == R.id.btn_done) {
            orderDone();
        } else if (i == R.id.btn_user_group) {
            if (!TextUtils.isEmpty(mToUid)) {
                RouteUtil.forwardUserHome(mToUid);
            }
        } else if (i == R.id.tv_order_num){
            StringUtil.copyText(mContext,tv_order_num.getText().toString().trim());
        }
    }

    /**
     * 完成订单
     */

    private void orderDone() {
        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.order_done_tip), true, new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                MainHttpUtil.orderDone(mOrderId, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (mOrderBean != null) {
                                EventBus.getDefault().post(new OrderChangedEvent(mOrderBean.getId()));
                                mOrderBean.setStatus(OrderBean.STATUS_DONE);
                                if (mStatus != null) {
                                    mStatus.setText(mOrderBean.getStatusString());
                                }
                                if (mBtnDone != null && mBtnDone.getVisibility() == View.VISIBLE) {
                                    mBtnDone.setVisibility(View.INVISIBLE);
                                }
                                OrderCommentActivity.forward(mContext, mOrderBean);
                            }
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }
        });
    }

    /**
     * 更多
     */

    private void moreClick() {
        if(mOrderBean==null|| !ClickUtil.canClick()){
            return;
        }

        BottomDealFragment bottomDealFragment=new BottomDealFragment();
        int status=mOrderBean.getStatus();
        if(status==OrderBean.STATUS_DOING){
            bottomDealFragment.setDialogButtonArray(new BottomDealFragment.DialogButton("申请退款", new BottomDealFragment.ClickListnter() {
                @Override
                public void click(View view) {
                 RefundApplyActivity.forward(OrderDetailActivity.this,mOrderBean);
                }
            }));
        }else if(status==OrderBean.STATUS_REFUSE_REFUND){
            bottomDealFragment.setDialogButtonArray(new BottomDealFragment.DialogButton("退款申诉", new BottomDealFragment.ClickListnter() {
                @Override
                public void click(View view) {
                  if(!ClickUtil.canClick()){
                      return;
                  }
                  MainHttpUtil.setRefundStatus(mOrderId, OrderBean.STATUS_WAIT_PLATFORM).
                          compose(OrderDetailActivity.this.<Boolean>bindToLifecycle())
                          .subscribe(new DefaultObserver<Boolean>() {
                              @Override
                              public void onNext(Boolean aBoolean) {
                                  if(aBoolean){
                                    EventBus.getDefault().post(new OrderChangedEvent(mOrderId));
                                    if(mOrderBean!=null){
                                      mOrderBean.setStatus(OrderBean.STATUS_WAIT_PLATFORM);
                                      checkStatus(mOrderBean.getStatus());
                                    }

                                  }
                              }
                          });
                }
            }));
        }else if(status==OrderBean.STATUS_WAIT){
            bottomDealFragment.setDialogButtonArray(new BottomDealFragment.DialogButton(getString(R.string.order_cancel), new BottomDealFragment.ClickListnter() {
                @Override
                public void click(View view) {
                    if (mOrderBean != null) {
                        OrderCancelActivity.forward(mContext, mOrderBean.getId());
                    }
                }
            }));
        }
        bottomDealFragment.show(getSupportFragmentManager());
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
        MainHttpUtil.cancel(MainHttpConsts.GET_ORDER_DETAIL);
        MainHttpUtil.cancel(MainHttpConsts.ORDER_DONE);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mPaused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused) {
            if (mNeedRefresh) {
                mNeedRefresh = false;
                getData();
            }
        }
        mPaused = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderChangedEvent(OrderChangedEvent e) {
        if (e == null) {
            return;
        }

        String orderId = e.getOrderId();
        if (!TextUtils.isEmpty(orderId) && orderId.equals(mOrderId)) {
            mNeedRefresh = true;
        }
    }

}
