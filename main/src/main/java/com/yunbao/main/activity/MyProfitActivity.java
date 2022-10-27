package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.utils.MainIconUtil;

import java.text.DecimalFormat;

/**
 * Created by cxf on 2018/10/20.
 */

public class MyProfitActivity extends AbsActivity implements View.OnClickListener {


    public static void forward(Context context) {
        context.startActivity(new Intent(context, MyProfitActivity.class));
    }

    private TextView mVotes;
    private EditText mEdit;
    private View mChooseTip;
    private View mAccountGroup;
    private ImageView mAccountIcon;
    private TextView mAccount;
    private String mAccountID;
    private View mBtnCash;
    private Double mMaxCanMoney;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_profit;
    }

    @Override
    protected void main() {
        mVotes = (TextView) findViewById(R.id.votes);
        mEdit = findViewById(R.id.edit);
        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    double currentVote = Double.parseDouble(s.toString());
                    if (currentVote > mMaxCanMoney) {
                        currentVote = mMaxCanMoney;
                        int maxMoney =  new Double(mMaxCanMoney).intValue();
                        s = String.valueOf(maxMoney);
                        mEdit.setText(s);
                        mEdit.setSelection(s.length());
                    }else{

                    }
                    if (!TextUtils.isEmpty(mAccountID)) {
                        mBtnCash.setEnabled(true);
                    }
                } else {
                    mBtnCash.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBtnCash = findViewById(R.id.btn_cash);
        mBtnCash.setOnClickListener(this);
        findViewById(R.id.btn_choose_account).setOnClickListener(this);
        findViewById(R.id.btn_cash_record).setOnClickListener(this);
        mChooseTip = findViewById(R.id.choose_tip);
        mAccountGroup = findViewById(R.id.account_group);
        mAccountIcon = findViewById(R.id.account_icon);
        mAccount = findViewById(R.id.account);
        loadData();
    }






    private void loadData() {
        MainHttpUtil.getProfit(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    try {
                        JSONObject obj = JSON.parseObject(info[0]);
                        String votes = obj.getString("votes");
                        if (votes.contains(".")) {
                            votes = votes.substring(0, votes.indexOf('.'));
                        }
                        mMaxCanMoney = Double.parseDouble(votes);
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

    /**
     * 提现记录
     */
    private void cashRecord() {
        WebViewActivity.forward(mContext, HtmlConfig.CASH_RECORD);
    }

    /**
     * 提现
     */

    private void cash() {
        if (!ClickUtil.canClick()){
            return;
        }
        String votes = mEdit.getText().toString().trim();
        if (TextUtils.isEmpty(votes)) {
            ToastUtil.show(R.string.profit_amount);
            return;
        }
        if (TextUtils.isEmpty(mAccountID)) {
            ToastUtil.show(R.string.profit_choose_account);
            return;
        }
        MainHttpUtil.doCash(votes, mAccountID, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (mEdit != null){
                    mEdit.setText("");
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
                if (mEdit != null && mBtnCash != null) {
                    String s = mEdit.getText().toString().trim();
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

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.DO_CASH);
        MainHttpUtil.cancel(MainHttpConsts.GET_PROFIT);
        super.onDestroy();
    }
}
