package com.yunbao.common.pay.paypal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.yunbao.common.Constants;

import java.math.BigDecimal;

/**
 * Created by Sky.L on 2020-12-21
 * PayPal支付相关
 */
public class PaypalPayTask {
    private Activity mActivity;
    private PayPalConfiguration mPalConfiguration;
    private Handler mHandler;
    public static final int PAYPAL_TASK_REQUEST_CODE = 1001;

    public PaypalPayTask(Activity activity) {
        mActivity = activity;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    onBuyPressed(msg.getData().getString(Constants.PAYPAL_PAYMENT_MONEY), msg.getData().getString(Constants.PAYPAL_PAYMENT_COIN), msg.getData().getString(Constants.ORDER_ID));
                }
            }
        };
    }

    //设置支付环境，clientid
    public PaypalPayTask setPalConfiguration(String environment,String paypalClientId){
        mPalConfiguration = new PayPalConfiguration()
                .environment(environment)
                .clientId(paypalClientId);
        initPayPalService();
        return this;
    }

    //初始化PayPal设置
    private void initPayPalService() {
        Intent intent = new Intent(mActivity, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mPalConfiguration);
        mActivity.startService(intent);
    }

    //启动支付
    public PaypalPayTask startPay(String orderid,String money,String coin){
        Message message = Message.obtain();
        message.what = 0;
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ORDER_ID, orderid);
        bundle.putString(Constants.PAYPAL_PAYMENT_MONEY, money);
        bundle.putString(Constants.PAYPAL_PAYMENT_COIN, coin);
        message.setData(bundle);
        mHandler.sendMessageDelayed(message,200);
        return this;
    }

    //真正调起paypal
    private void onBuyPressed(String price, String orderName, String orderId) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(price), Constants.PAYPAL_PAYMENT_CURRENCY_TYPE, orderName,
                PayPalPayment.PAYMENT_INTENT_SALE);
        payment.invoiceNumber(orderId);
        payment.custom(orderId);
        Intent intent = new Intent(mActivity, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mPalConfiguration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        mActivity.startActivityForResult(intent, PAYPAL_TASK_REQUEST_CODE);
    }
}
