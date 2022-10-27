package com.yunbao.main.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.TxLocationPoiBean;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.LocationAdapter;
import java.util.List;
import io.reactivex.Observable;
import static com.yunbao.common.Constants.ADDRESS;

public class LocationActivity extends AbsActivity {
    public static final int GET_LOCATION=10;

    private RxRefreshView<TxLocationPoiBean> rxRefreshView;
    private LocationAdapter locationAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_location;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.at_location));
        rxRefreshView=findViewById(R.id.refreshView);
        rxRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(this,1));
        rxRefreshView.setDataListner(new RxRefreshView.DataListner<TxLocationPoiBean>() {
            @Override
            public Observable<List<TxLocationPoiBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<TxLocationPoiBean> data) {
            }
            @Override
            public void error(Throwable e) {
                e.printStackTrace();
            }
        });

        locationAdapter=new LocationAdapter(null);
        locationAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
               TxLocationPoiBean bean= locationAdapter.getArray().get(position);
               returnSelData(bean);
            }
        });
        rxRefreshView.setAdapter(locationAdapter);
        rxRefreshView.initData();
    }

    private void returnSelData(TxLocationPoiBean bean) {
        Intent intent=getIntent();
        if(!TextUtils.isEmpty(bean.getId())) {
            intent.putExtra(ADDRESS,bean.getTitle());
        }
        setResult(RESULT_OK,intent);
        finish();
    }

    private Observable<List<TxLocationPoiBean>> getData(int p) {
       return CommonHttpUtil.obseverAddressInfoByTxLocaitonSdk(1,p,"obseverAddressInfoByTxLocaitonSdk").compose(this.<List<TxLocationPoiBean>>bindToLifecycle());
    }
}
