package com.yunbao.main.activity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.FootAdapter;
import com.yunbao.main.bean.FootBean;
import com.yunbao.main.bean.VisitBean;
import com.yunbao.main.http.MainHttpUtil;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class FootActivity extends AbsActivity implements View.OnClickListener {
    private RxRefreshView<FootBean> refreshView;
    private TextView footView;
    private FootAdapter footAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_foot;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(getString(R.string.browse_footprints));
        TextView tvRight=setRightTitle(WordUtil.getString(R.string.clear_empty));
        tvRight.setTextColor(Color.parseColor("#C7C7C7"));
        tvRight.setOnClickListener(this);

        refreshView = (RxRefreshView) findViewById(R.id.refreshView);
        refreshView.setNoDataTip(R.string.no_people_foot);
        RxRefreshView.ReclyViewSetting reclyViewSetting=RxRefreshView.ReclyViewSetting.createLinearSetting(this,1);
        refreshView.setReclyViewSetting(reclyViewSetting);
        refreshView.setItemCount(1);
        refreshView.setDataListner(new RxRefreshView.DataListner<FootBean>() {
            @Override
            public Observable<List<FootBean>> loadData(int p) {
                return getData();
            }
            @Override
            public void compelete(List<FootBean> data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });

        footView= (TextView) LayoutInflater.from(this).inflate(R.layout.bottom_tip,refreshView,false);
        footView.setText(R.string.foot_record_tip);
        footAdapter=new FootAdapter(null);
        footAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FootBean.UserInfo userInfo=footAdapter.getItem(position).getUserinfo();
                if(userInfo!=null) {
                    toUsePage(userInfo.getId());
                }
            }
        });

        footAdapter.setDataChangeListner(new BaseRecyclerAdapter.DataChangeListner<FootBean>() {
            @Override
            public void change(List<FootBean> t) {
                if(ListUtil.haveData(t)){
                  if(footView.getParent()==null) {
                      footAdapter.addFooterView(footView);
                  }
                }else{
                   ViewParent viewParent=footView.getParent();
                    if (viewParent == null){
                        footAdapter.addHeaderView(footView);
                    }
//                   if(viewParent!=null&&viewParent==footAdapter.getFooterLayout()) {
//                       footAdapter.removeFooterView(footView);
//                   }
                }
            }
        });
        refreshView.setAdapter(footAdapter);
        refreshView.initData();
    }



    private void toUsePage(String id) {
        RouteUtil.forwardUserHome(id);
    }
    public String getLastTime(){
        if(footAdapter==null||footAdapter.size()==0||refreshView.isRefreshing()) {
            return "0";
        }
        return footAdapter.getLastData().getAddtime();
    }

    private Observable<List<FootBean>> getData() {
        return MainHttpUtil.getFootView(getLastTime()).compose(this.<List<FootBean>>bindToLifecycle());
    }

    @Override
    public void onClick(View v) {
        if(!ClickUtil.canClick()){
            return;
        }
        int id=v.getId();
        if(id==R.id.tv_right_title){
            clearFoot();
        }
    }

    private void clearFoot() {
        MainHttpUtil.footClear().compose(this.<Boolean>bindToLifecycle()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                 if(aBoolean){
                    refreshView.autoRefresh();
                 }
            }
        });
    }
}
