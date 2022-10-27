package com.example.video.ui.activity;

import android.view.ViewGroup;
import com.example.video.R;
import com.example.video.ui.view.AbsPlayViewHolder;
import com.example.video.ui.view.IjkViewPlayer;
import com.yunbao.common.activity.AbsActivity;


public abstract class VideoPlayActivity extends AbsActivity  {
    protected AbsPlayViewHolder playViewHolder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_play;
    }

    @Override
    protected void main() {
        super.main();
        playViewHolder=new IjkViewPlayer(this,getVideoContainerView());
        playViewHolder.addToParent();
        playViewHolder.subscribeActivityLifeCycle();
    }

    public abstract ViewGroup getVideoContainerView();
    protected void startPlay(final String url,String cover){
        if(playViewHolder!=null){
            playViewHolder.play(url,cover);
        }
    }

    @Override
        protected void onDestroy() {
            super.onDestroy();
            if(playViewHolder!=null){
                playViewHolder.unSubscribeActivityLifeCycle();
            }
      }
}