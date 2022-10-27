package com.yunbao.main.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.event.UpdateFieldEvent;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2019/7/24.
 * 编辑资料 签名
 */

public class EditSignActivity extends AbsActivity implements View.OnClickListener {

    private EditText mEditText;
    private TextView mCount;

    @Override
    protected int getLayoutId() {
        return R.layout.actiivity_edit_sign;
    }

    @Override
    protected void main() {
        mCount = findViewById(R.id.count);
        mEditText = findViewById(R.id.edit);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mCount != null) {
                    mCount.setText(StringUtil.contact(String.valueOf(s.length()), "/30"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        String sign = getIntent().getStringExtra(Constants.SIGN);
        if (!TextUtils.isEmpty(sign)) {
            mEditText.setText(sign);
            mEditText.setSelection(sign.length());
        }
        findViewById(R.id.btn_save).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_save) {
            save();
        }
    }

    private void save() {
        String result = mEditText.getText().toString().trim();
//        if (TextUtils.isEmpty(result)) {
//            ToastUtil.show(R.string.edit_profile_sign_2);
//            return;
//        }
        MainHttpUtil.updateUserInfo(StringUtil.contact("{\"signature\":\"", result, "\"}"), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    EventBus.getDefault().post(new UpdateFieldEvent());
                    if (info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        String sign = obj.getString("signature");
                        UserBean userBean = CommonAppConfig.getInstance().getUserBean();
                        userBean.setSignature(sign);
                        Intent intent = new Intent();
                        intent.putExtra(Constants.SIGN, sign);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                ToastUtil.show(msg);
            }
        });

    }


    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.UPDATE_USER_INFO);
        super.onDestroy();
    }
}
