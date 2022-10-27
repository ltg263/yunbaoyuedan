package com.yunbao.main.views;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yunbao.common.adapter.ViewPagerAdapter;
import com.yunbao.common.custom.ScaleTransitionPagerTitleView;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.views.AbsMainHomeChildViewHolder;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.main.R;
import com.yunbao.main.custom.LinePagerIndicator;
import com.yunbao.main.interfaces.MainAppBarLayoutListener;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/2/20.
 */

public abstract class AbsMainHomeParentParentViewHolder extends AbsMainViewHolder {

    private AppBarLayout mAppBarLayout;
    protected ViewPager mViewPager;
    private MagicIndicator mIndicator;
    protected AbsMainHomeParentViewHolder[] mViewHolders;
    private MainAppBarLayoutListener mAppBarLayoutListener;
    private boolean mPaused;
    protected List<FrameLayout> mViewList;
    private int mAppLayoutOffestY;

    public AbsMainHomeParentParentViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        if(mAppBarLayout!=null){
            mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (mAppBarLayoutListener != null) {
                        if (verticalOffset > mAppLayoutOffestY) {
                            mAppBarLayoutListener.onOffsetChanged(false);
                        } else if (verticalOffset < mAppLayoutOffestY) {
                            mAppBarLayoutListener.onOffsetChanged(true);
                        }
                        mAppLayoutOffestY = verticalOffset;
                    }
                }
            });
        }

        mViewList = new ArrayList<>();
        int pageCount = getPageCount();
        for (int i = 0; i < pageCount; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewHolders = new AbsMainHomeParentViewHolder[pageCount];
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

        final String[] titles = getTitles();
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
                return createIPagerIndicator(context);
            }
        });
        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
    }

    protected IPagerIndicator createIPagerIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        linePagerIndicator.setXOffset(DpUtil.dp2px(10));
        linePagerIndicator.setYOffset(DpUtil.dp2px(3));
        linePagerIndicator.setLineWidth(DpUtil.dp2px(5));
        linePagerIndicator.setLineHeight(DpUtil.dp2px(2));
        linePagerIndicator.setRoundRadius(DpUtil.dp2px(10));
        //linePagerIndicator.setY(-10);
        return linePagerIndicator;
    }

    protected IPagerTitleView createIPagerTitleView(Context context,final int index) {
        SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
        simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.textColor));
        simplePagerTitleView.setSelectedColor(0xff1e1e1e);
        simplePagerTitleView.setText(getTitles()[index]);
        simplePagerTitleView.setTextSize(20);
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

    /**
     * 设置AppBarLayout滑动监听
     */
    public void setAppBarLayoutListener(MainAppBarLayoutListener appBarLayoutListener) {
        mAppBarLayoutListener = appBarLayoutListener;
    }
    @Override
    public void loadData() {
        if (mViewPager != null) {
            loadPageData(mViewPager.getCurrentItem());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShowed() && mPaused) {
            loadData();
        }
        mPaused = false;
    }


    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppBarLayoutListener = null;
        super.onDestroy();
    }

    public void setCurrentPage(int position) {
        if (mViewPager == null) {
            return;
        }
        if (mViewPager.getCurrentItem() == position) {
            loadPageData(position);
        } else {
            mViewPager.setCurrentItem(position, false);
        }
    }

    protected abstract void loadPageData(int position);

    protected abstract int getPageCount();

    protected abstract String[] getTitles();


}
