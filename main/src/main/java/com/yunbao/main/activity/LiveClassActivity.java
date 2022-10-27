package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.live.bean.LiveBeanReal;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.utils.LiveStorge;
import com.yunbao.main.R;
import com.yunbao.main.adapter.LiveClassAdapter;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.presenter.CheckLivePresenter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/10/27.
 */

public class LiveClassActivity extends AbsActivity implements OnItemClickListener<LiveBeanReal> {

    private int mClassId;
    private CommonRefreshView mRefreshView;
    private LiveClassAdapter mAdapter;
    private CheckLivePresenter mCheckLivePresenter;
    private String mKey;

    public static void forward(Context context, int classId, String className) {
        Intent intent = new Intent(context, LiveClassActivity.class);
        intent.putExtra(Constants.CLASS_ID, classId);
        intent.putExtra(Constants.CLASS_NAME, className);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_class;
    }

    @Override
    protected void main() {
        Intent intent = getIntent();
        mClassId = intent.getIntExtra(Constants.CLASS_ID, 0);
        mKey = StringUtil.contact(Constants.LIVE_CLASS_PREFIX, String.valueOf(mClassId));
        String liveClassName = intent.getStringExtra(Constants.CLASS_NAME);
        setTitle(liveClassName);
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live_class);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LiveBeanReal>() {
            @Override
            public RefreshAdapter<LiveBeanReal> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new LiveClassAdapter(mContext);
                    mAdapter.setOnItemClickListener(LiveClassActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                LiveHttpUtil.getClassLive(mClassId, p, callback);
            }

            @Override
            public List<LiveBeanReal> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), LiveBeanReal.class);
            }

            @Override
            public void onRefreshSuccess(List<LiveBeanReal> list, int listCount) {
                if (CommonAppConfig.LIVE_ROOM_SCROLL) {
                    LiveStorge.getInstance().put(mKey, list);
                }
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<LiveBeanReal> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }

    @Override
    public void onItemClick(LiveBeanReal bean, int position) {
        watchLive(bean, position);
    }


    /**
     * 观看直播
     */
    public void watchLive(LiveBeanReal LiveBeanReal, int position) {
//        if (!FloatWindowHelper.checkVoice(true)) {
//            return;
//        }
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        if (CommonAppConfig.LIVE_ROOM_SCROLL) {
            mCheckLivePresenter.watchLive(LiveBeanReal, mKey, position);
        } else {
            mCheckLivePresenter.watchLive(LiveBeanReal);
        }
    }

    @Override
    protected void onDestroy() {
        LiveStorge.getInstance().remove(mKey);
        MainHttpUtil.cancel(LiveHttpConsts.GET_CLASS_LIVE);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

}
