package com.yunbao.chatroom.business.behavior.spatch;

import android.view.View;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.behavior.ApplyResultBehavior;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.dispatch.DispatchSocketProxy;
import com.yunbao.chatroom.business.socket.dispatch.mannger.WheeledWheatMannger;

public class SpApplyResultBehavior extends ApplyResultBehavior<DispatchSocketProxy> {
    @Override
    public void agree(UserBean userBean, LifecycleProvider lifecycleProvider, View view,SuccessListner successListner) {
        if(mSocketProxy!=null&&mLiveBean!=null){
            WheeledWheatMannger wheelMannnger=mSocketProxy.getWheeledWheatMannger();
            if(wheelMannnger!=null&&wheelMannnger.isStartWheeled()){
                ToastUtil.show(WordUtil.getString(R.string.wheat_speak_tip1));
                return;
            }
           mSocketProxy.getWheatMannger().agreeUserUpBossWheat(userBean,mLiveBean.getStream(),view,lifecycleProvider,successListner);
        }
    }


    @Override
    public void refuse(UserBean userBean, LifecycleProvider lifecycleProvider,SuccessListner successListner) {
        if(mSocketProxy!=null&&mLiveBean!=null){
           mSocketProxy.getWheatMannger().refuseUserUpBossWheat(userBean);
            successListner.success();
        }
    }
}
