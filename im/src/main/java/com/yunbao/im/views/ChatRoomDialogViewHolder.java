package com.yunbao.im.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.adapter.ImChatFacePagerAdapter;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.ChatFaceDialog;
import com.yunbao.common.event.FollowEvent;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.KeyBoardHeightChangeListener;
import com.yunbao.common.interfaces.OnFaceClickListener;
import com.yunbao.common.utils.DateFormatUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsViewHolder2;
import com.yunbao.im.R;
import com.yunbao.im.adapter.ImRoomAdapter;
import com.yunbao.im.bean.ImMessageBean;
import com.yunbao.im.custom.MyImageView;
import com.yunbao.im.dialog.ChatImageDialog;
import com.yunbao.im.dialog.ChatMoreDialog;
import com.yunbao.im.interfaces.ChatRoomActionListener;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.im.utils.ImTextRender;
import com.yunbao.im.utils.MediaRecordUtil;
import com.yunbao.im.utils.VoiceMediaPlayerUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;


/**
 * Created by cxf on 2018/10/24.
 */

public class ChatRoomDialogViewHolder extends AbsViewHolder2 implements
        View.OnClickListener, OnFaceClickListener, ChatFaceDialog.ActionListener,
        ChatMoreDialog.ActionListener, ImRoomAdapter.ActionListener {

    private InputMethodManager imm;
    private RecyclerView mRecyclerView;
    private ImRoomAdapter mAdapter;
    private EditText mEditText;
    private TextView mVoiceRecordEdit;
    private Drawable mVoiceUnPressedDrawable;
    private Drawable mVoicePressedDrawable;
    private TextView mTitleView;
    private UserBean mUserBean;
    private String mToUid;
    private ChatRoomActionListener mActionListener;
    private ImMessageBean mCurMessageBean;
    private long mLastSendTime;//???????????????????????????
    private HttpCallback mCheckBlackCallback;
    private CheckBox mBtnFace;
    private CheckBox mBtnVoice;
    private View mFaceView;//????????????
    private int mFaceViewHeight;//??????????????????
    private ChatFaceDialog mChatFaceDialog;//????????????
    private ChatImageDialog mChatImageDialog;//??????????????????
    private boolean mFollowing;
    private View mFollowGroup;
    private String mPressSayString;
    private String mUnPressStopString;
    private MediaRecordUtil mMediaRecordUtil;
    private File mRecordVoiceFile;//????????????
    private long mRecordVoiceDuration;//????????????
    private Handler mHandler;
    private VoiceMediaPlayerUtil mVoiceMediaPlayerUtil;

    public ChatRoomDialogViewHolder(Context context, ViewGroup parentView, UserBean userBean, boolean following) {
        super(context, parentView, userBean, following);
    }

    @Override
    protected void processArguments(Object... args) {
        mUserBean = (UserBean) args[0];
        mFollowing = (boolean) args[1];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_room_2;
    }

    @Override
    public void init() {
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mTitleView = (TextView) findViewById(R.id.titleView);
        mEditText = (EditText) findViewById(R.id.edit);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendText();
                    return true;
                }
                return false;
            }
        });

        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (CommonAppConfig.getInstance().getIsState()==1){
            mEditText.setHint(configBean.getImTeenagerTip());
        }
        findViewById(R.id.img_title_right).setVisibility(View.INVISIBLE);
        mEditText.setOnClickListener(this);
        mVoiceRecordEdit = (TextView) findViewById(R.id.btn_voice_record_edit);
        if (mVoiceRecordEdit != null) {
            mVoiceUnPressedDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_chat_voice_record_0);
            mVoicePressedDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_chat_voice_record_1);
            mPressSayString = WordUtil.getString(R.string.im_press_say);
            mUnPressStopString = WordUtil.getString(R.string.im_unpress_stop);
            mVoiceRecordEdit.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    switch (e.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startRecordVoice();
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            stopRecordVoice();
                            break;
                    }
                    return true;
                }
            });
        }
        mFollowGroup = findViewById(R.id.btn_follow_group);
        if (!mFollowing) {
            mFollowGroup.setVisibility(View.VISIBLE);
            mFollowGroup.findViewById(R.id.btn_close_follow).setOnClickListener(this);
            mFollowGroup.findViewById(R.id.btn_follow).setOnClickListener(this);
        }
        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnFace = (CheckBox) findViewById(R.id.btn_face);
        mBtnFace.setOnClickListener(this);
        View btnAdd = findViewById(R.id.btn_add);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(this);
        }
        mBtnVoice = (CheckBox) findViewById(R.id.btn_voice_record);
        if (mBtnVoice != null) {
            mBtnVoice.setOnClickListener(this);
        }
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    return hideSoftInput() || hideFace();
                }
                return false;
            }
        });
        mCheckBlackCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                processCheckBlackData(code, msg, info);
            }
        };
        if(!EventBus.getDefault().isRegistered(this)){//????????????
            EventBus.getDefault().register(this);
        }
        mHandler = new Handler();
        mEditText.requestFocus();
    }

    /**
     * ?????????????????????
     */
    private View initFaceView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.view_chat_face, null);
        v.measure(0, 0);
        mFaceViewHeight = v.getMeasuredHeight();
        v.findViewById(R.id.btn_send).setOnClickListener(this);
        final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(10);
        ImChatFacePagerAdapter adapter = new ImChatFacePagerAdapter(mContext, this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0, pageCount = adapter.getCount(); i < pageCount; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_chat_indicator, radioGroup, false);
            radioButton.setId(i + 10000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            radioGroup.addView(radioButton);
        }
        return v;
    }


    public void loadData() {
        if (mUserBean == null) {
            return;
        }
        mToUid = mUserBean.getId();
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        mTitleView.setText(mUserBean.getUserNiceName());
        mAdapter = new ImRoomAdapter(mContext, mToUid, mUserBean,ImRoomAdapter.TYPE_DIALOG);
        mAdapter.setActionListener(this);
        mRecyclerView.setAdapter(mAdapter);
        ImMessageUtil.getInstance().getChatMessageList(mToUid, new CommonCallback<List<ImMessageBean>>() {
            @Override
            public void callback(List<ImMessageBean> list) {
                mAdapter.setList(list);
                mAdapter.scrollToBottom();
            }
        });
    }


    public void setActionListener(ChatRoomActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void scrollToBottom() {
        if (mAdapter != null) {
            mAdapter.scrollToBottom();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_back) {
            back();

        } else if (i == R.id.btn_send) {
            sendText();

        } else if (i == R.id.btn_face) {
            faceClick();

        } else if (i == R.id.edit) {
            clickInput();

        } else if (i == R.id.btn_close_follow) {
            closeFollow();

        } else if (i == R.id.btn_follow) {
            follow();

        }
    }

    /**
     * ??????????????????
     */
    private void closeFollow() {
        if (mFollowGroup != null && mFollowGroup.getVisibility() == View.VISIBLE) {
            mFollowGroup.setVisibility(View.GONE);
        }
    }

    /**
     * ??????
     */
    private void follow() {
        CommonHttpUtil.setAttention(mToUid, null);
    }

    /**
     * ??????
     */
    public void back() {
        if (hideFace() || hideSoftInput()) {
            return;
        }
        if (mActionListener != null) {
            mActionListener.onCloseClick();
        }
    }

    /**
     * ???????????????
     */
    private void clickInput() {
        hideFace();
    }


    /**
     * ???????????????
     */
    private void showSoftInput() {
        if (/*!((KeyBoardHeightChangeListener) mContext).isSoftInputShowed() &&*/ imm != null && mEditText != null) {
            imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
            mEditText.requestFocus();
        }
    }

    /**
     * ????????????
     */
    private boolean hideSoftInput() {
        if (/*((KeyBoardHeightChangeListener) mContext).isSoftInputShowed() &&*/ imm != null && mEditText != null) {
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    /**
     * ??????????????????
     */
    private void faceClick() {
        if (mBtnFace.isChecked()) {
            hideSoftInput();
            hideVoiceRecord();
            if (mHandler != null) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showFace();
                    }
                }, 200);
            }
        } else {
            hideFace();
            showSoftInput();
        }
    }

    /**
     * ??????????????????
     */
    private void showFace() {
        if (mChatFaceDialog != null && mChatFaceDialog.isShowing()) {
            return;
        }
        if (mFaceView == null) {
            mFaceView = initFaceView();
        }
        if (mActionListener != null) {
            mActionListener.onPopupWindowChanged(mFaceViewHeight);
        }
        mChatFaceDialog = new ChatFaceDialog(mParentView, mFaceView, false, this);
        mChatFaceDialog.show();
    }

    /**
     * ??????????????????
     */
    private boolean hideFace() {
        if (mChatFaceDialog != null) {
            mChatFaceDialog.dismiss();
            mChatFaceDialog = null;
            return true;
        }
        return false;
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void onFaceDialogDismiss() {
        if (mActionListener != null) {
            mActionListener.onPopupWindowChanged(0);
        }
        mChatFaceDialog = null;
        if (mBtnFace != null) {
            mBtnFace.setChecked(false);
        }
    }


    /**
     * ???????????????????????????
     */
    @Override
    public void onMoreDialogDismiss() {
        if (mActionListener != null) {
            mActionListener.onPopupWindowChanged(0);
        }
    }

    /**
     * ????????????????????????
     */
    @Override
    public void onFaceClick(String str, int faceImageRes) {
        if (mEditText != null) {
            Editable editable = mEditText.getText();
            editable.insert(mEditText.getSelectionStart(), ImTextRender.getFaceImageSpan(str, faceImageRes));
        }
    }

    /**
     * ????????????????????????
     */
    @Override
    public void onFaceDeleteClick() {
        if (mEditText != null) {
            int selection = mEditText.getSelectionStart();
            String text = mEditText.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1, selection);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[", selection);
                    if (start >= 0) {
                        mEditText.getText().delete(start, selection);
                    } else {
                        mEditText.getText().delete(selection - 1, selection);
                    }
                } else {
                    mEditText.getText().delete(selection - 1, selection);
                }
            }
        }
    }

    /**
     * ????????????
     */
    @Override
    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mMediaRecordUtil != null) {
            mMediaRecordUtil.release();
        }
        mMediaRecordUtil = null;
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.destroy();
        }
        mVoiceMediaPlayerUtil = null;
        if (mAdapter != null) {
            mAdapter.release();
        }
        ImMessageUtil.getInstance().refreshAllUnReadMsgCount();
         if (EventBus.getDefault().isRegistered(this)) {//????????????
            EventBus.getDefault().unregister(this);
        }
        mActionListener = null;
        if (mChatFaceDialog != null) {
            mChatFaceDialog.dismiss();
        }
        mChatFaceDialog = null;
        if (mChatImageDialog != null) {
            mChatImageDialog.dismiss();
        }
        mChatImageDialog = null;
    }

    /**
     * ????????????????????????????????????
     */
    @Override
    public void onImageClick(MyImageView imageView, int x, int y) {
        if (mAdapter == null || imageView == null) {
            return;
        }
        hideSoftInput();
        File imageFile = imageView.getFile();
        ImMessageBean imMessageBean = imageView.getImMessageBean();
        if (imageFile != null && imMessageBean != null) {
//            mChatImageDialog = new ChatImageDialog();
//            mChatImageDialog.setImageInfo(mAdapter.getChatImageBean(imMessageBean), x, y, imageView.getWidth(), imageView.getHeight(), imageView.getDrawable());
//            mChatImageDialog.show(((AbsActivity) mContext).getSupportFragmentManager(), "ChatImageDialog2");
        }
    }


    /**
     * ??????????????????????????????????????????
     */
    @Override
    public void onVoiceStartPlay(File voiceFile) {
        if (mVoiceMediaPlayerUtil == null) {
            mVoiceMediaPlayerUtil = new VoiceMediaPlayerUtil(mContext);
            mVoiceMediaPlayerUtil.setActionListener(new VoiceMediaPlayerUtil.ActionListener() {
                @Override
                public void onPlayEnd() {
                    if (mAdapter != null) {
                        mAdapter.stopVoiceAnim();
                    }
                }
            });
        }
        mVoiceMediaPlayerUtil.startPlay(voiceFile.getAbsolutePath());
    }

    /**
     * ????????????????????????????????????????????????
     */
    @Override
    public void onVoiceStopPlay() {
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.stopPlay();
        }
    }


    /**
     * ????????????
     */
    private void hideVoiceRecord() {
        if (mBtnVoice != null && mBtnVoice.isChecked()) {
            mBtnVoice.setChecked(false);
            if (mEditText.getVisibility() != View.VISIBLE) {
                mEditText.setVisibility(View.VISIBLE);
            }
            if (mVoiceRecordEdit.getVisibility() == View.VISIBLE) {
                mVoiceRecordEdit.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * ????????????
     */
    public void clickVoiceRecord() {
        if (mBtnVoice == null) {
            return;
        }
        if (mBtnVoice.isChecked()) {
            hideSoftInput();
            hideFace();
            if (mEditText.getVisibility() == View.VISIBLE) {
                mEditText.setVisibility(View.INVISIBLE);
            }
            if (mVoiceRecordEdit != null && mVoiceRecordEdit.getVisibility() != View.VISIBLE) {
                mVoiceRecordEdit.setVisibility(View.VISIBLE);
            }
        } else {
            if (mVoiceRecordEdit != null && mVoiceRecordEdit.getVisibility() == View.VISIBLE) {
                mVoiceRecordEdit.setVisibility(View.INVISIBLE);
            }
            if (mEditText.getVisibility() != View.VISIBLE) {
                mEditText.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * ????????????
     */
    public void startRecordVoice() {
        if (mVoiceRecordEdit == null) {
            return;
        }
        mVoiceRecordEdit.setBackground(mVoicePressedDrawable);
        mVoiceRecordEdit.setText(mUnPressStopString);
        if (mMediaRecordUtil == null) {
            mMediaRecordUtil = new MediaRecordUtil();
        }
        File dir = new File(CommonAppConfig.MUSIC_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mRecordVoiceFile = new File(dir, DateFormatUtil.getCurTimeString() + ".m4a");
        mMediaRecordUtil.startRecord(mRecordVoiceFile.getAbsolutePath());
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopRecordVoice();
                }
            }, 60000);
        }
    }

    /**
     * ????????????
     */
    public void stopRecordVoice() {
        if (mVoiceRecordEdit == null) {
            return;
        }
        mVoiceRecordEdit.setBackground(mVoiceUnPressedDrawable);
        mVoiceRecordEdit.setText(mPressSayString);
        mRecordVoiceDuration = mMediaRecordUtil.stopRecord();
        if (mRecordVoiceDuration < 2000) {
            ToastUtil.show(WordUtil.getString(R.string.im_record_audio_too_short));
            deleteVoiceFile();
        } else {
            mCurMessageBean = ImMessageUtil.getInstance().createVoiceMessage(mToUid, mRecordVoiceFile, mRecordVoiceDuration);
            if (mCurMessageBean != null) {
                sendMessage();
            } else {
                deleteVoiceFile();
            }
        }
    }

    /**
     * ??????????????????
     */
    private void deleteVoiceFile() {
        if (mRecordVoiceFile != null && mRecordVoiceFile.exists()) {
            mRecordVoiceFile.delete();
        }
        mRecordVoiceFile = null;
        mRecordVoiceDuration = 0;
    }

    /**************************************************************************************************/
    /*********************************?????????????????????????????????????????????????????????***********************************/
    /**************************************************************************************************/

    /**
     * ??????????????????????????????
     */
    public void refreshLastMessage() {
        if (mAdapter != null) {
            ImMessageBean bean = mAdapter.getLastMessage();
            if (bean != null) {
                ImMessageUtil.getInstance().refreshLastMessage(mToUid, bean);
            }
        }
    }


    /**
     * ?????????????????????
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImMessageBean(ImMessageBean bean) {
        if (!bean.getUid().equals(mToUid)) {
            return;
        }
        if (mAdapter != null) {
            mAdapter.insertItem(bean);
            ImMessageUtil.getInstance().markAllMessagesAsRead(mToUid, false);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (e.getToUid().equals(mToUid)) {
            if (mFollowGroup != null) {
                if (e.getAttention() == 1) {
                    if (mFollowGroup.getVisibility() == View.VISIBLE) {
                        mFollowGroup.setVisibility(View.GONE);
                    }
                    ToastUtil.show(R.string.im_follow_tip_2);
                } else {
                    if (mFollowGroup.getVisibility() != View.VISIBLE) {
                        mFollowGroup.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }


    /**
     * ??????????????????????????????
     */
    private boolean isCanSendMsg() {
        if (!CommonAppConfig.getInstance().isLoginIM()) {
            ToastUtil.show("IM???????????????????????????");
            return false;
        }
        long curTime = System.currentTimeMillis();
        if (curTime - mLastSendTime < 1500) {
            ToastUtil.show(R.string.im_send_too_fast);
            return false;
        }
        mLastSendTime = curTime;
        return true;
    }

    /**
     * ??????????????????
     */
    public void sendText(String content) {
        if (TextUtils.isEmpty(content)) {
            ToastUtil.show(R.string.content_empty);
            return;
        }
        ImMessageBean messageBean = ImMessageUtil.getInstance().createTextMessage(mToUid, content);
        if (messageBean == null) {
            ToastUtil.show(R.string.im_msg_send_failed);
            return;
        }
        mCurMessageBean = messageBean;
        sendMessage();
    }

    /**
     * ??????????????????
     */
    private void sendText() {
        String content = mEditText.getText().toString().trim();
        sendText(content);
    }

    /**
     * ??????????????????
     */
    public void sendImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        ImMessageBean messageBean = ImMessageUtil.getInstance().createImageMessage(mToUid, path);
        if (messageBean == null) {
            ToastUtil.show(R.string.im_msg_send_failed);
            return;
        }
        mCurMessageBean = messageBean;
        sendMessage();
    }

    /**
     * ??????????????????
     */
    public void sendLocation(double lat, double lng, int scale, String address) {
        ImMessageBean messageBean = ImMessageUtil.getInstance().createLocationMessage(mToUid, lat, lng, scale, address);
        if (messageBean == null) {
            ToastUtil.show(R.string.im_msg_send_failed);
            return;
        }
        mCurMessageBean = messageBean;
        sendMessage();
    }


    /**
     * ????????????
     */
    private void sendMessage() {
        if (!isCanSendMsg()) {
            return;
        }
        if (mCurMessageBean != null) {
            CommonHttpUtil.checkBlack(mToUid,mCheckBlackCallback);
        } else {
            ToastUtil.show(R.string.im_msg_send_failed);
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void processCheckBlackData(int code, String msg, String[] info) {
        if (code == 0) {
            if (info.length > 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                int t2u = obj.getIntValue("t2u");
                int u2t = obj.getIntValue("u2t");
                if (1 == t2u && 1 == u2t){
                    ToastUtil.show(R.string.im_you_are_blacked_u2t);
                }else if (1 == t2u) {//?????????
                    ToastUtil.show(R.string.im_you_are_blacked);
//                    if (mCurMessageBean != null) {
//                        ImMessageUtil.getInstance().removeMessage(mToUid, mCurMessageBean);
//                    }
                }else if (1 == u2t) {//????????????
                    ToastUtil.show(R.string.im_you_are_blacked_u2t);
                } else {
                    if (mCurMessageBean != null) {
                        if (mCurMessageBean.getType() == ImMessageBean.TYPE_TEXT) {
                            mEditText.setText("");
                        }
                        if (mAdapter != null) {
                            mAdapter.insertSelfItem(mCurMessageBean);
                        }
                    } else {
                        ToastUtil.show(WordUtil.getString(R.string.im_msg_send_failed));
                    }
                }
            }
        } else {
            ToastUtil.show(msg);
        }
    }


    @Override
    public void onPause() {
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.pausePlay();
        }
    }

    @Override
    public void onResume() {
        if (mVoiceMediaPlayerUtil != null) {
            mVoiceMediaPlayerUtil.resumePlay();
        }
    }


}
