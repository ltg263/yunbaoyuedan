package com.yunbao.main.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.event.LoginSuccessEvent;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.mob.LoginData;
import com.yunbao.common.mob.MobBean;
import com.yunbao.common.mob.MobCallback;
import com.yunbao.common.mob.MobLoginUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastHigherUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.LoginTypeAdapter;
import com.yunbao.main.dialog.LoginTipDialogFragment;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by cxf on 2018/9/17.
 */

public class LoginActivity extends AbsActivity implements OnItemClickListener<MobBean> {

    private boolean mHasGetCode;
    private HttpCallback mGetCodeCallback;
    private Handler mHandler;
    private int TOTAL = 60;
    private int mCount = TOTAL;
    private String mGetCodeAgain;
    private TextView mBtnBottomTip;
    private TextView tv_phone_code;
    private boolean mIsAgreeTerms = false;
    private CheckBox cb_read_terms;

    public static void forward() {
        Intent intent = new Intent(CommonAppContext.sInstance, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        CommonAppContext.sInstance.startActivity(intent);
    }

    private EditText mPhoneNum;//手机号
    private View mBtnLogin;
    private RecyclerView mRecyclerView;
    private MobLoginUtil mLoginUtil;
    private String mLoginType = Constants.MOB_PHONE;//登录方式

    private TextView mEditCode;
    private TextView mBtnGetCode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }

    @Override
    protected void main() {
        mPhoneNum = findViewById(R.id.phone_num);
        mEditCode = findViewById(R.id.edit_code);
        tv_phone_code = findViewById(R.id.tv_phone_code);
        mBtnGetCode = findViewById(R.id.btn_get_code);
        mBtnLogin = findViewById(R.id.btn_login);
        mGetCodeAgain = WordUtil.getString(R.string.login_get_code_again);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mCount--;
                if (mCount > 0) {
                    mBtnGetCode.setText(mCount + "s");
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(0, 1000);
                    }
                } else {
                    mBtnGetCode.setText(mGetCodeAgain);
                    mCount = TOTAL;
                    if (mBtnGetCode != null) {
                        mBtnGetCode.setEnabled(true);
                    }
                }
            }
        };

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = mPhoneNum.getText().toString();
                String code = mEditCode.getText().toString();
                if (phone.length() > 0) {
                    mBtnGetCode.setEnabled(true);
                } else {
                    mBtnGetCode.setEnabled(false);
                }
                mBtnLogin.setEnabled(code.length() == 6 && phone.length() > 0 && mHasGetCode);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mPhoneNum.addTextChangedListener(textWatcher);
        mEditCode.addTextChangedListener(textWatcher);
        cb_read_terms = findViewById(R.id.cb_read_terms);
        cb_read_terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsAgreeTerms = isChecked;
            }
        });
        //String companyName = CommonAppConfig.APP_IS_YUNBAO_SELF ? "云豹约玩系统" : CommonAppConfig.getInstance().getAppName();
        ImageView appIcon = findViewById(R.id.app_icon);
        ImgLoader.display(mContext, CommonAppConfig.getInstance().getAppIconRes(), appIcon);
        TextView appName = findViewById(R.id.app_name);
        appName.setText(WordUtil.getString(R.string.login_tip_3));
        mBtnBottomTip = findViewById(R.id.btn_tip);
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            List<MobBean> list = MobBean.getLoginTypeList(configBean.getLoginType());
            if (list != null && list.size() > 0) {
                mRecyclerView = findViewById(R.id.recyclerView);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                LoginTypeAdapter adapter = new LoginTypeAdapter(mContext, list);
                adapter.setOnItemClickListener(this);
                mRecyclerView.setAdapter(adapter);
                mLoginUtil = new MobLoginUtil();
            }
        }
        showBottomTips();
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
    }

    private void showBottomTips() {
        MainHttpUtil.getLoginInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject loginInfo = JSON.parseObject(info[0]).getJSONObject("login_alert");
                    if (mBtnBottomTip != null) {
                        String content = loginInfo.getString("login_title");
                        SpannableString spannableString = new SpannableString(content);
                        JSONArray msgArray = JSON.parseArray(loginInfo.getString("message"));
                        for (int i = 0, size = msgArray.size(); i < size; i++) {
                            final JSONObject msgItem = msgArray.getJSONObject(i);
                            String title = msgItem.getString("title");
                            int startIndex = content.indexOf(title);
                            if (startIndex > 0) {
                                ClickableSpan clickableSpan = new ClickableSpan() {
                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        super.updateDrawState(ds);
                                        ds.setColor(0xff3399ee);
                                        ds.setUnderlineText(false);
                                    }

                                    @Override
                                    public void onClick(View widget) {
                                        WebViewActivity.forward(mContext, msgItem.getString("url"), false);
                                    }

                                };
                                int endIndex = startIndex + title.length();
                                spannableString.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            }
                        }

                        mBtnBottomTip.setText(spannableString);
                        mBtnBottomTip.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
                        mBtnBottomTip.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明
                    }
                }
            }
        });
    }

    private void getLoginTip() {
        LoginTipDialogFragment fragment = new LoginTipDialogFragment();
        fragment.show(getSupportFragmentManager(), "LoginTipDialogFragment");
    }

    public void loginClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_login) {
            login();
        } else if (i == R.id.btn_register) {
            register();
        } else if (i == R.id.btn_forget_pwd) {
            forgetPwd();
        } else if (i == R.id.btn_get_code) {
            getLoginCode();
        } else if (i == R.id.ll_phone_code){
            changePhoneCountryCode();
        }
    }

    private void changePhoneCountryCode(){
        startActivityForResult(new Intent(mContext,ChoosePhoneCountryCodeActivity.class),ChoosePhoneCountryCodeActivity.INTENT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ChoosePhoneCountryCodeActivity.INTENT_REQUEST_CODE && resultCode == RESULT_OK){
            if (data != null){
                if (tv_phone_code != null){
                    tv_phone_code.setText(data.getStringExtra(Constants.INTENT_PHONE_COUNTRY_CODE));
                }
            }
        }
    }

    private void getLoginCode() {
        String phoneNum = mPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
//            mPhoneNum.setError(WordUtil.getString(R.string.login_input_phone));
            ToastUtil.show(WordUtil.getString(R.string.login_input_phone));
            mPhoneNum.requestFocus();
            return;
        }
