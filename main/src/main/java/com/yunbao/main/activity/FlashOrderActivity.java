package com.yunbao.main.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.commit.CommitEntity;
import com.yunbao.common.bean.DataListner;
import com.yunbao.common.bean.ConditionLevel;
import com.yunbao.common.bean.ExportNamer;
import com.yunbao.common.custom.FlowRadioDataGroup;
import com.yunbao.common.custom.UIFactory;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.server.observer.DialogObserver;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.JsonUtil;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.main.bean.commit.FlashOrderCommitBean;
import com.yunbao.main.business.ConditionModel;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;
import com.yunbao.main.utils.CityUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;

public class FlashOrderActivity extends AbsActivity {

    private LinearLayout llCategory;
    private TextView tvCategory;
    private LinearLayout llLevel;
    private LinearLayout llSex;
    private FlowRadioDataGroup<ConditionLevel> frLevel;
    private FlowRadioDataGroup<ConditionLevel> frSex;
    private LinearLayout llOrderTime;
    private TextView tvOrderTime;
    private TextView btnDecrease;
    private TextView orderNum1;
    private TextView btnIncrease;
    private EditText tvDes;
    private TextView tvNum;
    public static final int TEXT_MAX_LENGTH=50;
    private TextView btnOrder;

    private ArrayList<Province> mTimeList;
    private String mTimeVal;
    private String mTimeType;

    private FlashOrderCommitBean flashOrderCommitBean;
    private LayoutInflater layoutInflater;
    LinearLayout.LayoutParams layoutParams;

