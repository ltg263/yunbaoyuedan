package com.yunbao.main.bean.commit;

import android.text.TextUtils;

import com.yunbao.common.bean.commit.CommitEntity;
import com.yunbao.common.utils.StringUtil;

public class DressingCommitBean extends CommitEntity {
    //区分首页/动态筛选
    public static final String MAIN_HOME_PEIWAN = "mainHomePeiWan";
    public static final String MAIN_HOME_DYNAMIC = "mainHomeDynamic";
    private String sex;
    private String age;
    private String skill;

    private String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
        observer();
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
        observer();
    }

    public String getSkill() {
        return skill;
    }


    public void setSkill(String skill) {
        this.skill = skill;
        observer();
    }
    @Override
    public boolean observerCondition() {
        return fieldNotEmptyAndZero(sex)||fieldNotEmptyAndZero(age)||fieldNotEmptyAndZero(skill);
    }

    public boolean fieldNotEmptyAndZero(String data){
        return !TextUtils.isEmpty(data)&&!data.equals(DEFAUlT_VALUE);
    }

    @Override
    public boolean equals( Object obj) {
        if(obj instanceof DressingCommitBean){
            try {
            DressingCommitBean dressingCommitBean= (DressingCommitBean) obj;
             return  StringUtil.equalsContainNull(dressingCommitBean.skill,skill)
                     && StringUtil.equalsContainNull(dressingCommitBean.age,age)
                     && StringUtil.equalsContainNull(dressingCommitBean.sex,sex);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return super.equals(obj);
    }

    public void copy(DressingCommitBean dressingCommitBean){
        sex=dressingCommitBean.sex;
        age=dressingCommitBean.age;
        skill=dressingCommitBean.skill;
    }
}
