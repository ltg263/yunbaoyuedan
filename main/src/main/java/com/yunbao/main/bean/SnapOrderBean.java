package com.yunbao.main.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.common.bean.OrderCommentBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.MoneyHelper;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;

public class SnapOrderBean  implements Parcelable {
   public static final int STATUS_GRAB_TICKET=0; //抢单中
   public static final int STATUS_RECEIVED_ORDERS= OrderBean.STATUS_WAIT;//已接单
   public static final int STATUS_CANCEL=OrderBean.STATUS_CANCEL;//已取消
   public static final int STATUS_TIME_OUT=-2;//已超时

    private int isgrap;
    private int count;
    @SerializedName("id")
    private String mId;
    @SerializedName( "uid")
    private String mUid;
    @SerializedName("liveuid")
    private String mLiveUid;
    @SerializedName("skillid")
    private String mSkillId;
    @SerializedName("svctm")
    private long mServiceTime;
    @SerializedName("nums")
    private int mOrderNum;
    @SerializedName("total")
    private String mTotal;
    @SerializedName("fee")
    private String mFee;
    @SerializedName("profit")
    private String mProfit;
    @SerializedName("des")
    private String mDes;
    @SerializedName("status")
    private int mStatus;
    @SerializedName("addtime")
    private String mAddTime;
    private String mAddTimeString;
    @SerializedName("reason")
    private String mReason;
    @SerializedName("iscommnet")
    private int mIsComment;//用户是否评价了主播
    @SerializedName("isevaluate")
    private int mIsEvaluate;//主播是否评价了用户
    @SerializedName("userinfo")
    private UserBean mLiveUserInfo;
    @SerializedName("skill")
    private SkillBean mSkillBean;

    @SerializedName("datesvctm")
    private String serviceTimeFormat;

    private int sex;
    private int coin;

    private String level;

    private boolean mHasTitile;
    @SerializedName("tips")
    private String mTips;
    @SerializedName("tips_en")
    private String mTipsEn;
    @SerializedName("comment")
    private OrderCommentBean mCommentBean;//用户对主播的评价
    private OrderCommentBean mEvaluate;//主播对用户的评价


    @SerializedName("orderno")
    private String mOrderNumber;


    public SnapOrderBean() {

    }

