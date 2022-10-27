package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.RatingBar;
import com.yunbao.common.event.OrderChangedEvent;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.KeyBoardHeightChangeListener;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.KeyBoardHeightUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2019/8/7.
 * 主播评价用户,  已废弃
 */
@Route(path = RouteUtil.MAIN_ORDER_COMMENT_ANCHOR)
public class OrderCommentActivity3 extends AbsActivity implements KeyBoardHeightChangeListener, View.OnClickListener {

    public static void forward(Context context, OrderBean orderBean) {
        Intent intent = new Intent(context, OrderCommentActivity3.class);
        intent.putExtra(Constants.ORDER_BEAN, orderBean);
        context.startActivity(intent);
    }

    private KeyBoardHeightUtil mKeyBoardHeightUtil;
    private View mRoot;
    private int mDp90;
    private ImageView mAvatar;
    private TextView mNickname;
    private TextView mTip;
    private RatingBar mRatingBar;
    private EditText mContent;
    private View mBtnSubmit;
    private String[] tipArr;
    private OrderBean mOrderBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_comment_3;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.order_comment_user));
        OrderBean orderBean = getIntent().getParcelableExtra(Constants.ORDER_BEAN);
        mOrderBean = orderBean;
        mRoot = findViewById(R.id.root);
        mDp90 = DpUtil.dp2px(90);
        mKeyBoardHeightUtil = new KeyBoardHeightUtil(mContext, findViewById(android.R.id.content), this);
        mKeyBoardHeightUtil.start();
        mAvatar = findViewById(R.id.avatar);
        mNickname = findViewById(R.id.nickname);
        mTip = findViewById(R.id.tip);
        tipArr = new String[5];
        tipArr[0] = WordUtil.getString(R.string.order_comment_tip_5);
        tipArr[1] = WordUtil.getString(R.string.order_comment_tip_6);
        tipArr[2] = WordUtil.getString(R.string.order_comment_tip_2);
        tipArr[3] = WordUtil.getString(R.string.order_comment_tip_7);
        tipArr[4] = WordUtil.getString(R.string.order_comment_tip_8);
        mRatingBar = findViewById(R.id.rating_bar);
        mRatingBar.setOnRatingChangedListener(new RatingBar.OnRatingChangedListener() {
            @Override
            public void onRatingChanged(int curCount, int maxCount) {

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
        mBtnSubmit = findViewById(R.id.btn_submit);
        mBtnSubmit.setOnClickListener(this);
        mContent = findViewById(R.id.content);

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
                mNickname.setText(u.getUserNiceName());
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
        String content = mContent.getText().toString().trim();
        String star = String.valueOf(mRatingBar.getFillCount());
        MainHttpUtil.orderCommentUser(mOrderBean.getId(), content, star, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    EventBus.getDefault().post(new OrderChangedEvent(mOrderBean.getId()));
                    finish();
                }
                ToastUtil.show(msg);
            }
        });

    }
}
