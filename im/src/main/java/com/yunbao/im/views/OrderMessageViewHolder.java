package com.yunbao.im.views;

import android.content.Context;
import android.view.ViewGroup;
import com.yunbao.common.views.AbsViewHolder2;
import com.yunbao.im.R;

public class OrderMessageViewHolder extends AbsViewHolder2 {

    public OrderMessageViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_im_order_message;
    }

    @Override
    public void init() {

    }
}
