package com.yunbao.common.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.R;
import com.yunbao.common.utils.L;

import java.io.File;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by cxf on 2017/8/9.
 */

public class ImgLoader {

    private static final boolean SKIP_MEMORY_CACHE = false;

    private static Headers sHeaders;
    private static BlurTransformation sBlurTransformation;

    static {
        sHeaders = new Headers() {
            @Override
            public Map<String, String> getHeaders() {
                return CommonAppConfig.HEADER;
            }
        };
        sBlurTransformation= new BlurTransformation(40);
    }


    public static void display(Context context, String url, ImageView imageView) {
        if(context==null || imageView == null){
            return;
        }
        Glide.with(context).asDrawable().load(new GlideUrl(url, sHeaders)).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void displayWithError(Context context, String url, ImageView imageView, int errorRes) {
        if(context==null || imageView == null){
            return;
        }
        Glide.with(context).asDrawable().load(new GlideUrl(url, sHeaders)).skipMemoryCache(SKIP_MEMORY_CACHE).error(errorRes).into(imageView);
    }

    public static void displayAvatar(Context context, String url, ImageView imageView) {
        if(context==null || imageView == null){
            return;
        }
        displayWithError(context, url, imageView, R.mipmap.icon_avatar_placeholder);
    }

    public static void display(Context context, File file, ImageView imageView) {
        if(context==null || imageView == null){
            return;
        }
        Glide.with(context).asDrawable().load(file).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void displayInChatList(Context context, File file, ImageView imageView) {
        if(context==null || imageView == null){
            return;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(),opts);
        //获取图片的宽高
        int height = opts.outHeight;
        int width = opts.outWidth;
//        L.e("图片的宽度:"+width+"图片的高度:"+height);
        Glide.with(context).asDrawable().load(file).skipMemoryCache(SKIP_MEMORY_CACHE).override(width,height).into(imageView);
    }


    public static void display(Context context, int res, ImageView imageView) {
        if(context==null || imageView == null){
            return;
        }
        Glide.with(context).asDrawable().load(res).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void clear(Context context, ImageView imageView) {
        Glide.with(context).clear(imageView);
    }

    public static void clearMemory(Context context) {
        Glide.get(context).clearMemory();
    }

    /**
     * 显示视频封面缩略图
     */

    public static void displayVideoThumb(Context context, String videoPath, ImageView imageView) {
        if(context==null || imageView == null){
            return;
        }
        Glide.with(context).asDrawable().load(Uri.fromFile(new File(videoPath))).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void displayVideoThumbRemote(Context context, String videoPath, ImageView imageView) {
        if(context==null || imageView == null){
            return;
        }
        Glide.with(context).asDrawable().load(Uri.parse(videoPath)).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void displayDrawable(Context context, String url, final DrawableCallback callback) {
        if(context==null ){
            return;
        }
        Glide.with(context).asDrawable().load(new GlideUrl(url, sHeaders)).skipMemoryCache(SKIP_MEMORY_CACHE).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                if (callback != null) {
                    callback.onLoadSuccess(resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                if (callback != null) {
                    callback.onLoadFailed();
                }
            }
        });
    }


    public static void display(Context context, String url, ImageView imageView, int placeholderRes) {
        if(context==null || imageView == null){
            return;
        }
        Glide.with(context).asDrawable().load(new GlideUrl(url, sHeaders)).skipMemoryCache(SKIP_MEMORY_CACHE).placeholder(placeholderRes).into(imageView);
    }

    /**
     * 显示模糊的毛玻璃图片
     */
    public static void displayBlur(Context context, String url, ImageView imageView) {
        if (context == null || imageView == null) {
            return;
        }
        Glide.with(context).asDrawable().load(new GlideUrl(url, sHeaders))
                .skipMemoryCache(SKIP_MEMORY_CACHE)
                .apply(RequestOptions.bitmapTransform(sBlurTransformation))
                .into(imageView);
    }



    public interface DrawableCallback {
        void onLoadSuccess(Drawable drawable);

        void onLoadFailed();
    }


}
