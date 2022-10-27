package com.yunbao.chatroom.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.common.Constants;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.chatroom.R;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.business.LiveType;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;

public class LiveRoomDetailViewHolder extends AbsMainViewHolder implements View.OnClickListener {
    private static final int NORMAL=0;
    private static final int EDIT_ING=1;
    private RoundedImageView mImgAvator;
    private TextView mTvTitle;
    private EditText mTvContent;
    private TextView mBtnEdit;
    private TextView mTvId;
    private TextView mTvLiveType;


    private LiveBean mLiveBean;
    private boolean canEdit;
    private int mState;
    private KeyListener mKeyListner;

    public LiveRoomDetailViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    @Override
    protected void processArguments(Object... args) {
        super.processArguments(args);
        this.canEdit= (boolean) args[0];
        this.mLiveBean= (LiveBean) args[1];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_room_detail;
    }

    @Override
    public void init() {
        mImgAvator = (RoundedImageView) findViewById(R.id.img_avator);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvContent = (EditText) findViewById(R.id.tv_content);
        mBtnEdit = (TextView) findViewById(R.id.btn_edit);
        mBtnEdit.setOnClickListener(this);
        mTvId = (TextView) findViewById(R.id.tv_id);
        mTvLiveType = (TextView) findViewById(R.id.tv_live_type);

        mKeyListner=mTvContent.getKeyListener();
        if(canEdit){
            mBtnEdit.setVisibility(View.VISIBLE);
        }

        requestLiveInfo();
        setUIData();
        changeState(NORMAL);
    }

    private void requestLiveInfo() {
        ChatRoomHttpUtil.getLiveInfo(mLiveBean.getUid()).subscribe(new DefaultObserver<LiveBean>() {
            @Override
            public void onNext(LiveBean liveBean) {
                String notice=liveBean.getDes();
                mLiveBean.setDes(notice);
                mTvContent.setText(notice);
            }
        });
    }

    // 隐藏键盘
    private InputMethodManager imm;
    private void showKeyBoard(boolean show) {
        if (!show){
            return;
        }
        if (imm == null){
            imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (imm.isActive()) {
            // 如果开启
            imm.showSoftInput(mTvContent,InputMethodManager.SHOW_IMPLICIT);
//            // 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
//            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
//                    InputMethodManager.HIDE_NOT_ALWAYS);

        }
    }

    private void setUIData() {
        if(mLiveBean!=null){
            ImgLoader.display(mContext,mLiveBean.getThumb(),mImgAvator);
            mTvTitle.setText(mLiveBean.getTitle());
            mTvId.setText(mLiveBean.getIdShow());
            mTvContent.setText(mLiveBean.getDes());
            int type=mLiveBean.getType();
            if(type!= Constants.LIVE_TYPE_DISPATCH){
                mTvLiveType.setText(mLiveBean.getTypeName());
            }else{
                mTvLiveType.setText(null);
            }
            mTvLiveType.setBackground(LiveType.getTagBgDrawable(type));
        }
    }
    private void changeState(int state) {
        mState=state;
        if(mState==EDIT_ING){
            mTvContent.setCursorVisible(true);
            mTvContent.setKeyListener(mKeyListner);
            mTvContent.setFocusableInTouchMode(true);
            mTvContent.setFocusable(true);
            mTvContent.requestFocus();
            String text = mTvContent.getText().toString();
            if (!TextUtils.isEmpty(text)){
                mTvContent.setSelection(text.length());
            }
            mBtnEdit.setText(R.string.save);
            showKeyBoard(true);

        }else{
            mTvContent.setCursorVisible(false);
            mTvContent.setFocusableInTouchMode(false);
            mTvContent.setKeyListener(null);
            mBtnEdit.setText(R.string.edit_announcement);
            showKeyBoard(false);
        }
    }


    @Override
    public void onClick(View v) {
        if(mState==EDIT_ING){
            edit();
        }else{
            changeState(EDIT_ING);
        }
    }

    private void edit() {
        changeState(NORMAL);
        ChatRoomHttpUtil.setLiveDes(mTvContent.getText().toString()).subscribe(new DefaultObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if(aBoolean){

                }
            }
        });
    }

}
