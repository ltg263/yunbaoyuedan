package com.yunbao.common.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2018/10/9.
 */

public class LevelBean {
    private int level;
    private String thumb;

    @JSONField(name = "levelid")
    public int getLevel() {
        return level;
    }

    @JSONField(name = "levelid")
    public void setLevel(int level) {
        this.level = level;
    }

    @JSONField(name = "thumb")
    public String getThumb() {
        return thumb;
    }

    @JSONField(name = "thumb")
    public void setThumb(String thumb) {
        this.thumb = thumb;
    }


    public String getThumbIcon(){
        return thumb;
    }

    public String getColor(){
        return "#ffffff";
    }

}
