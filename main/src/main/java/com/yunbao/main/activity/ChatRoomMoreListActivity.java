package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.event.ShowOrHideLiveRoomFloatWindowEvent;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.server.observer.LockClickObserver;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.config.CallConfig;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.ChatRoomMoreListAdapter;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

/**
 *聊天室 更多列表
 * Created by Sky.L on 2021-07-13
 */
public class ChatRoomMoreListActivity extends AbsActivity implements OnItemClickListener<LiveBean> {
    private CommonRefreshView mRefreshView;
    private ChatRoomMoreListAdapter mAdapter;

    public static void forward(Context context){
        Intent intent = new Intent(context,ChatRoomMoreListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_room_more_list;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.chat_room));
        mRefreshView = findViewById(R.id.refreshView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,2,GridLayoutManager.VERTICAL,false);
        mRefreshView.setLayoutManager(gridLayoutManager);
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<LiveBean>() {

            @Override
            public RefreshAdapter<LiveBean> getAdapter() {
                if (mAdapter == null){
                    mAdapter = new ChatRoomMoreListAdapter(mContext);
                    mAdapter.setOnItemClickListener(ChatRoomMoreListActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getRecomLists(p,callback);
            }

            @Override
            public List<LiveBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info),LiveBean.class);
            }

            @Override
            public void onRefreshSuccess(List<LiveBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<LiveBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mRefreshView.initData();

    }


    @Override
    public void onItemClick(LiveBean bean, int position) {
        if (CallConfig.isBusy()) {
            ToastUtil.show(WordUtil.getString(com.yunbao.chatroom.R.string.tip_please_close_chat_window));
            return;
        }
        if (CommonAppConfig.getInstance().isFloatButtonShowing()) {
            //之前是提示，不进入,修改为 直接进入聊天室，并关闭小窗口
            EventBus.getDefault().post(new ShowOrHideLiveRoomFloatWindowEvent(0));
        }
        enterRoom(bean,null);
    }

    private void enterRoom(final LiveBean liveBean, View view) {
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


}
