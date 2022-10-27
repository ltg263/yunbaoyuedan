package com.yunbao.common.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by cxf on 2018/9/28.
 * 我的 页面的item
 */

public class UserItemBean2 {

    private String mTitle;
    private List<UserItemBean> mList;

    @JSONField(name = "title")
    public String getTitle() {
        return mTitle;
    }
    @JSONField(name = "title")
    public void setTitle(String title) {
        mTitle = title;
    }
    @JSONField(name = "list")
    public List<UserItemBean> getList() {
        return mList;
    }
    @JSONField(name = "list")
    public void setList(List<UserItemBean> list) {
        mList = list;
    }
}
