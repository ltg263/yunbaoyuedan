package com.yunbao.main.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoaderInterface;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.custom.ScaleTransitionPagerTitleView;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.SystemUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainHomeChildViewHolder;
import com.yunbao.chatroom.bean.LiveBannerBean;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.chatroom.ui.activity.LivePieHallActivity;
import com.yunbao.chatroom.ui.view.LiveTabulationViewHolder;
import com.yunbao.main.R;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;
import java.util.List;
import io.reactivex.Observable;

/**
 * @author apple
 */
public class MainLiveChildViewHolder extends AbsMainHomeParentViewHolder implements View.OnClickListener, AbsMainHomeChildViewHolder.ActionListener {
    private SmartRefreshLayout mHomeRefreshLayout;
    private ClassicsHeader mHeader;
    private Banner mBanner;
    private LinearLayout mBtnSpatch;

    private LiveTabulationViewHolder mCollectViewHolder;
    private LiveTabulationViewHolder mHotViewHolder;
    private LiveTabulationViewHolder mFriendViewHolder;
    private LiveTabulationViewHolder mChatViewHolder;
    private LiveTabulationViewHolder mSongViewHolder;
    private int mPageTitleWidth;


    public MainLiveChildViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_child;
    }

    @Override
    public void init() {
        mPageTitleWidth= SystemUtil.getWindowsPixelWidth(mFragmentActivity)/5;
        super.init();
        mHomeRefreshLayout = (SmartRefreshLayout) findViewById(R.id.home_refresh_layout);
        mHeader = (ClassicsHeader) findViewById(R.id.header);
        mBanner = (Banner) findViewById(R.id.banner);
        mBtnSpatch = (LinearLayout) findViewById(R.id.btn_spatch);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mBtnSpatch.setOnClickListener(this);
        initSmartRefreshLayout();
        ChatRoomHttpUtil.getLiveBanner().subscribe(new DefaultObserver<List<LiveBannerBean>>() {
            @Override
            public void onNext(List<LiveBannerBean> liveBannerBeans) {
                initBanner(liveBannerBeans);
            }
        });
        if (mViewPager != null) {
            mViewPager.setCurrentItem(1);
        }
        if (CommonAppConfig.getInstance().getIsState()==1){
            mBtnSpatch.setVisibility(View.GONE);
        }
    }

    private void initBanner(final List<LiveBannerBean> liveBannerBeans) {
            //设置banner样式(显示圆形指示器)
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
            //设置指示器位置（指示器居右）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
            //设置图片加载器
        mBanner.setImageLoader(new ImageLoaderInterface<ImageView>() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                LiveBannerBean liveBannerBean= (LiveBannerBean) path;
                ImgLoader.display(context,liveBannerBean.getImage(),imageView);
            }
            @Override
            public ImageView createImageView(Context context) {
                ImageView imageView=new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                return imageView;
            }
        });
        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if(liveBannerBeans!=null&&liveBannerBeans.size()>position){
                   LiveBannerBean liveBannerBean= liveBannerBeans.get(position);
                   String url=liveBannerBean.getUrl();
                   if(!TextUtils.isEmpty(url)){
                       WebViewActivity.forward(mContext,liveBannerBean.getUrl(),false);
                   }
                }
            }
        });
            //设置图片集合
        mBanner.setImages(liveBannerBeans);
            //设置banner动画效果
//        banner.setBannerAnimation(Transformer.DepthPage);
            //设置标题集合（当banner样式有显示title时）
//        banner.setBannerTitles(titles);
            //设置自动轮播，默认为true
