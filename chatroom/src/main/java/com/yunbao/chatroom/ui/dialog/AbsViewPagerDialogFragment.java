package com.yunbao.chatroom.ui.dialog;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.yunbao.common.adapter.ViewPagerAdapter;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.SystemUtil;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.chatroom.R;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;
import java.util.List;

public abstract class AbsViewPagerDialogFragment extends AbsDialogFragment {
    private MagicIndicator mMagicIndicator;
    protected ViewPager mViewPager;
    private AbsMainViewHolder[] mViewHolders;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_view_pager;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }
    @Override
    protected boolean canCancel() {
        return true;
    }
    @Override
    public void init() {
        super.init();
        mMagicIndicator = (MagicIndicator) findViewById(R.id.magicIndicator);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        final String[] titles = getTitles();
        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles.length;
            }
            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.gray1));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.textColor));
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setTextSize(15);
                simplePagerTitleView.getPaint().setFakeBoldText(true);
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
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setXOffset(DpUtil.dp2px(10));
                linePagerIndicator.setLineWidth(DpUtil.dp2px(15));
                linePagerIndicator.setRoundRadius(DpUtil.dp2px(2));
                linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.global));
                return linePagerIndicator;
            }
        });

        mViewHolders = createViewHolder();
        if(mViewHolders!=null){
            List<View> viewList=ListUtil.transForm(mViewHolders, View.class, new ListUtil.TransFormListner<AbsMainViewHolder, View>() {
                @Override
                public View transform(AbsMainViewHolder absMainViewHolder) {
                    return absMainViewHolder.getContentView();
                }
            });
          mViewPager.setAdapter(new ViewPagerAdapter(viewList));
        }
          mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i) {
                loadPageData(i);
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
        loadPageData(0);
    }

    private void loadPageData(int position){
        mViewHolders[position].loadData();
    }

    protected abstract AbsMainViewHolder[] createViewHolder();

    public abstract String[] getTitles();
    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) (SystemUtil.getWindowsPixelHeight(getActivity())*0.7);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }
}
