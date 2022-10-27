package com.yunbao.chatroom.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.event.ShowOrHideLiveRoomFloatWindowEvent;
import com.yunbao.common.server.observer.LockClickObserver;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainHomeChildViewHolder;
import com.yunbao.im.config.CallConfig;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.adapter.LiveAdapter;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import io.reactivex.Observable;

/*聊天室列表UI*/
public abstract class LiveTabulationViewHolder extends AbsMainHomeChildViewHolder {
    private RxRefreshView<LiveBean> mRefreshView;
    private LiveAdapter mLiveAdapter;
    public LiveTabulationViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_single_refresh;
    }

    @Override
    public void init() {
        mRefreshView = (RxRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        mRefreshView.setNoDataTip(R.string.no_open_room_tip_1);

        mRefreshView.setDataListner(new RxRefreshView.DataListner<LiveBean>() {
            @Override
            public Observable<List<LiveBean>> loadData(int p) {
                if(mActionListener!=null){
                    mActionListener.onRefreshCompleted();
                }
                return getData(p);
            }

            @Override
            public void compelete(List data) {

            }
            @Override
            public void error(Throwable e) {
                if(mActionListener!=null){
                    mActionListener.onRefreshCompleted();
                }
                e.printStackTrace();
            }
        });
        mLiveAdapter=new LiveAdapter(null);
        mLiveAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(CallConfig.isBusy()){
                   ToastUtil.show(WordUtil.getString(R.string.tip_please_close_chat_window));
                   return;
                }
                if (CommonAppConfig.getInstance().isFloatButtonShowing()){
//                    ToastUtil.show(WordUtil.getString(R.string.tip_please_close_chat_window));
//                    return;
                    //之前是提示，不进入,修改为 直接进入聊天室，并关闭小窗口
                    EventBus.getDefault().post(new ShowOrHideLiveRoomFloatWindowEvent(0));
                }
                enterRoom(mLiveAdapter.getItem(position),view);
            }
        });
        mRefreshView.setAdapter(mLiveAdapter);
    }

    private void enterRoom(final LiveBean liveBean,View view) {
        ChatRoomHttpUtil.enterRoom(liveBean.getUid(),liveBean.getStream()).subscribe(new LockClickObserver<LiveBean>(view) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
            @Override
            public void onSucc(LiveBean tempLiveBean) {
                liveBean.setVotestotal(tempLiveBean.getVotestotal());
                liveBean.setChatserver(tempLiveBean.getChatserver());
                liveBean.setIsattent(tempLiveBean.getIsattent());
                liveBean.setSits(tempLiveBean.getSits());
                liveBean.setSkillid(tempLiveBean.getSkillid());
                liveBean.setIsdispatch(tempLiveBean.getIsdispatch());
                liveBean.setInviteCode(tempLiveBean.getInviteCode());
                liveBean.setExpandParm(tempLiveBean.getExpandParm());
                liveBean.setRoomCover(tempLiveBean.getRoomCover());
                RouteUtil.forwardLiveAudience(liveBean,liveBean.getType(),false);
            }
        });
    }
    @Override
    public void loadData() {
        super.loadData();
        mRefreshView.initData();
    }

    public abstract Observable<List<LiveBean>> getData(int p);
}
