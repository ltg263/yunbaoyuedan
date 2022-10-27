package com.yunbao.dynamic.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.dialog.ChatFaceDialog;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.bean.DynamicCommentBean;
import com.yunbao.dynamic.event.DynamicCommentEvent;
import com.yunbao.dynamic.http.DynamicHttpUtil;
import com.yunbao.dynamic.ui.activity.DynamicDetailActivity;
import com.yunbao.dynamic.util.CommentTextRender;
import org.greenrobot.eventbus.EventBus;

public class DynamicInputDialogFragment extends AbsDialogFragment implements View.OnClickListener, ChatFaceDialog.ActionListener {

    private InputMethodManager imm;
    private EditText mInput;
    private boolean mOpenFace;
    private int mOriginHeight;
    private int mFaceHeight;
    private CheckBox mCheckBox;
    private ChatFaceDialog mChatFaceDialog;
    private static Handler mHandler;
    private String mDynamicId;
    private String mDynamicUid;
    private DynamicCommentBean dynamicCommentBean;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_dynamic_input;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        mOriginHeight = DpUtil.dp2px(48);
        params.height = mOriginHeight;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imm = (InputMethodManager) ((AbsActivity)mContext).getSystemService(Context.INPUT_METHOD_SERVICE);
        mHandler = new Handler();
        mInput = (EditText) mRootView.findViewById(R.id.input);
        mInput.setOnClickListener(this);
        mCheckBox = mRootView.findViewById(R.id.btn_face);
        mCheckBox.setOnClickListener(this);
        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendComment();
                    return true;
                }
                return false;
            }
        });
//        mInput.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP){
//                    showSoftInput();
//                }
//                return false;
//            }
//        });


        Bundle bundle = getArguments();
        if (bundle != null) {
            mOpenFace = bundle.getBoolean(Constants.VIDEO_FACE_OPEN, false);
            mFaceHeight = bundle.getInt(Constants.VIDEO_FACE_HEIGHT, 0);
            dynamicCommentBean = bundle.getParcelable(Constants.VIDEO_COMMENT_BEAN);
            if (dynamicCommentBean != null) {
                UserBean replyUserBean = dynamicCommentBean.getUserBean();//要回复的人
                if (replyUserBean != null) {
                    mInput.setHint(WordUtil.getString(R.string.comment_reply) + replyUserBean.getUserNiceName());
                }
            }
        }
        if (mOpenFace) {
            if (mCheckBox != null) {
                mCheckBox.setChecked(true);
            }
            if (mFaceHeight > 0) {
                changeHeight(mFaceHeight);
                if (mHandler != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showFace();
                        }
                    }, 200);
                }
            }
        } else {
            if (mInput != null) {
                mInput.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showSoftInput();
                    }
                }, 200);
            }
        }
    }


    public void setDynamicInfo(String videoId, String videoUid) {
        mDynamicId = videoId;
        mDynamicUid = videoUid;
    }


    private void showSoftInput() {
        if (mInput != null) {
            mInput.requestFocus();
        }
        //软键盘弹出
        if (imm != null) {
            imm.showSoftInput(mInput, InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }

    }


    private void hideSoftInput() {
        if (imm != null) {
            imm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mChatFaceDialog != null) {
            mChatFaceDialog.dismiss();
        }
        mChatFaceDialog = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_face) {
            clickFace();

        } else if (i == R.id.input) {
            clickInput();

        }
    }

    private void clickInput() {
        hideFace();
        if (mCheckBox != null) {
            mCheckBox.setChecked(false);
        }
    }

    private void clickFace() {
        if (mCheckBox.isChecked()) {
            hideSoftInput();
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

    private void showFace() {
        if (mFaceHeight > 0) {
            changeHeight(mFaceHeight);
            View faceView = ((DynamicDetailActivity) mContext).getFaceView();
            if (faceView != null) {
                mChatFaceDialog = new ChatFaceDialog(mRootView, faceView, false, DynamicInputDialogFragment.this);

                mChatFaceDialog.show();
            }
        }
    }

    private void hideFace() {
        if (mChatFaceDialog != null) {
            mChatFaceDialog.dismiss();
        }
    }

    /**
     * 改变高度
     */

    private void changeHeight(int deltaHeight) {
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        WindowManager.LayoutParams params = window.getAttributes();
        params.height = mOriginHeight + deltaHeight;
        window.setAttributes(params);
    }

    @Override
    public void onFaceDialogDismiss() {
        changeHeight(0);
        mChatFaceDialog = null;
    }

    /**
     * 发表评论
     */

    public void sendComment() {
        if (TextUtils.isEmpty(mDynamicId) || TextUtils.isEmpty(mDynamicUid) || mInput == null || !canClick()) {
            return;
        }
        String content = mInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.show(R.string.content_empty);
            return;
        }
        String toUid = mDynamicUid;
        String commentId = "0";
        String parentId = "0";
        if (mDynamicUid != null&&dynamicCommentBean!=null) {
            toUid = dynamicCommentBean.getUid();
            commentId = dynamicCommentBean.getCommentId();
            parentId = dynamicCommentBean.getId();
        }
        DynamicHttpUtil.setDynamicComment(toUid,content,mDynamicId,commentId,parentId).subscribe(new DefaultObserver<Boolean>() {
            @Override
            public void onNext(Boolean isSuccess) {
                if(isSuccess){
                 if (mInput != null) {
                     mInput.setText("");
                 }
                 ToastUtil.show(WordUtil.getString(R.string.comment_success));
                 EventBus.getDefault().post(new DynamicCommentEvent(mDynamicId));
                 dismiss();
                }
            }
        });
    }



    /**
     * 点击表情上面的删除按钮
     */

    public void onFaceDeleteClick() {
        if (mInput != null) {
            int selection = mInput.getSelectionStart();
            String text = mInput.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1, selection);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[", selection);
                    if (start >= 0) {
                        mInput.getText().delete(start, selection);
                    } else {
                        mInput.getText().delete(selection - 1, selection);
                    }
                } else {
                    mInput.getText().delete(selection - 1, selection);
                }
            }
        }
    }


    /**
     * 点击表情
     */


    public void onFaceClick(String str, int faceImageRes) {
        if (mInput != null) {
            Editable editable = mInput.getText();
            editable.insert(mInput.getSelectionStart(), CommentTextRender.getFaceImageSpan(str, faceImageRes));
        }
    }

}
