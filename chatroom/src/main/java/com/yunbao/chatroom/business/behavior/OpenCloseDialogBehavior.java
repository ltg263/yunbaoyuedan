package com.yunbao.chatroom.business.behavior;

import android.view.View;
import com.yunbao.common.dialog.BottomDealFragment;
import com.yunbao.common.event.ShowLiveRoomFloatWindowEvent;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.live.presenter.ILivePresenter;
import com.yunbao.chatroom.ui.activity.LiveActivity;

import org.greenrobot.eventbus.EventBus;

/*打开关闭聊天室弹框*/
public class OpenCloseDialogBehavior extends BaseBehavior {
    public void  openCloseDialog(LiveActivity liveActivity,String title,final  ILivePresenter livePresenter){
      if(liveActivity==null||livePresenter==null){
          return;
      }
        BottomDealFragment bottomDealFragment=new BottomDealFragment();
        bottomDealFragment.setDialogButtonArray(new BottomDealFragment.DialogButton(title, new BottomDealFragment.ClickListnter() {
            @Override
            public void click(View view) {
                if(livePresenter!=null){
                    livePresenter.exitRoom();
                }
            }
        },liveActivity.getResources().getColor(R.color.red)));
        bottomDealFragment.show(liveActivity.getSupportFragmentManager());
    }

    //打开带有小窗口选项的弹窗
    public void  openCloseDialogWithFloat(final LiveActivity liveActivity, String title1, String title2, final  ILivePresenter livePresenter){
        if(liveActivity==null||livePresenter==null){
            return;
        }
        BottomDealFragment bottomDealFragment=new BottomDealFragment();
        bottomDealFragment.setDialogButtonArray(new BottomDealFragment.DialogButton(title1, new BottomDealFragment.ClickListnter() {
            @Override
            public void click(View view) {
                EventBus.getDefault().post(new ShowLiveRoomFloatWindowEvent());
                if(livePresenter!=null){
                    livePresenter.exitRoom();
                }
            }
        },liveActivity.getResources().getColor(R.color.global)),new BottomDealFragment.DialogButton(title2, new BottomDealFragment.ClickListnter() {
                    @Override
                    public void click(View view) {
                        if(livePresenter!=null){
                            livePresenter.exitRoom();
                        }
                    }
                },liveActivity.getResources().getColor(R.color.red))
        );
        bottomDealFragment.show(liveActivity.getSupportFragmentManager());
    }


}
