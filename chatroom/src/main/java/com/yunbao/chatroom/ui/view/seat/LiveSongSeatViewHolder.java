package com.yunbao.chatroom.ui.view.seat;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.LiveAnthorBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.adapter.LiveSongAnthorAdapter;
import com.yunbao.chatroom.business.socket.song.SongSocketProxy;
import com.yunbao.chatroom.ui.activity.song.LiveSongAudienceActivity;

import java.util.List;

public class LiveSongSeatViewHolder extends  AbsLiveSeatViewHolder<LiveSongAnthorAdapter>{
    public LiveSongSeatViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    @Override
    public LiveSongAnthorAdapter initAdapter() {
        return new LiveSongAnthorAdapter(null,valueFrameAnimator);
    }
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
       LiveAnthorBean liveAnthorBean= mLiveAnthorAdapter.getItem(position);
       UserBean userBean=liveAnthorBean.getUserBean();
       if(userBean!=null){
          openItemOpenUserDialog(position);
       }else{
           if(mContext!=null&&mContext instanceof LiveSongAudienceActivity&&!mLiveActivityLifeModel.isOnWheat(CommonAppConfig.getInstance().getUserBean())&&!liveAnthorBean.isBoss()){
              showUpperWheatDialog(position);
           }
       }
    }
    private void showUpperWheatDialog(final int position) {
        DialogUitl.showSimpleDialog(mFragmentActivity, WordUtil.getString(R.string.are_you_sure_to_apply_for_singer_mic), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if(mLiveActivityLifeModel==null){
                    return;
                }
                SongSocketProxy songSocketProxy =(SongSocketProxy) mLiveActivityLifeModel.getSocketProxy();
                songSocketProxy.getWheatMannger().sendSocketSingerApply(Integer.toString(position+1));
            }
        });
    }

    @Override
    public void setData(List userBeanList) {
        if(userBeanList!=null&&userBeanList.size()==8){
            LiveAnthorBean liveAnthorBean= (LiveAnthorBean) userBeanList.get(7);
            liveAnthorBean.setBoss(true);
        }
        super.setData(userBeanList);
    }
}
