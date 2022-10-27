package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.activity.ChatRoomActivity;
import com.yunbao.main.R;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.main.bean.SnapOrderBean;
import com.yunbao.main.http.MainHttpUtil;
import static com.yunbao.common.Constants.DATA;

public class OrderTakingDetailActivity extends AbsActivity implements View.OnClickListener  {

    private int mStatus;
    private TextView tv_order_num;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_taking;
    }

    public static void forward(Context context, SnapOrderBean snapOrderBean) {
        Intent intent = new Intent(context, OrderTakingDetailActivity.class);
        intent.putExtra(DATA, snapOrderBean);
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
    private TextView mTvStatus;

    private TextView mTvFee;
    private View mTip;
    private View mBtnMore;
    private View mBtnDone;
    private String mCoinName;
    private SnapOrderBean mOrderBean;
    private boolean mNeedRefresh;
    private boolean mPaused;

    @Override
    protected void main() {
        mOrderBean=getIntent().getParcelableExtra(DATA);
        if(mOrderBean==null){
            finish();
        }
        mStatus=mOrderBean.getIsgrap();
        setTitle(WordUtil.getString(R.string.order_take_detail));
        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        tv_order_num = findViewById(R.id.tv_order_num);
        tv_order_num.setOnClickListener(this);
        mSexGroup = findViewById(R.id.sex_group);
        mTvFee=findViewById(R.id.tv_fee);
        mSex = findViewById(R.id.sex);
        mAge = findViewById(R.id.age);
        mStar = findViewById(R.id.star);
        mSkillName = findViewById(R.id.skill_name);
        mServiceTime = findViewById(R.id.service_time);
        mDes = findViewById(R.id.des);
        mPrice = findViewById(R.id.price);
        mNum = findViewById(R.id.num);
        mTotal = findViewById(R.id.total);
        mTvStatus = findViewById(R.id.status);
        findViewById(R.id.btn_chat).setOnClickListener(this);
        mBtnDone = findViewById(R.id.btn_done);
        mBtnDone.setOnClickListener(this);
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        setData();
    }

    private void setData() {
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
        SkillBean skillBean = mOrderBean.getSkillBean();
        if (skillBean != null) {
            if (mSkillName != null) {
                mSkillName.setText(skillBean.getSkillName());
            }
            if (mServiceTime != null) {
                mServiceTime.setText(StringUtil.contact(mOrderBean.getAppointmentTime(), " ", mOrderBean.getOrderNum(), "*", skillBean.getUnit()));
            }
        }
        if (mDes != null) {
            mDes.setText(mOrderBean.getDes());
        }
        if (tv_order_num != null){
            tv_order_num.setText(mOrderBean.getOrderNumber());
        }
//        String total = StringUtil.contact(mOrderBean.getTotal(), mCoinName);
        String conin = StringUtil.contact(mOrderBean.getCoin(), mCoinName);
        String profit = StringUtil.contact(mOrderBean.getProfit(), mCoinName);
        if (mPrice != null) {
            mPrice.setText(conin);
        }
        if (mTotal != null) {
            mTotal.setText(profit);
        }
        if (mNum != null) {
            mNum.setText(StringUtil.contact("x", mOrderBean.getOrderNum()));
        }

        if(mTvFee!=null){
            mTvFee.setText(StringUtil.contact(WordUtil.getString(R.string.platform_handling_fee),
                    mOrderBean.getFee()
                    ,"\t\t\t",
                    WordUtil.getString(R.string.estimated_revenue)
                    ));
        }

        if (mTvStatus != null) {
            mTvStatus.setText(setOrderStatusTips());
        }
        isSnapping();
    }

    private void isSnapping() {
        if (mOrderBean.getIsgrap()== 0 ){
            mBtnDone.setVisibility(View.VISIBLE);
        }else {
//            mTvStatus.setTextColor(getResources().getColor(R.color.global));
            mBtnDone.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
       if (i == R.id.btn_chat) {
            chatClick();
        } else if (i == R.id.btn_done) {
            orderDone();
        }else if (i == R.id.tv_order_num){
           StringUtil.copyText(mContext,tv_order_num.getText().toString().trim());
       }
    }

    /**
     * 立即抢单
     */

    private void orderDone() {
        if(mOrderBean!=null){
            MainHttpUtil.grapDrip(mOrderBean.getId()).compose(this.<Boolean>bindToLifecycle()).subscribe(new DefaultObserver<Boolean>() {
                @Override
                public void onNext(Boolean aBoolean) {
                    if(aBoolean){
                       finish();
                    }
                }
            });
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
                setData();
            }
        }
        mPaused = false;
    }


    // TODO: 2020-12-10 20201210  修改，使用grap字段 判断订单状态 
    public String getStatusString() {
        return null;
//        if(mStatus==SnapOrderBean.STATUS_CANCEL){
//            return WordUtil.getString(R.string.order_status_cancel);
//        }else if(mStatus==SnapOrderBean.STATUS_GRAB_TICKET){
//            if(mOrderBean.getIsgrap()==1){
//                return WordUtil.getString(R.string.snaping);
//            }else{
//                return WordUtil.getString(R.string.order_snap_status_tips);
//            }
//
//        }else if(mStatus==SnapOrderBean.STATUS_TIME_OUT){
//            return WordUtil.getString(R.string.order_status_time_out);
//        }else if(mStatus==SnapOrderBean.STATUS_RECEIVED_ORDERS){
//            return WordUtil.getString(R.string.received_orders);
//        }
//        return null;
    }

    public String setOrderStatusTips(){
        if (mStatus == 0) {
            return  WordUtil.getString(R.string.order_snap_status_tips);
        } else if (mStatus == 1) {
            return  WordUtil.getString(R.string.snaping);
        } else if (mStatus == 2) {
            return  WordUtil.getString(R.string.get_order_success);
        } else if (mStatus == -1) {
            return  WordUtil.getString(R.string.order_status_cancel);
        } else if (mStatus == -2) {
            return  WordUtil.getString(R.string.order_status_time_out);
        } else if (mStatus == -3) {
            return  WordUtil.getString(R.string.get_order_failure);
        } else if (mStatus == -4) {
            return  WordUtil.getString(R.string.do_not_get_order);
        } else{
            return  "";
        }
    }
    
}

