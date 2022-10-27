package com.yunbao.main.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.event.FollowEvent;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.dynamic.bean.DynamicBean;
import com.yunbao.im.activity.ChatRoomActivity;
import com.yunbao.im.activity.SystemMainActivity;
import com.yunbao.im.activity.SystemMessageActivity;
import com.yunbao.im.adapter.ImListAdapter;
import com.yunbao.im.bean.IMLiveBean;
import com.yunbao.im.bean.ImMessageBean;
import com.yunbao.im.bean.ImUserBean;
import com.yunbao.im.bean.SystemMessageBean;
import com.yunbao.im.event.ImRemoveAllMsgEvent;
import com.yunbao.im.event.ImUserMsgEvent;
import com.yunbao.im.event.SystemMsgEvent;
import com.yunbao.im.http.ImHttpConsts;
import com.yunbao.im.http.ImHttpUtil;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.main.R;
import com.yunbao.main.activity.DidiOrderActivity;
import com.yunbao.main.activity.OrderMsgActivity;
import com.yunbao.main.activity.SnatchHallActivity;
import com.yunbao.main.http.MainHttpUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by cxf on 2019/4/1.
 */

public class MainMessageMsgViewHolder extends AbsMainViewHolder implements View.OnClickListener, ImListAdapter.ActionListener {

    private static final String TAG = "MainMessageMsgViewHolde";
    private RecyclerView mRecyclerView;
    private ImListAdapter mAdapter;
    private TextView mOrderRedPoint;//订单消息的红点
    private TextView mDiDiRedPoint;//滴滴消息的红点

    private TextView mHallRedPoint;//抢单大厅消息的红点
    private View mOfficialMsgRedPoint;//官方消息的红点
    private View mBtnOfficialMsg;//
    private TextView mOfficialMsgContent;
    private TextView mOfficialMsgTime;
    private View mBtnSystemMain;
    private TextView mSystemMsgPoint;//系统消息红点


    private HttpCallback mSystemMsgCallback;
    private View mBtnSnatch;
    private View mBtnOrder;
    private View mBtnDidi;
    private View mView1;
    private View mView2;
    private View mView3;

