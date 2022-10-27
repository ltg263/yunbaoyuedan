package com.yunbao.chatroom.ui.view.bottom;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.chatroom.ui.activity.LiveActivity;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.views.AbsViewHolder2;
import com.yunbao.im.bean.ImUserBean;
import com.yunbao.im.event.ImUnReadCountEvent;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.behavior.WatchApplyBehavior;
import com.yunbao.chatroom.business.behavior.factory.CacheBehaviorFactory;
import com.yunbao.chatroom.ui.dialog.LiveChatListDialogFragment;
import com.yunbao.chatroom.ui.dialog.LiveGiftDialogFragment;
import com.yunbao.chatroom.ui.dialog.LiveInputDialogFragment;
import com.yunbao.chatroom.ui.dialog.LiveShareDialogFragment;
import com.yunbao.chatroom.ui.dialog.UpWheatApplyDialogFragment;
import com.yunbao.chatroom.ui.dialog.WheatManangerDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LiveHostBottomViewHolder extends AbsViewHolder2 implements View.OnClickListener, WatchApplyBehavior.WatchApplyListner, LiveGiftDialogFragment.ActionListener {
    private View mVRedPoint;
    private View mBtnChat;
    private AbsDialogFragment mUpWheatApplyDialogFragment;
    private TextView mRedPoint;

    public LiveHostBottomViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_bottom_host;
    }

    @Override
    public void init() {
        mVRedPoint = (View) findViewById(R.id.v_red_point);
        mBtnChat =  findViewById(R.id.btn_chat);
        mBtnChat.setOnClickListener(this);
        mRedPoint = (TextView) findViewById(R.id.red_point);
        setOnClickListner(R.id.btn_wheat_control,this);
        setOnClickListner(R.id.btn_mike_apply,this);
        setOnClickListner(R.id.btn_share,this);
        setOnClickListner(R.id.btn_gift,this);
        setOnClickListner(R.id.btn_msg,this);
        WatchApplyBehavior watchApplyBehavior= CacheBehaviorFactory.getInstance().getWatchApplyBehavior(mFragmentActivity);
        watchApplyBehavior.watch(this);
        setUnReadCount(ImMessageUtil.getInstance().getLiveAllUnReadMsgCount());
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_wheat_control){
            controlWheat();
        }else if(id==R.id.btn_mike_apply){
            openMikeApplyDialog();
            mVRedPoint.setVisibility(View.INVISIBLE);
        }else if(id==R.id.btn_chat){
            openInputDialog();
        }else if(id==R.id.btn_share){
            openShareDialog();
        }
        else if(id==R.id.btn_gift){
            openGiftDialog();
        }else if (id == R.id.btn_msg){
            openChatListWindow();
        }
    }

    private void openShareDialog() {
        LiveShareDialogFragment dialogFragment=new LiveShareDialogFragment();
        dialogFragment.show(mFragmentActivity.getSupportFragmentManager());
    }


    /*打开赠送礼物窗口*/
    private void openGiftDialog() {
        LiveGiftDialogFragment chatGiftDialogFragment=new LiveGiftDialogFragment();
        chatGiftDialogFragment.setActionListener(this);
        chatGiftDialogFragment.show(mFragmentActivity.getSupportFragmentManager());
    }

    /*打开输入框*/
    private void openInputDialog() {
        LiveInputDialogFragment liveInputDialogFragment=new LiveInputDialogFragment();
        liveInputDialogFragment.show(mFragmentActivity.getSupportFragmentManager());
    }


    /*打开申请框*/
    protected void openMikeApplyDialog() {
        if(mFragmentActivity!=null&&(mUpWheatApplyDialogFragment==null||!mUpWheatApplyDialogFragment.isAdded())){
            mUpWheatApplyDialogFragment=getApplyDialog();
            mUpWheatApplyDialogFragment.show(mFragmentActivity.getSupportFragmentManager());
        }
    }

    protected AbsDialogFragment getApplyDialog() {
    return new UpWheatApplyDialogFragment();
    }

    /*打开控麦的*/
    private void controlWheat() {
        if(mFragmentActivity!=null){
           WheatManangerDialogFragment upperManangerDialogFragment=new WheatManangerDialogFragment();
           upperManangerDialogFragment.show(mFragmentActivity.getSupportFragmentManager());

        }
    }

    /*监听是否有上麦申请的行为*/
    @Override
    public void watch(boolean isUp) {
        if(mUpWheatApplyDialogFragment!=null&&mUpWheatApplyDialogFragment.isAdded()){
            mUpWheatApplyDialogFragment.setDissMissLisnter(new AbsDialogFragment.DissMissLisnter() {
                @Override
                public void close() {
                    mUpWheatApplyDialogFragment=null;
                }
            });
            mUpWheatApplyDialogFragment.initData();
            return;
        }
        if(isUp){
            mVRedPoint.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onChargeClick() {
        RouteUtil.forwardMyCoin();
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
