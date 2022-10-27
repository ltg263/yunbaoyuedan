package com.yunbao.dynamic.widet;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

/*模拟RecyclerView的滑动事件,为了让非reclyview的部分能够响应behaveior*/
public class SimulateReclyViewTouchLisnter implements View.OnTouchListener {
    private static final float LIMIT_OFFECT=20;

    private RecyclerView mRecyclerView;
    private float startX;
    private float startY;
    private float endX;
    private float endY;

    public SimulateReclyViewTouchLisnter(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public SimulateReclyViewTouchLisnter() {
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action=event.getAction();
        if(action==MotionEvent.ACTION_DOWN){
            startX=event.getX();
            startY=event.getY();
        }else if(action==MotionEvent.ACTION_UP){
            if(Math.abs(endX-startX)<LIMIT_OFFECT&&Math.abs(endY-startY)<LIMIT_OFFECT){
               v.performClick();
            }
        }
            endX=event.getX();
            endY=event.getY();

        if(mRecyclerView!=null){
           mRecyclerView.onTouchEvent(event);
        }
        return true;
    }


    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }
}
