package com.yunbao.main.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.HtmlConfig;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.LogoutConditionAdapter;
import com.yunbao.main.bean.LogoutConditionBean;
import com.yunbao.main.http.MainHttpUtil;

import java.util.List;

/**
 * Created by Sky.L on 2020-06-20
 * 注销条件
 */
public class LogoutActivity extends AbsActivity {
    private RecyclerView mRecyclerView;
    private LogoutConditionAdapter mAdapter;
    private TextView mBtnNextStep;
    private boolean mLogoutEnable;




    @Override
    protected int getLayoutId() {
        return R.layout.activity_logout_account;
    }

    @Override
    protected void main() {
        super.main();
        initView();
        initData();
    }

    private void initView() {
        setTitle(WordUtil.getString(R.string.logout_condition));
        mRecyclerView = findViewById(R.id.rl_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        mBtnNextStep = findViewById(R.id.btn_next_step);
    }


    private void initData() {
        MainHttpUtil.getCancelCondition(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject object = JSON.parseObject(info[0]);
                    if (object != null && !object.isEmpty()) {
                        mLogoutEnable = "1".equals(object.getString("can_cancel"));
                        List<LogoutConditionBean> list = JSON.parseArray(object.getString("list"), LogoutConditionBean.class);
                        if (mAdapter == null){
                            mAdapter = new LogoutConditionAdapter(mContext,list);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        if (mLogoutEnable){
                            mBtnNextStep.setEnabled(true);
                        }
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    public void onLogoutActivityClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_next_step) {
            clickNext();
        }
    }

    private void clickNext(){
        if (mLogoutEnable){
            LogoutWebViewActivity.forward(mContext, HtmlConfig.LOGOUT_ACCOUNT_URL);
        }
    }
}