//        banner.isAutoPlay(true);
            //设置轮播时间
        mBanner.setDelayTime(5000);
            //banner设置方法全部调用完毕时最后调用
        mBanner.start();
    }


    @Override
    protected IPagerIndicator createIPagerIndicator(Context context) {
       // LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
       //linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
//        linePagerIndicator.setLineWidth(DpUtil.dp2px(15));
//        linePagerIndicator.setLineHeight(DpUtil.dp2px(3));
//        linePagerIndicator.setRoundRadius(DpUtil.dp2px(2));
//        linePagerIndicator.setColors(ContextCompat.getColor(mContext, R.color.global));

        return null;
    }

    @Override
    protected IPagerTitleView createIPagerTitleView(Context context,final int index) {
        SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
        simplePagerTitleView.setPadding(40,0,0,0);
        simplePagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.gray1));
        simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.textColor));
        simplePagerTitleView.setText(getTitles()[index]);
        simplePagerTitleView.setTextSize(18);
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
    protected void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        AbsMainHomeChildViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mCollectViewHolder = new LiveTabulationViewHolder(mContext, parent) {
                        @Override
                        public Observable<List<LiveBean>> getData(int p) {
                            return ChatRoomHttpUtil.getLiveAttenList(p);
                        }
                    };
                    mCollectViewHolder.setActionListener(MainLiveChildViewHolder.this);
                    vh =  mCollectViewHolder;
                }else if(position ==1){
                    mHotViewHolder = new LiveTabulationViewHolder(mContext, parent) {
                        @Override
                        public Observable<List<LiveBean>> getData(int p) {
                            return ChatRoomHttpUtil.getLiveList(p,0);
                        }
                    };
                    mHotViewHolder.setActionListener(MainLiveChildViewHolder.this);
                    vh = mHotViewHolder;
                }else if(position ==2){
                    mFriendViewHolder = new LiveTabulationViewHolder(mContext, parent) {
                        @Override
                        public Observable<List<LiveBean>> getData(int p) {
                            return ChatRoomHttpUtil.getLiveList(p, Constants.LIVE_TYPE_FRIEND);
                        }
                    };
                    mFriendViewHolder.setActionListener(MainLiveChildViewHolder.this);
                    vh = mFriendViewHolder;            ;
                }else if(position ==3){
                    mChatViewHolder = new LiveTabulationViewHolder(mContext, parent) {
                        @Override
                        public Observable<List<LiveBean>> getData(int p) {
                            return ChatRoomHttpUtil.getLiveList(p, Constants.LIVE_TYPE_CHAT);
                        }
                    };
                    mChatViewHolder.setActionListener(MainLiveChildViewHolder.this);
                    vh =mChatViewHolder;            ;
                }
                else if(position ==4){
                    mSongViewHolder = new LiveTabulationViewHolder(mContext, parent) {
                        @Override
                        public Observable<List<LiveBean>> getData(int p) {
                            return ChatRoomHttpUtil.getLiveList(p, Constants.LIVE_TYPE_SONG);
                        }
                    };
                    mSongViewHolder.setActionListener(MainLiveChildViewHolder.this);
                    vh = mSongViewHolder;            ;
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
    protected int getPageCount() {
        return getTitles().length;
    }

    @Override
    protected String[] getTitles() {
        return new String[]{
                WordUtil.getString(R.string.collect),
                WordUtil.getString(R.string.hot),
                WordUtil.getString(R.string.make_friends),
                WordUtil.getString(R.string.chatting),
                WordUtil.getString(R.string.choose_song)
        };
    }

    private void initSmartRefreshLayout() {
        mHomeRefreshLayout = (SmartRefreshLayout) findViewById(R.id.home_refresh_layout);
        mHomeRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);//是否在列表不满一页时候开启上拉加载功能
        mHomeRefreshLayout.setEnableFooterFollowWhenLoadFinished(true);//是否在全部加载结束之后Footer跟随内容
        mHomeRefreshLayout.setEnableOverScrollBounce(false);//设置是否开启越界回弹功能（默认true）
        mHomeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mFirstLoadData = true;
                loadData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        toSpatch();
    }

    private void toSpatch() {
        startActivity(LivePieHallActivity.class);
    }

    @Override
    public void onRefreshCompleted() {
        if(mHomeRefreshLayout!=null){
            mHomeRefreshLayout.finishRefresh();
        }
    }
}
