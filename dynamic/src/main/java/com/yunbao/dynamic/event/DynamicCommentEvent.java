package com.yunbao.dynamic.event;

public class DynamicCommentEvent {
    private String id;

    public DynamicCommentEvent(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
}
