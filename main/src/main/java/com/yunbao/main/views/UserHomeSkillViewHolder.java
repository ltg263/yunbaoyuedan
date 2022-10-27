package com.yunbao.main.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.main.R;
import com.yunbao.main.activity.OrderMakeActivity;
import com.yunbao.main.activity.SkillHomeActivity;
import com.yunbao.main.activity.UserHomeActivity;
import com.yunbao.main.adapter.UserHomeSkillAdapter;
import com.yunbao.main.bean.SkillHomeBean;
import com.yunbao.main.http.MainHttpUtil;

import java.util.List;

/**
 * Created by cxf on 2019/7/20.
 * 个人主页 技能
 */

public class UserHomeSkillViewHolder extends AbsMainViewHolder implements UserHomeSkillAdapter.ActionListener {

    private CommonRefreshView mRefreshView;
    private UserHomeSkillAdapter mAdapter;
    private boolean mSelf;

    private String mTouid;

    public UserHomeSkillViewHolder(Context context, ViewGroup parentView, boolean self,String touid) {
        super(context, parentView, self,touid);
    }

    @Override
    protected void processArguments(Object... args) {
        if (args.length > 0) {
            mSelf = (boolean) args[0];
            mTouid = (String) args[1];
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_user_home_skill;
    }


    @Override
    public void init() {
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 10, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<SkillHomeBean>() {
            @Override
            public RefreshAdapter<SkillHomeBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new UserHomeSkillAdapter(mContext,mSelf);
                    mAdapter.setActionListener(UserHomeSkillViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if (!TextUtils.isEmpty(mTouid)){
                    MainHttpUtil.getUserHome(mTouid,callback);
                }

            }

            @Override
            public List<SkillHomeBean> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                return JSON.parseArray(obj.getString("list"), SkillHomeBean.class);
            }

            @Override
            public void onRefreshSuccess(List<SkillHomeBean> list, int count) {
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<SkillHomeBean> loadItemList, int loadItemCount) {
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
    public void onItemClick(SkillHomeBean bean) {
        SkillHomeActivity.forward(mContext, bean.getUid(), bean.getSkillId());
    }

    @Override
    public void onOrderClick(SkillHomeBean bean) {
        if (mContext != null && mContext instanceof UserHomeActivity) {
            UserBean u = ((UserHomeActivity) mContext).getUserBean();
            if (u != null) {
                OrderMakeActivity.forward(mContext, u, bean);
            }
        }
    }
}
