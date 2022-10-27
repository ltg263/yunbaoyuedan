package com.yunbao.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.CoinBean;
import com.yunbao.common.bean.CoinPayBean;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.event.CoinChangeEvent;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.pay.PayCallback;
import com.yunbao.common.pay.PayPresenter;
import com.yunbao.common.pay.paypal.PaypalPayTask;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.CoinAdapter;
import com.yunbao.main.adapter.CoinPayAdapter;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by cxf on 2018/10/23.
 * 充值
 */
@Route(path = RouteUtil.PATH_COIN)
public class MyCoinActivity extends AbsActivity implements OnItemClickListener<CoinBean>, View.OnClickListener {

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView mPayRecyclerView;
    private CoinAdapter mAdapter;
    private CoinPayAdapter mPayAdapter;
    private TextView mBalance;
    private long mBalanceValue;
    private boolean mFirstLoad = true;
    private PayPresenter mPayPresenter;
    private String mCoinName;

    private boolean mPaypalIsSandbox;//paypal支付是否为沙盒模式


    @Override
    protected int getLayoutId() {
        return R.layout.activity_coin;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.charge));
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setColorSchemeResources(R.color.global);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 3;
                }
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 20);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);
        mAdapter = new CoinAdapter(mContext, mCoinName);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        findViewById(R.id.btn_tip).setOnClickListener(this);
        View headView = mAdapter.getHeadView();
        TextView coinNameTextView = headView.findViewById(R.id.coin_name);
        // coinNameTextView.setText(String.format(WordUtil.getString(R.string.wallet_coin_name), mCoinName));
        coinNameTextView.setText(WordUtil.getString(R.string.wallet_coin_name) + mCoinName);
        mBalance = headView.findViewById(R.id.coin);
        mPayRecyclerView = headView.findViewById(R.id.pay_recyclerView);
        ItemDecoration decoration2 = new ItemDecoration(mContext, 0x00000000, 14, 10);
        decoration2.setOnlySetItemOffsetsButNoDraw(true);
        mPayRecyclerView.addItemDecoration(decoration2);
        mPayRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        mPayAdapter = new CoinPayAdapter(mContext);
        mPayAdapter.setOnItemClickListener(new OnItemClickListener<CoinPayBean>() {
            @Override
            public void onItemClick(CoinPayBean bean, int position) {
                //选择了PayPal支付方式,充值货币显示美元
                if (mAdapter != null) {
                    mAdapter.updatePayType(bean.getId());
                }
            }
        });
        mPayRecyclerView.setAdapter(mPayAdapter);
        mPayPresenter = new PayPresenter(this);
        mPayPresenter.setServiceNameAli(Constants.PAY_BUY_COIN_ALI);
        mPayPresenter.setServiceNameWx(Constants.PAY_BUY_COIN_WX);
        mPayPresenter.setAliCallbackUrl(HtmlConfig.ALI_PAY_COIN_URL);
        mPayPresenter.setPayCallback(new PayCallback() {
            @Override
            public void onSuccess() {
                if (mPayPresenter != null) {
                    mPayPresenter.checkPayResult();
                }
            }

            @Override
            public void onFailed() {

            }
        });
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mFirstLoad) {
            mFirstLoad = false;
            loadData();
        }
    }

    private void loadData() {
        CommonHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    mBalanceValue = Long.parseLong(coin);
                    mBalance.setText(coin);
                    List<CoinPayBean> payList = JSON.parseArray(obj.getString("paylist"), CoinPayBean.class);
                    if (mPayAdapter != null) {
                        mPayAdapter.setList(payList);
                    }
                    List<CoinBean> list = JSON.parseArray(obj.getString("rules"), CoinBean.class);
                    if (mAdapter != null) {
                        mAdapter.setList(list);
                    }
                    mPayPresenter.setBalanceValue(mBalanceValue);
                    JSONObject ali = obj.getJSONObject("ali");
                    mPayPresenter.setAliPartner(ali.getString("partner"));
                    mPayPresenter.setAliSellerId(ali.getString("seller_id"));
                    mPayPresenter.setAliPrivateKey(ali.getString("key"));
                    JSONObject wxObj = obj.getJSONObject("wx");
                    mPayPresenter.setWxAppID(wxObj.getString("wx_appid"));
                }
            }

            @Override
            public void onFinish() {
                if (mRefreshLayout != null) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }


    @Override
    public void onItemClick(final CoinBean bean, int position) {
        if (mPayPresenter == null) {
            return;
        }
        if (mPayAdapter == null) {
            ToastUtil.show(R.string.wallet_tip_5);
            return;
        }
        final String payType = mPayAdapter.getPayType();
        if (Constants.PAY_TYPE_GOOGLE.equals(payType)) {
            CommonHttpUtil.getGoogleOrder(bean.getId(), bean.getCoin(), bean.getMoney(), new HttpCallback() {

                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        mPayPresenter.pay(payType, "", "", obj.getString("orderid"));
                    }
                }
            });
        } else if (Constants.PAY_TYPE_TEST.equals(payType)) {
            MainHttpUtil.testCharge(bean.getId(), bean.getCoin(), bean.getMoney(), new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        if (mPayPresenter != null) {
                            mPayPresenter.checkPayResult();
                        }
                    }
                }
            });
        } else if (Constants.PAY_TYPE_PAYPAL.equals(payType)) {
            CommonHttpUtil.getPaypalOrder(bean.getId(), bean.getPaypalCoin(), bean.getMoney(), payType, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        String orderid = obj.getString("orderid");
                        JSONObject paypalObj = obj.getJSONObject("paypal");
                        mPaypalIsSandbox = "0".equals(paypalObj.getString("paypal_sandbox"));
                        String client_id = paypalObj.getString("product_clientid");
                        String paypal_sandbox_id = paypalObj.getString("sandbox_clientid");
                        String environment = "";//支付环境
                        String paypalClientId = "";
                        if (mPaypalIsSandbox) {
                            paypalClientId = paypal_sandbox_id;
                            environment = PayPalConfiguration.ENVIRONMENT_SANDBOX;
                        } else {
                            paypalClientId = client_id;
                            environment = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
                        }
                        new PaypalPayTask(MyCoinActivity.this)
                                .setPalConfiguration(environment, paypalClientId)
                                .startPay(orderid, bean.getMoney(), bean.getPaypalCoin() + mCoinName);
                    } else {
                        ToastUtil.show(msg);
                    }
                }
            });
        } else {
            String money = bean.getMoney();
            String goodsName = StringUtil.contact(bean.getCoin(), mCoinName);
            String orderParams = StringUtil.contact(
                    "&uid=", CommonAppConfig.getInstance().getUid(),
                    "&token=", CommonAppConfig.getInstance().getToken(),
                    "&money=", money,
                    "&changeid=", bean.getId(),
                    "&coin=", bean.getCoin(),
                    "&type=", payType);
            mPayPresenter.pay(payType, money, goodsName, orderParams);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PaypalPayTask.PAYPAL_TASK_REQUEST_CODE) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.e("paymentExample", confirm.toJSONObject().toString(4));
                    loadData();
                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("paymentExample", "The user canceled.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.e("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinChangeEvent(CoinChangeEvent e) {
        if (mBalance != null) {
            mBalance.setText(e.getCoin());
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_tip) {
            WebViewActivity.forward(mContext, HtmlConfig.CHARGE_PRIVCAY);
        }
    }

    @Override
    protected void onDestroy() {
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        CommonHttpUtil.cancel(CommonHttpConsts.GET_BALANCE);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_ALI_ORDER);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_WX_ORDER);
        MainHttpUtil.cancel(MainHttpConsts.TEST_CHARGE);
        if (mRefreshLayout != null) {
            mRefreshLayout.setOnRefreshListener(null);
        }
        mRefreshLayout = null;
        if (mPayPresenter != null) {
            mPayPresenter.release();
        }
        mPayPresenter = null;
        super.onDestroy();
    }


}
