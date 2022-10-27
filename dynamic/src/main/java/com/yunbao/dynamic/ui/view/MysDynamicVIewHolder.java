package com.yunbao.dynamic.ui.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.adapter.MyDynamicAdapter;
import com.yunbao.dynamic.bean.DynamicBean;
import com.yunbao.dynamic.bean.MyDynamicBean;
import com.yunbao.dynamic.event.DynamicCommentNumberEvent;
import com.yunbao.dynamic.event.DynamicLikeEvent;
import com.yunbao.dynamic.http.DynamicHttpUtil;
import com.yunbao.dynamic.ui.activity.DynamicDetailActivity;
import com.yunbao.common.utils.VoiceMediaPlayerUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import io.reactivex.Observable;

import static com.yunbao.common.Constants.DYNAMIC_VIDEO;

public class MysDynamicVIewHolder extends AbsMainViewHolder {
    private RxRefreshView<MyDynamicBean> refreshView;
    private MyDynamicAdapter myDynamicAdapter;

    public MysDynamicVIewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_my_dynamic;
    }

    @Override
    public void init() {
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
        refreshView = (RxRefreshView) findViewById(R.id.refreshView);
        RxRefreshView.ReclyViewSetting setting= RxRefreshView.ReclyViewSetting.createLinearSetting(mContext,1);
        refreshView.setReclyViewSetting(setting);
        myDynamicAdapter=new MyDynamicAdapter(null, (AppCompatActivity) mContext);
        refreshView.setAdapter(myDynamicAdapter);
        refreshView.setDataListner(new RxRefreshView.DataListner<MyDynamicBean>() {
            @Override
            public Observable<List<MyDynamicBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });


        refreshView.setAdapter(myDynamicAdapter);
        myDynamicAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (myDynamicAdapter.getItem(position).getType() == DYNAMIC_VIDEO){
                    if (CommonAppConfig.getInstance().isFloatButtonShowing()){
                        ToastUtil.show(WordUtil.getString(com.yunbao.im.R.string.can_not_do_this_in_opening_live_room));
                        return;
                    }
                }
                DynamicDetailActivity.forward(mContext,myDynamicAdapter.getItem(position));
            }
        });

        refreshView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleRange((LinearLayoutManager) recyclerView.getLayoutManager());
            }
        });
    }
    /*判断item可见来取消掉正在播放的语音*/
    private void visibleRange(LinearLayoutManager layoutManager) {
        int startPosition=layoutManager.findFirstVisibleItemPosition();
        int endPosition=layoutManager.findLastVisibleItemPosition();
        myDynamicAdapter.visibleRange(startPosition,endPosition);
    }

    public Observable<List<MyDynamicBean>> getData(int p) {
        RxAppCompatActivity appCompatActivity= (RxAppCompatActivity) mContext;
        return DynamicHttpUtil.getMyDynamic(p).compose(appCompatActivity.<List<MyDynamicBean>>bindToLifecycle());
    }


    public void  setNoDataTip(String tip){
        if(refreshView!=null){
            refreshView.setNoDataTip(tip);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDynamicLikeEvent(DynamicLikeEvent dynamicLikeEvent){
        int size=myDynamicAdapter.size();
        if(size<=0||dynamicLikeEvent.compare(MyDynamicAdapter.class.getName())){
            return;
        }
        List<MyDynamicBean>list=myDynamicAdapter.getArray();
        for(int i=0;i<size;i++){
            DynamicBean dynamicBean=list.get(i);
            if(StringUtil.equals(dynamicBean.getId(),dynamicLikeEvent.getDynamicId())){
                dynamicBean.setIslike(dynamicLikeEvent.getIsLike());
                dynamicBean.setLikes(dynamicLikeEvent.getLikesNum());
                myDynamicAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDynamicCommentEvent(DynamicCommentNumberEvent dynamicCommentNumberEvent){
        int size=myDynamicAdapter.size();
        if(size<=0){
            return;
        }
        List<MyDynamicBean>list=myDynamicAdapter.getArray();
        for(int i=0;i<size;i++){
            DynamicBean dynamicBean=list.get(i);

            if(StringUtil.equals(dynamicBean.getId(),dynamicCommentNumberEvent.getId())){
                dynamicBean.setComments(dynamicCommentNumberEvent.getNum());
                myDynamicAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    public void setAttention(int attention){
        int size=myDynamicAdapter.size();
        if(size<=0){
            return;
        }
        List<MyDynamicBean>list=myDynamicAdapter.getArray();
        for(MyDynamicBean dynamicBean:list){
            dynamicBean.setIsattent(attention);
        }

    }


    @Override
    public void loadData() {
       refreshView.initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
    }
}
