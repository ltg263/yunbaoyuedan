package com.yunbao.main.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.main.R;
import com.yunbao.main.activity.UserHomeActivity;
import com.yunbao.main.adapter.UserHomeProfileAdapter;

/**
 * Created by cxf on 2019/7/20.
 * 个人主页 资料
 */

public class UserHomeProfileViewHolder extends AbsMainViewHolder {

    private RecyclerView mRecyclerView;
    private UserHomeProfileAdapter mAdapter;

    public UserHomeProfileViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_user_home_profile;
    }


    @Override
    public void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new UserHomeProfileAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void loadData() {
        if (!mFirstLoadData) {
            return;
        }
        if (mRecyclerView == null) {
            return;
        }
        if (mContext != null && mContext instanceof UserHomeActivity) {
            UserHomeActivity userHomeActivity = ((UserHomeActivity) mContext);
            UserBean u = userHomeActivity.getUserBean();
            JSONObject obj = userHomeActivity.getUserObj();
            if (mAdapter != null && u != null && obj != null) {
                mAdapter.setData(obj.getString("des"),
                        StringUtil.contact(WordUtil.getString(R.string.nickname), "：", u.getUserNiceName()),
                        StringUtil.contact("ID：   ", u.getId()),
                        StringUtil.contact(WordUtil.getString(R.string.age), "：", u.getAge()));
                mFirstLoadData = false;
            }
        }

    }
}
