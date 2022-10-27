package com.yunbao.chatroom.business.behavior.song;

import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.business.behavior.WatchApplyBehavior;
import com.yunbao.chatroom.business.socket.song.SongSocketProxy;
import com.yunbao.chatroom.business.socket.song.callback.SongWheatListner;

public class SongWatchApplyBehavior extends WatchApplyBehavior<SongSocketProxy> implements SongWheatListner {

    @Override
    public void watch(WatchApplyListner watchApplyListner) {
        mWatchApplyListner=watchApplyListner;
        if(mSocketProxy!=null){
           mSocketProxy.getWheatMannger().addWheatListner(this );
        }
    }

    @Override
    public void applyBosssWheat(String uid, boolean isUp) {
       if(isUp){
           mWatchApplyListner.watch(isUp);
       }
    }

    @Override
    public void unSubscribe() {
        super.unSubscribe();
        if(mSocketProxy!=null){
            mSocketProxy.getWheatMannger().removeWheatListner(this );
        }
    }

    @Override
    public void upNormalWheatSuccess(UserBean userBean, int positon) {

    }

    @Override
    public void upBossWheatSuccess(UserBean userBean) {

    }

    @Override
    public void resfuseUpWheat(String uid) {

    }
    @Override
    public int downWheat(UserBean userBean, boolean isSelf) {
        return -1;
    }

    @Override
    public void applySinger(UserBean userBean, int sitId) {
    }
    @Override
    public void applySingerResult(UserBean userBean, int sitId, boolean isArgee) {

    }
}
