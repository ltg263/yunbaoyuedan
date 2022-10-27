package com.yunbao.dynamic.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.bean.UserBean;

import java.util.List;

/**
 * Created by Sky.L on 2020-09-24
 */
public class DynamicUserBean implements Parcelable {

//     "id": "100349",
//             "user_nickname": "财务部经理",
//             "avatar": "http://thirdwx.qlogo.cn/mmopen/vi_32/BagUJIjSicHGIm5HhrYk6OSNufLeJdOSJk5eQgs6lZz50OyJ6lDGKVWjgVuNuDwicxunjEDXRYJc7JzATQWrovYg/132?imageView2/2/w/600/h/600",
//             "avatar_thumb": "http://thirdwx.qlogo.cn/mmopen/vi_32/BagUJIjSicHGIm5HhrYk6OSNufLeJdOSJk5eQgs6lZz50OyJ6lDGKVWjgVuNuDwicxunjEDXRYJc7JzATQWrovYg/132?imageView2/2/w/200/h/200",
//             "sex": "1",
//             "signature": "",
//             "profession": "",
//             "school": "",
//             "hobby": "",
//             "voice": "",
//             "voice_l": "0",
//             "addr": "",
//             "user_status": "1",
//             "orders": "0",
//             "age": "20",
//             "constellation": "摩羯座",
//             "star": "5.0",
//             "isattent": "1",
//             "skill_list": [


    private String id;
    private String user_nickname;
    private String avatar;
    private String avatar_thumb;
    private String sex;
    private String signature;
    private String profession;
    private String school;
    private String hobby;
    private String voice;
    private String voice_l;
    private String addr;
    private String user_status;
    private String orders;
    private String age;
    private String constellation;
    private String star;
    private String isattent;
    private String skill_des;
    private List<SkillBean> skill_list;
    private SkillBean skillinfo;
    private UserLabelInfoBean labelinfo;
    private int level;
    private int level_anchor;
    private int isshow_anchorlev;//是否展示陪玩（原主播）等级：0：否；1：是


    public DynamicUserBean() {
    }

    protected DynamicUserBean(Parcel in) {
        id = in.readString();
        user_nickname = in.readString();
        avatar = in.readString();
        avatar_thumb = in.readString();
        sex = in.readString();
        signature = in.readString();
        profession = in.readString();
        school = in.readString();
        hobby = in.readString();
        voice = in.readString();
        voice_l = in.readString();
        addr = in.readString();
        user_status = in.readString();
        orders = in.readString();
        age = in.readString();
        constellation = in.readString();
        star = in.readString();
        isattent = in.readString();
        skill_des = in.readString();
        level = in.readInt();
        level_anchor = in.readInt();
        isshow_anchorlev = in.readInt();
        skill_list = in.readArrayList(SkillBean.class.getClassLoader());
        labelinfo = in.readParcelable(UserLabelInfoBean.class.getClassLoader());
        skillinfo = in.readParcelable(SkillBean.class.getClassLoader());

    }

    public static final Creator<DynamicUserBean> CREATOR = new Creator<DynamicUserBean>() {
        @Override
        public DynamicUserBean createFromParcel(Parcel in) {
            return new DynamicUserBean(in);
        }

        @Override
        public DynamicUserBean[] newArray(int size) {
            return new DynamicUserBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user_nickname);
        dest.writeString(avatar);
        dest.writeString(avatar_thumb);
        dest.writeString(sex);
        dest.writeString(signature);
        dest.writeString(profession);
        dest.writeString(school);
        dest.writeString(hobby);
        dest.writeString(voice);
        dest.writeString(voice_l);
        dest.writeString(addr);
        dest.writeString(user_status);
        dest.writeString(orders);
        dest.writeString(age);
        dest.writeString(constellation);
        dest.writeString(star);
        dest.writeString(isattent);
        dest.writeString(skill_des);
        dest.writeInt(level);
        dest.writeInt(level_anchor);
        dest.writeInt(isshow_anchorlev);
        dest.writeParcelable((Parcelable) skill_list, flags);
        dest.writeParcelable((Parcelable) labelinfo, flags);
        dest.writeParcelable((Parcelable) skillinfo, flags);


    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAvatar_thumb() {
        return avatar_thumb;
    }

    public void setAvatar_thumb(String avatar_thumb) {
        this.avatar_thumb = avatar_thumb;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
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

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getIsattent() {
        return isattent;
    }

    public void setIsattent(String isattent) {
        this.isattent = isattent;
    }

    public List<SkillBean> getSkill_list() {
        return skill_list;
    }

    public void setSkill_list(List<SkillBean> skill_list) {
        this.skill_list = skill_list;
    }

    public UserLabelInfoBean getLabelinfo() {
        return labelinfo;
    }

    public void setLabelinfo(UserLabelInfoBean labelinfo) {
        this.labelinfo = labelinfo;
    }

    public String getSkill_des() {
        return skill_des;
    }

    public void setSkill_des(String skill_des) {
        this.skill_des = skill_des;
    }

    public SkillBean getSkillinfo() {
        return skillinfo;
    }

    public void setSkillinfo(SkillBean skillinfo) {
        this.skillinfo = skillinfo;
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
