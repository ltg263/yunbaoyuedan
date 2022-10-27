package com.yunbao.common.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.yunbao.common.R;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;

/**
 * Created by cxf on 2017/8/14.
 */

public class UserBean implements Parcelable{

    @SerializedName("id")
    protected String mId;

    @SerializedName("user_nickname")
    protected String mUserNiceName;

    @SerializedName("avatar")
    protected String mAvatar;

    @SerializedName("avatar_thumb")
    protected String mAvatarThumb;

    @SerializedName("sex")
    protected int mSex;
    @SerializedName("level")
    protected int mLevel;
    @SerializedName("level_anchor")
    protected int mAnchorLevel;
    @SerializedName("isshow_anchorlev")
    protected int mShowAnchorLevel;

    @SerializedName("birthday")
    protected String mBirthday;

    @SerializedName("age")
    protected String mAge;

    @SerializedName("constellation")
    protected String mXingZuo;

    @SerializedName("signature")
    protected String mSignature;

    @SerializedName("addr")
    protected String mCity;

    @SerializedName("hobby")
    protected String mInteret;

    @SerializedName("school")
    protected String mSchool;

    @SerializedName("profession")
    protected String mProfession;

    @SerializedName("voice")
    protected String mVoice;

    @SerializedName("voice_l")
    protected int mVoiceDuration;

    @SerializedName("coin")
    protected String mCoin;

    @SerializedName("votes")
    protected String mVotes;

    @SerializedName("follows")
    protected long mFollowNum;

    @SerializedName("fans")
    protected long mFansNum;

    @SerializedName("star")
    protected String mStar;

    @SerializedName("auth_nums")
    protected int mAuthNum;

    private int isFollow;
    private int mVisitNums;
    private int mViewNums;
    private int mNewNums;
    private int isblack;

    protected String consumption;
    protected String votestotal;
    protected String province;
    protected String location;//所在地
    protected int lives;
    protected Vip vip;
    protected Liang liang;
    protected Car car;


    public UserBean(){

    }




    @JSONField(name = "id")
    public String getId() {
        if(mId==null){
           mId="";
        }
        return mId;
    }

    @JSONField(name = "id")
    public void setId(String id) {
        mId = id;
    }

    @JSONField(name = "user_nickname")
    public String getUserNiceName() {
        return mUserNiceName;
    }

    @JSONField(name = "user_nickname")
    public void setUserNiceName(String userNiceName) {
        mUserNiceName = userNiceName;
    }

    @JSONField(name = "avatar")
    public String getAvatar() {
        return mAvatar;
    }

    @JSONField(name = "avatar")
    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    @JSONField(name = "avatar_thumb")
    public String getAvatarThumb() {
        return mAvatarThumb;
    }

    @JSONField(name = "avatar_thumb")
    public void setAvatarThumb(String avatarThumb) {
        mAvatarThumb = avatarThumb;
    }

    @JSONField(name = "sex")
    public int getSex() {
        return mSex;
    }

    @JSONField(name = "sex")
    public void setSex(int sex) {
        mSex = sex;
    }

    @JSONField(name = "birthday")
    public String getBirthday() {
        return mBirthday;
    }

    @JSONField(name = "birthday")
    public void setBirthday(String birthday) {
        mBirthday = birthday;
    }

    @JSONField(name = "age")
    public String getAge() {
        return mAge;
    }

    @JSONField(name = "age")
    public void setAge(String age) {
        mAge = age;
    }

    @JSONField(name = "constellation")
    public String getXingZuo() {
        return mXingZuo;
    }

    @JSONField(name = "constellation")
    public void setXingZuo(String xingZuo) {
        mXingZuo = xingZuo;
    }

    @JSONField(name = "signature")
    public String getSignature() {
        return mSignature;
    }

    @JSONField(name = "signature")
    public void setSignature(String signature) {
        mSignature = signature;
    }

    @JSONField(name = "addr")
    public String getCity() {
        return mCity;
    }

    @JSONField(name = "addr")
    public void setCity(String city) {
        mCity = city;
    }

    @JSONField(name = "hobby")
    public String getInteret() {
        return mInteret;
    }

    @JSONField(name = "hobby")
    public void setInteret(String interet) {
        mInteret = interet;
    }

    @JSONField(name = "school")
    public String getSchool() {
        return mSchool;
    }

    @JSONField(name = "school")
    public void setSchool(String school) {
        mSchool = school;
    }

    @JSONField(name = "profession")
    public String getProfession() {
        return mProfession;
    }

    @JSONField(name = "profession")
    public void setProfession(String profession) {
        mProfession = profession;
    }

    @JSONField(name = "voice")
    public String getVoice() {
        return mVoice;
    }

