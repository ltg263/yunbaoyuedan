package com.yunbao.chatroom.ui.activity.dispatch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.yunbao.common.Constants;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.business.behavior.OpenCloseDialogBehavior;
import com.yunbao.chatroom.business.behavior.StopLiveBehavior;
import com.yunbao.chatroom.event.AudioChangeEvent;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.chatroom.ui.view.bottom.LiveDisPatchHostViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LiveDispatchHostActivity extends LiveSpatchActivity {
    @Override
    protected int getRole() {
        return Constants.ROLE_HOST;
    }
    @Override
    protected void clickClose() {
        OpenCloseDialogBehavior openCloseDialogBehavior=new OpenCloseDialogBehavior();
        openCloseDialogBehavior.openCloseDialog(this,WordUtil.getString(R.string.close_live_room),mLivePresenter);
    }

    @Override
    protected void main(Bundle savedInstanceState) {
        super.main(savedInstanceState);

    }

    @Override
    public void exitSdkRoomSuccess() {
        super.exitSdkRoomSuccess();
        stopLive();
    }
    /*进入sdk直播房间成功*/
    @Override
    public void enterSdkRoomSuccess() {
        String stream=mLiveBean==null?null:mLiveBean.getStream();
        //更改直播状态
        ChatRoomHttpUtil.changeLive(1,stream).subscribe(new DefaultObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
            }
        });
    }

    /*调用结束直播*/
    private void stopLive() {
        StopLiveBehavior stopLiveBehavior=new StopLiveBehavior();
        stopLiveBehavior.stopLive(mLiveBean,this);
    }
    @Override
    public void main() {
        super.main();
        LiveDisPatchHostViewHolder liveHostBottomViewHolder =new LiveDisPatchHostViewHolder(this,mVpBottom);
        liveHostBottomViewHolder.addToParent();
    }

    /*监听到房间内用户声音流的改变发送socket*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioChangeEvent(AudioChangeEvent audioChangeEvent){
        if(mSocketProxy!=null){
           mSocketProxy.getWheatControllMannger().sendWheatIsOpenState(audioChangeEvent.getUid(),audioChangeEvent.isOpen());
        }
    }


    public static void forward(Context context, LiveBean liveBean){
        Intent intent=new Intent(context, LiveDispatchHostActivity.class);
        intent.putExtra(Constants.DATA,liveBean);
        context.startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();

    }
}
