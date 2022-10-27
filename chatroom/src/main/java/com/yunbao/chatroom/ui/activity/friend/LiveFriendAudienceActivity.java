package com.yunbao.chatroom.ui.activity.friend;

import android.text.TextUtils;
import android.view.View;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LiveAnthorBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.behavior.EndLiveBehavior;
import com.yunbao.chatroom.business.behavior.OpenCloseDialogBehavior;
import com.yunbao.chatroom.business.behavior.factory.CacheBehaviorFactory;
import com.yunbao.chatroom.business.state.Stater;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.chatroom.ui.view.LiveAudienceBottomViewHolder;
import com.yunbao.chatroom.ui.view.seat.LiveFriendSeatViewHolder;

@Route(path = RouteUtil.PATH_LIVE_FRIEND_AUDIENCE)
public class LiveFriendAudienceActivity extends LiveFriendActivity {
    private LiveAudienceBottomViewHolder mLiveAudienceBottomViewHolder;
    private boolean mIsAllowHeartBeat;
    @Override
    protected void clickClose() {
        OpenCloseDialogBehavior openCloseDialogBehavior=new OpenCloseDialogBehavior();
        if (mLivePresenter.getLiveState().role == Constants.ROLE_ANTHOR) {
            openCloseDialogBehavior.openCloseDialog(this, WordUtil.getString(R.string.out_live_room), mLivePresenter);
        } else {
            openCloseDialogBehavior.openCloseDialogWithFloat(this, WordUtil.getString(R.string.live_room_minimize), WordUtil.getString(R.string.out_live_room), mLivePresenter);

        }
    }
    @Override
    protected int getRole() {
        return getIntent().getIntExtra(Constants.ROLE,Constants.ROLE_AUDIENCE);
    }

    @Override
    public void main() {
        super.main();
        mLiveAudienceBottomViewHolder=new LiveAudienceBottomViewHolder(this,mVpBottom);
        mLiveAudienceBottomViewHolder.addToParent();
        restoreState();
    }

    private void restoreState() {
        if(mLiveBean==null){
            return;
        }
        String json=mLiveBean.getExpandParm();
        if(TextUtils.isEmpty(json)){
            return;
        }
        JSONObject jsonObject=JSON.parseObject(json);
        int state=jsonObject.getIntValue("jy_step");
        if(state==0||state==Constants.STATE_PREPARATION_LINK){
            return;
        }
        int time= jsonObject.getIntValue("jy_step_time");
        if(state==Constants.STATE_CARDIAC_SELECTION){
            mTotalTime=time;
        }
        changeState(state);
    }

    @Override
    protected void initView() {
        super.initView();
        mRgBtnProgress.setVisibility(View.GONE);
    }

    @Override
    protected void initSeatItemClickListner() {
        if(mLiveSeatViewHolder!=null) {
            mLiveSeatViewHolder.setOnItemClickListner(new LiveFriendSeatViewHolder.OnItemClickListner() {
                @Override
                public void onItem(int position, LiveAnthorBean liveAnthorBean) {
                    UserBean userBean = liveAnthorBean.getUserBean();
                    if (userBean == null) {
                       return;
                    }
                    if (userBean.getSex() == CommonAppConfig.getInstance().getUserBean().getSex()){
                        mLiveSeatViewHolder.openUserDialog(userBean);
                        return;
                    }
                    if(mLiveSeatViewHolder.isCanSelectHeartBeat()){
                        requestHeartBeat(position);
                    }else{
                      mLiveSeatViewHolder.openUserDialog(userBean);
                    }
                }
            });
        }
    }

    private void requestHeartBeat(int tositid) {
        if (mLiveBean==null){
            return;
        }
        ChatRoomHttpUtil.setHeart(mLiveBean.getUid(),mLiveBean.getStream(),Integer.toString(tositid+1)).subscribe(new DefaultObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if(aBoolean){
                   mLiveSeatViewHolder.stopHeartAnim();
                }
            }
        });
    }

    @Override
    protected void stateAsPreparationLink() {
        super.stateAsPreparationLink();
        mIsAllowHeartBeat=false;
        if(mLiveSeatViewHolder!=null){
           mLiveSeatViewHolder.stopHeartAnim();
        }
           mLiveAudienceBottomViewHolder.handAction(Stater.DOWN_WHEAT);
    }

    @Override
    protected void stateAsCardiacSelection() {
        super.stateAsCardiacSelection();
        if(mLiveActivityLifeModel!=null&&mLiveActivityLifeModel.isOnWheat(CommonAppConfig.getInstance().getUserBean())){
           mIsAllowHeartBeat=true;
        }
        if(mLiveSeatViewHolder!=null){
           mLiveSeatViewHolder.startHeartAnim();
        }
    }

    @Override
    protected void stateAnnounceHeartbeat() {
        super.stateAnnounceHeartbeat();
        mIsAllowHeartBeat=false;
        if(mLiveSeatViewHolder!=null){
           mLiveSeatViewHolder.stopHeartAnim();
        }
    }

    @Override
    public void exitSdkRoomSuccess() {
        super.exitSdkRoomSuccess();
        finish();
    }

    @Override
    public void argreeUpWheat(UserBean userBean, int seatid) {
        super.argreeUpWheat(userBean, seatid);
        boolean isSelf= CommonAppConfig.getInstance().isSelf(userBean);
        if(!isSelf){
           return;
        }
        if(mLivePresenter!=null){
            mLivePresenter.changeRole(Constants.ROLE_ANTHOR);
            mLiveAudienceBottomViewHolder.handAction(Stater.UP_WHEAT);
        }
        if(mIsAllowHeartBeat&&mCurrentState==Constants.STATE_CARDIAC_SELECTION&&mLiveSeatViewHolder!=null){
            mLiveSeatViewHolder.startHeartAnim();
        }
    }

    @Override
    public int downWheat(UserBean userBean, boolean isSelf) {
        int index=super.downWheat(userBean, isSelf);
        if(CommonAppConfig.getInstance().isSelf(userBean)){
            mLivePresenter.changeRole(Constants.ROLE_AUDIENCE);
            mLiveAudienceBottomViewHolder.handAction(Stater.DOWN_WHEAT);
        }
        if(mLiveSeatViewHolder!=null&&CommonAppConfig.getInstance().isSelf(userBean)){
           mLiveSeatViewHolder.stopHeartAnim();
        }
        return index;
    }

    @Override
    public void endLive() {
        super.endLive();
        EndLiveBehavior endLiveBehavior=new EndLiveBehavior();
        endLiveBehavior.subscribe(this);
        endLiveBehavior.endLive(this,mLivePresenter);
    }

    @Override
    public void refuseUpWheat(UserBean userBean) {
        super.refuseUpWheat(userBean);
        if(CommonAppConfig.getInstance().isSelf(userBean)){
            ToastUtil.show(R.string.refuse_wheat_tip);
        }
        CacheBehaviorFactory.setApplying(false,this);
    }

    @Override
    public void openSpeak(UserBean userBean, boolean isOpen) {
        super.openSpeak(userBean, isOpen);
        if(!CommonAppConfig.getInstance().isSelf(userBean)){
            return;
        }
        if(isOpen){
            mLiveAudienceBottomViewHolder.handAction(Stater.WHEAT_OPEN_LIMIT);
        }else{
            mLiveAudienceBottomViewHolder.handAction(Stater.WHEAT_BAN);
        }
        if(mLiveActivityLifeModel!=null){
           mLiveActivityLifeModel.setAudienceCanSpeakState(!isOpen);
        }
    }
}
