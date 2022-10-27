package com.yunbao.main.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainHomeChildViewHolder;
import com.yunbao.dynamic.bean.DynamicUserBean;
import com.yunbao.dynamic.http.DynamicHttpUtil;
import com.yunbao.main.R;
import com.yunbao.main.activity.AllClassActivity;
import com.yunbao.main.activity.FlashOrderActivity;
import com.yunbao.main.activity.SearchActivity;
import com.yunbao.main.activity.SkillUserActivity;
import com.yunbao.main.adapter.MainHomeClassAdapter;
import com.yunbao.main.bean.BannerBean;
import com.yunbao.main.bean.SkillClassBean;
import com.yunbao.main.bean.commit.DressingCommitBean;
import com.yunbao.main.event.OpenDrawEvent;
import com.yunbao.main.http.MainHttpUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import io.reactivex.Observable;

/**
 * Created by cxf on 2018/9/22.
 *  首页子页面  陪玩父页面
 */

public class MainHomeParentPlayViewHolder extends AbsMainHomeParentViewHolder implements View.OnClickListener, AbsMainHomeChildViewHolder.ActionListener, OnItemClickListener<SkillClassBean> {

    private MainHomeDynamicViewHolder mRecommendViewHolder;
    private MainHomeAttentionDynamicViewHolder mAttentionViewHolder;
    private MainHomeDynamicViewHolder mNewViewHolder;

    private RecyclerView mClassRecyclerView;
    private SmartRefreshLayout mSmartRefreshLayout;
    private MainHomeClassAdapter mGameClassAdapter;
    private ImageView mSlideLeft;
    private ImageView mSlideRight;

    private HttpCallback mHomeCallback;
    private List<BannerBean> mBannerList;
    private int mCurrentPosition;

