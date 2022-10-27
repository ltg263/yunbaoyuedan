package com.yunbao.main.bean;

import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.bean.UserBean;

public class GreateManBean {
    private UserBean userinfo;
    private SkillBean authinfo;
    private String id;
    private String dripid;
    private String liveuid;

    private boolean canPlay;
    private int playStatus;
    public static final int PLAY_STATUS_STOP = 0;
    public static final int PLAY_STATUS_PAUSE = 1;
    public static final int PLAY_STATUS_STARTED = 2;

    public UserBean getUserinfo() {
        return userinfo;
    }


    public void setUserinfo(UserBean userinfo) {
        this.userinfo = userinfo;
    }
    public SkillBean getAuthinfo() {
        return authinfo;
    }
    public void setAuthinfo(SkillBean authinfo) {
        this.authinfo = authinfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getDripid() {
        return dripid;
    }

    public void setDripid(String dripid) {
        this.dripid = dripid;
    }

    public String getLiveuid() {
        return liveuid;
    }

    public void setLiveuid(String liveuid) {
        this.liveuid = liveuid;
    }

    public boolean isCanPlay() {
        return canPlay;
    }

    public void setCanPlay(boolean canPlay) {
        this.canPlay = canPlay;
    }

    public int getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(int playStatus) {
        this.playStatus = playStatus;
    }
}
