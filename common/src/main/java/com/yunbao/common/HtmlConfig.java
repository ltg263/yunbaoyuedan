package com.yunbao.common;

/**
 * Created by cxf on 2018/10/15.
 */

public class HtmlConfig {

    //登录即代表同意服务和隐私条款
    public static final String LOGIN_PRIVCAY = CommonAppConfig.HOST + "/appapi/page/detail?id=1";
    //个人主页分享链接
    public static final String SHARE_HOME_PAGE = CommonAppConfig.HOST + "/index.php?g=Appapi&m=home&a=index&touid=";
    //提现记录
    public static final String CASH_RECORD = CommonAppConfig.HOST + "/appapi/cash/index?votestype=0";

    //礼物记录
    public static final String CASH_GIFT_RECORD = CommonAppConfig.HOST + "/appapi/cash/index?votestype=1";


    //支付宝充值回调地址
    public static final String ALI_PAY_COIN_URL = CommonAppConfig.HOST + "/appapi/pay/notify_ali";
    //支付宝 订单支付 回调地址
    public static final String ALI_PAY_ORDER_URL = CommonAppConfig.HOST + "/appapi/orderback/notify_ali";
    //视频分享地址
    public static final String SHARE_VIDEO = CommonAppConfig.HOST + "/Appapi/Video/share?id=";
    //钱包明细 支出
    public static final String WALLET_EXPAND = CommonAppConfig.HOST + "/appapi/record/expend";
    //钱包明细 收入
    public static final String WALLET_INCOME = CommonAppConfig.HOST + "/appapi/record/income";
    //充值协议
    public static final String CHARGE_PRIVCAY = CommonAppConfig.HOST + "/appapi/page/detail?id=2";
    //关于我们
    public static final String ABOUT_US = CommonAppConfig.HOST + "/appapi/page/detail?id=3";
    //全民赚钱
    public static final String MAKE_MONEY = CommonAppConfig.HOST + "/appapi/Agent/share?code=";
    //技能认证
    public static final String SKILL_AUTH = CommonAppConfig.HOST + "/appapi/skillauth/apply?skillid=";

    //谷歌支付充值回调地址
    public static final String GOOGLE_PAY_COIN_URL = CommonAppConfig.HOST + "/appapi/google/notify";
    //谷歌支付下单回调地址
    public static final String GOOGLE_PAY_ORDER_URL = CommonAppConfig.HOST + "/appapi/Orderback/notify_google";

    //注销账号：注销说明  http://yuedantest.yunbaozb.com/appapi/page/detail?id=8
    public static final String LOGOUT_ACCOUNT_URL = CommonAppConfig.HOST + "/appapi/page/detail?id=8";

    // 隐私政策  /appapi/page/detail?id=6
    public static final String SETTING_PRIVACY_POLICY = CommonAppConfig.HOST + "/appapi/page/detail?id=6";

    // 服务协议  /appapi/page/detail?id=9
    public static final String SETTING_SERVICE_PROTOCOL = CommonAppConfig.HOST + "/appapi/page/detail?id=9";

    //收支明细：http://yuedantest.yunbaozb.com/appapi/detail/index&uid=101196&token=
    public static final String INCOME_AND_EXPENSES_DETAIL = CommonAppConfig.HOST + "/appapi/detail/index";
    //直播间幸运礼物说明
    public static final String LUCK_GIFT_TIP = CommonAppConfig.HOST + "/portal/page/index?id=26";
    //直播间道具礼物说明
    public static final String DAO_GIFT_TIP = CommonAppConfig.HOST + "/portal/page/index?id=39";
    //直播间贡献榜
    public static final String LIVE_LIST = CommonAppConfig.HOST + "/Appapi/contribute/index?uid=";

}
