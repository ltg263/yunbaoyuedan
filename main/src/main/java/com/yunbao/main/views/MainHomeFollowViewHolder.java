package com.yunbao.main.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainHomeChildViewHolder;
import com.yunbao.dynamic.bean.DynamicUserBean;
import com.yunbao.main.R;
import com.yunbao.main.adapter.MainHomeFollowAdapter;
import com.yunbao.main.adapter.MainHomeFollowTopUserAdapter;
import com.yunbao.main.adapter.MainHomeRecommendTopAdapter;
import com.yunbao.main.bean.BannerBean;
import com.yunbao.main.bean.commit.DressingCommitBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 首页 关注
 */

public class MainHomeFollowViewHolder extends AbsMainHomeChildViewHolder implements OnItemClickListener<DynamicUserBean>, View.OnClickListener {

    private CommonRefreshView mRefreshView;
    private MainHomeRecommendTopAdapter mAdapter;
    private DressingCommitBean dressingCommitBean;
    private View ll_top_user;
    private RecyclerView mRecyclerView;
    private MainHomeFollowTopUserAdapter mFollowTopUserAdapter;
    private int mNextPage = 1;

    public MainHomeFollowViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_follow;
    }

    @Override
    public void init() {
        dressingCommitBean=new DressingCommitBean();
        ll_top_user = findViewById(R.id.ll_top_user);
        mRecyclerView = findViewById(R.id.rlv_user);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,3,GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        findViewById(R.id.btn_change_it).setOnClickListener(this);
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyTips(WordUtil.getString(R.string.no_more_data_follow));
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<DynamicUserBean>() {
            @Override
            public RefreshAdapter<DynamicUserBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainHomeRecommendTopAdapter(mContext);
                    mAdapter.setOnItemClickListener(MainHomeFollowViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getFollowList(dressingCommitBean.getSex(),dressingCommitBean.getAge(),dressingCommitBean.getSkill(),p, callback);
            }

            @Override
            public List<DynamicUserBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), DynamicUserBean.class);
            }

            @Override
            public void onRefreshSuccess(List<DynamicUserBean> adapterItemList, int allItemCount) {
                if (mActionListener != null) {
                    mActionListener.onRefreshCompleted();
                }
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<DynamicUserBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mNextPage = 1;
        updateNewPlayerEnjoy();
        if (CommonAppConfig.getInstance().getIsState()==1){
            ll_top_user.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * 新人专享
     */
    private void updateNewPlayerEnjoy(){
        MainHttpUtil.getAuthlist(mNextPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0){
                    if (info == null || info.length <= 0){
                        return;
                    }
                    JSONObject object = JSON.parseObject(info[0]);
                    mNextPage = object.getIntValue("next_p");
                    List<DynamicUserBean> list = JSON.parseArray(object.getString("list"),DynamicUserBean.class);
                    if (mFollowTopUserAdapter == null){
                        mFollowTopUserAdapter = new MainHomeFollowTopUserAdapter(mContext,list);
                        mFollowTopUserAdapter.setOnItemClickListener(new OnItemClickListener<DynamicUserBean>() {
                            @Override
                            public void onItemClick(DynamicUserBean bean, int position) {
                                forwardUserHome(bean.getId());
                            }
                        });
                        mRecyclerView.setAdapter(mFollowTopUserAdapter);
                    }else {
                        mFollowTopUserAdapter.setData(list);
                    }
                    if (list == null || list.size() == 0){
                        ll_top_user.setVisibility(View.GONE);
                    }else {
                        if (ll_top_user.getVisibility() == View.GONE&&CommonAppConfig.getInstance().getIsState()!=1){
                            ll_top_user.setVisibility(View.VISIBLE);
                        }
                    }
                }else {
                    if (ll_top_user.getVisibility() == View.VISIBLE){
                        ll_top_user.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public DressingCommitBean getDressingCommitBean() {
        if(dressingCommitBean==null){
            dressingCommitBean= new DressingCommitBean();
        }
        return dressingCommitBean;
    }

    public void receiverConditionData(DressingCommitBean dressingCommitBean, boolean needRefresh){
        if(!needRefresh){
            return;
        }
        this.dressingCommitBean.copy(dressingCommitBean);
        setFirstLoadData(true);
        loadData();
    }

    @Override
    public void loadData() {
        if (!isFirstLoadData()){
            return;
        }
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Override
    public void onItemClick(DynamicUserBean bean, int position) {
        forwardUserHome(bean.getId());
    }

    @Override
    public void release() {
        MainHttpUtil.cancel(MainHttpConsts.GET_FOLLOW_LIST);
        mActionListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_change_it){
            updateNewPlayerEnjoy();
        }
    }
}
