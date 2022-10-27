package com.yunbao.main.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;

import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.views.AbsMainHomeChildViewHolder;
import com.yunbao.main.R;
import com.yunbao.main.adapter.MainHomeNearAdapter;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 首页 附近
 */

public class MainHomeNearViewHolder extends AbsMainHomeChildViewHolder implements OnItemClickListener<UserBean> {

    private CommonRefreshView mRefreshView;
    private MainHomeNearAdapter mAdapter;


    public MainHomeNearViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_near;
    }

    @Override
    public void init() {
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<UserBean>() {
            @Override
            public RefreshAdapter<UserBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeNearAdapter(mContext);
                    mAdapter.setOnItemClickListener(MainHomeNearViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getNear(p, callback);
            }

            @Override
            public List<UserBean> processData(String[] info) {

                List<UserBean> list = new ArrayList<UserBean>();
                for (int i = 0; i < 20; i++) {
                    list.add(new UserBean());
                }
                return list;

            }

            @Override
            public void onRefreshSuccess(List<UserBean> list, int count) {
                if (mActionListener != null) {
                    mActionListener.onRefreshCompleted();
                }
            }
            @Override
            public void onRefreshFailure() {

            }
            @Override
            public void onLoadMoreSuccess(List<UserBean> loadItemList, int loadItemCount) {

            }
            @Override
            public void onLoadMoreFailure() {

            }
        });
    }


    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Override
    public void onItemClick(UserBean bean, int position) {
        forwardUserHome(bean.getId());
    }


    @Override
    public void release() {
        MainHttpUtil.cancel(MainHttpConsts.GET_NEAR);
        mActionListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

}
