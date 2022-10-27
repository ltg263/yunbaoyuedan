package com.yunbao.main.bean.commit;

import com.yunbao.common.bean.commit.CommitEntity;
public class FlashOrderCommitBean extends CommitEntity {
    private String skillId=CommitEntity.DEFAUlT_VALUE;
    private String sex=CommitEntity.DEFAUlT_VALUE;
    private String time;
    private String timeType;
    private int number;
    private String res="";
    private String level=CommitEntity.DEFAUlT_VALUE;


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
        observer();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
        observer();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        observer();
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
        observer();
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
        observer();
    }

    public String getSkillId() {

        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
        observer();
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public boolean observerCondition() {
        return fieldNotEmpty(time)&&fieldNotEmpty(timeType)&&fieldNotEmptyAndNoZero(skillId);
    }
}
