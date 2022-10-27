package com.yunbao.dynamic.business;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.yunbao.dynamic.R;

public class AnimHelper {
   public static final int[] FOLLOW_ANIM_LIST={
           R.mipmap.zan_0,
           R.mipmap.zan_1,
           R.mipmap.zan_2,
           R.mipmap.zan_3,
           R.mipmap.zan_4,
           R.mipmap.zan_5,
           };

    public static final int[] FOLLOW_ANIM_VIDEO_LIST={
            R.mipmap.zan_video0,
            R.mipmap.zan_video1,
            R.mipmap.zan_video2,
            R.mipmap.zan_video3,
            R.mipmap.zan_video4,
            R.mipmap.zan_video5,
    };

    public static final int[] FOLLOW_ANIM_VOICE_LIST={
            R.mipmap.icon_skill_voice_0,
            R.mipmap.icon_skill_voice_1,
            R.mipmap.icon_skill_voice_2,
    };



   public static Drawable[] createDrawableArray(Context context,int[]resources){
       if(resources==null&&context==null){
           return null;
       }

       Drawable[] drawables = new Drawable[resources.length];
       int length=resources.length;
       for(int i=0;i<length;i++){
           drawables[i]=ContextCompat.getDrawable(context,resources[i]);
       }
       return drawables;
   }


}

