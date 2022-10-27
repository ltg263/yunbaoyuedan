package com.yunbao.chatroom.ui.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.adapter.LiveOnLineAdapter;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.bean.LiveUserBean;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.chatroom.ui.dialog.LiveUserDialogFragment;
import java.util.List;
import io.reactivex.Observable;

public class LiveOnLineViewHolder extends AbsMainViewHolder {
    private RxRefreshView<LiveUserBean> mRefreshView;
    private LiveOnLineAdapter mLiveOnLineAdapter;
    private LiveBean mLiveBean;



    public LiveOnLineViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_single_refresh;
    }
    @Override
    public void init() {
        mRefreshView = (RxRefreshView) findViewById(R.id.refreshView);
        mLiveOnLineAdapter=new LiveOnLineAdapter(null);

        mRefreshView.setAdapter(mLiveOnLineAdapter);
        RxRefreshView.ReclyViewSetting reclyViewSetting=RxRefreshView.ReclyViewSetting.createLinearSetting(mContext,1);
        mRefreshView.setReclyViewSetting(reclyViewSetting);
        mRefreshView.setDataListner(new RxRefreshView.DataListner<LiveUserBean>() {
            @Override
            public Observable<List<LiveUserBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<LiveUserBean> data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });
        mLiveOnLineAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LiveUserBean liveUserBean=mLiveOnLineAdapter.getItem(position);
                LiveUserDialogFragment.showLiveUserFragment(mFragmentActivity,liveUserBean);
            }
        });
        mLiveBean= LiveActivityLifeModel.getByContext(mFragmentActivity, LiveActivityLifeModel.class).getLiveBean();
    }

    @Override
    public void loadData() {
        super.loadData();
        mRefreshView.initData();
    }

    private Observable<List<LiveUserBean>> getData(int p) {
            String liveUid=mLiveBean==null?null:mLiveBean.getUid();
            String stream=mLiveBean==null?null:mLiveBean.getStream();
         return  ChatRoomHttpUtil.getLiveUserList(liveUid,stream,p);
    }
}
