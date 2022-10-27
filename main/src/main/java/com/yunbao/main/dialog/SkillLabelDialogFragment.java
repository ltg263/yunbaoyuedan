package com.yunbao.main.dialog;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.SkillLabelAdapter;
import com.yunbao.main.bean.SkillLabelBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/7/26.
 */

public class SkillLabelDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private View mLoading;
    private RecyclerView mRecyclerView;
    private SkillLabelAdapter mAdapter;
    private ActionListener mActionListener;
    private List<SkillLabelBean> mLabelList;
    private String mSkillId;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_skill_label;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(320);
        params.height = DpUtil.dp2px(290);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoading = findViewById(R.id.loading);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 10, 10);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);

        MainHttpUtil.getSkillLabel(mSkillId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<SkillLabelBean> list = JSON.parseArray(Arrays.toString(info), SkillLabelBean.class);
                    if (mLabelList != null && mLabelList.size() > 0) {
                        for (int i = 0, size = mLabelList.size(); i < size; i++) {
                            String labelName = mLabelList.get(i).getName();
                            if (!TextUtils.isEmpty(labelName)) {
                                for (int j = 0, size2 = list.size(); j < size2; j++) {
                                    SkillLabelBean bean = list.get(j);
                                    if (labelName.equals(bean.getName())) {
                                        bean.setChecked(true);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    mAdapter = new SkillLabelAdapter(mContext, list);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onFinish() {
                if (mLoading != null) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_SKILL_LABEL);
        mActionListener = null;
        mContext = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_close) {
            dismiss();
        } else if (i == R.id.btn_confirm) {
            dismiss();
            if (mActionListener != null && mAdapter != null) {
                mActionListener.onConfrim(mAdapter.getCheckedList());
            }
        }
    }

    public void setSkillId(String skillId) {
        mSkillId = skillId;
    }

    public void setLabelList(List<SkillLabelBean> labelList) {
        mLabelList = labelList;
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onConfrim(List<SkillLabelBean> list);
    }
}
