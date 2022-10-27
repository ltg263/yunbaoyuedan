package com.yunbao.dynamic.ui.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.ui.view.MysDynamicVIewHolder;

public class MyDynamicActivity extends AbsActivity implements View.OnClickListener {
    private MysDynamicVIewHolder viewHolder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_dynamic;
    }
    @Override
    protected void main() {
        super.main();
        setTitle(getString(R.string.my_dynamic));
        TextView tvRightTitle= setRightTitle(getString(R.string.publish));
        tvRightTitle.setTextColor(getResources().getColor(R.color.global));
        tvRightTitle.setOnClickListener(this);

        viewHolder=new MysDynamicVIewHolder(mContext, (ViewGroup) findViewById(R.id.container), CommonAppConfig.getInstance().getUid());
        viewHolder.setNoDataTip(WordUtil.getString(R.string.no_publish_dynamic_tip));
        viewHolder.addToParent();
        viewHolder.subscribeActivityLifeCycle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(viewHolder!=null) {
            viewHolder.loadData();
        }
    }

    @Override
    public void onClick(View v) {
     int id=v.getId();
     if(id==R.id.tv_right_title){
         toPub();
     }
    }

    private void toPub() {
        RouteUtil.forwardPubDynamics();
    }


}
