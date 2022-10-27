package com.yunbao.chatroom.ui.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.yunbao.common.views.AbsViewHolder2;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.LiveEndResultBean;

public class LiveEndViewHolder extends AbsViewHolder2 implements View.OnClickListener {
    private TextView mTvTitle;
    private TextView mTvDuration;
    private TextView mTvTotalNum;
    private TextView mBtnConfirm;




    public LiveEndViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_live_end_chat;
    }

    @Override
    public void init() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvDuration = (TextView) findViewById(R.id.tv_duration);
        mTvTotalNum = (TextView) findViewById(R.id.tv_total_num);
        mBtnConfirm = (TextView) findViewById(R.id.btn_confirm);

        setOnClickListner(R.id.btn_confirm,this);
        setOnClickListner(R.id.vp_container,this);
    }


    public void pushData(LiveEndResultBean liveEndResultBean){
        if(liveEndResultBean!=null){
            mTvTitle.setText(liveEndResultBean.getTitle());
            mTvDuration.setText(liveEndResultBean.getLength());
            mTvTotalNum.setText(liveEndResultBean.getNums());
        }
    }



    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id=v.getId();
    }

    public void setOnEndButtonClickListener(View.OnClickListener onClickListener) {
        if(mBtnConfirm!=null){
           mBtnConfirm.setOnClickListener(onClickListener);
        }
    }
}
