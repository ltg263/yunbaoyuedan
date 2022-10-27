package com.yunbao.main.bean;

import java.util.List;

/**
 * Created by Sky.L on 2020-12-21
 */
public class CountryCodeParentBean {
    private String title;
    private List<CountryCodeBean> lists;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CountryCodeBean> getLists() {
        return lists;
    }

    public void setLists(List<CountryCodeBean> lists) {
        this.lists = lists;
    }
}
