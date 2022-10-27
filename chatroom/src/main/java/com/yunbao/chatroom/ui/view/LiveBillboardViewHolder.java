package com.yunbao.chatroom.ui.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.view.ViewGroup;

import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.adapter.LiveBillboardAdapter;
import com.yunbao.chatroom.bean.ListBean;

import java.util.List;

import io.reactivex.Observable;

public abstract class LiveBillboardViewHolder extends AbsMainViewHolder {
    private RxRefreshView<ListBean> mRefreshView;
    private LiveBillboardAdapter mLiveBillboardAdapter;
    private String mUnit;


    public LiveBillboardViewHolder(Context context, ViewGroup parentView, String unit) {
        super(context, parentView, unit);
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
        mUnit = (String) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_single_refresh;
    }

    @Override
    public void init() {
        mRefreshView = (RxRefreshView) findViewById(R.id.refreshView);
        mLiveBillboardAdapter = new LiveBillboardAdapter(null);
        mLiveBillboardAdapter.setUnit(mUnit);
        mRefreshView.setAdapter(mLiveBillboardAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position < 3) {
                    return 1;
                }
                return 3;
            }
        });
        mRefreshView.setLayoutManager(gridLayoutManager);
        mRefreshView.setLoadMoreEnable(false);
        mRefreshView.setDataListner(new RxRefreshView.DataListner<ListBean>() {
            @Override
            public Observable<List<ListBean>> loadData(int p) {
                return getData(p);
            }

            @Override
            public void compelete(List<ListBean> data) {
                while (data.size() < 3) {
                    //为了展示前三位的  虚位以待
                    ListBean listBean = new ListBean();
                    listBean.setEmpty(true);
                    data.add(listBean);
                }
            }

            @Override
            public void error(Throwable e) {
            }
        });
        mRefreshView.setRefreshDataLisnter(new RxRefreshView.RefreshDataLisnter<ListBean>() {
            @Override
            public void refreshData(List<ListBean> t) {
                if (!ListUtil.haveData(t)) {
                    return;
                }
                int size = t.size();
                if (size == 1) {
                    t.add(1, t.get(0));
                    ListBean listBean = new ListBean();
                    listBean.setEmpty(true);
                    t.set(0, listBean);
                } else {
                    ListBean var1 = t.get(0);
                    ListBean var2 = t.get(1);
                    t.set(0, var2);
                    t.set(1, var1);
                }
            }
        });
    }

    @Override
    public void loadData() {
        super.loadData();
        if (isFirstLoadData()) {
            mRefreshView.initData();
        }
    }

    public abstract Observable<List<ListBean>> getData(int p);
}
