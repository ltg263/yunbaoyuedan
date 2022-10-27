package com.yunbao.main.activity;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.event.OrderChangedEvent;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.DidiOrderAdapter;
import com.yunbao.main.bean.SnapOrderBean;
import com.yunbao.main.event.OrderCancelEvent;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import io.reactivex.Observable;

public class DidiOrderActivity extends AbsActivity implements View.OnClickListener {
    private RxRefreshView<SnapOrderBean> refreshView;
    private ViewGroup btnFlashOrder;
    private DidiOrderAdapter didiOrderAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_didi_order;
    }
    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.didi_order));
        refreshView = (RxRefreshView) findViewById(R.id.refreshView);
        btnFlashOrder=findViewById(R.id.btn_flash_order);
        refreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(this,5));
        refreshView.setNoDataTip(WordUtil.getString(R.string.no_snap_flash_order));
        refreshView.setDataListner(new RxRefreshView.DataListner<SnapOrderBean>() {
            /*Observable 网络请求生成的Observable 直接return过去*/
            @Override
            public Observable<List<SnapOrderBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<SnapOrderBean> data) {
                setVisibleFlashOrderButton(data);

            }
            @Override
            public void error(Throwable e) {
            }
        });

        didiOrderAdapter=new DidiOrderAdapter(null);
        didiOrderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SnapOrderBean snapOrderBean=didiOrderAdapter.getItem(position);
                toFlashOrderDetail(snapOrderBean);
            }
        });

        didiOrderAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                SnapOrderBean snapOrderBean=didiOrderAdapter.getItem(position);
                toSelectGreateMan(snapOrderBean);
            }
        });

        refreshView.setAdapter(didiOrderAdapter);
        refreshView.initData();
        btnFlashOrder.setOnClickListener(this);
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
    }

    /*根据判断结果去显示按钮*/
    private void setVisibleFlashOrderButton(List<SnapOrderBean> data) {
        if(judgeOrdersHaveActive(data)){
            btnFlashOrder.setVisibility(View.GONE);
        }else{
            btnFlashOrder.setVisibility(View.VISIBLE);
        }
    }

    /*判断是否有活跃的订单,显示快速下单*/
    private boolean judgeOrdersHaveActive(List<SnapOrderBean> data) {
        boolean haveActiveOrder=false;
        if(!ListUtil.haveData(data)){
           return haveActiveOrder;
        }
        for(SnapOrderBean snapOrderBean:data){
            boolean tempIsActive=snapOrderBean.getStatus()==SnapOrderBean.STATUS_GRAB_TICKET;
            if(tempIsActive){
                haveActiveOrder  =tempIsActive;
                break;
            }
        }
        return haveActiveOrder;
    }

    private void toSelectGreateMan(SnapOrderBean snapOrderBean) {
        ChooseGreatActivity.forward(this,snapOrderBean);
    }

    private void toFlashOrderDetail(SnapOrderBean snapOrderBean) {
        FlashOrderDetailActivity.forward(this,snapOrderBean);
    }
    private Observable<List<SnapOrderBean>> getData(int p) {
        return MainHttpUtil.getMyDrip(p).compose(this.<List<SnapOrderBean>>bindToLifecycle());
    }
    @Override
    public void onClick(View v) {
        startActivity(FlashOrderActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderCancelEvent(OrderCancelEvent e) {
        if (e != null && e.isCancel()) {
            finish();
        }
    }

}
