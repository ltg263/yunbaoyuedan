package com.yunbao.dynamic.event;

public class DynamicCommentNumberEvent {
    private String id;
    private int num;

    public DynamicCommentNumberEvent(String id, int num) {
        this.id = id;
        this.num = num;
    }
    public String getId() {
        return id;
    }
    public int getNum() {
        return num;
    }

}
