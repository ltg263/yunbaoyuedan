package com.yunbao.main.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.event.TeenagerEvent;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * @author apple
 */
public class YoungActivity extends AbsActivity implements View.OnClickListener {
    private TextView mPwd;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_young;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.young));
        findViewById(R.id.btn_open_young).setOnClickListener(this);
        mPwd=findViewById(R.id.change_pwd);
        mPwd.setOnClickListener(this);

        if (CommonAppConfig.getInstance().getIsPwd()==1){
            mPwd.setVisibility(View.VISIBLE);
        }
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }




    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_open_young) {
            if (CommonAppConfig.getInstance().getIsPwd()==1){ //已设置过密码
                PwdInputActivity.forward(mContext,1);
            }else{
                Intent intent=new Intent(mContext, PwdActivity.class);
                intent.putExtra(Constants.TYPE,1);
                startActivity(intent);
            }
        }else if (i== R.id.change_pwd){
            PwdChangeActivity.forward(mContext);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTeenagerEvent(TeenagerEvent e) {
            finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
       if (EventBus.getDefault().isRegistered(this)) {//加上判断
            EventBus.getDefault().unregister(this);
        }
    }
}