    @JSONField(name = "voice")
    public void setVoice(String voice) {
        mVoice = voice;
    }

    @JSONField(name = "voice_l")
    public int getVoiceDuration() {
        return mVoiceDuration;
    }

    @JSONField(name = "voice_l")
    public void setVoiceDuration(int voiceDuration) {
        mVoiceDuration = voiceDuration;
    }

    @JSONField(name = "coin")
    public String getCoin() {
        return mCoin;
    }

    @JSONField(name = "coin")
    public void setCoin(String coin) {
        mCoin = coin;
    }

    @JSONField(name = "votes")
    public String getVotes() {
        return mVotes;
    }

    @JSONField(name = "votes")
    public void setVotes(String votes) {
        mVotes = votes;
    }

    @JSONField(name = "follows")
    public long getFollowNum() {
        return mFollowNum;
    }

    @JSONField(name = "follows")
    public void setFollowNum(long followNum) {
        mFollowNum = followNum;
    }

    @JSONField(name = "fans")
    public long getFansNum() {
        return mFansNum;
    }

    @JSONField(name = "fans")
    public void setFansNum(long fansNum) {
        mFansNum = fansNum;
    }

    @JSONField(name = "star")
    public String getStar() {
        return mStar;
    }

    @JSONField(name = "star")
    public void setStar(String star) {
        mStar = star;
    }


    @JSONField(name = "visitnums")
    public int getVisitNums() {
        return mVisitNums;
    }
    @JSONField(name = "visitnums")
    public void setVisitNums(int mVisitNums) {
        this.mVisitNums = mVisitNums;
    }

    @JSONField(name = "viewnums")
    public int getViewNums() {
        return mViewNums;
    }

    @JSONField(name = "viewnums")
    public void setViewNums(int mViewNums) {
        this.mViewNums = mViewNums;
    }

    @JSONField(name = "newnums")
    public int getNewNums() {
        return mNewNums;
    }
    @JSONField(name = "newnums")
    public void setNewNums(int mNewNums) {
        this.mNewNums = mNewNums;
    }

    @JSONField(name = "level")
    public int getLevel() {
        return mLevel;
    }

    @JSONField(name = "level")
    public void setLevel(int level) {
        mLevel = level;
    }

    @JSONField(name = "level_anchor")
    public int getAnchorLevel() {
        return mAnchorLevel;
    }

    @JSONField(name = "level_anchor")
    public void setAnchorLevel(int anchorLevel) {
        mAnchorLevel = anchorLevel;
    }

    @JSONField(name = "isshow_anchorlev")
    public int getShowAnchorLevel() {
        return mShowAnchorLevel;
    }

    @JSONField(name = "isshow_anchorlev")
    public void setShowAnchorLevel(int showAnchorLevel) {
        mShowAnchorLevel = showAnchorLevel;
    }

    public boolean isShowAnchorLevel(){
        return mShowAnchorLevel == 1;
    }

    public int getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(int isFollow) {
        this.isFollow = isFollow;
    }


    public String getConsumption() {
        return consumption;
    }

    public void setConsumption(String consumption) {
        this.consumption = consumption;
    }

    public String getVotestotal() {
        return votestotal;
    }

