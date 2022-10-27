package com.yunbao.chatroom.bean;

import com.google.gson.annotations.SerializedName;
import com.yunbao.common.bean.LiveBean;
import java.util.List;

public class LiveSetInfo extends LiveBean {
    @SerializedName("bglist")
    private List<LiveBgBean>coverList;
    @SerializedName("typelist")
    private List<LiveTypeBean>liveTypeBeanList;
    private String bgid;
    public List<LiveBgBean> getCoverList() {
        return coverList;
    }
    public void setCoverList(List<LiveBgBean> coverList) {
        this.coverList = coverList;
    }

    public List<LiveTypeBean> getLiveTypeBeanList() {
        return liveTypeBeanList;
    }
    public void setLiveTypeBeanList(List<LiveTypeBean> liveTypeBeanList) {
        this.liveTypeBeanList = liveTypeBeanList;
    }

    public String getBgid() {
        return bgid;
    }
    public void setBgid(String bgid) {
        this.bgid = bgid;
    }
    public static class LiveBgBean{
        private String id;
        private String thumb;

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getThumb() {
            return thumb;
        }
        public void setThumb(String thumb) {
            this.thumb = thumb;
        }
    }
}
