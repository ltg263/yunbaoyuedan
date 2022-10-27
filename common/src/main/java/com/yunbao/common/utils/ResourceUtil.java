package com.yunbao.common.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.ArrayMap;
import com.yunbao.common.CommonAppContext;

public class ResourceUtil {
    private static ArrayMap<Integer, Drawable>drawableArrayMap;
    static {
        drawableArrayMap=new ArrayMap<>();
    }

    public static ColorStateList getColorList(Context context,int color) {
        Resources resources = context.getResources();
        return resources.getColorStateList(color);
    }

    public static int getColor(int color) {
        Resources resources = CommonAppContext.sInstance.getResources();
        return resources.getColor(color);
    }

    /*Drawable的*/
    public static Drawable getDrawable(int resourceId,boolean shouldUserCache){
        Drawable drawable=null;
        if(resourceId==0){
            return drawable;
        }
        if(!shouldUserCache){
            drawable= ContextCompat.getDrawable(CommonAppContext.sInstance, resourceId);
        }
         else{
            drawable=drawableArrayMap.get(resourceId);
            if(drawable==null){
            drawable= ContextCompat.getDrawable(CommonAppContext.sInstance, resourceId);
            drawableArrayMap.put(resourceId,drawable);
            }
        }
         return drawable;
    }


    public static void clearDrawable(int...drawaleIdArray){
        if(drawaleIdArray==null||drawaleIdArray.length==0){
            return;
        }
        for(int drawaleId:drawaleIdArray){
            drawableArrayMap.remove(drawaleId);
        }
    }
}
