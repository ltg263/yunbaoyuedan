package com.yunbao.main.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.adapter.radio.CheckEntity;
import com.yunbao.common.bean.ConditionLevel;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.common.bean.Reason;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.server.MapBuilder;
import com.yunbao.common.server.RequestFactory;
import com.yunbao.common.server.entity.BaseResponse;
import com.yunbao.common.server.entity.Data;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.dynamic.bean.DynamicUserBean;
import com.yunbao.main.bean.AllSkillBean;
import com.yunbao.main.bean.FootBean;
import com.yunbao.main.bean.GreateManBean;
import com.yunbao.main.bean.LoginTypeBean;
import com.yunbao.main.bean.MySkillBean;
import com.yunbao.main.bean.PhotoBean;
import com.yunbao.main.bean.RefundinfoBean;
import com.yunbao.main.bean.SnapOrderBean;
import com.yunbao.main.bean.VisitBean;
import com.yunbao.main.bean.commit.FlashOrderCommitBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by cxf on 2018/9/17.
 */

public class MainHttpUtil {

    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }

    /**
     * 注册
     */
    public static void register(String phoneNum, String code, String password, HttpCallback callback) {
        HttpClient.getInstance().get("Login.Reg", MainHttpConsts.REGISTER)
                .params("username", phoneNum)
                .params("code", code)
                .params("password", password)
                .params("repass", password)
                .params("source", 1)//来源设备,0web，1android，2ios，3小程序
                .execute(callback);
    }

    /**
     * 注册获取验证码
     */
    public static void getRegisterCode(String phoneNum, HttpCallback callback) {
        String sign = MD5Util.getMD5(StringUtil.contact("account=", phoneNum, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Login.GetCode", MainHttpConsts.GET_REGISTER_CODE)
                .params("account", phoneNum)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 找回密码
     */
    public static void findPwd(String phoneNum, String code, String password, HttpCallback callback) {
        HttpClient.getInstance().get("Login.Forget", MainHttpConsts.FIND_PWD)
                .params("username", phoneNum)
                .params("code", code)
                .params("password", password)
                .params("repass", password)
                .execute(callback);
    }

    /**
     * 找回密码获取验证码
     */
    public static void getFindPwdCode(String phoneNum, HttpCallback callback) {
        String sign = MD5Util.getMD5(StringUtil.contact("account=", phoneNum, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Login.GetForgetCode", MainHttpConsts.GET_FIND_PWD_CODE)
                .params("account", phoneNum)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 手机号+密码登录
     */
    public static void login(String phoneNum, String password, String code, HttpCallback callback) {
        HttpClient.getInstance().get("Login.Login", MainHttpConsts.LOGIN)
                .params("username", phoneNum)
                .params("code", password)
                .params("country_code", code)
                .execute(callback);
    }

    /**
     * 获取登录验证码接口
     */

    public static void getLoginCode(String phoneNum, String code, HttpCallback callback) {
        String sign = MD5Util.getMD5(StringUtil.contact("account=", phoneNum, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Login.getCode", MainHttpConsts.GET_LOGIN_CODE)
                .params("account", phoneNum)
                .params("country_code", code)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 第三方登录
     */
    public static void loginByThird(String openid, String access_token,String nicename, String avatar, int flag, HttpCallback callback) {
        String sign = MD5Util.getMD5(StringUtil.contact("openid=", openid, "&type=", String.valueOf(flag), "&", HttpClient.SALT));
        HttpClient.getInstance().get("Login.LoginByThird", MainHttpConsts.LOGIN_BY_THIRD)
                .params("openid", openid)
                .params("access_token", access_token)
                .params("nicename", nicename)
                .params("avatar", avatar)
                .params("type", flag)
                .params("source", 1)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 首次登录后设置个人信息
     *
     * @param fields JSON字符串
     */
    public static void setUserProfile(String fields, HttpCallback callback) {
        HttpClient.getInstance().get("User.SetUserinfo", MainHttpConsts.SET_USER_PROFILE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("fields", fields)
                .execute(callback);
    }


    /**
     * 首页
     */
    public static void getHome(HttpCallback callback) {
        HttpClient.getInstance().get("Home.GetIndex", MainHttpConsts.GET_HOME)
                .execute(callback);
    }


    /**
     * 首页 推荐
     */
    public static void getRecommend(String sex, String age, String skillid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("User.getRecom", MainHttpConsts.GET_RECOMMEND)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .params("sex", sex)
                .params("age", age)
                .params("skillid", skillid)
                .execute(callback);
    }


    /**
     * 获取全部游戏分类
     */

    public static void getAllGameClass(HttpCallback callback) {
        HttpClient.getInstance().get("Skill.GetAll", MainHttpConsts.GET_ALL_GAME_CLASS)
                .execute(callback);
    }

    /**
     * 获取某个游戏的所有玩家
     */

    public static void getGameUserList(int order, int sex, String level, int voice, String gameId, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Skill.GetUserList", MainHttpConsts.GET_GAME_USER_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("skillid", gameId)
                .params("order", order)
                .params("sex", sex)
                .params("level", level)
                .params("voice", voice)
                .params("p", p)
                .execute(callback);
    }


    /**
     * 获取用户信息
     */
    public static void getBaseInfo(String uid, String token, final CommonCallback<UserBean> commonCallback) {
        HttpClient.getInstance().get("User.getBaseInfo", MainHttpConsts.GET_BASE_INFO)
                .params("uid", uid)
                .params("token", token)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            UserBean bean = JSON.toJavaObject(obj, UserBean.class);
                            CommonAppConfig appConfig = CommonAppConfig.getInstance();
                            appConfig.setUserBean(bean);
                            appConfig.setUserItemList(obj.getString("list"));
                            SpUtil.getInstance().setStringValue(SpUtil.USER_INFO, info[0]);
                            if (commonCallback != null) {
                                commonCallback.callback(bean);
                            }
                        }
                    }

                    @Override
                    public void onError() {
                        if (commonCallback != null) {
                            commonCallback.callback(null);
                        }
                    }
                });
    }

//    /**
//     * 手机号 验证码登录
//     */
//    public static void login(String phoneNum, String code, String agentCode, HttpCallback callback) {
//        HttpClient.getInstance().get("Login.userLogin", MainHttpConsts.LOGIN)
//                .params("user_login", phoneNum)
//                .params("code", code)
//                .params("source", "android")
//                .params("agentcode", agentCode)
//                .execute(callback);
//    }


    /**
     * 获取用户信息
     */
    public static void getBaseInfo(CommonCallback<UserBean> commonCallback) {
        getBaseInfo(CommonAppConfig.getInstance().getUid(),
                CommonAppConfig.getInstance().getToken(),
                commonCallback);
    }


    /**
     * 获取用户信息
     */
    public static void getBaseInfo(HttpCallback callback) {
        HttpClient.getInstance().get("User.getBaseInfo", MainHttpConsts.GET_BASE_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 用户个人主页信息
     */
    public static void getUserHome(String touid, HttpCallback callback) {
        HttpClient.getInstance().get("User.GetHome", MainHttpConsts.GET_USER_HOME)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .execute(callback);
    }


    /**
     * 搜索
     */
    public static void search(String key, int p, HttpCallback callback) {
        HttpClient.getInstance().get("Home.Search", MainHttpConsts.SEARCH)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("keyword", key)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取对方的关注列表
     *
     * @param touid 对方的uid
     */
    public static void getFollowList(String touid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("User.GetFollow", MainHttpConsts.GET_FOLLOW_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 获取对方的粉丝列表
     *
     * @param touid 对方的uid
     */
    public static void getFansList(String touid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("User.GetFans", MainHttpConsts.GET_FANS_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("p", p)
                .execute(callback);

    }


    /**
     * 更新用户资料
     */
    public static void updateUserInfo(String fields, HttpCallback callback) {
        HttpClient.getInstance().get("User.UpUserInfo", MainHttpConsts.UPDATE_USER_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("fields", fields)
                .execute(callback);
    }

    /**
     * 获取兴趣列表
     */
    public static void getInterestList(HttpCallback callback) {
        HttpClient.getInstance().get("User.GetHobby", MainHttpConsts.GET_INTEREST_LIST)
                .execute(callback);
    }

    /**
     * 获取游戏技能主页信息
     */
    public static void getSkillHome(String toUid, String gameId, HttpCallback callback) {
        CommonHttpUtil.getSkillHome(toUid, gameId, callback);
    }

    /**
     * 用于获取技能认证信息
     */
    public static void getUserSkill(HttpCallback callback) {
        HttpClient.getInstance().get("Skill.GetUserSkill", MainHttpConsts.GET_USER_SKILL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 我的技能列表
     */

    public static void getMySkill(int p, HttpCallback callback) {
        HttpClient.getInstance().get("Skill.GetMySkill", MainHttpConsts.GET_MY_SKILL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }


    /**
     * 我的技能开关
     */
    public static void setSkillOpen(String skillId, boolean open, HttpCallback callback) {
        HttpClient.getInstance().get("Skill.SetSwitch", MainHttpConsts.SET_SKILL_OPEN)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("skillid", skillId)
                .params("isswitch", open ? 1 : 0)
                .execute(callback);
    }

    /**
     * 获取技能标签
     */
    public static void getSkillLabel(String skillId, HttpCallback callback) {
        HttpClient.getInstance().get("Skill.GetLabel", MainHttpConsts.GET_SKILL_LABEL)
                .params("skillid", skillId)
                .execute(callback);
    }

    /**
     * 获取技能价格
     */
    public static void getSkillPrice(String skillId, HttpCallback callback) {
        HttpClient.getInstance().get("Skill.GetCoins", MainHttpConsts.GET_SKILL_PRICE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("skillid", skillId)
                .execute(callback);
    }

    /**
     * 获取技能价格说明
     */
    public static void getSkillPriceInfo(String skillId, HttpCallback callback) {
        HttpClient.getInstance().get("Skill.GetCoinInfo", MainHttpConsts.GET_SKILL_PRICE_INFO)
                .params("skillid", skillId)
                .execute(callback);
    }

    /**
     * 获取技能段位
     */
    public static void getSkillLevel(String skillId, HttpCallback callback) {
        HttpClient.getInstance().get("Skill.GetLevel", MainHttpConsts.GET_SKILL_LEVEL)
                .params("skillid", skillId)
                .execute(callback);
    }

    /**
     * 更新技能资料
     */
    public static void updateSkillInfo(String skillId, String fields, HttpCallback callback) {
        HttpClient.getInstance().get("Skill.UpSkill", MainHttpConsts.UPDATE_SKILL_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("skillid", skillId)
                .params("fields", fields)
                .execute(callback);
    }

    /**
     * 获取 我的收益 可提现金额数
     */
    public static void getProfit(HttpCallback callback) {
        HttpClient.getInstance().get("Cash.getProfit", MainHttpConsts.GET_PROFIT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 获取 我的礼物收益 可提现金额数
     */
    public static void getGiftProfit(HttpCallback callback) {
        HttpClient.getInstance().get("Cash.getGiftProfit", MainHttpConsts.GET_GIFT_PROFIT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 获取 提现账户列表
     */
    public static void getCashAccountList(HttpCallback callback) {
        HttpClient.getInstance().get("Cash.GetUserAccountList", MainHttpConsts.GET_USER_ACCOUNT_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 添加 提现账户
     */
    public static void addCashAccount(String account, String name, String bank, int type, HttpCallback callback) {
        HttpClient.getInstance().get("Cash.SetUserAccount", MainHttpConsts.ADD_CASH_ACCOUNT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("account", account)
                .params("name", name)
                .params("account_bank", bank)
                .params("type", type)
                .execute(callback);
    }

    /**
     * 删除 提现账户
     */
    public static void deleteCashAccount(String accountId, HttpCallback callback) {
        HttpClient.getInstance().get("Cash.delUserAccount", MainHttpConsts.DEL_CASH_ACCOUNT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("id", accountId)
                .execute(callback);
    }

    /**
     * 提现
     */
    public static void doCash(String votes, String accountId, HttpCallback callback) {
        HttpClient.getInstance().get("Cash.setCash", MainHttpConsts.DO_CASH)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("cashvote", votes)//提现的票数
                .params("accountid", accountId)//账号ID
                .execute(callback);
    }


    /**
     * 礼物提现
     */
    public static void doCashGift(String votes, String accountId, String cashmoney, HttpCallback callback) {
        HttpClient.getInstance().get("Cash.setCashgift", MainHttpConsts.DO_CASH_GIFT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("cashvote", votes)//提现的票数
                .params("accountid", accountId)//账号
                .params("cashmoney", cashmoney)//体现金额
                .execute(callback);
    }


    /**
     * 下单时候获取支付列表
     */
    public static void getOrderPay(HttpCallback callback) {
        HttpClient.getInstance().get("Orders.GetPay", MainHttpConsts.GET_ORDER_PAY)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("type", 1)
                .execute(callback);
    }

    /**
     * 购买技能下单
     *
     * @param timeType 时间类型，0今天1明天2后天
     * @param payType  支付方式，0余额1支付宝2微信3苹果4google
     */
    public static void setOrder(String toUid, String skillId, String timeType, String time, String nums, String des, String payType, HttpCallback callback) {
        HttpClient.getInstance().get("Orders.SetOrder", MainHttpConsts.SET_ORDER)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", toUid)
                .params("skillid", skillId)
                .params("type", timeType)
                .params("svctm", time)
                .params("nums", nums)
                .params("des", des)
                .params("paytype", payType)
                .execute(callback);
    }


    /**
     * 获取订单详情
     */
    public static void getOrderDetail(String orderId, HttpCallback callback) {
        HttpClient.getInstance().get("Orders.getOrderDetail", MainHttpConsts.GET_ORDER_DETAIL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("orderid", orderId)
                .execute(callback);
    }

    /**
     * 获取多个订单详情
     *
     * @param orderIds 多个订单 id 逗号拼接
     */
    public static void getMutiOrderDetail(String orderIds, HttpCallback callback) {
        CommonHttpUtil.getMutiOrderDetail(orderIds, callback);
    }

    /**
     * 获取取消订单的原因
     */
    public static void getOrderCancelList(HttpCallback callback) {
        HttpClient.getInstance().get("Orders.GetCancelList", MainHttpConsts.GET_ORDER_CANCEL_LIST)
                .execute(callback);
    }

    /**
     * 取消订单
     */
    public static void orderCancel(String orderId, String reason, HttpCallback callback) {
        HttpClient.getInstance().get("Orders.CancelOrder", MainHttpConsts.ORDER_CANCEL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("orderid", orderId)
                .params("reason", reason)
                .execute(callback);
    }


    /**
     * 我的全部订单列表
     */
    public static void getMyOrderList(HttpCallback callback) {
        HttpClient.getInstance().get("Orders.GetOrders", MainHttpConsts.GET_MY_ORDER_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 我的全部订单列表
     */
    public static void getMyOrderList2(int p, HttpCallback callback) {
        HttpClient.getInstance().get("Orders.GetOrdersMore", MainHttpConsts.GET_MY_ORDER_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }


    /**
     * 拒接订单
     */
    public static void orderRefuse(String orderId, HttpCallback callback) {
        HttpClient.getInstance().get("Orders.RefuseOrder", MainHttpConsts.ORDER_REFUSE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("orderid", orderId)
                .execute(callback);
    }


    /**
     * 接单
     */
    public static void orderAccpet(String orderId, HttpCallback callback) {
        HttpClient.getInstance().get("Orders.ReceiptOrder", MainHttpConsts.ORDER_ACCPET)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("orderid", orderId)
                .execute(callback);
    }


    /**
     * 完成订单
     */
    public static void orderDone(String orderId, HttpCallback callback) {
        HttpClient.getInstance().get("Orders.CompleteOrder", MainHttpConsts.ORDER_DONE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("orderid", orderId)
                .execute(callback);
    }

    /**
     * 评价订单
     */

    public static void orderSetComment(String orderId, String content, String star, String label, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String token = CommonAppConfig.getInstance().getToken();
        String sign = MD5Util.getMD5(StringUtil.contact("orderid=", orderId, "&star=", star, "&token=", token, "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Comment.SetComment", MainHttpConsts.ORDER_SET_COMMENT)
                .params("uid", uid)
                .params("token", token)
                .params("orderid", orderId)
                .params("content", content)
                .params("star", star)
                .params("label", label)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 主播评价用户
     */
    public static void orderCommentUser(String orderId, String content, String star, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String token = CommonAppConfig.getInstance().getToken();
        String sign = MD5Util.getMD5(StringUtil.contact("orderid=", orderId, "&star=", star, "&token=", token, "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Comment.SetEvaluate", MainHttpConsts.ORDER_COMMENT_USER)
                .params("uid", uid)
                .params("token", token)
                .params("orderid", orderId)
                .params("content", content)
                .params("star", star)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 获取用户技能评论
     */
    public static void getSkillComment(int p, String skillid, String liveuid, HttpCallback callback) {
        HttpClient.getInstance().get("Comment.GetComment", MainHttpConsts.ORDER_COMMENT_USER)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("skillid", skillid)
                .params("liveuid", liveuid)
                .params("p", p)
                .execute(callback);
    }


    /**
     * 获取自己的VIP状态
     */
    public static void getMyVip(HttpCallback callback) {
        HttpClient.getInstance().get("Vip.MyVip", MainHttpConsts.GET_MY_VIP)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 余额购买vip
     */
    public static void buyVipWithCoin(String vipid, HttpCallback callback) {
        HttpClient.getInstance().get("Vip.BuyVip", MainHttpConsts.BUY_VIP_WITH_COIN)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("vipid", vipid)
                .execute(callback);
    }


    /**
     * 检查是否要弹邀请码的弹窗
     */
    public static void checkAgent(HttpCallback callback) {
        HttpClient.getInstance().get("Agent.Check", MainHttpConsts.CHECK_AGENT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 设置邀请码
     */
    public static void setAgent(String code, HttpCallback callback) {
        HttpClient.getInstance().get("Agent.SetAgent", MainHttpConsts.CHECK_AGENT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("code", code)
                .execute(callback);
    }


    /**
     * 首页 关注
     */
    public static void getFollow(int p, HttpCallback callback) {
        HttpClient.getInstance().get("Home.GetAttention", MainHttpConsts.GET_FOLLOW)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }

    /**
     * 首页 附近
     */
    public static void getNear(int p, HttpCallback callback) {
        HttpClient.getInstance().get("Home.getNearby", MainHttpConsts.GET_NEAR)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("lng", CommonAppConfig.getInstance().getLng())
                .params("lat", CommonAppConfig.getInstance().getLat())
                .params("p", p)
                .execute(callback);
    }


    /**
     * 模拟支付
     */
    public static void testCharge(String changeid, String coin, String money, HttpCallback callback) {
        HttpClient.getInstance().get("Charge.GetTestOrder", MainHttpConsts.GET_FOLLOW)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("changeid", changeid)
                .params("coin", coin)
                .params("money", money)
                .execute(callback);
    }


    /**
     * 获取全部分类
     */
    public static Observable<List<AllSkillBean>> getAllSkill() {
        return RequestFactory.getRequestManager().get("Skill.GetAll", null, AllSkillBean.class);
    }

    /**
     * 获取我的技能
     */
    public static Observable<List<MySkillBean>> getMySkill() {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken()).build();
        return RequestFactory.getRequestManager().get("Skill.GetMySkill", parmMap, MySkillBean.class);
    }

    public static Observable<List<MySkillBean>> getSkillAuth(String liveUid) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("liveuid", liveUid)
                .put("token", CommonAppConfig.getInstance().getToken()).build();
        return RequestFactory.getRequestManager().get("Skill.GetSkillAuth", parmMap, MySkillBean.class);
    }


    /*我的相册列表*/
    public static Observable<List<PhotoBean>> getMyPhotos(int p) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("p", p)
                .build();
        return RequestFactory.getRequestManager().get("Photo.GetMyPhotos", parmMap, PhotoBean.class);
    }


    public static Observable<List<PhotoBean>> getPhotos(String liveUid, int p) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("p", p)
                .put("liveuid", liveUid)
                .build();
        return RequestFactory.getRequestManager().get("Photo.GetPhotos", parmMap, PhotoBean.class);
    }

    /*上传照片*/
    public static Observable<Boolean> setPhoto(String thumbs) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("thumbs", thumbs)
                .build();
        String sign = StringUtil.createSign(parmMap, "uid", "token", "thumbs");
        parmMap.put("sign", sign);
        return RequestFactory.getRequestManager().noReturnPost("Photo.SetPhoto", parmMap);
    }


    public static Observable<Boolean> footClear() {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())

                .build();
        return RequestFactory.getRequestManager().noReturnPost("Foot.clearView", parmMap);
    }


    /*是否已下单*/
    public static Observable<Boolean> checkDrip() {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .build();
        return RequestFactory.getRequestManager().noReturnPost("Drip.CheckDrip", parmMap);
    }

    /*删除照片*/
    public static Observable<Boolean> delPhoto(String id) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("id", id)
                .build();
        String sign = StringUtil.createSign(parmMap, "uid", "token", "id");
        parmMap.put("sign", sign);
        return RequestFactory.getRequestManager().noReturnPost("Photo.DelPhoto", parmMap);
    }

    /*获取来访记录*/
    public static Observable<List<VisitBean>> getVisit(String lastTime) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("lasttime", lastTime)
                .build();
        return RequestFactory.getRequestManager().get("Foot.GetVisit", parmMap, VisitBean.class);
    }


    /*获取浏览记录*/
    public static Observable<List<FootBean>> getFootView(String lastTime) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("lasttime", lastTime)
                .build();
        return RequestFactory.getRequestManager().get("Foot.GetView", parmMap, FootBean.class);
    }


    public static Observable<List<UserBean>> getBlackList(int p) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("p", p)
                .build();
        return RequestFactory.getRequestManager().get("User.blackList", parmMap, UserBean.class);
    }


    /*获取举报列表*/
    public static Observable<List<CheckEntity>> getUserReport() {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .build();
        return RequestFactory.getRequestManager().get("User.getReport", parmMap, CheckEntity.class);
    }


    /*举报用户*/
    public static Observable<Boolean> setUserReport(String touid, String content, String thumbs) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("touid", touid)
                .put("content", content)
                .put("thumbs", thumbs)
                .build();
        String sign = StringUtil.createSign(parmMap, "uid", "token", "touid", "content");
        parmMap.put("sign", sign);
        return RequestFactory.getRequestManager().noReturnPost("User.setReport", parmMap);
    }


    /*快速下单*/
    public static Observable<Boolean> setDrip(FlashOrderCommitBean flashOrderCommitBean) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("skillid", flashOrderCommitBean.getSkillId())
                .put("nums", flashOrderCommitBean.getNumber())
                .put("des", flashOrderCommitBean.getRes())
                .put("sex", flashOrderCommitBean.getSex())
                .put("type", flashOrderCommitBean.getTimeType())
                .put("svctm", flashOrderCommitBean.getTime())
                .put("levelid", flashOrderCommitBean.getLevel())
                .build();
        return RequestFactory.getRequestManager().noReturnPost("Drip.SetDrip", parmMap);
    }


    /*获取我的订单*/
    public static Observable<List<SnapOrderBean>> getMyDrip(int p) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("p", p)
                .build();
        return RequestFactory.getRequestManager().get("Drip.GetMyDrip", parmMap, SnapOrderBean.class);
    }

    /*获取抢单的大神列表*/
    public static Observable<List<GreateManBean>> getLiveGreatMan(String dripid, String lastid) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("dripid", dripid)
                .put("lastid", lastid)
                .build();
        return RequestFactory.getRequestManager().get("Drip.GetLiveid", parmMap, GreateManBean.class);
    }


    /*抢单大厅列表*/
    public static Observable<List<SnapOrderBean>> getDripList(String lastid) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("lastid", lastid)
                .build();
        return RequestFactory.getRequestManager().get("Drip.GetDripList", parmMap, SnapOrderBean.class);
    }


    /*抢单*/
    public static Observable<Boolean> grapDrip(String dripid) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("dripid", dripid)
                .build();
        return RequestFactory.getRequestManager().noReturnPost("Drip.GrapDrip", parmMap);
    }

    /*取消订单原因*/
    public static Observable<JSONObject> getDripCancel() {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .build();
        return RequestFactory.getRequestManager().get("Drip.GetDripCancel", parmMap, JSONObject.class).map(new Function<List<JSONObject>, JSONObject>() {
            @Override
            public JSONObject apply(List<JSONObject> jsonObjects) throws Exception {
                return jsonObjects.get(0);
            }
        });
    }

    /*取消订单*/
    public static Observable<Boolean> cancelDrip(String dripid, String content) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("dripid", dripid)
                .put("content", content)
                .build();
        String sign = StringUtil.createSign(parmMap, "uid", "token", "dripid", "content");
        parmMap.put("sign", sign);
        return RequestFactory.getRequestManager().noReturnPost("Drip.CancelDrip", parmMap);
    }

    /*用于滴滴订单提示信息*/
    public static Observable<JSONObject> getDripTips() {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .build();
        return RequestFactory.getRequestManager().postNormal("Drip.GetDripTips", parmMap).map(new Function<BaseResponse<JSONObject>, JSONObject>() {
            @Override
            public JSONObject apply(BaseResponse<JSONObject> jsonObjectBaseResponse) throws Exception {
                Data data = jsonObjectBaseResponse.getData();
                if (data != null && data.getCode() == 700) {
                    RouteUtil.forwardLoginInvalid(data.getMsg());
                    return null;
                }
                return jsonObjectBaseResponse.getData().getInfo().get(0);
            }
        });
    }

    /*获取登陆类型*/
    public static Observable<LoginTypeBean> getLoginType() {
        return RequestFactory.getRequestManager().get("Login.getLoginType", null, LoginTypeBean.class)
                .map(new Function<List<LoginTypeBean>, LoginTypeBean>() {
                    @Override
                    public LoginTypeBean apply(List<LoginTypeBean> loginTypeBeans) throws Exception {
                        return loginTypeBeans.get(0);
                    }
                })
                ;
    }

    /**
     * 获取登录信息，三方登录类型，服务和隐私条款
     */
    public static void getLoginInfo(HttpCallback callback) {
        HttpClient.getInstance().get("Login.getLoginType", MainHttpConsts.GET_LOGIN_INFO)
                .execute(callback);
    }

    /*  用于选择大神进行下单    paytype===0余额1支付宝2微信3苹果4google*/
    public static Observable<JSONObject> selectLive(String dripid, String liveuid, String paytype) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("dripid", dripid)
                .put("liveuid", liveuid)
                .put("paytype", paytype)
                .build();
        return RequestFactory.getRequestManager().postNormal("Drip.SelectLive", parmMap).map(new Function<BaseResponse<JSONObject>, JSONObject>() {
            @Override
            public JSONObject apply(BaseResponse<JSONObject> jsonObjectBaseResponse) throws Exception {
                com.yunbao.common.server.entity.Data<JSONObject> data = jsonObjectBaseResponse.getData();
                ToastUtil.show(data.getMsg());
                if (data != null && data.getCode() == 700) {
                    RouteUtil.forwardLoginInvalid(data.getMsg());
                    return null;
                }
                if (data.getCode() == 0) {
                    return data.getInfo().get(0);
                } else {
                    return null;
                }
            }
        });
    }

    /*获取退款理由列表*/
    public static Observable<List<Reason>> getRefundList() {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .build();
        return RequestFactory.getRequestManager().get("Refund.getRefundcat", parmMap, Reason.class);
    }


    /*退款申请*/
    public static Observable<Boolean> setRefund(String orderid, String touid, String content) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("orderid", orderid)
                .put("touid", touid)
                .put("content", content)
                .build();
        String sign = StringUtil.createSign(parmMap, "uid", "token", "touid", "content");
        parmMap.put("sign", sign);
        return RequestFactory.getRequestManager().noReturnPost("Refund.setRefund", parmMap);
    }


    /*获取退款信息*/
    public static Observable<List<RefundinfoBean>> getRefundinfo(String orderid) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("orderid", orderid)
                .build();
        return RequestFactory.getRequestManager().get("Refund.getRefundinfo", parmMap, RefundinfoBean.class);
    }


    /*变更退款状态*/
    public static Observable<Boolean> setRefundStatus(String orderid, int status) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("orderid", orderid)
                .put("status", status)
                .build();
        String sign = StringUtil.createSign(parmMap, "uid", "token", "touid", "content");
        parmMap.put("sign", sign);


        return RequestFactory.getRequestManager().noReturnPost("Refund.setRefundStatus", parmMap);
    }


    /**
     * 检查注销条件是否达成
     * http://yuedantest.yunbaozb.com/api/?service=Login.getCancelCondition&uid=100619&token=b063f4d9aabd47809fdec7334ab5c2ec
     */
    public static void getCancelCondition(HttpCallback callback) {
        HttpClient.getInstance().get("Login.getCancelCondition", MainHttpConsts.GET_CANCEL_CONDITION)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 注销账号
     * http://yuedantest.yunbaozb.com/api/?service=Login.cancelAccount&uid=100686&token=&time=1592819843&sign=222
     * 参数：uid：用户ID
     * token：token
     * time：当前时间戳
     * sign ：签名：uid、token、time
     */
    public static void cancelAccount(String time, HttpCallback callback) {
        String uid = CommonAppConfig.getInstance().getUid();
        String token = CommonAppConfig.getInstance().getToken();
        String sign = MD5Util.getMD5(StringUtil.contact("time=", time, "&token=", token, "&uid=", uid, "&", HttpClient.SALT));
        HttpClient.getInstance().get("Login.cancelAccount", MainHttpConsts.GET_LOGOUT_ACCOUNT)
                .params("uid", uid)
                .params("token", token)
                .params("time", time)
                .params("sign", sign)
                .execute(callback);
    }


    /**
     * 清空访问记录
     * http://yuedantest.yunbaozb.com/api/?service=Foot.clearVisit&uid=100619&token=35750ecf3b59777d90f986fb45468ca4
     * 参数：uid：用户ID
     * token：token
     */
    public static void clearVisitRecord(HttpCallback callback) {
        HttpClient.getInstance().get("Foot.clearVisit", MainHttpConsts.GET_CLEAR_VISIT_RECORD)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * http://yuedantest.yunbaozb.com/api/?service=Agent.getCode&uid=100762&token=da3c09cf92faab26148bdd10c7187da7
     *
     * @param callback
     */
    public static void getInviteCode(HttpCallback callback) {
        HttpClient.getInstance().get("Agent.getCode", MainHttpConsts.GET_AGENT_CODE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 国际区号
     * http://yuedantest.yunbaozb.com/api/?service=Login.getCountrys&field=
     * 参数：field：搜索内容，为空则显示全部
     */
    public static void getCountryCode(String field, HttpCallback callback) {
        HttpClient.getInstance().get("Login.getCountrys", MainHttpConsts.GET_COUNTRY_CODE)
                .params("field", field)
                .execute(callback);
    }

    public static void getFollowList(String sex, String age, String skillid, int p, HttpCallback callback) {
        HttpClient.getInstance().get("User.getFollowlist", MainHttpConsts.GET_FOLLOW_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("sex", sex)
                .params("age", age)
                .params("skillid", skillid)
                .params("p", p)
                .execute(callback);
    }

    /**
     * 聊天室：更多推荐列表http://yuedantest.yunbaozb.com/api/?service=Live.getRecomLists&uid=100619&p=1
     * 参数：uid：用户idp：分页
     */
    public static void getRecomLists(int p, HttpCallback callback) {
        HttpClient.getInstance().get("Live.getRecomLists", "getRecomLists")
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }

    /**
     * 关注页：新人专享---换一批（原最新用户接口）http://yuedantest.yunbaozb.com/api/?service=
     * User.getAuthlist&uid=100619&token=f71b28fae45d71ff69d09e624203e4d5&p=1参数：（其他参数删除，只剩以下三个）uid：用户idtoken：tokenp：分页
     */
    public static void getAuthlist(int p, HttpCallback callback) {
        HttpClient.getInstance().get("User.getAuthlist", "User.getAuthlist")
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }


    /**
     * 请求签到奖励
     */
    public static void requestBonus(HttpCallback callback) {
        HttpClient.getInstance().get("User.Bonus", MainHttpConsts.REQUEST_BONUS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);

    }


    /**
     * 获取签到奖励
     */
    public static void getBonus(HttpCallback callback) {
        HttpClient.getInstance().get("User.getBonus", MainHttpConsts.GET_BONUS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 青少年模式
     */
    public static void checkYoung(HttpCallback callback) {
        HttpClient.getInstance().get("User.checkTeenager", MainHttpConsts.TEENAGERS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }
    /**
     * 开启青少年模式
     */
    public static void openTeenagers(int type,String pwd,HttpCallback callback) {
        HttpClient.getInstance().get("User.setTeenagerPassword", MainHttpConsts.OPEN_TEENAGER)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("type",type )
                .params("password", pwd)
                .execute(callback);
    }
    /**
     * 关闭青少年模式
     */
    public static void closeTeenagers(String pwd,HttpCallback callback) {
        HttpClient.getInstance().get("User.closeTeenager", MainHttpConsts.CLOSE_TEENAGER)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("password", pwd)
                .execute(callback);
    }
    /**
     * 修改青少年模式密码
     */
    public static void updateTeenagerPassword(String pwd,String newpwd,HttpCallback callback) {
        HttpClient.getInstance().get("User.updateTeenagerPassword", MainHttpConsts.UPDATE_PASSWORD)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("oldpassword", pwd)
                .params("password", newpwd)
                .execute(callback);
    }

    /**
     * 上报青少年模式时长
     */
    public static void reduceTeenagers(HttpCallback callback) {
        HttpClient.getInstance().get("User.addTeenagerTime", MainHttpConsts.REDUCE_TEENAGERS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 检查是否有未完成的订单
     */
    public static void checkUnfinishedOrder(HttpCallback callback) {
        HttpClient.getInstance().get("Orders.checkUnfinishedOrder", MainHttpConsts.REDUCE_TEENAGERS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

}




