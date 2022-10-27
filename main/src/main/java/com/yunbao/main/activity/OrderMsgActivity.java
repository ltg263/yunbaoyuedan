package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.utils.ImDateUtil;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.OrderMsgAdapter;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.common.event.OrderChangedEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/8/6.
 * 订单消息
 */

public class OrderMsgActivity extends AbsActivity implements OrderMsgAdapter.ActionListener {

    public static void forward(Context context) {
        Intent intent = new Intent(context, OrderMsgActivity.class);
        context.startActivity(intent);
    }

    private CommonRefreshView mRefreshView;
    private OrderMsgAdapter mAdapter;
    private CommonCallback<List<String>> mGetImMsgCallback;
    private List<OrderBean> mOrderList;
    private StringBuilder mStringBuilder;
    private boolean mNeedRefresh;
    private boolean mPaused;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_msg;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.order_msg));
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_order_msg);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<OrderBean>() {
            @Override
            public RefreshAdapter<OrderBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new OrderMsgAdapter(mContext);
                    mAdapter.setActionListener(OrderMsgActivity.this);
                }
                return mAdapter;
            }
            @Override
            public void loadData(int p, HttpCallback callback) {
                if (mStringBuilder != null) {
                    String s = mStringBuilder.toString().trim();
                    if (!TextUtils.isEmpty(s)) {
                        MainHttpUtil.getMutiOrderDetail(s, callback);
                    }
                }
            }

            @Override
            public List<OrderBean> processData(String[] info) {
                List<OrderBean> list = JSON.parseArray(Arrays.toString(info), OrderBean.class);
                if (mOrderList != null) {
                    for (OrderBean orderBean : mOrderList) {
                        for (OrderBean o : list) {
                            if (orderBean.getId().equals(o.getId())) {
                                orderBean.setIsComment(o.getIsComment());
                                orderBean.setIsEvaluate(o.getIsEvaluate());
                                if (orderBean.getStatus() == OrderBean.STATUS_WAIT && o.getStatus() == OrderBean.STATUS_DONE) {
                                    orderBean.setIsComment(1);
                                    orderBean.setIsEvaluate(1);
                                }
                                orderBean.setReceptStatus(o.getReceptStatus());
                                orderBean.setStatus(o.getStatus());
                                orderBean.setSkillBean(o.getSkillBean());
                                orderBean.setServiceTime(o.getServiceTime());
                                break;
                            }
                        }
                    }
                }
                return mOrderList;
            }
            @Override
            public void onRefreshSuccess(List<OrderBean> list, int listCount) {

            }
            @Override
            public void onRefreshFailure() {

            }
            @Override
            public void onLoadMoreSuccess(List<OrderBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
        loadData();

    }


    private void loadData() {
        if (mGetImMsgCallback == null) {
            mGetImMsgCallback = new CommonCallback<List<String>>() {
                @Override
                public void callback(List<String> list) {
                    if (mOrderList == null) {
                        mOrderList = new ArrayList<>();
                    }
                    mOrderList.clear();
                    if (mStringBuilder == null) {
                        mStringBuilder = new StringBuilder();
                    }
                    mStringBuilder.delete(0, mStringBuilder.length());
                    for (String s : list) {
                        OrderBean orderBean = JSON.parseObject(s, OrderBean.class);
                        //设置 收到IM订单消息展示的时间
                        orderBean.setAddTimeString(ImDateUtil.getTimestampString(orderBean.getTimestamp() * 1000));
                        mOrderList.add(orderBean);
                        mStringBuilder.append(orderBean.getId());
                        mStringBuilder.append(",");
                    }
                    if (mRefreshView != null) {
                        mRefreshView.initData();
                    }
                }
            };

        }
        ImMessageUtil.getInstance().getOrderMsgList(mGetImMsgCallback);
    }


    @Override
    public void onItemClick(OrderBean orderBean) {
        boolean isMyAnchor = orderBean.isMyAnchor();
        if (isMyAnchor) {
            int status = orderBean.getStatus();
            if (status == OrderBean.STATUS_WAIT || status == OrderBean.STATUS_DOING||
                status == OrderBean.STATUS_REFUSE_REFUND||
                status == OrderBean.STATUS_AGREE_REFUND||
                status ==OrderBean.STATUS_WAIT_REFUND||
                status == OrderBean.STATUS_WAIT_PLATFORM
            ) {
                OrderAccpetDetailActivity.forward(mContext, orderBean);
            } else {
                if (orderBean.getStatus() == OrderBean.STATUS_DONE) {
                    OrderCommentActivity2.forward(mContext, orderBean.getId(), true);
                } else {
                    OrderAccpetDetailActivity2.forward(mContext, orderBean.getId());
                }
            }
        } else {
            if (orderBean.getStatus() == OrderBean.STATUS_DONE) {
                OrderCommentActivity2.forward(mContext, orderBean.getId(), false);
            } else {
                OrderDetailActivity.forward(mContext, orderBean.getId());
            }
        }
    }


    @Override
    public void onRefuseClick(OrderBean orderBean) {
        MainHttpUtil.orderRefuse(orderBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    loadData();
                }
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    public void onGetOrderClick(OrderBean orderBean) {
        OrderAccpetDetailActivity.forward(mContext, orderBean);
    }

    @Override
    public void onCommentClick(OrderBean orderBean) {
        if (orderBean.isMyAnchor()) {
            //OrderCommentActivity3.forward(mContext, orderBean);
        } else {
            OrderCommentActivity.forward(mContext, orderBean);
        }
    }

    @Override
    protected void onDestroy() {
        if (mAdapter != null) {
            mAdapter.release();
        }
        mAdapter = null;
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        MainHttpUtil.cancel(MainHttpConsts.GET_MUTI_ORDER_DETAIL);
        MainHttpUtil.cancel(MainHttpConsts.ORDER_REFUSE);
        super.onDestroy();
    }



    @Override
    protected void onPause() {
        mPaused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused) {
            if (mNeedRefresh) {
                mNeedRefresh = false;
                loadData();
            }
        }
        mPaused = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderChangedEvent(OrderChangedEvent e) {
        mNeedRefresh = true;
    }

}
