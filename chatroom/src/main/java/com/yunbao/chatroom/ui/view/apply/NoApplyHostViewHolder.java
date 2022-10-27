package com.yunbao.chatroom.ui.view.apply;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.yunbao.chatroom.R;

public class NoApplyHostViewHolder extends AbsApplyHostViewHolder implements View.OnClickListener {

    public NoApplyHostViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    public NoApplyHostViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_no_apply_host;
    }

    @Override
    public void init() {
        setOnClickListner(R.id.btn_confirm,this);
    }

    @Override
    public void onClick(View v) {
        confirm();
    }

    private void confirm() {
        notifyState(APPLYING);
        removeFromParent();
    }
}
