package com.yunbao.common.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

/**
 * Created by cxf on 2018/7/19.
 * 播放语音消息的工具类
 */

public class VoiceMediaPlayerUtil {

    private MediaPlayer mPlayer;
    private boolean mStarted;
    private boolean mPaused;
    private boolean mDestroy;
    private String mCurPath;
    private ActionListener mActionListener;
    private static final String TAG = "VoiceMediaPlayerUtil";

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mDestroy) {
                destroy();
            } else {
                mPlayer.start();
                mStarted = true;
                L.e(TAG,"mOnPreparedListener--->start");
                if (mActionListener != null) {
                    mActionListener.onPrepared();
                }
            }
        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mStarted = false;
            mCurPath = null;
            L.e(TAG,"mOnPreparedListener--->onPlayEnd");
            if (mActionListener != null) {
                mActionListener.onPlayEnd();
            }
        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mStarted = false;
            mCurPath = null;
            L.e(TAG,"mOnPreparedListener--->onError");
            if (mActionListener != null) {
                mActionListener.onError();
            }
            return false;
        }
    };

    public VoiceMediaPlayerUtil() {
        init();
    }
    public VoiceMediaPlayerUtil(Context context) {
        init();

    }

    private void init(){
        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(mOnPreparedListener);
        mPlayer.setOnErrorListener(mOnErrorListener);
        mPlayer.setOnCompletionListener(mOnCompletionListener);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }
    public void startPlay(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        if (!mStarted) {
            mCurPath = path;
            try {
                mPlayer.reset();
                mPlayer.setDataSource(path);
                mPlayer.setLooping(false);
                mPlayer.setVolume(1f, 1f);
                mPlayer.prepare();
//                mPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (!path.equals(mCurPath)) {
                mCurPath = path;
                mStarted = false;
                try {
                    mPlayer.stop();
                    mPlayer.reset();
                    mPlayer.setDataSource(path);
                    mPlayer.setLooping(false);
                    mPlayer.setVolume(1f, 1f);
                    mPlayer.prepare();
//                    mPlayer.prepareAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        L.e("mPlayUrl--->"+path);
    }

    public int getCurPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition() / 1000;
        }
        return 0;
    }

    public int getDuration(){
        if (mPlayer != null){
            return  mPlayer.getDuration() / 1000;
        }
        return 0;
    }

    //毫秒时间
    public int getMSCurPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }
    //毫秒时间
    public int getMSDuration(){
        if (mPlayer != null){
            return  mPlayer.getDuration();
        }
        return 0;
    }

    public void pausePlay() {
        if (mPlayer != null && mStarted && !mDestroy) {
            mPlayer.pause();
            mPaused = true;
        }
    }

    public void resumePlay() {
        if (mPlayer != null && mStarted && !mDestroy && mPaused) {
            mPaused = false;
            mPlayer.start();
        }
    }

    public boolean isPaused(){
        return mPlayer != null && mStarted && !mDestroy && mPaused;
    }


    public void stopPlay() {
        if (mPlayer != null && mStarted) {
            mPlayer.stop();
        }
        mStarted = false;
        mCurPath = null;
    }

    public void destroy() {
        stopPlay();
        if (mPlayer != null) {
            mPlayer.release();
        }
        mActionListener = null;
        mDestroy = true;
    }

    public interface ActionListener {

        void onPrepared();

        void onError();

        void onPlayEnd();
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public boolean isStarted() {
        return mStarted;
    }
}
