package com.example.video.widet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yunbao.common.utils.ScreenDimenUtil;

import java.util.Map;

import cn.qqtheme.framework.util.ScreenUtils;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Author: wangkai(wangkai@tv365.net)
 * Date: 2018-10-08
 * Time: 15:01
 * Description:
 */

public class VideoPlayer extends FrameLayout implements TextureView.SurfaceTextureListener {

    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private int mCurrentState = STATE_IDLE;
    /**
     * 由ijkplayer提供，用于播放视频，需要给他传入一个surfaceView
     */
    private IMediaPlayer mMediaPlayer = null;
    /**
     * 视频文件地址
     */
    private String mPath ;
    /**
     * 视频请求header
     */
    private Map<String, String> mHeader;

    private GSYTextureView mSurfaceView;
    private Surface mSurface;
    private Context mContext;

    private VideoListener mListener;
    private AudioManager mAudioManager;
    private AudioFocusHelper mAudioFocusHelper;

    private float windowsWidth;
    private float windowHeight;
    private boolean isSkipLoopFilter=true;

    public VideoPlayer(@NonNull Context context) {
        this(context, null);
    }

    public VideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public void setSkipLoopFilter(boolean skipLoopFilter) {
        isSkipLoopFilter = skipLoopFilter;
    }

    //初始化
    private void init(Context context) {
        mContext = context;
        setBackgroundColor(Color.BLACK);
        createSurfaceView();
        mAudioManager = (AudioManager)mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioFocusHelper = new AudioFocusHelper();

        DisplayMetrics outMetrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
         windowsWidth = outMetrics.widthPixels;
         windowHeight = outMetrics.heightPixels;

    }

    //创建surfaceView
    private void createSurfaceView() {
        mSurfaceView=new GSYTextureView(mContext);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
                , LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mSurfaceView.setSurfaceTextureListener(this);
        addView(mSurfaceView,0,layoutParams);
    }

    //创建一个新的player
    private IMediaPlayer createPlayer() {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.setScreenOnWhilePlaying(true);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC,"skip_loop_filter",isSkipLoopFilter?0:48L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
        return ijkMediaPlayer;
    }

