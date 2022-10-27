package com.yunbao.im.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.business.acmannger.ActivityMannger;
import com.yunbao.common.business.acmannger.ReleaseListner;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.im.R;
import com.yunbao.im.business.CallIMHelper;
import com.yunbao.im.business.ICallPresnter;
import com.yunbao.im.business.VideoCallPresneter;
import com.yunbao.im.config.CallConfig;
import com.yunbao.im.event.AcceptCallEvent;
import com.yunbao.im.event.CallBusyEvent;
import com.yunbao.im.event.CancleCallEvent;
import com.yunbao.im.event.RefuseCallEvent;
import com.yunbao.im.event.VideoAllCloseEvent;
import com.yunbao.im.views.call.AbsCallViewHolder;
import com.yunbao.im.views.call.AudioCallViewHolder;
import com.yunbao.im.views.call.VideoCallViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.yunbao.common.Constants.CALL_TYPE;
import static com.yunbao.common.Constants.DATA;
import static com.yunbao.common.Constants.ROLE;
import static com.yunbao.common.Constants.ROOM_ID;

@Route(path = RouteUtil.PATH_CALL_ACTIVITY)
public class MediaCallActivity extends AbsActivity implements ReleaseListner {

    protected FrameLayout container;
    protected ICallPresnter callPresnter;
    protected AbsCallViewHolder callViewHolder;
    private UserBean userBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        L.e("onSaveInstanceState Bundle==" + outState.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        L.e("onRestoreInstanceState Bundle==" + savedInstanceState.toString());
    }

    @Override
    public void addToActivityMannger() {
        ActivityMannger.getInstance().addActivityByNewStack(this);
    }

    @Override
    public void removToAcitivyMannger() {
        /*不实现他*/
    }

    private static final String TAG = "MediaCallActivity";

    @Override
    protected void onNewIntent(Intent intent) {
        L.e(TAG, "onNewIntent----");
        super.onNewIntent(intent);
        addToActivityMannger();
        if (callViewHolder != null) {
            callViewHolder.restoreVideoFromWindowFlat();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        L.e(TAG, "onStart----");
        if (callViewHolder != null) {
            callViewHolder.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (callViewHolder != null) {
            callViewHolder.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (callViewHolder != null) {
            callViewHolder.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (callViewHolder != null) {
            callViewHolder.onStop();
        }
    }


    @Override
    protected void main() {
        super.main();
        L.e(TAG, "main Bundle==");
        ActivityMannger.getInstance().addActivityByNewStack(this);
        container = findViewById(R.id.container);
        Intent intent = getIntent();
        int role = intent.getIntExtra(ROLE, 0);
        int roomId = intent.getIntExtra(ROOM_ID, 0);
        int callType = intent.getIntExtra(CALL_TYPE, 0);
        userBean = intent.getParcelableExtra(DATA);
        if (roomId == 0) {
            finish();
        }
        if (role == Constants.ROLE_ANTHOR) {
            CallIMHelper.sendStart(callType, userBean.getId());
        }
        if (callType == Constants.CHAT_TYPE_VIDEO) {
            callViewHolder = new VideoCallViewHolder(this, container, roomId, userBean);
            callViewHolder.addToParent();

        } else {
            callViewHolder = new AudioCallViewHolder(this, container, roomId, userBean);
            callViewHolder.addToParent();
        }
        callViewHolder.setReleaseListner(this);
        callPresnter = new VideoCallPresneter(callViewHolder, role);
        if (callPresnter instanceof VideoCallPresneter) {
            ((VideoCallPresneter) callPresnter).setRoomInfo(roomId, callType);
        }
        callPresnter.isVideo(callType == Constants.CHAT_TYPE_VIDEO);
        callViewHolder.setCallPresnter(callPresnter);
        int state = role == Constants.ROLE_ANTHOR ? CallConfig.CALL : CallConfig.WAIT;
        /*弹出等待界面*/
        callViewHolder.showWaitViewHolder(state);
        if (!EventBus.getDefault().isRegistered(this)) {
            if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
        }
    }

    /*必须留这,请勿删除*/
    @Override
    public void setIsBackGround() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_media_call;
    }

    public static void forward(Context context, int role, String roomId, int callType, UserBean userBean) {
        Intent intent = new Intent(context, MediaCallActivity.class);
        intent.putExtra(ROLE, role);
        intent.putExtra(ROOM_ID, roomId);
        intent.putExtra(CALL_TYPE, callType);
        intent.putExtra(DATA, userBean);
        context.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void registerAcceptCallEvent(AcceptCallEvent event) {
        callViewHolder.accept();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void registerCancleCallEvent(CancleCallEvent event) {
        callViewHolder.cancle();
        L.e("接收到 CancleCallEvent()");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void registerRefuseCallEvent(RefuseCallEvent event) {
        callViewHolder.refuse();
    }

    /*两方视频窗口都停止推流的时候会回调到这里*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void registerCloseEvent(VideoAllCloseEvent event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void registerCallBusyEvent(CallBusyEvent event) {
        callViewHolder.cancle();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void release() {
        L.e(TAG, "release----");
        ActivityMannger.getInstance().removeActivity(this);
//        TimeModel.getInstance().clear();
        finish();
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        callPresnter = null;
        callViewHolder = null;
        userBean = null;

    }
}
