package com.yunbao.chatroom.bean;

import com.yunbao.common.bean.commit.CommitEntity;
import com.yunbao.common.bean.commit.ObservableString;

public class OpenLiveCommitBean extends CommitEntity {
    private  ObservableString title;
    private  ObservableString  notice;
    private  String  conver;
    private  String  roomConver;
    private  String  roomConverId;
    private  int  liveType;

    public OpenLiveCommitBean(){
        title=new ObservableString(this);
        notice=new ObservableString(this);
    }

    public ObservableString getTitle() {
        if(title==null){
            title=new ObservableString(this);
        }
       return title;
    }

    public ObservableString getNotice() {
        if(notice==null){
           notice=new ObservableString(this);
        }
        return notice;
    }

    public String getConver() {
        return conver;
    }

    public void setConver(String conver) {
        this.conver = conver;
        observer();
    }

    @Override
    public boolean observerCondition() {
        return fieldNotEmpty(title.toString())&&
                fieldNotEmpty(notice.toString())&&
                fieldNotEmpty(roomConverId)
                &&liveType!=0;
    }

    public String getRoomConver() {
        return roomConver;
    }

    public void setRoomConver(String roomConver) {
        this.roomConver = roomConver;
        observer();
    }

    public int getLiveType() {
        return liveType;
    }

    public void setLiveType(int liveType) {
        this.liveType = liveType;
        observer();
    }

    public String getRoomConverId() {
        return roomConverId;
    }

    public void setRoomConverId(String roomConverId) {
        this.roomConverId = roomConverId;
    }

    @Override
    public void release() {
        super.release();
        if(title!=null){
            title.release();
        }
        if(notice!=null){
            notice.release();
        }
    }
}
