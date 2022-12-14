package com.yunbao.main.dialog;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.CoinBean;
import com.yunbao.common.bean.CoinPayBean;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.pay.PayPresenter;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.activity.VipActivity;
import com.yunbao.main.adapter.VipBuyAdapter;
import com.yunbao.main.bean.VipBuyBean;

import java.util.List;

/**
 * Created by cxf on 2019/5/11.
 */

public class BuyVipDialogFragment extends AbsDialogFragment implements OnItemClickListener<VipBuyBean>, View.OnClickListener {

    private List<VipBuyBean> mBuyList;
    private List<CoinPayBean> mPayList;
    private RecyclerView mRecyclerView;
    private VipBuyAdapter mAdapter;
    private VipBuyBean mCheckedBuyBean;
    private TextView mCoin;
    private TextView mMoney;
    private String mCoinName;
    private PayPresenter mPayPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_vip_charge;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(270);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_charge).setOnClickListener(this);
        mCoin = (TextView) findViewById(R.id.coin);
        mMoney = (TextView) findViewById(R.id.money);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 50, 15);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);
        if (mBuyList != null && mBuyList.size() > 0) {
            mAdapter = new VipBuyAdapter(mContext, mBuyList);
            mAdapter.setOnItemClickListener(BuyVipDialogFragment.this);
            mRecyclerView.setAdapter(mAdapter);
        }
        showPrice();
    }


    public void setBuyList(List<VipBuyBean> buyList) {
        mBuyList = buyList;
        mCheckedBuyBean = mBuyList.get(0);
    }


    public void setCoinName(String coinName) {
        mCoinName = coinName;
    }

    public void setPayList(List<CoinPayBean> payList) {
        mPayList = payList;
    }

    private void showPrice() {
        if (mCheckedBuyBean == null) {
            return;
        }
        if (mCoin != null) {
            mCoin.setText(StringUtil.contact(mCheckedBuyBean.getCoin(), mCoinName, "/"));
        }
        if (mMoney != null) {
            mMoney.setText(StringUtil.contact("???", mCheckedBuyBean.getMoney()));
        }

    }


    @Override
    public void onItemClick(VipBuyBean bean, int position) {
        mCheckedBuyBean = bean;
        showPrice();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_close) {
            dismiss();
        } else if (i == R.id.btn_charge) {
            charge();
        }
    }


    private void charge() {
        if (mCheckedBuyBean == null || mPayList == null || mPayList.size() == 0) {
            return;
        }
//        BuyVipPayDialogFragment fragment = new BuyVipPayDialogFragment();
//        fragment.setVipBuyBean(mCheckedBuyBean);
//        fragment.setPayList(mPayList);
//        fragment.setCoinName(mCoinName);
//        fragment.setActionListener(this);
//        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "BuyVipPayDialogFragment");
    }


    public void onChargeClick(String payType) {
        if (mCheckedBuyBean == null) {
            return;
        }
        if (Constants.PAY_TYPE_COIN.equals(payType)) {
            if (mContext != null && mContext instanceof VipActivity) {
                ((VipActivity) mContext).buyVipWithCoin(mCheckedBuyBean.getId());
            }
        } else {
            if (mPayPresenter != null) {
                CoinBean bean = new CoinBean();
                bean.setMoney(mCheckedBuyBean.getMoney());
                bean.setCoin(mCheckedBuyBean.getCoin());
                String money = mCheckedBuyBean.getMoney();
                String goodsName = StringUtil.contact(mCheckedBuyBean.getName(), WordUtil.getString(R.string.vip));
                String orderParams = StringUtil.contact(
                        "&uid=", CommonAppConfig.getInstance().getUid(),
                        "&token=", CommonAppConfig.getInstance().getToken(),
                        "&money=", money,
                        "&vipid=", mCheckedBuyBean.getId());
                mPayPresenter.pay(payType, money, goodsName, orderParams);
            }
        }
        dismiss();
    }

    @Override
    public void onDestroy() {
        mPayPresenter = null;
        mContext =null;
        super.onDestroy();
    }

    public void setPayPresenter(PayPresenter payPresenter) {
        mPayPresenter = payPresenter;
    }
}
