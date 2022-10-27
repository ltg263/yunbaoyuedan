package com.yunbao.main.bean;

import com.yunbao.common.bean.SkillBean;

import java.util.List;

public class AllSkillBean {
    /**
     * id : 1
     * name : 手游
     * list_order : 1
     * list : []
     */
    public AllSkillBean(){

    }
    private String id;
    private String name;
    private String list_order;
    private List<SkillBean> list;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getList_order() {
        return list_order;
    }

    public void setList_order(String list_order) {
        this.list_order = list_order;
    }

    public List<SkillBean> getList() {
        return list;
    }

    public void setList(List<SkillBean> list) {
        this.list = list;
    }
}
