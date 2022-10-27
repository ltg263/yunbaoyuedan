package com.yunbao.main.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.adapter.ViewPagerAdapter;
import com.yunbao.common.custom.ScaleTransitionPagerTitleView;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainHomeChildViewHolder;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.main.R;
import com.yunbao.main.activity.FlashOrderActivity;
import com.yunbao.main.activity.SearchActivity;
import com.yunbao.main.bean.SkillClassBean;
import com.yunbao.main.bean.commit.DressingCommitBean;
import com.yunbao.main.custom.LinePagerIndicator;
import com.yunbao.main.event.OpenDrawEvent;
import com.yunbao.main.http.MainHttpUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sky.L on 2021-07-14
 * 首页
 */
public class MainHomeViewHolder extends AbsMainViewHolder implements View.OnClickListener {
    private static final String TAG = "MainHomeViewHolder";
    private ViewPager mViewPager;
    private MagicIndicator mIndicator;
    private AbsMainHomeChildViewHolder[] mViewHolders;
    private List<FrameLayout> mViewList;
    private HttpCallback mHomeCallback;
    private List<SkillClassBean> mClassList;
    private MainHomeFollowViewHolder mHomeFollowViewHolder;
    private MainHomeRecommendViewHolder mHomeRecommendViewHolder;
    private MainHomeGameChildViewHolder[] mHomeGameChildViewHolders;
    private int mCurrentPosition;
    private View mBtnFilter;
    private View mBtnOrder;

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_new;
    }

    public MainHomeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {
        mBtnFilter = findViewById(R.id.btn_filter);
        mBtnOrder = findViewById(R.id.btn_order);
        findViewById(R.id.btn_search).setOnClickListener(this);
        mBtnFilter.setOnClickListener(this);
        mBtnOrder.setOnClickListener(this);
        getHomeData();
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }

    }

    private void getHomeData() {
        if (mHomeCallback == null) {
            mHomeCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        mClassList = new ArrayList<>();
                        SkillClassBean followBean = new SkillClassBean();
                        followBean.setName(WordUtil.getString(R.string.follow));
                        followBean.setId("-1");
                        SkillClassBean recommendBean = new SkillClassBean();
                        recommendBean.setName(WordUtil.getString(R.string.recommend));
                        recommendBean.setId("-2");
                        mClassList.add(followBean);
                        mClassList.add(recommendBean);
                        if (CommonAppConfig.getInstance().getIsState()!=1) {
                            mClassList.addAll(JSON.parseArray(obj.getString("skilllist"), SkillClassBean.class));
                        }
                        mViewList = new ArrayList<>();
                        int pageCount = mClassList.size();
                        for (int i = 0; i < pageCount; i++) {
                            FrameLayout frameLayout = new FrameLayout(mContext);
                            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            mViewList.add(frameLayout);
                        }
                        mHomeGameChildViewHolders = new MainHomeGameChildViewHolder[pageCount];
                        mViewHolders = new AbsMainHomeChildViewHolder[pageCount];
                        mViewPager = (ViewPager) findViewById(R.id.viewPager);
                        if (pageCount > 1) {
                            mViewPager.setOffscreenPageLimit(pageCount - 1);
                        }
                        mViewPager.setAdapter(new ViewPagerAdapter(mViewList));
                        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            }
                            @Override
                            public void onPageSelected(int position) {
                                loadPageData(position);
                            }
                            @Override
                            public void onPageScrollStateChanged(int state) {
                            }
                        });
                        mIndicator = (MagicIndicator) findViewById(R.id.indicator);
                        final String[] titles = new String[mClassList.size()];
                        for (int i = 0;i < mClassList.size();i++){
                            titles[i] = mClassList.get(i).getId();
                        }
                        CommonNavigator commonNavigator = new CommonNavigator(mContext);
                        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
                            @Override
                            public int getCount() {
                                return titles.length;
                            }
                            @Override
                            public IPagerTitleView getTitleView(Context context, final int index) {
                                return createIPagerTitleView(context,index);
                            }
                            @Override
                            public IPagerIndicator getIndicator(Context context) {
                                return null;
                            }
                        });
                        mIndicator.setNavigator(commonNavigator);
                        ViewPagerHelper.bind(mIndicator, mViewPager);
                        mViewPager.setCurrentItem(1);
                        loadPageData(1);
                    }
                }
            };
        }
        MainHttpUtil.getHome(mHomeCallback);
    }

    protected IPagerIndicator createIPagerIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        linePagerIndicator.setXOffset(-15);
        linePagerIndicator.setLineWidth(DpUtil.dp2px(15));
        linePagerIndicator.setLineHeight(DpUtil.dp2px(10));
        linePagerIndicator.setRoundRadius(DpUtil.dp2px(10));
        linePagerIndicator.setY(-10);
        return linePagerIndicator;
    }

    protected IPagerTitleView createIPagerTitleView(Context context,final int index) {
        SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
        simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.black));
        simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.black));
        simplePagerTitleView.setText(mClassList.get(index).getName());
        simplePagerTitleView.setTextSize(19);
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(index);
                }
            }
        });
        return simplePagerTitleView;
    }

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
                    mHomeFollowViewHolder = new MainHomeFollowViewHolder(mContext, parent);
                    vh = mHomeFollowViewHolder;
                } else if(position ==1){
                    mHomeRecommendViewHolder= new MainHomeRecommendViewHolder(mContext, parent);
                    vh = mHomeRecommendViewHolder;
                }else {
                    mHomeGameChildViewHolders[position] = new MainHomeGameChildViewHolder(mContext,parent,mClassList.get(position));
                    vh = mHomeGameChildViewHolders[position];
                }
                if (vh == null) {
                    return;
                }
                mViewHolders[position] = vh;
                vh.addToParent();
                vh.subscribeActivityLifeCycle();
            }
        }
        if (vh != null) {
            if (CommonAppConfig.getInstance().getIsState()==1){
                mBtnFilter.setVisibility(View.GONE);
                mBtnOrder.setVisibility(View.GONE);
            }else {
                if (position == 0 || position == 1) {
                    mBtnFilter.setVisibility(View.VISIBLE);
                } else {
                    mBtnFilter.setVisibility(View.INVISIBLE);
                }
            }
            vh.loadData();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_search) {
            SearchActivity.forward(mContext);
        }else if (i == R.id.btn_filter){
            openDressLayout();
        }else if (i == R.id.btn_order){
            startActivity(FlashOrderActivity.class);
        }
    }

    private void openDressLayout() {
        DressingCommitBean dressingCommitBean = getSelectDynamicUserBean();
        dressingCommitBean.setFrom(DressingCommitBean.MAIN_HOME_PEIWAN);
        EventBus.getDefault().post(new OpenDrawEvent(dressingCommitBean));
    }

    public DressingCommitBean getSelectDynamicUserBean(){
        if(mViewHolders!=null&&mViewHolders.length>mCurrentPosition){
            if (mCurrentPosition == 0){
                return mHomeFollowViewHolder.getDressingCommitBean();
            }else if (mCurrentPosition == 1){
                return mHomeRecommendViewHolder.getDressingCommitBean();
            }
        }
        return new DressingCommitBean();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverCondition(DressingCommitBean dressingCommitBean){
        if (!DressingCommitBean.MAIN_HOME_PEIWAN.equals(dressingCommitBean.getFrom())){
            return;
        }
        if(mViewHolders!=null&&mViewHolders.length>0&&dressingCommitBean!=null){
            int length=mViewHolders.length;
            for(int i=0;i<length;i++){
                if (i == 0){
                    if (mHomeFollowViewHolder != null){
                        mHomeFollowViewHolder.receiverConditionData(dressingCommitBean,i==mCurrentPosition);
                    }
                }else if (i == 1){
                    if (mHomeRecommendViewHolder != null){
                        mHomeRecommendViewHolder.receiverConditionData(dressingCommitBean,i==mCurrentPosition);
                    }
                }
            }

        }
    }

    @Override
    public void onDestroy() {
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }
}
