package com.yunbao.main.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.main.activity.SkillHomeActivity;
import com.yunbao.common.bean.OrderCommentBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.main.R;
import com.yunbao.main.adapter.SkillCommentAdapter;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/7/20.
 */

public class SkillCommentViewHolder extends AbsMainViewHolder implements OnItemClickListener<OrderCommentBean> {

    private CommonRefreshView mRefreshView;
    private SkillCommentAdapter mAdapter;
    private String mToUid;
    private String mSkillId;

    public SkillCommentViewHolder(Context context, ViewGroup parentView, String touid, String skillId) {
        super(context, parentView, touid, skillId);
    }

    @Override
    protected void processArguments(Object... args) {
        if (args.length > 0) {
            mToUid = (String) args[0];
        }
        if (args.length > 1) {
            mSkillId = (String) args[1];
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_game_comment;
    }


    @Override
    public void init() {
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<OrderCommentBean>() {
            @Override
            public RefreshAdapter<OrderCommentBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new SkillCommentAdapter(mContext);
                    mAdapter.setOnItemClickListener(SkillCommentViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getSkillComment(p, mSkillId, mToUid, callback);
            }

            @Override
            public List<OrderCommentBean> processData(String[] info) {
                if (mAdapter != null) {
                    mAdapter.setTagList(((SkillHomeActivity) mContext).getTagList());
                }
                return JSON.parseArray(Arrays.toString(info), OrderCommentBean.class);
            }

            @Override
            public void onRefreshSuccess(List<OrderCommentBean> list, int count) {
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<OrderCommentBean> loadItemList, int loadItemCount) {
            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }


    @Override
    public void onItemClick(OrderCommentBean bean, int position) {
    }

    @Override
    public void loadData() {
        if (!isFirstLoadData()) {
            return;
        }
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Override
    public void release() {
        MainHttpUtil.cancel(MainHttpConsts.ORDER_COMMENT_USER);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }
}
