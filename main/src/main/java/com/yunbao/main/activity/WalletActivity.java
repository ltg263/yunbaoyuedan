package com.yunbao.main.activity;

import android.view.View;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

/**
 * Created by cxf on 2019/4/10.
 */

public class WalletActivity extends AbsActivity implements View.OnClickListener {

    private TextView mBalance;
    private TextView mCoin;
    private TextView mBtnCharge;
    private TextView mIncome;
    private TextView mTvOrderTip;
    private TextView mProfit;
    private TextView mBtnCash;
    private TextView mIncomeGift;
    private TextView mTvGiftTip;
    private TextView mProfitGift;
    private TextView mBtnCashGift;

    private HttpCallback mHttpCallback;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.wallet));
        setRightTitle(WordUtil.getString(R.string.detail));
        TextView tv_right = findViewById(R.id.tv_right_title);
        tv_right.setTextColor(mContext.getResources().getColor(R.color.global));
        tv_right.setOnClickListener(this);
        mBalance = (TextView) findViewById(R.id.balance);
        mCoin = (TextView) findViewById(R.id.coin);
        mBtnCharge = (TextView) findViewById(R.id.btn_charge);
        mIncome = (TextView) findViewById(R.id.income);
        mTvOrderTip = (TextView) findViewById(R.id.tv_order_tip);
        mProfit = (TextView) findViewById(R.id.profit);
        mBtnCash = (TextView) findViewById(R.id.btn_cash);
        mIncomeGift = (TextView) findViewById(R.id.income_gift);
        mTvGiftTip = (TextView) findViewById(R.id.tv_gift_tip);
        mProfitGift = (TextView) findViewById(R.id.profit_gift);
        mBtnCashGift = (TextView) findViewById(R.id.btn_cash_gift);

        String coinName = CommonAppConfig.getInstance().getCoinName();
        mBalance.setText(StringUtil.contact(coinName, WordUtil.getString(R.string.wallet_2)));
        findViewById(R.id.btn_charge).setOnClickListener(this);
        mBtnCash.setOnClickListener(this);
        mBtnCashGift.setOnClickListener(this);

        String voteName = CommonAppConfig.getInstance().getVotesName();
        mTvGiftTip.setText(getString(R.string.wallet_10,voteName));
        mTvOrderTip.setText(getString(R.string.wallet_5,voteName));
        getData();
    }

    /**
     * 获取余额
     */

    private void getData() {
        if (mHttpCallback == null) {
            mHttpCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        if (mCoin != null) {
                            mCoin.setText(obj.getString("coin"));
                        }
                        if (mProfit != null) {
                            mProfit.setText(obj.getString("votes"));
                        }
                        if(mProfitGift!=null){
                           mProfitGift.setText(obj.getString("votes_gift"));
                        }
                    }
                }
            };
        }
        MainHttpUtil.getBaseInfo(mHttpCallback);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_charge) {
            RouteUtil.forwardMyCoin();
        } else if (i == R.id.btn_cash) {
            MyProfitActivity.forward(mContext);
        }else if(i==R.id.btn_cash_gift){
            startActivity(MyGiftProfitActivity.class);
        } else if (i == R.id.tv_right_title){
            WebViewActivity.forward(mContext, HtmlConfig.INCOME_AND_EXPENSES_DETAIL,true);
        }
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
        super.onDestroy();
    }

}