//        if (!ValidatePhoneUtil.validateMobileNumber(phoneNum)) {
////            mPhoneNum.setError(WordUtil.getString(R.string.login_phone_error));
//            ToastUtil.show(WordUtil.getString(R.string.login_phone_error));
//            mPhoneNum.requestFocus();
//            return;
//        }
        mEditCode.requestFocus();
        if (mGetCodeCallback == null) {
            mGetCodeCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        mBtnGetCode.setEnabled(false);
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(0);
                        }
                        if (!TextUtils.isEmpty(msg) && msg.contains("123456")) {
                            ToastUtil.show(msg);
                        }
                    }
                    ToastUtil.show(msg);
                }
            };
        }
        mHasGetCode = true;
        MainHttpUtil.getLoginCode(phoneNum, tv_phone_code.getText().toString(),mGetCodeCallback);
    }


    /**
     * 注册
     */

    private void register() {
        startActivity(new Intent(mContext, RegisterActivity.class));
    }

    /**
     * 忘记密码
     */
    private void forgetPwd() {
        startActivity(new Intent(mContext, FindPwdActivity.class));
    }

    /**
     * 手机号密码登录
     */
    private void login() {
        if (!mIsAgreeTerms){
            ToastHigherUtil.show(WordUtil.getString(R.string.login_tip_5));
            return;
        }
        final String phoneNum = mPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
//            mPhoneNum.setError(WordUtil.getString(R.string.login_input_phone));
            ToastUtil.show(WordUtil.getString(R.string.login_input_phone));
            mPhoneNum.requestFocus();
            return;
        }
//        if (!ValidatePhoneUtil.validateMobileNumber(phoneNum)) {
////            mPhoneNum.setError(WordUtil.getString(R.string.login_phone_error));
//            ToastUtil.show(WordUtil.getString(R.string.login_phone_error));
//            mPhoneNum.requestFocus();
//            return;
//        }
        if (!mHasGetCode) {
            ToastUtil.show(R.string.login_get_code_please);
            return;
        }

        final String code = mEditCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
//            mEditCode.setError(WordUtil.getString(R.string.login_input_code));
            ToastUtil.show(WordUtil.getString(R.string.login_input_code));
            mEditCode.requestFocus();
            return;
        }

        mLoginType = Constants.MOB_PHONE;
        MainHttpUtil.login(phoneNum, code,tv_phone_code.getText().toString(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }
        });
    }

    /**
     * 登录即代表同意服务和隐私条款
     */
    private void forwardTip() {
        WebViewActivity.forward(mContext, HtmlConfig.LOGIN_PRIVCAY);
    }

    /**
     * 三方登录
     */
    private void loginBuyThird(LoginData data) {
        mLoginType = data.getType();
        MainHttpUtil.loginByThird(data.getOpenID(), data.getAccessToken(),data.getNickName(), data.getAvatar(), data.getFlag(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }
        });
    }

    /**
     * 登录成功！
     */
    private void onLoginSuccess(int code, String msg, String[] info) {
        if (code == 0) {
            if (info.length > 0) {
                try {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String uid = obj.getString("id");
                    String token = obj.getString("token");
                    SpUtil.getInstance().setBooleanValue(Constants.FIRST_LOGIN, obj.getIntValue("isreg") == 1);
                    if (obj.getIntValue("sex") == 0) {
                        CommonAppConfig.getInstance().setLoginInfo(uid, token, false);
                        SetProfileActivity.forward(mContext, info[0], !mLoginType.equals(Constants.MOB_PHONE));
                    } else {
                        CommonAppConfig appConfig = CommonAppConfig.getInstance();
                        appConfig.setLoginInfo(uid, token, true);
                        UserBean userBean = JSON.toJavaObject(obj, UserBean.class);
                        appConfig.setUserBean(userBean);
                        Map<String, String> map = new HashMap<>();
                        map.put(SpUtil.USER_INFO, JSON.toJSONString(userBean));
                        map.put(SpUtil.TX_IM_USER_SIGN, obj.getString("usersig"));
                        SpUtil.getInstance().setMultiStringValue(map);
                        CommonAppConfig.getInstance().setIsLogin(1);
                        MainActivity.forward(mContext);
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


    @Override
    public void onItemClick(MobBean bean, int position) {
        if (mLoginUtil == null) {
            return;
        }
        if (!mIsAgreeTerms){
            ToastHigherUtil.show(WordUtil.getString(R.string.login_tip_5));
            return;
        }
        final Dialog dialog = DialogUitl.loginAuthDialog(mContext);
        dialog.show();
        mLoginUtil.execute(bean.getType(), new MobCallback() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    loginBuyThird((LoginData) data);
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFinish() {
                if (dialog != null) {
                    dialog.dismiss();
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
        CommonHttpUtil.cancel(CommonHttpConsts.GET_QQ_LOGIN_UNION_ID);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN_BY_THIRD);
        if (mLoginUtil != null) {
            mLoginUtil.release();
        }
        super.onDestroy();
    }
}
