package com.yunbao.chatroom.ui.view.seat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.bean.LiveAnthorBean;
import com.yunbao.chatroom.adapter.BaseLiveAnthorAdapter;
import com.yunbao.chatroom.adapter.LiveGossipAnthorAdapter;

public class LiveGossipSeatViewHolder extends AbsLiveSeatViewHolder {
    public LiveGossipSeatViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public BaseLiveAnthorAdapter<LiveAnthorBean> initAdapter() {
        return new LiveGossipAnthorAdapter(null,valueFrameAnimator);
    }
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        openItemOpenUserDialog(position);
    }
}
