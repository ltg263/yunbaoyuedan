package com.yunbao.common.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.R;
import com.yunbao.common.utils.WordUtil;

/**
 * Created by cxf on 2019/8/5.
 */

public class OrderBean implements Parcelable {

    public static final int STATUS_CANCEL = -1;//已取消
    public static final int STATUS_DONE = -2;//已完成
    public static final int STATUS_REJECT = -3;//已拒绝
    public static final int STATUS_TIMEOUT = -4;//已超时
    public static final int STATUS_WAIT = 1;//待服务
    public static final int STATUS_DOING = 2;//进行中
    public static final int STATUS_WAIT_REFUND =3;//等待退款
    public static final int STATUS_REFUSE_REFUND =4;//拒绝退款
    public static final int STATUS_AGREE_REFUND =5;//同意退款
    public static final int STATUS_WAIT_PLATFORM =6;//等待平台退款
    public static final int STATUS_ORDER_TO_BE_PAID =0;//刚下单，待支付



    public static final int STATUS_RECEPT_APPLYED = -1;//已申请等待陪玩
    public static final int STATUS_RECEPT_DEFAULT = 0;//默认倒计时陪玩状态
    public static final int STATUS_RECEPT_ARGREE = 2;//同意
    public static final int STATUS_RECEPT_REFUSE = 1;//拒绝


    @SerializedName("id")
    private String mId;
    @SerializedName( "uid")
    private String mUid;
    @SerializedName("liveuid")
    private String mLiveUid;
    @SerializedName("skillid")
    private String mSkillId;
    @SerializedName("svctm")
    private String mServiceTime;
    @SerializedName("orderno")
    private String mOrderNo;//订单号 orderno
    @SerializedName("nums")
    private String mOrderNum;
    @SerializedName("total")
    private String mTotal;
    @SerializedName("fee")
    private String mFee;
    @SerializedName("profit")
    private String mProfit;
    @SerializedName("des")
    private String mDes;
    @SerializedName("f")
    private int mStatus;
    @SerializedName("addtime")
    private long mAddTime;
    //腾讯IM 收到消息的时间
    @SerializedName("timestamp")
    private long mTimestamp;
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

    private boolean mHasTitile;
    @SerializedName("tips")
    private String mTips;
    @SerializedName("tips_en")
    private String mTipsEn;
    @SerializedName("comment")
    private OrderCommentBean mCommentBean;//用户对主播的评价

    private OrderCommentBean mEvaluate;//主播对用户的评价


    @SerializedName("recept_status")
    private int receptStatus; //申请立即服务的时候用的到

    @SerializedName("ishideok")
    private int shouldHide;

    @SerializedName("auth")
    private AuthBean mAuth;

    @SerializedName("datesvctm")
    private String serviceTimeFormat;




    public OrderBean() {

    }

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
    public String getServiceTime() {
        return mServiceTime;
    }

    @JSONField(name = "svctm")
    public void setServiceTime(String serviceTime) {
        mServiceTime = serviceTime;
    }

    @JSONField(name = "nums")
    public String getOrderNum() {
        return mOrderNum;
    }

