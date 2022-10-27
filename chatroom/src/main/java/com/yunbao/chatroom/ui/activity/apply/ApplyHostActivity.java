package com.yunbao.chatroom.ui.activity.apply;

import android.os.Bundle;
import android.view.ViewGroup;

import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.ApplyHostInfo;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.chatroom.ui.view.apply.AbsApplyHostViewHolder;
import com.yunbao.chatroom.ui.view.apply.ApplyHostResultViewHolder;
import com.yunbao.chatroom.ui.view.apply.ApplyHostViewHolder;
import com.yunbao.chatroom.ui.view.apply.NoApplyHostViewHolder;

/*申请当主持人哦*/
public class ApplyHostActivity extends AbsActivity implements AbsApplyHostViewHolder.OnChangeStateListner {
    private ViewGroup mVpContainer;

    private ApplyHostResultViewHolder mApplyHostResultViewHolder;
    private ApplyHostViewHolder mApplyHostViewHolder;
    private NoApplyHostViewHolder mNoApplyHostViewHolder;

    private int mState = AbsApplyHostViewHolder.NOAPPLY;
    private ApplyHostInfo mApplyHostInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_apply_host;
    }

    @Override
    protected void main() {
        super.main();
        mVpContainer = (ViewGroup) findViewById(R.id.vp_container);
        getLiveApplyInfo();
        //changeState();
    }

    protected void getLiveApplyInfo() {
        ChatRoomHttpUtil.getLiveApplyInfo().compose(this.<ApplyHostInfo>bindToLifecycle()).subscribe(new DefaultObserver<ApplyHostInfo>() {
            @Override
            public void onNext(ApplyHostInfo applyHostInfo) {
                mState = applyHostInfo.getStatus();
                mApplyHostInfo = applyHostInfo;
                changeState();
            }
        });
    }

    private void changeState() {
        switch (mState) {
            case AbsApplyHostViewHolder.NOAPPLY:
                addNoApplyViewHolder();
                setTitle(WordUtil.getString(R.string.apply_hosting_qualification));
                break;
            case AbsApplyHostViewHolder.APPLYING:
                addApplyViewHolder();
                setTitle(WordUtil.getString(R.string.apply_hosting_qualification));
                break;
            default:
                setTitle(WordUtil.getString(R.string.apply_hosting_qualification));
                addApplyReultViewHolder();
                break;
        }
    }

    /*添加未申请的*/
    private void addNoApplyViewHolder() {
        if (mNoApplyHostViewHolder == null) {
            mNoApplyHostViewHolder = new NoApplyHostViewHolder(this, mVpContainer);
            mNoApplyHostViewHolder.setOnChangeStateListner(this);
        }
        mNoApplyHostViewHolder.addToParent();
    }

    /*添加申请结果*/
    private void addApplyReultViewHolder() {
        if (mApplyHostInfo == null) {
            return;
        }
        if (mApplyHostResultViewHolder == null) {
            mApplyHostResultViewHolder = new ApplyHostResultViewHolder(this, mVpContainer, mApplyHostInfo);
            mApplyHostResultViewHolder.setOnChangeStateListner(this);
        } else {
            mApplyHostResultViewHolder.changeInfo(mApplyHostInfo);
        }
        mApplyHostResultViewHolder.addToParent();
    }

    /*添加申请*/
    private void addApplyViewHolder() {
        if (mApplyHostViewHolder == null) {
            String noticeString = mApplyHostInfo == null ? "" : mApplyHostInfo.getTips();
            mApplyHostViewHolder = new ApplyHostViewHolder(this, mVpContainer, noticeString);
            mApplyHostViewHolder.subscribeActivityLifeCycle();
            mApplyHostViewHolder.setOnChangeStateListner(this);
        }
        mApplyHostViewHolder.addToParent();
    }

    @Override
    public void change(int state) {
        if (mState == state) {
            return;
        }
        mState = state;
        changeState();
    }

    @Override
    public void refresh() {
        getLiveApplyInfo();
    }
}
