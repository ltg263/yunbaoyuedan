package com.yunbao.main.dialog;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.yunbao.main.R;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.main.adapter.SkillPriceTipAdapter;
import com.yunbao.main.bean.SkillPriceTipBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/4/17.
 */

public class SkillPriceTipDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private String mSkillId;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_skill_price_tip;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(320);
        params.height = DpUtil.dp2px(330);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewById(R.id.btn_close).setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        if (!TextUtils.isEmpty(mSkillId)) {
            MainHttpUtil.getSkillPriceInfo(mSkillId, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        List<SkillPriceTipBean> list = JSON.parseArray(Arrays.toString(info), SkillPriceTipBean.class);
                        if (mRecyclerView != null && list != null && list.size() > 0) {
                            SkillPriceTipAdapter adapter = new SkillPriceTipAdapter(mContext, list);
                            mRecyclerView.setAdapter(adapter);
                        }
                    }
                }
            });
        }
    }

    public void setSkillId(String skillId) {
        mSkillId = skillId;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_SKILL_PRICE_INFO);
        super.onDestroy();
    }
}
