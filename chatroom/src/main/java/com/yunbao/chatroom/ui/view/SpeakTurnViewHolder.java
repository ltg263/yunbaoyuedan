package com.yunbao.chatroom.ui.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.business.liveobsever.LifeObjectHolder;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.views.AbsViewHolder2;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.business.socket.dispatch.DispatchSocketProxy;
import com.yunbao.chatroom.business.socket.dispatch.callback.WheeledWheatListner;
import com.yunbao.chatroom.business.socket.dispatch.mannger.WheeledWheatMannger;

public class SpeakTurnViewHolder extends AbsViewHolder2 implements View.OnClickListener, WheeledWheatListner {
    private CheckBox mBtnSpeakTurn;
    private LiveActivityLifeModel<DispatchSocketProxy> mLiveActivityLifeModel;
    private WheeledWheatMannger mWheeledWheatMannger;
    private WheelSpeakOpenListner mWheelSpeakOpenListner;

    public SpeakTurnViewHolder(Context context, ViewGroup parentView, WheelSpeakOpenListner mWheelSpeakOpenListner) {
        super(context, parentView, mWheelSpeakOpenListner);
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
        mWheelSpeakOpenListner= (WheelSpeakOpenListner) args[0];
    }


    @Override
    protected int getLayoutId() {
        return R.layout.view_speak_turn;
    }

    @Override
    public void init() {
        mBtnSpeakTurn=findViewById(R.id.btn_speak_turn);
        mLiveActivityLifeModel = LifeObjectHolder.getByContext(getActivity(), LiveActivityLifeModel.class);
        if( mLiveActivityLifeModel !=null){
            mWheeledWheatMannger=mLiveActivityLifeModel.getSocketProxy().getWheeledWheatMannger();
            boolean isStartWheeled=mWheeledWheatMannger.isStartWheeled();
            mBtnSpeakTurn.setChecked(isStartWheeled);
            if(mWheelSpeakOpenListner!=null){
                mWheelSpeakOpenListner.open(isStartWheeled);
            }

            mWheeledWheatMannger.addWheeledListner(this);
        }
        mBtnSpeakTurn = (CheckBox) findViewById(R.id.btn_speak_turn);
        mBtnSpeakTurn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    SpeakTurnViewHolder.this.onClick(v);
                }
                return true;
            }
        });


    }

    /*开启轮流发言*/
    private void openSpeakTurn() {
        if(!mLiveActivityLifeModel.hasNormalUser()){
            ToastUtil.show(R.string.wheat_no_greate_man_tip);
            return;
        }
        if(mWheeledWheatMannger!=null){
            mWheeledWheatMannger.startWheelWheet();
        }
    }

    /*关闭轮流发言*/
    private void closeSpeakTurn() {
        if(mWheeledWheatMannger!=null){
            mWheeledWheatMannger.cancleWheelWheet();
        }
    }

    private void toggleSpeakInTurn(View v) {
        boolean isOpenTurn=mBtnSpeakTurn.isChecked();
        if(!isOpenTurn){
            openSpeakTurn();
        }else{
            closeSpeakTurn();
        }

        if(mWheelSpeakOpenListner!=null){
            mWheelSpeakOpenListner.open(mWheeledWheatMannger.isStartWheeled());
        }
    }



    public interface WheelSpeakOpenListner{
        public void open(boolean isOpen);
    }

    @Override
    public void onClick(View v) {
        toggleSpeakInTurn(v);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mWheeledWheatMannger!=null){
           mWheeledWheatMannger.removeWheeledListner(this);
        }
        mWheelSpeakOpenListner=null;
    }




    public void setWheelSpeakOpenListner(WheelSpeakOpenListner wheelSpeakOpenListner) {
        mWheelSpeakOpenListner = wheelSpeakOpenListner;
    }

    @Override
    public void openWheeledWheat(boolean isOpen) {
        mBtnSpeakTurn.setChecked(isOpen);
    }
    @Override
    public void changeSpeakUser(UserBean userBean) {

    }
}
