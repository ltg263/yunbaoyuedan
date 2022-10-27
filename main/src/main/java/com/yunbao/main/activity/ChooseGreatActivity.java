package com.yunbao.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.dialog.BottomDealFragment;
import com.yunbao.common.pay.paypal.PaypalPayTask;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.GreatManAdapter;
import com.yunbao.main.bean.GreateManBean;
import com.yunbao.main.bean.SnapOrderBean;
import com.yunbao.main.business.OrderCutDownModel;
import com.yunbao.main.dialog.SelectGreateManFragment;
import com.yunbao.main.event.OrderCancelEvent;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.List;
import io.reactivex.Observable;
import static com.yunbao.common.Constants.DATA;

@SuppressWarnings("ALL")
public class ChooseGreatActivity extends AbsActivity {
    private RxRefreshView refreshView;
    private GreatManAdapter greatManAdapter;
    private SnapOrderBean snapOrderBean;
    private String orderId;
    private OrderCutDownModel cutDownModel;
    private TextView tvCountDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose_great;
    }

    @Override
    protected void main() {
        super.main();
        snapOrderBean=getIntent().getParcelableExtra(DATA);
        if(snapOrderBean==null){
            finish();
        }
        orderId=snapOrderBean.getId();
        setTitle(getString(R.string.select_great));

        cutDownModel=new OrderCutDownModel();
        cutDownModel.setTimeListner(new OrderCutDownModel.TimeListner() {
            @Override
            public void time(StringBuilder stringBuilder) {
                tvCountDown.setText(stringBuilder.insert(0, WordUtil.getString(R.string.time_cut_down)));
            }
            @Override
            public void compelete() {
                finish();
            }
        });

        cutDownModel.start(snapOrderBean.getLastWaitTime());
        refreshView = (RxRefreshView) findViewById(R.id.refreshView);
        tvCountDown = findViewById(R.id.tv_count_down);

        RxRefreshView.ReclyViewSetting setting=RxRefreshView.ReclyViewSetting.createLinearSetting(this,1);
        refreshView.setReclyViewSetting(setting);
        refreshView.setNoDataTip(WordUtil.getString(R.string.no_grean_man_snap_tip));
        greatManAdapter=new GreatManAdapter(null,this);

        refreshView.setAdapter(greatManAdapter);
        refreshView.setDataListner(new RxRefreshView.DataListner() {
            @Override
            public Observable<List<GreateManBean>> loadData(int p) {
                return getData();
            }
            @Override
            public void compelete(List data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });

        greatManAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GreateManBean greateManBean=greatManAdapter.getItem(position);
                showSelGreateManDialog(greateManBean);
            }
        });
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
    }


    private void showSelGreateManDialog(GreateManBean greateManBean) {
        SelectGreateManFragment selectGreateManFragment=new SelectGreateManFragment();
        selectGreateManFragment.setGreateManBean(greateManBean);
        selectGreateManFragment.setSnapOrderBean(snapOrderBean);
        selectGreateManFragment.show(getSupportFragmentManager());
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshView.initData();
    }


    private Observable<List<GreateManBean>> getData() {
        return MainHttpUtil.getLiveGreatMan(orderId,getLastIndex());
    }
    @SuppressWarnings("AliControlFlowStatementWithoutBraces")
    public String getLastIndex(){
        //noinspection AliControlFlowStatementWithoutBraces,AliControlFlowStatementWithoutBraces
//        if(greatManAdapter==null||greatManAdapter.size()==0||refreshView.isRefreshing())
//            return "0";
        // TODO: 2020-12-08 跟IOS保持一致，不使用分页，lastid都返回0  //greatManAdapter.getLastData().getId()
        return "0";
    }


    public static void forward(Context context, SnapOrderBean snapOrderBean){
        Intent intent=new Intent(context,ChooseGreatActivity.class);
        intent.putExtra(DATA,snapOrderBean);
        context.startActivity(intent);
    }


    /*点击弹出选择框*/
    public void rightClick(View vew){
        BottomDealFragment dealFragment=new BottomDealFragment();
        dealFragment.setDialogButtonArray(
                new BottomDealFragment.DialogButton(WordUtil.getString(R.string.order_detail),new BottomDealFragment.ClickListnter(){
                    @Override
                    public void click(View view) {
                        toOrderDetail();
                    }
                }),
                new BottomDealFragment.DialogButton(WordUtil.getString(R.string.order_cancel), new BottomDealFragment.ClickListnter() {
                    @Override
                    public void click(View view) {
                      cancleOrder();
                    }
                  })
               );
        dealFragment.show(getSupportFragmentManager());
    }

    private void cancleOrder() {

        FlashOrderCancleActivity.forward(this,orderId);
    }

    private void toOrderDetail() {
        FlashOrderDetailActivity.forward(this,snapOrderBean);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        if(cutDownModel!=null){
           cutDownModel.release();
           cutDownModel=null;
        }
        if(greatManAdapter!=null){
            greatManAdapter.release();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderCancelEvent(OrderCancelEvent e) {
        if (e != null && e.isCancel()) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PaypalPayTask.PAYPAL_TASK_REQUEST_CODE) {
            //paypal支付结果
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.e("paymentExample", confirm.toJSONObject().toString(4));
                    finish();
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

}
