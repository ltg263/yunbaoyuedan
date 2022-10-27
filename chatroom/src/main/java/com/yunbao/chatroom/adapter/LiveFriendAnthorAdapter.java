package com.yunbao.chatroom.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.bean.LiveAnthorBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.ValueFrameAnimator;
import com.yunbao.common.utils.ResourceUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import java.util.List;

public class LiveFriendAnthorAdapter extends BaseLiveAnthorAdapter<LiveAnthorBean> {
    private int[] TAG_MAN={0,1,4,5};
    private String mManTip;
    private String mWomanTip;

    private int mManTextColor;
    private int mWomanTextColor;

    private ValueFrameAnimator mHeartAnimator;
    private boolean mIsStartHeartBeat;

    public LiveFriendAnthorAdapter(List<LiveAnthorBean> data, ValueFrameAnimator valueFrameAnimator) {
        super(data, valueFrameAnimator);
        mManTip= WordUtil.getString(R.string.handsome_tag);
        mWomanTip= WordUtil.getString(R.string.beauty_tag);

        mManTextColor= CommonAppContext.sInstance.getResourceColor(R.color.global);
        mWomanTextColor= CommonAppContext.sInstance.getResourceColor(R.color.purplish_red);

    }
    @Override
    public int getLayoutId() {
        return R.layout.item_recly_live_anthor_friend;
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, LiveAnthorBean item) {
        UserBean userBean=item.getUserBean();
        int postion=helper.getLayoutPosition();
        boolean isManTag=isManTag(postion);

        helper.setText(R.id.tv_num,Integer.toString(postion+1));
        View vBgName=helper.getView(R.id.vp_name);
        TextView tvNum=helper.getView(R.id.tv_num);
        int drawableBgNameId=isManTag?R.drawable.bg_man:R.drawable.bg_woman;
        Drawable bgNameDrawale=ResourceUtil.getDrawable(drawableBgNameId,true);

        if(userBean!=null){
            helper.setText(R.id.tv_name,userBean.getUserNiceName());
            helper.setImageUrl(userBean.getAvatar(),R.id.img_avatar);
            View titleBgView=helper.getView(R.id.vp_name);
            titleBgView.setBackground(bgNameDrawale);
            int drawableBgNumId=isManTag?R.drawable.round_global:R.drawable.round_purplish_red;
            tvNum.setBackground(ResourceUtil.getDrawable(drawableBgNumId,true));
            vBgName.setBackground(null);
            tvNum.setTextColor(Color.WHITE);
        }else{
            String emptyString=isManTag?mManTip:mWomanTip;
            helper.setText(R.id.tv_name,emptyString);
            helper.setImageResouceId(0,R.id.img_avatar);
            vBgName.setBackground(bgNameDrawale);
            tvNum.setBackground(ResourceUtil.getDrawable(R.drawable.round_white,true));

            int emptyColor=isManTag?mManTextColor:mWomanTextColor;
            tvNum.setTextColor(emptyColor);
        }
        ImageView bgImageView=helper.getView(R.id.bg_imageView);
        if(item.isCurrentSpeak()&&item.getUserBean()!=null){
            mValueFrameAnimator.addAnim(bgImageView);
        }else {
            mValueFrameAnimator.removeAnim(bgImageView);
            bgImageView.setImageResource(0);
        }

        /*mHeartAnimator==null 是因为没有自己上座,getUserBean==null说明这个座位是空的,没必要显示动画*/
        if(mHeartAnimator==null||item.getUserBean()==null){
            return;
        }

        boolean selfIsMan=CommonAppConfig.getInstance().getUserBean().getSex()==1;
        if((selfIsMan&&!isManTag)||(!selfIsMan&&isManTag)){
            mHeartAnimator.addAnim((ImageView) helper.getView(R.id.img_heart));
        }
    }

    public void startHeartAnim(){
        if(!mData.contains(CommonAppConfig.getInstance().getUserBean())){
            return;
        }

        if(mHeartAnimator==null){
            Drawable[]drawableArray=new Drawable[3];
            Drawable drawable= ResourceUtil.getDrawable(R.mipmap.icon_beckoning_1,true);
            drawableArray[0]=drawable;
            drawable= ResourceUtil.getDrawable(R.mipmap.icon_beckoning_2,true);
            drawableArray[1]=drawable;
            drawable= ResourceUtil.getDrawable(R.mipmap.icon_beckoning_3,true);
            drawableArray[2]=drawable;
            mHeartAnimator=ValueFrameAnimator.ofFrameAnim(drawableArray).
                   setSingleInterpolator(new LinearOutSlowInInterpolator())
                   .durcation(500)
                   .setRepeat(1000);
        }
        if(!mHeartAnimator.isStarted()){
            mHeartAnimator.start();
        }
           mIsStartHeartBeat=true;
           notifyReclyDataChange();
    }

    public void stopHeartAnim(){
        mIsStartHeartBeat=false;
        if(mHeartAnimator!=null){
            mHeartAnimator.clearImage();
            if(mHeartAnimator.isStarted()){
                mHeartAnimator.cancle();
                mHeartAnimator.release();

                mHeartAnimator=null;
            }
        }
    }

    /*是否是男生的位置*/
    private boolean isManTag(int postion) {
        for(int integer:TAG_MAN){
            if(postion==integer){
                return true;
            }
        }
        return false;
    }

    @Override
    public void release() {
        super.release();
        ResourceUtil.clearDrawable(R.drawable.bg_man,
                R.drawable.bg_woman,
                R.drawable.round_global,
                R.drawable.round_purplish_red,
                R.mipmap.icon_beckoning_1,
                R.mipmap.icon_beckoning_2,
                R.mipmap.icon_beckoning_3
                ,R.drawable.round_white
                );
    }

    public boolean isStartHeartBeat() {
        return mIsStartHeartBeat;
    }

    public boolean isCanSelectHeartBeat(int position){
        boolean selfIsMan=CommonAppConfig.getInstance().getUserBean().getSex()==1;
        boolean isManTag=isManTag(position);
        return mIsStartHeartBeat&&((selfIsMan&&!isManTag)||(!selfIsMan&&isManTag));
    }
}
