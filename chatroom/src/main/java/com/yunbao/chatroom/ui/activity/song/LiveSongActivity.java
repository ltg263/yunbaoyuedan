package com.yunbao.chatroom.ui.activity.song;

import android.os.Bundle;
import android.widget.FrameLayout;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.LiveInfo;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.behavior.factory.AbsBehaviorFactory;
import com.yunbao.chatroom.business.behavior.factory.SongBehaviorFactory;
import com.yunbao.chatroom.business.socket.base.callback.WheatControllListner;
import com.yunbao.chatroom.business.socket.song.SongSocketProxy;
import com.yunbao.chatroom.business.socket.song.callback.SongWheatListner;
import com.yunbao.chatroom.business.socket.song.impl.SongSmsListnerImpl;
import com.yunbao.chatroom.ui.activity.LiveActivity;
import com.yunbao.chatroom.ui.view.seat.LiveSongSeatViewHolder;

public  abstract class LiveSongActivity extends LiveActivity<SongSmsListnerImpl, SongSocketProxy, LiveSongSeatViewHolder>
 implements SongWheatListner,
        WheatControllListner
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected AbsBehaviorFactory onCreateBehaviorFactory() {
        return new SongBehaviorFactory();
    }
    @Override
    protected SongSocketProxy onCreateSocketProxy(String chatserver, SongSmsListnerImpl socketMessageBrider, LiveInfo parseLiveInfo) {
        return new SongSocketProxy(chatserver,socketMessageBrider,parseLiveInfo);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_song;
    }
    @Override
    protected LiveSongSeatViewHolder initSeatViewHolder(FrameLayout vpSeatContainer) {
        return new LiveSongSeatViewHolder(this,vpSeatContainer);
    }

    @Override
    protected SongSmsListnerImpl initSocketMessageBride() {
        SongSmsListnerImpl songSmsListner=new SongSmsListnerImpl(this);
        songSmsListner.setWheatControllListner(this);
        songSmsListner.setWheatLisnter(this);
        return songSmsListner;
    }
    @Override
    public void openSpeak(UserBean userBean, boolean isOpen) {

    }
    @Override
    public void userAudioOpen(String uid, boolean isOpen) {
        if(mLiveSeatViewHolder!=null){
            mLiveSeatViewHolder.onTalkStateChange(uid,isOpen);
        }
    }
    @Override
    public void applyBosssWheat(String uid, boolean isUp) {

    }
    @Override
    public void upNormalWheatSuccess(UserBean userBean, int positon) {

    }
    @Override
    public void upBossWheatSuccess(UserBean userBean) {
        String tip=getString(R.string.up_boss_wheat,userBean.getUserNiceName());
        sendLocalTip(tip);
        mLiveSeatViewHolder.onUpperSeat(userBean,7);
    }
    @Override
    public void resfuseUpWheat(String uid) {

    }
    @Override
    public int downWheat(UserBean userBean, boolean isSelf) {
        int index= mLiveSeatViewHolder.onDownSeat(userBean);
        if(index!=-1){
            showDownWheatTip(index,userBean);
        }
        return index;
    }

    /*显示下麦信息*/
    private void showDownWheatTip(int index,UserBean userBean) {
        String tip="";
        if(index==7){
            tip=getString(R.string.boss_down_wheat2);
        }else{
            tip=getString(R.string.user_down_wheat,userBean.getUserNiceName(),Integer.toString(index+1));
        }
        sendLocalTip(tip);
    }
    @Override
    public void applySinger(UserBean userBean, int sitId) {

    }

    @Override
    public void applySingerResult(UserBean userBean, int sitId,boolean isArgee) {
        if(isArgee){
            try {
                String tip=getString(R.string.up_normal_wheat,userBean.getUserNiceName(),Integer.toString(sitId));
                sendLocalTip(tip);
                mLiveSeatViewHolder.onUpperSeat(userBean,sitId-1);
            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            // TODO: 2020-12-17 20201217 产品让修改
        if( CommonAppConfig.getInstance().isSelf(userBean)){
//            String tip=getString(R.string.refuse_wheat_tip);
//            sendLocalTip(tip);
            ToastUtil.show(WordUtil.getString(R.string.refuse_wheat_tip));
        }
      }
    }
}
