package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import com.alibaba.fastjson.JSON;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.MySkillAdapter;
import com.yunbao.main.bean.SkillMyBean;
import com.yunbao.main.event.UpdateSkillEvent;
import com.yunbao.main.http.MainHttpUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/7/26.
 */

public class MySkillActivity extends AbsActivity implements MySkillAdapter.ActionListener {

    public static void forward(Context context) {
        context.startActivity(new Intent(context, MySkillActivity.class));
    }

    private CommonRefreshView mRefreshView;
    private MySkillAdapter mAdapter;
    private boolean mNeedRefresh;
    private boolean mPaused;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_skill;
    }
    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.my_skill));
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 15, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<SkillMyBean>() {
            @Override
            public RefreshAdapter<SkillMyBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MySkillAdapter(mContext);
                    mAdapter.setActionListener(MySkillActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getMySkill(p, callback);
            }

            @Override
            public List<SkillMyBean> processData(String[] info) {
                List<SkillMyBean> list = JSON.parseArray(Arrays.toString(info), SkillMyBean.class);
                SkillMyBean bean = new SkillMyBean();
                bean.setType(SkillMyBean.ADD);
                list.add(bean);
                return list;
            }

            @Override
            public void onRefreshSuccess(List<SkillMyBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }
            @Override
            public void onLoadMoreSuccess(List<SkillMyBean> loadItemList, int loadItemCount) {

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

    @Override
    public void onItemClick(SkillMyBean bean) {
        EditSkillActivity.forward(mContext, bean);
    }

    @Override
    public void onAddClick() {
        ChooseSkillActivity.forward(mContext);
    }

    @Override
    protected void onDestroy() {
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSkillEvent(UpdateSkillEvent e) {
        mNeedRefresh = true;
    }

    @Override
    protected void onPause() {
        mPaused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused) {
            if (mNeedRefresh) {
                mNeedRefresh = false;
                if (mRefreshView != null) {
                    mRefreshView.initData();
                }
            }
        }
        mPaused = false;
    }
}
