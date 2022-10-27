package com.yunbao.dynamic.bean;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.commit.CommitEntity;
import com.yunbao.common.upload.UploadBean;
import com.yunbao.common.utils.FileUtil;
import com.yunbao.common.utils.ListUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommitPubDynamicBean extends CommitEntity {
    public static final int VIDEO_RESOURCE=1;
    public static final int PHOTO_RESOURCE=2;
    public static final int VOICE_RESOURCE=3;

   private String content;
   private List<String> thumbs;
   private String thumbsString;

   private String video;
   private String video_t;
   private String voice;
   private int voice_l;
   private String location;
   private String city;
   private String skillid=DEFAUlT_VALUE;
   private int resourceType;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        observer();
    }


    public List<String> getThumbs() {
        return thumbs;
    }

    public void setThumbs(List<String> thumbs) {
        this.thumbs = thumbs;
        observer();
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        clearResouce();
        this.video = video;
        observer();
    }

    public String getVideo_t() {
        return video_t;
    }

    public void setVideo_t(String video_t) {
        this.video_t = video_t;
        observer();
    }

    public String getVoice() {
        return voice;
    }
    public void setVoice(String voice) {
        clearResouce();
        this.voice = voice;
        observer();
    }

    public String getCity() {
       return CommonAppConfig.getInstance().getCity();
    }
    public void setCity(String city) {
        this.city = city;
    }

    //保证资源数据类型单一不出现多个,所以必须清空数据，避免上传参数混乱，接口参数会自动过滤掉空数据
    private void clearResouce() {
        video=null;
        video_t=null;
        thumbs=null;
        voice=null;
        voice_l=0;

    }

    public int getVoice_l() {
        return voice_l;
    }

    public void setVoice_l(int voice_l) {
        this.voice_l = voice_l;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public String getSkillid() {
        return skillid;
    }
    public void setSkillid(String skillid) {
        this.skillid = skillid;
    }

    /*创建上传对象，根据字段的存在去判断当前是装载了图片还是视频，还是语音*/
    public  List<UploadBean> createUploadBean(){
        List<UploadBean>uploadBeanList=null;
        if(ListUtil.haveData(thumbs)){
            uploadBeanList=new ArrayList<>();
            for(String thumb:thumbs){
                uploadBeanList.add(new UploadBean(new File(thumb)));
            }
            resourceType=PHOTO_RESOURCE;
        }else if(fieldNotEmpty(video)){
            video_t=FileUtil.saveVideoCoverPath(video);
            uploadBeanList= Arrays.asList(new UploadBean(new File(video)),new UploadBean(new File(video_t)));
            resourceType=VIDEO_RESOURCE;
        }else if(fieldNotEmpty(voice)){
            resourceType=VOICE_RESOURCE;
            uploadBeanList= Arrays.asList(new UploadBean(new File(voice)));
        }else{
            resourceType=0;
        }

        return uploadBeanList;
    }

    public void setResouce(List<UploadBean>uploadBeans){
         if(ListUtil.haveData(thumbs)){
             this.thumbsString=changeToString(uploadBeans);
         }else if(fieldNotEmpty(video)){
             UploadBean uploadBean=ListUtil.safeGetData(uploadBeans,0);
             if(uploadBean!=null){
                 video=uploadBean.getRemoteFileName();
             }
             uploadBean=ListUtil.safeGetData(uploadBeans,1);
             if(uploadBean!=null){
                 video_t=uploadBean.getRemoteFileName();
             }
         }else if(fieldNotEmpty(voice)){
             UploadBean uploadBean=ListUtil.safeGetData(uploadBeans,0);
             if(uploadBean!=null){
                 voice=uploadBean.getRemoteFileName();
             }
         }
    }

    private String changeToString(List<UploadBean> uploadBeans) {
        StringBuilder builder=new StringBuilder();
        for(UploadBean bean:uploadBeans){
            builder.append(bean.getRemoteFileName())
                    .append(",") ;
        }
        int length=builder.length();
        if(length>0){
            builder.deleteCharAt(length-1);
        }
        return builder.toString();
    }

    public String getThumbsString() {
        return thumbsString;
    }

    public int getResourceType() {
        return resourceType;
    }

    @Override
    public boolean observerCondition() {
        return  fieldNotEmpty(content)||
                fieldNotEmpty(thumbs)||
                fieldNotEmpty(video)||
                fieldNotEmpty(voice);
    }
}
