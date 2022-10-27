package com.yunbao.common.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;


public class ImageUtil {
    public static Drawable[] getDrawalesByResource(Context context,int...resourceArray){
        if(resourceArray==null) {
            return null;
        }
        int length=resourceArray.length;
        Drawable[] drawables=new Drawable[resourceArray.length];
        for(int i=0;i<length;i++){
            drawables[i]= ContextCompat.getDrawable(context,resourceArray[i]);
        }
        return drawables;
    }
}
