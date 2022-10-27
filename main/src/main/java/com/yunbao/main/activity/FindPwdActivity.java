package com.yunbao.main.activity;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

/**
 * Created by cxf on 2018/9/25.
 */

public class FindPwdActivity extends AbsActivity {

    private EditText mPhoneNum;
    private EditText mCode;
    private EditText mPwd1;
    private EditText mPwd2;
    private TextView mBtnGetCode;
    private View mBtnFind;
    private Handler mHandler;
    private static final int TOTAL = 60;
    private int mCount = TOTAL;
    private String mGetCode;
    private String mGetCodeAgain;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_pwd;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.login_pwd_forget));
        mPhoneNum = findViewById(R.id.phone_num);
        mCode = (EditText) findViewById(R.id.code);
        mPwd1 = (EditText) findViewById(R.id.pwd);
        mPwd2 = (EditText) findViewById(R.id.pwd_2);
        mBtnGetCode = (TextView) findViewById(R.id.btn_get_code);
        mBtnFind = findViewById(R.id.btn_find);
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
    }

    private void changeEnable() {
        String phone = mPhoneNum.getText().toString();
        String code = mCode.getText().toString();
        String pwd1 = mPwd1.getText().toString();
        String pwd2 = mPwd2.getText().toString();
        mBtnFind.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(code) && !TextUtils.isEmpty(pwd1) && !TextUtils.isEmpty(pwd2));
    }

    public void registerClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_get_code) {
            getCode();
        } else if (i == R.id.btn_find) {
            findPwd();
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
        MainHttpUtil.getFindPwdCode(phoneNum, mGetCodeCallback);
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
     * 找回密码
     */
    private void findPwd() {
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
        MainHttpUtil.findPwd(phoneNum, code, pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    finish();
                }
                ToastUtil.show(msg);
            }

        });
    }


    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.FIND_PWD);
        MainHttpUtil.cancel(MainHttpConsts.GET_FIND_PWD_CODE);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }
}
