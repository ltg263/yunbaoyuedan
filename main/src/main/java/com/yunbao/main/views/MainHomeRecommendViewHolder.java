package com.yunbao.main.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.event.ShowOrHideLiveRoomFloatWindowEvent;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.server.observer.LockClickObserver;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainHomeChildViewHolder;
import com.yunbao.dynamic.bean.DynamicUserBean;
import com.yunbao.im.config.CallConfig;
import com.yunbao.main.R;
import com.yunbao.main.activity.ChatRoomMoreListActivity;
import com.yunbao.main.activity.GameActivity;
import com.yunbao.main.adapter.MainHomeRecommendAdapter;
import com.yunbao.main.adapter.MainHomeRecommendClassAdapter;
import com.yunbao.main.adapter.MainHomeRecommendHeadLiveAdapter;
import com.yunbao.main.bean.BannerBean;
import com.yunbao.main.bean.ClassBean;
import com.yunbao.main.bean.commit.DressingCommitBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity 首页 推荐
 */

public class MainHomeRecommendViewHolder extends AbsMainHomeChildViewHolder implements OnItemClickListener<DynamicUserBean>, View.OnClickListener {

    private CommonRefreshView mRefreshView;
    private MainHomeRecommendAdapter mAdapter;
    private DressingCommitBean dressingCommitBean;
    private View mBannerWrap;
    private Banner mBanner;
    private List<BannerBean> mBannerList;
    private boolean mBannerNeedUpdate;
    private MainHomeRecommendHeadLiveAdapter mLiveAdapter;
    private int mCurrentPage;//当前接口请求的P
    private RecyclerView classRlv;
    private MainHomeRecommendClassAdapter mClassAdapter;
    private List<ClassBean> mClassList;
    private HttpCallback mHomeCallback;
    private View mLiveWrap;
    private RecyclerView mLiveRecyclerView;


