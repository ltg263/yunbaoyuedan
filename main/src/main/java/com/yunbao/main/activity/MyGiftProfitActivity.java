package com.yunbao.main.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.utils.MainIconUtil;

import java.text.DecimalFormat;

public class MyGiftProfitActivity extends AbsActivity implements View.OnClickListener {
    private DecimalFormat mDecimalFormat;

    private FrameLayout mTop;
    private TextView mTitleView;
    private ImageView mBtnBack;
    private TextView mBtnCashRecord;
    private TextView mVotes;
    private TextView mTvTag1;
    private EditText mEtPickNum;
    private View mLine;
    private TextView mTvTag2;
    private TextView mTvUnit;
    private TextView mTvGetMoney;
    private FrameLayout mBtnChooseAccount;
    private TextView mChooseTip;
    private LinearLayout mAccountGroup;
    private ImageView mAccountIcon;
    private TextView mAccount;
    private TextView mBtnCash;
    private double mMaxCanMoney;
    private String mAccountID;
    private String mCurrentGetMoney;
    private Double mCashRate;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_gift_profit;
    }

    @Override
    protected void main() {
        super.main();
        mTop = (FrameLayout) findViewById(R.id.top);
        mTitleView = (TextView) findViewById(R.id.titleView);
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnCashRecord = (TextView) findViewById(R.id.btn_cash_record);
        mVotes = (TextView) findViewById(R.id.votes);
        mTvTag1 = (TextView) findViewById(R.id.tv_tag_1);
        mEtPickNum = (EditText) findViewById(R.id.et_pick_num);
        mLine = (View) findViewById(R.id.line);
        mTvTag2 = (TextView) findViewById(R.id.tv_tag_2);
        mTvUnit = (TextView) findViewById(R.id.tv_unit);
        mTvGetMoney = (TextView) findViewById(R.id.tv_get_money);
        mBtnChooseAccount = (FrameLayout) findViewById(R.id.btn_choose_account);
        mChooseTip = (TextView) findViewById(R.id.choose_tip);
        mAccountGroup = (LinearLayout) findViewById(R.id.account_group);
        mAccountIcon = (ImageView) findViewById(R.id.account_icon);
        mAccount = (TextView) findViewById(R.id.account);
        mBtnCash = (TextView) findViewById(R.id.btn_cash);

        findViewById(R.id.btn_choose_account).setOnClickListener(this);
        findViewById(R.id.btn_cash_record).setOnClickListener(this);
        findViewById(R.id.btn_cash).setOnClickListener(this);
        ConfigBean configBean=CommonAppConfig.getInstance().getConfig();
        if(configBean!=null){
           String tip= getString(R.string.input_get_gift);
           mTvTag1.setText(tip);
        }
        mEtPickNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    double currentVote = Double.parseDouble(s.toString());
                    if (currentVote > mMaxCanMoney) {
                        currentVote = mMaxCanMoney;
                        int maxMoney = new Double(mMaxCanMoney).intValue();
                        s = String.valueOf(maxMoney);
                        mEtPickNum.setText(s);
                        mEtPickNum.setSelection(s.length());
                    }else{
                        double currentPrice=mCashRate*currentVote;
                        mCurrentGetMoney= getFormatPrice(currentPrice);
                        mTvGetMoney.setText(mCurrentGetMoney);
                    }
                    if (!TextUtils.isEmpty(mAccountID)) {
                        mBtnCash.setEnabled(true);
                    }
                } else {
                    mCurrentGetMoney= null;
                    mTvGetMoney.setText(mCurrentGetMoney);
                    mBtnCash.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loadData();
    }


    private void loadData() {
        MainHttpUtil.getGiftProfit(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    try {
                        JSONObject obj = JSON.parseObject(info[0]);
                        String votes = obj.getString("votes");
                        if (votes.contains(".")) {
                            votes = votes.substring(0, votes.indexOf('.'));
                        }
                        mCashRate=obj.getDouble("cash_rate");
                        mMaxCanMoney = Long.parseLong(votes);
                        if (mVotes != null) {
                            mVotes.setText(obj.getString("votes"));
                        }
                    } catch (Exception e) {
                        L.e("提现接口错误------>" + e.getClass() + "------>" + e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cash) {
            cash();
        } else if (i == R.id.btn_choose_account) {
            chooseAccount();
        } else if (i == R.id.btn_cash_record) {
            cashRecord();
        }
    }

    private void cash() {
        if (!ClickUtil.canClick()){
            return;
        }
        String votes = mEtPickNum.getText().toString().trim();
        if (TextUtils.isEmpty(votes)) {
            ToastUtil.show(R.string.profit_amount);
            return;
        }


        if (TextUtils.isEmpty(mAccountID)) {
            ToastUtil.show(R.string.profit_choose_account);
            return;
        }
        if (TextUtils.isEmpty(mCurrentGetMoney)) {
            ToastUtil.show(R.string.profit_amount_error);
            return;
        }

        MainHttpUtil.doCashGift(votes, mAccountID, mCurrentGetMoney,new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (mEtPickNum != null){
                    mEtPickNum.setText("");
                }
                ToastUtil.show(msg);
            }
        });
    }

    /**
     * 选择账户
     */

    private void chooseAccount() {
        Intent intent = new Intent(mContext, CashActivity.class);
        intent.putExtra(Constants.CASH_ACCOUNT_ID, mAccountID);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getAccount();
    }

    private void getAccount() {
        String[] values = SpUtil.getInstance().getMultiStringValue(Constants.CASH_ACCOUNT_ID, Constants.CASH_ACCOUNT, Constants.CASH_ACCOUNT_TYPE);
        if (values != null && values.length == 3) {
            String accountId = values[0];
            String account = values[1];
            String type = values[2];
            if (!TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(account) && !TextUtils.isEmpty(type)) {
                if (mChooseTip.getVisibility() == View.VISIBLE) {
                    mChooseTip.setVisibility(View.INVISIBLE);
                }
                if (mAccountGroup.getVisibility() != View.VISIBLE) {
                    mAccountGroup.setVisibility(View.VISIBLE);
                }
                mAccountID = accountId;
                mAccountIcon.setImageResource(MainIconUtil.getCashTypeIcon(Integer.parseInt(type)));
                mAccount.setText(account);
                if (mTvGetMoney != null && mBtnCash != null) {
                    String s = mTvGetMoney.getText().toString().trim();
                    if (!TextUtils.isEmpty(s)) {
                        mBtnCash.setEnabled(true);
                    }
                }
            } else {
                if (mAccountGroup.getVisibility() == View.VISIBLE) {
                    mAccountGroup.setVisibility(View.INVISIBLE);
                }
                if (mChooseTip.getVisibility() != View.VISIBLE) {
                    mChooseTip.setVisibility(View.VISIBLE);
                }
                mAccountID = null;
            }
        } else {
            if (mAccountGroup.getVisibility() == View.VISIBLE) {
                mAccountGroup.setVisibility(View.INVISIBLE);
            }
            if (mChooseTip.getVisibility() != View.VISIBLE) {
                mChooseTip.setVisibility(View.VISIBLE);
            }
            mAccountID = null;
        }
    }

    public  String getFormatPrice(double price) {
        if(price==0){
            return "0";
        }

        if(mDecimalFormat==null){
            mDecimalFormat = new DecimalFormat("#0.00");
        }
        String formatPrice=mDecimalFormat.format(price);
        return formatPrice;
    }


    private void cashRecord() {
        WebViewActivity.forward(mContext, HtmlConfig.CASH_GIFT_RECORD);
    }
}
