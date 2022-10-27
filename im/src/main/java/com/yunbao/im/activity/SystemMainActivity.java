package com.yunbao.im.activity;


import android.os.Bundle;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.server.observer.DialogObserver;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.R;
import com.yunbao.im.adapter.SysMainAdapter;
import com.yunbao.im.bean.IMLiveBean;
import com.yunbao.im.utils.ImMessageUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import io.reactivex.Observable;

public class SystemMainActivity extends AbsActivity {
    private RxRefreshView<IMLiveBean> mRefreshView;
    private SysMainAdapter mSysMainAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_system_main;
    }

    @Override
    protected void main(Bundle savedInstanceState) {
        super.main(savedInstanceState);
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
        setTitle(WordUtil.getString(R.string.im_system_main));
        mRefreshView = (RxRefreshView) findViewById(R.id.refreshView);
        mSysMainAdapter=new SysMainAdapter(null);
        mRefreshView.setDataListner(new RxRefreshView.DataListner<IMLiveBean>() {
            @Override
            public Observable<List<IMLiveBean>> loadData(int p) {
                return getData();
            }
            @Override
            public void compelete(List<IMLiveBean> data) {

            }
            @Override
            public void error(Throwable e) {
            }
        });
        mRefreshView.setAdapter(mSysMainAdapter);
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(this,10));
        mSysMainAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (CommonAppConfig.getInstance().isFloatButtonShowing()){
                    ToastUtil.show(WordUtil.getString(R.string.tip_please_close_chat_window));
                    return;
                }
                IMLiveBean liveBean=mSysMainAdapter.getItem(position);
                enterRoom(liveBean,view);
            }
        });
        mRefreshView.initData();
    }
    private void enterRoom(final LiveBean liveBean,View view) {
        CommonHttpUtil.enterRoom(liveBean.getUid(),liveBean.getStream()).subscribe(new DialogObserver<LiveBean>(this) {
            @Override
            public void onNext(LiveBean tempLiveBean) {
                liveBean.setVotestotal(tempLiveBean.getVotestotal());
                liveBean.setChatserver(tempLiveBean.getChatserver());
                liveBean.setIsattent(tempLiveBean.getIsattent());
                liveBean.setSits(tempLiveBean.getSits());
                liveBean.setSkillid(tempLiveBean.getSkillid());
                liveBean.setIsdispatch(tempLiveBean.getIsdispatch());
                liveBean.setRoomCover(tempLiveBean.getRoomCover());
                liveBean.setType(Constants.LIVE_TYPE_DISPATCH);
                RouteUtil.forwardLiveAudience(liveBean, Constants.LIVE_TYPE_DISPATCH,true);
            }
        });
    }

    private Observable<List<IMLiveBean>> getData() {
       return ImMessageUtil.getInstance().geSpatchList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegisterLiveBean(IMLiveBean liveBean ){
        if(mSysMainAdapter!=null){
           mSysMainAdapter.addData(0,liveBean);
           mRefreshView.scrollPosition(0);
        }
//        SpUtil.getInstance().setBooleanValue(SpUtil.LIVE_BRO,false);
//        ImMessageUtil.getInstance().markAllMessagesAsRead(CommonAppConfig.getInstance().getConfig().getAdmin_dispatch(), true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
    }
}
