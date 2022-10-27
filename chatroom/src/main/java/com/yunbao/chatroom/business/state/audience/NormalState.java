package com.yunbao.chatroom.business.state.audience;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.yunbao.chatroom.R;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.business.behavior.ApplyQuequeBehavior;
import com.yunbao.chatroom.business.behavior.factory.CacheBehaviorFactory;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.business.socket.SocketProxy;
import com.yunbao.chatroom.business.state.Stater;
import com.yunbao.chatroom.ui.dialog.LineUpDialogFragment;
import com.yunbao.chatroom.ui.dialog.UpperWheatDialogFragment;
import static com.yunbao.chatroom.business.state.Stater.TAKE_UI;
import static com.yunbao.chatroom.business.state.Stater.UP_WHEAT;

/*普通模式*/
public class NormalState extends State{
    protected FragmentActivity mFragmentActivity;
    private LiveBean mLiveBean;
    private SocketProxy mSocketProxy;


    public NormalState(@NonNull TextView mBtnMike1, @NonNull TextView mBtnMike2,TextView tvSpeakTime,Stater stater) {
        super(mBtnMike1, mBtnMike2, tvSpeakTime,stater);
        Context context=mBtnMike1.getContext();
        if(context instanceof FragmentActivity){
           mFragmentActivity= (FragmentActivity) mBtnMike1.getContext();
            LiveActivityLifeModel liveActivityLifeModel = LiveActivityLifeModel.getByContext(mFragmentActivity,
                    LiveActivityLifeModel.class);
            if(liveActivityLifeModel !=null){
                mLiveBean= liveActivityLifeModel.getLiveBean();
            }
        }
    }

    @Override
    public void handAction(int action) {
        if(action==TAKE_UI){
            changeUI();
        }else if(action==UP_WHEAT){
            changeToUpperWheatState();
        }
    }

    private void changeToUpperWheatState() {
        UpperWheatState upperWheatState=new UpperWheatState(mBtnMike1,mBtnMike2,mTvSpeakTime,mStater);
        mStater.setState(upperWheatState);
        mStater.handAction(TAKE_UI);
    }

    public void changeUI() {
            if(mLiveBean!=null){
               mLiveBean.setAudienceCanNotSpeak(false);
            }
            mBtnMike1.setVisibility(View.GONE);
            mBtnMike2.setText(R.string.join);
            mBtnMike2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialog();
                }
            });
            mTvSpeakTime.setVisibility(View.GONE);
        }

    private void openDialog() {
        if(mFragmentActivity==null){
            return;
        }
      final ApplyQuequeBehavior applyQuequeBehavior= CacheBehaviorFactory.getInstance().getApplyQueBehavior(mFragmentActivity);
        if(applyQuequeBehavior==null){
            return;
        }
        if(applyQuequeBehavior.isApplying()){
            showLineUpDialog();
        }else{
            showUpperUpperWheatDialogFragment();
        }
    }

    private void showUpperUpperWheatDialogFragment() {
          UpperWheatDialogFragment upperWheatDialogFragment=new UpperWheatDialogFragment();
          upperWheatDialogFragment.show(mFragmentActivity.getSupportFragmentManager());
    }

    private void showLineUpDialog() {
        LineUpDialogFragment lineUpDialogFragment=new LineUpDialogFragment();
        lineUpDialogFragment.show(mFragmentActivity.getSupportFragmentManager());
    }

    @Override
    public void clear() {
        mSocketProxy=null;
        mLiveBean=null;
    }
}