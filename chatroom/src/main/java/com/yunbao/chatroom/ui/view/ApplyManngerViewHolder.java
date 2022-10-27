package com.yunbao.chatroom.ui.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.adapter.UpWheatApplyAdapter;
import com.yunbao.chatroom.business.behavior.ApplyResultBehavior;
import com.yunbao.chatroom.business.behavior.factory.CacheBehaviorFactory;
import com.yunbao.chatroom.business.socket.SuccessListner;
import java.util.List;
import io.reactivex.Observable;

public abstract class ApplyManngerViewHolder extends AbsMainViewHolder {
    private RxRefreshView<UserBean> mRefreshView;
    private UpWheatApplyAdapter mUpWheatApplyAdapter;
    private LifecycleProvider mLifecycleProvider;
    private SuccessListner mSuccessListner;

    public ApplyManngerViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_single_refresh;
    }

    @Override
    public void init() {
        mRefreshView = (RxRefreshView) findViewById(R.id.refreshView);
        mUpWheatApplyAdapter=new UpWheatApplyAdapter(null);
        mRefreshView.setAdapter(mUpWheatApplyAdapter);
        mUpWheatApplyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int id=view.getId();
                UserBean userBean=mUpWheatApplyAdapter.getItem(position);
                if(id==R.id.btn_wheat_agree){
                    handleApplyResult(userBean,true,view);
                }else if(id==R.id.btn_wheat_refuse){
                    handleApplyResult(userBean,false,view);
                    mRefreshView.initData();
                }
            }
        });
        RxRefreshView.ReclyViewSetting reclyViewSetting=RxRefreshView.ReclyViewSetting.createLinearSetting(mContext,1);
        mRefreshView.setReclyViewSetting(reclyViewSetting);
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

    }

    public abstract Observable<List<UserBean>> getData(int p);
    private void handleApplyResult(UserBean userBean, boolean apply,View view) {
        ApplyResultBehavior applyResultBehavior= CacheBehaviorFactory.getInstance().getApplyResultBehavior(getActivity());
        if(applyResultBehavior==null){
            return;
        }
        if(apply){
            applyResultBehavior.agree(userBean, mLifecycleProvider, view,mSuccessListner);
        }else {
            applyResultBehavior.refuse(userBean,mLifecycleProvider,mSuccessListner);
        }
    }
    public void setSuccessListner(SuccessListner successListner) {
        mSuccessListner = successListner;
    }
    public void setLifecycleProvider(LifecycleProvider lifecycleProvider) {
        mLifecycleProvider = lifecycleProvider;
    }

    @Override
    public void loadData() {
        super.loadData();
        mRefreshView.initData();
    }
}
