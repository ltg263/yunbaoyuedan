package com.yunbao.main.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.OrderCancelAdapter;
import com.yunbao.main.bean.OrderCancelBean;
import com.yunbao.common.event.OrderChangedEvent;
import com.yunbao.main.event.OrderCancelEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/8/5.
 * 取消订单
 */

public class OrderCancelActivity extends AbsActivity implements OrderCancelAdapter.ActionListener {

    public static void forward(Context context, String orderId) {
        Intent intent = new Intent(context, OrderCancelActivity.class);
        intent.putExtra(Constants.ORDER_ID, orderId);
        context.startActivity(intent);
    }

    private RecyclerView mRecyclerView;
    private String mOrderId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_cancel;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.order_cancel));
        mOrderId = getIntent().getStringExtra(Constants.ORDER_ID);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        MainHttpUtil.getOrderCancelList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (mRecyclerView != null) {
                        List<OrderCancelBean> list = JSON.parseArray(Arrays.toString(info), OrderCancelBean.class);
                        if (list != null && list.size() > 0){
                            list.get(0).setChecked(true);
                        }
                        OrderCancelAdapter adapter = new OrderCancelAdapter(mContext, list);
                        adapter.setActionListener(OrderCancelActivity.this);
                        mRecyclerView.setAdapter(adapter);
                    }
                }
            }
        });
    }

    @Override
    public void onConfirmClick(final String ids) {
        if (TextUtils.isEmpty(mOrderId)) {
            return;
        }
        if (TextUtils.isEmpty(ids)) {
            ToastUtil.show(R.string.order_cancel_reason_1);
            return;
        }
        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.dialog_tip),WordUtil.getString(R.string.order_cancel_1), true,new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                MainHttpUtil.orderCancel(mOrderId, ids, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            EventBus.getDefault().post(new OrderChangedEvent(mOrderId));
                            //EventBus.getDefault().post(new OrderCancelEvent(true));
                            finish();
                        }
                        ToastUtil.show(msg);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_ORDER_CANCEL_LIST);
        super.onDestroy();
    }
}
