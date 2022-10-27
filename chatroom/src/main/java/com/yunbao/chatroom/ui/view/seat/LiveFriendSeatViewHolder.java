package com.yunbao.chatroom.ui.view.seat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.bean.LiveAnthorBean;
import com.yunbao.chatroom.adapter.LiveFriendAnthorAdapter;

public class LiveFriendSeatViewHolder extends AbsLiveSeatViewHolder<LiveFriendAnthorAdapter> {
    private OnItemClickListner mOnItemClickListner;

    public LiveFriendSeatViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    @Override
    public LiveFriendAnthorAdapter initAdapter() {
        return new LiveFriendAnthorAdapter(null,valueFrameAnimator);
    }
    /**
     * Callback method to be invoked when an item in this RecyclerView has
     * been clicked.
     *
     * @param adapter  the adpater
     * @param view     The itemView within the RecyclerView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            if(mOnItemClickListner!=null){
                mOnItemClickListner.onItem(position, mLiveAnthorAdapter.getItem(position));
            }
    }

    public static interface OnItemClickListner{
        public void onItem(int position, LiveAnthorBean liveAnthorBean);
    }

    public void startHeartAnim(){
        if(mLiveAnthorAdapter!=null){
           mLiveAnthorAdapter.startHeartAnim();
        }
    }

    public void setOnItemClickListner(OnItemClickListner onItemClickListner) {
        mOnItemClickListner = onItemClickListner;
    }

    public void stopHeartAnim(){
        if(mLiveAnthorAdapter!=null){
           mLiveAnthorAdapter.stopHeartAnim();
        }
    }

    public boolean isCanSelectHeartBeat(){
        if(mLiveAnthorAdapter!=null){
        return   mLiveAnthorAdapter.isStartHeartBeat();
        }
        return false;
    }
}
