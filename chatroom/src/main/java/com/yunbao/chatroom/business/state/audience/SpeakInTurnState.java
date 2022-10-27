package com.yunbao.chatroom.business.state.audience;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.business.liveobsever.LifeObjectHolder;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.business.socket.dispatch.DispatchSocketProxy;
import com.yunbao.chatroom.business.socket.dispatch.mannger.WheeledWheatMannger;
import com.yunbao.chatroom.business.state.Stater;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/*轮流发言状态*/
public class SpeakInTurnState extends State implements View.OnClickListener {
    private int mSingleManTime=30;
    private Disposable mDisposable;
    private Drawable mSpeakSelfDrawable;
    private Drawable mSpeakUnSelfDrawable;
    private int mSpeakUnSelfTextColor;
    private DispatchSocketProxy mSocketProxy;
    private WheeledWheatMannger mWheeledWheatMannger;
    public SpeakInTurnState(@NonNull TextView mBtnMike1, @NonNull TextView mBtnMike2,TextView tvSpeakTime,Stater stater) {
        super(mBtnMike1, mBtnMike2,tvSpeakTime, stater);
        initResource();
        initLifeViewHolder((FragmentActivity)mBtnMike1.getContext());
    }

    private void initLifeViewHolder(FragmentActivity fragmentActivity) {
        LiveActivityLifeModel<DispatchSocketProxy> liveActivityLifeModel = LifeObjectHolder.getByContext(fragmentActivity, LiveActivityLifeModel.class);
        if(liveActivityLifeModel !=null){
            mSocketProxy= liveActivityLifeModel.getSocketProxy();
            mWheeledWheatMannger=mSocketProxy.getWheeledWheatMannger();
        }
    }

    private void initResource() {
        mSpeakSelfDrawable= ContextCompat.getDrawable(CommonAppContext.sInstance, com.yunbao.common.R.drawable.bg_color_global_radius_13);
        mSpeakUnSelfDrawable = ContextCompat.getDrawable(CommonAppContext.sInstance, com.yunbao.common.R.drawable.bg_color_black_alpha_99_radius_13);
        mSpeakUnSelfTextColor=Color.parseColor("#80FFFFFF");
    }


    @Override
    public void handAction(int action) {
        L.e("isSelf==action="+action);
        if(action==Stater.SPEAK_IN_TURN){
            startInTurn();
       } else if(action==Stater.TAKE_UI){
            takeUI();
       }
        else if(action==Stater.SPEAK_OUT_TURN){
            closeTurn();
        }else if(action==Stater.SPEAK_IN_TURN_UNSELF){
           takingUnSelf();
        }else if(action==Stater.SPEAK_IN_TURN_SELF){
            takingSelf();
        }else if(action==Stater.DOWN_WHEAT){
            downWheat();
        }

    }

    private void downWheat() {
        disposable();
        if(mStater!=null){
           mStater.setState(new NormalState(mBtnMike1,mBtnMike2,mTvSpeakTime,mStater));
           mStater.handAction(Stater.TAKE_UI);
        }
    }

    private void takingUnSelf() {
        mBtnMike1.setEnabled(false);
        mTvSpeakTime.setBackground(mSpeakUnSelfDrawable);
        mTvSpeakTime.setTextColor(mSpeakUnSelfTextColor);
        startInTurn();
    }

    private void takingSelf() {
        mBtnMike1.setEnabled(true);
        mTvSpeakTime.setTextColor(Color.WHITE);
        mTvSpeakTime.setBackground(mSpeakSelfDrawable);
        startInTurn();
    }

    private void closeTurn() {
        disposable();
        if(mStater!=null){
           mStater.setState(new UpperWheatState(mBtnMike1,mBtnMike2,mTvSpeakTime,mStater));
           mStater.handAction(Stater.TAKE_UI);
        }
        mBtnMike1.setOnClickListener(null);
    }

    /*开始倒计时*/
    private void startInTurn() {
        disposable();
        mDisposable= Observable.interval(1, TimeUnit.SECONDS).take(mSingleManTime+1).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                mTvSpeakTime.setText(WordUtil.getString(R.string.speak_inturn_tip,mSingleManTime-aLong));
            }
        });
    }

    /*关闭倒计时*/
    private void disposable() {
        if(mDisposable!=null&&!mDisposable.isDisposed()){
            mDisposable.dispose();
            mDisposable=null;
        }
    }

    private void takeUI() {
        mTvSpeakTime.setVisibility(View.VISIBLE);
        mBtnMike1.setText(WordUtil.getString(R.string.skip));
        mBtnMike1.setOnClickListener(this);
        mBtnMike1.setEnabled(false);
    }

    @Override
    public void clear() {
        super.clear();
        disposable();
    }
    @Override
    public void onClick(View v) {
       skipSpeak();
    }
    /*跳过说话*/
    private void skipSpeak() {
        if(mWheeledWheatMannger!=null){
           mWheeledWheatMannger.dropWheelWheet();
        }
    }
}