    public void setVotestotal(String votestotal) {
        this.votestotal = votestotal;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public Vip getVip() {
        return vip;
    }



    public void setVip(Vip vip) {
        this.vip = vip;
    }

    public Liang getLiang() {
        return liang;
    }

    public void setLiang(Liang liang) {
        this.liang = liang;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public boolean equals( Object obj) {
        if(obj!=null &&obj instanceof UserBean){
            UserBean userBean= (UserBean) obj;
            return StringUtil.equals(userBean.getId(),mId);
        }else{
            return obj.equals(this);
        }
    }
    public static boolean isSameUser(UserBean userBean1,UserBean userBean2){
        if(userBean1==null||userBean2==null){
            return false;
        }
        return StringUtil.equals(userBean1.getId(),userBean2.getId());
    }

    private static UserBean compareUserBean;
    public static UserBean getCompareUserBean(String uid){
       if(compareUserBean==null){
          compareUserBean=new UserBean();
       }
        compareUserBean.setId(uid);
       return compareUserBean;
    }


    public int getIsblack() {
        return isblack;
    }

    public void setIsblack(int isblack) {
        this.isblack = isblack;
    }

    /**
     * 显示靓号
     */
    public String getLiangNameTip() {
        if (this.liang != null) {
            String liangName = this.liang.getName();
            if (!TextUtils.isEmpty(liangName) && !"0".equals(liangName)) {
                return WordUtil.getString(R.string.live_liang) + ":" + liangName;
            }
        }
        return "ID:" + this.mId;
    }

    /**
     * 获取靓号
     */
    public String getGoodName() {
        if (this.liang != null) {
            return this.liang.getName();
        }
        return "0";
    }

    public int getVipType() {
        if (this.vip != null) {
            return this.vip.getType();
        }
        return 0;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mUserNiceName);
        dest.writeString(mAvatar);
        dest.writeString(mAvatarThumb);
        dest.writeInt(mSex);
        dest.writeInt(mLevel);
        dest.writeInt(mAnchorLevel);
        dest.writeInt(mShowAnchorLevel);
        dest.writeString(mBirthday);
        dest.writeString(mAge);
        dest.writeString(mXingZuo);
        dest.writeString(mSignature);
        dest.writeString(mCity);
        dest.writeString(mInteret);
        dest.writeString(mSchool);
        dest.writeString(mProfession);
        dest.writeString(mVoice);
        dest.writeInt(mVoiceDuration);
        dest.writeString(mCoin);
        dest.writeString(mVotes);
        dest.writeLong(mFollowNum);
        dest.writeLong(mFansNum);
        dest.writeString(mStar);
        dest.writeInt(mAuthNum);
        dest.writeInt(isFollow);
        dest.writeInt(mVisitNums);
        dest.writeInt(mViewNums);
        dest.writeInt(mNewNums);
        dest.writeInt(isblack);
        dest.writeString(consumption);
        dest.writeString(votestotal);
        dest.writeString(province);
        dest.writeString(location);
        dest.writeInt(lives);
        dest.writeParcelable(vip, flags);
        dest.writeParcelable(liang, flags);
        dest.writeParcelable(car, flags);
    }


    protected UserBean(Parcel in) {
        mId = in.readString();
        mUserNiceName = in.readString();
        mAvatar = in.readString();
        mAvatarThumb = in.readString();
        mSex = in.readInt();
        mLevel = in.readInt();
        mAnchorLevel = in.readInt();
        mShowAnchorLevel = in.readInt();
        mBirthday = in.readString();
        mAge = in.readString();
        mXingZuo = in.readString();
        mSignature = in.readString();
        mCity = in.readString();
        mInteret = in.readString();
        mSchool = in.readString();
        mProfession = in.readString();
        mVoice = in.readString();
        mVoiceDuration = in.readInt();
        mCoin = in.readString();
        mVotes = in.readString();
        mFollowNum = in.readLong();
        mFansNum = in.readLong();
        mStar = in.readString();
        mAuthNum = in.readInt();
        isFollow = in.readInt();
        mVisitNums = in.readInt();
        mViewNums = in.readInt();
        mNewNums = in.readInt();
        isblack = in.readInt();
        consumption = in.readString();
        votestotal = in.readString();
        province = in.readString();
        location = in.readString();
        lives = in.readInt();
        vip = in.readParcelable(Vip.class.getClassLoader());
        liang = in.readParcelable(Liang.class.getClassLoader());
        car = in.readParcelable(Car.class.getClassLoader());
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    public static class Vip implements Parcelable {
        protected int type;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public Vip() {

        }

        public Vip(Parcel in) {
            this.type = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.type);
        }

        public static final Creator<Vip> CREATOR = new Creator<Vip>() {
            @Override
            public Vip[] newArray(int size) {
                return new Vip[size];
            }

            @Override
            public Vip createFromParcel(Parcel in) {
                return new Vip(in);
            }
        };
    }

    public static class Liang implements Parcelable {
        protected String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Liang() {

        }


        public Liang(Parcel in) {
            this.name = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
        }

        public static final Creator<Liang> CREATOR = new Creator<Liang>() {

            @Override
            public Liang createFromParcel(Parcel in) {
                return new Liang(in);
            }

            @Override
            public Liang[] newArray(int size) {
                return new Liang[size];
            }
        };

    }

    public static class Car implements Parcelable {
        protected int id;
        protected String swf;
        protected float swftime;
        protected String words;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSwf() {
            return swf;
        }

        public void setSwf(String swf) {
            this.swf = swf;
        }

        public float getSwftime() {
            return swftime;
        }

        public void setSwftime(float swftime) {
            this.swftime = swftime;
        }

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }

        public Car() {

        }

        public Car(Parcel in) {
            this.id = in.readInt();
            this.swf = in.readString();
            this.swftime = in.readFloat();
            this.words = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.swf);
            dest.writeFloat(this.swftime);
            dest.writeString(this.words);
        }


        public static final Creator<Car> CREATOR = new Creator<Car>() {
            @Override
            public Car[] newArray(int size) {
                return new Car[size];
            }

            @Override
            public Car createFromParcel(Parcel in) {
                return new Car(in);
            }
        };

    }

}





