package com.yunbao.chatroom.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.ConditionLevel;
import com.yunbao.common.bean.DataListner;
import com.yunbao.common.bean.ExportNamer;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.bean.commit.CommitEntity;
import com.yunbao.common.business.ConditionModel;
import com.yunbao.common.business.liveobsever.LifeObjectHolder;
import com.yunbao.common.custom.FlowRadioDataGroup;
import com.yunbao.common.custom.UIFactory;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.SystemUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.LiveOrderCommitBean;
import com.yunbao.chatroom.bean.SkillPriceBean;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.business.socket.SuccessListner;
import com.yunbao.chatroom.business.socket.dispatch.DispatchSocketProxy;
import com.yunbao.chatroom.business.socket.dispatch.mannger.OrderMessageMannger;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/*推送订单*/
public class LiveOrderDialog extends AbsDialogFragment implements View.OnClickListener {
    private static final int GET_SKILL = 1;

    private ViewGroup mLlevel;
    private TextView mTvCategory;
    private FlowRadioDataGroup<ConditionLevel> mFrLevel;
    private FlowRadioDataGroup<ConditionLevel> mFrSex;
    private FlowRadioDataGroup<ConditionLevel> mFrAge;
    private FlowRadioDataGroup<ConditionLevel> mFrPrice;

    private TextView mBtnReset;
    private TextView mBtnConfirm;
    private View mVLineLevel;

