package com.yunbao.common.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by cxf on 2018/9/26.
 */

public class MyRelativeLayout7 extends RelativeLayout {

    public MyRelativeLayout7(Context context) {
        super(context);
    }

    public MyRelativeLayout7(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelativeLayout7(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
