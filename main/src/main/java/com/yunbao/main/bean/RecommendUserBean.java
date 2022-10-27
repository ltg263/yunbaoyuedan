package com.yunbao.main.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.yunbao.common.bean.UserBean;

/**
 * Created by cxf on 2019/7/29.
 */

public class RecommendUserBean extends UserBean {

    private TagBean[] mGameTags;

    @JSONField(name = "list")
    public TagBean[] getGameTags() {
        return mGameTags;
    }

    @JSONField(name = "list")
    public void setGameTags(TagBean[] gameTags) {
        mGameTags = gameTags;
    }
}
