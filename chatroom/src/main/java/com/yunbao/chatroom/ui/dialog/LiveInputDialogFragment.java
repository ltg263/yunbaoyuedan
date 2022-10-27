package com.yunbao.chatroom.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.MyRadioButton;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.business.socket.SocketProxy;
import com.yunbao.chatroom.business.socket.base.mannger.ChatMannger;


/*输入框*/
public class LiveInputDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private InputMethodManager imm;
    private EditText mInput;
    private MyRadioButton mMyRadioButton;

    private ChatMannger mChatMannger;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_chat_input;
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
        params.height = DpUtil.dp2px(50);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LiveActivityLifeModel liveActivityLifeModel = LiveActivityLifeModel.getByContext(getActivity(), LiveActivityLifeModel.class);
        SocketProxy socketProxy= liveActivityLifeModel.getSocketProxy();
        mChatMannger= socketProxy==null?null:socketProxy.getChatMannger();
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInput = (EditText) mRootView.findViewById(R.id.input);
        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mMyRadioButton.doChecked(false);
                } else {
                    mMyRadioButton.doChecked(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                //软键盘弹出
                mInput.requestFocus();
                imm.showSoftInput(mInput, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 200);
        mMyRadioButton = (MyRadioButton) mRootView.findViewById(R.id.btn_send);
        mMyRadioButton.setOnClickListener(this);

        initSoftInputListener();

        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
    }

    @Override
    public void onClick(View v) {
        sendMessage();
    }

    private void sendMessage() {
        String content = mInput.getText().toString().trim();
        if(!TextUtils.isEmpty(content)){
            sendMessage(content);
            mInput.setText("");
        }
    }


    private void sendMessage(String content) {
        UserBean userBean= CommonAppConfig.getInstance().getUserBean();
        if(mChatMannger!=null){
            mChatMannger.sendChatMessage(content);
            closeInput();
            dismiss();
        }
    }

    /**
     * 点击非输入框区域时，自动收起键盘
     */

    private void initSoftInputListener() {
        getDialog().getWindow().getDecorView()
                .setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {

                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            closeInput();

                        }
                        return false;
                    }
                });
    }

    private void closeInput() {
        if (getDialog().getCurrentFocus() != null
                && getDialog().getCurrentFocus().getWindowToken() != null) {
            imm.hideSoftInputFromWindow(
                    getDialog().getCurrentFocus().getWindowToken(),
                    0);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (imm != null) {
            imm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext=null;
        mChatMannger=null;
    }
}
