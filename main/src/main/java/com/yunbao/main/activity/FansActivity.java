package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.FansUserBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.event.FollowEvent;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.FansAdapter;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/29.
 * 我的粉丝 TA的粉丝
 */

public class FansActivity extends AbsActivity implements OnItemClickListener<FansUserBean> {

    public static void forward(Context context, String toUid) {
        Intent intent = new Intent(context, FansActivity.class);
        intent.putExtra(Constants.TO_UID, toUid);
        context.startActivity(intent);
    }

    private CommonRefreshView mRefreshView;
    private FansAdapter mAdapter;
    private String mToUid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fans;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.fans));
        mToUid = getIntent().getStringExtra(Constants.TO_UID);
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        mRefreshView = findViewById(R.id.refreshView);
        if (mToUid.equals(CommonAppConfig.getInstance().getUid())) {
            mRefreshView.setEmptyLayoutId(R.layout.view_no_data_fans);
        } else {
            mRefreshView.setEmptyLayoutId(R.layout.view_no_data_fans_2);
        }
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<FansUserBean>() {
            @Override
            public RefreshAdapter<FansUserBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new FansAdapter(mContext);
                    mAdapter.setOnItemClickListener(FansActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getFansList(mToUid, p, callback);
            }

            @Override
            public List<FansUserBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), FansUserBean.class);
            }

            @Override
            public void onRefreshSuccess(List<FansUserBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<FansUserBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (e != null && mAdapter != null) {
            mAdapter.setFollow(e.getToUid(), e.getAttention());
        }
    }


    @Override
    protected void onDestroy() {
        CommonHttpUtil.cancel(CommonHttpConsts.SET_ATTENTION);
        MainHttpUtil.cancel(MainHttpConsts.GET_FANS_LIST);
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }


    @Override
    public void onItemClick(FansUserBean u, int position) {
        RouteUtil.forwardUserHome(u.getId());
    }
}
