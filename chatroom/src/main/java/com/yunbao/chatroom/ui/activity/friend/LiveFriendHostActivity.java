package com.yunbao.chatroom.ui.activity.friend;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LiveAnthorBean;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.LiveEndResultBean;
import com.yunbao.chatroom.business.behavior.OpenCloseDialogBehavior;
import com.yunbao.chatroom.event.AudioChangeEvent;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.chatroom.ui.view.bottom.LiveFriendHostViewHolder;
import com.yunbao.chatroom.ui.view.seat.LiveFriendSeatViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LiveFriendHostActivity extends LiveFriendActivity implements CompoundButton.OnCheckedChangeListener {
    private RadioButton mRadioBtn1;
    private RadioButton mRadioBtn2;
    private RadioButton mRadioBtn3;

    @Override
    protected void clickClose() {
        OpenCloseDialogBehavior openCloseDialogBehavior=new OpenCloseDialogBehavior();
        openCloseDialogBehavior.openCloseDialog(this, WordUtil.getString(R.string.close_live_room),mLivePresenter);
    }

    /*关闭sdk直播房间*/
    private void closeRoom() {
        if(mLivePresenter!=null){
            mLivePresenter.exitRoom();
        }
    }
    @Override
    protected int getRole() {
        return Constants.ROLE_HOST;
    }

    @Override
    public void main() {
        super.main();
        LiveFriendHostViewHolder liveHostBottomViewHolder =new LiveFriendHostViewHolder(this,mVpBottom);
        liveHostBottomViewHolder.addToParent();
    }

    @Override
    protected void initSeatItemClickListner() {
        if(mLiveSeatViewHolder!=null){
            mLiveSeatViewHolder.setOnItemClickListner(new LiveFriendSeatViewHolder.OnItemClickListner() {
                @Override
                public void onItem(int position, LiveAnthorBean liveAnthorBean) {
                    UserBean userBean=liveAnthorBean.getUserBean();
                    if(userBean==null){
                      return;
                    }
                    mLiveSeatViewHolder.openUserDialog(userBean);
                }
            });
        }
    }

    @Override
    protected void initView() {
        super.initView();
        mRadioBtn1 = (RadioButton) findViewById(R.id.radio_btn_1);
        mRadioBtn2 = (RadioButton) findViewById(R.id.radio_btn_2);
        mRadioBtn3 = (RadioButton) findViewById(R.id.radio_btn_3);

        mRadioBtn1.setOnCheckedChangeListener(this);
        mRadioBtn2.setOnCheckedChangeListener(this);
        mRadioBtn3.setOnCheckedChangeListener(this);

    }

    @Override
    public void exitSdkRoomSuccess() {
        super.exitSdkRoomSuccess();
        stopLive();
    }

    /*调用结束直播*/
    private void stopLive() {
        if(mLiveBean!=null){
            ChatRoomHttpUtil.endRoom(mLiveBean.getStream()).subscribe(new DefaultObserver<LiveEndResultBean>() {
                @Override
                public void onNext(LiveEndResultBean liveEndResultBean) {
                    showLiveEndViewHolder(liveEndResultBean, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            });
        }
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
    /*监听到房间内用户声音流的改变发送socket*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioChangeEvent(AudioChangeEvent audioChangeEvent){
        if(mSocketProxy!=null){
            mSocketProxy.getWheatControllMannger().sendWheatIsOpenState(audioChangeEvent.getUid(),audioChangeEvent.isOpen());
        }
    }

    private void selectState(int id) {
        if(mSocketProxy==null){
            return;
        }
        if(id==R.id.radio_btn_1){
            mSocketProxy.getFriendStateMannger().sendSocketCardiacSelection();
        }else if(id==R.id.radio_btn_2){
            mSocketProxy.getFriendStateMannger().sendSocketHeartBeat();
        }else if(id==R.id.radio_btn_3){
            mSocketProxy.getFriendStateMannger().sendSocketNextGame();
        }
    }

    /*心动选择状态下,心动选择按钮和下一场不可点击*/
    @Override
    protected void stateAsCardiacSelection() {
        super.stateAsCardiacSelection();
        mRadioBtn1.setEnabled(false);
        mRadioBtn2.setEnabled(true);
        mRadioBtn3.setEnabled(true);
    }

    /*公布心动模式下,不可回退到心动选择*/
    @Override
    protected void stateAnnounceHeartbeat() {
        super.stateAnnounceHeartbeat();
        mRadioBtn1.setEnabled(false);
        mRadioBtn2.setEnabled(false);
        mRadioBtn3.setEnabled(true);
    }

    /*准备阶段*/
    @Override
    protected void stateAsPreparationLink() {
        super.stateAsPreparationLink();
        mRadioBtn1.setEnabled(true);
        mRadioBtn2.setEnabled(false);
        mRadioBtn3.setEnabled(false);
        mRgBtnProgress.clearCheck();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        float alpha=isChecked?1F:0.5F;
        buttonView.setAlpha(alpha);
        if(isChecked){
            int id=buttonView.getId();
            selectState(id);
        }
    }
    /*倒计时完成后自动进入公布心动*/
    @Override
    public void compelete() {
        mRadioBtn2.setChecked(true);
    }

    public static void forward(Context context, LiveBean liveBean){
        Intent intent=new Intent(context, LiveFriendHostActivity.class);
        intent.putExtra(Constants.DATA,liveBean);
        context.startActivity(intent);
    }
}
