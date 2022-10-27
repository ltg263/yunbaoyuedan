package com.yunbao.main.activity;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.event.LoginSuccessEvent;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by cxf on 2018/9/25.
 */

public class RegisterActivity extends AbsActivity {

    private EditText mPhoneNum;
    private EditText mCode;
    private EditText mPwd1;
    private EditText mPwd2;
    private TextView mBtnGetCode;
    private View mBtnRegister;
    private Handler mHandler;
    private static final int TOTAL = 60;
    private int mCount = TOTAL;
    private String mGetCode;
    private String mGetCodeAgain;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }


    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.reg_register));
        mPhoneNum = findViewById(R.id.phone_num);
        mCode = findViewById(R.id.code);
        mPwd1 = findViewById(R.id.pwd);
        mPwd2 = findViewById(R.id.pwd_2);
        mBtnGetCode = (TextView) findViewById(R.id.btn_get_code);
        mBtnRegister = findViewById(R.id.btn_register);
        mGetCode = WordUtil.getString(R.string.reg_get_code);
        mGetCodeAgain = WordUtil.getString(R.string.reg_get_code_again);
        mPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mCount == TOTAL) {
                    if (!TextUtils.isEmpty(s)) {
                        mBtnGetCode.setEnabled(true);
                    } else {
                        mBtnGetCode.setEnabled(false);
                    }
                }
                changeEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mCode.addTextChangedListener(textWatcher);
        mPwd1.addTextChangedListener(textWatcher);
        mPwd2.addTextChangedListener(textWatcher);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mCount--;
                if (mCount > 0) {
                    mBtnGetCode.setText(String.format(mGetCodeAgain, String.valueOf(mCount)));
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(0, 1000);
                    }
                } else {
                    mBtnGetCode.setText(mGetCode);
                    mCount = TOTAL;
                    if (mBtnGetCode != null) {
                        mBtnGetCode.setEnabled(true);
                    }
                }
            }
        };
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
    }

    private void changeEnable() {
        String phone = mPhoneNum.getText().toString();
        String code = mCode.getText().toString();
        String pwd1 = mPwd1.getText().toString();
        String pwd2 = mPwd2.getText().toString();
        mBtnRegister.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(code) && !TextUtils.isEmpty(pwd1) && !TextUtils.isEmpty(pwd2));
    }

    public void registerClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_get_code) {
            getCode();

        } else if (i == R.id.btn_register) {
            register();
        }
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        String phoneNum = mPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            mPhoneNum.setError(WordUtil.getString(R.string.login_input_email));
            mPhoneNum.requestFocus();
            return;
        }
        mCode.requestFocus();
        MainHttpUtil.getRegisterCode(phoneNum, mGetCodeCallback);
    }

    private HttpCallback mGetCodeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (mBtnGetCode != null) {
                    mBtnGetCode.setEnabled(false);
                }
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(0);
                }
            } else {
                ToastUtil.show(msg);
            }
        }
    };

    /**
     * 注册并登陆
     */
    private void register() {
        final String phoneNum = mPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            mPhoneNum.setError(WordUtil.getString(R.string.login_input_email));
            mPhoneNum.requestFocus();
            return;
        }
        String code = mCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            mCode.setError(WordUtil.getString(R.string.login_input_code));
            mCode.requestFocus();
            return;
        }
        final String pwd = mPwd1.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            mPwd1.setError(WordUtil.getString(R.string.login_input_pwd_1));
            mPwd1.requestFocus();
            return;
        }
        String pwd2 = mPwd2.getText().toString().trim();
        if (TextUtils.isEmpty(pwd2)) {
            mPwd2.setError(WordUtil.getString(R.string.login_input_pwd_2));
            mPwd2.requestFocus();
            return;
        }
        if (!pwd.equals(pwd2)) {
            mPwd2.setError(WordUtil.getString(R.string.reg_pwd_error));
            mPwd2.requestFocus();
            return;
        }
        MainHttpUtil.register(phoneNum, code, pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        try {
                            JSONObject obj = JSON.parseObject(info[0]);
                            if (obj.getIntValue("sex") == 0) {
                                CommonAppConfig.getInstance().setLoginInfo(obj.getString("id"), obj.getString("token"), false);
                                SetProfileActivity.forward(mContext, info[0], false);
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }

        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccessEvent(LoginSuccessEvent e) {
        finish();
    }

    @Override
    protected void onDestroy() {
         if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
        MainHttpUtil.cancel(MainHttpConsts.GET_REGISTER_CODE);
        MainHttpUtil.cancel(MainHttpConsts.REGISTER);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }
}
