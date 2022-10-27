package com.yunbao.main.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.dynamic.ui.activity.DynamicVideoActivity;
import com.yunbao.main.R;
import com.yunbao.dynamic.ui.activity.GalleryActivity;
import com.yunbao.dynamic.ui.activity.SelectPhotoActivity;
import java.util.ArrayList;
import java.util.List;
import static com.yunbao.common.Constants.DATA;
import static com.yunbao.common.Constants.EMPTY_TYPE;
import static com.yunbao.common.Constants.MAX_PHOTO_LENGTH;
import static com.yunbao.common.Constants.POSITION;
import static com.yunbao.common.Constants.DYNAMIC_PHOTO;
import static com.yunbao.common.Constants.DYNAMIC_VIDEO;
import static com.yunbao.common.Constants.DYNAMIC_VOICE;


@Deprecated
public class PubDynAdapter extends BaseRecyclerAdapter<String, BaseReclyViewHolder> {
    private static final String EMPTY_TAG="-1"; //元素占位符

    private int dataRequestType=EMPTY_TYPE;
    private Activity activity;

    public PubDynAdapter(List<String> data,Activity activity) {
        super(data);
        this.activity=activity;
    }

    @Override
    public int getLayoutId() {
        if(dataRequestType==DYNAMIC_PHOTO){
            return R.layout.item_recly_pub_image;
        }else if(dataRequestType==DYNAMIC_VIDEO){
            return R.layout.item_recly_pub_video;
        }else if(dataRequestType==DYNAMIC_VOICE){
            return R.layout.item_recly_pub_voice;
        }else{
          return R.layout.item_recly_pub_image;
        }
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, String item) {
        if(dataRequestType==DYNAMIC_PHOTO){
            convertPhoto(helper,item);
        }else if(dataRequestType==DYNAMIC_VIDEO){
            convertVideo(helper,item);
        }else if(dataRequestType==DYNAMIC_VOICE){
            convertVoice(helper,item);
        }
    }
    private void convertPhoto(BaseReclyViewHolder helper, String item) {
        if(TextUtils.isEmpty(item)||item.equals("-1")){
            helper.setImageResouceId(R.mipmap.icon_photo_add,R.id.image);
        }else{
           helper.setImageUrl(item,R.id.image);
        }
    }

    private void convertVideo(BaseReclyViewHolder helper, String item) {
        helper.setVideoThumb(item,R.id.image_thumb);
    }
    private void convertVoice(BaseReclyViewHolder helper, String item) {

    }
    private OnItemClickListener onItemVideoClickListener=new OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        }
    };

    private BaseQuickAdapter.OnItemClickListener onItemPhotoClickListener=new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            if( activity==null) {
                return;
            }
            String url=mData.get(position);
            mData.remove(EMPTY_TAG); //移除空白添加按钮的占位元素
            if(TextUtils.isEmpty(url)||url.equals(EMPTY_TAG)){
                Intent intent=new Intent(activity, SelectPhotoActivity.class);
                intent.putStringArrayListExtra(DATA,(ArrayList<String>) mData);
                activity.startActivityForResult(intent,DYNAMIC_PHOTO);
            }else{
                Intent intent=new Intent(activity, GalleryActivity.class);
                intent.putStringArrayListExtra(DATA,(ArrayList<String>) mData);
                intent.putExtra(POSITION,position);
                activity.startActivityForResult(intent,DYNAMIC_PHOTO);
            }
        }
    };


    @Override
    public void setData(List<String> data) {
        mData = data;
        addTag();
        notifyDataSetChanged();
    }


    private void addTag() {
        if(mData!=null&&mData.size()<MAX_PHOTO_LENGTH&&dataRequestType==DYNAMIC_PHOTO){
            mData.add(EMPTY_TAG);
        }
    }

    private OnItemClickListener onItemVoiceClickListener=new OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        }
    };

    public void setDataRequestType(int dataRequestType) {
        this.dataRequestType = dataRequestType;
        mLayoutResId=getLayoutId();
        if(dataRequestType==DYNAMIC_PHOTO){
          setOnItemClickListener(onItemPhotoClickListener);
        }else if(dataRequestType==DYNAMIC_VIDEO){
            setOnItemClickListener(onItemVideoClickListener);
        }else if(dataRequestType==DYNAMIC_VOICE){
            setOnItemClickListener(onItemVoiceClickListener);
        }else{
            setOnItemClickListener(null);
        }
    }
    public int getDataRequestType() {
        return dataRequestType;
    }
}