    protected SnapOrderBean(Parcel in) {
        isgrap = in.readInt();
        count = in.readInt();
        mId = in.readString();
        mUid = in.readString();
        mLiveUid = in.readString();
        mSkillId = in.readString();
        mServiceTime = in.readLong();
        mOrderNum = in.readInt();
        mTotal = in.readString();
        mFee = in.readString();
        mProfit = in.readString();
        mDes = in.readString();
        mStatus = in.readInt();
        mAddTime = in.readString();
        mAddTimeString = in.readString();
        mReason = in.readString();
        mIsComment = in.readInt();
        mIsEvaluate = in.readInt();
        mLiveUserInfo = in.readParcelable(UserBean.class.getClassLoader());
        mSkillBean = in.readParcelable(SkillBean.class.getClassLoader());
        mHasTitile = in.readByte() != 0;
        mTips = in.readString();
        mTipsEn = in.readString();
        serviceTimeFormat= in.readString();
        mOrderNumber= in.readString();
        sex=in.readInt();
        coin=in.readInt();
        level=in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(isgrap);
        dest.writeInt(count);
        dest.writeString(mId);
        dest.writeString(mUid);
        dest.writeString(mLiveUid);
        dest.writeString(mSkillId);
        dest.writeLong(mServiceTime);
        dest.writeInt(mOrderNum);
        dest.writeString(mTotal);
        dest.writeString(mFee);
        dest.writeString(mProfit);
        dest.writeString(mDes);
        dest.writeInt(mStatus);
        dest.writeString(mAddTime);
        dest.writeString(mAddTimeString);
        dest.writeString(mReason);
        dest.writeInt(mIsComment);
        dest.writeInt(mIsEvaluate);
        dest.writeParcelable(mLiveUserInfo, flags);
        dest.writeParcelable(mSkillBean, flags);
        dest.writeByte((byte) (mHasTitile ? 1 : 0));
        dest.writeString(mTips);
        dest.writeString(mTipsEn);
        dest.writeString(serviceTimeFormat);
        dest.writeString(mOrderNumber);
        dest.writeInt(sex);
        dest.writeInt(coin);
        dest.writeString(level);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SnapOrderBean> CREATOR = new Creator<SnapOrderBean>() {
        @Override
        public SnapOrderBean createFromParcel(Parcel in) {
            return new SnapOrderBean(in);
        }

        @Override
        public SnapOrderBean[] newArray(int size) {
            return new SnapOrderBean[size];
        }
    };

    @JSONField(name = "id")
    public String getId() {
        return mId;
    }

    @JSONField(name = "id")
    public void setId(String id) {
        mId = id;
    }

    @JSONField(name = "uid")
    public String getUid() {
        return mUid;
    }

    @JSONField(name = "uid")
    public void setUid(String uid) {
        mUid = uid;
    }

    @JSONField(name = "liveuid")
    public String getLiveUid() {
        return mLiveUid;
    }

    @JSONField(name = "liveuid")
    public void setLiveUid(String liveUid) {
        mLiveUid = liveUid;
    }

    @JSONField(name = "skillid")
    public String getSkillId() {
        return mSkillId;
    }

    @JSONField(name = "skillid")
    public void setSkillId(String skillId) {
        mSkillId = skillId;
    }

    @JSONField(name = "svctm")
    public long getServiceTime() {
        return mServiceTime;
    }

    @JSONField(name = "svctm")
    public void setServiceTime(long serviceTime) {
        mServiceTime = serviceTime;
    }

    @JSONField(name = "nums")
    public int getOrderNum() {
        return mOrderNum;
    }

    @JSONField(name = "nums")
    public void setOrderNum(int orderNum) {
        mOrderNum = orderNum;
    }

    @JSONField(name = "total")
    public String getTotal() {
        return mTotal;
    }

    @JSONField(name = "total")
    public void setTotal(String total) {
        mTotal = total;
    }

    @JSONField(name = "fee")
    public String getFee() {
        return mFee;
    }

    @JSONField(name = "fee")
    public void setFee(String fee) {
        mFee = fee;
    }


    @JSONField(name = "profit")
    public String getProfit() {
        return mProfit;
    }

    @JSONField(name = "profit")
    public void setProfit(String profit) {
        mProfit = profit;
    }

    @JSONField(name = "des")
    public String getDes() {
        return mDes;
    }

    @JSONField(name = "des")
    public void setDes(String des) {
        mDes = des;
    }

    @JSONField(name = "status")
    public int getStatus() {
        return mStatus;
    }

    @JSONField(name = "status")
    public void setStatus(int status) {
        mStatus = status;
    }

    @JSONField(name = "addtime")
    public String getAddTime() {
        return mAddTime;
    }

    @JSONField(name = "addtime")
    public void setAddTime(String addTime) {
        mAddTime = addTime;
    }

    @JSONField(name = "reason")
    public String getReason() {
        return mReason;
    }

    @JSONField(name = "reason")
    public void setReason(String reason) {
        mReason = reason;
    }

    @JSONField(name = "iscommnet")
    public int getIsComment() {
        return mIsComment;
    }

    @JSONField(name = "iscommnet")
    public void setIsComment(int isComment) {
        mIsComment = isComment;
    }

    @JSONField(name = "isevaluate")
    public int getIsEvaluate() {
        return mIsEvaluate;
    }

    @JSONField(name = "isevaluate")
    public void setIsEvaluate(int isEvaluate) {
        mIsEvaluate = isEvaluate;
    }

    @JSONField(name = "userinfo")
    public UserBean getLiveUserInfo() {
        return mLiveUserInfo;
    }

    @JSONField(name = "userinfo")
    public void setLiveUserInfo(UserBean liveUserInfo) {
        mLiveUserInfo = liveUserInfo;
    }

    @JSONField(name = "skill")
    public SkillBean getSkillBean() {
        return mSkillBean;
    }

    @JSONField(name = "skill")
    public void setSkillBean(SkillBean skillBean) {
        mSkillBean = skillBean;
    }

    @JSONField(name = "tips")
    public String getTips() {
        return mTips;
    }

    @JSONField(name = "tips")
    public void setTips(String tips) {
        mTips = tips;
    }

    @JSONField(name = "tips_en")
    public String getTipsEn() {
        return mTipsEn;
    }
    @JSONField(name = "tips_en")
    public void setTipsEn(String tipsEn) {
        mTipsEn = tipsEn;
    }

    @JSONField(name = "comment")
    public OrderCommentBean getCommentBean() {
        return mCommentBean;
    }
    @JSONField(name = "comment")
    public void setCommentBean(OrderCommentBean commentBean) {
        mCommentBean = commentBean;
    }


    public String getOrderNumber() {
        return mOrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        mOrderNumber = orderNumber;
    }

    @JSONField(serialize = false)
    public boolean isHasTitile() {
        return mHasTitile;
    }

    @JSONField(serialize = false)
    public void setHasTitile(boolean hasTitile) {
        mHasTitile = hasTitile;
    }

    @JSONField(serialize = false)
    public String getAddTimeString() {
        return mAddTimeString;
    }

    @JSONField(serialize = false)
    public void setAddTimeString(String addTimeString) {
        mAddTimeString = addTimeString;
    }


    public boolean hasComment() {
        return mIsComment == 1;
    }


    public boolean hasEvaluate() {
        return mIsEvaluate == 1;
    }


    public OrderCommentBean getEvaluate() {
        return mEvaluate;
    }

    public void setEvaluate(OrderCommentBean evaluate) {
        mEvaluate = evaluate;
    }


    public String getServiceTimeFormat() {
        return serviceTimeFormat;
    }

    public void setServiceTimeFormat(String serviceTimeFormat) {
        this.serviceTimeFormat = serviceTimeFormat;
    }

    public boolean isMyAnchor() {
        return !TextUtils.isEmpty(mLiveUid) && mLiveUid.equals(CommonAppConfig.getInstance().getUid());
    }

    public int getIsgrap() {
        return isgrap;
    }

    public void setIsgrap(int isgrap) {
        this.isgrap = isgrap;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String parseSexCondition(){
        if(sex==0){
            return WordUtil.getString(R.string.no_limit);
        }else if(sex==1){
            return WordUtil.getString(R.string.woman);
        }else{
            return WordUtil.getString(R.string.man);
        }
    }

    public long getLastWaitTime() {
        if (mStatus != STATUS_GRAB_TICKET) {
            return 0;
        }
        return  (mServiceTime * 1000 - System.currentTimeMillis())/1000;
    }


    public String getAppointmentTime(){
//        if(mServiceTime!=0){
//          return  DateUtils.getFormatTime("yyyy-MM-dd HH:mm:ss",mServiceTime*1000);
//        }
        if (serviceTimeFormat != null){
            return serviceTimeFormat;
        }

        return "";
    }


    public String getTotalUnit() {
        if(mSkillBean!=null){
        return mOrderNum+"*"+mSkillBean.getUnit();
        }
        return Integer.toString(mOrderNum);
    }

    public String getLevel() {
        if(TextUtils.isEmpty(level)){
            return WordUtil.getString(R.string.no_limit);
        }
        return level;
    }
    public void setLevel(String level) {
        this.level = level;
    }

    public int getCoin() {
        return coin;
    }

    public String getCoinWithUnit() {
        return MoneyHelper.moneySymbol(coin,MoneyHelper.TYPE_PLATFORM);
    }

    public String getTotalCoinWithUnit() {
        return MoneyHelper.moneySymbol(coin*mOrderNum,MoneyHelper.TYPE_PLATFORM);
    }


    public void setCoin(int coin) {
        this.coin = coin;
    }


}
