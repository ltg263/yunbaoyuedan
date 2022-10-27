package com.yunbao.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.pay.PayCallback;
import com.yunbao.common.pay.PayPresenter;
import com.yunbao.common.pay.paypal.PaypalPayTask;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastHigherUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.event.LiveChatRoomBossPlaceOrderEvent;
import com.yunbao.main.R;
import com.yunbao.main.adapter.OrderPayAdapter;
import com.yunbao.main.bean.MySkillBean;
import com.yunbao.main.bean.OrderPayBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.main.dialog.SelectSkillDialogFragemnt;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.utils.CityUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;

/**
 * Created by cxf on 2019/8/3.
 * 下单
 */
@Route(path = RouteUtil.PATH_ORDER_MAKE)
public class OrderMakeActivity extends AbsActivity implements View.OnClickListener, OnItemClickListener<OrderPayBean> {


    public static void forward(Context context, UserBean userBean, SkillBean skillBean) {
        Intent intent = new Intent(context, OrderMakeActivity.class);
        intent.putExtra(Constants.USER_BEAN, userBean);
        intent.putExtra(Constants.SKILL_BEAN, skillBean);
        context.startActivity(intent);
    }

    private ImageView mAvatar;
    private TextView mName;
    private View mSexGroup;
    private ImageView mSex;
    private TextView mAge;
    private TextView mPrice;
    private TextView mSkillName;
    private TextView mTime;
    private TextView mOrderNum1;
    private TextView mOrderNum2;
    private View mBtnDecrease;
    private View mBtnOrder;
    private TextView mTotal1;
    private TextView mTotal2;
    private EditText mDes;
    private TextView mTvFee;


    private RecyclerView mRecyclerView;
    private OrderPayAdapter mAdapter;
    private UserBean mUserBean;
    private SkillBean mSkillBean;
    private String mCoinName;
    private String mPayType;
    private int mOrderCount = 1;
    private ArrayList<Province> mTimeList;
    private String mTimeType;
    private String mTimeVal;
    private PayPresenter mPayPresenter;
    private String mOrderId;
    private ViewGroup mBtnSkill;

