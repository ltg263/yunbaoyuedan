package com.yunbao.chatroom.ui.activity.dispatch;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.business.behavior.EndLiveBehavior;
import com.yunbao.chatroom.business.behavior.OpenCloseDialogBehavior;
import com.yunbao.chatroom.business.behavior.factory.CacheBehaviorFactory;
import com.yunbao.chatroom.business.state.Stater;
import com.yunbao.chatroom.ui.view.LiveAudienceBottomViewHolder;

@Route(path = RouteUtil.PATH_LIVE_DISPATH_AUDIENCE)
public class LiveDispatchAudienceActivity extends LiveSpatchActivity {
    private LiveAudienceBottomViewHolder mLiveAudienceBottomViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void main() {
        super.main();
        mLiveAudienceBottomViewHolder = new LiveAudienceBottomViewHolder(this, mVpBottom);
        mLiveAudienceBottomViewHolder.addToParent();
        mBtnOrderTip.setOnClickListener(this);
        boolean isQuickUpWheat = getIntent().getBooleanExtra(Constants.UP_WHEAT, false);
        if (isQuickUpWheat) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    upNormalWheat(null);

                }
            },1000);
        }
        allowOpenOrderMessage();
    }

    /*初进场是否允许展示订单信息*/
    private void allowOpenOrderMessage() {
        if (mLiveBean.getIsdispatch() == 1 && !TextUtils.isEmpty(mLiveBean.getSkillid())) {
            onOrderUpDate(mLiveBean.getSkillid());
        }
    }

    /*切换是否开启轮流的状态*/
    @Override
    public void openWheeledWheat(boolean isOpen) {
        super.openWheeledWheat(isOpen);
        if (isOpen) {
            mLiveAudienceBottomViewHolder.handAction(Stater.SPEAK_IN_TURN);
        } else {
            mLiveAudienceBottomViewHolder.handAction(Stater.SPEAK_OUT_TURN);
        }
        if (mLivePresenter != null) {
            mLivePresenter.openSpeak(false);
        }
    }


    /*轮流发言中,切换发言者*/
    @Override
    public void changeSpeakUser(UserBean userBean) {
        super.changeSpeakUser(userBean);
        boolean isSelf = CommonAppConfig.getInstance().isSelf(userBean);
        L.e("isSelf==" + isSelf);
        if (isSelf) {
            mLiveAudienceBottomViewHolder.handAction(Stater.SPEAK_IN_TURN_SELF);
        } else {
            mLiveAudienceBottomViewHolder.handAction(Stater.SPEAK_IN_TURN_UNSELF);
        }
        if (mLivePresenter != null) {
            mLivePresenter.openSpeak(isSelf);
        }
    }

    private void enterRoomSucc() {
        initAndConnectSocket();
        initAndEnterSdkRoom();
    }


    private void margeLiveBean(LiveBean liveBean) {
        if (mLiveActivityLifeModel != null) {
            mLiveActivityLifeModel.margeLiveBean(liveBean);
        }
    }


    @Override
    public void resfuseUpWheat(String uid) {
        super.resfuseUpWheat(uid);
        if (StringUtil.equals(CommonAppConfig.getInstance().getUid(), uid)) {
            ToastUtil.show(R.string.host_refuse_apply);
        }
        CacheBehaviorFactory.setApplying(false, this);
    }

    @Override
    protected int getRole() {
        return getIntent().getIntExtra(Constants.ROLE, Constants.ROLE_AUDIENCE);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.btn_order_tip) {
            upNormalWheat(v);
        }
    }

    /*点击上普通麦*/
    private void upNormalWheat(View v) {
        if (mSocketProxy != null && mLiveBean != null) {
            mSocketProxy.getWheatMannger().upNormalWheat(mLiveBean.getUid(), mLiveBean.getStream(), this, v);
        }
    }

    /*收到上老板麦成功的申请*/
    @Override
    public void upBossWheatSuccess(UserBean userBean) {
        super.upBossWheatSuccess(userBean);
        judgeUpWheat(userBean);
    }

    /*判断是否是自己上了麦*/
    private void judgeUpWheat(UserBean userBean) {
        boolean isSelf = CommonAppConfig.getInstance().isSelf(userBean);
        if (isSelf && mLivePresenter != null) {
            mLivePresenter.changeRole(Constants.ROLE_ANTHOR);
            mLiveAudienceBottomViewHolder.handAction(Stater.UP_WHEAT);
        }
    }

    /*上普通麦成功*/
    @Override
    public void upNormalWheatSuccess(UserBean userBean, int positon) {
        super.upNormalWheatSuccess(userBean, positon);
        boolean isSelf = CommonAppConfig.getInstance().isSelf(userBean);
        if (isSelf && mLivePresenter != null) {
            mLivePresenter.changeRole(Constants.ROLE_ANTHOR);
            mLiveAudienceBottomViewHolder.handAction(Stater.UP_WHEAT);
        }
    }

    @Override
    public int downWheat(UserBean userBean, boolean isSelf) {
        int index = super.downWheat(userBean, isSelf);
        if (CommonAppConfig.getInstance().isSelf(userBean) || index == 7) {
            mLivePresenter.changeRole(Constants.ROLE_AUDIENCE);
            mLiveAudienceBottomViewHolder.handAction(Stater.DOWN_WHEAT);
        }
        return index;
    }

    @Override
    protected void clickClose() {
        OpenCloseDialogBehavior openCloseDialogBehavior = new OpenCloseDialogBehavior();
        if (mLivePresenter.getLiveState().role == Constants.ROLE_ANTHOR) {
            openCloseDialogBehavior.openCloseDialog(this, WordUtil.getString(R.string.out_live_room), mLivePresenter);
        } else {
            openCloseDialogBehavior.openCloseDialogWithFloat(this, WordUtil.getString(R.string.live_room_minimize), WordUtil.getString(R.string.out_live_room), mLivePresenter);

        }
    }

    @Override
    public void exitSdkRoomSuccess() {
        super.exitSdkRoomSuccess();
        finish();
    }

    @Override
    public void endLive() {
        super.endLive();
        EndLiveBehavior endLiveBehavior = new EndLiveBehavior();
        endLiveBehavior.subscribe(this);
        endLiveBehavior.endLive(this, mLivePresenter);
    }

    /*收到是否允许发言的状态*/
    @Override
    public void openSpeak(UserBean userBean, boolean isOpen) {
        super.openSpeak(userBean, isOpen);
        if (!CommonAppConfig.getInstance().isSelf(userBean)) {
            return;
        }
        if (isOpen) {
            mLiveAudienceBottomViewHolder.handAction(Stater.WHEAT_OPEN_LIMIT);
        } else {
            mLiveAudienceBottomViewHolder.handAction(Stater.WHEAT_BAN);
        }
        if (mLiveActivityLifeModel != null) {
            mLiveActivityLifeModel.setAudienceCanSpeakState(!isOpen);
        }
    }

}
