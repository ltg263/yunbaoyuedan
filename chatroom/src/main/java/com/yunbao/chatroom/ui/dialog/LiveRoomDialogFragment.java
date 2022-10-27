package com.yunbao.chatroom.ui.dialog;

import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.chatroom.R;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.ui.view.LiveOnLineViewHolder;
import com.yunbao.chatroom.ui.view.LiveRoomDetailViewHolder;

/*房间管理*/
public class LiveRoomDialogFragment extends AbsViewPagerDialogFragment {
    private boolean isHost;
    private LiveBean mLiveBean;
    @Override
    protected AbsMainViewHolder[] createViewHolder() {
        return new AbsMainViewHolder[]{
          new LiveRoomDetailViewHolder(mContext,mViewPager,isHost,mLiveBean),
          new LiveOnLineViewHolder(mContext,mViewPager)
        };
    }

    @Override
    public String[] getTitles() {
        return new String[]{WordUtil.getString(R.string.room_detail),
        WordUtil.getString(R.string.online_detail)
        };
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public void setLiveBean(LiveBean liveBean) {
        mLiveBean = liveBean;
    }
}
