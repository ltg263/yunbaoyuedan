package com.yunbao.dynamic.event;

import com.yunbao.common.utils.StringUtil;

public class DynamicLikeEvent {
    private String tag;
    private int isLike;
    private int likesNum;
    private String dynamicId;


    public DynamicLikeEvent(int isLike, int likesNum, String dynamicId) {
        this.isLike = isLike;
        this.likesNum = likesNum;
        this.dynamicId = dynamicId;
    }

    public int getIsLike() {
        return isLike;
    }
    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }
    public int getLikesNum() {
        return likesNum;
    }
    public void setLikesNum(int likesNum) {
        this.likesNum = likesNum;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean compare(String tag){
        return StringUtil.equals(this.tag,tag);
    }

    public String getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(String dynamicId) {
        this.dynamicId = dynamicId;
    }
}
