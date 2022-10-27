package com.yunbao.main.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.activity.ChoosePhoneCountryCodeActivity;
import com.yunbao.main.activity.FlashOrderActivity;
import com.yunbao.main.activity.SearchActivity;

/**
 * Created by Sky.L on 2020-09-24
 * MainActivity 首页父页面
 * 20210722 废弃使用
 */
public class MainHomeParentViewHolder extends AbsMainHomeParentParentViewHolder implements View.OnClickListener{

    private MainHomeParentPlayViewHolder mPlayViewHolder;
    private MainHomeParentDynamicViewHolder mDynamicViewHolder;

    private int mCurrentPosition;

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_parent;
    }

    public MainHomeParentViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init(){
        super.init();
        findViewById(R.id.btn_search).setOnClickListener(this);
        findViewById(R.id.btn_publish).setOnClickListener(this);
    }

    @Override
    protected void loadPageData(int position) {
        if (mViewHolders == null) {
            return;
        }
        this.mCurrentPosition=position;
        AbsMainHomeParentViewHolder vh = mViewHolders[position];
        if (vh == null) {
            if (mViewList != null && position < mViewList.size()) {
                FrameLayout parent = mViewList.get(position);
                if (parent == null) {
                    return;
                }
                if (position == 0) {
                    mPlayViewHolder = new MainHomeParentPlayViewHolder(mContext, parent);
                    vh = mPlayViewHolder;
                }else if(position ==1){
                    //mDynamicViewHolder= new MainHomeParentDynamicViewHolder(mContext, parent);
                   // vh = mDynamicViewHolder;
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
        if (!isFirstLoadData()) {
            return;
        }
        super.loadData();
    }

    @Override
    protected int getPageCount() {
        return 2;
    }

    @Override
    protected String[] getTitles() {
        return new String[]{
                WordUtil.getString(R.string.play_with),
                WordUtil.getString(R.string.dynamic)
        };
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_search) {
            SearchActivity.forward(mContext);
        }else if(i== R.id.btn_publish){
            publishDynamic();
        }else if (i == R.id.v_order) {
            startActivity(FlashOrderActivity.class);
        }
    }

    private void publishDynamic() {
        RouteUtil.forwardPubDynamics();
    }


}
