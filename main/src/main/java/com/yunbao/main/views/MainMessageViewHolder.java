package com.yunbao.main.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.yunbao.common.adapter.ViewPagerAdapter;
import com.yunbao.common.custom.ScaleTransitionPagerTitleView;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.im.http.ImHttpUtil;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.main.R;
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
 * Created by cxf on 2019/4/1.
 * 首页 消息
 */

public class MainMessageViewHolder extends AbsMainViewHolder implements View.OnClickListener {
    private static final int PAGE_COUNT = 1;
    private List<FrameLayout> mViewList;
    private AbsMainViewHolder[] mViewHolders;
    private MagicIndicator mIndicator;
    private ViewPager mViewPager;
    private MainMessageMsgViewHolder mMsgViewHolder;

    public MainMessageViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_msg;
    }

    @Override
    public void init() {
        mViewList = new ArrayList<>();
        for (int i = 0; i < PAGE_COUNT; i++) {
            FrameLayout frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mViewList.add(frameLayout);
        }
        mViewHolders = new AbsMainViewHolder[PAGE_COUNT];
        findViewById(R.id.btn_ignore).setOnClickListener(this);
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
                WordUtil.getString(R.string.im_msg)
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
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {

                return null;
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
                    mMsgViewHolder = new MainMessageMsgViewHolder(mContext, parent);
                    vh = mMsgViewHolder;
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
    }


    @Override
    public void loadData() {
        if (mViewPager != null) {
            loadPageData(mViewPager.getCurrentItem());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ignore) {
            ignoreUnReadCount();

        }
    }

    /**
     * 忽略未读
     */
    private void ignoreUnReadCount() {
        String unReadCount = ImMessageUtil.getInstance().getAllUnReadMsgCount();
        boolean hasSystemMsg = SpUtil.getInstance().getBooleanValue(SpUtil.HAS_SYSTEM_MSG);
        ImHttpUtil.getSystemMessageList(1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {

            }
        });
        if ("0".equals(unReadCount) && !hasSystemMsg) {
            ToastUtil.show(WordUtil.getString(R.string.im_msg_ignore_unread_3));
//            DialogUitl.showSimpleTipDialog(mContext, WordUtil.getString(R.string.im_msg_ignore_unread_3));
            return;
        }
        ImMessageUtil.getInstance().markAllConversationAsRead();
        if (mMsgViewHolder != null){
            mMsgViewHolder.ignoreUnReadCount();
        }
        ToastUtil.show(R.string.im_msg_ignore_unread_2);
    }

}
