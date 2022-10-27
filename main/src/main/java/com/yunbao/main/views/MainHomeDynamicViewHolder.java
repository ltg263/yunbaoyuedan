package com.yunbao.main.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.event.FollowEvent;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.views.AbsMainHomeChildViewHolder;
import com.yunbao.dynamic.bean.DynamicUserBean;
import com.yunbao.dynamic.event.DynamicLikeEvent;
import com.yunbao.main.R;
import com.yunbao.main.adapter.MainPlayUserAdapter;
import com.yunbao.main.bean.commit.DressingCommitBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by cxf on 2018/9/22.
 *  首页子页面  陪玩-子页面
 */


public abstract class MainHomeDynamicViewHolder extends AbsMainHomeChildViewHolder {
    private RxRefreshView<DynamicUserBean> mRefreshView;
    private MainPlayUserAdapter adapter;
    private DressingCommitBean dressingCommitBean;
    private static RecyclerView.RecycledViewPool recycledViewPool;

    public MainHomeDynamicViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
        dressingCommitBean=new DressingCommitBean();
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_dynamic;
    }
    @Override
    public void init() {
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
        adapter=new MainPlayUserAdapter(null,mContext);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DynamicUserBean bean=MainHomeDynamicViewHolder.this.adapter.getArray().get(position);
                toDynamicDetail(bean);
            }
        });

        mRefreshView=findViewById(R.id.refreshView);
        //new LinearSnapHelper().attachToRecyclerView(mRefreshView.getRecyclerView());
        //setMaxFlingVelocity(mRefreshView.getRecyclerView(),1);
        RxRefreshView.ReclyViewSetting setting=RxRefreshView.ReclyViewSetting.creatStaggeredGridSetting(mContext,2);
        mRefreshView.setReclyViewSetting(setting);
        mRefreshView.setAdapter(adapter);
        mRefreshView.setLoadMoreEnable(true);
        mRefreshView.setRefreshEnable(true);
        mRefreshView.setDataListner(new RxRefreshView.DataListner<DynamicUserBean>() {
            @Override
            public Observable<List<DynamicUserBean>> loadData(int p) {
                return getData(dressingCommitBean.getSex(),dressingCommitBean.getAge(),dressingCommitBean.getSkill(),p);
            }
            @Override
            public void compelete(List<DynamicUserBean> data) {
                if(mActionListener!=null){
                   mActionListener.onRefreshCompleted();
                }
            }
            @Override
            public void error(Throwable e) {
                if(mActionListener!=null){
                   mActionListener.onRefreshCompleted();
                }
            }
        });

       /*  if(recycledViewPool==null){
            recycledViewPool=new RecyclerView.RecycledViewPool();
         }
        mRefreshView.setRecycledViewPool(recycledViewPool);*/
    }

    private void toDynamicDetail(DynamicUserBean DynamicUserBean) {
       // DynamicDetailActivity.forward(mContext,DynamicUserBean);
        //跳转个人主页
        RouteUtil.forwardUserHome(DynamicUserBean.getId());
    }

    public DressingCommitBean getDressingCommitBean() {
        if(dressingCommitBean==null){
           dressingCommitBean= new DressingCommitBean();
        }
        return dressingCommitBean;
    }

    public void receiverConditionData(DressingCommitBean dressingCommitBean, boolean needRefresh){
        if(!needRefresh){
            return;
        }
        this.dressingCommitBean.copy(dressingCommitBean);
        loadData();
    }

    public static void setMaxFlingVelocity(RecyclerView recyclerView, int velocity){
        try{
            Field field = recyclerView.getClass().getDeclaredField("mMaxFlingVelocity");
            field.setAccessible(true);
            field.set(recyclerView, velocity);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDynamicLikeEvent(DynamicLikeEvent dynamicLikeEvent){
//        int size=adapter.size();
//        if(size<=0){
//           return;
//        }
//
//        List<DynamicUserBean>list=adapter.getArray();
//        for(int i=0;i<size;i++){
//            DynamicUserBean DynamicUserBean=list.get(i);
//            if(StringUtil.equals(DynamicUserBean.getId(),dynamicLikeEvent.getDynamicId())){
//               DynamicUserBean.setIslike(dynamicLikeEvent.getIsLike());
//               DynamicUserBean.setLikes(dynamicLikeEvent.getLikesNum());
//               adapter.notifyItemChanged(i);
//                break;
//            }
//        }
    }

    @Override
    public void loadData() {
        super.loadData();
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent followEvent){
        if(adapter.size()==0){
            return;
        }
        List<DynamicUserBean>array=adapter.getData();
        for(DynamicUserBean DynamicUserBean:array){
            if(StringUtil.equals(DynamicUserBean.getId(),followEvent.getToUid())){
                DynamicUserBean.setIsattent(String.valueOf(followEvent.getAttention()));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recycledViewPool=null;
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
    }

    public abstract Observable<List<DynamicUserBean>>getData(String sex,String age,String skillid,int p);
}
