package com.yunbao.chatroom.event;

public class AudioChangeEvent {
    private String uid;
    private boolean isOpen;

    public AudioChangeEvent(String uid, boolean isOpen) {
        this.uid = uid;
        this.isOpen = isOpen;
    }
    public String getUid() {
        return uid;
    }
    public boolean isOpen() {
        return isOpen;
    }
}
