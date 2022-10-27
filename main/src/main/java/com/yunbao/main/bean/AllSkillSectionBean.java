package com.yunbao.main.bean;

import com.chad.library.adapter.base.entity.SectionEntity;
import com.yunbao.common.bean.SkillBean;

public class AllSkillSectionBean extends SectionEntity<SkillBean> {

    public AllSkillSectionBean(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public AllSkillSectionBean(SkillBean allSkillBean) {
        super(allSkillBean);
    }
}
