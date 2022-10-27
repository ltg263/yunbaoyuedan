package com.yunbao.chatroom.ui.activity.gossip;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.behavior.EndLiveBehavior;
import com.yunbao.chatroom.business.behavior.OpenCloseDialogBehavior;
import com.yunbao.chatroom.business.behavior.factory.CacheBehaviorFactory;
import com.yunbao.chatroom.business.state.Stater;
import com.yunbao.chatroom.ui.view.LiveAudienceBottomViewHolder;

@Route(path = RouteUtil.PATH_LIVE_GOSSIP_AUDIENCE)
public class LiveGossipAudienceActivity extends LiveGossipActivity {
    private LiveAudienceBottomViewHolder mLiveAudienceBottomViewHolder;
    @Override
    public void main() {
        super.main();
        mLiveAudienceBottomViewHolder=new LiveAudienceBottomViewHolder(this,mVpBottom);
        mLiveAudienceBottomViewHolder.addToParent();
    }

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
    public void exitSdkRoomSuccess() {
        super.exitSdkRoomSuccess();
        finish();
    }


    @Override
    public void argreeUpWheat(UserBean userBean, int seatid) {
        super.argreeUpWheat(userBean, seatid);
        boolean isSelf= CommonAppConfig.getInstance().isSelf(userBean);
        if(isSelf&&mLivePresenter!=null){
            mLivePresenter.changeRole(Constants.ROLE_ANTHOR);
            mLiveAudienceBottomViewHolder.handAction(Stater.UP_WHEAT);
        }
    }
    @Override
    public int downWheat(UserBean userBean, boolean isSelf) {
        int index=super.downWheat(userBean, isSelf);
        if(CommonAppConfig.getInstance().isSelf(userBean)){
            mLivePresenter.changeRole(Constants.ROLE_AUDIENCE);
            mLiveAudienceBottomViewHolder.handAction(Stater.DOWN_WHEAT);
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

    @Override
    public void refuseUpWheat(UserBean userBean) {
        if(CommonAppConfig.getInstance().isSelf(userBean)){
            ToastUtil.show(R.string.refuse_wheat_tip);
        }
        CacheBehaviorFactory.setApplying(false,this);
    }
}
