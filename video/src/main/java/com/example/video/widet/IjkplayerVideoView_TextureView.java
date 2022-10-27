package com.example.video.widet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
 
import java.io.IOException;
 
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
 
/**
 * Created by wjj on 2018/11/12.
 */
 
public class IjkplayerVideoView_TextureView extends FrameLayout implements TextureView.SurfaceTextureListener  {
    public IjkplayerVideoView_TextureView(@NonNull Context context) {
        super(context);
        initVideoView(context);
    }
    public IjkplayerVideoView_TextureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }
    public IjkplayerVideoView_TextureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }
    @SuppressLint("NewApi")
    public IjkplayerVideoView_TextureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVideoView(context);
    }

    /**
     * 由ijkplayer提供，用于播放视频，需要给他传入一个surfaceView
     */
    private IMediaPlayer mMediaPlayer = null;
 
    /**
     * 视频文件地址
     */
    private String mPath = "";
    private TextureView textureView;
    //private VideoPlayerListener listener;
    private Context mContext;
    private Surface surface;
 
    private void initVideoView(Context context) {
        mContext = context;
        //获取焦点
//        setFocusable(true);
    }
 
    /**
     * 设置视频地址。
     * 根据是否第一次播放视频，做不同的操作。
     * @param path 视频地址
     */
    public void setVideoPath(String path) {
        if (TextUtils.equals("", mPath)) {
            //如果是第一次播放视频，那就创建一个新的TextureView
            mPath = path;
            createTextureView();
        } else {
            //否则就直接load
            mPath = path;
            load();
        }
    }
 
    /**
     * 新建一个TextureView
     */
    private void createTextureView() {
        textureView = null;
        textureView = new TextureView(getContext());
        textureView.setSurfaceTextureListener(this);
 
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        textureView.setLayoutParams(layoutParams);
        addView(textureView);
    }
 
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int i, int i1) {
        this.surface = new Surface(surface);
        // surfaceTexture数据通道准备就绪，打开播放器
        load();
    }
 
 
 
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
 
    }
 
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        surface.release();
        return false;
    }
 
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
 
    }
 
 
 
    /**
     * 加载视频
     */
    private void load() {
        //每次都要重新创建IMediaPlayer
        createPlayer();
        try {
            mMediaPlayer.setDataSource(mPath);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setVolume(0f,0f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //给mediaPlayer设置视图
        mMediaPlayer.setSurface(surface);
        mMediaPlayer.prepareAsync();
    }

    /**
     * 创建一个新的player
     */
    private void createPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.release();
        }
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        //开启硬解码
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        mMediaPlayer = ijkMediaPlayer;
       /* if (listener != null) {
            mMediaPlayer.setOnPreparedListener(listener);
            mMediaPlayer.setOnInfoListener(listener);
            mMediaPlayer.setOnSeekCompleteListener(listener);
            mMediaPlayer.setOnBufferingUpdateListener(listener);
            mMediaPlayer.setOnErrorListener(listener);
        }*/
    }

   /* public void setListener(VideoPlayerListener listener) {
        this.listener = listener;
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnPreparedListener(listener);
        }
    }*/
 
    /**
     * -------======--------- 下面封装了一下控制视频的方法
     */
 
    public void setVolume(float v1,float v2 ){
        //关闭声音
        if (mMediaPlayer!=null){
            mMediaPlayer.setVolume(v1,v2);
        }
    }
 
    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }
 
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
 
    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }
 
    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }
 
 
    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
    }
 
    public void reCreate(){
        if (mMediaPlayer == null) {
            mMediaPlayer.reset();
        }
    }
 
 
 
    public long getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public long getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }
 
 
    public void seekTo(long l) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(l);
        }
    }
}