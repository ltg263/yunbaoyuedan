package com.yunbao.common.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.yunbao.common.utils.StringUtil;

import java.util.List;

/**
 * Created by cxf on 2017/8/9.
 */

public class LiveBean implements Parcelable {
    private String uid;
    private String avatar;
    @JSONField(name = "avatar_thumb")
    @SerializedName( "avatar_thumb")
    private String avatarThumb;
    @JSONField(name = "user_nickname")
    @SerializedName( "user_nickname")
    private String userNiceName;
    private String title;
    private String city;
    private String stream;
    private String pull;
    private String thumb;
    @JSONField(name = "bg")
    @SerializedName( "bg")
    private String roomCover;

    private int nums;
    private int sex;
    private String distance;
    private int type;
    @SerializedName("type_v")
    private String typeName;
    private String typeVal;
    private String chatserver;
    private String showid;
    private String des;
    private String votestotal;
    private int isattent;
    private List<UserBean>sits;
    private List<LiveAnthorBean>wheatList; //从sits转换出数据

    private boolean isAudienceCanNotSpeak;
    private int isdispatch;
    private String skillid;
    private String skillname;
    private String thumb_p;
    private int isCollect;
   /*拓展参数*/
    private String expandParm;

    private String isvideo;

    @JSONField(name = "agentcode")
    @SerializedName( "agentcode")
    private String inviteCode;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getUid() {
        return uid;
    }
    public int getRoomId() {
        try {
            return Integer.parseInt(uid);
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getAvatarThumb() {
        return avatarThumb;
    }

    public void setAvatarThumb(String avatarThumb) {
        this.avatarThumb = avatarThumb;
    }

    public String getUserNiceName() {
        return userNiceName;
    }

    public void setUserNiceName(String userNiceName) {
        this.userNiceName = userNiceName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getPull() {
        return pull;
    }

    public void setPull(String pull) {
        this.pull = pull;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getNums() {

        return nums;
    }

    public void setNums(int nums) {
        this.nums = nums;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @JSONField(name = "type_val")
    public String getTypeVal() {
        return typeVal;
    }

    @JSONField(name = "type_val")
    public void setTypeVal(String typeVal) {
        this.typeVal = typeVal;
    }


    public int getSex() {
        return sex;
    }
    public void setSex(int sex) {
        this.sex = sex;
    }

    public LiveBean() {

    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getVotestotal() {
        if(TextUtils.isEmpty(votestotal)){
            return "0";
        }
        return votestotal;
    }

    public void setVotestotal(String votestotal) {
        this.votestotal = votestotal;
    }

    private LiveBean(Parcel in) {
        this.uid = in.readString();
        this.avatar = in.readString();
        this.avatarThumb = in.readString();
        this.userNiceName = in.readString();
        this.sex = in.readInt();
        this.title = in.readString();
        this.city = in.readString();
        this.stream = in.readString();
        this.pull = in.readString();
        this.thumb = in.readString();
        this.nums = in.readInt();
        this.distance = in.readString();
        this.type = in.readInt();
        this.typeVal = in.readString();
        this.chatserver = in.readString();
        this.showid = in.readString();
        this.des = in.readString();
        this.votestotal = in.readString();
        this.isattent = in.readInt();
        this.isdispatch = in.readInt();
        this.skillid = in.readString();
        this.sits=in.readArrayList(UserBean.class.getClassLoader());
        this.roomCover=in.readString();
        this.isCollect=in.readInt();
        this.typeName=in.readString();
        this.expandParm=  in.readString();
        this.inviteCode=  in.readString();
        this.isvideo=  in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.avatar);
        dest.writeString(this.avatarThumb);
        dest.writeString(this.userNiceName);
        dest.writeInt(this.sex);
        dest.writeString(this.title);
        dest.writeString(this.city);
        dest.writeString(this.stream);
        dest.writeString(this.pull);
        dest.writeString(this.thumb);
        dest.writeInt(this.nums);
        dest.writeString(this.distance);
        dest.writeInt(this.type);
        dest.writeString(this.typeVal);
        dest.writeString(this.chatserver);
        dest.writeString(this.showid);
        dest.writeString(this.des);
        dest.writeString(this.votestotal);
        dest.writeInt(this.isattent);
        dest.writeInt(this.isdispatch);
        dest.writeString(this.skillid);
        dest.writeList(this.sits);
        dest.writeString(this.roomCover);
        dest.writeInt(this.isCollect);
        dest.writeString(this.typeName);
        dest.writeString(expandParm);
        dest.writeString(inviteCode);
        dest.writeString(isvideo);

    }

    public String getShowid() {
        return showid;
    }

    public void setShowid(String showid) {
        this.showid = showid;
    }

    public String getChatserver() {
        return chatserver;
    }

    public void setChatserver(String chatserver) {
        this.chatserver = chatserver;
    }


    public List<UserBean> getSits() {
        return sits;
    }

    public void setSits(List<UserBean> sits) {
        this.sits = sits;
    }







    public boolean isAudienceCanNotSpeak() {
        return isAudienceCanNotSpeak;
    }

    public void setAudienceCanNotSpeak(boolean audienceCanNotSpeak) {
        isAudienceCanNotSpeak = audienceCanNotSpeak;
    }

    public String getIdShow(){
        if(uid==null){
            return "";
        }
        return StringUtil.contact("ID:",uid);
    }

    public LiveInfo parseLiveInfo(){
       LiveInfo liveInfo=new LiveInfo();
       liveInfo.setSteam(stream);
       liveInfo.setRoomId(getRoomId());
       liveInfo.setLiveUid(uid);
       return liveInfo;
    }


    public int getIsdispatch() {
        return isdispatch;
    }

    public void setIsdispatch(int isdispatch) {
        this.isdispatch = isdispatch;
    }

    public String getSkillid() {
        return skillid;
    }

    public void setSkillid(String skillid) {
        this.skillid = skillid;
    }

    public int getIsattent() {
        return isattent;
    }

    public void setIsattent(int isattent) {
        this.isattent = isattent;
    }


    public String getSkillname() {
        return skillname;
    }

    public void setSkillname(String skillname) {
        this.skillname = skillname;
    }

    public String getRoomCover() {
        return roomCover;
    }

    public void setRoomCover(String roomCover) {
        this.roomCover = roomCover;
    }

    public static final Creator<LiveBean> CREATOR = new Creator<LiveBean>() {
        @Override
        public LiveBean[] newArray(int size) {
            return new LiveBean[size];
        }

        @Override
        public LiveBean createFromParcel(Parcel in) {
            return new LiveBean(in);
        }
    };

    private UserBean liveUserBean;

    public UserBean getLiveUserBean(){
        if(liveUserBean==null){
            UserBean userBean=new UserBean();
            userBean.setId(uid);
            userBean.setAvatar(avatar);
            userBean.setSex(sex);
            userBean.setUserNiceName(userNiceName);
            liveUserBean=userBean;
        }
        return liveUserBean;
    }

    @JSONField(name = "type_v")
    public String getTypeName() {
        return typeName;
    }

    @JSONField(name = "type_v")
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }

    public String getThumb_p() {
        return thumb_p;
    }

    public void setThumb_p(String thumb_p) {
        this.thumb_p = thumb_p;
    }

    public String getExpandParm() {
        return expandParm;
    }

    public void setExpandParm(String expandParm) {
        this.expandParm = expandParm;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    @Override
    public String toString() {
        return "uid: " + uid + " , userNiceName: " + userNiceName + " ,playUrl: " + pull;
    }

    public String getIsvideo() {
        return isvideo;
    }

    public void setIsvideo(String isvideo) {
        this.isvideo = isvideo;
    }
}
