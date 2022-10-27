package com.yunbao.chatroom.business.socket.song.impl;

import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.business.socket.base.BaseSocketMessageLisnerImpl;
import com.yunbao.chatroom.business.socket.base.callback.BaseSocketMessageListner;
import com.yunbao.chatroom.business.socket.base.callback.WheatControllListner;
import com.yunbao.chatroom.business.socket.song.callback.SongWheatListner;

/*点歌实现类桥梁,你可以不必在一个类里面实现所有的接口方法,哪里需要set哪里*/
public class SongSmsListnerImpl extends BaseSocketMessageLisnerImpl implements SongWheatListner
,WheatControllListner
{
    private SongWheatListner mWheatLisnter;
    private WheatControllListner mWheatControllListner;

    public SongSmsListnerImpl(BaseSocketMessageListner baseSocketMessageListner) {
        super(baseSocketMessageListner);
    }

    @Override
    public void applyBosssWheat(String uid, boolean isUp) {
        if(mWheatLisnter!=null){
            mWheatLisnter.applyBosssWheat(uid,isUp);
        }
    }
    @Override
    public void upNormalWheatSuccess(UserBean userBean, int positon) {
        if(mWheatLisnter!=null){
            mWheatLisnter.upNormalWheatSuccess(userBean,positon);
        }
    }
    @Override
    public void upBossWheatSuccess(UserBean userBean) {
        if(mWheatLisnter!=null){
            mWheatLisnter.upBossWheatSuccess(userBean);
        }
    }
    @Override
    public void resfuseUpWheat(String uid) {
        if(mWheatLisnter!=null){
            mWheatLisnter.resfuseUpWheat(uid);
        }
    }
    @Override
    public int downWheat(UserBean userBean, boolean isSelf) {
        if(mWheatLisnter!=null){
            return mWheatLisnter.downWheat(userBean,isSelf);
        }
        return -1;
    }

    public void setWheatLisnter(SongWheatListner wheatLisnter) {
        mWheatLisnter = wheatLisnter;
    }

    public void setWheatControllListner(WheatControllListner wheatControllListner) {
        mWheatControllListner = wheatControllListner;
    }

    @Override
    public void openSpeak(UserBean userBean, boolean isOpen) {
        if(mWheatControllListner!=null){
            mWheatControllListner.openSpeak(userBean,isOpen);
        }
    }
    @Override
    public void userAudioOpen(String uid, boolean isOpen) {
        if(mWheatControllListner!=null){
            mWheatControllListner.userAudioOpen(uid,isOpen);
        }
    }

    @Override
    public void clear() {
        super.clear();
        mWheatControllListner=null;
        mWheatLisnter=null;
    }
    @Override
    public void applySinger(UserBean userBean, int sitId) {
        if(mWheatLisnter!=null){
           mWheatLisnter.applySinger(userBean,sitId);
        }
    }

    @Override
    public void applySingerResult(UserBean userBean, int sitId, boolean isArgee) {
        if(mWheatLisnter!=null){
            mWheatLisnter.applySingerResult(userBean,sitId,isArgee);
        }
    }
}
