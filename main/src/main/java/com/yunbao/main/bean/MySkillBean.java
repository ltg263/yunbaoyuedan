package com.yunbao.main.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.yunbao.common.adapter.radio.IRadioChecker;
import com.yunbao.common.bean.SkillBean;
import java.util.List;

public class MySkillBean implements Parcelable , IRadioChecker {
    public static final String EMPTY_ID="-1";
    /**
     * id : 8
     * uid : 100187
     * sex : 1
     * skillid : 6
     * thumb : http://ybpeiwan.yunbaozb.com/default/20191115/6e5a99aff502051dc49d2328993dac79.jpg
     * levelid : 1
     * switch : 1
     * coinid : 1
     * coin : 10
     * voice :
     * voice_l : 0
     * des :
     * orders : 0
     * stars : 5.0
     * star_level : 5
     * label_a : ["666"]
     * method : 半小时
     * skillname : 第五人格
     * level : 第一
     * skill : {"id":"6","classid":"1","name":"第五人格","thumb":"http://ybpeiwan.yunbaozb.com/skill_6.png","method":"半小时"}
     */

    private String id;
    private String uid;
    private String sex;
    private String skillid;
    private String thumb;
    private String levelid;
    @SerializedName("switch")
    private int switchX;
    private String coinid;
    private String coin;
    private String voice;
    private String voice_l;
    private String des;
    private String orders;
    private String stars;
    private String star_level;
    private String method;
    private String skillname;
    private String level;
    private SkillBean skill;
    private List<String> label_a;

    public MySkillBean(){

    }


    protected MySkillBean(Parcel in) {
        id = in.readString();
        uid = in.readString();
        sex = in.readString();
        skillid = in.readString();
        thumb = in.readString();
        levelid = in.readString();
        switchX = in.readInt();
        coinid = in.readString();
        coin = in.readString();
        voice = in.readString();
        voice_l = in.readString();
        des = in.readString();
        orders = in.readString();
        stars = in.readString();
        star_level = in.readString();
        method = in.readString();
        skillname = in.readString();
        level = in.readString();
        skill = in.readParcelable(SkillBean.class.getClassLoader());
        label_a = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(uid);
        dest.writeString(sex);
        dest.writeString(skillid);
        dest.writeString(thumb);
        dest.writeString(levelid);
        dest.writeInt(switchX);
        dest.writeString(coinid);
        dest.writeString(coin);
        dest.writeString(voice);
        dest.writeString(voice_l);
        dest.writeString(des);
        dest.writeString(orders);
        dest.writeString(stars);
        dest.writeString(star_level);
        dest.writeString(method);
        dest.writeString(skillname);
        dest.writeString(level);
        dest.writeParcelable(skill, flags);
        dest.writeStringList(label_a);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MySkillBean> CREATOR = new Creator<MySkillBean>() {
        @Override
        public MySkillBean createFromParcel(Parcel in) {
            return new MySkillBean(in);
        }

        @Override
        public MySkillBean[] newArray(int size) {
            return new MySkillBean[size];
        }
    };

    @Override
    public String getContent() {
        return skillname;
    }

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSkillid() {
        return skillid;
    }

    public void setSkillid(String skillid) {
        this.skillid = skillid;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getLevelid() {
        return levelid;
    }

    public void setLevelid(String levelid) {
        this.levelid = levelid;
    }

    public int getSwitchX() {
        return switchX;
    }

    public void setSwitchX(int switchX) {
        this.switchX = switchX;
    }

    public String getCoinid() {
        return coinid;
    }

    public void setCoinid(String coinid) {
        this.coinid = coinid;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getVoice_l() {
        return voice_l;
    }

    public void setVoice_l(String voice_l) {
        this.voice_l = voice_l;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getStar_level() {
        return star_level;
    }

    public void setStar_level(String star_level) {
        this.star_level = star_level;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getSkillname() {
        return skillname;
    }

    public void setSkillname(String skillname) {
        this.skillname = skillname;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public SkillBean getSkill() {
        if(skill==null){
           skill=new SkillBean();
           skill.setSkillName(skillname);
           skill.setSkillPrice(coin);
           skill.setId(skillid);
           skill.setUnit(method);
        }
        return skill;
    }

    public void setSkill(SkillBean skill) {
        this.skill = skill;
    }

    public List<String> getLabel_a() {
        return label_a;
    }

    public void setLabel_a(List<String> label_a) {
        this.label_a = label_a;
    }
}
