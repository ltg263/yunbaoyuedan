package com.yunbao.main.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.adapter.ViewPagerAdapter;
import com.yunbao.common.custom.ScaleTransitionPagerTitleView;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.main.R;
import com.yunbao.main.activity.MainActivity;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

public class MainLiveViewHolder extends AbsMainViewHolder {
    private static final int PAGE_COUNT = 2;
    private List<FrameLayout> mViewList;
    private AbsMainViewHolder[] mViewHolders;
    private MagicIndicator mIndicator;
    private ViewPager mViewPager;
    private View mBtnStartLive;
    private View mLiveTitle;

    public MainLiveViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_live_chat;
    }

    @Override
    public void init() {
        mBtnStartLive = findViewById(R.id.btn_start_live);
        mBtnStartLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonAppConfig.getInstance().getIsState()==1){
                    ToastUtil.show(R.string.teenager_live_tip);
                    return;
                }
                ((MainActivity) mContext).startLive();
            }
        });
        mViewList = new ArrayList<>();
        for (int i = 0; i < PAGE_COUNT; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewHolders = new AbsMainViewHolder[PAGE_COUNT];
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        if (PAGE_COUNT > 1) {
            mViewPager.setOffscreenPageLimit(PAGE_COUNT - 1);
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
        final String[] titles = new String[]{
                WordUtil.getString(R.string.chat_room), WordUtil.getString(R.string.live)
        };
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.textColor));
                simplePagerTitleView.setSelectedColor(0xff1e1e1e);
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setTextSize(20);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mViewPager != null) {
                            mViewPager.setCurrentItem(index);
                        }
                    }
                });
                if(index==1){
                    mLiveTitle= simplePagerTitleView;
                }
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
//                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
//                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
//                linePagerIndicator.setLineWidth(DpUtil.dp2px(18));
//                linePagerIndicator.setRoundRadius(DpUtil.dp2px(2));
//                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.global));
                return  null;
            }
        });
        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
    }


    private void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        AbsMainViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    vh = new MainLiveChildViewHolder(mContext, parent);
                } else if (position == 1) {
                    vh = new MainHomeLiveViewHolder(mContext, parent);
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
            vh.loadData();
        }
        if (mBtnStartLive != null) {
            if (position == 1) {
                if (mBtnStartLive.getVisibility() != View.VISIBLE) {
                    mBtnStartLive.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnStartLive.getVisibility() == View.VISIBLE) {
                    mBtnStartLive.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void loadData() {
        if(isFirstLoadData()){
            if(mLiveTitle!=null){
                mLiveTitle.performClick();
            }
        }else{
            if (mViewPager != null) {
                loadPageData(mViewPager.getCurrentItem());
            }
        }
    }


}