    public MainHomeRecommendViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_recommend;
    }

    @Override
    public void init() {
        dressingCommitBean = new DressingCommitBean();
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        classRlv = findViewById(R.id.rlv_class);
        mRefreshView.setEmptyTips(WordUtil.getString(R.string.no_more_data_o));
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new MainHomeRecommendAdapter(mContext);
        mAdapter.setOnItemClickListener(MainHomeRecommendViewHolder.this);
        mRefreshView.setRecyclerViewAdapter(mAdapter);
        if (CommonAppConfig.getInstance().getIsState()==1) {
              classRlv.setVisibility(View.GONE);
         }else {
            getHomeData();
        }
        View headView = mAdapter.getHeadView();
        headView.findViewById(R.id.btn_more).setOnClickListener(this);

        mLiveRecyclerView = headView.findViewById(R.id.rlv_live);
        mLiveRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mLiveWrap = headView.findViewById(R.id.ll_live_wrapper);//聊天室区域

        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<DynamicUserBean>() {
            @Override
            public RefreshAdapter<DynamicUserBean> getAdapter() {
                return null;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                mCurrentPage = p;
                MainHttpUtil.getRecommend(dressingCommitBean.getSex(), dressingCommitBean.getAge(), dressingCommitBean.getSkill(), p, callback);
            }

            @Override
            public List<DynamicUserBean> processData(String[] info) {
                JSONObject object = JSON.parseObject(info[0]);
                List<DynamicUserBean> userList = JSON.parseArray(object.getString("userlist"), DynamicUserBean.class);
                if (mCurrentPage == 1) {
                    List<BannerBean> bannerList = JSON.parseArray(object.getString("silidelist"), BannerBean.class);
                    mBannerNeedUpdate = false;
                    if (bannerList != null && bannerList.size() > 0) {
                        if (mBannerList == null || mBannerList.size() != bannerList.size()) {
                            mBannerNeedUpdate = true;
                        } else {
                            for (int i = 0; i < mBannerList.size(); i++) {
                                BannerBean bean = mBannerList.get(i);
                                if (bean == null || !bean.isEqual(bannerList.get(i))) {
                                    mBannerNeedUpdate = true;
                                    break;
                                }
                            }
                        }
                    }
                    mBannerList = bannerList;

                    List<LiveBean> liveList = JSON.parseArray(object.getString("livelist"), LiveBean.class);
                    if (liveList == null || liveList.size() == 0) {
                        if (mLiveWrap != null && mLiveWrap.getVisibility() != View.GONE) {
                            mLiveWrap.setVisibility(View.GONE);
                        }
                    } else {
                        if (mLiveWrap != null && mLiveWrap.getVisibility() != View.VISIBLE) {
                            mLiveWrap.setVisibility(View.VISIBLE);
                        }
                        if (mLiveRecyclerView != null) {
                            if (mLiveAdapter == null) {
                                mLiveAdapter = new MainHomeRecommendHeadLiveAdapter(mContext, liveList);
                                mLiveAdapter.setOnItemClickListener(new OnItemClickListener<LiveBean>() {
                                    @Override
                                    public void onItemClick(LiveBean bean, int position) {
                                        if (CallConfig.isBusy()) {
                                            ToastUtil.show(WordUtil.getString(com.yunbao.chatroom.R.string.tip_please_close_chat_window));
                                            return;
                                        }
                                        if (CommonAppConfig.getInstance().isFloatButtonShowing()) {
                                            //之前是提示，不进入,修改为 直接进入聊天室，并关闭小窗口
                                            EventBus.getDefault().post(new ShowOrHideLiveRoomFloatWindowEvent(0));
                                        }
                                        enterRoom(bean, null);
                                    }
                                });
                                mLiveRecyclerView.setAdapter(mLiveAdapter);
                            } else {
                                mLiveAdapter.setData(liveList);
                            }
                        }
                    }
                }
                return userList != null ?
                        userList : new ArrayList<DynamicUserBean>();
            }

            @Override
            public void onRefreshSuccess(List<DynamicUserBean> list, int count) {
                if (mActionListener != null) {
                    mActionListener.onRefreshCompleted();
                }
                showBanner();
                if (mAdapter != null && mAdapter.getItemCount() > 0 ||
                        mLiveAdapter != null && mLiveAdapter.getItemCount() > 0) {
                    mRefreshView.hideEmpty();
                }
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<DynamicUserBean> loadItemList, int loadItemCount) {
            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mBannerWrap = findViewById(R.id.banner_wrap);
        mBanner = findViewById(R.id.banner);
        mBanner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                ImgLoader.display(mContext, ((BannerBean) path).getImageUrl(), imageView);
            }
        });
        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int p) {
                if (mBannerList != null) {
                    if (p >= 0 && p < mBannerList.size()) {
                        BannerBean bean = mBannerList.get(p);
                        if (bean != null) {
                            String link = bean.getLink();
                            if (!TextUtils.isEmpty(link)) {
                                WebViewActivity.forwardH5(mContext, link);
                            }
                        }
                    }
                }
            }
        });
    }


    private void showBanner() {
        if (mBanner == null) {
            return;
        }
        if (mBannerList == null || mBannerList.size() == 0) {
            if (mBannerWrap.getVisibility() != View.GONE) {
                mBannerWrap.setVisibility(View.GONE);
            }
        } else {
            if (mBannerWrap.getVisibility() != View.VISIBLE) {
                mBannerWrap.setVisibility(View.VISIBLE);
            }
            if (mBannerNeedUpdate) {
                mBanner.update(mBannerList);
            }
        }
    }

    @Override
    public void onItemClick(DynamicUserBean bean, int position) {
        forwardUserHome(bean.getId());
    }

    @Override
    public void loadData() {
        if (!isFirstLoadData()) {
            return;
        }
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Override
    public void release() {
        MainHttpUtil.cancel(MainHttpConsts.GET_RECOMMEND);
        mActionListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }


    public DressingCommitBean getDressingCommitBean() {
        if (dressingCommitBean == null) {
            dressingCommitBean = new DressingCommitBean();
        }
        return dressingCommitBean;
    }

    public void receiverConditionData(DressingCommitBean dressingCommitBean, boolean needRefresh) {
        if (!needRefresh) {
            return;
        }
        this.dressingCommitBean.copy(dressingCommitBean);
        setFirstLoadData(true);
        loadData();
    }

    private void enterRoom(final LiveBean liveBean, View view) {
        ChatRoomHttpUtil.enterRoom(liveBean.getUid(), liveBean.getStream()).subscribe(new LockClickObserver<LiveBean>(view) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onSucc(LiveBean tempLiveBean) {
                liveBean.setVotestotal(tempLiveBean.getVotestotal());
                liveBean.setChatserver(tempLiveBean.getChatserver());
                liveBean.setIsattent(tempLiveBean.getIsattent());
                liveBean.setSits(tempLiveBean.getSits());
                liveBean.setSkillid(tempLiveBean.getSkillid());
                liveBean.setIsdispatch(tempLiveBean.getIsdispatch());
                liveBean.setInviteCode(tempLiveBean.getInviteCode());
                liveBean.setExpandParm(tempLiveBean.getExpandParm());
                liveBean.setRoomCover(tempLiveBean.getRoomCover());
                RouteUtil.forwardLiveAudience(liveBean, liveBean.getType(), false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_more) {
            ChatRoomMoreListActivity.forward(mContext);
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
                        mClassList.addAll(JSON.parseArray(obj.getString("skilllist"), ClassBean.class));
                        classRlv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                        mClassAdapter = new MainHomeRecommendClassAdapter(mContext, mClassList);
                        mClassAdapter.setOnItemClickListener(new OnItemClickListener<ClassBean>() {
                            @Override
                            public void onItemClick(ClassBean bean, int position) {
                                GameActivity.forward(mContext, bean.getId(), bean.getName());
                            }
                        });
                        classRlv.setAdapter(mClassAdapter);

                    }
                }
            };
        }
        MainHttpUtil.getHome(mHomeCallback);
    }

}
