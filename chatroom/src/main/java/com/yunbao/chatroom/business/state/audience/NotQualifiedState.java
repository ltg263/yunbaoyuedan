package com.yunbao.chatroom.business.state.audience;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import com.yunbao.chatroom.business.state.Stater;

public class NotQualifiedState extends State{
    public NotQualifiedState(@NonNull TextView mBtnMike1, @NonNull TextView mBtnMike2,TextView tvSpeakTime, Stater stater) {
        super(mBtnMike1, mBtnMike2,tvSpeakTime, stater);
    }
    @Override
    public void handAction(int action) {
        if(action==Stater.TAKE_UI){
            takeUI();
        }
    }

    private void takeUI() {
        mBtnMike1.setVisibility(View.GONE);
        mBtnMike2.setVisibility(View.GONE);
    }

}
