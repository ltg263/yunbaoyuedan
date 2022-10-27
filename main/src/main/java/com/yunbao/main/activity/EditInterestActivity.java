package com.yunbao.main.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.event.UpdateFieldEvent;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.InterestAdapter;
import com.yunbao.main.bean.InterestBean;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/7/24.
 * 编辑资料 兴趣
 */

public class EditInterestActivity extends AbsActivity implements View.OnClickListener, InterestAdapter.ActionListener {

    private CommonRefreshView mCommonRefreshView;
    private InterestAdapter mAdapter;
    private TextView mTip;
    private String mTipString;
    private String mInteretString;

    @Override
    protected int getLayoutId() {
        return R.layout.actiivity_edit_interest;
    }

    @Override
    protected void main() {
        mInteretString = getIntent().getStringExtra(Constants.INTEREST);
        mTip = findViewById(R.id.tip);
        mTipString = WordUtil.getString(R.string.edit_profile_interest_2);
        findViewById(R.id.btn_save).setOnClickListener(this);
        mCommonRefreshView = findViewById(R.id.refreshView);
        mCommonRefreshView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 11, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mCommonRefreshView.setItemDecoration(decoration);
        mCommonRefreshView.setDataHelper(new CommonRefreshView.DataHelper<InterestBean>() {
            @Override
            public RefreshAdapter<InterestBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new InterestAdapter(mContext);
                    mAdapter.setActionListener(EditInterestActivity.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MainHttpUtil.getInterestList(callback);
            }

            @Override
            public List<InterestBean> processData(String[] info) {
                List<InterestBean> list = JSON.parseArray(Arrays.toString(info), InterestBean.class);
                if (!TextUtils.isEmpty(mInteretString)) {
                    String[] arr = mInteretString.split(";");
                    for (InterestBean bean : list) {
                        for (String s : arr) {
                            if (s.equals(bean.getName())) {
                                bean.setChecked(true);
                                break;
                            }
                        }
                    }
                }
                return list;
            }

            @Override
            public void onRefreshSuccess(List<InterestBean> list, int listCount) {
                if (mTip != null && mAdapter != null) {
                    mTip.setText(getString(R.string.edit_profile_interest_2, Integer.toString(mAdapter.getCheckedCount())));
                }
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<InterestBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mCommonRefreshView.initData();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_save) {
            save();
        }
    }

    private void save() {
        if (mAdapter == null) {
            return;
        }
        String result = mAdapter.getResult();
        if (TextUtils.isEmpty(result)) {
            ToastUtil.show(R.string.edit_profile_interest_1);
            return;
        }
        MainHttpUtil.updateUserInfo(StringUtil.contact("{\"hobby\":\"", result, "\"}"), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    EventBus.getDefault().post(new UpdateFieldEvent());
                    if (info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        String hobby = obj.getString("hobby");
                        UserBean userBean = CommonAppConfig.getInstance().getUserBean();
                        userBean.setInteret(hobby);
                        Intent intent = new Intent();
                        intent.putExtra(Constants.INTEREST, hobby);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                ToastUtil.show(msg);
            }
        });
    }

    @Override
    public void onItemSelected(int count) {
        if (mTip != null) {
            mTip.setText(getString(R.string.edit_profile_interest_2, Integer.toString(count)));
        }
    }
}