    private int mBufferRange;
    //设置ijkplayer的监听
    private boolean isLockSizeChange;
    private void setListener(IMediaPlayer player){

        player.setOnPreparedListener(mPreparedListener);
        player.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int code) {
                if(what==IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED){
                    isLockSizeChange=false;
                    int ration=code;
                    changeRotation(ration);
                }else if(what==IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                    if(mListener!=null){
                        mListener.videoSizeChangeCompelete();
                    }
                }
                return false;
            }
        });
        player.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
        player.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                if(mListener!=null){
                  return  mListener.onError(iMediaPlayer,i,i1);
                }
                return false;
            }
        });
        player.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
                mBufferRange=i;
            }
        });

        player.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                if(mListener!=null){
                    mListener.onCompletion(iMediaPlayer);
                }
            }
        });
    }

    private void changeRotation(int ration) {
        mSurfaceView.setRotation(ration);
    }

    public int getBufferRange() {
        return mBufferRange;
    }


    /**
     * 设置自己的player回调
     */

    public void setVideoListener(VideoListener listener){
        mListener = listener;
    }

    //设置播放地址
    public void setPath(String path) {
        setPath(path,null);
        isLockSizeChange=true;
    }

    public void setPath(String path, Map<String, String> header){
        mPath = path;
        mHeader = header;
    }

    //是否循环播放
    private boolean mLoopEnable = true;
    public void setLoopEnable(boolean loopEnable){
        mLoopEnable  = loopEnable;
    }

    private boolean mIsFullScreen = false;
    public void setFullScreenVideo(boolean fullScreenVideo){
        mIsFullScreen = fullScreenVideo;
    }

    //开始加载视频
    public void load()  {
        try {

            if(mMediaPlayer != null){
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
            mMediaPlayer = createPlayer();
            mMediaPlayer.setLooping(mLoopEnable);
            setListener(mMediaPlayer);
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setDataSource(mContext, Uri.parse(mPath),mHeader);
            mMediaPlayer.prepareAsync();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            mAudioFocusHelper.requestFocus();
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mAudioFocusHelper.abandonFocus();
        }
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            mAudioFocusHelper.abandonFocus();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mAudioFocusHelper.abandonFocus();
        }
    }


    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mAudioFocusHelper.abandonFocus();
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

    public boolean isPlaying(){
        if(mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public void resume(){
        if(mMediaPlayer != null) {

        }
    }
    //------------------  各种listener 赋值 ---------------------//

    private IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener(){
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            if(mListener != null){
                mListener.onPrepared(iMediaPlayer);
            }
            iMediaPlayer.start();
        }
    };


    private IMediaPlayer.OnVideoSizeChangedListener mVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            if(isLockSizeChange){
                return;
            }

            if (mIsFullScreen){
                mSurfaceView.setVideoWidth( ScreenDimenUtil.getInstance().getScreenWdith());
                mSurfaceView.setVideoHeight( ScreenDimenUtil.getInstance().getScreenHeight());
                mSurfaceView.requestLayout();
                return;
            }

            if(mSurfaceView.setVideoWidth(iMediaPlayer.getVideoWidth())|| mSurfaceView.setVideoHeight(iMediaPlayer.getVideoHeight())){
                mSurfaceView.requestLayout();
            }
        }
    };

    private void setLayoutHeight(ViewGroup.LayoutParams params,boolean isRotation,int height) {
        params.height= height;
    }

    private void setLayoutWidth(ViewGroup.LayoutParams params,boolean isRotation,int width) {
        params.width= width;
    }

    /**
     * Invoked when a {@link TextureView}'s SurfaceTexture is ready for use.
     *
     * @param surface The surface returned by
     *                {@link TextureView#getSurfaceTexture()}
     * @param width   The width of the surface
     * @param height  The height of the surface
     */

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.mSurface = new Surface(surface);
        load();
    }

    /**
     * Invoked when the {@link SurfaceTexture}'s buffers size changed.
     *
     * @param surface The surface returned by
     *                {@link TextureView#getSurfaceTexture()}
     * @param width   The new width of the surface
     * @param height  The new height of the surface
     */
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    /**
     * Invoked when the specified {@link SurfaceTexture} is about to be destroyed.
     * If returns true, no rendering should happen inside the surface texture after this method
     * is invoked. If returns false, the client needs to call {@link SurfaceTexture#release()}.
     * Most applications should return true.
     *
     * @param surface The surface about to be destroyed
     */
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    /**
     * Invoked when the specified {@link SurfaceTexture} is updated through
     * {@link SurfaceTexture#updateTexImage()}.
     *
     * @param surface The surface just updated
     */
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    //------------------  音频监听 ---------------------//

    /**
     * 音频焦点改变监听
     */
    private class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
        boolean startRequested = false;
        boolean pausedForLoss = false;
        int currentFocus = 0;

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (currentFocus == focusChange) {
                return;
            }

            currentFocus = focusChange;
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN://获得焦点
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT://暂时获得焦点
                    if (startRequested || pausedForLoss) {
                        start();
                        startRequested = false;
                        pausedForLoss = false;
                    }
                    if (mMediaPlayer != null)//恢复音量
                    {
                        mMediaPlayer.setVolume(1.0f, 1.0f);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS://焦点丢失
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://焦点暂时丢失
                    if (isPlaying()) {
                        pausedForLoss = true;
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://此时需降低音量
                    if (mMediaPlayer != null && isPlaying()) {
                        mMediaPlayer.setVolume(0.1f, 0.1f);
                    }
                    break;
            }
        }

        boolean requestFocus() {
            if (currentFocus == AudioManager.AUDIOFOCUS_GAIN) {
                return true;
            }

            if (mAudioManager == null) {
                return false;
            }

            int status = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status) {
                currentFocus = AudioManager.AUDIOFOCUS_GAIN;
                return true;
            }

            startRequested = true;
            return false;
        }

        boolean abandonFocus() {

            if (mAudioManager == null) {
                return false;
            }

            startRequested = false;
            int status = mAudioManager.abandonAudioFocus(this);
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status;
        }
    }

}
