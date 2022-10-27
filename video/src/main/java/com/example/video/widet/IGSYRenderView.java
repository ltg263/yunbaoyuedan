package com.example.video.widet;

import android.graphics.Matrix;
import android.view.View;

import com.example.video.util.MeasureHelper;

import java.io.File;

public interface IGSYRenderView {

    IGSYSurfaceListener getIGSYSurfaceListener();

    /**
     * Surface变化监听，必须
     */
    void setIGSYSurfaceListener(IGSYSurfaceListener surfaceListener);

    /**
     * 当前view高度，必须
     */
    int getSizeH();

    /**
     * 当前view宽度，必须
     */
    int getSizeW();

    /**
     * 实现该接口的view，必须
     */
    View getRenderView();

    /**
     * 渲染view通过MeasureFormVideoParamsListener获取视频的相关参数，必须
     */
    void setVideoParamsListener(MeasureHelper.MeasureFormVideoParamsListener listener);


    void onRenderResume();

    void onRenderPause();

    void releaseRenderAll();

    void setRenderMode(int mode);

    void setRenderTransform(Matrix transform);


    void setGLMVPMatrix(float[] MVPMatrix);


}
