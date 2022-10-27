package com.yunbao.common.adapter.base;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yunbao.common.Constants;
import com.yunbao.common.glide.ImgLoader;

import java.io.File;

public  class BaseReclyViewHolder extends BaseViewHolder {

        public BaseReclyViewHolder(View view) {
            super(view);
        }
        public void setImageUrl(String url, int id){
            ImageView imageView=getView(id);
            if(imageView==null|| TextUtils.isEmpty(url)){
                return;
            }
            if (url.contains(Constants.HTTP)){
                ImgLoader.display(imageView.getContext(),url,imageView);
            }else {
                File file = new File(url);
                ImgLoader.display(imageView.getContext(), file, imageView);
            }
        }


    public void setImageResouceId(int resoureId,int id){
        ImageView imageView=getView(id);
        if(imageView==null ){
            return;
        }
        ImgLoader.display(imageView.getContext(),resoureId,imageView);
    }


    public void setImageDrawable(int resoureId,int id){
        ImageView imageView=getView(id);
        if(imageView==null ){
            return;
        }
        ImgLoader.display(imageView.getContext(),resoureId,imageView);
    }

    public void setVideoThumb(String url,int id){
        ImageView imageView=getView(id);
        if(imageView==null||TextUtils.isEmpty(url) ){
            return;
        }
        ImgLoader.displayVideoThumb(imageView.getContext(),url,imageView);
    }


    public void setVideoThumbRemote(String url,int id){
        ImageView imageView=getView(id);
        if(imageView==null||TextUtils.isEmpty(url) ){
            return;
        }
        ImgLoader.displayVideoThumbRemote(imageView.getContext(),url,imageView);
    }



    public void setImageResouceFile(File file, int id){
        ImageView imageView=getView(id);
        if(imageView==null ){
            return;
        }
        ImgLoader.display(imageView.getContext(),file,imageView);
    }


    }