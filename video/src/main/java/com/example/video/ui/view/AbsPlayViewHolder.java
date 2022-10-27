package com.example.video.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import com.yunbao.common.views.AbsViewHolder2;

public abstract class AbsPlayViewHolder extends AbsViewHolder2 implements IMediaController {
    protected VideoListner videoListner;
    public AbsPlayViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {
    }

    public void setVideoListner(VideoListner videoListner) {
        this.videoListner = videoListner;
    }

    public interface VideoListner{
        public static final int PREPARED=1;
        public static final int PLAYING=2;
        public static final int BUFFER=3;
        public static final int END=4;
        public static final int ERROR=5;
        public void state(int state, Bundle bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
       resume();
    }
    @Override
    public void onPause() {
        pause();
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }
}