    public MainHomeParentPlayViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home;
    }

    @Override
    public void init() {
        super.init();
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
        findViewById(R.id.btn_search).setOnClickListener(this);
        findViewById(R.id.img_dressing).setOnClickListener(this);
        findViewById(R.id.btn_publish).setOnClickListener(this);

        mSmartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.home_refresh_layout);
        mSmartRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);//是否在列表不满一页时候开启上拉加载功能
        mSmartRefreshLayout.setEnableFooterFollowWhenLoadFinished(true);//是否在全部加载结束之后Footer跟随内容
        mSmartRefreshLayout.setEnableOverScrollBounce(false);//设置是否开启越界回弹功能（默认true）
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mFirstLoadData = true;
                loadData();
            }
        });
        ClassicsHeader header = (ClassicsHeader) findViewById(R.id.header);
        header.setAccentColor(ContextCompat.getColor(mContext, R.color.textColor));
        mClassRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_class);
        mClassRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 5, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 20, 15);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mClassRecyclerView.addItemDecoration(decoration);
        mSlideLeft = (ImageView) findViewById(R.id.slide_left);
        mSlideRight = (ImageView) findViewById(R.id.slide_right);
        mSlideLeft.setOnClickListener(this);
        mSlideRight.setOnClickListener(this);
        findViewById(R.id.v_order).setOnClickListener(this);
    }

    private void getHomeData() {
        if (mHomeCallback == null) {
            mHomeCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        if (mClassRecyclerView != null) {
                            List<SkillClassBean> classList = JSON.parseArray(obj.getString("skilllist"), SkillClassBean.class);
                            if (classList.size() > 10) {
                                classList = classList.subList(0, 9);
                                SkillClassBean more = new SkillClassBean(true);
                                classList.add(more);
                            }
                            if (mGameClassAdapter == null) {
                                mGameClassAdapter = new MainHomeClassAdapter(mContext, classList);
                                mGameClassAdapter.setOnItemClickListener(MainHomeParentPlayViewHolder.this);
                                mClassRecyclerView.setAdapter(mGameClassAdapter);
                            } else {
                                mGameClassAdapter.setList(classList);
                            }
                        }
                        List<BannerBean> bannerList = JSON.parseArray(obj.getString("silidelist"), BannerBean.class);
                        mBannerList = bannerList;
                        if (mSlideLeft != null) {
                            if (bannerList.size() > 0) {
                                ImgLoader.display(mContext, bannerList.get(0).getImageUrl(), mSlideLeft);
                            } else {
                                mSlideLeft.setImageDrawable(null);
                            }
                        }
                        if (mSlideRight != null) {
                            if (bannerList.size() > 1) {
                                ImgLoader.display(mContext, bannerList.get(1).getImageUrl(), mSlideRight);
                            } else {
                                mSlideRight.setImageDrawable(null);
                            }
                        }
                    }
                }
            };
        }
        MainHttpUtil.getHome(mHomeCallback);
    }

    @Override
    public void loadData() {
        if (!isFirstLoadData()) {
            return;
        }
        getHomeData();
        super.loadData();
    }


    @Override
    protected void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        this.mCurrentPosition=position;
        AbsMainHomeChildViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mRecommendViewHolder = new MainHomeDynamicViewHolder(mContext, parent){
                        @Override
                        public Observable<List<DynamicUserBean>> getData(String sex, String age, String skillid, int p) {
                            return DynamicHttpUtil.getUserDynamicRecom(sex,age,skillid,p);
                        }
                    };
                    mRecommendViewHolder.setActionListener(this);
                    vh = mRecommendViewHolder;
                }else if(position ==1){
                    mAttentionViewHolder= new MainHomeAttentionDynamicViewHolder(mContext, parent) {
                        @Override
                        public Observable<List<DynamicUserBean>> getData(String sex, String age, String skillid, int p) {
                            return DynamicHttpUtil.getUserDynamicFollow(sex,age,skillid,p);
                        }
                    };
                    mAttentionViewHolder.setActionListener(this);
                    vh = mAttentionViewHolder;
                }else if(position ==2){
                    mNewViewHolder= new MainHomeDynamicViewHolder(mContext, parent) {
                        @Override
                        public Observable<List<DynamicUserBean>> getData(String sex, String age, String skillid, int p) {
                            return DynamicHttpUtil.getUserAuthlist(sex,age,skillid,p);
                        }
                    };
                    mNewViewHolder.setActionListener(this);
                    vh = mNewViewHolder               ;
                }
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.subscribeActivityLifeCycle();
                vh.setActionListener(this);
            }
        }
        if (vh != null) {
            vh.loadData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverCondition(DressingCommitBean dressingCommitBean){
        if(mViewHolders!=null&&mViewHolders.length>0&&dressingCommitBean!=null){
          int length=mViewHolders.length;
          for(int i=0;i<length;i++){
              if (i == 0){
                  if (mRecommendViewHolder != null){
                      mRecommendViewHolder.receiverConditionData(dressingCommitBean,i==mCurrentPosition);
                  }
              }else if (i == 1){
                  if (mAttentionViewHolder != null){
                      mAttentionViewHolder.receiverConditionData(dressingCommitBean,i==mCurrentPosition);
                  }
              }else if (i == 2){
                  if (mNewViewHolder != null){
                      mNewViewHolder.receiverConditionData(dressingCommitBean,i==mCurrentPosition);
                  }
              }
          }

        }
    }

    @Override
    protected int getPageCount() {
        return 3;
    }

    @Override
    protected String[] getTitles() {
        return new String[]{
                WordUtil.getString(R.string.recommend),
                WordUtil.getString(R.string.follow),
                WordUtil.getString(R.string.is_new)
        };
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_search) {
            SearchActivity.forward(mContext);
        } else if (i == R.id.slide_left) {
            forwardAd(0);
        } else if (i == R.id.slide_right) {
            forwardAd(1);
        }else if(i== R.id.img_dressing){
            openDressLayout();
        }else if(i== R.id.btn_publish){
            publishDynamic();
        }else if (i == R.id.v_order) {
            startActivity(FlashOrderActivity.class);
        }


    }

    private void publishDynamic() {
        RouteUtil.forwardPubDynamics();
    }

    public DressingCommitBean getSelectDynamicUserBean(){
        if(mViewHolders!=null&&mViewHolders.length>mCurrentPosition){
            if (mCurrentPosition == 0){
                return mRecommendViewHolder.getDressingCommitBean();
            }else if (mCurrentPosition == 1){
                return mAttentionViewHolder.getDressingCommitBean();
            }else if (mCurrentPosition == 2){
                return mNewViewHolder.getDressingCommitBean();
            }
        }
        return new DressingCommitBean();

    }

    private void openDressLayout() {
        DressingCommitBean dressingCommitBean = getSelectDynamicUserBean();
        dressingCommitBean.setFrom(DressingCommitBean.MAIN_HOME_PEIWAN);
        EventBus.getDefault().post(new OpenDrawEvent(dressingCommitBean));
    }

    private void forwardAd(int index) {
        if (mBannerList != null && mBannerList.size() > index) {
            BannerBean bannerBean = mBannerList.get(index);
            String link = bannerBean.getLink();
            if (!TextUtils.isEmpty(link)) {
                WebViewActivity.forwardNoLanguage(mContext, link, false);
            }
        }
    }


    @Override
    public void onRefreshCompleted() {
        if (mSmartRefreshLayout != null) {
            mSmartRefreshLayout.finishRefresh(true);
        }
    }

    @Override
    public void onItemClick(SkillClassBean bean, int position) {
        if (bean.isMore()) {
            AllClassActivity.forward(mContext);
        } else {
            SkillUserActivity.forward(mContext, bean);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
    }
}
