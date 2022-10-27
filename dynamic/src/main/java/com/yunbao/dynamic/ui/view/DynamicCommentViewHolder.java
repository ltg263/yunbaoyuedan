package com.yunbao.dynamic.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yunbao.common.custom.ValueFrameAnimator;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.JsonUtil;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsViewHolder2;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.adapter.DynamicCommentAdapter;
import com.yunbao.dynamic.bean.DynamicBean;
import com.yunbao.dynamic.bean.DynamicCommentBean;
import com.yunbao.dynamic.business.AnimHelper;
import com.yunbao.dynamic.event.DynamicCommentEvent;
import com.yunbao.dynamic.event.DynamicCommentNumberEvent;
import com.yunbao.dynamic.event.DynamicLikeEvent;
import com.yunbao.dynamic.http.DynamicHttpUtil;
import com.yunbao.dynamic.ui.activity.DynamicDetailActivity;
import com.yunbao.dynamic.widet.SimulateReclyViewTouchLisnter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class DynamicCommentViewHolder extends AbsViewHolder2 implements View.OnClickListener , OnItemClickListener<DynamicCommentBean>, DynamicCommentAdapter.ActionListener{
    private FrameLayout root;
    private LinearLayout bottom;
    private TextView commentNum;
    private RxRefreshView<DynamicCommentBean> refreshView;
    private TextView input;
    private ImageView btnFace;
    private ImageView imgZan;
    private TextView tvZan;
    private DynamicBean dynamicBean;
    private LinearLayout btnZan;
    private LinearLayout mVpTools;

    private DynamicCommentAdapter dynamicCommentAdapter;
    private String dynamicId;
    private String dynamicUid;
    private Drawable[] mLikeAnimDrawables;
    private ValueFrameAnimator valueFrameAnimator;
    private boolean isShowLike;

    public DynamicCommentViewHolder(Context context, ViewGroup parentView, DynamicBean dynamicBean,boolean isShowLike) {
        super(context, parentView, dynamicBean,isShowLike);
    }
    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
        dynamicBean = (DynamicBean) args[0];
        dynamicId = dynamicBean.getId();
        dynamicUid = dynamicBean.getUid();
        this.isShowLike= (boolean) args[1];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_dynamic_comment;
    }
    @Override
    public void init() {
        root = (FrameLayout) findViewById(R.id.root);
        bottom = (LinearLayout) findViewById(R.id.bottom);
        commentNum = (TextView) findViewById(R.id.comment_num);
        refreshView = (RxRefreshView) findViewById(R.id.refreshView);
        input = (TextView) findViewById(R.id.input);
        btnFace = (ImageView) findViewById(R.id.btn_face);
        imgZan = (ImageView) findViewById(R.id.img_zan);
        tvZan = (TextView) findViewById(R.id.tv_zan);
        btnZan = (LinearLayout) findViewById(R.id.btn_zan);
        mVpTools = (LinearLayout) findViewById(R.id.vp_tools);

        btnZan.setOnClickListener(this);
        input.setOnClickListener(this);
        btnFace.setOnClickListener(this);
//        refreshView.setRefreshEnable(false);
//        refreshView.setLoadMoreEnable(false);
        refreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(mContext,0));
        refreshView.setNoDataTip(R.string.rob_sofa_tip);
        refreshView.setDataListner(new RxRefreshView.DataListner<DynamicCommentBean>() {
            @Override
            public Observable<List<DynamicCommentBean>> loadData(int p) {
                return getData();
            }
            @Override
            public void compelete(List<DynamicCommentBean> data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });
        dynamicCommentAdapter=new DynamicCommentAdapter(mContext,dynamicBean);
        dynamicCommentAdapter.setOnItemClickListener(this);
        dynamicCommentAdapter.setActionListener(this);
        refreshView.setAdapter(dynamicCommentAdapter);
        refreshView.initData();

        if(dynamicBean!=null&&isShowLike){
            initAnim();
           tvZan.setText(dynamicBean.getLikes()+"");
            if(dynamicBean.getIslike()==1){
                imgZan.setImageDrawable(mLikeAnimDrawables[mLikeAnimDrawables.length-1]);
            }else{
                imgZan.setImageDrawable(mLikeAnimDrawables[0]);
            }
        }else{
           btnZan.setVisibility(View.GONE);
        }
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
        commentNum.setOnTouchListener(new SimulateReclyViewTouchLisnter(getRecyclerView()));

    }

    private void initAnim() {
        mLikeAnimDrawables= AnimHelper.createDrawableArray(mContext,AnimHelper.FOLLOW_ANIM_LIST);
        valueFrameAnimator= ValueFrameAnimator.
                ofFrameAnim(mLikeAnimDrawables)
                .setSingleInterpolator(new OvershootInterpolator())
                .durcation(600)
                .anim(imgZan);
    }

    /*获取网络数据*/
    private Observable<List<DynamicCommentBean>> getData() {
       String dynamicId=dynamicBean!=null?dynamicBean.getId():"0";
       return DynamicHttpUtil.getDynaimcComments(dynamicId,getLastIndex()).map(new Function<JSONObject, List<DynamicCommentBean>>() {
           @Override
           public List<DynamicCommentBean> apply(JSONObject jsonObject) throws Exception {
               Integer commentNums=jsonObject.getInteger("nums");
               EventBus.getDefault().post(new DynamicCommentNumberEvent(dynamicBean.getId(),commentNums));
               commentNum.setText(WordUtil.getString(R.string.comment,commentNums));
               String dataString=jsonObject.getJSONArray("list").toJSONString();
               List<DynamicCommentBean>list=JsonUtil.getJsonToList(dataString,DynamicCommentBean.class);
               for (DynamicCommentBean bean : list) {
                   if (bean != null) {
                       bean.setParentNode(true);
                   }
               }
               return list;
           }
       }).compose(((RxAppCompatActivity)mContext).<List<DynamicCommentBean>>bindToLifecycle());
    }

    private String getLastIndex() {
        if(dynamicCommentAdapter==null|| !ListUtil.haveData(dynamicCommentAdapter.getList())||refreshView.isRefreshing()){
            return "0";
        }
        List<DynamicCommentBean> dynamicCommentBeans=dynamicCommentAdapter.getList();
        return dynamicCommentBeans.get(dynamicCommentBeans.size()-1).getId();
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.input){
            openInputDialog();
        }else if(id==R.id.btn_face){
            openFaceDialog();
        }else if(id==R.id.btn_zan){
            toggleLike();
        }
    }

    public void toggleLike(){
        if(dynamicBean==null){
            return;
        }
        DynamicHttpUtil.dynamicAddLikeDefault(dynamicBean.getId()).subscribe(new DefaultObserver<JSONObject>() {
            @Override
            public void onNext(JSONObject jsonObject) {
                if (jsonObject == null){
                    return;
                }
                Integer isLike=jsonObject.getInteger("islike");
                int likesNum=jsonObject.getInteger("likes");
                EventBus.getDefault().post(new DynamicLikeEvent(isLike,likesNum,dynamicBean.getId()));
            }
        });
    }

    private void openFaceDialog() {
        if (!TextUtils.isEmpty(dynamicId) && !TextUtils.isEmpty(dynamicUid)) {
            ((DynamicDetailActivity) mContext).openCommentInputWindow(true, dynamicId, dynamicUid, null);
        }
    }
    private void openInputDialog() {
        if (!TextUtils.isEmpty(dynamicId) && !TextUtils.isEmpty(dynamicUid)) {
            ((DynamicDetailActivity) mContext).openCommentInputWindow(false, dynamicId, dynamicUid, null);
        }
    }

    @Override
    public void onItemClick(DynamicCommentBean bean, int position) {
        if (!TextUtils.isEmpty(dynamicId) && !TextUtils.isEmpty(dynamicUid)) {
            ((DynamicDetailActivity) mContext).openCommentInputWindow(false, dynamicId, dynamicUid, bean);
        }
    }

    /*展开评论*/
    @Override
    public void onExpandClicked(final DynamicCommentBean commentBean) {
        final DynamicCommentBean parentNodeBean = commentBean.getParentNodeBean();
        if (parentNodeBean == null) {
            return;
        }

        DynamicHttpUtil.getDynaimcReply(parentNodeBean.getId(),commentBean.getId()).subscribe(new DefaultObserver<List<DynamicCommentBean>>() {
            @Override
            public void onNext(List<DynamicCommentBean> dynamicCommentBeans) {
                for(DynamicCommentBean dynamicCommentBean:dynamicCommentBeans){
                    dynamicCommentBean.setParentNodeBean(parentNodeBean);
                }
                List<DynamicCommentBean> childList = parentNodeBean.getChildList();
                if (childList != null) {
                    childList.addAll(dynamicCommentBeans);
                    if (dynamicCommentAdapter != null) {
                        dynamicCommentAdapter.insertReplyList(commentBean, dynamicCommentBeans.size());
                    }
                }
            }
        });
    }

    /*必须调用手动刷新*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDynamicCommentEvent(DynamicCommentEvent dynamicCommentEvent){
        if(dynamicBean!=null&&StringUtil.equals(dynamicBean.getId(),dynamicCommentEvent.getId())){
           refreshView.autoRefresh();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDynamicLikeEvent(DynamicLikeEvent dynamicLikeEvent){
        if(dynamicBean==null|| !isShowLike||!StringUtil.equals(dynamicLikeEvent.getDynamicId(),dynamicBean.getId())){
            return;
        }
        dynamicBean.setLikes(dynamicLikeEvent.getLikesNum());
        dynamicBean.setIslike(dynamicLikeEvent.getIsLike());

        if(valueFrameAnimator!=null){
            if(dynamicLikeEvent.getIsLike()==0){
                valueFrameAnimator.reverse();
            }else{
                valueFrameAnimator.start();
            }
        }
        tvZan.setText(dynamicBean.getLikes()+"");
    }

    @Override
    public void onCollapsedClicked(DynamicCommentBean commentBean) {
        DynamicCommentBean parentNodeBean = commentBean.getParentNodeBean();
        if (parentNodeBean == null) {
            return;
        }
        List<DynamicCommentBean> childList = parentNodeBean.getChildList();
        DynamicCommentBean node0 = childList.get(0);
        int orignSize = childList.size();
        parentNodeBean.removeChild();
        parentNodeBean.setChildPage(1);
        if (dynamicCommentAdapter != null){
            dynamicCommentAdapter.notifyDataSetChanged();
        }
    }

    public RecyclerView getRecyclerView(){
        return refreshView.getRecyclerView();
    }

    public View exportToolView(){
        if(mVpTools!=null&&mVpTools.getParent()!=null){
            ((ViewGroup)mVpTools.getParent()).removeView(mVpTools);
        }
        return mVpTools;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(valueFrameAnimator!=null){
           valueFrameAnimator.release();
        }
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
    }
}
