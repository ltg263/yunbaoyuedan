package com.yunbao.chatroom.business.state.audience;

import android.support.annotation.NonNull;
import android.widget.TextView;
import com.yunbao.chatroom.business.state.Stater;

public  abstract class State{
    protected TextView mBtnMike1;
    protected TextView mBtnMike2;
    protected TextView mTvSpeakTime;
    protected Stater mStater;

    public State(@NonNull TextView mBtnMike1, @NonNull TextView mBtnMike2, @NonNull TextView tvSpeakTime,Stater stater){
            this.mBtnMike1= mBtnMike1;
            this.mBtnMike2=mBtnMike2;
            this.mTvSpeakTime=tvSpeakTime;
            this.mStater=stater;

        }
    public abstract void handAction(int action);

    public void clear(){

    }
    }

