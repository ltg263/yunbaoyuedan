package com.yunbao.common.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpMethod;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.GetRequest;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.R;
import com.yunbao.common.activity.ErrorActivity;
import com.yunbao.common.bean.ConditionLevel;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.bean.TxLocationPoiBean;
import com.yunbao.common.event.BlackEvent;
import com.yunbao.common.event.FollowEvent;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.server.MapBuilder;
import com.yunbao.common.server.RequestFactory;
import com.yunbao.common.server.RxUtils;
import com.yunbao.common.server.SeverConfig;
import com.yunbao.common.server.entity.BaseResponse;
import com.yunbao.common.server.entity.Data;
import com.yunbao.common.server.generic.ParameterizedTypeImpl;
import com.yunbao.common.utils.JsonUtil;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;

/**
 * Created by cxf on 2018/9/17.
 */

public class CommonHttpUtil {

    public static final String SALT = HttpClient.SALT;

    private static final String TAG = "CommonHttpUtil";

    /**
     * 初始化
     */
    public static void init() {
        HttpClient.getInstance().init();
    }

    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }

    /**
     * 使用腾讯定位sdk获取 位置信息
     *
     * @param lng 经度
     * @param lat 纬度
     * @param poi 是否要查询POI
     */
    public static void getAddressInfoByTxLocaitonSdk(final double lng, final double lat, final int poi, int pageIndex, String tag, final HttpCallback commonCallback) {
        String txMapAppKey = CommonAppConfig.getInstance().getTxMapAppKey();
        String s = "/ws/geocoder/v1/?get_poi=" + poi + "&key=" + txMapAppKey + "&location=" + lat + "," + lng
                + "&poi_options=address_format=short;radius=1000;page_size=20;page_index=" + pageIndex + ";policy=5" + CommonAppConfig.getInstance().getTxMapAppSecret();
        String sign = MD5Util.getMD5(s);
        GetRequest getRequest = OkGo.<String>get("https://apis.map.qq.com/ws/geocoder/v1/")
                .params("location", lat + "," + lng)
                .params("get_poi", poi)
                .params("poi_options", "address_format=short;radius=1000;page_size=20;page_index=" + pageIndex + ";policy=5")
                .params("key", txMapAppKey)
                .params("sig", sign)
                .tag(tag);

        L.e("getRequest==" + getRequest.getParams().toString());
        getRequest.execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                JSONObject obj = JSON.parseObject(response.body());
                if (obj != null && commonCallback != null) {
                    commonCallback.onSuccess(obj.getIntValue("status"), "", new String[]{obj.getString("result")});
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                if (commonCallback != null) {
                    commonCallback.onError();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (commonCallback != null) {
                    commonCallback.onFinish();
                }
            }
        });
    }

    /**
     * 使用腾讯地图API进行搜索
     *
     * @param lng 经度
     * @param lat 纬度
     */
    public static void searchAddressInfoByTxLocaitonSdk(final double lng, final double lat, String keyword, int pageIndex, final HttpCallback commonCallback) {

        String txMapAppKey = CommonAppConfig.getInstance().getTxMapAppKey();
        String s = "/ws/place/v1/search?boundary=nearby(" + lat + "," + lng + ",1000)&key=" + txMapAppKey + "&keyword=" + keyword + "&orderby=_distance&page_index=" + pageIndex +
                "&page_size=20" + CommonAppConfig.getInstance().getTxMapAppSecret();
        String sign = MD5Util.getMD5(s);
        OkGo.<String>get("https://apis.map.qq.com/ws/place/v1/search")
                .params("keyword", keyword)
                .params("boundary", "nearby(" + lat + "," + lng + ",1000)&orderby=_distance&page_size=20&page_index=" + pageIndex)
                .params("key", txMapAppKey)
                .params("sig", sign)
                .tag(CommonHttpConsts.GET_MAP_SEARCH)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject obj = JSON.parseObject(response.body());
                        if (obj != null && commonCallback != null) {
                            commonCallback.onSuccess(obj.getIntValue("status"), "", new String[]{obj.getString("data")});
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (commonCallback != null) {
                            commonCallback.onError();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (commonCallback != null) {
                            commonCallback.onFinish();
                        }
                    }
                });
    }

    /**
     * 获取config
     */
    public static void getConfig(final CommonCallback<ConfigBean> commonCallback) {
        HttpClient.getInstance().get("Home.getConfig", CommonHttpConsts.GET_CONFIG)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            try {
                                JSONObject obj = JSON.parseObject(info[0]);
                                ConfigBean bean = JSON.toJavaObject(obj, ConfigBean.class);
                                CommonAppConfig appConfig = CommonAppConfig.getInstance();
                                appConfig.setConfig(bean);
                                CommonAppConfig.getInstance().setLevel(obj.getString("level"));
                                CommonAppConfig.getInstance().setAnchorLevel(obj.getString("levelanchor"));
                                SpUtil.getInstance().setStringValue(SpUtil.CONFIG, info[0]);
                                if (commonCallback != null) {
                                    commonCallback.callback(bean);
                                }
                            } catch (Exception e) {
                                String error = "info[0]:" + info[0] + "\n\n\n" + "Exception:" + e.getClass() + "---message--->" + e.getMessage();
                                ErrorActivity.forward("GetConfig接口返回数据异常", error);
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


    /**
     * QQ登录的时候 获取unionID 与PC端互通的时候用
     */
    public static void getQQLoginUnionID(String accessToken, final CommonCallback<String> commonCallback) {
        OkGo.<String>get("https://graph.qq.com/oauth2.0/me?access_token=" + accessToken + "&unionid=1")
                .tag(CommonHttpConsts.GET_QQ_LOGIN_UNION_ID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (commonCallback != null) {
                            String data = response.body();
                            data = data.substring(data.indexOf("{"), data.lastIndexOf("}") + 1);
                            L.e("getQQLoginUnionID------>" + data);
                            JSONObject obj = JSON.parseObject(data);
                            commonCallback.callback(obj.getString("unionid"));
                        }
                    }
                });
    }


    /*进入房间*/
    public static Observable<LiveBean> enterRoom(String liveuid, String stream) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid", liveuid)
                .put("stream", stream)
                .build();
        return RequestFactory.getRequestManager().postNormal("Live.Enter", parmMap).map(new Function<BaseResponse<JSONObject>, LiveBean>() {
            @Override
            public LiveBean apply(BaseResponse<JSONObject> jsonObjectBaseResponse) throws Exception {
                Data<JSONObject> data = jsonObjectBaseResponse.getData();
                ToastUtil.show(data.getMsg());
                if (data.getCode() == 700) {
                    RouteUtil.forwardLoginInvalid(data.getMsg());
                    return null;
                }
                JSONObject jsonObject = data.getInfo().get(0);
                LiveBean liveBean = JSON.toJavaObject(jsonObject, LiveBean.class);
                liveBean.setExpandParm(jsonObject.toJSONString()); //额外参数保存进入直播间可以调用
                CommonAppConfig.getInstance().getUserBean().setLevel(jsonObject.getIntValue("userlevel"));
                return liveBean;
            }
        });
    }


    /**
     * 关注别人 或 取消对别人的关注的接口
     */
    public static void setAttention(String touid, CommonCallback<Integer> callback) {
        setAttention(CommonHttpConsts.SET_ATTENTION, touid, callback);
    }

    /**
     * 关注别人 或 取消对别人的关注的接口
     */
    public static void setAttention(String tag, final String touid, final CommonCallback<Integer> callback) {
        if (touid.equals(CommonAppConfig.getInstance().getUid())) {
            ToastUtil.show(WordUtil.getString(R.string.cannot_follow_self));
            return;
        }
        HttpClient.getInstance().get("User.setAttent", tag)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            int isAttention = JSON.parseObject(info[0]).getIntValue("isattent");//1是 关注  0是未关注
                            EventBus.getDefault().post(new FollowEvent(touid, isAttention));
                            if (callback != null) {
                                callback.callback(isAttention);
                            }
                            ToastUtil.show(msg);
                        }
                    }
                });
    }

    /*跟新订单立即服务的状态*/
    public static Observable<Boolean> updateReceptOrder(String orderid) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("orderid", orderid)

                .build();
        return RequestFactory.getRequestManager().noReturnPost("Orders.upReceptOrder", parmMap);

    }

    public static Observable<Boolean> upReceptStatus(String orderid, int receptStatus) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("orderid", orderid)
                .put("recept_status", receptStatus)
                .build();
        return RequestFactory.getRequestManager().noReturnPost("Orders.upReceptStatus", parmMap);

    }


    /**
     * 获取多个订单详情
     *
     * @param orderIds 多个订单 id 逗号拼接
     */
    public static void getMutiOrderDetail(String orderIds, HttpCallback callback) {
        HttpClient.getInstance().get("Orders.GetOrderDetails", CommonHttpConsts.GET_MUTI_ORDER_DETAIL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("orderids", orderIds)
                .execute(callback);
    }

    public static void getReceptDetails(String orderIds, HttpCallback callback) {
        HttpClient.getInstance().get("Orders.getReceptDetails", CommonHttpConsts.GET_MUTI_ORDER_DETAIL)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("orderids", orderIds)
                .execute(callback);
    }


    /**
     * 充值页面，我的钻石
     */


    public static void getBalance(HttpCallback callback) {
        HttpClient.getInstance().get("Charge.getBalance", CommonHttpConsts.GET_BALANCE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("type", 1)
                .execute(callback);
    }


    /**
     * 用支付宝充值 的时候在服务端生成订单号
     *
     * @param callback
     */
    public static void getAliOrder(String parmas, HttpCallback callback) {
        HttpClient.getInstance().get(parmas, CommonHttpConsts.GET_ALI_ORDER)
                .execute(callback);
    }

    /**
     * 用微信支付充值 的时候在服务端生成订单号
     *
     * @param callback
     */
    public static void getWxOrder(String parmas, HttpCallback callback) {
        HttpClient.getInstance().get(parmas, CommonHttpConsts.GET_WX_ORDER)
                .execute(callback);
    }

    /**
     * 用谷歌支付充值 的时候在服务端生成订单号
     *
     * @param callback
     */
    public static void getGoogleOrder(String changeid, String coin, String money, HttpCallback callback) {
        HttpClient.getInstance().get("Charge.GetGoogleOrder", CommonHttpConsts.GET_GOOGLE_ORDER)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("changeid", changeid)
                .params("coin", coin)
                .params("money", money)
                .execute(callback);
    }

    /**
     * 用paypal充值 的时候在服务端生成订单号
     */
    public static void getPaypalOrder(String changeid, String coin, String money, String type, HttpCallback callback) {
        HttpClient.getInstance().get("Charge.getOrder", CommonHttpConsts.GET_PAYPAL_ORDER)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("changeid", changeid)
                .params("coin", coin)
                .params("money", money)
                .params("type", type)
                .execute(callback);
    }

    /**
     * google支付回调
     */

    public static void checkGooglePay(String callbackUrl, String OriginalJson, String Signature, String OrderId, String order, HttpCallback callback) {
        OkGo.<JsonBean>post(callbackUrl)
                .params("signed_data", OriginalJson)
                .params("signature", Signature)
                .params("google_orderid", OrderId)
                .params("orderid", order)
                .execute(callback);
    }


    //不做任何操作的HttpCallback
    public static final HttpCallback NO_CALLBACK = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {

        }
    };

    /**
     * 上传文件 获取七牛云token的接口
     */
    public static void getUploadQiNiuToken(HttpCallback callback) {
        HttpClient.getInstance().get("Upload.getQiniuToken", CommonHttpConsts.GET_UPLOAD_QI_NIU_TOKEN)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 获取存储区域信息：
     * http://yuedantest.yunbaozb.com//api/?service=Upload.getCosInfo&uid=100831&token=cc671a78dd3a870bb15c3e6ff8fb1b48
     * 参数：UID：用户ID
     * token：token
     * 返回值：亚马逊cloudtype值：aws
     */
    public static void getUploadCosInfo(HttpCallback callback) {
        HttpClient.getInstance().get("Upload.getCosInfo", CommonHttpConsts.GET_COS_INFO)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 把自己的位置信息上传到服务器
     *
     * @param lng
     * @param lat
     */
    public static void setLocaiton(double lng, double lat) {
        HttpClient.getInstance().get("User.SetLocal", CommonHttpConsts.SET_LOCAITON)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("lng", lng)
                .params("lat", lat)
                .execute(NO_CALLBACK);
    }

    /*获取技能等级level*/
    public static Observable<List<ConditionLevel>> getSkillLevel(String skillid) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("skillid", skillid)
                .build();
        return RequestFactory.getRequestManager().get("Skill.GetLevel", parmMap, ConditionLevel.class);
    }

    /**
     * 获取视频价格说明
     */
    public static void getVideoPriceTip(HttpCallback callback) {
        HttpClient.getInstance().get("User.GetVideoInfo", CommonHttpConsts.GET_VIDEO_PRICE_TIP)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 获取语音价格说明
     */
    public static void getVoicePriceTip(HttpCallback callback) {
        HttpClient.getInstance().get("User.GetVoiceInfo", CommonHttpConsts.GET_VOICE_PRICE_TIP)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 拉黑对方， 解除拉黑
     */
    public static void setBlack(final String toUid) {
        HttpClient.getInstance().get("User.setBlack", CommonHttpConsts.SET_BLACK)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", toUid)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            EventBus.getDefault().post(new BlackEvent(toUid, JSON.parseObject(info[0]).getIntValue("isblack")));
                        }
                        ToastUtil.show(msg);
                    }
                });
    }


    public static void checkBlack(final String toUid, HttpCallback httpCallback) {
        HttpClient.getInstance().get("User.checkBlack", CommonHttpConsts.CHECK_BLACK)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", toUid)
                .execute(httpCallback);

    }


    /**
     * 获取黑名单
     */
    public static void getBlackList(HttpCallback callback) {
        HttpClient.getInstance().get("User.getBlackList", CommonHttpConsts.GET_BLACK_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /*url 是默认的tag可以显式的取消*/
    public static <T> io.reactivex.Observable<List<T>> request(HttpMethod httpMethod, String url, HttpParams params, final Class<T> cs, final boolean showMsg) {
        url = appendUrl(url);

        Type type = null;
        // L.e(TAG,"request===SeverConfig.jsonType= "+SeverConfig.jsonType);
        if (SeverConfig.jsonType == SeverConfig.FAST_JSON) {
            type = new com.alibaba.fastjson.util.ParameterizedTypeImpl(new Type[]{cs}, null, BaseResponse.class);
        } else {
            type = new ParameterizedTypeImpl(BaseResponse.class, new Class[]{cs});
        }
        Observable<BaseResponse<T>> observable = RxUtils.request(httpMethod, url, type, params);
        return observable.map(new Function<BaseResponse<T>, List<T>>() {
            @Override
            public List<T> apply(BaseResponse<T> response) {
                Data<T> data = response.getData();
                try {
                    if (data == null) {
                        return null;
                    }
                    if (data.getCode() == 700) {
                        L.e("报700-get");
                        //token过期，重新登录
                        RouteUtil.forwardLoginInvalid(data.getMsg());
                    }
                    if (showMsg) {
                        ToastUtil.show(data.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return data.getInfo();
            }
        });
    }

    private static String appendUrl(String url) {
        return HttpClient.getInstance().getUrl() + url;
    }

    public static <T> io.reactivex.Observable<List<T>> get(String url, HttpParams params, Class<T> cs) {
        return request(HttpMethod.GET, url, params, cs, false);
    }

    public static <T> io.reactivex.Observable<List<T>> getAndShowMsg(String url, HttpParams params, Class<T> cs) {
        return request(HttpMethod.GET, url, params, cs, true);
    }

    public static <T> io.reactivex.Observable<List<T>> post(String url, HttpParams params, Class<T> cs) {
        return request(HttpMethod.POST, url, params, cs, false);
    }

    public static io.reactivex.Observable<Boolean> postNoReturn(String url, HttpParams params) {
        url = appendUrl(url);

        Type type = new ParameterizedTypeImpl(BaseResponse.class, new Class[]{Void.class});
        Observable<BaseResponse> observable = RxUtils.request(HttpMethod.GET, url, type, params);
        return observable.map(new Function<BaseResponse, Boolean>() {
            @Override
            public Boolean apply(BaseResponse baseResponse) throws Exception {
                ToastUtil.show(baseResponse.getData().getMsg());
                return baseResponse.getData().getCode() == 0;
            }
        });
    }

    /*转化为rxjava流的形式*/
    public static Observable<List<TxLocationPoiBean>> obseverAddressInfoByTxLocaitonSdk(final int poi, final int pageIndex, final String tag) {
        return Observable.create(new ObservableOnSubscribe<List<TxLocationPoiBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<TxLocationPoiBean>> e) throws Exception {
                getAddressInfoByTxLocaitonSdk(CommonAppConfig.getInstance().getLng(), CommonAppConfig.getInstance().getLat(), poi, pageIndex, tag, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            List<TxLocationPoiBean> list = JsonUtil.getJsonToList(JsonUtil.getString(info[0], "pois"), TxLocationPoiBean.class);
                            e.onNext(list);
                        }
                        e.onComplete();
                    }

                    @Override
                    public void onError() {
                        super.onError();
                        e.onComplete();
                    }
                });
            }
        });
    }

    public static io.reactivex.Observable<BaseResponse<JSONObject>> postNormal(String url, HttpParams params) {
        url = appendUrl(url);
        Type type = null;
        type = new ParameterizedTypeImpl(BaseResponse.class, new Class[]{JSONObject.class});
        Observable<BaseResponse<JSONObject>> observable = RxUtils.request(HttpMethod.GET, url, type, params);
        return observable;
    }


    /**
     * 获取游戏技能主页信息
     */
    public static void getSkillHome(String toUid, String gameId, HttpCallback callback) {
        HttpClient.getInstance().get("Skill.GetSkillHome", CommonHttpConsts.GET_SKILL_HOME)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("liveuid", toUid)
                .params("skillid", gameId)
                .execute(callback);
    }

    /**
     * 判断退款订单状态
     *
     * @param orderid
     * @param callback
     */
    public static void getRefundInfoStatus(String orderid, HttpCallback callback) {
        HttpClient.getInstance().get("Refund.getRefundinfo", "Refund.getRefundinfo")
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("orderid", orderid)
                .execute(callback);
    }

    /**
     * 获取订单退款状态：用于退款IM信息处理
     * http://yuedantest.yunbaozb.com/api/?service=Refund.getRefundStatus&uid=100761&token=a6da60462c624361c2f1548cad69fe32&orderid=1394
     * 参数：uid：用户ID
     * token：token
     * orderid：订单ID
     */
    public static void getOrderRefundStatus(String orderid, HttpCallback callback) {
        HttpClient.getInstance().get("Refund.getRefundStatus", "Refund.getRefundStatus")
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("orderid", orderid)
                .execute(callback);
    }


    public static void setRefundStatus(String orderid, int status, HttpCallback callback) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("orderid", orderid)
                .put("status", status)
                .build();
        String sign = StringUtil.createSign(parmMap, "uid", "token", "touid", "content");
        HttpClient.getInstance().get("Refund.setRefundStatus", "Refund.setRefundStatus")
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("orderid", orderid)
                .params("status", status)
                .params("sign", sign)
                .execute(callback);
    }

    /**
     * 亚马逊：上传图片
     * http://ybyuewan.yunbaozb.com/api/?service=Upload.uploadImage&uid=101432&token=5d0f92dc1757110877e64adccbf57a4b
     * 参数：UID：用户ID
     * token：token
     * file：文件流
     */
    public static void updateAWSFileImg(File file, String remoteName, HttpCallback callback) {
        HttpUploadClient.getInstance().post("Upload.uploadImage", CommonHttpConsts.POST_AWS_UPLOAD_FILE)
                .isMultipart(false)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("file", file)
                .params("an_name", remoteName)
                .execute(callback);
    }


    /**
     * 亚马逊：上传视频接口
     * http://yuedantest.yunbaozb.com/api/?service=Upload.uploadVideo&uid=101303&token=3a666e3045cb44354980b5e1df0791d6&file=
     */
    public static void updateAWSFileVideo(File file, String remoteName, HttpCallback callback) {
        HttpUploadClient.getInstance().post("Upload.uploadVideo", CommonHttpConsts.POST_AWS_UPLOAD_FILE)
                .isMultipart(true)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("file", file)
                .params("an_name", remoteName)
                .execute(callback);
    }


    /**
     * 获取美颜值
     */
    public static void getBeautyValue(HttpCallback callback) {
        HttpClient.getInstance().get("User.getBeautyParams", CommonHttpConsts.GET_BEAUTY_VALUE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }


    /**
     * 设置美颜值
     */
    public static void setBeautyValue(String jsonStr) {
        HttpClient.getInstance().get("User.setBeautyParams", CommonHttpConsts.SET_BEAUTY_VALUE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("params", jsonStr)
                .execute(NO_CALLBACK);
    }

}




