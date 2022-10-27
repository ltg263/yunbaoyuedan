package com.yunbao.main.activity;

import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.custom.DrawableTextView;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.event.DripEvent;
import com.yunbao.main.R;
import com.yunbao.main.adapter.SnatchHallAdapter;
import com.yunbao.main.bean.SnapOrderBean;
import com.yunbao.main.http.MainHttpUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import io.reactivex.Observable;

public class SnatchHallActivity extends AbsActivity implements View.OnClickListener {
    private RxRefreshView<SnapOrderBean> refreshView;
    private SnatchHallAdapter snatchHallAdapter;
    private DrawableTextView dtCountDown;

    private int currentNewOrderNum;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_snatch_hall;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.snatch_hall));
        refreshView=findViewById(R.id.refreshView);
        refreshView.setEmptyLayoutId(R.layout.view_no_data_order_msg);
        dtCountDown = findViewById(R.id.dt_count_down);
        dtCountDown.setOnClickListener(this);
        RxRefreshView.ReclyViewSetting reclyViewSetting=RxRefreshView.ReclyViewSetting.createLinearSetting(this,5);
        refreshView.setReclyViewSetting(reclyViewSetting);
        refreshView.setDataListner(new RxRefreshView.DataListner<SnapOrderBean>() {
            @Override
            public Observable<List<SnapOrderBean>> loadData(int p) {
                return getData();
            }
            @Override
            public void compelete(List<SnapOrderBean> data) {
                currentNewOrderNum=0;
                setNewOrderNum();
            }
            @Override
            public void error(Throwable e) {
            }
        });
        snatchHallAdapter=new SnatchHallAdapter(null);
        snatchHallAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int id=view.getId();
                if(id==R.id.btn_detail){
                    SnapOrderBean snapOrderBean=snatchHallAdapter.getItem(position);
                    OrderTakingDetailActivity.forward(SnatchHallActivity.this,snapOrderBean);
                }else if(id==R.id.btn_confirm){
                    confirm(position);
                }
            }
        });
        snatchHallAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SnapOrderBean item=snatchHallAdapter.getItem(position);
                OrderTakingDetailActivity.forward(SnatchHallActivity.this,item);
            }
        });

        refreshView.setAdapter(snatchHallAdapter);
        refreshView.initData();
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
    }

    private void confirm(int position) {
        if(!ClickUtil.canClick()) {
            return;
        }
        final SnapOrderBean orderBean= snatchHallAdapter.getItem(position);
        MainHttpUtil.grapDrip(orderBean.getId()).compose(this.<Boolean>bindToLifecycle()).subscribe(new DefaultObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if(aBoolean){
                  refreshView.autoRefresh();
                }
            }
        });
    }

    private void toDetail(SnapOrderBean orderBean) {
        FlashOrderDetailActivity.forward(this,orderBean);
    }

    /*跟新订单消息*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverDripEvent(DripEvent event){
        currentNewOrderNum=++currentNewOrderNum;
        setNewOrderNum();
    }

    private void setNewOrderNum() {
        if(currentNewOrderNum>0){
            dtCountDown.setText(WordUtil.getString(R.string.order_renewal_tip,currentNewOrderNum));
            dtCountDown.setVisibility(View.VISIBLE);
        }else{
            dtCountDown.setVisibility(View.GONE);
        }
    }

    private Observable<List<SnapOrderBean>> getData() {
       return MainHttpUtil.getDripList(getLastId()).compose(this.<List<SnapOrderBean>>bindToLifecycle());
    }

    private String getLastId() {
        if(snatchHallAdapter==null||snatchHallAdapter.getLastData()==null||refreshView.isRefreshing()){
            return "0";
        }
        return snatchHallAdapter.getLastData().getId();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onClick(View v) {
        refreshView.autoRefresh();
    }
}
