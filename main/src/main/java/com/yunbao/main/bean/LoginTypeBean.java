package com.yunbao.main.bean;

import java.util.List;

public class LoginTypeBean {

    /**
     * title : 服务协议和隐私政策
     * content : 请您务必仔细阅读，充分理解“服务协议”和“隐私政策”各条款，包括但不限于为了向您提供即时通讯，内容分享等服务，我们需要收集您设备信息和个人信息，您可以在设置中查看，管理您的授权。您可阅读《隐私政策》和《服务协议》了解详细信息，如您同意，请点击同意接受我们的服务。
     * login_title : 登录即代表同意《隐私政策》和《服务协议》
     * message : [{"title":"《服务协议》","url":"http://yuedantest.yunbaozb.com/portal/page/index?id=4"},{"title":"《隐私政策》","url":"http://yuedantest.yunbaozb.com/portal/page/index?id=3"}]
     */

    private String title;
    private String content;
    private String login_title;
    private List<MessageBean> message;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLogin_title() {
        return login_title;
    }

    public void setLogin_title(String login_title) {
        this.login_title = login_title;
    }

    public List<MessageBean> getMessage() {
        return message;
    }

    public void setMessage(List<MessageBean> message) {
        this.message = message;
    }

    public static class MessageBean {
        /**
         * title : 《服务协议》
         * url : http://yuedantest.yunbaozb.com/portal/page/index?id=4
         */

        private String title;
        private String url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
