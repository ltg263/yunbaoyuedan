package com.yunbao.chatroom.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.behavior.factory.CacheBehaviorFactory;
import com.yunbao.chatroom.business.state.Stater;
import com.yunbao.chatroom.ui.activity.LiveActivity;
import com.yunbao.chatroom.ui.dialog.LiveChatListDialogFragment;
import com.yunbao.chatroom.ui.dialog.LiveGiftDialogFragment;
import com.yunbao.chatroom.ui.dialog.LiveInputDialogFragment;
import com.yunbao.chatroom.ui.dialog.LiveShareDialogFragment;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.views.AbsViewHolder2;
import com.yunbao.im.bean.ImUserBean;
import com.yunbao.im.event.ImUnReadCountEvent;
import com.yunbao.im.utils.ImMessageUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LiveAudienceBottomViewHolder extends AbsViewHolder2 implements View.OnClickListener, LiveGiftDialogFragment.ActionListener {
    private TextView mBtnMike1;
    private TextView mBtnMike2;
    private ImageView mBtnGift;
    private TextView mTvSpeakTime;
    private Stater mStater;
    private TextView mRedPoint;

    public LiveAudienceBottomViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_bottom_audience;
    }

    @Override
    public void init() {
        mBtnMike1 = (TextView) findViewById(R.id.btn_mike_1);
        mBtnMike2 = (TextView) findViewById(R.id.btn_mike_2);
        mBtnGift = (ImageView) findViewById(R.id.btn_gift);
        mTvSpeakTime = (TextView) findViewById(R.id.tv_speak_time);
        mStater=new Stater(mBtnMike1,mBtnMike2,mTvSpeakTime);
        mRedPoint = (TextView) findViewById(R.id.red_point);
        mStater.handAction(Stater.TAKE_UI);
        mBtnGift.setOnClickListener(this);
        setOnClickListner(R.id.btn_chat,this);
        setOnClickListner(R.id.btn_share,this);
        setOnClickListner(R.id.btn_msg,this);
        setUnReadCount(ImMessageUtil.getInstance().getLiveAllUnReadMsgCount());
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
        if (CommonAppConfig.getInstance().getIsState()==1){
            mBtnMike1.setVisibility(View.GONE);
            mBtnMike2.setVisibility(View.GONE);
            mBtnGift.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_gift){
           openGiftDialog();
        }else if(id==R.id.btn_chat){
            openInputDialog();
        }else if(id==R.id.btn_share){
            share();
        }else if (id == R.id.btn_msg){
            openChatListWindow();
        }
    }
    private void share() {
        LiveShareDialogFragment shareDialogFragment=new LiveShareDialogFragment();
        shareDialogFragment.show(mFragmentActivity.getSupportFragmentManager());
    }

    private void openInputDialog() {
        LiveInputDialogFragment liveInputDialogFragment=new LiveInputDialogFragment();
        liveInputDialogFragment.show(mFragmentActivity.getSupportFragmentManager());
    }

    public void handAction(int action){
        if(mStater!=null){
           mStater.handAction(action);
        }
        /*上麦成功后将申请状态重置为false*/
        if(action==Stater.UP_WHEAT){
            CacheBehaviorFactory.setApplying(false,getActivity());
        }
    }

    /*打开赠送礼物窗口*/
    private void openGiftDialog() {
        LiveGiftDialogFragment chatGiftDialogFragment=new LiveGiftDialogFragment();
        chatGiftDialogFragment.setActionListener(this);
        chatGiftDialogFragment.show(mFragmentActivity.getSupportFragmentManager());
    }
    @Override
    public void onChargeClick() {
        RouteUtil.forwardMyCoin();
    }

    /*打开充值界面*/
    public void openChargeWindow() {

    }



    /**
     * 打开私信列表窗口
     */
    public void openChatListWindow() {
        LiveChatListDialogFragment fragment = new LiveChatListDialogFragment();
        fragment.setClickCallback(new LiveChatListDialogFragment.ClickCallback() {
            @Override
            public void openChatRoomWindow(ImUserBean bean) {
                ((LiveActivity) mContext).openChatRoomWindow(bean, bean.getIsFollow() == 1);
            }
        });
        fragment.show(mFragmentActivity.getSupportFragmentManager(), "LiveChatListDialogFragment");
    }


    public void setUnReadCount(String unReadCount) {
        if (mRedPoint != null) {
            if ("0".equals(unReadCount)) {
                if (mRedPoint.getVisibility() == View.VISIBLE) {
                    mRedPoint.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mRedPoint.getVisibility() != View.VISIBLE) {
                    mRedPoint.setVisibility(View.VISIBLE);
                }
            }
            mRedPoint.setText(unReadCount);
        }
    }

    /**
     * 监听私信未读消息数变化事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(ImUnReadCountEvent e) {
        String unReadCount = e.getUnReadCount();
        if (e.isLiveChatRoom()){
            if (!TextUtils.isEmpty(unReadCount)) {
                setUnReadCount(unReadCount);
            }
        }
    }

    @Override
    public void onDestroy() {
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }
}
