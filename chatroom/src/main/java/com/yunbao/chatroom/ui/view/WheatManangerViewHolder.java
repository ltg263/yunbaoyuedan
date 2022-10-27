package com.yunbao.chatroom.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.bean.LiveAnthorBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.business.liveobsever.DataChangeListner;
import com.yunbao.common.business.liveobsever.LifeObjectHolder;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.views.AbsViewHolder2;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.adapter.WheatManangerAdapter;
import com.yunbao.chatroom.business.behavior.SkPowerBehavior;
import com.yunbao.chatroom.business.behavior.factory.CacheBehaviorFactory;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import java.util.List;

public class WheatManangerViewHolder extends AbsViewHolder2 implements DataChangeListner<List<LiveAnthorBean>> {
    private WheatManangerAdapter mUpperManangerAdapter;
    private RecyclerView mRecyclerView;
    private LiveActivityLifeModel mLiveActivityLifeModel;
    private SkPowerBehavior mSkPowerBehavior;

    public WheatManangerViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    public WheatManangerViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_only_reclyview;
    }
    @Override
    public void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.reclyView);
        mUpperManangerAdapter=new WheatManangerAdapter(null);
        mRecyclerView.setAdapter(mUpperManangerAdapter);

        RxRefreshView.ReclyViewSetting reclyViewSetting=RxRefreshView.ReclyViewSetting.createLinearSetting(mContext,1);
        reclyViewSetting.settingRecyclerView(mRecyclerView);

        mUpperManangerAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                onManangerAdapterItemChildClick(adapter,view,position);
            }
        });
        initModel();
    }


    private void initModel() {
        mLiveActivityLifeModel= LifeObjectHolder.getByContext(getActivity(),LiveActivityLifeModel.class);
        if(mLiveActivityLifeModel!=null){
           mLiveActivityLifeModel.getLiveSeatDataObsever().addObsever(this);
           mUpperManangerAdapter.setData(mLiveActivityLifeModel.getSeatList());
        }
        mSkPowerBehavior= CacheBehaviorFactory.getInstance().getSkPowerBehavior(getActivity());
    }

    private void onManangerAdapterItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int id=view.getId();
        LiveAnthorBean anthorBean=mUpperManangerAdapter.getItem(position);
        if(id==R.id.btn_wheat_control){
            anthorBean.toggle();
            switchUserWheat(anthorBean,position);
        }else if(id==R.id.btn_close_wheat){
            downUserWheat(anthorBean.getUserBean(),position);
        }
    }

    /*切换麦上用户的发言权限*/
    private void switchUserWheat(LiveAnthorBean liveAnthorBean, int position) {
        if(mSkPowerBehavior!=null){
           mSkPowerBehavior.sendWheatIsOpen(liveAnthorBean.getUserBean(),liveAnthorBean.isOpenWheat());
        }
    }

    /*强制用户下麦*/
    private void downUserWheat(UserBean userBean, int position) {
        if(mSkPowerBehavior!=null){
           mSkPowerBehavior.downWheat(userBean,position+1,false);
        }

    }
    public void setNotCanSwitchWheat(boolean isOpen){
        if(mUpperManangerAdapter!=null){
            mUpperManangerAdapter.setNotCanSwitchWheat(isOpen);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mLiveActivityLifeModel!=null){
            mLiveActivityLifeModel.getLiveSeatDataObsever().removeObsever(this);
        }
    }

    @Override
    public void change(List<LiveAnthorBean> liveAnthorBeanList) {
        mUpperManangerAdapter.setData(liveAnthorBeanList);
    }
}
