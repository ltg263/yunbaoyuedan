package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.custom.CommonRefreshView;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.LetterIndexAdapter;
import com.yunbao.main.adapter.PhoneCountryCodeAdapter;
import com.yunbao.main.adapter.PhoneCountryCodeSearchAdapter;
import com.yunbao.main.bean.CountryCodeBean;
import com.yunbao.main.bean.CountryCodeParentBean;
import com.yunbao.main.http.MainHttpUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sky.L on 2020-12-18
 */
public class ChoosePhoneCountryCodeActivity extends AbsActivity implements OnItemClickListener<CountryCodeBean>{
    public static final int INTENT_REQUEST_CODE = 1002;
    private RecyclerView rlv_index;
    private LetterIndexAdapter mIndexAdapter;

    private SmartRefreshLayout mSmartRefreshLayout;
    private RecyclerView mRecyclerView;
    private PhoneCountryCodeAdapter mCodeAdapter;
    private List<CountryCodeBean> mCountryCodeList;


    private CommonRefreshView mRefreshView2;
    private PhoneCountryCodeSearchAdapter mSearchAdapter;
    private EditText mEditText;
    private View mSearchGroup;
    private Handler mHandler;
    private InputMethodManager imm;

    private View v_empty;
    
    private List<CountryCodeParentBean> mCodeParentList;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose_phone_country_code;
    }

    @Override
    protected void main() {
        super.main();
        initView();
        initData();
    }

    private void initView(){
        setTitle(WordUtil.getString(R.string.phone_num_country_code_1));
        mCountryCodeList = new ArrayList<>();
        mSearchGroup = findViewById(R.id.search_group);
        v_empty = findViewById(R.id.v_empty);
        mSmartRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mSmartRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);//是否在列表不满一页时候开启上拉加载功能
        mSmartRefreshLayout.setEnableFooterFollowWhenLoadFinished(true);//是否在全部加载结束之后Footer跟随内容
        mSmartRefreshLayout.setEnableOverScrollBounce(false);//设置是否开启越界回弹功能（默认true）
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                initData();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        //监听滑动，确保第一条在首位
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mShouldScroll) {
                    mShouldScroll = false;
                    if (mToPosition != -1) {
                        smoothMoveToPosition(mRecyclerView, mToPosition);
                    } else {
                        smoothMoveToPosition(mRecyclerView, mToPosition + 1);
                    }
                }
            }
        });
        rlv_index = findViewById(R.id.rlv_index);
        rlv_index.setHasFixedSize(true);
        rlv_index.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mIndexAdapter = new LetterIndexAdapter(mContext);
        mIndexAdapter.setOnItemClickListener(new OnItemClickListener<String>() {
            @Override
            public void onItemClick(String letter, int position) {
                forwardLetterPosition(letter, position);
            }
        });
        rlv_index.setAdapter(mIndexAdapter);
        mRefreshView2 = findViewById(R.id.refreshView2);
        mRefreshView2.setEmptyLayoutId(R.layout.view_no_data_default);
        //   mRefreshView2.setEmptyLayoutId(R.layout.view_empty_search);
        mRefreshView2.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView2.setDataHelper(new CommonRefreshView.DataHelper<CountryCodeBean>() {
            @Override
            public RefreshAdapter<CountryCodeBean> getAdapter() {
                if (mSearchAdapter == null) {
                    mSearchAdapter = new PhoneCountryCodeSearchAdapter(mContext);
                    mSearchAdapter.setOnItemClickListener(ChoosePhoneCountryCodeActivity.this);
                }
                return mSearchAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                search(callback);
            }

            @Override
            public List<CountryCodeBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), CountryCodeBean.class);
            }

            @Override
            public void onRefreshSuccess(List<CountryCodeBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<CountryCodeBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                doSearch();
            }
        };
        mEditText = findViewById(R.id.edit);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                    if (mHandler != null) {
                        mHandler.removeMessages(0);
                    }
                    doSearch();
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
                if (mHandler != null) {
                    mHandler.removeMessages(0);
                    if (!TextUtils.isEmpty(s)) {
                        mHandler.sendEmptyMessageDelayed(0, 500);
                    } else {
                        if (mSearchGroup != null && mSearchGroup.getVisibility() == View.VISIBLE) {
                            mSearchGroup.setVisibility(View.INVISIBLE);
                        }
                        if (mSearchAdapter != null) {
                            mSearchAdapter.clearData();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mEditText.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                mEditText.requestFocus();
                imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
                mEditText.setText("");
            }
        });
    }

    private void initData(){
        MainHttpUtil.getCountryCode("", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mCountryCodeList != null) {
                        mCountryCodeList.clear();
                    }
                    if (mCodeParentList != null) {
                        mCodeParentList.clear();
                    }
                    mCodeParentList = JSON.parseArray(Arrays.toString(info), CountryCodeParentBean.class);
                    for (CountryCodeParentBean letterBean : mCodeParentList) {
                        if (letterBean != null) {
                            CountryCodeBean titleBean = new CountryCodeBean();
                            titleBean.setName(letterBean.getTitle());
                            titleBean.setNameEn(letterBean.getTitle());
                            titleBean.setTitle(true);
                            mCountryCodeList.add(titleBean);
                            mCountryCodeList.addAll(letterBean.getLists());
                        }
                    }
                    if (mCountryCodeList != null && mCountryCodeList.size() > 0 ){
                        if (v_empty.getVisibility() == View.VISIBLE){
                            v_empty.setVisibility(View.INVISIBLE);
                        }
                    }else {
                        v_empty.setVisibility(View.VISIBLE);
                    }
                    if (mCodeAdapter == null) {
                        mCodeAdapter = new PhoneCountryCodeAdapter(mContext, mCountryCodeList);
                        mCodeAdapter.setOnItemClickListener(new OnItemClickListener<CountryCodeBean>() {
                            @Override
                            public void onItemClick(CountryCodeBean bean, int position) {
                                selectComplete(bean.getTel());
                            }
                        });
                        mRecyclerView.setAdapter(mCodeAdapter);
                    }
                }
            }

            @Override
            public void onFinish() {
                if (mSmartRefreshLayout != null) {
                    mSmartRefreshLayout.finishRefresh(true);
                }
            }
        });
    }

    private void search(HttpCallback callback) {
        String key = mEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(key)) {
            MainHttpUtil.getCountryCode(key, callback);
        } else {
            ToastUtil.show(WordUtil.getString(R.string.content_empty));
        }
    }

    private void doSearch() {
        if (mSearchGroup != null && mSearchGroup.getVisibility() != View.VISIBLE) {
            mSearchGroup.setVisibility(View.VISIBLE);
        }
        if (mRefreshView2 != null) {
            mRefreshView2.initData();
        }
    }


    /**
     * 跳转指定字母开头的用户
     *
     * @param letter
     * @param position
     */
    private void forwardLetterPosition(String letter, int position) {
        int count = 0;
        if (mCodeParentList != null && mRecyclerView != null) {
            for (CountryCodeParentBean bean : mCodeParentList) {
                if (letter.equals(bean.getTitle())) {
                    if (mCountryCodeList.size() > count) {
                        ToastUtil.show(letter);
                        smoothMoveToPosition(mRecyclerView, count);
                    }
                } else {
                    if (bean.getLists() != null) {
                        count = count + bean.getLists().size()+1;
                    }
                }
            }
        }
    }

    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;

    /**
     * 滑动到指定位置
     */
    private LinearLayoutManager mLinearLayoutManager;
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        if (mLinearLayoutManager == null){
            mLinearLayoutManager  = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        }
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前
            mLinearLayoutManager.scrollToPositionWithOffset(position,0);
//            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
//                mRecyclerView.smoothScrollBy(0, top);
                mLinearLayoutManager.scrollToPositionWithOffset(position,0);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
//            mRecyclerView.smoothScrollToPosition(position);
            mLinearLayoutManager.scrollToPositionWithOffset(position,0);
            mToPosition = position;
            mShouldScroll = true;
        }
    }


    //点击搜索出的条目
    @Override
    public void onItemClick(CountryCodeBean bean, int position) {
        selectComplete(bean.getTel());
    }

    private void selectComplete(String code){
        Intent intent = new Intent(mContext,LoginActivity.class);
        intent.putExtra(Constants.INTENT_PHONE_COUNTRY_CODE,code);
        setResult(RESULT_OK,intent);
        finish();
    }
}
