package com.yunbao.chatroom.business.state;

import android.widget.TextView;
import com.yunbao.chatroom.business.state.audience.NormalState;
import com.yunbao.chatroom.business.state.audience.State;

/*聊天室底部导航栏的状态机*/
public class Stater {
    /*
    展示当前状态的默认UI
    */
    public static final int TAKE_UI=1;

    /*
     接收到上麦到action
   */
    public static final int UP_WHEAT=2;

    /*
     接收到下麦到action
   */
    public static final int DOWN_WHEAT=3;
    /*
    禁麦
    */
    public static final int WHEAT_BAN=4;
    /*
   开麦
   */
    public static final int WHEAT_OPEN_LIMIT=5;
    /*
     开启轮流发言
   */
    public static final int SPEAK_IN_TURN=6;
    /*
      关闭轮流发言
    */
    public static final int SPEAK_OUT_TURN=7;

    /*
     轮到自己发言
   */
    public static final int SPEAK_IN_TURN_SELF=8;
    /*
     轮到别人发言
   */
    public static final int SPEAK_IN_TURN_UNSELF=9;

    private State mState;
    private TextView[]mViewArray;

    public Stater(TextView...viewArray) {
        mViewArray=viewArray;
        if(viewArray!=null){
            mState =new NormalState(viewArray[0],viewArray[1],viewArray[2],this);
        }
    }

    public void setState(State state) {
        mState = state;
    }

    public void handAction(int handAction){
        if(mState!=null){
           mState.handAction(handAction);
        }
    }
}
