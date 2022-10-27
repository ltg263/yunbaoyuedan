package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.custom.RatingBar;
import com.yunbao.common.event.OrderChangedEvent;
import com.yunbao.common.event.OrderEvaluateCompleteEvent;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.KeyBoardHeightChangeListener;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.KeyBoardHeightUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.SkillLabelAdapter;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.main.bean.SkillLabelBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2019/8/7.
 * 用户评价主播
 */
@Route(path = RouteUtil.MAIN_ORDER_COMMENT)
public class OrderCommentActivity extends AbsActivity implements KeyBoardHeightChangeListener, View.OnClickListener {

    public static void forward(Context context, OrderBean orderBean) {
        Intent intent = new Intent(context, OrderCommentActivity.class);
        intent.putExtra(Constants.ORDER_BEAN, orderBean);
        context.startActivity(intent);
    }

    private KeyBoardHeightUtil mKeyBoardHeightUtil;
    private View mRoot;
    private int mDp90;
    private ImageView mAvatar;
    private ImageView mSkillThumb;
    private TextView mSkillName;
    private TextView mServiceTime;
    private TextView mPrice;
    private TextView mScore;
    private TextView mTip;
    private RatingBar mRatingBar;
    private RecyclerView mRecyclerView;
    private OrderBean mOrderBean;
    private SkillLabelAdapter mAdapter;
    private EditText mContent;
    private TextView mCount;//字数
    private View mBtnSubmit;
    private String[] tipArr;
    private String mFen;//分

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_comment;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.order_comment));
        mOrderBean = getIntent().getParcelableExtra(Constants.ORDER_BEAN);
        mRoot = findViewById(R.id.root);
        mDp90 = DpUtil.dp2px(90);
        mKeyBoardHeightUtil = new KeyBoardHeightUtil(mContext, findViewById(android.R.id.content), this);
        mKeyBoardHeightUtil.start();
        mAvatar = findViewById(R.id.avatar);
        mSkillThumb = findViewById(R.id.skill_thumb);
        mSkillName = findViewById(R.id.skill_name);
        mServiceTime = findViewById(R.id.service_time);
        mPrice = findViewById(R.id.price);
        mScore = findViewById(R.id.score);
        mTip = findViewById(R.id.tip);
        tipArr = new String[5];
        tipArr[0] = WordUtil.getString(R.string.order_comment_tip_0);
        tipArr[1] = WordUtil.getString(R.string.order_comment_tip_1);
        tipArr[2] = WordUtil.getString(R.string.order_comment_tip_2);
        tipArr[3] = WordUtil.getString(R.string.order_comment_tip_3);
        tipArr[4] = WordUtil.getString(R.string.order_comment_tip_4);
        mFen = WordUtil.getString(R.string.order_comment_fen);
        mRatingBar = findViewById(R.id.rating_bar);
        mRatingBar.setOnRatingChangedListener(new RatingBar.OnRatingChangedListener() {
            @Override
            public void onRatingChanged(int curCount, int maxCount) {
                if (mScore != null) {
                    mScore.setText(StringUtil.contact(String.valueOf(curCount), mFen));
                }

                int index = curCount - 1;
                if (index < 0) {
                    if (mTip != null) {
                        mTip.setText("");
                    }
                } else {
                    if (mTip != null && tipArr != null) {
                        mTip.setText(tipArr[index]);
                    }
                }
                if (mBtnSubmit != null) {
                    mBtnSubmit.setEnabled(index >= 0);
                }

            }
        });
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 10, 10);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);
        mBtnSubmit = findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(this);
        mCount = findViewById(R.id.count);
        mContent = findViewById(R.id.content);
        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mCount != null) {
                    mCount.setText(StringUtil.contact(String.valueOf(s.length()), "/100"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (mOrderBean == null) {
            String orderId = getIntent().getStringExtra(Constants.ORDER_ID);
            MainHttpUtil.getOrderDetail(orderId, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        OrderBean orderBean = JSON.parseObject(info[0], OrderBean.class);
                        mOrderBean = orderBean;
                        showData();
                    }
                }
            });
        } else {
            showData();
        }

    }


    private void showData() {
        if (mOrderBean != null) {
            UserBean u = mOrderBean.getLiveUserInfo();
            if (u != null) {
                ImgLoader.display(mContext, u.getAvatar(), mAvatar);
            }
            String coinName = CommonAppConfig.getInstance().getCoinName();
            mPrice.setText(StringUtil.contact(WordUtil.getString(R.string.pay), mOrderBean.getTotal(), coinName));
            SkillBean skillBean = mOrderBean.getSkillBean();
            if (skillBean != null) {
                ImgLoader.display(mContext, skillBean.getSkillThumb(), mSkillThumb);
                mSkillName.setText(skillBean.getSkillName());
                mServiceTime.setText(StringUtil.contact(mOrderBean.getServiceTime(), " ", mOrderBean.getOrderNum(), "*", skillBean.getUnit()));

                MainHttpUtil.getSkillLabel(skillBean.getId(), new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && mRecyclerView != null) {
                            List<SkillLabelBean> list = JSON.parseArray(Arrays.toString(info), SkillLabelBean.class);
                            mAdapter = new SkillLabelAdapter(mContext, list);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    }
                });
            }
        }
    }


    @Override
    protected void onDestroy() {
        if (mKeyBoardHeightUtil != null) {
            mKeyBoardHeightUtil.release();
        }
        mKeyBoardHeightUtil = null;
        MainHttpUtil.cancel(MainHttpConsts.GET_SKILL_LABEL);
        MainHttpUtil.cancel(MainHttpConsts.ORDER_SET_COMMENT);
        super.onDestroy();
    }

    @Override
    public void onKeyBoardHeightChanged(int visibleHeight, int keyboardHeight) {
        if (mRoot != null) {
            if (keyboardHeight > 0) {
                mRoot.setTranslationY(mDp90 - keyboardHeight);
            } else {
                mRoot.setTranslationY(0);
            }
        }
    }

    @Override
    public boolean isSoftInputShowed() {
        return false;
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_submit) {
            submit();
        }
    }

    /**
     * 提交评价
     */
    private void submit() {
        if (mOrderBean == null) {
            return;
        }
        StringBuilder sb = null;
        if (mAdapter != null) {
            List<SkillLabelBean> list = mAdapter.getCheckedList();
            if (list != null) {
                for (SkillLabelBean bean : list) {
                    if (sb == null) {
                        sb = new StringBuilder();
                    }
                    sb.append(bean.getId());
                    sb.append(",");
                }
            }
        }
        String labels = sb != null ? sb.toString() : "";
        String content = mContent.getText().toString();
        String star = String.valueOf(mRatingBar.getFillCount());
        MainHttpUtil.orderSetComment(mOrderBean.getId(), content, star, labels, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    EventBus.getDefault().post(new OrderChangedEvent(mOrderBean.getId()));
//                    EventBus.getDefault().post(new OrderEvaluateCompleteEvent());
                    finish();
                }
                ToastUtil.show(msg);
            }
        });

    }
}
