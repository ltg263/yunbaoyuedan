package com.yunbao.chatroom.business;

import android.graphics.drawable.Drawable;

import com.yunbao.common.Constants;
import com.yunbao.common.utils.ResourceUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;

public class LiveType {
 public static final int LIVE_ROOM_TYPE_FRIEND= Constants.LIVE_TYPE_FRIEND;
 public static final int LIVE_ROOM_TYPE_DISPATCH=Constants.LIVE_TYPE_DISPATCH;
 public static final int LIVE_ROOM_TYPE_CHAT=Constants.LIVE_TYPE_CHAT;
    public static final int LIVE_ROOM_TYPE_SONG=Constants.LIVE_TYPE_SONG;

 public static String getTagTitle(int type){
     if(type==LIVE_ROOM_TYPE_FRIEND){
         return WordUtil.getString(R.string.make_friends);
     }else if(type==LIVE_ROOM_TYPE_CHAT){
         return WordUtil.getString(R.string.chatting) ;
     }else if(type==LIVE_ROOM_TYPE_SONG){
         return WordUtil.getString(R.string.choose_song);
     }
     else if(type==LIVE_ROOM_TYPE_DISPATCH){
         return WordUtil.getString(R.string.dispatch);
     }
     return null;
 }

    public static Drawable getTagBgDrawable(int type){
        if(type==LIVE_ROOM_TYPE_FRIEND){
            return ResourceUtil.getDrawable(R.mipmap.icon_type_friend,true);
        }else if(type==LIVE_ROOM_TYPE_CHAT){
            return ResourceUtil.getDrawable(R.mipmap.icon_type_echat,true);
        }else if(type==LIVE_ROOM_TYPE_SONG){
            return ResourceUtil.getDrawable(R.mipmap.icon_type_song,true);
        }
        return null;
    }

}
