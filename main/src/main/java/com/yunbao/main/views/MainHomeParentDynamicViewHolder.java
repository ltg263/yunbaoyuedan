package com.yunbao.main.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainHomeChildViewHolder;
import com.yunbao.main.R;
import com.yunbao.main.bean.commit.DressingCommitBean;
import com.yunbao.main.event.OpenDrawEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Sky.L on 2020-09-24
 * 首页子页面  动态父页面
 */
public class MainHomeParentDynamicViewHolder extends AbsMainHomeParentViewHolder1 implements View.OnClickListener {
    private MainRecommendDynamicViewHolder mRecommendViewHolder;
    private MainFollowDynamicViewHolder mFollowViewHolder;
    private MainNewestDynamicViewHolder mNewestViewHolder;

    private int mCurrentPosition;
    private ImageView mImgDressing;

    public MainHomeParentDynamicViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {
        super.init();
        mImgDressing=findViewById(R.id.img_dressing);
        mImgDressing.setOnClickListener(this);
        findViewById(R.id.btn_publish).setOnClickListener(this);
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
        if (CommonAppConfig.getInstance().getIsState()==1){
            mImgDressing.setVisibility(View.GONE);
        }
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
                    mRecommendViewHolder = new MainRecommendDynamicViewHolder(mContext, parent);
                    vh = mRecommendViewHolder;
                }
                else if(position ==1){
                    mFollowViewHolder= new MainFollowDynamicViewHolder(mContext, parent);
                    vh = mFollowViewHolder;
                }else if(position ==2){
                    mNewestViewHolder= new MainNewestDynamicViewHolder(mContext, parent);
                    vh = mNewestViewHolder;
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
    protected int getLayoutId() {
        return R.layout.view_main_home_parent_dynamic;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverCondition(DressingCommitBean dressingCommitBean){
        if (!DressingCommitBean.MAIN_HOME_DYNAMIC.equals(dressingCommitBean.getFrom())){
            return;
        }
        if(mViewHolders!=null&&mViewHolders.length>0&&dressingCommitBean!=null){
            int length=mViewHolders.length;
            for(int i=0;i<length;i++){
                if (i == 0){
                    if (mRecommendViewHolder != null){
                        mRecommendViewHolder.receiverConditionData(dressingCommitBean,i == mCurrentPosition);
                    }
                }else if (i == 1){
                    if (mFollowViewHolder != null){
                        mFollowViewHolder.receiverConditionData(dressingCommitBean,i == mCurrentPosition);
                    }
                }else if (i == 2){
                    if (mNewestViewHolder != null){
                        mNewestViewHolder.receiverConditionData(dressingCommitBean,i == mCurrentPosition);
                    }
                }
            }
        }
    }

    private void openDressLayout() {
        DressingCommitBean dressingCommitBean = getSelectDynamicUserBean();
        dressingCommitBean.setFrom(DressingCommitBean.MAIN_HOME_DYNAMIC);
        EventBus.getDefault().post(new OpenDrawEvent(dressingCommitBean));
    }

    public DressingCommitBean getSelectDynamicUserBean(){
        if(mViewHolders!=null&&mViewHolders.length>mCurrentPosition){
            if (mCurrentPosition == 0){
                return mRecommendViewHolder.getDressingCommitBean();
            }else if (mCurrentPosition == 1){
                return mFollowViewHolder.getDressingCommitBean();
            }else if (mCurrentPosition == 2){
                return mNewestViewHolder.getDressingCommitBean();
            }
        }
        return new DressingCommitBean();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.img_dressing){
            openDressLayout();
        }else if (i == R.id.btn_publish){
            RouteUtil.forwardPubDynamics();
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
