package com.yunbao.common.views;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Created by cxf on 2018/10/26.
 */

public abstract class AbsMainViewHolder extends AbsViewHolder2 {

    protected boolean mFirstLoadData = true;
    private boolean mShowed;

    public AbsMainViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public AbsMainViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }
    public void loadData() {

    }
    protected boolean isFirstLoadData() {
        if (mFirstLoadData) {
            mFirstLoadData = false;
            return true;
        }
        return false;
    }


    public void setShowed(boolean showed) {
        mShowed = showed;
    }

    public boolean isShowed() {
        return mShowed;
    }

    public void setFirstLoadData(boolean firstLoadData) {
        mFirstLoadData = firstLoadData;
    }
}
