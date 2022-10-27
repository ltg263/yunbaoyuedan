package com.yunbao.chatroom.bean;

import com.yunbao.common.bean.UserBean;
import java.util.List;

public class ApplyResult {
  private String nums;
  private int rank;
  private List<UserBean> list;
  private  int isme;
  private int isapply;


    public String getNums() {
        return nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public List<UserBean> getList() {
        return list;
    }

    public void setList(List<UserBean> list) {
        this.list = list;
    }

    public int getIsme() {
        return isme;
    }

    public void setIsme(int isme) {
        this.isme = isme;
    }

    public int getIsapply() {
        return isapply;
    }

    public void setIsapply(int isapply) {
        this.isapply = isapply;
    }
}
