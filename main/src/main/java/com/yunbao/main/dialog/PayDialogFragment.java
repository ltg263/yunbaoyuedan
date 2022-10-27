package com.yunbao.main.dialog;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.pay.paypal.PaypalPayTask;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.MoneyHelper;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.pay.PayCallback;
import com.yunbao.common.pay.PayPresenter;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.main.R;
import com.yunbao.main.activity.OrderMakeActivity;
import com.yunbao.main.adapter.OrderPayAdapter;
import com.yunbao.main.bean.OrderPayBean;
import com.yunbao.main.http.MainHttpUtil;
import java.util.List;

public class PayDialogFragment extends AbsDialogFragment implements OnItemClickListener<OrderPayBean>, View.OnClickListener {
    private ImageView mImgClose;
    private TextView mTvCoin;
    private TextView mTvRealyPrice;
    private RecyclerView mRecyclerView;
    private String mPayType;
    private OrderPayAdapter mAdapter;
    private String mCoinName;
    private int totalPrice;
    private String dripid;
    private String liveuid;
    private PayPresenter mPayPresenter;
    private PayCallback mPayCallback;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_pay;
    }
    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }
    @Override
    protected boolean canCancel() {
        return false;
    }
    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(com.yunbao.common.R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        int size= DpUtil.dp2px(320);
        params.width = size;
        params.height =size;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }
    @Override
    public void init() {
        super.init();
        mImgClose = (ImageView) findViewById(R.id.img_close);
        mTvCoin = (TextView) findViewById(R.id.tv_coin);
        mTvRealyPrice = (TextView) findViewById(R.id.tv_realy_price);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mCoinName= CommonAppConfig.getInstance().getCoinName();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mTvCoin.setText(MoneyHelper.moneySymbol(totalPrice,MoneyHelper.TYPE_PLATFORM));
        mTvRealyPrice.setText(MoneyHelper.moneySymbol(totalPrice,MoneyHelper.TYPE_RMB));
        setOnClickListener(R.id.img_close,this);
        setOnClickListener(R.id.btn_commit,this);
        getPayList();
        mPayPresenter=new PayPresenter((Activity) mContext);
        mPayPresenter.setAliCallbackUrl(HtmlConfig.ALI_PAY_ORDER_URL);
        mPayPresenter.setPayCallback(mPayCallback);
    }

    private void getPayList() {
        MainHttpUtil.getOrderPay(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                parseData(code,msg,info);
            }
        });
    }

    /*解析数据*/
    private void parseData(int code, String msg, String[] info) {
        if (code == 0 && info.length > 0) {
            if (mRecyclerView != null) {
                JSONObject obj = JSON.parseObject(info[0]);
                List<OrderPayBean> list = JSON.parseArray(obj.getString("paylist"), OrderPayBean.class);
                if (list.size() > 0) {
                    OrderPayBean bean = list.get(0);
                    bean.setChecked(true);
                    mPayType = bean.getId();
                }
                if (mAdapter == null) {
                    mAdapter = new OrderPayAdapter(mContext, list, mCoinName, obj.getLongValue("coin"));
                    mAdapter.setOnItemClickListener(PayDialogFragment.this);
                }
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public void onItemClick(OrderPayBean bean, int position) {
        mPayType=bean.getId();
    }

    @Override
        public void onClick(View v) {
            int id=v.getId();
            if(id==R.id.img_close){
                close();
            }else if(id==R.id.btn_commit){
                commit(v);
            }
    }
    private void close() {
        dismiss();
    }
    private void commit(final View v) {
        if(!ClickUtil.canClick()||TextUtils.isEmpty(dripid)||TextUtils.isEmpty(mPayType)||TextUtils.isEmpty(liveuid)){
            return;
        }
        v.setEnabled(false);
        MainHttpUtil.selectLive(dripid,liveuid,mPayType).subscribe(new DefaultObserver<JSONObject>() {
            @Override
            public void onNext(JSONObject jsonObject) {
                if (Constants.PAY_TYPE_COIN.equals(mPayType)) {
                    if(mPayCallback!=null){
                        mPayCallback.onSuccess();
                    }
                } else if (Constants.PAY_TYPE_ALI.equals(mPayType)) {
                    v.setEnabled(true);
                    JSONObject ali = jsonObject.getJSONObject("ali");
                    mPayPresenter.setAliPartner(ali.getString("partner"));
                    mPayPresenter.setAliSellerId(ali.getString("seller_id"));
                    mPayPresenter.setAliPrivateKey(ali.getString("key"));
                    mPayPresenter.pay2(Constants.PAY_TYPE_ALI, jsonObject.getString("total"), StringUtil.contact(jsonObject.getString("total"), mCoinName), null, jsonObject.getString("orderno"));
                } else if (Constants.PAY_TYPE_WX.equals(mPayType)){
                    JSONObject wxObj = jsonObject.getJSONObject("wx");
                    L.e("timestamp---"+wxObj.getString("timestamp"));
                    mPayPresenter.setWxAppID(wxObj.getString("appid"));
                    mPayPresenter.wxPay2(wxObj.getString("partnerid"),wxObj.getString("prepayid"),wxObj.getString("package"),wxObj.getString("noncestr"),wxObj.getString("timestamp"),wxObj.getString("sign"));
                } else if (Constants.PAY_TYPE_PAYPAL.equals(mPayType)){
                    JSONObject paypalObj = jsonObject.getJSONObject("paypal");
                    boolean paypalIsSandbox = "0".equals(paypalObj.getString("paypal_sandbox"));
                    String client_id = paypalObj.getString("product_clientid");
                    String paypal_sandbox_id = paypalObj.getString("sandbox_clientid");
                    String environment = "";//支付环境
                    String paypalClientId = "";
                    if (paypalIsSandbox){
                        paypalClientId = paypal_sandbox_id;
                        environment = PayPalConfiguration.ENVIRONMENT_SANDBOX;
                    }else {
                        paypalClientId = client_id;
                        environment = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
                    }
                    new PaypalPayTask(getActivity())
                            .setPalConfiguration(environment,paypalClientId)
                            .startPay(jsonObject.getString("orderno"),jsonObject.getString("total"), StringUtil.contact(jsonObject.getString("total"), mCoinName));
                }
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                v.setEnabled(true);
            }
        });
    }

    public PayDialogFragment setDripid(String dripid) {
        this.dripid = dripid;
        return this;
    }

    public void setmPayCallback(PayCallback mPayCallback) {
        this.mPayCallback = mPayCallback;
    }

    public PayDialogFragment setLiveuid(String liveuid) {
        this.liveuid = liveuid;
        return this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPayPresenter!=null){
            mPayPresenter.release();
            mPayPresenter=null;
        }
    }
}
