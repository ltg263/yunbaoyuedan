package com.yunbao.main.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

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
 * 编辑资料 昵称
 */

public class EditNameActivity extends AbsActivity implements View.OnClickListener {

    private EditText mEditText;

    @Override
    protected int getLayoutId() {
        return R.layout.actiivity_edit_name;
    }

    @Override
    protected void main() {
        mEditText = findViewById(R.id.edit);
        String nickname = getIntent().getStringExtra(Constants.NICKNAME);
        if (!TextUtils.isEmpty(nickname)) {
            mEditText.setText(nickname);
            mEditText.setSelection(nickname.length());
        }
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_clear) {
            if (mEditText != null) {
                mEditText.setText("");
            }
        } else if (i == R.id.btn_save) {
            save();
        }
    }

    private void save() {
        String result = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(result)) {
            ToastUtil.show(R.string.set_profile_nickname);
            return;
        }
        MainHttpUtil.updateUserInfo(StringUtil.contact("{\"user_nickname\":\"", result, "\"}"), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    EventBus.getDefault().post(new UpdateFieldEvent());
                    if (info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        String nickname = obj.getString("user_nickname");
                        UserBean userBean = CommonAppConfig.getInstance().getUserBean();
                        userBean.setUserNiceName(nickname);
                        Intent intent = new Intent();
                        intent.putExtra(Constants.NICKNAME, nickname);
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
