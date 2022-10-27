package com.yunbao.common.views;

import android.content.Context;
import android.view.ViewGroup;
import com.yunbao.common.utils.RouteUtil;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity中的首页，附近 的子页面
 */

public abstract class AbsMainHomeChildViewHolder extends AbsMainViewHolder {
    protected ActionListener mActionListener;

    public AbsMainHomeChildViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public AbsMainHomeChildViewHolder(Context context, ViewGroup parentView,Object... args) {
        super(context, parentView,args);
    }

    public void forwardUserHome(String toUid) {
        RouteUtil.forwardUserHome(toUid);
    }

    public interface ActionListener {
        void onRefreshCompleted();
    }



    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }
}
