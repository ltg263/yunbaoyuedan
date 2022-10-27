package com.yunbao.chatroom.ui.view.seat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.bean.LiveAnthorBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.business.liveobsever.LifeObjectHolder;
import com.yunbao.common.custom.ValueFrameAnimator;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.views.AbsViewHolder2;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.adapter.BaseLiveAnthorAdapter;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.ui.dialog.LiveUserDialogFragment;
import java.util.List;

/*座位列表*/
public abstract class AbsLiveSeatViewHolder<T extends BaseLiveAnthorAdapter<LiveAnthorBean>> extends AbsViewHolder2 implements BaseQuickAdapter.OnItemClickListener {
    protected RecyclerView mReclyLiveAnthor;
    protected LiveActivityLifeModel mLiveActivityLifeModel;
    protected Drawable[] mVoiceDrawables;
    protected ValueFrameAnimator valueFrameAnimator;
    protected T mLiveAnthorAdapter;

    public AbsLiveSeatViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_seat;
    }

    @Override
    public void init() {
        mVoiceDrawables=createDrawableArray(CommonAppContext.sInstance,0,R.mipmap.icon_talk_small,R.mipmap.icon_talk_big);
        valueFrameAnimator=ValueFrameAnimator.
                ofFrameAnim(mVoiceDrawables)
                .setRepeat(ValueFrameAnimator.NO_LIMIT)
                .setSingleInterpolator(new LinearOutSlowInInterpolator())
                .durcation(2000);
        valueFrameAnimator.start();

        mLiveActivityLifeModel= LifeObjectHolder.getByContext(getActivity(),LiveActivityLifeModel.class);
        mReclyLiveAnthor = (RecyclerView) findViewById(R.id.recly_live_anthor);
        RxRefreshView.ReclyViewSetting.createGridSetting(mContext,4)
                .settingRecyclerView(mReclyLiveAnthor);
        mLiveAnthorAdapter=initAdapter();
        mLiveAnthorAdapter.setOnItemClickListener(this);
        mReclyLiveAnthor.setAdapter(mLiveAnthorAdapter);
        RxRefreshView.ReclyViewSetting.createGridSetting(mContext,4)
                .settingRecyclerView(mReclyLiveAnthor);
    }

    public void setData(List<LiveAnthorBean> userBeanList){
        if(mLiveAnthorAdapter!=null){
           mLiveAnthorAdapter.setData(userBeanList);
        }
    }

    /*上座*/
    public void onUpperSeat(UserBean userBean,int position){
        int index=-1;
        if(mLiveActivityLifeModel!=null){
            index= mLiveActivityLifeModel.upNormalWheat(userBean,position);
        }
        if(mLiveAnthorAdapter!=null&&index!=-1){
           mLiveAnthorAdapter.notifyItemChanged(index);
        }
    }

    /*下座*/
    public int onDownSeat(UserBean userBean){
        return onDownSeat(userBean,false);
    }

    /*下座，老板下来是否全部下麦*/
    public int onDownSeat(UserBean userBean,boolean bossAllDown){
        int index=mLiveActivityLifeModel==null?-1:mLiveActivityLifeModel.downSeat(userBean,bossAllDown);
        if(index==-1){
            return index;
        }
        if(!bossAllDown){
           mLiveAnthorAdapter.notifyItemChanged(index);
        }else {
            mLiveAnthorAdapter.notifyDataSetChanged();
        }
        return index;
    }




    /*全部下麦的方法*/
    public void onDownAllSeat(){
        mLiveActivityLifeModel.clearAllSeat();
        mLiveAnthorAdapter.notifyReclyDataChange();
    }

    /*发言状态变化*/
    public void  onTalkStateChange(String uid,boolean isOpen) {
        if (mLiveActivityLifeModel != null) {
            int index = mLiveActivityLifeModel.setAudienceSpeakState(uid, isOpen);
            if (index != -1) {
                mLiveAnthorAdapter.notifyItemChanged(index);
            }
        }
    }

    /*打开用户弹框*/
    public void openUserDialog(UserBean userBean) {
       LiveUserDialogFragment.showLiveUserFragment(getActivity(),userBean);
    }

    /*打开用户弹框*/
    protected void openItemOpenUserDialog(int position) {
        LiveAnthorBean liveBean=mLiveAnthorAdapter.getItem(position);
        LiveUserDialogFragment.showLiveUserFragment(getActivity(),liveBean.getUserBean());
    }

    public static Drawable[] createDrawableArray(Context context, int...resources){
        if(resources==null&&context==null){
            return null;
        }
        Drawable[] drawables = new Drawable[resources.length];
        int length=resources.length;
        for(int i=0;i<length;i++){
            int id=resources[i];
            if(id!=0){
                drawables[i]= ContextCompat.getDrawable(context,id);
            }
        }
        return drawables;
    }
     public abstract T initAdapter();

    public ValueFrameAnimator getValueFrameAnimator() {
        return valueFrameAnimator;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLiveActivityLifeModel=null;
        if(valueFrameAnimator!=null){
           valueFrameAnimator.release();
        }
    }
}
