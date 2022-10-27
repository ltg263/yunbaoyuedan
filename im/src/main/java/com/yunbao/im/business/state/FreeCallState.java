package com.yunbao.im.business.state;

import com.yunbao.common.Constants;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.im.business.CallIMHelper;

public class FreeCallState implements ICallState {


    @Override
    public void handle(int action, int role) {
        CallStateMachine callStateMachine=CallStateMachine.getInstance();
        if(action==STATE_CALL_START){
            RouteUtil.forwardCallActivity(callStateMachine.currentRole,callStateMachine.roomId,callStateMachine.callType,callStateMachine.toUserBean);
            WaitState waitState=new WaitState();
            callStateMachine.setCurrentState(waitState);
            waitState.handle(action,role);
            if(role== Constants.ROLE_ANTHOR){
             CallIMHelper.sendStart(Constants.CHAT_TYPE_AUDIO,callStateMachine.toUserBean.getId());
            }
        }
    }
}
