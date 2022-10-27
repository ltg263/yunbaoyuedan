package com.yunbao.main.activity;

import android.os.Bundle;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.event.BlackEvent;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.ResourceUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.BlackListAdapter;
import com.yunbao.main.http.MainHttpUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import io.reactivex.Observable;

public class BlackListActivity extends AbsActivity implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {
    private RxRefreshView<UserBean> mRefreshView;
    private BlackListAdapter mBlackListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.blacklist));
        mRefreshView =findViewById(R.id.refreshView);
        mRefreshView.setNoDataTip(getString(R.string.no_black_list));
        mRefreshView.setNoDataIcon(ResourceUtil.getDrawable(R.mipmap.icon_no_black,false));
        mRefreshView.setNoDataTextColor(R.color.textColor2);

        mBlackListAdapter=new BlackListAdapter(null);
        mBlackListAdapter.setOnItemChildClickListener(this);
        mBlackListAdapter.setOnItemClickListener(this);
        mRefreshView.setAdapter(mBlackListAdapter);


        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(this,0));
        mRefreshView.setDataListner(new RxRefreshView.DataListner<UserBean>() {
            @Override
            public Observable<List<UserBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<UserBean> data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });
        mRefreshView.initData();
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
    }

    private Observable<List<UserBean>> getData(int p) {
        return MainHttpUtil.getBlackList(p);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_black_list;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBlackEvent(BlackEvent blackEvent){
        String toUid=blackEvent.getToUid();
        if(blackEvent.getIsBlack()==0&&mBlackListAdapter!=null){
           int index= mBlackListAdapter.contain(toUid);
           if(index>-1){
             mBlackListAdapter.remove(index);
             if(mBlackListAdapter.size()<=0&&mRefreshView!=null){
                mRefreshView.statusChange(RxRefreshView.STATE_NO_DATA);
             }
           }

        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if(mBlackListAdapter==null){
           return;
        }
        UserBean userBean=mBlackListAdapter.getItem(position);
          int id=view.getId();
          if(id==R.id.btn_remove){
             remove(userBean,view,position);
          }
    }


    private void remove(UserBean userBean, View view, int position) {
        if(!ClickUtil.canClick()){
            return;
        }
        CommonHttpUtil.setBlack(userBean.getId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * Callback method to be invoked when an item in this RecyclerView has
     * been clicked.
     *
     * @param adapter  the adpater
     * @param view     The itemView within the RecyclerView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     */

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            if(mBlackListAdapter==null){
                return;
            }
        UserBean userBean=mBlackListAdapter.getItem(position);
        RouteUtil.forwardUserHome(userBean.getId());
    }
}

