package com.yunbao.chatroom.ui.dialog;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.ListBean;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.chatroom.ui.view.LiveBillboardViewHolder;
import java.util.List;
import io.reactivex.Observable;

public class LiveBillboardDialogFragment extends AbsViewPagerDialogFragment {
    private String mLiveUid;
    private boolean mIsLive;
    private String mStream;
    @Override
    protected AbsMainViewHolder[] createViewHolder() {
        String coinName= CommonAppConfig.getInstance().getCoinName();
        return new AbsMainViewHolder[]{
                new LiveBillboardViewHolder(mContext, mViewPager,coinName) {
                    @Override
                    public Observable<List<ListBean>> getData(int p) {
                        if(mIsLive){
                            return ChatRoomHttpUtil.getLiveConsumeList(mStream,p);
                        }else{
                            return ChatRoomHttpUtil.getLiveContri(mLiveUid,p);
                        }
                    }
                },
                new LiveBillboardViewHolder(mContext, mViewPager,WordUtil.getString(R.string.charm_value)) {
                    @Override
                    public Observable<List<ListBean>> getData(int p) {
                        if(mIsLive){
                            return ChatRoomHttpUtil.getLiveProfitList(mLiveUid,p);
                        }else{
                            return ChatRoomHttpUtil.getCharm(p);
                        }
                    }
                }
        };
    }


    @Override
    public String[] getTitles() {
        return new String[]{WordUtil.getString(R.string.contribution_list),
                WordUtil.getString(R.string.charm_list)
        };
    }
    public void setLiveUid(String liveUid) {
        mLiveUid = liveUid;
    }

    public void setStream(String stream) {
        mStream = stream;
    }

    public void setIsLive(boolean isLive){
        mIsLive=isLive;
    }
}
