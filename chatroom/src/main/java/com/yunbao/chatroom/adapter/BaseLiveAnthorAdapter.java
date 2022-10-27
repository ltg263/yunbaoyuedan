package com.yunbao.chatroom.adapter;

import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.bean.LiveAnthorBean;
import com.yunbao.common.custom.ValueFrameAnimator;
import java.util.List;

public abstract class BaseLiveAnthorAdapter<T extends LiveAnthorBean> extends BaseRecyclerAdapter<T, BaseReclyViewHolder> {
    protected ValueFrameAnimator mValueFrameAnimator;

    public BaseLiveAnthorAdapter(List<T> data,ValueFrameAnimator valueFrameAnimator) {
        super(data);
        this.mValueFrameAnimator=valueFrameAnimator;
    }

    public ValueFrameAnimator getValueFrameAnimator() {
        return mValueFrameAnimator;
    }

    public void release(){
        mValueFrameAnimator=null;
    }

}