    private String mFrom;//跳转来源

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_make;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.order_confirm));
        Intent intent = getIntent();
        UserBean u = intent.getParcelableExtra(Constants.USER_BEAN);
        SkillBean skillBean = intent.getParcelableExtra(Constants.SKILL_BEAN);
        mFrom = intent.getStringExtra(Constants.LIVE_CHAT_ROOM);
        mUserBean = u;
        mSkillBean = skillBean;
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        mTotal2 = (TextView) findViewById(R.id.total_2);
        mSexGroup = findViewById(R.id.sex_group);
        mSex = findViewById(R.id.sex);
        mAge = findViewById(R.id.age);
        mPrice = findViewById(R.id.price);
        mSkillName = findViewById(R.id.skill_name);
        mTime = findViewById(R.id.time);
        mOrderNum1 = findViewById(R.id.order_num_1);
        mOrderNum2 = findViewById(R.id.order_num_2);
        mBtnDecrease = findViewById(R.id.btn_decrease);
        mBtnOrder = findViewById(R.id.btn_order);
        mTotal1 = findViewById(R.id.total_1);
        mBtnSkill =findViewById(R.id.btn_buy_skill);
        mTvFee=findViewById(R.id.tv_fee);
        mDes = findViewById(R.id.des);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        findViewById(R.id.btn_choose_time).setOnClickListener(this);
        findViewById(R.id.btn_increase).setOnClickListener(this);
        mBtnDecrease.setOnClickListener(this);
        mBtnOrder.setOnClickListener(this);
        mBtnSkill.setOnClickListener(this);
        if (u != null) {
            ImgLoader.display(mContext, u.getAvatar(), mAvatar);
            mName.setText(u.getUserNiceName());
            mSexGroup.setBackground(CommonIconUtil.getSexBgDrawable(u.getSex()));
            mSex.setImageDrawable(CommonIconUtil.getSexDrawable(u.getSex()));
            mAge.setText(u.getAge());
        }
        setSkill(skillBean);

        mPayPresenter = new PayPresenter(this);
        mPayPresenter.setAliCallbackUrl(HtmlConfig.ALI_PAY_ORDER_URL);
        mPayPresenter.setPayCallback(new PayCallback() {
            @Override
            public void onSuccess() {
                if (!TextUtils.isEmpty(mOrderId)) {
                    OrderDetailActivity.forwardWithFrom(mContext, mOrderId,mFrom);
                    EventBus.getDefault().post(new LiveChatRoomBossPlaceOrderEvent(mUserBean));
                    finish();
                }
            }

            @Override
            public void onFailed() {

            }
        });

        getLatestTime();
        getPayList();
    }

    private void setSkill(SkillBean skillBean) {
        if (skillBean != null) {
            mSkillName.setText(skillBean.getSkillName());
            mPrice.setText(skillBean.getPirceResult(mCoinName));
        }
    }


    private void showPrice() {
        if (mSkillBean == null) {
            return;
        }
        int totalPrice = mSkillBean.getPriceVal() * mOrderCount;
        String totalString = StringUtil.contact(String.valueOf(totalPrice), mCoinName);
        if (mTotal1 != null) {
            mTotal1.setText(totalString);
        }
        if (mTotal2 != null) {
            mTotal2.setText(totalString);
        }
        if (mOrderNum1 != null) {
            mOrderNum1.setText(String.valueOf(mOrderCount));
        }
        if (mOrderNum2 != null) {
            mOrderNum2.setText(String.valueOf(mOrderCount));
        }
        if (mBtnDecrease != null) {
            mBtnDecrease.setEnabled(mOrderCount > 0);
        }
        if (mBtnOrder != null) {
            mBtnOrder.setEnabled(mOrderCount > 0 && !TextUtils.isEmpty(mPayType));
        }
    }


    private void getPayList() {
        MainHttpUtil.getOrderPay(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
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
                            mAdapter.setOnItemClickListener(OrderMakeActivity.this);
                        }
                        mRecyclerView.setAdapter(mAdapter);
                        showPrice();
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(OrderPayBean bean, int position) {
        mPayType = bean.getId();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_decrease) {
            decreaseNum();
        } else if (i == R.id.btn_increase) {
            increaseNum();
        } else if (i == R.id.btn_order) {
            submitOrder(v);
        } else if (i == R.id.btn_choose_time) {
            chooseTime();
        }else if(i==R.id.btn_buy_skill){
            selectSkill();
        }
    }

    private void selectSkill() {
        if(ClickUtil.canClick()){
           MainHttpUtil.getSkillAuth(mUserBean.getId()).compose(this.<List<MySkillBean>>bindToLifecycle()).subscribe(new DefaultObserver<List<MySkillBean>>() {
               @Override
               public void onNext(List<MySkillBean> mySkillBeans) {
                   SelectSkillDialogFragemnt skillDialogFragemnt=new SelectSkillDialogFragemnt();
                   skillDialogFragemnt.setMySkillBeanList(mySkillBeans);
                   skillDialogFragemnt.setOnSelectListner(new SelectSkillDialogFragemnt.OnSelectListner() {
                       @Override
                       public void onSelect(MySkillBean mySkillBean) {
                           mSkillBean=mySkillBean.getSkill();
                           setSkill(mSkillBean);
                           showPrice();
                       }
                   });
                   skillDialogFragemnt.show(getSupportFragmentManager());
               }
           });
        }

    }

    /**
     * 减少数量
     */
    private void decreaseNum() {
        if (mOrderCount <= 0) {
            return;
        }
        mOrderCount--;
        showPrice();
    }

    /**
     * 增加数量
     */
    private void increaseNum() {
        mOrderCount++;
        showPrice();
    }


    private void getLatestTime() {
        mTimeList = CityUtil.getInstance().getTimeList(WordUtil.getString(R.string.today),
                WordUtil.getString(R.string.tomorrow),
                WordUtil.getString(R.string.tomorrow2));
        Calendar c = Calendar.getInstance();
        Province day = mTimeList.get(0);
        mTimeType = day.getAreaId();
        City hour = day.getCities().get(0);
        County m = hour.getCounties().get(0);
        mTimeVal = StringUtil.contact(hour.getAreaName(), ":", m.getAreaName());
        if (mTime != null) {
            mTime.setText(StringUtil.contact(day.getAreaName(), " ", mTimeVal));
        }
    }

    /**
     * 选择时间
     */
    private void chooseTime() {
        mTimeList = CityUtil.getInstance().getTimeList(WordUtil.getString(R.string.today),
                WordUtil.getString(R.string.tomorrow),
                WordUtil.getString(R.string.tomorrow2));
        Calendar c = Calendar.getInstance();
        DialogUitl.showOrderTimeDialog(this, mTimeList, 0, 0, 0, new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(Province province, City city, County county) {
                mTimeType = province.getAreaId();
                mTimeVal = StringUtil.contact(city.getAreaName(), ":", county.getAreaName());
                if (mTime != null) {
                    mTime.setText(StringUtil.contact(province.getAreaName(), " ", mTimeVal));
                }
            }
        });
    }

    /**
     * 提交订单
     */
    private void submitOrder(final View view) {
        if(!ClickUtil.canClick()){
            return;
        }
        if (mSkillBean == null && mUserBean == null && TextUtils.isEmpty(mPayType)) {
            return;
        }
        if (TextUtils.isEmpty(mTimeType) || TextUtils.isEmpty(mTimeVal)) {
            ToastUtil.show(R.string.order_service_time_2);
            return;
        }
        String des = mDes.getText().toString().trim();

        view.setEnabled(false);
        MainHttpUtil.setOrder(
                mUserBean.getId(),
                mSkillBean.getSkillId(), mTimeType, mTimeVal,
                String.valueOf(mOrderCount), des, mPayType, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        view.setEnabled(true);
                        if (code == 0) {
                            if (info.length > 0) {
                                JSONObject obj = JSON.parseObject(info[0]);
                                mOrderId = obj.getString("orderid");
                                if (Constants.PAY_TYPE_COIN.equals(mPayType)) {
                                    OrderDetailActivity.forwardWithFrom(mContext, mOrderId,mFrom);
                                    EventBus.getDefault().post(new LiveChatRoomBossPlaceOrderEvent(mUserBean));
                                    finish();
                                } else if (Constants.PAY_TYPE_ALI.equals(mPayType)) {
                                    JSONObject ali = obj.getJSONObject("ali");
                                    mPayPresenter.setAliPartner(ali.getString("partner"));
                                    mPayPresenter.setAliSellerId(ali.getString("seller_id"));
                                    mPayPresenter.setAliPrivateKey(ali.getString("key"));
                                    mPayPresenter.pay2(Constants.PAY_TYPE_ALI, obj.getString("total"), StringUtil.contact(obj.getString("total"), mCoinName), null, obj.getString("orderno"));
                                } else if (Constants.PAY_TYPE_WX.equals(mPayType)){
                                    JSONObject wxObj = obj.getJSONObject("wx");
                                    mPayPresenter.setWxAppID(wxObj.getString("appid"));
                                    mPayPresenter.wxPay2(wxObj.getString("partnerid"),wxObj.getString("prepayid"),wxObj.getString("package"),wxObj.getString("noncestr"),wxObj.getString("timestamp"),wxObj.getString("sign"));
                                } else if (Constants.PAY_TYPE_PAYPAL.equals(mPayType)){
                                    JSONObject paypalObj = obj.getJSONObject("paypal");
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
                                    new PaypalPayTask(OrderMakeActivity.this)
                                            .setPalConfiguration(environment,paypalClientId)
                                            .startPay(obj.getString("orderno"),obj.getString("total"), StringUtil.contact(obj.getString("total"), mCoinName));

                                }
                            }
                        } else {
                            ToastHigherUtil.show(msg);
                        }
                    }

                    @Override
                    public void onError() {
                        super.onError();
                        view.setEnabled(true);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PaypalPayTask.PAYPAL_TASK_REQUEST_CODE) {
            //paypal支付结果
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                //支付成功
                try {
                    Log.e("paymentExample", confirm.toJSONObject().toString(4));
                    if (!TextUtils.isEmpty(mOrderId)) {
//                        ToastUtil.show("支付成功");
                        OrderDetailActivity.forwardWithFrom(mContext, mOrderId,mFrom);
                        EventBus.getDefault().post(new LiveChatRoomBossPlaceOrderEvent(mUserBean));
                        finish();
                    }
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

    @Override
    protected void onDestroy() {
        if (mPayPresenter != null) {
            mPayPresenter.release();
        }
        mPayPresenter = null;
        MainHttpUtil.cancel(MainHttpConsts.GET_ORDER_PAY);
        MainHttpUtil.cancel(MainHttpConsts.SET_ORDER);
        super.onDestroy();
    }

}
