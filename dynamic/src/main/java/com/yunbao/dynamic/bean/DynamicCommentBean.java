package com.yunbao.dynamic.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.util.CommentTextRender;

import java.util.List;


/**
 * Created by cxf on 2017/7/14.
 */

public class DynamicCommentBean implements Parcelable {

    public static final String REPLY = WordUtil.getString(R.string.comment_reply) + " ";

    @SerializedName( "id")
    @JSONField(name = "id")
    private String mId;

    @SerializedName( "uid")
    @JSONField(name = "uid")
    private String mUid;

    @SerializedName( "touid")
    @JSONField(name = "touid")
    private String mToUid;

    @SerializedName( "did")
    @JSONField(name = "did")
    private String mDynamicId;

    @SerializedName( "cid")
    @JSONField(name = "cid")
    private String mCommentId;

    @SerializedName( "parentid")
    @JSONField(name = "parentid")
    private String mParentId;

    @SerializedName( "content")
    @JSONField(name = "content")
    private String mContent;

    @SerializedName( "likes")
    @JSONField(name = "likes")
    private String mLikeNum;

    @SerializedName( "addtime")
    @JSONField(name = "addtime")
    private String mAddTime;

    @SerializedName( "at_info")
    @JSONField(name = "at_info")
    private String mAtInfo;

    @SerializedName( "islike")
    @JSONField(name = "islike")
    private int mIsLike;

    @SerializedName( "replys")
    @JSONField(name = "replys")
    private int mReplyNum;

    @SerializedName( "replycount")
    @JSONField(name = "replycount")
    private int mReplycount;

    @SerializedName( "datetime")
    @JSONField(name = "datetime")
    private String mDatetime;


    @SerializedName( "userinfo")
    @JSONField(name = "userinfo")
    private UserBean mUserBean;

    @SerializedName( "touserinfo")
    @JSONField(name = "touserinfo")
    private UserBean mToUserBean;

    @SerializedName( "reply")
    @JSONField(name = "reply")
    private List<DynamicCommentBean> mChildList;


    private boolean mParentNode;//是否是父元素
    private int mPosition;
    private DynamicCommentBean mParentNodeBean;
    private int mChildPage = 1;
    @JSONField(name = "type")
    private int mType;
    private String mVoiceLink;
    private String mVoiceDuration;
    private boolean mVoicePlaying;

    public DynamicCommentBean() {

    }


    public String getId() {
        return mId;
    }


    public void setId(String id) {
        mId = id;
    }


    public String getUid() {
        return mUid;
    }


    public void setUid(String uid) {
        mUid = uid;
    }


    public String getToUid() {
        return mToUid;
    }


    public void setToUid(String toUid) {
        mToUid = toUid;
    }


    public String getDynamicId() {
        return mDynamicId;
    }

    public void setDynamicId(String dynamicId) {
        mDynamicId = dynamicId;
    }


    public String getCommentId() {
        return mCommentId;
    }


    public void setCommentId(String commentId) {
        mCommentId = commentId;
    }


    public String getParentId() {
        return mParentId;
    }


    public void setParentId(String parentId) {
        mParentId = parentId;
    }

    public CharSequence getContent() {

        if (!mParentNode && this.mToUserBean != null && !TextUtils.isEmpty(mToUserBean.getId())) {
            String userName = mToUserBean.getUserNiceName();
            if (!TextUtils.isEmpty(userName)) {
                return REPLY + userName+" : "+mContent;
            }
        }
        return mContent;
    }

    public CharSequence getToUserName(){
        if (!mParentNode && this.mToUserBean != null && !TextUtils.isEmpty(mToUserBean.getId())) {
            String userName = mToUserBean.getUserNiceName();
            if (!TextUtils.isEmpty(userName)) {
                return userName;
            }
        }
        return null;
    }


    public void setContent(String content) {
        mContent = content;
    }


    public String getLikeNum() {
        return mLikeNum;
    }

    public void setLikeNum(String likeNum) {
        mLikeNum = likeNum;
    }

    public String getAddTime() {
        return mAddTime;
    }

    public void setAddTime(String addTime) {
        mAddTime = addTime;
    }

    public UserBean getUserBean() {
        return mUserBean;
    }

    public void setUserBean(UserBean userBean) {
        mUserBean = userBean;
    }

    public String getDatetime() {
        return mDatetime;
    }

    public void setDatetime(String datetime) {
        mDatetime = datetime;
    }

    public int getIsLike() {
        return mIsLike;
    }

    public void setIsLike(int like) {
        mIsLike = like;
    }


    public String getAtInfo() {
        return mAtInfo;
    }


    public void setAtInfo(String atInfo) {
        mAtInfo = atInfo;
    }



    public UserBean getToUserBean() {
        return mToUserBean;
    }

    public void setToUserBean(UserBean toUserBean) {
        mToUserBean = toUserBean;
    }


    public int getReplyNum() {
        return mReplyNum;
    }

    public void setReplyNum(int replyNum) {
        mReplyNum = replyNum;
    }

    public int getReplycount() {
        return mReplycount;
    }

    public void setReplycount(int replycount) {
        mReplycount = replycount;
    }

    public List<DynamicCommentBean> getChildList() {
        return mChildList;
    }