    @JSONField(name = "nums")
    public void setOrderNum(String orderNum) {
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
    public long getAddTime() {
        return mAddTime;
    }

    @JSONField(name = "addtime")
    public void setAddTime(long addTime) {
        mAddTime = addTime;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
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

    @JSONField(name = "auth")
    public AuthBean getAuth() {
        return mAuth;
    }

    @JSONField(name = "auth")
    public void setAuth(AuthBean auth) {
        this.mAuth = auth;
    }

    public String getOrderNo() {
        return mOrderNo;
    }

    public void setOrderNo(String orderNo) {
        mOrderNo = orderNo;
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

    @Override
    public int describeContents() {
        return 0;
    }


    @JSONField(name ="recept_status")
    public int getReceptStatus() {
        return receptStatus;
    }

    @JSONField(name ="recept_status")
    public void setReceptStatus(int receptStatus) {
        this.receptStatus = receptStatus;
    }

    public String getServiceTimeFormat() {
        return serviceTimeFormat;
    }

    public void setServiceTimeFormat(String serviceTimeFormat) {
        this.serviceTimeFormat = serviceTimeFormat;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mUid);
        dest.writeString(mLiveUid);
        dest.writeString(mSkillId);
        dest.writeString(mServiceTime);
        dest.writeString(mOrderNo);
        dest.writeString(mOrderNum);
        dest.writeString(mTotal);
        dest.writeString(mFee);
        dest.writeString(mProfit);
        dest.writeString(mDes);
        dest.writeInt(mStatus);
        dest.writeLong(mAddTime);
        dest.writeLong(mTimestamp);
        dest.writeString(mReason);
        dest.writeParcelable(mLiveUserInfo, flags);
        dest.writeParcelable(mSkillBean, flags);
        dest.writeParcelable(mAuth, flags);
        dest.writeInt(receptStatus);
        dest.writeInt(shouldHide);
        dest.writeString(serviceTimeFormat);

    }


    public OrderBean(Parcel in) {
        mId = in.readString();
        mUid = in.readString();
        mLiveUid = in.readString();
        mSkillId = in.readString();
        mServiceTime = in.readString();
        mOrderNo = in.readString();
        mOrderNum = in.readString();
        mTotal = in.readString();
        mFee = in.readString();
        mProfit = in.readString();
        mDes = in.readString();
        mStatus = in.readInt();
        mAddTime = in.readLong();
        mTimestamp = in.readLong();
        mReason = in.readString();
        mLiveUserInfo = in.readParcelable(UserBean.class.getClassLoader());
        mSkillBean = in.readParcelable(SkillBean.class.getClassLoader());
        mAuth = in.readParcelable(AuthBean.class.getClassLoader());
        receptStatus=in.readInt();
        shouldHide=in.readInt();
        serviceTimeFormat= in.readString();
    }


    public static final Creator<OrderBean> CREATOR = new Creator<OrderBean>() {
        @Override
        public OrderBean createFromParcel(Parcel in) {
            return new OrderBean(in);
        }

        @Override
        public OrderBean[] newArray(int size) {
            return new OrderBean[size];
        }
    };

    public String getStatusString() {
        //是否  用户已经评价了主播  1已评
        if (mIsComment == 1){
            return WordUtil.getString(R.string.evaluated);
        }
        if (mStatus == STATUS_CANCEL) {
            return WordUtil.getString(R.string.order_status_cancel);
        } else if (mStatus == STATUS_DONE) {
            return WordUtil.getString(R.string.order_status_done);
        } else if (mStatus == STATUS_REJECT) {
            return WordUtil.getString(R.string.order_status_reject);
        } else if (mStatus == STATUS_WAIT) {
            if(getLastWaitTime()<=0){
                return WordUtil.getString(R.string.order_status_time_out);
            }
            return WordUtil.getString(R.string.order_status_wait);
        } else if (mStatus == STATUS_DOING) {
            return WordUtil.getString(R.string.order_status_doing);
        } else if (mStatus == STATUS_TIMEOUT) {
            return WordUtil.getString(R.string.order_status_time_out);
        }else if(mStatus == STATUS_WAIT_REFUND){
            return WordUtil.getString(R.string.order_status_refuse_wait);
        }else if(mStatus == STATUS_REFUSE_REFUND){
            return WordUtil.getString(R.string.order_status_refuse_refund);
        }else if(mStatus ==STATUS_AGREE_REFUND){
            return WordUtil.getString(R.string.order_status_refuse_agree);
        }else if(mStatus==STATUS_WAIT_PLATFORM){
            return WordUtil.getString(R.string.order_status_refuse_wait_platform);
        }
        return null;
    }


    @JSONField(name = "ishideok")
    public int getShouldHide() {
        return shouldHide;
    }

    @JSONField(name = "ishideok")
    public void setShouldHide(int shouldHide) {
        this.shouldHide = shouldHide;
    }

    public boolean isMyAnchor() {
        return !TextUtils.isEmpty(mLiveUid) && mLiveUid.equals(CommonAppConfig.getInstance().getUid());
    }


    public long getLastWaitTime() {
        if (mStatus != STATUS_WAIT) {
            return 0;
        }
        return 15 * 60 * 1000 + mAddTime * 1000 - System.currentTimeMillis();
    }

    public static class AuthBean implements Parcelable{
        private String coin;
        @JSONField(name = "switch")
        private String mSkillSwitch;

        public AuthBean() {
        }

        protected AuthBean(Parcel in) {
            coin = in.readString();
            mSkillSwitch = in.readString();
        }

        public static final Creator<AuthBean> CREATOR = new Creator<AuthBean>() {
            @Override
            public AuthBean createFromParcel(Parcel in) {
                return new AuthBean(in);
            }

            @Override
            public AuthBean[] newArray(int size) {
                return new AuthBean[size];
            }
        };

        public String getCoin() {
            return coin;
        }

        public void setCoin(String coin) {
            this.coin = coin;
        }

        @JSONField(name = "switch")
        public String getSkillSwitch() {
            return mSkillSwitch;
        }

        @JSONField(name = "switch")
        public void setSkillSwitch(String skillSwitch) {
            mSkillSwitch = skillSwitch;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(coin);
            dest.writeString(mSkillSwitch);
        }
    }
}