    //已经选择的技能
    private SkillBean mSelectedSkillBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_flash_order;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void main() {
        super.main();
        setTitleById(R.string.flash_order);
        llCategory = (LinearLayout) findViewById(R.id.ll_category);
        tvCategory = (TextView) findViewById(R.id.tv_category);
        llLevel = (LinearLayout) findViewById(R.id.ll_level);
        frLevel = (FlowRadioDataGroup) findViewById(R.id.fr_level);
        llSex = (LinearLayout) findViewById(R.id.ll_sex);
        frSex = (FlowRadioDataGroup) findViewById(R.id.fr_sex);
        llOrderTime = (LinearLayout) findViewById(R.id.ll_order_time);
        tvOrderTime = (TextView) findViewById(R.id.tv_order_time);
        btnDecrease = (TextView) findViewById(R.id.btn_decrease);
        orderNum1 = (TextView) findViewById(R.id.order_num_1);
        btnIncrease = (TextView) findViewById(R.id.btn_increase);
        tvDes = (EditText) findViewById(R.id.tv_des);
        btnOrder = (TextView) findViewById(R.id.btn_order);
        tvNum = (TextView) findViewById(R.id.tv_num);
        tvDes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == TEXT_MAX_LENGTH){
                    //ToastUtil.show(WordUtil.getString(R.string.text_length_too_large));
                }
                setCurrentTextLength(s.length());
            }
        });
        setCurrentTextLength(0);

        layoutInflater=LayoutInflater.from(this);
        layoutParams =new LinearLayout.LayoutParams(DpUtil.dp2px(70),DpUtil.dp2px(30),1);
        layoutParams.leftMargin=DpUtil.dp2px(5);
        layoutParams.bottomMargin=DpUtil.dp2px(10);

        FlowRadioDataGroup.RadioButtonFactory factory=  new FlowRadioDataGroup.RadioButtonFactory() {
            @Override
            public RadioButton createRadioButton(Context context, ExportNamer conditionLevel, ViewGroup viewGroup, ViewGroup.LayoutParams params) {
                return UIFactory.createOrderLabelRadioButton(layoutInflater,conditionLevel.exportName(),viewGroup,params);
            }
            @Override
            public LinearLayout.LayoutParams getLayoutParm() {
                return layoutParams;
            }
        };

        frLevel.setRadioButtonFactory(factory);
        frLevel.setSelectDataChangeListner(new FlowRadioDataGroup.SelectDataChangeListner<ConditionLevel>() {
            @Override
            public void select(View view,ConditionLevel conditionLevel) {
                if(conditionLevel!=null){
                    getCommitData().setLevel(conditionLevel.getId());
                }else{
                    getCommitData().setLevel(CommitEntity.DEFAUlT_VALUE);
                }
            }
        });

        frSex.setRadioButtonFactory(factory);
        frSex.setSelectDataChangeListner(new FlowRadioDataGroup.SelectDataChangeListner<ConditionLevel>() {
            @Override
            public void select(View view,ConditionLevel conditionLevel) {
                if(conditionLevel!=null){
                 getCommitData().setSex(conditionLevel.getId());
                }else{
                 getCommitData().setSex(CommitEntity.DEFAUlT_VALUE);
                }
            }
        });
        defaultSetting();
        frSex.setData(Arrays.asList(ConditionModel.getSexLevel(WordUtil.getString(R.string.all))));
        frSex.createChildByData();
    }

    /*setDefaultSelectPosition一定要在createChildByData之前调用*/
    private void defaultSetting() {
        numChange(1);
        frSex.setDefaultSelectPosition(0);
        frLevel.setDefaultSelectPosition(0);
        MainHttpUtil.getHome(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(code==0&&info.length>0){
                 List<SkillBean> skillBeanList = JSON.parseArray(JsonUtil.getString(info[0],"skilllist"), SkillBean.class);
                 if(ListUtil.haveData(skillBeanList)){
                    setSkill(skillBeanList.get(0));
                 }
                }
            }
        });
    }

    private void setCurrentTextLength(int length) {
        tvNum.setText(length+"/"+TEXT_MAX_LENGTH);
    }

    public void selTime(View view){
        chooseTime();
    }
    public void numReduce(View view){
        int nums=getCommitData().getNumber()-1;
        numChange(nums);
    }

    private void numChange(int nums) {
        if(nums==1){
            btnDecrease.setEnabled(false);
        }else if(nums>1){
            btnDecrease.setEnabled(true);
        }
        if(nums<1){
            return;
        }
        getCommitData().setNumber(nums);
        orderNum1.setText(nums+"");
    }

    public void numAdd(View view){
        int nums= getCommitData().getNumber()+1;
        numChange(nums);
    }

    /*获取游戏标签*/
    private void requestGameLabel(String skillId) {
        CommonHttpUtil.getSkillLevel(skillId).compose(this.<List<ConditionLevel>>bindToLifecycle()).subscribe(new DefaultObserver<List<ConditionLevel>>() {
            @Override
            public void onNext(List<ConditionLevel> conditionLevels) {
                conditionLevels.add(0,ConditionModel.defaultLevel(WordUtil.getString(R.string.all)));
                frLevel.setData(conditionLevels);
                frLevel.createChildByData();
            }
        });
    }


    /*提交数据的包装类，修改类内部的条件可以更改按钮的隐藏*/
    public FlashOrderCommitBean getCommitData(){
        if( flashOrderCommitBean==null){
            flashOrderCommitBean=new FlashOrderCommitBean();
            flashOrderCommitBean.setDataListner(new DataListner() {
                @Override
                public void compelete(boolean isCompelete) {
                    btnOrder.setEnabled(isCompelete);
                }
            });
        }
        return flashOrderCommitBean;
    }

    /**
     * 选择时间
     */

    private void chooseTime() {
        mTimeList = CityUtil.getInstance().getTimeList(WordUtil.getString(R.string.today),
                WordUtil.getString(R.string.tomorrow),
                WordUtil.getString(R.string.tomorrow2));
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(c.getTimeInMillis() + 15 * 60 * 1000);
        DialogUitl.showOrderTimeDialog(this, mTimeList, 0, 0, 0, new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(Province province, City city, County county) {
                mTimeType = province.getAreaId();
                mTimeVal = StringUtil.contact(city.getAreaName(), ":", county.getAreaName());
                if (tvOrderTime != null) {
                    String showTime=StringUtil.contact(province.getAreaName(), " ", mTimeVal);
                    tvOrderTime.setText(showTime);
                    flashOrderCommitBean.setTime(mTimeVal);
                    flashOrderCommitBean.setTimeType(mTimeType);
                }
            }
        });
    }

    /*提示框*/
    private void publishSucceDialog() {
        new DialogUitl.Builder(mContext)
                .setTitle(getString(R.string.publish_succ))
                .setContent(getString(R.string.publish_tip1))
                .setCancelable(true)
                .setCancelString(DialogUitl.GONE)
                .setConfrimString(getString(R.string.know))
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        finish();
                    }
                })
                .build()
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==AllSkillActivity.GET_SKILL&&resultCode==RESULT_OK){
          SkillBean skillBean= data.getParcelableExtra(Constants.DATA);
          mSelectedSkillBean = skillBean;
          setSkill(skillBean);
        }
    }


    /*从全部品类界面返回的数据*/
    private void setSkill(SkillBean skillBean) {
        if(skillBean!=null){
          mSelectedSkillBean = skillBean;
          String skillId=skillBean.getSkillId();
          getCommitData().setSkillId(skillId);
          requestGameLabel(skillId);
          tvCategory.setText(skillBean.getSkillName2());
        }
    }

    /*跳转全部分类界面*/
    public void toAllSkill(View view){
        Intent intent = new Intent(mContext,AllSkillActivity.class);
        intent.putExtra(Constants.SELECTED_SKILL,mSelectedSkillBean);
        startActivityForResult(intent,AllSkillActivity.GET_SKILL);
      //  startActivityForResult(AllSkillActivity.class,AllSkillActivity.GET_SKILL);
    }


    /*提交快速下单*/
    public void confirm(View view){

        if(!ClickUtil.canClick()) {
            return;
        }

        getCommitData().setRes(tvDes.getText().toString());
        MainHttpUtil.setDrip(getCommitData()).compose(this.<Boolean>bindToLifecycle())
                .subscribe(new DialogObserver<Boolean>(this) {
            @Override
            public void onNext(Boolean aBoolean) {
                    if(aBoolean){
                       publishSucceDialog();
                    }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainHttpUtil.cancel(MainHttpConsts.GET_HOME);
    }
}
