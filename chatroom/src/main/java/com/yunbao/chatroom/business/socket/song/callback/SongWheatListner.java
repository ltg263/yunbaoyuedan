package com.yunbao.chatroom.business.socket.song.callback;

import com.yunbao.common.bean.UserBean;
import com.yunbao.chatroom.business.socket.dispatch.callback.WheatLisnter;

public interface SongWheatListner extends WheatLisnter {
    /*申请上歌手麦*/
    public void applySinger(UserBean userBean,int sitId);
    /*处理歌手麦结果*/
    public void applySingerResult(UserBean userBean,int sitId,boolean isArgee);
}