    public MainMessageMsgViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_msg_msg;
    }

    @Override
    public void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ImListAdapter(mContext);
        mAdapter.setActionListener(this);
        mRecyclerView.setAdapter(mAdapter);
        View headView = mAdapter.getHeadView();
        mOrderRedPoint = headView.findViewById(R.id.red_point_order);
        mDiDiRedPoint = headView.findViewById(R.id.red_point_didi);
        mHallRedPoint = headView.findViewById(R.id.red_point_snatch);
        mSystemMsgPoint = (TextView) headView.findViewById(R.id.red_point_main);

        mBtnOfficialMsg = headView.findViewById(R.id.btn_system_msg);
        mBtnSnatch= headView.findViewById(R.id.btn_snatch);
        mBtnOrder= headView.findViewById(R.id.btn_order);
        mBtnDidi= headView.findViewById(R.id.btn_didi_order);
        mView1= headView.findViewById(R.id.view1);
        mView2= headView.findViewById(R.id.view2);
        mView3= headView.findViewById(R.id.view3);
        mBtnOfficialMsg.setOnClickListener(this);
        mBtnOrder.setOnClickListener(this);
        mBtnSnatch.setOnClickListener(this);
        mBtnDidi.setOnClickListener(this);

        mOfficialMsgRedPoint = headView.findViewById(R.id.red_point);
        mOfficialMsgContent = headView.findViewById(R.id.msg_sys);
        mBtnSystemMain = headView.findViewById(R.id.btn_system_main);
        mBtnSystemMain.setVisibility(View.VISIBLE);
        mOfficialMsgTime = headView.findViewById(R.id.time);
        mBtnSystemMain.setOnClickListener(this);
        mSystemMsgCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject jsonObject=JSON.parseObject(info[0]);
                    SystemMessageBean bean = JSON.parseObject(info[0], SystemMessageBean.class);

                    if (mOfficialMsgContent != null) {
                        mOfficialMsgContent.setText(bean.getContent());
                    }
                    if (mOfficialMsgTime != null) {
                        mOfficialMsgTime.setText(bean.getTime());
                    }
                    if (jsonObject.getIntValue("status")==1) {
                        if (mOfficialMsgRedPoint != null && mOfficialMsgRedPoint.getVisibility() != View.VISIBLE) {
                            mOfficialMsgRedPoint.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        };
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
        setSysMainRedPoinitVisible(SpUtil.getInstance().getBooleanValue(SpUtil.LIVE_BRO));

       if(CommonAppConfig.getInstance().getIsState()==1){
         mBtnOrder.setVisibility(View.GONE);
         mBtnSnatch.setVisibility(View.GONE);
         mBtnDidi.setVisibility(View.GONE);
         mView1.setVisibility(View.INVISIBLE);
         mView2.setVisibility(View.INVISIBLE);
         mView3.setVisibility(View.INVISIBLE);
       }

    }

    private void getDripTips() {
        MainHttpUtil.getDripTips().compose(((RxAppCompatActivity)mContext).<JSONObject>bindToLifecycle()).subscribe(new DefaultObserver<JSONObject>() {
            @Override
            public void onNext(JSONObject jsonObject) {
                    int isAuth=jsonObject.getIntValue("isauth");
                    String tip=jsonObject.getString("tips");
                    if(isAuth==1&&CommonAppConfig.getInstance().getIsState()!=1){
                        mBtnSnatch.setVisibility(View.VISIBLE);
                    }else{
                        mBtnSnatch.setVisibility(View.GONE);
                    }

               }
        });
    }
    @Override
    public void loadData() {
        getSystemMessageList();
        getDripTips();
        setSysMainRedPoinitVisible(SpUtil.getInstance().getBooleanValue(SpUtil.LIVE_BRO));
        ImUserBean orderMsg = ImMessageUtil.getInstance().getLastMsgInfo(Constants.IM_MSG_ADMIN);
        if(orderMsg!=null){
            if (mOrderRedPoint != null) {
                if (orderMsg.getUnReadCount() > 0) {
                    if (mOrderRedPoint.getVisibility() != View.VISIBLE) {
                        mOrderRedPoint.setVisibility(View.VISIBLE);
                    }
                    mOrderRedPoint.setText(String.valueOf(orderMsg.getUnReadCount()));
                } else {
                    if (mOrderRedPoint.getVisibility() == View.VISIBLE) {
                        mOrderRedPoint.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
        ImUserBean didiMsg = ImMessageUtil.getInstance().getLastMsgInfo(CommonAppConfig.getInstance().getConfig().getDripAdmin());
        if(didiMsg!=null){
            if (mDiDiRedPoint != null) {
                if (didiMsg.getUnReadCount() > 0) {
                    if (mDiDiRedPoint.getVisibility() != View.VISIBLE) {
                        mDiDiRedPoint.setVisibility(View.VISIBLE);
                    }
                    mDiDiRedPoint.setText(String.valueOf(didiMsg.getUnReadCount()));
                } else {
                    if (mDiDiRedPoint.getVisibility() == View.VISIBLE) {
                        mDiDiRedPoint.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
        ImUserBean hallMsg = ImMessageUtil.getInstance().getLastMsgInfo(CommonAppConfig.getInstance().getConfig().getOrderHallAdmin());
        if(hallMsg!=null){
            if (mHallRedPoint != null) {
                if (hallMsg.getUnReadCount() > 0) {
                    if (mHallRedPoint.getVisibility() != View.VISIBLE) {
                        mHallRedPoint.setVisibility(View.VISIBLE);
                    }
                    mHallRedPoint.setText(String.valueOf(hallMsg.getUnReadCount()));
                } else {
                    if (mHallRedPoint.getVisibility() == View.VISIBLE) {
                        mHallRedPoint.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
        String uids = ImMessageUtil.getInstance().getConversationUids();
        if (!TextUtils.isEmpty(uids)) {
            ImHttpUtil.getImUserInfo(uids, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        List<ImUserBean> list = JSON.parseArray(Arrays.toString(info), ImUserBean.class);
                        list = ImMessageUtil.getInstance().getLastMsgInfoList(list);
                        Collections.sort(list, new Comparator<ImUserBean>() {
                            @Override
                            public int compare(ImUserBean o1, ImUserBean o2) {
                                if (Constants.IM_MSG_ADMIN.equals(o1.getId())) {
                                    return -1;
                                } else if (Constants.IM_MSG_ADMIN.equals(o2.getId())) {
                                    return 1;
                                } else {
                                    return (int) (o2.getLastTimeStamp() - o1.getLastTimeStamp());
                                }
                            }
                        });
                        if (mRecyclerView != null && mAdapter != null) {
                            mAdapter.setList(list);
                        }
                    }
                }
            });
        }
    }

    /**
     * 是否显示系统消息红点
     * @param live_bro
     */
    private void setSysMainRedPoinitVisible(boolean live_bro) {
        String  systemImUid = CommonAppConfig.getInstance().getConfig().getAdmin_dispatch();
        int unRead = ImMessageUtil.getInstance().getUnReadMsgCount(systemImUid);
        ImUserBean systemMsg = ImMessageUtil.getInstance().getLastMsgInfo(systemImUid);
        if(unRead > 0){
            mSystemMsgPoint.setVisibility(View.VISIBLE);
            mSystemMsgPoint.setText(String.valueOf(unRead));
        }else{
            mSystemMsgPoint.setVisibility(View.INVISIBLE);
        }
        ImMessageUtil.getInstance().refreshAllUnReadMsgCount();
    }

    /**
     * 获取系统消息
     */

    private void getSystemMessageList() {
        ImHttpUtil.getSystemStatus(mSystemMsgCallback);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_system_msg) {
            forwardSystemMessage();
        } else if (i == R.id.btn_order) {
            forwardOrderMsg();
        }else if (i == R.id.btn_snatch) {
            forwardSnatch();
        }
        else if (i == R.id.btn_didi_order) {
            forwardidiOrder();
        }else if (i == R.id.btn_system_main) {
            forwarMainSystem();
        }
    }

    /**
     * 系统消息
     */
    private void forwarMainSystem() {
        String admin=CommonAppConfig.getInstance().getConfig().getAdmin_dispatch();
        markRead(admin);
        SpUtil.getInstance().setBooleanValue(SpUtil.LIVE_BRO,false);
        if (mSystemMsgPoint != null && mSystemMsgPoint.getVisibility() == View.VISIBLE){
            mSystemMsgPoint.setVisibility(View.INVISIBLE);
        }
        startActivity(SystemMainActivity.class);
    }

    /*滴滴大厅*/
    private void forwardidiOrder() {
//        ImMessageUtil.getInstance().markAllMessagesAsRead(Constants.IM_MSG_DIDI, true);
        String admin=CommonAppConfig.getInstance().getConfig().getDripAdmin();
        markRead(admin);
        if(mDiDiRedPoint != null && mDiDiRedPoint.getVisibility() == View.VISIBLE) {
            mDiDiRedPoint.setVisibility(View.INVISIBLE);
        }
        startActivity(DidiOrderActivity.class);
    }

    /*抢单大厅*/
    private void forwardSnatch() {
        String admin=CommonAppConfig.getInstance().getConfig().getOrderHallAdmin();
        markRead(admin);
        if(mHallRedPoint != null && mHallRedPoint.getVisibility() == View.VISIBLE) {
            mHallRedPoint.setVisibility(View.INVISIBLE);
        }
        startActivity(SnatchHallActivity.class);
    }

    /**
     * 前往订单消息
     */
    private void forwardOrderMsg() {
        ImMessageUtil.getInstance().markAllMessagesAsRead(Constants.IM_MSG_ADMIN, true);
        if(mOrderRedPoint != null && mOrderRedPoint.getVisibility() == View.VISIBLE) {
           mOrderRedPoint.setVisibility(View.INVISIBLE);
        }
           OrderMsgActivity.forward(mContext);
    }


    /**
     * 忽略消息
     */
    public void ignoreUnReadCount(){
        ImMessageUtil.getInstance().markAllMessagesAsRead(Constants.IM_MSG_ADMIN, true);
        if(mOrderRedPoint != null && mOrderRedPoint.getVisibility() == View.VISIBLE) {
            mOrderRedPoint.setVisibility(View.INVISIBLE);
        }
        if(mDiDiRedPoint != null && mDiDiRedPoint.getVisibility() == View.VISIBLE) {
            mDiDiRedPoint.setVisibility(View.INVISIBLE);
        }
        //官方通告
        SpUtil.getInstance().setBooleanValue(SpUtil.HAS_SYSTEM_MSG, false);
        if (mOfficialMsgRedPoint != null && mOfficialMsgRedPoint.getVisibility() == View.VISIBLE) {
            mOfficialMsgRedPoint.setVisibility(View.INVISIBLE);
        }
        //滴滴
        String admin=CommonAppConfig.getInstance().getConfig().getDripAdmin();
        markRead(admin);
        //抢单大厅
        String adminHall=CommonAppConfig.getInstance().getConfig().getOrderHallAdmin();
        markRead(adminHall);
        if(mHallRedPoint != null && mHallRedPoint.getVisibility() == View.VISIBLE) {
            mHallRedPoint.setVisibility(View.INVISIBLE);
        }
        //系统消息
        String adminSys=CommonAppConfig.getInstance().getConfig().getAdmin_dispatch();
        markRead(adminSys);
        SpUtil.getInstance().setBooleanValue(SpUtil.LIVE_BRO,false);
       // setSysMainRedPoinitVisible(false);
        if (mSystemMsgPoint != null && mSystemMsgPoint.getVisibility() == View.VISIBLE){
            mSystemMsgPoint.setVisibility(View.INVISIBLE);
        }
        if (mAdapter != null) {
            mAdapter.resetAllUnReadCount();
        }
    }

    /**
     * 前往官方消息
     */

    private void forwardSystemMessage() {
        SpUtil.getInstance().setBooleanValue(SpUtil.HAS_SYSTEM_MSG, false);
        if (mOfficialMsgRedPoint != null && mOfficialMsgRedPoint.getVisibility() == View.VISIBLE) {
            mOfficialMsgRedPoint.setVisibility(View.INVISIBLE);
        }
        SystemMessageActivity.forward(mContext);
    }

    private void markRead(String uid){
        if(TextUtils.isEmpty(uid)){
            return;
        }
        ImMessageUtil.getInstance().markAllMessagesAsRead(uid,true);
    }

    @Override
    public void onItemClick(ImUserBean bean) {
        if (bean != null) {
            ImMessageUtil.getInstance().markAllMessagesAsRead(bean.getId(), true);
            //跳转聊天页面
            ChatRoomActivity.forward(mContext, bean, bean.getIsFollow()==1, bean.getIsblack()==1, true, false);
        }
    }


    @Override
    public void onItemDelete(ImUserBean bean, int size) {
        ImMessageUtil.getInstance().removeConversation(bean.getId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (e != null) {
            if (mAdapter != null) {
                mAdapter.setFollow(e.getToUid(), e.getAttention());
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIMLiveEvent(IMLiveBean e) {
       if(e!=null){
           SpUtil.getInstance().setBooleanValue(SpUtil.LIVE_BRO,true);
           setSysMainRedPoinitVisible(true);
       }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSystemMsgEvent(SystemMsgEvent e) {
        getSystemMessageList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUserMsgEvent(final ImUserMsgEvent e) {
        if (e == null) {
            return;
        }
        L.e(TAG,"onImUserMsgEvent--->"+e.getUnReadCount()+"---uid--->"+e.getUid());
        if (e.getType() == ImMessageBean.TYPE_ORDER) {
            //订单消息
            if (mOrderRedPoint != null) {
                if (mOrderRedPoint.getVisibility() != View.VISIBLE) {
                    mOrderRedPoint.setVisibility(View.VISIBLE);
                }
                int unReadCount=e.getUnReadCount();
                if(unReadCount<=0){
                   mOrderRedPoint.setText("1");
                }else{
                   mOrderRedPoint.setText(String.valueOf(e.getUnReadCount()));
                }
            }
            return;
        }else if (e.getType() == ImMessageBean.TYPE_DIDI){
            //滴滴消息
            if (mDiDiRedPoint != null) {
                if (mDiDiRedPoint.getVisibility() != View.VISIBLE) {
                    mDiDiRedPoint.setVisibility(View.VISIBLE);
                }
                int unReadCount=e.getUnReadCount();
                if(unReadCount<=0){
                    mDiDiRedPoint.setText("1");
                }else{
                    mDiDiRedPoint.setText(String.valueOf(e.getUnReadCount()));
                }
            }
            return;
        }else if (e.getType() == ImMessageBean.TYPE_ORDER_HALL){
            //抢单大厅消息
            if (mHallRedPoint != null) {
                if (mHallRedPoint.getVisibility() != View.VISIBLE) {
                    mHallRedPoint.setVisibility(View.VISIBLE);
                }
                int unReadCount=e.getUnReadCount();
                if(unReadCount<=0){
                    mHallRedPoint.setText("1");
                }else{
                    mHallRedPoint.setText(String.valueOf(e.getUnReadCount()));
                }
            }
            return;
        }else if (mRecyclerView != null && mAdapter != null) {
            int position = mAdapter.getPosition(e.getUid());
            if (position < 0) {
                ImHttpUtil.getImUserInfo(e.getUid(), new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            ImUserBean bean = JSON.parseObject(info[0], ImUserBean.class);
                            bean.setLastMessage(e.getLastMessage());
                            bean.setUnReadCount(e.getUnReadCount());
                            bean.setLastTime(e.getLastTime());
                            mAdapter.insertItem(bean);
                        }
                    }
                });
            } else {
                mAdapter.updateItem(e.getLastMessage(), e.getLastTime(), e.getUnReadCount(), position);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImRemoveAllMsgEvent(ImRemoveAllMsgEvent e) {
        if (mAdapter != null) {
            mAdapter.removeItem(e.getToUid());
        }
    }

    @Override
    public void onDestroy() {
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        ImHttpUtil.cancel(ImHttpConsts.GET_SYSTEM_MESSAGE_LIST);
        ImHttpUtil.cancel(ImHttpConsts.GET_IM_USER_INFO);
        super.onDestroy();
    }
}
