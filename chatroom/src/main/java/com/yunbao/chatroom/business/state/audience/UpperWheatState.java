package com.yunbao.chatroom.business.state.audience;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.yunbao.common.business.liveobsever.LifeObjectHolder;
import com.yunbao.common.utils.ResourceUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.business.behavior.SkPowerBehavior;
import com.yunbao.chatroom.business.behavior.factory.CacheBehaviorFactory;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.business.state.Stater;

/*上麦模式*/
public class UpperWheatState extends State {
    private boolean mIsOpenWheat;
    private LiveActivityLifeModel mLiveActivityLifeModel;
    private LiveBean mLiveBean;



    public UpperWheatState(@NonNull TextView mBtnMike1, @NonNull TextView mBtnMike2, TextView tvSpeakTime,Stater stater) {
        super(mBtnMike1, mBtnMike2, tvSpeakTime,stater);
        AppCompatActivity appCompatActivity=(AppCompatActivity)mBtnMike1.getContext();
        mLiveActivityLifeModel = LifeObjectHolder.getByContext(appCompatActivity, LiveActivityLifeModel.class);
        if(mLiveActivityLifeModel !=null){
           mLiveBean= mLiveActivityLifeModel.getLiveBean();
        }
    }

    @Override
    public void handAction(int action) {
        if(action==Stater.TAKE_UI){
           takeUI();
        }else if(action==Stater.WHEAT_BAN){
            banWheat();
        }else if(action==Stater.SPEAK_IN_TURN){
            changeToSpeakInTurnState();
        }else if(action==Stater.DOWN_WHEAT){
            changeToNormalState();
        }else if(action==Stater.WHEAT_OPEN_LIMIT){
            changeToOpenLimit();
        }else if(action==Stater.SPEAK_IN_TURN_SELF){
            changeToSpeakInTurnState();
            mStater.handAction(Stater.SPEAK_IN_TURN_SELF);
        }else if(action==Stater.SPEAK_IN_TURN_UNSELF){
            changeToSpeakInTurnState();
            mStater.handAction(Stater.SPEAK_IN_TURN_SELF);
        }
    }

    private void changeToOpenLimit() {
        mBtnMike1.setEnabled(true);
    }

    /*恢复到正常的模式*/
    private void changeToNormalState() {
        if(mStater!=null){
            mStater.setState(new NormalState(mBtnMike1,mBtnMike2,mTvSpeakTime,mStater));
            mStater.handAction(Stater.TAKE_UI);
        }
    }

    private void changeToSpeakInTurnState() {
        if(mStater!=null){
           mStater.setState(new SpeakInTurnState(mBtnMike1,mBtnMike2,mTvSpeakTime,mStater));
           mStater.handAction(Stater.TAKE_UI);
           mStater.handAction(Stater.SPEAK_IN_TURN);
        }
    }

    private void banWheat() {
        mBtnMike1.setEnabled(false);
        setOpenWheat(false);
    }

    private void takeUI() {
        setOpenWheat(false);
        if(mLiveBean!=null){
           mBtnMike1.setEnabled(mLiveBean.isAudienceCanNotSpeak());
        }

        mTvSpeakTime.setVisibility(View.GONE);
        mBtnMike1.setVisibility(View.VISIBLE);
        mBtnMike2.setVisibility(View.VISIBLE);

        mBtnMike2.setText(WordUtil.getString(R.string.lower_wheat));
        mBtnMike1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        mBtnMike2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               downWheat();
            }
        });
    }

    /*发送下麦申请,接收到socket会进行相应变更处理*/
    private void downWheat() {
        AppCompatActivity appCompatActivity=(AppCompatActivity)mBtnMike1.getContext();
        SkPowerBehavior skPowerBehavior= CacheBehaviorFactory.getInstance().getSkPowerBehavior(appCompatActivity);
        if(skPowerBehavior!=null){
          skPowerBehavior.downWheat(null,-1,true);
        }
    }


    /*设置开麦和关麦*/
    private void setOpenWheat(boolean isOpenWheat){
        mIsOpenWheat=isOpenWheat;
        if(mLiveActivityLifeModel !=null){
           mLiveActivityLifeModel.changeSpeakState(isOpenWheat);
        }
        if(mIsOpenWheat){
        mBtnMike1.setText(WordUtil.getString(R.string.close_wheat));
        mBtnMike1.setBackground(ResourceUtil.getDrawable(R.drawable.bg_color_global_radiu_2,true));
        }else{
        mBtnMike1.setText(WordUtil.getString(R.string.open_wheat));
        mBtnMike1.setBackground(null);
        }
    }
    private void toggle(){
        setOpenWheat(!mIsOpenWheat);
    }
}
