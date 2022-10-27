package com.yunbao.dynamic.bean;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.StringUtil;

import java.util.List;

public class DynamicBean implements Parcelable {


    /**
     * id : 14
     * uid : 100183
     * content : 图片➕文字➕位置➕技能
     * thumbs : ["http://ybpeiwan.yunbaozb.com/IOS_20191105102254_action_image4_cover.png","http://ybpeiwan.yunbaozb.com/IOS_20191105102254_action_image5_cover.png","http://ybpeiwan.yunbaozb.com/IOS_20191105102254_action_image1_cover.png","http://ybpeiwan.yunbaozb.com/IOS_20191105102254_action_image2_cover.png","http://ybpeiwan.yunbaozb.com/IOS_20191105102254_action_image3_cover.png","http://ybpeiwan.yunbaozb.com/IOS_20191105102254_action_image0_cover.png","http://ybpeiwan.yunbaozb.com/IOS_20191105102254_action_image6_cover.png"]
     * video_t :
     * video :
     * voice :
     * voice_l : 0
     * skillid : 3
     * location : 名阁造型烫染会所(长城小区店)
     * type : 1
     * likes : 0
     * comments : 0
     * orders : 0
     * skillinfo : {"method":"局","name":"英雄联盟","thumb":"http://ybpeiwan.yunbaozb.com/skill_1.png","coin":"10"}
     * user_nickname : 一定有时间
     * avatar : http://ybpeiwan.yunbaozb.com/image_IOS_20191104085048.png?imageView2/2/w/600/h/600
     * age : 19
     * sex : 1
     * addr :
     * islike : 0
     * isattent : 0
     */

    protected String id;
    protected String uid;
    protected String content;
    protected String video_t;
    protected String video;
    protected String voice;
    protected int voice_l;
    protected String skillid;
    protected String location;
    protected int type;
    protected int likes;
    protected int comments;
    protected String orders;
    protected SkillBean skillinfo;
    protected String user_nickname;
    protected String avatar;
    protected int age;
    protected int sex;
    @SerializedName("city")
    protected String addr;
    protected int islike;
    protected int isattent;
    protected List<String> thumbs;
    private String datatime;
    private int isblack; //是否拉黑


    private String star;
    private UserBean userinfo;
    private List<SkillBean> skill_list;
    private int level;
    private int level_anchor;
    private int isshow_anchorlev;//是否展示陪玩（原主播）等级：0：否；1：是


    protected DynamicBean(Parcel in) {
        id = in.readString();
        uid = in.readString();
        content = in.readString();
        video_t = in.readString();
        video = in.readString();
        voice = in.readString();
        voice_l = in.readInt();
        skillid = in.readString();
        location = in.readString();
        type = in.readInt();
        likes = in.readInt();
        comments = in.readInt();
        orders = in.readString();
        skillinfo = in.readParcelable(SkillBean.class.getClassLoader());
        userinfo = in.readParcelable(UserBean.class.getClassLoader());
        skill_list = in.readArrayList(SkillBean.class.getClassLoader());
        user_nickname = in.readString();
        avatar = in.readString();
        age = in.readInt();
        sex = in.readInt();
        addr = in.readString();
        star = in.readString();
        islike = in.readInt();
        isattent = in.readInt();
        thumbs = in.createStringArrayList();
        datatime=in.readString();
        isblack = in.readInt();
        level = in.readInt();
        level_anchor = in.readInt();
        isshow_anchorlev = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(uid);
        dest.writeString(content);
        dest.writeString(video_t);
        dest.writeString(video);
        dest.writeString(voice);
        dest.writeInt(voice_l);
        dest.writeString(skillid);
        dest.writeString(location);
        dest.writeInt(type);
        dest.writeInt(likes);
        dest.writeInt(comments);
        dest.writeString(orders);
        dest.writeParcelable(skillinfo, flags);
        dest.writeParcelable(userinfo, flags);
        dest.writeParcelable((Parcelable) skill_list, flags);
        dest.writeString(user_nickname);
        dest.writeString(avatar);
        dest.writeInt(age);
        dest.writeInt(sex);
        dest.writeString(addr);
        dest.writeString(star);
        dest.writeInt(islike);
        dest.writeInt(isattent);
        dest.writeStringList(thumbs);
        dest.writeString(datatime);
        dest.writeInt(isblack);
        dest.writeInt(level);
        dest.writeInt(level_anchor);
        dest.writeInt(isshow_anchorlev);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DynamicBean> CREATOR = new Creator<DynamicBean>() {
        @Override
        public DynamicBean createFromParcel(Parcel in) {
            return new DynamicBean(in);
        }

        @Override
        public DynamicBean[] newArray(int size) {
            return new DynamicBean[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVideo_t() {
        return video_t;
    }

    public void setVideo_t(String video_t) {
        this.video_t = video_t;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public int getVoice_l() {
        return voice_l;
    }

    public void setVoice_l(int voice_l) {
        this.voice_l = voice_l;
    }

    public String getSkillid() {
        return skillid;
    }

    public void setSkillid(String skillid) {
        this.skillid = skillid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    public SkillBean getSkillinfo() {
        return skillinfo;
    }

    public void setSkillinfo(SkillBean skillinfo) {
        this.skillinfo = skillinfo;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getIslike() {
        return islike;
    }

    public void setIslike(int islike) {
        this.islike = islike;
    }

    public int getIsattent() {
        return isattent;
    }

    public void setIsattent(int isattent) {
        this.isattent = isattent;
    }

    public List<String> getThumbs() {
        return thumbs;
    }

    public void setThumbs(List<String> thumbs) {
        this.thumbs = thumbs;
    }

    public String getDatatime() {
        return datatime;
    }
    public void setDatatime(String datatime) {
        this.datatime = datatime;
    }

    public int getIsblack() {
        return isblack;
    }

    public void setIsblack(int isblack) {
        this.isblack = isblack;
    }

    public String getAddrAndTime(){
        if(TextUtils.isEmpty(addr)){
            return getDatatime();
        }
        return StringUtil.contact(addr," | ",getDatatime());
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public UserBean getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserBean userinfo) {
        this.userinfo = userinfo;
    }

    public List<SkillBean> getSkill_list() {
        return skill_list;
    }

    public void setSkill_list(List<SkillBean> skill_list) {
        this.skill_list = skill_list;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel_anchor() {
        return level_anchor;
    }

    public void setLevel_anchor(int level_anchor) {
        this.level_anchor = level_anchor;
    }

    public int getIsshow_anchorlev() {
        return isshow_anchorlev;
    }

    public boolean showAnchorLevel() {
        return isshow_anchorlev == 1;
    }

    public void setIsshow_anchorlev(int isshow_anchorlev) {
        this.isshow_anchorlev = isshow_anchorlev;
    }
}
