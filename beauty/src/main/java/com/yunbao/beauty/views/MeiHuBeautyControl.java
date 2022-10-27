package com.yunbao.beauty.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yunbao.beauty.interfaces.OnBottomShowListener;
import com.yunbao.beauty.interfaces.OnCaptureListener;
import com.yunbao.beauty.utils.MhDataManager;

public class MeiHuBeautyControl extends RelativeLayout {

    private BeautyViewHolder beautyViewHolder;
    private OnCaptureListener mOnCaptureListener;

    private OnBottomShowListener mOnBottomShowListener;

    public void setOnCaptureListener(OnCaptureListener onCaptureListener) {
        mOnCaptureListener = onCaptureListener;
    }

    public void setOnBottomShowListener(OnBottomShowListener onBottomShowListener ) {
        mOnBottomShowListener = onBottomShowListener;
    }

    public MeiHuBeautyControl(@NonNull Context context) {
        super(context);
    }

    public MeiHuBeautyControl(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MeiHuBeautyControl(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (beautyViewHolder == null){
            beautyViewHolder = new BeautyViewHolder(getContext(),this);
            MhDataManager.getInstance().setBeautyViewHolder(beautyViewHolder);
            beautyViewHolder.setOnCaptureListener(new OnCaptureListener() {
                @Override
                public void OnCapture() {
                    if (mOnCaptureListener != null){
                        mOnCaptureListener.OnCapture();
                    }
                }
            });
            beautyViewHolder.setOnBottomShowListener(new OnBottomShowListener() {

                @Override
                public void onShow(boolean show) {
                    if (mOnBottomShowListener != null){
                        mOnBottomShowListener.onShow(show);
                    }
                }
            });
            beautyViewHolder.addToParent();
        }
    }

    public View getCenterViewContainer(){
        return beautyViewHolder.getCenterViewContainer();
    }


    public ImageView getRecordView(){
        return beautyViewHolder.getRecordView();
    }


    public void showViewContainer(boolean isShow){
        beautyViewHolder.showViewContainer(isShow);
    }

}
