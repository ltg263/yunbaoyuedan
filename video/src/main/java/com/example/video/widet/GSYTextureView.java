package com.example.video.widet;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import com.example.video.util.GSYVideoType;
import com.example.video.util.MeasureHelper;

public class GSYTextureView extends TextureView implements TextureView.SurfaceTextureListener, IGSYRenderView, MeasureHelper.MeasureFormVideoParamsListener {
    private IGSYSurfaceListener mIGSYSurfaceListener;
    private MeasureHelper.MeasureFormVideoParamsListener mVideoParamsListener;
    private MeasureHelper measureHelper;
    private SurfaceTexture mSaveTexture;
    private Surface mSurface;

    private int videoWidth;
    private int videoHeight;

    public GSYTextureView(Context context) {
        super(context);
        init();
    }

    public GSYTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        measureHelper = new MeasureHelper(this, this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureHelper.prepareMeasure(widthMeasureSpec, heightMeasureSpec, (int) getRotation());
        setMeasuredDimension(measureHelper.getMeasuredWidth(), measureHelper.getMeasuredHeight());
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (GSYVideoType.isMediaCodecTexture()) {
            if (mSaveTexture == null) {
                mSaveTexture = surface;
                mSurface = new Surface(surface);
            } else {
                setSurfaceTexture(mSaveTexture);
            }
            if (mIGSYSurfaceListener != null) {
                mIGSYSurfaceListener.onSurfaceAvailable(mSurface);
            }
        } else {
            mSurface = new Surface(surface);
            if (mIGSYSurfaceListener != null) {
                mIGSYSurfaceListener.onSurfaceAvailable(mSurface);
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (mIGSYSurfaceListener != null) {
            mIGSYSurfaceListener.onSurfaceSizeChanged(mSurface, width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

        //清空释放
        if (mIGSYSurfaceListener != null) {
            mIGSYSurfaceListener.onSurfaceDestroyed(mSurface);
        }
        if (GSYVideoType.isMediaCodecTexture()) {
            return (mSaveTexture == null);
        } else {
            return true;
        }
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //如果播放的是暂停全屏了
        if (mIGSYSurfaceListener != null) {
            mIGSYSurfaceListener.onSurfaceUpdated(mSurface);
        }
    }

    @Override
    public IGSYSurfaceListener getIGSYSurfaceListener() {
        return mIGSYSurfaceListener;
    }

    @Override
    public void setIGSYSurfaceListener(IGSYSurfaceListener surfaceListener) {
        setSurfaceTextureListener(this);
        mIGSYSurfaceListener = surfaceListener;
    }

    @Override
    public int getSizeH() {
        return getHeight();
    }

    @Override
    public int getSizeW() {
        return getWidth();
    }




    @Override
    public View getRenderView() {
        return this;
    }

    @Override
    public void onRenderResume() {
    }

    @Override
    public void onRenderPause() {
    }

    @Override
    public void releaseRenderAll() {
    }

    @Override
    public void setRenderMode(int mode) {
    }

    @Override
    public void setRenderTransform(Matrix transform) {
        setTransform(transform);
    }

    @Override
    public void setGLMVPMatrix(float[] MVPMatrix) {

    }

    @Override
    public void setVideoParamsListener(MeasureHelper.MeasureFormVideoParamsListener listener) {
        mVideoParamsListener = listener;
    }

    @Override
    public int getCurrentVideoWidth() {

        return videoWidth;
    }

    @Override
    public int getCurrentVideoHeight() {

        return videoHeight;
    }

    @Override
    public int getVideoSarNum() {
        /*if (mVideoParamsListener != null) {
            return mVideoParamsListener.getVideoSarNum();
        }*/
        return 1;
    }

    @Override
    public int getVideoSarDen() {
        /*if (mVideoParamsListener != null) {
            return mVideoParamsListener.getVideoSarDen();
        }*/
        return 1;
    }

    public boolean setVideoWidth(int videoWidth) {
        if(this.videoWidth==videoWidth){
            return false;
        }
        this.videoWidth = videoWidth;
        return true;
    }

    public boolean setVideoHeight(int videoHeight) {
        if(this.videoHeight==videoHeight){
            return false;
        }
        this.videoHeight = videoHeight;
        return true;
    }

    /**
     * 添加播放的view
     */
    public static GSYTextureView addTextureView(Context context, ViewGroup textureViewContainer, int rotate,
                                                final IGSYSurfaceListener gsySurfaceListener,
                                                final MeasureHelper.MeasureFormVideoParamsListener videoParamsListener) {
        if (textureViewContainer.getChildCount() > 0) {
            textureViewContainer.removeAllViews();
        }
        GSYTextureView gsyTextureView = new GSYTextureView(context);
        gsyTextureView.setIGSYSurfaceListener(gsySurfaceListener);
        gsyTextureView.setVideoParamsListener(videoParamsListener);
        gsyTextureView.setRotation(rotate);
       // GSYRenderView.addToParent(textureViewContainer, gsyTextureView);
        return gsyTextureView;
    }
}