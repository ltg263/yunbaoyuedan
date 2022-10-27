package com.yunbao.main.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.activity.ChatRoomActivity;
import com.yunbao.main.R;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.common.bean.OrderCommentBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.main.custom.StarCountView;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

/**
 * Created by cxf on 2019/8/7.
 * 订单评价
 */

public class OrderCommentActivity2 extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context, String orderId, boolean isMyAchor) {
        Intent intent = new Intent(context, OrderCommentActivity2.class);
        intent.putExtra(Constants.ORDER_ID, orderId);
        intent.putExtra(Constants.ORDER_ANCHOR, isMyAchor);
        context.startActivity(intent);
    }

    private ImageView mAvatar;
    private TextView mNickname;
    private TextView mTotal;
    private TextView mCoinName;
    private TextView mSkillName;
    private ImageView mSkillThumb;
    private TextView mServiceTime;
    private StarCountView mStar;
    private TextView mContent;
    private String mOrderId;
    private OrderBean mOrderBean;
    private boolean mIsMyAchor;
    private TextView tv_order_num;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_comment_2;
    }

    @Override
    protected void main() {
        Intent intent = getIntent();
        mOrderId = intent.getStringExtra(Constants.ORDER_ID);
        mIsMyAchor = intent.getBooleanExtra(Constants.ORDER_ANCHOR, false);
        setTitle(WordUtil.getString(mIsMyAchor ? R.string.order_get_detail : R.string.order_detail));
        TextView centerTitleTV = findViewById(R.id.tv_center_title);
        centerTitleTV.setText(WordUtil.getString(mIsMyAchor ? R.string.order_get_detail : R.string.order_detail));
        mAvatar = findViewById(R.id.avatar);
        tv_order_num = findViewById(R.id.tv_order_num);
        tv_order_num.setOnClickListener(this);
        mNickname = findViewById(R.id.nickname);
        mTotal = findViewById(R.id.total);
        mCoinName = findViewById(R.id.coin_name);
        mSkillThumb = findViewById(R.id.skill_thumb);
        mSkillName = findViewById(R.id.skill_name);
        mServiceTime = findViewById(R.id.service_time);
        mStar = findViewById(R.id.star);
        mContent = findViewById(R.id.content);
        TextView btnChat = findViewById(R.id.btn_chat);
        btnChat.setOnClickListener(this);
        btnChat.setText(mIsMyAchor ? R.string.order_comment_chat_2 : R.string.order_comment_chat);
        getData();
    }


    private void getData() {
        MainHttpUtil.getOrderDetail(mOrderId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    OrderBean orderBean = JSON.parseObject(info[0], OrderBean.class);
                    mOrderBean = orderBean;
                    UserBean u = orderBean.getLiveUserInfo();
                    if (u != null) {
                        if (mAvatar != null) {
                            ImgLoader.display(mContext, u.getAvatar(), mAvatar);
                        }
                        if (mNickname != null) {
                            mNickname.setText(u.getUserNiceName());
                        }
                    }
                    if (tv_order_num != null){
                        tv_order_num.setText(mOrderBean.getOrderNo());
                    }
                    if (mTotal != null) {
                        mTotal.setText(mIsMyAchor ? orderBean.getProfit() : orderBean.getTotal());
                    }
                    if (mCoinName != null) {
                        mCoinName.setText(CommonAppConfig.getInstance().getCoinName());
                    }
                    SkillBean skillBean = orderBean.getSkillBean();
                    if (skillBean != null) {
                        if (mSkillThumb != null) {
                            ImgLoader.display(mContext, skillBean.getSkillThumb(), mSkillThumb);
                        }
                        if (mSkillName != null) {
                            mSkillName.setText(skillBean.getSkillName());
                        }
                        if (mServiceTime != null) {
                            mServiceTime.setText(StringUtil.contact(orderBean.getServiceTime(), " ", orderBean.getOrderNum(), "*", skillBean.getUnit()));
                        }
                    }

                    if (mIsMyAchor) {
                        if (orderBean.hasEvaluate()) {
                            OrderCommentBean evaluate = orderBean.getEvaluate();
                            if (evaluate != null) {
                                findViewById(R.id.comment_group).setVisibility(View.VISIBLE);
                                if (mStar != null) {
                                    mStar.setFillCount(evaluate.getStar());
                                }
                                if (mContent != null) {
                                    mContent.setText(evaluate.getContent());
                                }
                            }
                        } else {
                            if (mIsMyAchor){
                                //主播不可评价用户
                                return;
                            }
                            View btnToComment = findViewById(R.id.btn_to_comment);
                            if (btnToComment != null) {
                                btnToComment.setVisibility(View.VISIBLE);
                                btnToComment.setOnClickListener(OrderCommentActivity2.this);
                            }
                        }
                    } else {
                        if (orderBean.hasComment()) {
                            OrderCommentBean commentBean = orderBean.getCommentBean();
                            if (commentBean != null) {
                                findViewById(R.id.comment_group).setVisibility(View.VISIBLE);
                                if (mStar != null) {
                                    mStar.setFillCount(commentBean.getStar());
                                }
                                String[] labels = commentBean.getLables();
                                if (labels != null && labels.length > 0) {
                                    findViewById(R.id.label_group).setVisibility(View.VISIBLE);
                                    TextView[] labelArr = new TextView[3];
                                    labelArr[0] = findViewById(R.id.skill_label_0);
                                    labelArr[1] = findViewById(R.id.skill_label_1);
                                    labelArr[2] = findViewById(R.id.skill_label_2);
                                    for (int i = 0, len = Math.min(labels.length, labelArr.length); i < len; i++) {
                                        if (labelArr[i] != null) {
                                            labelArr[i].setVisibility(View.VISIBLE);
                                            labelArr[i].setText(labels[i]);
                                        }
                                    }
                                }
                                if (mContent != null) {
                                    mContent.setText(commentBean.getContent());
                                }
                            }
                        } else {
                            if (mIsMyAchor){
                                //主播不可评价用户
                                return;
                            }
                            View btnToComment = findViewById(R.id.btn_to_comment);
                            if (btnToComment != null) {
                                btnToComment.setVisibility(View.VISIBLE);
                                btnToComment.setOnClickListener(OrderCommentActivity2.this);
                            }
                        }
                    }
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_ORDER_DETAIL);
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_chat) {
            chatClick();
        } else if (i == R.id.btn_to_comment) {
            if (mOrderBean != null) {
                if (mIsMyAchor) {
//                    OrderCommentActivity3.forward(mContext, mOrderBean);
                } else {
                    OrderCommentActivity.forward(mContext, mOrderBean);
                }
                finish();
            }
        }else if (i == R.id.tv_order_num){
            StringUtil.copyText(mContext,tv_order_num.getText().toString().trim());
        }
    }


    /**
     * 私信
     */
    private void chatClick() {
        if (mOrderBean == null) {
            return;
        }
        UserBean u = mOrderBean.getLiveUserInfo();
        if (u == null) {
            return;
        }
        ChatRoomActivity.forward(mContext, u, u.getIsFollow() == 1, false, true, false);
    }

}
