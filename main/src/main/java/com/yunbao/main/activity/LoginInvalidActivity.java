package com.yunbao.main.activity;

import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.event.LoginInvalidEvent;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.im.config.CallConfig;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.main.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2017/10/9.
 * 登录失效的时候以dialog形式弹出的activity
 */
@Route(path = RouteUtil.PATH_LOGIN_INVALID)
public class LoginInvalidActivity extends AbsActivity implements View.OnClickListener {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_invalid;
    }

    @Override
    protected void main() {
        TextView textView = (TextView) findViewById(R.id.content);
        String tip = getIntent().getStringExtra(Constants.TIP);
        textView.setText(tip);
        findViewById(R.id.btn_confirm).setOnClickListener(this);

        EventBus.getDefault().post(new LoginInvalidEvent());
        CommonAppConfig.getInstance().clearLoginInfo();
        //退出IM
        ImMessageUtil.getInstance().logoutImClient();
        CallConfig.setIsBusy(false);
        //友盟统计登出
        //UMengUtil.onLogout();
    }

    @Override
    public void onClick(View v) {
        LoginActivity.forward();
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
