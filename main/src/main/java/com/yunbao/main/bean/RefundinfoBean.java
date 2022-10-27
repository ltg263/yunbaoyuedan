package com.yunbao.main.bean;
import com.yunbao.common.bean.SkillBean;

public class RefundinfoBean
{
    /**
     * id : 7
     * uid : 100410
     * touid : 100222
     * orderid : 485
     * content : 没有按约定时间陪练
     * addtime : 1591171153
     * uptime : 0
     * total : 10
     * difftime : -80
     * difftime_str : 还剩-80分钟,若超时未处理系统将自行处理
     * order_status : 3
     */
    private String id;
    private String uid;
    private String touid;
    private String orderid;
    private String content;
    private String addtime;
    private String uptime;
    private String total;
    private int difftime;
    private String difftime_str;
    private String order_status;
    private SkillBean skill;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getTouid() {
        return touid;
    }
    public void setTouid(String touid) {
        this.touid = touid;
    }
    public String getOrderid() {
        return orderid;
    }
    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public int getDifftime() {
        return difftime;
    }

    public void setDifftime(int difftime) {
        this.difftime = difftime;
    }

    public String getDifftime_str() {
        return difftime_str;
    }

    public void setDifftime_str(String difftime_str) {
        this.difftime_str = difftime_str;
    }
    public String getOrder_status() {
        return order_status;
    }
    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public SkillBean getSkill() {
        return skill;
    }
    public void setSkill(SkillBean skill) {
        this.skill = skill;
    }
}