    public void setChildList(List<DynamicCommentBean> childList) {
        mChildList = childList;
        for (DynamicCommentBean bean : childList) {
            if (bean != null) {
                bean.setParentNodeBean(this);
            }
        }
    }
    public int getType() {
        return mType;
    }


    public void setType(int type) {
        mType = type;
    }

    @JSONField(name = "voice")
    public String getVoiceLink() {
        return mVoiceLink;
    }

    @JSONField(name = "voice")
    public void setVoiceLink(String voiceLink) {
        mVoiceLink = voiceLink;
    }

    @JSONField(name = "length")
    public String getVoiceDuration() {
        return mVoiceDuration;
    }

    @JSONField(name = "length")
    public void setVoiceDuration(String voiceDuration) {
        mVoiceDuration = voiceDuration;
    }
    public boolean isVoice() {
        return mType == 1;
    }

    public boolean isVoicePlaying() {
        return mVoicePlaying;
    }


    public void setVoicePlaying(boolean voicePlaying) {
        mVoicePlaying = voicePlaying;
    }



    public DynamicCommentBean getParentNodeBean() {
        return mParentNodeBean;
    }

    public void setParentNodeBean(DynamicCommentBean parentNodeBean) {
        mParentNodeBean = parentNodeBean;
    }

    public boolean isParentNode() {
        return mParentNode;
    }

    public void setParentNode(boolean parentNode) {
        mParentNode = parentNode;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    @JSONField(serialize = false)
    public int getChildPage() {
        return mChildPage;
    }
    @JSONField(serialize = false)
    public void setChildPage(int childPage) {
        mChildPage = childPage;
    }

    public boolean isFirstChild(DynamicCommentBean parentNodeBean) {
        if (!mParentNode && parentNodeBean != null) {
            List<DynamicCommentBean> parentChildList = parentNodeBean.getChildList();
            if (parentChildList != null && parentChildList.size() > 0) {
                return this == parentChildList.get(0);
            }
        }
        return false;
    }


    public boolean needShowExpand(DynamicCommentBean parentNodeBean) {
        if (!mParentNode && parentNodeBean != null) {
            List<DynamicCommentBean> parentChildList = parentNodeBean.getChildList();
            if (parentChildList != null) {
                int size = parentChildList.size();
                if (size>=3 && this == parentChildList.get(size - 1) && parentNodeBean.getReplycount() > size) {
                    return true;
                }
                /*if (parentNodeBean.getReplyNum() > 1 && parentNodeBean.getReplyNum() > size && this == parentChildList.get(size - 1)) {
                    return true;
                }*/
            }
        }
        return false;
    }

    public boolean needShowCollapsed(DynamicCommentBean parentNodeBean) {
        if (!mParentNode && parentNodeBean != null) {
            List<DynamicCommentBean> parentChildList = parentNodeBean.getChildList();
            if (parentChildList != null) {
                int size = parentChildList.size();
                 if (size > 3 && this == parentChildList.get(size - 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeChild() {
        if (mChildList != null && mChildList.size() > 1) {
            mChildList = mChildList.subList(0, 3);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mUid);
        dest.writeString(mToUid);
        dest.writeString(mDynamicId);
        dest.writeString(mCommentId);
        dest.writeString(mParentId);
        dest.writeString(mContent);
        dest.writeString(mLikeNum);
        dest.writeString(mAddTime);
        dest.writeString(mAtInfo);
        dest.writeInt(mIsLike);
        dest.writeInt(mReplyNum);
        dest.writeInt(mReplycount);
        dest.writeString(mDatetime);
        dest.writeParcelable(mUserBean, flags);
        dest.writeParcelable(mToUserBean, flags);
        dest.writeTypedList(mChildList);
        dest.writeByte((byte) (mParentNode ? 1 : 0));
        dest.writeInt(mPosition);
        dest.writeInt(mType);
        dest.writeString(mVoiceLink);
        dest.writeString(mVoiceDuration);
    }


    public DynamicCommentBean(Parcel in) {
        mId = in.readString();
        mUid = in.readString();
        mToUid = in.readString();
        mDynamicId = in.readString();
        mCommentId = in.readString();
        mParentId = in.readString();
        mContent = in.readString();
        mLikeNum = in.readString();
        mAddTime = in.readString();
        mAtInfo = in.readString();
        mIsLike = in.readInt();
        mReplyNum = in.readInt();
        mReplycount = in.readInt();
        mDatetime = in.readString();
        mUserBean = in.readParcelable(UserBean.class.getClassLoader());
        mToUserBean = in.readParcelable(UserBean.class.getClassLoader());
        mChildList = in.createTypedArrayList(DynamicCommentBean.CREATOR);
        mParentNode = in.readByte() != 0;
        mPosition = in.readInt();
        mType = in.readInt();
        mVoiceLink=in.readString();
        mVoiceDuration=in.readString();
    }

    public static final Creator<DynamicCommentBean> CREATOR = new Creator<DynamicCommentBean>() {
        @Override
        public DynamicCommentBean createFromParcel(Parcel in) {
            return new DynamicCommentBean(in);
        }

        @Override
        public DynamicCommentBean[] newArray(int size) {
            return new DynamicCommentBean[size];
        }
    };
}
