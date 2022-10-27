package com.yunbao.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.youth.banner.loader.ImageLoader;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.radio.CheckEntity;
import com.yunbao.common.adapter.radio.IRadioChecker;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.common.bean.Reason;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.custom.DrawableTextView;
import com.yunbao.common.dialog.SelectDialogFragment;
import com.yunbao.common.event.OrderChangedEvent;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class RefundApplyActivity extends AbsActivity implements View.OnClickListener {
    public static final int REFUND = 1;

    private final int mMaxLength = 30;
    private int mCurrentLength;
    private RoundedImageView mImgAvatar;
    private TextView mTvSkillName;
    private TextView mTvSkillPrice;
    private TextView mTvRefundPrice;
    private EditText mTvContent;
    private TextView mTvLengthShow;
    private Button mBtnConfirm;
    private TextView mTvReason;
    private Reason mReason;
    private String mCoinName;

    private OrderBean mOrderBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_refund_apply;
    }

    @Override
    protected void main() {
        super.main();
        setTitleById(R.string.refund);
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mImgAvatar = (RoundedImageView) findViewById(R.id.img_avatar);
        mTvSkillName = (TextView) findViewById(R.id.tv_skill_name);
        mTvSkillPrice = (TextView) findViewById(R.id.tv_skill_price);
        mTvRefundPrice = (TextView) findViewById(R.id.tv_refund_price);
        mTvContent = (EditText) findViewById(R.id.tv_content);
        mTvLengthShow = (TextView) findViewById(R.id.tv_length_show);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mTvReason = findViewById(R.id.tv_reason);
        mOrderBean = getIntent().getParcelableExtra(Constants.DATA);
        if (mOrderBean == null) {
            finish();
            return;
        }
        SkillBean skillBean = mOrderBean.getSkillBean();
        if (skillBean != null) {
            ImgLoader.display(this, skillBean.getAuthThumb(), mImgAvatar);
            mTvSkillName.setText(skillBean.getSkillName());
            mTvSkillPrice.setText(skillBean.getSkillPrice() + mCoinName);
        }
        mTvRefundPrice.setText(mOrderBean.getTotal() + mCoinName);

        //手动设置maxLength为20
        InputFilter[] filters = {new InputFilter.LengthFilter(mMaxLength)};
        mTvContent.setFilters(filters);
        setCurrentLength();
        mTvContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mCurrentLength = s.length();
                setCurrentLength();
            }
        });
        findViewById(R.id.btn_reason).setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
    }

    private void setCurrentLength() {
        mTvLengthShow.setText(getString(R.string.refund_content_text_max_length, Integer.toString(mCurrentLength), Integer.toString(mMaxLength)));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_reason) {
            showReasonDialog();
        } else if (id == R.id.btn_confirm) {
            confirm();
        }
    }

    private void confirm() {
        if (mOrderBean == null || !ClickUtil.canClick()) {
            return;
        }
        String des = mReason.getContent();
        String content = mTvContent.getText().toString();
        if (!TextUtils.isEmpty(des)) {
            content = des + "\t" + content;
        }
        MainHttpUtil.setRefund(mOrderBean.getId(), mOrderBean.getLiveUid(), content).
                compose(this.<Boolean>bindToLifecycle()).subscribe(new DefaultObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    EventBus.getDefault().post(new OrderChangedEvent(mOrderBean.getId()));
                    finish();
                }
            }
        });
    }

    private List<Reason> mData;

    private void showReasonDialog() {
        if (!ClickUtil.canClick()) {
            return;
        }
        if (mData == null) {
            MainHttpUtil.getRefundList().compose(this.<List<Reason>>bindToLifecycle()).subscribe(new DefaultObserver<List<Reason>>() {
                @Override
                public void onNext(List<Reason> list) {
                    mData = list;
                    showReasonDialog(mData);
                }
            });
        } else {
            showReasonDialog(mData);
        }
    }

    private void showReasonDialog(List<Reason> data) {
        SelectDialogFragment<Reason> selectDialogFragment = new SelectDialogFragment();
        selectDialogFragment.setOnSelectListner(new SelectDialogFragment.OnSelectListner<Reason>() {
            @Override
            public void onSelect(Reason checker) {
                mReason = checker;
                if (mReason != null) {
                    mTvReason.setText(mReason.getContent());
                }
                mBtnConfirm.setEnabled(mReason != null);
            }
        });
        selectDialogFragment.setNoSelectTip(getString(R.string.please_selct_refund_money_reason));
        if (mReason != null) {
            selectDialogFragment.setSelect(mReason.getId());
        }
        selectDialogFragment.setList(data);
        selectDialogFragment.show(getSupportFragmentManager());
    }


    public static void forward(Activity context, OrderBean orderBean) {
        Intent intent = new Intent(context, RefundApplyActivity.class);
        intent.putExtra(Constants.DATA, orderBean);
        context.startActivity(intent);
    }


}
