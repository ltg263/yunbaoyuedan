package com.yunbao.dynamic.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by Sky.L on 2020-11-05
 */
public class UserLabelInfoBean implements Parcelable {

    private String id;
    private String name;
    private String list_order;
    private String colour;
    private String colour2;

    public UserLabelInfoBean() {
    }

    protected UserLabelInfoBean(Parcel in) {
        id = in.readString();
        name = in.readString();
        list_order = in.readString();
        colour = in.readString();
        colour2 = in.readString();
    }

    public static final Creator<UserLabelInfoBean> CREATOR = new Creator<UserLabelInfoBean>() {
        @Override
        public UserLabelInfoBean createFromParcel(Parcel in) {
            return new UserLabelInfoBean(in);
        }

        @Override
        public UserLabelInfoBean[] newArray(int size) {
            return new UserLabelInfoBean[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getList_order() {
        return list_order;
    }

    public void setList_order(String list_order) {
        this.list_order = list_order;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getColour2() {
        return colour2;
    }

    public void setColour2(String colour2) {
        this.colour2 = colour2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(list_order);
        dest.writeString(colour);
        dest.writeString(colour2);
    }

    public boolean isUnable(){
        if (TextUtils.isEmpty(id) ||
                TextUtils.isEmpty(name)||
                TextUtils.isEmpty(colour)||
                TextUtils.isEmpty(colour2)
        ){
            return true;
        }
        return false;
    }
}
