package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.OrderCenterAdapter;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.event.OrderChangedEvent;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/8/5.
 */

public class OrderCenterActivity extends AbsActivity implements OrderCenterAdapter.ActionListener {

    public static void forward(Context context) {
        Intent intent = new Intent(context, OrderCenterActivity.class);
        context.startActivity(intent);
    }

    private CommonRefreshView mRefreshView;
    private OrderCenterAdapter mAdapter;
    private int mPage = 1;
    private boolean mNeedRefresh;
    private boolean mPaused;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_center;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.order_center));
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<OrderBean>() {
            @Override
            public RefreshAdapter<OrderBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new OrderCenterAdapter(mContext);
                    mAdapter.setActionListener(OrderCenterActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                mPage = p;
                if (p == 1) {
                    MainHttpUtil.getMyOrderList(callback);
                } else {
                    MainHttpUtil.getMyOrderList2(p,callback);
                }
            }

            @Override
            public List<OrderBean> processData(String[] info) {
                if (mPage == 1) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    List<OrderBean> list1 = JSON.parseArray(obj.getString("listing"), OrderBean.class);
                    if (list1.size() > 0) {
                        list1.get(0).setHasTitile(true);
                    }
                    List<OrderBean> list2 = JSON.parseArray(obj.getString("list"), OrderBean.class);
                    if (list2.size() > 0) {
                        list2.get(0).setHasTitile(true);
                    }
                    list1.addAll(list2);
                    return list1;
                } else {
                    return JSON.parseArray(Arrays.toString(info), OrderBean.class);
                }
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
        mRefreshView.initData();
    }


    @Override
    public void onNextClick(OrderBean orderBean) {
        UserBean u = orderBean.getLiveUserInfo();
        SkillBean skillBean = orderBean.getSkillBean();
        if (u != null && skillBean != null) {
            OrderMakeActivity.forward(mContext, u, skillBean);
        }
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
                    status == OrderBean.STATUS_WAIT_PLATFORM ) {
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
    protected void onDestroy() {
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        MainHttpUtil.cancel(MainHttpConsts.GET_MY_ORDER_LIST);
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
                if (mRefreshView != null) {
                    mRefreshView.initData();
                }
            }
        }
        mPaused = false;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderChangedEvent(OrderChangedEvent e) {
        mNeedRefresh = true;
    }
}
