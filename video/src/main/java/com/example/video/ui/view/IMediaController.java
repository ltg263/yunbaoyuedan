package com.example.video.ui.view;

import android.widget.MediaController;
/*媒体控制器*/
public interface IMediaController extends MediaController.MediaPlayerControl {
    public  void resume();
    public  void play(String url,String conver);
    public  void stop();
    public void setLoop(boolean loop);
    public void setFullScreen(boolean fullScreen);
}
