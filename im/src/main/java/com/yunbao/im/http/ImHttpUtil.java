package com.yunbao.im.http;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.common.server.MapBuilder;
import com.yunbao.common.server.RequestFactory;
import com.yunbao.common.server.entity.BaseResponse;
import com.yunbao.common.server.entity.Data;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by cxf on 2019/2/26.
 */

public class ImHttpUtil {

    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }

    /**
     * 私信聊天页面用于获取用户信息
     */
    public static void getImUserInfo(String uids, HttpCallback callback) {
        HttpClient.getInstance().get("Im.GetMultiInfo", ImHttpConsts.GET_IM_USER_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("uids", uids)
                .execute(callback);
    }


    /**
     * 获取系统消息列表
     */


    public static void getSystemMessageList(int p, HttpCallback callback) {
        HttpClient.getInstance().get("Im.GetSysNotice", ImHttpConsts.GET_SYSTEM_MESSAGE_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("p", p)
                .execute(callback);
    }


    /**
     * 判断自己有没有被对方拉黑，聊天的时候用到
     */


    public static void checkIm(String touid, HttpCallback callback) {
        HttpClient.getInstance().get("Im.Check", ImHttpConsts.CHECK_IM)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .execute(callback);
    }

    /**
     * 付费发送消息
     */

    public static void chargeSendIm(HttpCallback callback) {
        HttpClient.getInstance().get("Im.BuyIm", ImHttpConsts.CHARGE_SEND_IM)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 获取礼物列表，同时会返回剩余的钱
     */

    public static void getGiftList(HttpCallback callback) {
        HttpClient.getInstance().get("Gift.GetList", ImHttpConsts.GET_GIFT_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }

    /**
     * 观众给主播送礼物
     */

    public static void sendGift(String touid, String sessionId, int giftId, String giftCount, HttpCallback callback) {
        HttpClient.getInstance().get("Gift.SendGift", ImHttpConsts.SEND_GIFT)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .params("giftid", giftId)
                .params("nums", giftCount)
                .execute(callback);
    }


    /**
     * 检查是否存在进行中的订单
     */
    public static void checkOrder(String touid, HttpCallback callback) {
        HttpClient.getInstance().get("Orders.GetOrdering", ImHttpConsts.CHECK_ORDER)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .execute(callback);
    }

    /**
     * 完成订单
     */
    public static void orderDone(String orderId, HttpCallback callback) {
        HttpClient.getInstance().get("Orders.CompleteOrder", ImHttpConsts.ORDER_DONE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("orderid", orderId)
                .execute(callback);
    }

    public static Observable<Boolean> checkAttent(String touid) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("touid", touid)
                .build();
        return RequestFactory.getRequestManager().postNormal("User.CheckAttent", parmMap).map(new Function<BaseResponse<JSONObject>, Boolean>() {
            @Override
            public Boolean apply(BaseResponse<JSONObject> jsonObjectBaseResponse) throws Exception {
               // ToastUtil.show(jsonObjectBaseResponse.getData().getMsg());
                Data data = jsonObjectBaseResponse.getData();
                if (data != null && data.getCode() == 700){
                    RouteUtil.forwardLoginInvalid(data.getMsg());
                    return null;
                }
                JSONObject jsonObject=  jsonObjectBaseResponse.getData().getInfo().get(0);
                return jsonObject.getIntValue("status")==3;
            }
        });
    }


    public static void checkAttentStatus(String touid, HttpCallback callback) {
        HttpClient.getInstance().get("User.CheckAttent", "")
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .execute(callback);
    }

    public static void getSystemStatus(HttpCallback httpCallback){
        HttpClient.getInstance().get("Im.getStatus", ImHttpConsts.CHARGE_SEND_IM)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(httpCallback);
    }

    /**
     * 监控：音视频聊天：通话成功后增加通话记录
     * http://yuedantest.yunbaozb.com/api/?service=Livetalk.start&roomuid=100686&touid=100619&type=1
     * 参数：roomuid：房间用户ID
     *            touid：聊天用户ID
     *             type：类型：1：视频聊天；2：音频聊天
     */
    public static void getLivetalkStart(String roomuid,String touid,int type,HttpCallback httpCallback){
        HttpClient.getInstance().get("Livetalk.start", ImHttpConsts.CALL_GET_START)
                .params("roomuid",roomuid)
                .params("touid", touid)
                .params("type", type)
                .execute(httpCallback);
    }

    /**
     * 监控：结束聊天：
     * http://yuedantest.yunbaozb.com/api/?service=Livetalk.stop&roomuid=100686
     * 参数：roomuid：房间ID
     */
    public static void getLivetalkStop(String roomuid,HttpCallback httpCallback){
        HttpClient.getInstance().get("Livetalk.stop", ImHttpConsts.CALL_GET_STOP)
                .params("roomuid",roomuid)
                .execute(httpCallback);
    }

    /**
     * 接听通话邀请时，判断发起人在线状态
     * http://ybyuewan.yunbaozb.com/api/?service=Livetalk.getUserstatus&uid=100619&token=a&touid=101471
     */
    public static void getUserOnlineStatus(String touid,HttpCallback httpCallback){
        HttpClient.getInstance().get("Livetalk.getUserstatus", ImHttpConsts.CALL_GET_USER_STATUS)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid",touid)
                .execute(httpCallback);
    }

}
