package com.yunbao.main.views;


import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.bean.commit.CommitEntity;
import com.yunbao.common.bean.ConditionLevel;
import com.yunbao.common.bean.DataListner;
import com.yunbao.common.bean.ExportNamer;
import com.yunbao.common.custom.FlowRadioDataGroup;
import com.yunbao.common.custom.UIFactory;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsViewHolder2;
import com.yunbao.main.R;
import com.yunbao.main.bean.commit.DressingCommitBean;
import com.yunbao.main.business.ConditionModel;
import com.yunbao.main.http.MainHttpUtil;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectConditionViewHolder  extends AbsViewHolder2 implements View.OnClickListener {
    private TextView tvTitleSex;
    private FlowRadioDataGroup<ConditionLevel> frSexLevel;
    private TextView tvTitleAge;
    private FlowRadioDataGroup<ConditionLevel> frAgeLevel;
    private TextView tvTitleSkill;
    private FlowRadioDataGroup<ExportNamer> frSkillLevel;

    private LinearLayout.LayoutParams mLayoutParms;
    private LayoutInflater layoutInflater;
    private DressingCommitBean dressingCommitBean;
    private DrawerLayout drawerLayout;

    public SelectConditionViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
        if(args.length>0) {
            drawerLayout= (DrawerLayout) args[0];
        }
    }
    @Override
    protected int getLayoutId() {
        return R.layout.view_select_condition;
    }

    private TextView btnReset;
    private TextView btnConfirm;

    @Override
    public void init() {
        mLayoutParms=new LinearLayout.LayoutParams(DpUtil.dp2px(70),DpUtil.dp2px(30),1);
        mLayoutParms.leftMargin=DpUtil.dp2px(5);
        mLayoutParms.bottomMargin=DpUtil.dp2px(10);
        layoutInflater=LayoutInflater.from(mContext);
        btnReset = (TextView) findViewById(R.id.btn_reset);
        btnConfirm = (TextView) findViewById(R.id.btn_confirm);
        btnReset.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        mContentView.setOnClickListener(this);
        tvTitleSex = (TextView) findViewById(R.id.tv_title_sex);
        frSexLevel = (FlowRadioDataGroup) findViewById(R.id.fr_sex_level);
        tvTitleAge = (TextView) findViewById(R.id.tv_title_age);
        frAgeLevel = (FlowRadioDataGroup) findViewById(R.id.fr_age_level);
        tvTitleSkill = (TextView) findViewById(R.id.tv_title_skill);
        frSkillLevel = (FlowRadioDataGroup) findViewById(R.id.fr_skill_level);
        FlowRadioDataGroup.RadioButtonFactory factory=  new FlowRadioDataGroup.RadioButtonFactory() {
            @Override
            public RadioButton createRadioButton(Context context, ExportNamer conditionLevel, ViewGroup viewGroup, ViewGroup.LayoutParams params) {
                return UIFactory.createOrderLabelRadioButton2(layoutInflater,conditionLevel.exportName(),viewGroup,params);
            }
            @Override
            public LinearLayout.LayoutParams getLayoutParm() {
                return mLayoutParms;
            }

        };
        frSexLevel.setRadioButtonFactory(factory);
        frAgeLevel.setRadioButtonFactory(factory);
        frSkillLevel.setRadioButtonFactory(factory);


        frSexLevel.setSelectDataChangeListner(new FlowRadioDataGroup.SelectDataChangeListner<ConditionLevel>() {
            @Override
            public void select(View view,ConditionLevel conditionLevel) {
                    if(dressingCommitBean!=null) {
                        dressingCommitBean.setSex(conditionLevel!=null?conditionLevel.getId(): CommitEntity.DEFAUlT_VALUE);
                    }
            }
        });
        frAgeLevel.setSelectDataChangeListner(new FlowRadioDataGroup.SelectDataChangeListner<ConditionLevel>() {
            @Override
            public void select(View view,ConditionLevel conditionLevel) {
                if(dressingCommitBean!=null) {
                    dressingCommitBean.setAge(conditionLevel!=null?conditionLevel.getId():CommitEntity.DEFAUlT_VALUE);
                }
            }
        });
        frSkillLevel.setSelectDataChangeListner(new FlowRadioDataGroup.SelectDataChangeListner<ExportNamer>() {
            @Override
            public void select(View view,ExportNamer skillBean) {
                if(dressingCommitBean!=null) {
                    dressingCommitBean.setSkill(skillBean!=null?skillBean.exportId():CommitEntity.DEFAUlT_VALUE);
                }
                 }
        });

        frSexLevel.setData(Arrays.asList(ConditionModel.getSexLevel(WordUtil.getString(R.string.no_limit))));
        frAgeLevel.setData(Arrays.asList(ConditionModel.getAgeLevel(WordUtil.getString(R.string.no_limit))));

        frSexLevel.createChildByData();
        frAgeLevel.createChildByData();
        frSexLevel.checkByPosition(0);
        frAgeLevel.checkByPosition(0);

        dressingCommitBean=new DressingCommitBean();
        dressingCommitBean.setDataListner(new DataListner() {
            @Override
            public void compelete(boolean isCompelete) {
                btnReset.setEnabled(isCompelete);
                if(isCompelete){
                    btnReset.setAlpha(1F);
                }else{
                    btnReset.setAlpha(0.2F);
                }
            }
        });
        requestGameLabel();
    }


    private void requestGameLabel() {
        MainHttpUtil.getHome(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0){
                    JSONObject obj = JSON.parseObject(info[0]);
                    List<SkillBean> classList = JSON.parseArray(obj.getString("skilllist"), SkillBean.class);
                    List<ExportNamer> arrayList = new ArrayList<>();
                    arrayList.addAll(classList);
                    ConditionLevel conditionLevel=ConditionModel.defaultLevel(WordUtil.getString(R.string.no_limit));
                    arrayList.add(0,conditionLevel);
                    frSkillLevel.setData(arrayList);
                    frSkillLevel.createChildByData();
                    frSkillLevel.checkByPosition(0);
                    frSkillLevel.setCancleSelf(false);
                }
            }
        });
    }

    /*保存恢复的条件*/
    public void setCondition(DressingCommitBean dressingCommitBean){
       frSkillLevel.setSelect(dressingCommitBean.getSkill());
       frAgeLevel.setSelect(dressingCommitBean.getAge());
       frSexLevel.setSelect(dressingCommitBean.getSex());
       this.dressingCommitBean.setFrom(dressingCommitBean.getFrom());
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_confirm){
            confirm();
        }else if(id==R.id.btn_reset){
            reset();
        }
    }

    private void reset() {
        frSexLevel.checkByPosition(0);
        frAgeLevel.checkByPosition(0);
        frSkillLevel.checkByPosition(0);
    }


    private void confirm() {
        if(dressingCommitBean!=null){
            EventBus.getDefault().post(dressingCommitBean);
            drawerLayout.closeDrawers();
        }

    }
}
