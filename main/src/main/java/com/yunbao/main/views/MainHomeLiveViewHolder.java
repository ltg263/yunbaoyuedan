package com.yunbao.main.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.LiveClassBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.live.bean.LiveBeanReal;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.live.utils.LiveStorge;
import com.yunbao.main.R;
import com.yunbao.main.activity.LiveClassActivity;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.main.adapter.MainHomeLiveAdapter;
import com.yunbao.main.adapter.MainHomeLiveClassAdapter;
import com.yunbao.main.http.MainHttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity 首页 直播
 */

public class MainHomeLiveViewHolder extends AbsMainViewHolder implements OnItemClickListener<LiveBeanReal>, View.OnClickListener {

    private RecyclerView mClassRecyclerViewDialog;
    private View mShadow;
    private View mBtnDismiss;
    private CommonRefreshView mRefreshView;
    private RecyclerView mClassRecyclerViewTop;
    private MainHomeLiveAdapter mAdapter;
    private ObjectAnimator mShowAnimator;
    private ObjectAnimator mHideAnimator;


    public MainHomeLiveViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_live;
    }

    @Override
    public void init() {
        mShadow = findViewById(R.id.shadow);
        mBtnDismiss = findViewById(R.id.btn_dismiss);
        mBtnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canClick()) {
                    if (mShowAnimator != null) {
                        mShowAnimator.cancel();
                    }
                    if (mHideAnimator != null) {
                        mHideAnimator.start();
                    }
                }
            }
        });
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_live);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 2;
                }
                return 1;
            }
        });
        mRefreshView.setLayoutManager(gridLayoutManager);
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mAdapter = new MainHomeLiveAdapter(mContext);
        mAdapter.setOnItemClickListener(MainHomeLiveViewHolder.this);
        mRefreshView.setRecyclerViewAdapter(mAdapter);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LiveBeanReal>() {
            @Override
            public RefreshAdapter<LiveBeanReal> getAdapter() {
                return null;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                LiveHttpUtil.getHot(p, callback);
            }

            @Override
            public List<LiveBeanReal> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);

                return JSON.parseArray(obj.getString("list"), LiveBeanReal.class);
            }

            @Override
            public void onRefreshSuccess(List<LiveBeanReal> list, int count) {
                if (CommonAppConfig.LIVE_ROOM_SCROLL) {
                    LiveStorge.getInstance().put(Constants.LIVE_HOME, list);
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
        View headView = mAdapter.getHeadView();

        mClassRecyclerViewTop = headView.findViewById(R.id.classRecyclerView_top);
        mClassRecyclerViewTop.setHasFixedSize(true);
        mClassRecyclerViewTop.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mClassRecyclerViewDialog = (RecyclerView) findViewById(R.id.classRecyclerView_dialog);
        mClassRecyclerViewDialog.setHasFixedSize(true);
        mClassRecyclerViewDialog.setLayoutManager(new GridLayoutManager(mContext, 5, GridLayoutManager.VERTICAL, false));
        List<LiveClassBean> classList = null;
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            classList = configBean.getLiveClass();
        }
        if (classList == null) {
            classList = new ArrayList<>();
        }
        List<LiveClassBean> targetList = null;
        if (classList.size() <= 6) {
            targetList = classList;
        } else {
            targetList = new ArrayList<>();
            targetList.addAll(classList.subList(0, 5));
            LiveClassBean bean = new LiveClassBean();
            bean.setId(-1);
            bean.setName(WordUtil.getString(R.string.all));
            targetList.add(bean);
        }
        MainHomeLiveClassAdapter topAdapter = new MainHomeLiveClassAdapter(mContext, targetList, false);
        topAdapter.setOnItemClickListener(new OnItemClickListener<LiveClassBean>() {
            @Override
            public void onItemClick(LiveClassBean bean, int position) {
                if (!canClick()) {
                    return;
                }
                if (bean.getId() == -1) {//全部分类
                    showClassListDialog();
                }
                else {
                    LiveClassActivity.forward(mContext, bean.getId(), bean.getName());
                }
            }
        });
        if (mClassRecyclerViewTop != null) {
            mClassRecyclerViewTop.setAdapter(topAdapter);
        }
        MainHomeLiveClassAdapter dialogAdapter = new MainHomeLiveClassAdapter(mContext, classList, true);
        dialogAdapter.setOnItemClickListener(new OnItemClickListener<LiveClassBean>() {
            @Override
            public void onItemClick(LiveClassBean bean, int position) {
                if (!canClick()) {
                    return;
                }
                    LiveClassActivity.forward(mContext, bean.getId(), bean.getName());

            }
        });
        mClassRecyclerViewDialog.setAdapter(dialogAdapter);
        mClassRecyclerViewDialog.post(new Runnable() {
            @Override
            public void run() {
                initAnim();
            }
        });
    }




    /**
     * 初始化弹窗动画
     */
    private void initAnim() {
        final int height = mClassRecyclerViewDialog.getHeight();
        mClassRecyclerViewDialog.setTranslationY(-height);
        mShowAnimator = ObjectAnimator.ofFloat(mClassRecyclerViewDialog, "translationY", 0);
        mShowAnimator.setDuration(200);
        mHideAnimator = ObjectAnimator.ofFloat(mClassRecyclerViewDialog, "translationY", -height);
        mHideAnimator.setDuration(200);
        TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();
        mShowAnimator.setInterpolator(interpolator);
        mHideAnimator.setInterpolator(interpolator);
        ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float rate = 1 + ((float) animation.getAnimatedValue() / height);
                mShadow.setAlpha(rate);
            }
        };
        mShowAnimator.addUpdateListener(updateListener);
        mHideAnimator.addUpdateListener(updateListener);
        mHideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mBtnDismiss != null && mBtnDismiss.getVisibility() == View.VISIBLE) {
                    mBtnDismiss.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * 显示分类列表弹窗
     */
    private void showClassListDialog() {
        if (mBtnDismiss != null && mBtnDismiss.getVisibility() != View.VISIBLE) {
            mBtnDismiss.setVisibility(View.VISIBLE);
        }
        if (mShowAnimator != null) {
            mShowAnimator.start();
        }
    }


    @Override
    public void onItemClick(LiveBeanReal bean, int position) {
        ((MainActivity) mContext).watchLive(bean, Constants.LIVE_HOME, position);

    }

    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Override
    public void release() {
        MainHttpUtil.cancel(LiveHttpConsts.GET_HOT);
        if (mHideAnimator != null) {
            mHideAnimator.cancel();
        }
        if (mShowAnimator != null) {
            mShowAnimator.cancel();
        }
        mShowAnimator = null;
        mHideAnimator = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
//        if (id == R.id.btn_profit) {
//            RankActivity.forward(mContext, 0);
//        } else if (id == R.id.btn_con) {
//            RankActivity.forward(mContext, 1);
//        } else if (id == R.id.btn_more_recom) {
//            LiveRecommendActivity.forward(mContext);
//        }
    }

    private boolean mPaused;

    @Override
    public void onResume() {
        super.onResume();
        if(mPaused){
            mPaused=false;
            loadData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused=true;
    }
}
