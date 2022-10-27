package com.yunbao.dynamic.bean;

import android.os.Parcel;
import com.google.gson.annotations.Expose;
import com.yunbao.dynamic.adapter.DynamicResourceAdapter;
import java.util.Arrays;
import java.util.List;
import static com.yunbao.common.Constants.DYNAMIC_PHOTO;
import static com.yunbao.common.Constants.DYNAMIC_VIDEO;
import static com.yunbao.common.Constants.DYNAMIC_VOICE;

public class MyDynamicBean extends DynamicBean{

    private List<ResourseBean>resourseBeanArray;
    @Expose
    private transient DynamicResourceAdapter resourceAdapter;
    protected MyDynamicBean(Parcel in) {
        super(in);
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public List<ResourseBean> getResourseBeanArray() {
        if(resourseBeanArray!=null) {
            return resourseBeanArray;
        }
        if(type==DYNAMIC_PHOTO){
          resourseBeanArray= ResourseBean.transForm(type,thumbs);
        }else if(type==DYNAMIC_VIDEO){
          resourseBeanArray= Arrays.asList(new ResourseBean(type,video,video_t));
        }else if(type==DYNAMIC_VOICE){
            resourseBeanArray= Arrays.asList(new ResourseBean(type,voice,voice_l));
        }
        return resourseBeanArray;
    }

    public DynamicResourceAdapter getResourceAdapter() {
        return resourceAdapter;
    }

    public void setResourceAdapter(DynamicResourceAdapter resourceAdapter) {
        this.resourceAdapter = resourceAdapter;
    }
}
