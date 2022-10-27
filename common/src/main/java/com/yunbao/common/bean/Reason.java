package com.yunbao.common.bean;

import com.yunbao.common.adapter.radio.IRadioChecker;

public class Reason implements IRadioChecker {

    private String name;
    private String id;

    @Override
    public String getContent() {
        return name;
    }
    @Override
    public String getId() {
        return id;
    }




    public Reason(String content, String id) {
        this.name = content;
        this.id = id;
    }
    public void setContent(String content) {
        this.name = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }
}