    private OrderMessageMannger mOrderMessageMannger;
    private LiveOrderCommitBean mLiveOrderCommitBean;
    private LayoutInflater mLayoutInflater;
    LinearLayout.LayoutParams mLayoutParams;
    private String mStream;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_order;
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
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) (SystemUtil.getWindowsPixelHeight(getActivity()) * 0.79);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void init() {
        super.init();
        mLlevel = (LinearLayout) findViewById(R.id.ll_level);
        mTvCategory = (TextView) findViewById(R.id.tv_category);
        mFrLevel = (FlowRadioDataGroup) findViewById(R.id.fr_level);
        mFrSex = (FlowRadioDataGroup) findViewById(R.id.fr_sex);
        mFrAge = (FlowRadioDataGroup) findViewById(R.id.fr_age);
        mFrPrice = (FlowRadioDataGroup) findViewById(R.id.fr_price);
        mBtnReset = (TextView) findViewById(R.id.btn_reset);
        mBtnConfirm = (TextView) findViewById(R.id.btn_confirm);
        mVLineLevel = (View) findViewById(R.id.v_line_level);
        mBtnConfirm.setOnClickListener(this);
        mBtnReset.setOnClickListener(this);
        setOnClickListener(R.id.ll_skill, this);
        mLiveOrderCommitBean = new LiveOrderCommitBean();
        mLiveOrderCommitBean.setDataListner(new DataListner() {
            @Override
            public void compelete(boolean isCompelete) {
                mBtnConfirm.setEnabled(isCompelete);
                mBtnReset.setEnabled(isCompelete);
                if (isCompelete) {
                    mBtnReset.setAlpha(1F);
                } else {
                    mBtnReset.setAlpha(0.2F);
                }
            }
        });

        FlowRadioDataGroup.RadioButtonFactory factory = initRadioButtonFactory();
        mFrLevel.setRadioButtonFactory(factory);
        mFrSex.setRadioButtonFactory(factory);
        mFrAge.setRadioButtonFactory(factory);
        mFrPrice.setRadioButtonFactory(factory);

        mFrSex.setData(Arrays.asList(ConditionModel.getSexLevel(WordUtil.getString(R.string.no_limit))));
        mFrAge.setData(Arrays.asList(ConditionModel.getAgeLevel(WordUtil.getString(R.string.no_limit))));
        getCoinList();
        judegeLevelVisible();
    }

    private void judegeLevelVisible() {
        if (ListUtil.haveData(mFrLevel.getData())) {
            mLlevel.setVisibility(View.VISIBLE);
            mVLineLevel.setVisibility(View.VISIBLE);

        } else {
            mLlevel.setVisibility(View.GONE);
            mVLineLevel.setVisibility(View.GONE);
        }
    }

    private FlowRadioDataGroup.RadioButtonFactory initRadioButtonFactory() {
        mLayoutInflater = LayoutInflater.from(getContext());
        mLayoutParams = new LinearLayout.LayoutParams(DpUtil.dp2px(70), DpUtil.dp2px(30), 1);
        mLayoutParams.leftMargin = DpUtil.dp2px(5);
        mLayoutParams.bottomMargin = DpUtil.dp2px(10);
        FlowRadioDataGroup.RadioButtonFactory factory = new FlowRadioDataGroup.RadioButtonFactory() {
            @Override
            public RadioButton createRadioButton(Context context, ExportNamer conditionLevel, ViewGroup viewGroup, ViewGroup.LayoutParams params) {
                return UIFactory.createOrderLabelRadioButton(mLayoutInflater, conditionLevel.exportName(), viewGroup, params);
            }

            @Override
            public LinearLayout.LayoutParams getLayoutParm() {
                return mLayoutParams;
            }
        };
        return factory;
    }

    private SkillBean mSelectedSkillBean;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_SKILL && resultCode == RESULT_OK) {
            mSelectedSkillBean = data.getParcelableExtra(Constants.DATA);
            setSkill(mSelectedSkillBean);
        }
    }


    /*从全部品类界面返回的数据*/
    private void setSkill(SkillBean skillBean) {
        if (skillBean != null) {
            String skillId = skillBean.getSkillId();
            mLiveOrderCommitBean.setSkillId(skillId);
            requestGameLabel(skillId);
            mTvCategory.setText(skillBean.getSkillName2());
        }
    }


    /*获取游戏标签*/
    private void requestGameLabel(String skillId) {
        CommonHttpUtil.getSkillLevel(skillId).compose(this.<List<ConditionLevel>>bindToLifecycle()).subscribe(new DefaultObserver<List<ConditionLevel>>() {
            @Override
            public void onNext(List<ConditionLevel> conditionLevels) {
                conditionLevels.add(0, ConditionModel.defaultLevel(WordUtil.getString(R.string.no_limit)));
                mFrLevel.setData(conditionLevels);
                mFrLevel.createChildByData();
                mFrLevel.checkByPosition(0);
                judegeLevelVisible();
            }
        });
    }


    /*跳转全部分类界面*/
    public void toAllSkill() {
        try {
            Class cs = Class.forName("com.yunbao.main.activity.AllSkillActivity");
            Intent intent = new Intent(getContext(), cs);
            if (mSelectedSkillBean != null) {
                intent.putExtra(Constants.SELECTED_SKILL, mSelectedSkillBean);
            }
            startActivityForResult(intent, GET_SKILL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_reset) {
            reset();
        } else if (id == R.id.btn_confirm) {
            confirm(v);
        } else if (id == R.id.ll_skill) {
            toAllSkill();
        }
    }


    private void confirm(View v) {
        if (mOrderMessageMannger == null) {
            LiveActivityLifeModel<DispatchSocketProxy> liveActivityLifeModel = LifeObjectHolder.getByContext(getActivity(), LiveActivityLifeModel.class);
            mOrderMessageMannger = liveActivityLifeModel.getSocketProxy().getOrderMessageMannger();
            mStream = liveActivityLifeModel.getLiveBean() == null ? null : liveActivityLifeModel.getLiveBean().getStream();
        }
        mOrderMessageMannger.dispatchOrder(mLiveOrderCommitBean, mStream, this, v, new SuccessListner() {
            @Override
            public void success() {
                dismiss();
            }
        });
    }

    //获取 可选择的价格列表
    private void getCoinList() {
        ChatRoomHttpUtil.getSkillPrice("0", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<SkillPriceBean> list = JSON.parseArray(Arrays.toString(info), SkillPriceBean.class);
                    int arraySize = 0;
                    if (list != null && list.size() > 0) {
                        arraySize = list.size() + 1;
                    } else {
                        arraySize = 1;
                    }
                    ConditionLevel[] conditionLevels = new ConditionLevel[arraySize];
                    conditionLevels[0] = new ConditionLevel("0", WordUtil.getString(R.string.no_limit));
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            conditionLevels[i + 1] = new ConditionLevel(list.get(i).getId(), list.get(i).getCoin() + CommonAppConfig.getInstance().getCoinName());
                        }
                    }
                    mFrPrice.setData(Arrays.asList(conditionLevels));

                    mFrSex.createChildByData();
                    mFrAge.createChildByData();
                    mFrPrice.createChildByData();

                    FlowRadioDataGroup.SelectDataChangeListner<ConditionLevel> listner = new FlowRadioDataGroup.SelectDataChangeListner<ConditionLevel>() {
                        @Override
                        public void select(View view, ConditionLevel conditionModel) {
                            String id = conditionModel == null ? CommitEntity.DEFAUlT_VALUE : conditionModel.getId();
                            if (view == mFrSex) {
                                mLiveOrderCommitBean.setSex(id);
                            } else if (view == mFrAge) {
                                mLiveOrderCommitBean.setAge(id);
                            } else if (view == mFrPrice) {
                                mLiveOrderCommitBean.setPrice(id);
                            } else if (view == mFrLevel) {
                                mLiveOrderCommitBean.setLevel(id);
                            }
                        }
                    };
                    mFrSex.setSelectDataChangeListner(listner);
                    mFrSex.checkByPosition(0);
                    mFrAge.setSelectDataChangeListner(listner);
                    mFrAge.checkByPosition(0);
                    mFrPrice.setSelectDataChangeListner(listner);
                    mFrPrice.checkByPosition(0);
                    mFrLevel.setSelectDataChangeListner(listner);
                }
            }
        });
    }


    private void reset() {
        mLiveOrderCommitBean.setSkillId(CommitEntity.DEFAUlT_VALUE);
        mSelectedSkillBean = null;
        mFrLevel.clearAllData();
        mTvCategory.setText("");
        judegeLevelVisible();
        mFrSex.checkByPosition(0);
        mFrAge.checkByPosition(0);
        mFrPrice.checkByPosition(0);
    }
}
