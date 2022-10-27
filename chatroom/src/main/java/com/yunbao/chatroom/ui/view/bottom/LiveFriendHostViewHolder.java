package com.yunbao.chatroom.ui.view.bottom;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.ui.dialog.FriendApplyListFragment;

public class LiveFriendHostViewHolder extends LiveHostBottomViewHolder {
    public LiveFriendHostViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_live_bottom_host;
    }

    @Override
    protected AbsDialogFragment getApplyDialog() {
        return new FriendApplyListFragment();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
