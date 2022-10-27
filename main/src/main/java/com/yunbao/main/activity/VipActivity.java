package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.CoinPayBean;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.pay.PayCallback;
import com.yunbao.common.pay.PayPresenter;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.VipItemAdapter;
import com.yunbao.main.bean.VipBuyBean;
import com.yunbao.main.dialog.BuyVipDialogFragment;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import java.util.List;

/**
 * Created by cxf on 2019/5/11.
 * 会员中心
 */
@Route(path = RouteUtil.PATH_VIP)
public class VipActivity extends AbsActivity implements View.OnClickListener {


    private TextView mTip1;
    private TextView mTip2;
    private TextView mBtnCharge;
    private List<VipBuyBean> mBuyList;
    private List<CoinPayBean> mPayList;
    private String mCoinName;
    private PayPresenter mPayPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vip;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.vip_center));
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mTip1 = findViewById(R.id.tip_1);
        mTip2 = findViewById(R.id.tip_2);
        mBtnCharge = findViewById(R.id.btn_charge);
        mBtnCharge.setOnClickListener(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new VipItemAdapter(mContext));
        mPayPresenter = new PayPresenter(VipActivity.this);
        mPayPresenter.setServiceNameAli(Constants.PAY_VIP_COIN_ALI);
        mPayPresenter.setServiceNameWx(Constants.PAY_VIP_COIN_WX);
        mPayPresenter.setAliCallbackUrl(HtmlConfig.ALI_PAY_ORDER_URL);
        mPayPresenter.setPayCallback(new PayCallback() {
            @Override
            public void onSuccess() {
                getMyVip();
            }

            @Override
            public void onFailed() {

            }
        });
        getMyVip();
    }


    private void showVip(boolean isVip, String time) {
        if (isVip) {
            if (mTip1 != null) {
                mTip1.setText(R.string.vip_tip_6);
            }
            if (mTip2 != null) {
                mTip2.setText(String.format(WordUtil.getString(R.string.vip_tip_7), time));
            }
            if (mBtnCharge != null) {
                mBtnCharge.setText(R.string.vip_tip_5);
            }
        } else {
            if (mTip1 != null) {
                mTip1.setText(R.string.vip_tip_1);
            }
            if (mTip2 != null) {
                mTip2.setText(R.string.vip_tip_2);
            }
            if (mBtnCharge != null) {
                mBtnCharge.setText(R.string.vip_tip_4);
            }
        }
    }

    private void getMyVip() {
        MainHttpUtil.getMyVip(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mBuyList = JSON.parseArray(obj.getString("list"), VipBuyBean.class);
                    mPayList = JSON.parseArray(obj.getString("paylist"), CoinPayBean.class);
                    mPayPresenter.setAliPartner(obj.getString("aliapp_partner"));
                    mPayPresenter.setAliSellerId(obj.getString("aliapp_seller_id"));
                    mPayPresenter.setAliPrivateKey(obj.getString("aliapp_key"));
                    mPayPresenter.setWxAppID(obj.getString("wx_appid"));
                    showVip(obj.getIntValue("isvip") == 1, obj.getString("endtime"));
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_charge) {
            openBuyWindow();
        }
    }

    /**
     * 打开购买窗口
     */
    private void openBuyWindow() {
        if (mBuyList == null || mBuyList.size() == 0) {
            return;
        }
        for (int i = 0, size = mBuyList.size(); i < size; i++) {
            mBuyList.get(i).setChecked(i == 0);
        }
        BuyVipDialogFragment fragment = new BuyVipDialogFragment();
        fragment.setCoinName(mCoinName);
        fragment.setBuyList(mBuyList);
        fragment.setPayList(mPayList);
        fragment.setPayPresenter(mPayPresenter);
        fragment.show(getSupportFragmentManager(), "BuyVipDialogFragment");
    }


    /**
     * 余额购买vip
     */
    public void buyVipWithCoin(String vipid) {
        MainHttpUtil.buyVipWithCoin(vipid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    showVip(obj.getIntValue("isvip") == 1, obj.getString("endtime"));
                }
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_MY_VIP);
        if (mPayPresenter != null) {
            mPayPresenter.release();
        }
        mPayPresenter = null;
        super.onDestroy();
    }
}
