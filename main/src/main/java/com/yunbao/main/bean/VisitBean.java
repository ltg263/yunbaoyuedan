package com.yunbao.main.bean;

import com.yunbao.common.bean.UserBean;

public class VisitBean {
    private String nums;
    private String addtime;
    private String datetime;
    private UserInfo userinfo;

    public String getNums() {
        return nums;
    }
    public void setNums(String nums) {
        this.nums = nums;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public UserInfo getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserInfo userinfo) {
        this.userinfo = userinfo;
    }

    public static class UserInfo{
        private String id;
        private int sex;
        private String avatar;
        private String user_nickname;
        private String addr;
        private int age;
        private String constellation;


        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }

        public int getSex() {
            return sex;
        }
        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getUser_nickname() {
            return user_nickname;
        }

        public void setUser_nickname(String user_nickname) {
            this.user_nickname = user_nickname;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getConstellation() {
            return constellation;
        }

        public void setConstellation(String constellation) {
            this.constellation = constellation;
        }



    }





}
