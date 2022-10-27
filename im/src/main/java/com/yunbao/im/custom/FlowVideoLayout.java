package com.yunbao.im.custom;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.yunbao.common.utils.ViewUtil;
import com.yunbao.im.R;

import org.apache.commons.io.FileSystemUtils;

public class FlowVideoLayout extends FrameLayout {
    private TXCloudVideoView mCloudView;
    private GestureDetector mSimpleOnGestureListener;
    private OnClickListener mClickListener;
    private boolean mMoveable;

    private int windowWidth;
    private int windowHeight;
    public FlowVideoLayout( Context context) {
        super(context);
        init(context);
    }

    public FlowVideoLayout( Context context,  AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlowVideoLayout( Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public FlowVideoLayout( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(getContext()).inflate(R.layout.widet_flow_video,this,true);
        mCloudView = (TXCloudVideoView) findViewById(R.id.cloudView);
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        windowWidth = outMetrics.widthPixels;
        windowHeight = outMetrics.heightPixels;
        initGestureListener();
    }

    private void initGestureListener() {
        mSimpleOnGestureListener = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (mClickListener != null) {
                    mClickListener.onClick(FlowVideoLayout.this);
                }
                return mMoveable;
            }
            @Override
            public boolean onDown(MotionEvent e) {
                return mMoveable;
            }
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (!mMoveable) {
                    return false;
                }
                ViewGroup.LayoutParams params = FlowVideoLayout.this.getLayoutParams();
                // 当 TRTCVideoView 的父容器是 RelativeLayout 的时候，可以实现拖动
                if (params instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) FlowVideoLayout.this.getLayoutParams();
                    int newX = (int) (layoutParams.leftMargin + (e2.getX() - e1.getX()));
                    int newY = (int) (layoutParams.topMargin + (e2.getY() - e1.getY()));

                    newX=newX<0?0:newX;
                    newY=newY<0?0:newY;

                    newX=newX>windowWidth-getMeasuredWidth()?windowWidth-getMeasuredWidth():newX;
                    newY=newY>windowHeight-getMeasuredHeight()?windowHeight-getMeasuredHeight():newY;


                    layoutParams.leftMargin = newX;
                    layoutParams.topMargin = newY;
                    FlowVideoLayout.this.setLayoutParams(layoutParams);
                }
                return mMoveable;
            }
        });

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mSimpleOnGestureListener.onTouchEvent(event);
            }
        });
    }

    public void setOnClick(OnClickListener clickListener){
        this.mClickListener=clickListener;
    }


    public void setMoveable(boolean enable) {
        mMoveable = enable;
    }


    public TXCloudVideoView getVideoView() {
        return mCloudView;
    }

}
