package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.radio.CheckEntity;
import com.yunbao.common.adapter.radio.RadioAdapter;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.JsonUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.event.OrderCancelEvent;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import static android.os.Build.ID;

public class FlashOrderCancleActivity extends AbsActivity {

    private RecyclerView reclyView;
    private TextView tvOrderTip;
    private RadioAdapter<CheckEntity> radioAdapter;
    private String orderId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_flash_order_cancle;
    }

    @Override
    protected void main() {
        super.main();
        orderId=getIntent().getStringExtra(ID);
        setTitle(WordUtil.getString(R.string.order_cancel));
        reclyView = (RecyclerView) findViewById(R.id.reclyView);
        tvOrderTip = (TextView) findViewById(R.id.tv_order_tip);
        RxRefreshView.ReclyViewSetting reclyViewSetting=RxRefreshView.ReclyViewSetting.createLinearSetting(this,1);
        reclyViewSetting.settingRecyclerView(reclyView);
        radioAdapter=new RadioAdapter(null);
        reclyView.setAdapter(radioAdapter);
        getReason();
    }

    private void getReason() {
        MainHttpUtil.getDripCancel().compose(this.<JSONObject>bindToLifecycle()).subscribe(new DefaultObserver<JSONObject>() {
            @Override
            public void onNext(JSONObject jsonObject) {
                parseData(jsonObject);
            }
        });
    }


    private void parseData(JSONObject jsonObject) {
        tvOrderTip.setText(jsonObject.getString("tips"));
        JSONArray jsonArra=jsonObject.getJSONArray("list");
        String listString=jsonArra.toJSONString();
        List<CheckEntity> checkArray=JsonUtil.getJsonToList(listString,CheckEntity.class);
        radioAdapter.setData(checkArray);
        if (checkArray != null && checkArray.size() > 0){
            radioAdapter.setDefaultSelect(checkArray.get(0).getId());
        }

    }

    public void commit(final View view){
        CheckEntity checkEntity=radioAdapter.getSelectData();
        if(checkEntity==null){
            ToastUtil.show(R.string.please_sel_cancle_reason);
            return;
        }
        view.setClickable(false);
        MainHttpUtil.cancelDrip(orderId,checkEntity.getContent()).
                compose(this.<Boolean>bindToLifecycle()).subscribe(new DefaultObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                view.setClickable(!aBoolean);
                if(aBoolean){
                    EventBus.getDefault().post(new OrderCancelEvent(true));
                    finish();
                }
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                view.setClickable(true);
            }
        });
    }

    public static void forward(Context context,String orderId){
        Intent intent=new Intent(context,FlashOrderCancleActivity.class);
        intent.putExtra(ID,orderId);
        context.startActivity(intent);
    }

}
