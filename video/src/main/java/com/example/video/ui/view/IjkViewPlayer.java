package com.example.video.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;
import com.example.video.R;
import com.example.video.widet.VideoListener;
import com.example.video.widet.VideoPlayer;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.L;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class IjkViewPlayer extends AbsPlayViewHolder implements View.OnClickListener{
    private static final String TAG = "IjkViewPlayer";
    private VideoPlayer videoPlayer;
    private ImageView mImgCover;
    private MediaController mMediaController;
    private boolean isPerpare;


    public IjkViewPlayer(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    @Override
    public void init() {
        super.init();
        videoPlayer = findViewById(R.id.video_player);
        mImgCover = (ImageView) findViewById(R.id.img_cover);
        mMediaController=new MediaController(mContext);
        mMediaController.setMediaPlayer(this);
        mMediaController.setAnchorView(this.mContentView);
        mMediaController.setEnabled(true);
      //  mContentView.setOnClickListener(this);

    }


    public void play(String url) {
        videoPlayer.setPath(url);
        try {
            videoPlayer.load();
        }catch (Exception e){
            e.printStackTrace();
        }
        videoPlayer.setVideoListener(new VideoListener() {
            @Override
            public void videoSizeChangeCompelete() {
                ImgLoader.clear(mContext,mImgCover);
                //mMediaController.show();
                //isPerpare=true;
            }

            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
            }
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                L.e(TAG,"onCompletion--------");
                if (videoListner != null){
                    videoListner.state(VideoListner.END,null);
                }

            }
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                L.e(TAG,"onError--------");
                if (videoListner != null){
                    videoListner.state(VideoListner.ERROR,null);
                }
                return false;
            }
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                return false;
            }
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                L.e(TAG,"onPrepared==");
                if (videoListner != null){
                    videoListner.state(VideoListner.PREPARED,null);
                }

            }


            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {

            }
            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

            }
        });
    }

    @Override
    public void stop() {
        videoPlayer.stop();
    }

    @Override
    public void setLoop(boolean loop) {
        videoPlayer.setLoopEnable(loop);
    }

    @Override
    public void setFullScreen(boolean fullScreen) {
        videoPlayer.setFullScreenVideo(fullScreen);
    }


    @Override
    public void start() {
        videoPlayer.start();
    }
    @Override
    public void pause() {
        videoPlayer.pause();
    }

    @Override
    public int getDuration() {
        return (int) videoPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return (int) videoPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        videoPlayer.seekTo(pos);
    }
    @Override
    public boolean isPlaying() {
        return videoPlayer.isPlaying();
    }
    @Override
    public int getBufferPercentage() {
        return videoPlayer.getBufferRange();
    }
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void resume() {
        videoPlayer.start();
    }

    @Override
    public void play(String url, String conver) {
        if(!TextUtils.isEmpty(conver)){
            ImgLoader.display(mContext,conver,mImgCover);
        }
        play(url);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_ijk_play;
    }

    @Override
    public void release() {
        videoPlayer.release();
    }

    @Override
    public void onClick(View v) {
       /*if(isPerpare&&mMediaController!=null){
            mMediaController.show();
        }*/
    }
}
