package com.yunbao.main.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.main.R;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.main.bean.SkillPriceBean;
import com.yunbao.main.http.MainHttpUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.qqtheme.framework.widget.WheelView;

/**
 * Created by cxf on 2019/4/17.
 */

public class SkillPriceDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private WheelView mWheelView;
    private TextView mPrice;
    private int mMaxCanUseIndex;
    private ActionListener mActionListener;
    private SkillBean mSkillBean;
    private String mNowPrice;
    private List<SkillPriceBean> mPriceList;
    private String mCoinName;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_skill_price;
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
        params.height = DpUtil.dp2px(200);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPrice = (TextView) findViewById(R.id.price);
        findViewById(R.id.btn_price_tip).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        mWheelView = (WheelView) findViewById(R.id.wheelView);
        mWheelView.setTextSize(20);
        mWheelView.setTextColor(0xff969696, 0xff323232);
        mWheelView.setCycleDisable(true);//禁用循环
        mWheelView.setGravity(Gravity.CENTER);
        mWheelView.setVisibleItemCount(5);
        WheelView.DividerConfig config = new WheelView.DividerConfig();
        config.setColor(0xffdcdcdc);//线颜色
        config.setRatio(0.8f);//线比率
        mWheelView.setDividerConfig(config);
        mWheelView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int i) {
                if (mPriceList != null && mWheelView != null && mSkillBean != null) {
                    SkillPriceBean bean = mPriceList.get(i);
                    if (!bean.isCanUse()) {
                        mWheelView.setSelectedIndex(mMaxCanUseIndex);
                        bean = mPriceList.get(mMaxCanUseIndex);
                    }
                    if (mPrice != null) {
                        mPrice.setText(StringUtil.contact(bean.getCoin(), mCoinName, "/", mSkillBean.getUnit()));
                    }
                }
            }
        });

        if (mSkillBean != null) {
            mPrice.setText(mSkillBean.getPirceResult(mCoinName));
            MainHttpUtil.getSkillPrice(mSkillBean.getSkillId(), new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        List<SkillPriceBean> list = JSON.parseArray(Arrays.toString(info), SkillPriceBean.class);
                        mPriceList = list;
                        List<String> stringList = new ArrayList<>();
                        int selectedIndex = 0;
                        for (int i = 0, size = list.size(); i < size; i++) {
                            SkillPriceBean bean = list.get(i);
                            if (bean.isCanUse()) {
                                mMaxCanUseIndex = i;
                            }
                            String coin = bean.getCoin();
                            if (!TextUtils.isEmpty(mNowPrice) && mNowPrice.equals(coin)) {
                                selectedIndex = i;
                            }
                            stringList.add(coin);
                        }
                        if (mWheelView != null) {
                            mWheelView.setItems(stringList);
                            mWheelView.setSelectedIndex(selectedIndex);
                            FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(DpUtil.dp2px(150), FrameLayout.LayoutParams.WRAP_CONTENT);
                            param.gravity = Gravity.CENTER;
                            param.topMargin = -DpUtil.dp2px(10);
                            mWheelView.setLayoutParams(param);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_price_tip) {
            tipClick();
        } else if (i == R.id.btn_close) {
            dismiss();
        } else if (i == R.id.btn_confirm) {
            confirmClick();
        }
    }


    private void tipClick() {
        if(mSkillBean==null){
            return;
        }
        SkillPriceTipDialogFragment fragment = new SkillPriceTipDialogFragment();
        fragment.setSkillId(mSkillBean.getSkillId());
        fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "SkillPriceTipDialogFragment");
    }

    private void confirmClick() {
        if (mWheelView == null || TextUtils.isEmpty(mNowPrice)) {
            return;
        }
        int index = mWheelView.getSelectedIndex();
        SkillPriceBean bean = mPriceList.get(index);
        if (!bean.isCanUse()) {
            ToastUtil.show(R.string.main_price_tip_3);
            return;
        }
        if (!mNowPrice.equals(bean.getCoin()) && mActionListener != null) {
            mActionListener.onPriceSelected(bean);
        }
        dismiss();
    }

    @Override
    public void onDestroy() {
        mActionListener = null;
        super.onDestroy();
    }

    public interface ActionListener {
        void onPriceSelected(SkillPriceBean bean);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }


    public void setSkillBean(String coinName, SkillBean skillBean) {
        mCoinName = coinName;
        mSkillBean = skillBean;
        mNowPrice = skillBean.getSkillPrice();
    }

}
