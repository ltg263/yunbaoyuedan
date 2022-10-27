package com.yunbao.chatroom.bean;

import com.yunbao.common.bean.commit.CommitEntity;

public class LiveOrderCommitBean extends CommitEntity {
    private String skillId=CommitEntity.DEFAUlT_VALUE;
    private String level=CommitEntity.DEFAUlT_VALUE;
    private String sex=CommitEntity.DEFAUlT_VALUE;
    private String age=CommitEntity.DEFAUlT_VALUE;
    private String price=CommitEntity.DEFAUlT_VALUE;

    @Override
    public boolean observerCondition() {
        return fieldNotEmptyAndNoZero(skillId);
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
        observer();
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        observer();
    }
}
