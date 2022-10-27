package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.event.TeenagerEvent;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * @author apple
 */
@Route(path = RouteUtil.PATH_TEENAGER)
public class YoungOpenedActivity extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context) {
        Intent intent=new Intent(context, YoungOpenedActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_young_opened;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.young));
        findViewById(R.id.btn_close_young).setOnClickListener(this);
        findViewById(R.id.change_pwd).setOnClickListener(this);
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }


    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_close_young) {
            PwdInputActivity.forward(mContext,2);
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
