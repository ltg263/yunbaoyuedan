package com.yunbao.main.activity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.VisitAdapter;
import com.yunbao.main.bean.VisitBean;
import com.yunbao.main.http.MainHttpUtil;
import java.util.List;
import io.reactivex.Observable;

public class VisitActivity extends AbsActivity implements View.OnClickListener {
    private RxRefreshView<VisitBean> refreshView;
    private VisitAdapter visitAdapter;
    private TextView footView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_visit;
    }
    @Override
    protected void main() {
        super.main();
        setTitle(getString(R.string.near_visit));
        TextView tvRight=setRightTitle(WordUtil.getString(R.string.clear_empty));
        tvRight.setTextColor(Color.parseColor("#C7C7C7"));
        tvRight.setOnClickListener(this);
        refreshView = (RxRefreshView) findViewById(R.id.refreshView);
        refreshView.setItemCount(1);
        refreshView.setNoDataTip(R.string.no_people_see_u);

        RxRefreshView.ReclyViewSetting reclyViewSetting=RxRefreshView.ReclyViewSetting.createLinearSetting(this,1);
        refreshView.setReclyViewSetting(reclyViewSetting);
        refreshView.setDataListner(new RxRefreshView.DataListner<VisitBean>() {
            @Override
            public Observable<List<VisitBean>> loadData(int p) {
                return getData();
            }
            @Override
            public void compelete(List<VisitBean> data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });

        footView= (TextView) LayoutInflater.from(this).inflate(R.layout.bottom_tip,refreshView,false);
        footView.setText(R.string.visit_record_tip);

        visitAdapter=new VisitAdapter(null);
        visitAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VisitBean.UserInfo userInfo=visitAdapter.getItem(position).getUserinfo();
                if(userInfo!=null) {
                    toUsePage(userInfo.getId());
                }
            }
        });

        visitAdapter.setDataChangeListner(new BaseRecyclerAdapter.DataChangeListner<VisitBean>() {
            @Override
            public void change(List<VisitBean> t) {
                if(ListUtil.haveData(t)){
                    if(footView.getParent()==null) {
                        visitAdapter.addFooterView(footView);
                    }
                }else{
                     ViewParent viewParent=footView.getParent();
                     if (viewParent == null){
                         visitAdapter.addHeaderView(footView);
                     }
//                    if(viewParent!=null&&viewParent==visitAdapter.getFooterLayout()) {
//                        visitAdapter.removeFooterView(footView);
//                        visitAdapter.addHeaderView(footView);
//                    }
                }
            }
        });
        refreshView.setAdapter(visitAdapter);

        refreshView.initData();
    }

    private void toUsePage( String id) {
        RouteUtil.forwardUserHome(id);
    }

    public String getLastTime(){
       if(visitAdapter==null||visitAdapter.size()==0||refreshView.isRefreshing()) {
           return "0";
       }
       return visitAdapter.getLastData().getAddtime();
    }

    private Observable<List<VisitBean>> getData() {
        return MainHttpUtil.getVisit(getLastTime()).compose(this.<List<VisitBean>>bindToLifecycle());
    }

    private void clearVisitRecord(){
        MainHttpUtil.clearVisitRecord(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0){
                    refreshView.autoRefresh();
                }else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.tv_right_title){
            clearVisitRecord();
        }
    }
}
