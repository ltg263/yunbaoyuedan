package com.yunbao.chatroom.ui.view.apply;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.ApplyHostInfo;

public class ApplyHostResultViewHolder extends AbsApplyHostViewHolder implements View.OnClickListener {
    private int mState;
    private TextView mTvTipTop;
    private TextView mTvTipBottom;
    private TextView mBtnReply;

    private String mBottomTipString;


    public ApplyHostResultViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
        ApplyHostInfo  applyHostInfo= (ApplyHostInfo) args[0];
        if(applyHostInfo!=null){
            mState= applyHostInfo.getStatus();
            mBottomTipString= applyHostInfo.getReason();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_apply_host_result;
    }

    @Override
    public void init() {
        mTvTipTop = (TextView) findViewById(R.id.tv_tipTop);
        mTvTipBottom = (TextView) findViewById(R.id.tv_tipBottom);
        mBtnReply = (TextView) findViewById(R.id.btn_reply);
        setStateUI();
        setOnClickListner(R.id.btn_reply,this);
    }

    public void changeInfo(ApplyHostInfo info){
        if(mState==info.getStatus()){
            return;
        }
        mState=info.getStatus();
        mBottomTipString=info.getReason();
        setStateUI();
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void setStateUI() {
        if(mState== REVIEW_ERROR){
            mTvTipTop.setTextColor(mContext.getColorStateList(R.color.red3));
            mTvTipTop.setText(R.string.information_audit_tip2);
            mBtnReply.setVisibility(View.VISIBLE);
        }else if(mState==REVIEW_ING){
            mTvTipTop.setText(R.string.information_audit_tip1);
            mTvTipTop.setTextColor(mContext.getColorStateList(R.color.textColor));
            mBtnReply.setVisibility(View.INVISIBLE);
            mBottomTipString= WordUtil.getString(R.string.information_audit_tip3);
        }
            mTvTipBottom.setText(mBottomTipString);
    }
    @Override
    public void onClick(View v) {
        notifyState(APPLYING);
        removeFromParent();
    }
}
