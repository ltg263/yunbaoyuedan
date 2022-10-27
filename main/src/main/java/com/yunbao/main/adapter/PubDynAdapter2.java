package com.yunbao.main.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.dynamic.adapter.DynamicResourceAdapter;
import com.yunbao.dynamic.bean.ResourseBean;
import com.yunbao.dynamic.ui.activity.DynamicVideoActivity;
import com.yunbao.dynamic.ui.activity.GalleryActivity;
import com.yunbao.dynamic.ui.activity.SelectPhotoActivity;
import com.yunbao.dynamic.widet.VoicePlayView;
import java.util.List;
import static com.yunbao.common.Constants.DATA;
import static com.yunbao.common.Constants.EMPTY_TYPE;
import static com.yunbao.common.Constants.MAX_PHOTO_LENGTH;
import static com.yunbao.common.Constants.POSITION;
import static com.yunbao.common.Constants.DYNAMIC_PHOTO;
import static com.yunbao.common.Constants.TYPE;


public class PubDynAdapter2 extends DynamicResourceAdapter {
    private static final String EMPTY_TAG="-1"; //元素占位符
    private int type=EMPTY_TYPE;
    private DataLisnter dataLisnter;

    public PubDynAdapter2(List<ResourseBean> data, Activity activity) {
        super(data, activity);
    }

    @Override
    protected void clickPhoto(String resouce, int position, View view) {
        if( activity==null) {
            return;
        }
        ResourseBean resourseBean=mData.get(mData.size()-1);
        if(resourseBean.getResouce().equals(EMPTY_TAG)){
            mData.remove(resourseBean); //移除空白添加按钮的占位元素
        }
        if(TextUtils.isEmpty(resouce)||resouce.equals(EMPTY_TAG)){
            Intent intent=new Intent(activity, SelectPhotoActivity.class);
            intent.putStringArrayListExtra(DATA,ResourseBean.valuesTo(mData));
            activity.startActivityForResult(intent,DYNAMIC_PHOTO);
        }else{
            Intent intent=new Intent(activity, GalleryActivity.class);
            intent.putStringArrayListExtra(DATA,ResourseBean.valuesTo(mData));
            intent.putExtra(POSITION,position);
            intent.putExtra(TYPE,GalleryActivity.TYPE_EDIT);
            activity.startActivityForResult(intent,DYNAMIC_PHOTO);
        }
    }

    @Override
    protected void convertVideo(BaseReclyViewHolder helper, ResourseBean item) {
        helper.setVideoThumb(item.getResouce(), com.yunbao.dynamic.R.id.image_thumb);
    }

    @Override
    protected void convertVoice(final BaseReclyViewHolder helper,final  ResourseBean item) {
        super.convertVoice(helper, item);
        VoicePlayView voicePlayView=helper.getView(com.yunbao.dynamic.R.id.voiceView);
        voicePlayView.setCanDelete(true);
        voicePlayView.setDeleteListner(new VoicePlayView.OnDeleteListner() {
            @Override
            public void delete() {
               setData(null);
            }
        });
    }

    @Override
    protected void clickVideo(String resouce, String cover, int position) {
        DynamicVideoActivity.forwordForResult((Activity) mContext,DynamicVideoActivity.TYPE_EDIT,resouce,cover);
    }

    @Override
    protected void clickVoice(String resouce, int position) {
    }
    @Override
    public void setData(List<ResourseBean> data) {
        if(dataLisnter!=null){
           dataLisnter.dataChange(data);
        }
        setNewData(data);
        addTag();
        notifyDataSetChanged();
    }

    private void addTag() {
        if(mData!=null&&mData.size()<MAX_PHOTO_LENGTH&&type==DYNAMIC_PHOTO){
            mData.add(new ResourseBean(type,EMPTY_TAG));
        }
    }

    public interface DataLisnter{
        public void dataChange(List<ResourseBean> data);
    }

    public void setDataLisnter(DataLisnter dataLisnter) {
        this.dataLisnter = dataLisnter;
    }


    public void setDataRequestType(int type) {
        this.type = type;
    }
    public int getDataRequestType() {
        return type;
    }
}
