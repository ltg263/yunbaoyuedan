package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.SearchAdapter;
import com.yunbao.main.bean.SkillClassBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/10/25.
 */

public class SearchActivity extends AbsActivity implements View.OnClickListener, SearchAdapter.ActionListener {

    private EditText mEditText;
    private CommonRefreshView mRefreshView;
    private SearchAdapter mSearchAdapter;
    private InputMethodManager imm;
    private String mKey;
    private MyHandler mHandler;
    private int mPage;

    public static void forward(Context context) {
        context.startActivity(new Intent(context, SearchActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void main() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mEditText = (EditText) findViewById(R.id.edit);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MainHttpUtil.cancel(MainHttpConsts.SEARCH);
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (!TextUtils.isEmpty(s)) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(0, 500);
                    }
                } else {
                    mKey = null;
                    if (mSearchAdapter != null) {
                        mSearchAdapter.clearData2();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_search);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<UserBean>() {
            @Override
            public RefreshAdapter<UserBean> getAdapter() {
                if (mSearchAdapter == null) {
                    mSearchAdapter = new SearchAdapter(mContext);
                    mSearchAdapter.setActionListener(SearchActivity.this);
                }
                return mSearchAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                mPage = p;
                if (!TextUtils.isEmpty(mKey)) {
                    MainHttpUtil.search(mKey, p, callback);
                }
            }

            @Override
            public List<UserBean> processData(String[] info) {
                if (mPage == 1) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (mSearchAdapter != null) {
                        List<SkillClassBean> gameList = JSON.parseArray(obj.getString("skilllist"), SkillClassBean.class);
                        mSearchAdapter.setShowHead(true);
                        mSearchAdapter.setGameList(gameList);
                    }
                    return JSON.parseArray(obj.getString("list"), UserBean.class);
                } else {
                    return JSON.parseArray(Arrays.toString(info), UserBean.class);
                }
            }

            @Override
            public void onRefreshSuccess(List<UserBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<UserBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        mHandler = new MyHandler(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
    }

    private void search() {
        String key = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(key)) {
            ToastUtil.show(R.string.content_empty);
            return;
        }
        MainHttpUtil.cancel(MainHttpConsts.SEARCH);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mKey = key;
        mRefreshView.initData();
    }


    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.SEARCH);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.release();
        }
        mHandler = null;
        super.onDestroy();
    }


    private void clearEditText() {
        String s = mEditText.getText().toString();
        if (TextUtils.isEmpty(s)) {
            return;
        }
        mEditText.requestFocus();
        imm.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);
        mEditText.setText("");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_clear) {
            clearEditText();
        }
    }

    @Override
    public void onGameClick(SkillClassBean bean) {
        SkillUserActivity.forward(mContext, bean);
    }

    @Override
    public void onItemClick(UserBean bean) {
        if (imm != null && mEditText != null) {
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            RouteUtil.forwardUserHome(bean.getId());
        }
    }


    private static class MyHandler extends Handler {

        private SearchActivity mActivity;

        public MyHandler(SearchActivity activity) {
            mActivity = new WeakReference<>(activity).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity != null) {
                mActivity.search();
            }
        }

        public void release() {
            mActivity = null;
        }
    }


}
