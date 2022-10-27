package com.yunbao.dynamic.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.event.FollowEvent;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsViewHolder2;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.bean.DynamicBean;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class AbsDynamicDetailViewHolder extends AbsViewHolder2 {
    protected DynamicBean mDynamicBean;
    protected SkillBean skillBean;
    protected CheckedTextView mBtnFollow;
    protected ImageView mImgAvatar;

    public AbsDynamicDetailViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    public AbsDynamicDetailViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
    }
    public abstract  void setData(DynamicBean dynamicBean);
    public boolean judgeIsSelf(String uid){
       return !TextUtils.isEmpty(uid)&&uid.equals(CommonAppConfig.getInstance().getUid());
    }
    public boolean haveSkill(DynamicBean dynamicBean) {
        String skillId=dynamicBean.getSkillid();
        return dynamicBean.getSkillinfo()!=null&&!TextUtils.isEmpty(skillId)&&!skillId.equals("0");
    }
    /*关注*/
    public void follow(View v) {
        if(mDynamicBean==null){
            return;
        }

        CommonHttpUtil.setAttention(mDynamicBean.getUid(), new CommonCallback<Integer>() {
            @Override
            public void callback(Integer isAttention) {
                if(mDynamicBean!=null){
                    mDynamicBean.setIsattent(isAttention);
                }
                EventBus.getDefault().post(new FollowEvent(mDynamicBean.getUid(),isAttention));
                followButtonState(isAttention);
            }
        });
    }


    /*去下单*/
    public void toPlaceAnOrder() {
        if(mDynamicBean==null){
            return;
        }
        String toUid =mDynamicBean.getUid();
        String uid= CommonAppConfig.getInstance().getUid();
//        if(StringUtil.equals(toUid,uid)){
//            ToastUtil.show(R.string.not_down_self_order);
//            return;
//        }
        if(!TextUtils.isEmpty(mDynamicBean.getSkillid())){
            RouteUtil.forwardSkillHome(mDynamicBean.getUid(),mDynamicBean.getSkillid());
        }
    }


    public void toUserHome(){
       if(mDynamicBean!=null){
           RouteUtil.forwardUserHome(mDynamicBean.getUid());
       }
    }

    protected void followButtonState(Integer isAttention) {
        if(mBtnFollow==null){
            return;
        }
        if(judgeIsSelf(mDynamicBean.getUid())){
            mBtnFollow.setVisibility(View.GONE);
        }else{
            mBtnFollow.setVisibility(View.VISIBLE);
        }
        boolean isChecked=isAttention!=1;
        mBtnFollow.setChecked(isChecked);
        mBtnFollow.setText(isChecked? WordUtil.getString(R.string.user_follow_0):WordUtil.getString(R.string.following));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent followEvent){
        followButtonState(followEvent.getAttention());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
    }


    public float defaultColorRate(){
        return 0;
    }
}
