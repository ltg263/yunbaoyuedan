package com.yunbao.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.R;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.bean.UserBean;
import static com.yunbao.common.Constants.CALL_TYPE;
import static com.yunbao.common.Constants.DATA;
import static com.yunbao.common.Constants.ROLE;
import static com.yunbao.common.Constants.ROOM_ID;

/**
 * Created by cxf on 2019/2/25.
 */

public class RouteUtil {
    public static final String PATH_LAUNCHER = "/app/LauncherActivity";
    public static final String PATH_LOGIN_INVALID = "/main/LoginInvalidActivity";
    public static final String PATH_USER_HOME = "/main/UserHomeActivity";
    public static final String PATH_MAIN = "/main/MainActivity";
    public static final String PATH_COIN = "/main/MyCoinActivity";
    public static final String PATH_VIP = "/main/VipActivity";
    public static final String MAIN_ORDER_COMMENT = "/main/OrderCommentActivity";
    public static final String MAIN_ORDER_COMMENT_ANCHOR = "/main/OrderCommentActivity3";
    public static final String PUB_DYNAMIC = "/dynamics/PublishDynamicsActivity";
    public static final String PATH_CALL_SERVICE = "/im/CallService";
    public static final String PATH_CALL_ACTIVITY = "/im/CallActivity";
    public static final String PATH_SKILL_HOME = "/main/SkillHomeActivity";
    public static final String PATH_All_Skill = "/main/AllSkillActivity";
    public static final String PATH_ORDER_MAKE = "/main/OrderMakeActivity";
    public static final String PATH_ORDER_REFUND_DEAL = "/main/RefunDealActivity";

    public static final String PATH_LIVE_DISPATH_AUDIENCE = "/live/LiveDispatchAudienceActivity";
    public static final String PATH_LIVE_GOSSIP_AUDIENCE = "/live/LiveGossipAudienceActivity";
    public static final String PATH_LIVE_FRIEND_AUDIENCE = "/live/LiveFriendAudienceActivity";
    public static final String PATH_LIVE_SONG_AUDIENCE = "/live/LiveSongAudienceActivity";
    public static final String PATH_TEENAGER = "/main/YoungOpenedActivity";
    /**
     * 启动页
     */

    public static void forwardLauncher() {
        ARouter.getInstance().build(PATH_LAUNCHER)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .navigation();
    }

    /**
     * 登录过期
     */

    public static void forwardLoginInvalid(String tip) {
        ARouter.getInstance().build(PATH_LOGIN_INVALID)
                .withString(Constants.TIP, tip)
                .navigation();
    }

    /**
     * 跳转到个人主页
     */

    public static void forwardUserHome(String toUid) {
        ARouter.getInstance().build(PATH_USER_HOME)
                .withString(Constants.TO_UID, toUid)
                .navigation();
    }

    /**
     * 跳转到充值页面
     */

    public static void forwardMyCoin(Context context) {
        ARouter.getInstance().build(PATH_COIN).navigation();
    }

    /**
     * 跳转到充值页面
     */

    public static void forwardMyCoin() {
        ARouter.getInstance().build(PATH_COIN).navigation();
    }

    /**
     * 跳转到青少年模式
     */
    public static void forwardTeenager() {
        ARouter.getInstance().build(PATH_TEENAGER)
                .navigation();
    }

    /**
     * 跳转到订单评论 用户评价主播
     */

    public static void forwardOrderComment(String orderId) {
        ARouter.getInstance().build(MAIN_ORDER_COMMENT)
                .withString(Constants.ORDER_ID, orderId)
                .navigation();
    }

    /**
     * 跳转到订单评论 主播评价用户
     */
    public static void forwardOrderCommentAnchor(String orderId) {
        ARouter.getInstance().build(MAIN_ORDER_COMMENT_ANCHOR)
                .withString(Constants.ORDER_ID, orderId)
                .navigation();
    }


    public static void forwardPubDynamics() {
        ARouter.getInstance().build(PUB_DYNAMIC)
                .navigation();
    }

    /**
     * 跳转到VIP
     */

    public static void forwardVip() {
        ARouter.getInstance().build(PATH_VIP)
                .navigation();
    }

    public static void forwardAllSkill(Activity activity, int requestCode,NavigationCallback navigationCallback) {
        ARouter.getInstance().build(PATH_All_Skill)
                .navigation(activity,requestCode, navigationCallback);

    }

    /**
     * 跳转到通话
     */

    private static void forwardCall(String path,int role, int roomId, int callType, UserBean userBean) {
        ARouter.getInstance().build(path)
                .withInt(ROLE,role)
                .withInt(ROOM_ID,roomId)
                .withInt(CALL_TYPE,callType)
                .withParcelable(DATA,userBean)
                .navigation();
    }

    public static void forwardCallService(int role, int roomId, int callType, UserBean userBean){
        forwardCall(PATH_CALL_SERVICE,role,roomId,callType,userBean);
    }

    public static void forwardCallActivity(int role, int roomId, int callType, UserBean userBean){
        forwardCall(PATH_CALL_ACTIVITY,role,roomId,callType,userBean);
    }

    public static void forwardSkillHome(String toUid,String skillId) {
        ARouter.getInstance().build(PATH_SKILL_HOME)
                .withString(Constants.TO_UID, toUid)
                .withString(Constants.SKILL_ID, skillId)
                .navigation();
    }

    public static void forwardOrderMake(UserBean userBean, SkillBean skillBean) {
        ARouter.getInstance().build(PATH_ORDER_MAKE)
                .withParcelable(Constants.USER_BEAN, userBean)
                .withParcelable(Constants.SKILL_BEAN, skillBean)
                .navigation();
    }

    public static void forwardOrderRefundDeal(String orderId) {
        ARouter.getInstance().build(PATH_ORDER_REFUND_DEAL)
                .withString(Constants.ORDER_ID, orderId)
                .navigation();
    }



    public static void forwardOrderMakeFromLiveActivity(UserBean userBean, SkillBean skillBean) {
        ARouter.getInstance().build(PATH_ORDER_MAKE)
                .withParcelable(Constants.USER_BEAN, userBean)
                .withParcelable(Constants.SKILL_BEAN, skillBean)
                .withString(Constants.FROM,Constants.LIVE_CHAT_ROOM)
                .navigation();
    }


    public static void forwardLiveAudience(LiveBean liveBean,int type,boolean isUpWheat) {
        String path=null;
        switch (type){
            case Constants.LIVE_TYPE_DISPATCH:
                path=PATH_LIVE_DISPATH_AUDIENCE;
                break;
            case Constants.LIVE_TYPE_FRIEND:
                path=PATH_LIVE_FRIEND_AUDIENCE;
                break;
            case Constants.LIVE_TYPE_CHAT:
                path=PATH_LIVE_GOSSIP_AUDIENCE;
                break;
            case Constants.LIVE_TYPE_SONG:
                path=PATH_LIVE_SONG_AUDIENCE;
                break;
        }
        if(path==null){
            return;
        }
        if(CommonAppConfig.getInstance().getIsState()==1) {
            if (path.equals(PATH_LIVE_FRIEND_AUDIENCE) || path.equals(PATH_LIVE_DISPATH_AUDIENCE)) {
                ToastUtil.show(R.string.teenager_live_tip_1);
                return;
            }
        }
        ARouter.getInstance().build(path)
                .withParcelable(DATA, liveBean)
                .withBoolean(Constants.UP_WHEAT,isUpWheat)
                .navigation();
    }

}
