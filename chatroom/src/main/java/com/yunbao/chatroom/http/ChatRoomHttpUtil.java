package com.yunbao.chatroom.http;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.http.HttpClient;
import com.yunbao.common.server.MapBuilder;
import com.yunbao.common.server.RequestFactory;
import com.yunbao.common.server.entity.BaseResponse;
import com.yunbao.common.server.entity.Data;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.chatroom.bean.ApplyHostInfo;
import com.yunbao.chatroom.bean.ApplyResult;
import com.yunbao.chatroom.bean.ListBean;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.bean.LiveBannerBean;
import com.yunbao.chatroom.bean.LiveEndResultBean;
import com.yunbao.chatroom.bean.LiveOrderCommitBean;
import com.yunbao.chatroom.bean.LiveSetInfo;
import com.yunbao.chatroom.bean.LiveUserBean;
import com.yunbao.chatroom.bean.OpenLiveCommitBean;

import java.util.List;
import java.util.Map;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class ChatRoomHttpUtil {
    /*开始直播*/
    public static Observable<LiveBean> startLive(OpenLiveCommitBean openLiveCommitBean) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("title", openLiveCommitBean.getTitle())
                .put("des", openLiveCommitBean.getNotice())
                .put("thumb", openLiveCommitBean.getConver())
                .put("type", openLiveCommitBean.getLiveType())
                .put("bgid", openLiveCommitBean.getRoomConverId())
                .build();
        return RequestFactory.getRequestManager().getAndShowMsg("Live.Start", parmMap, LiveBean.class).map(new Function<List<LiveBean>, LiveBean>() {
            @Override
            public LiveBean apply(List<LiveBean> liveBeans) throws Exception {
                return liveBeans.get(0);
            }
        });
    }

    /*改变直播状态，在成功推流后调用,状态，0关播1直播*/
    public static Observable<Boolean> changeLive(int type,String stream) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("type",type)
                .put("stream",stream)
                .build();
        return RequestFactory.getRequestManager().noReturnPost("Live.ChangeLive", parmMap);
    }

    /*结束直播*/
    public static Observable<LiveEndResultBean> endRoom(String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("stream",stream)
                .build();
        return RequestFactory.getRequestManager().get("Live.Stop", parmMap, LiveEndResultBean.class).map(new Function<List<LiveEndResultBean>, LiveEndResultBean>() {
            @Override
            public LiveEndResultBean apply(List<LiveEndResultBean> liveBeans) throws Exception {
                return liveBeans.get(0);
            }
        });
    }

    /*聊天室列表*/
    public static Observable<List<LiveBean>> getLiveList(int p,int type){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("p",p)
                .put("type",type)
                .build();
        return RequestFactory.getRequestManager().get("Live.GetLists", parmMap, LiveBean.class);
    }
    

    public static Observable<List<LiveBean>> getLiveAttenList(int p){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("p",p)
                .build();
        return RequestFactory.getRequestManager().get("Live.GetListsAtten", parmMap, LiveBean.class);
    }



    /*获取直播基本信息*/
    public static Observable<LiveBean> getLiveInfo(String liveuid){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid",liveuid)
                .build();
        return RequestFactory.getRequestManager().get("Live.GetInfo", parmMap, LiveBean.class).map(new Function<List<LiveBean>, LiveBean>() {
            @Override
            public LiveBean apply(List<LiveBean> liveBeans) throws Exception {
                return liveBeans.get(0);
            }
        });
    }



    /*获取聊天室用户列表*/
    public static Observable<List<LiveUserBean>> getLiveUserList(String liveuid,String stream,int p){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid",liveuid)
                .put("stream",stream)
                .put("p",p)
                .build();
        return RequestFactory.getRequestManager().get("Live.GetUserList", parmMap, LiveUserBean.class);
    }

    /*修改聊天室公告*/
    public static Observable<Boolean> setLiveDes(String des){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("des",des)
                .put("type",1)
                .build();
        return RequestFactory.getRequestManager().noReturnPost("Live.SetInfo", parmMap);
    }
    /*申请上麦*/
    public static Observable<BaseResponse<JSONObject>> applyLinkmic(String liveuid, String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid",liveuid)
                .put("stream",stream)
                .build();
        return RequestFactory.getRequestManager().postNormal("Linkmic.Apply", parmMap);
    }



    /*取消上麦*/
    public static Observable<Boolean> cancelLinkmic(String liveuid,String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid",liveuid)
                .put("stream",stream)
                .build();
        return RequestFactory.getRequestManager().noReturnPost("Linkmic.cancel", parmMap);
    }



    /*用于主播同意用户上麦*/
    public static Observable<Boolean> setUserUpMic(String touid,String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("touid",touid)
                .put("stream",stream)
                .build();
        return RequestFactory.getRequestManager().noReturnPost("Linkmic.SetMic", parmMap);
    }



    /*下麦*/
    public static Observable<Boolean> delMic(String touid,String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("touid",touid)
                .put("stream",stream)
                .build();
        return RequestFactory.getRequestManager().noReturnPost("Linkmic.SetMic", parmMap);
    }



    /*派单申请列表*/
    public static Observable<ApplyResult> getApplyList(String liveUid, String stream, int p){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid",liveUid)
                .put("stream",stream)
                .put("p",p)
                .build();
        return RequestFactory.getRequestManager().get("Linkmic.GetList", parmMap,ApplyResult.class).map(new Function<List<ApplyResult>, ApplyResult>() {
            @Override
            public ApplyResult apply(List<ApplyResult> applyResults) throws Exception {
                return applyResults.get(0);
            }
        });
    }
    /*闲谈申请列表*/
    public static Observable<ApplyResult> getChatgApplyList(String liveUid, String stream, int p){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid",liveUid)
                .put("stream",stream)
                .put("p",p)
                .build();
        return RequestFactory.getRequestManager().get("Linkmic.GetChatList", parmMap,ApplyResult.class).map(new Function<List<ApplyResult>, ApplyResult>() {
            @Override
            public ApplyResult apply(List<ApplyResult> applyResults) throws Exception {
                return applyResults.get(0);
            }
        });
    }


    /*上抢单人的麦*/
    public static Observable<Integer> upNormalMic(String liveUid, String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid",liveUid)
                .put("stream",stream)
                .build();
        return RequestFactory.getRequestManager().postNormal("Linkmic.UpMic", parmMap).map(new Function<BaseResponse<JSONObject>, Integer>() {
            @Override
            public Integer apply(BaseResponse<JSONObject> jsonObjectBaseResponse) throws Exception {
                Data<JSONObject> data=jsonObjectBaseResponse.getData();
                if (data != null && data.getCode() == 700){
                    RouteUtil.forwardLoginInvalid(data.getMsg());
                    return null;
                }
                ToastUtil.show(data.getMsg());
                JSONObject jsonObject= ListUtil.safeGetData(data.getInfo(),0);
                return jsonObject.getIntValue("sitid");
            }
        });
    }



    /*推送订单*/
    public static Observable<Boolean> sendDispatchOrder(LiveOrderCommitBean liveOrderCommitBean, String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("skillid",liveOrderCommitBean.getSkillId())
                .put("levelid",liveOrderCommitBean.getLevel())
                .put("sex",liveOrderCommitBean.getSex())
                .put("age",liveOrderCommitBean.getAge())
                .put("stream",stream)
                .put("coin",liveOrderCommitBean.getPrice())
                .build();
        return RequestFactory.getRequestManager().noReturnPost("Dispatch.Send", parmMap);
    }

    //全站魅力榜
    public static Observable<List<ListBean>> getCharm(int p){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("p",p)
                .build();
        return RequestFactory.getRequestManager().get("Rank.GetCharm", parmMap,ListBean.class);
    }

    //主播贡献榜
    public static Observable<List<ListBean>> getLiveContri(String liveuid,int p){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid",liveuid)
                .put("p",p)
                .build();
        return RequestFactory.getRequestManager().get("Rank.GetLiveContri", parmMap,ListBean.class);
    }


    /*观众调用获取直播结束的信息*/
    public static Observable<LiveEndResultBean> getLiveEndInfo(String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("stream",stream)
                .build();
        return RequestFactory.getRequestManager().get("Live.StopInfo", parmMap, LiveEndResultBean.class).map(new Function<List<LiveEndResultBean>, LiveEndResultBean>() {
            @Override
            public LiveEndResultBean apply(List<LiveEndResultBean> liveBeans) throws Exception {
                return liveBeans.get(0);
            }
        });
    }


    /*聊天室弹框*/
    public static Observable<JSONObject> getPop(String liveuid,String touid,String skillid){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid",liveuid)
                .put("touid",touid)
                .put("skillid",skillid)
                .build();
        return RequestFactory.getRequestManager().postNormal("Live.GetPop", parmMap).map(new Function<BaseResponse<JSONObject>, JSONObject>() {
            @Override
            public JSONObject apply(BaseResponse<JSONObject> jsonObjectBaseResponse) throws Exception {
                Data data = jsonObjectBaseResponse.getData();
                if (data != null && data.getCode() == 700){
                    RouteUtil.forwardLoginInvalid(data.getMsg());
                    return null;
                }
                return jsonObjectBaseResponse.getData().getInfo().get(0);
            }
        });
    }

    /**
     * 聊天室 在线人数
     * @param liveuid
     * @param stream
     * @return
     */
    public static Observable<Integer> getUserNums(String liveuid,String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid",liveuid)
                .put("stream",stream)
                .build();
        return RequestFactory.getRequestManager().postNormal("Live.GetUserNums", parmMap).map(new Function<BaseResponse<JSONObject>, Integer>() {
            @Override
            public Integer apply(BaseResponse<JSONObject> jsonObjectBaseResponse) throws Exception {
                JSONObject jsonObject=jsonObjectBaseResponse.getData().getInfo().get(0);
                Data data = jsonObjectBaseResponse.getData();
                if (data != null && data.getCode() == 700){
                    RouteUtil.forwardLoginInvalid(data.getMsg());
                    return null;
                }
                return jsonObject.getIntValue("nums");
            }
        });
    }

    /*进入房间*/
    public static Observable<LiveBean> enterRoom(String uid, String stream) {
        return CommonHttpUtil.enterRoom(uid,stream);
    }
    /*申请当主持人*/
    public static Observable<Boolean> applyLiveHost(int length,String voice) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("length",length)
                .put("voice",voice)
                .build();
        return RequestFactory.getRequestManager().noReturnPost("Liveapply.Apply", parmMap);
    }

    /*获取主持人申请情况*/
    public static Observable<ApplyHostInfo> getLiveApplyInfo(){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .build();
        return RequestFactory.getRequestManager().get("Liveapply.GetInfo", parmMap, ApplyHostInfo.class).map(new Function<List<ApplyHostInfo>, ApplyHostInfo>() {
            @Override
            public ApplyHostInfo apply(List<ApplyHostInfo> liveInfoArray) throws Exception {
                return liveInfoArray.get(0);
            }
        });
    }


    /*获取设置直播信息*/
    public static Observable<LiveSetInfo> getLiveSetInfo(){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .build();
        return RequestFactory.getRequestManager().get("Live.GetSetInfo", parmMap, LiveSetInfo.class).map(new Function<List<LiveSetInfo>, LiveSetInfo>() {
            @Override
            public LiveSetInfo apply(List<LiveSetInfo> liveInfoArray) throws Exception {
                return liveInfoArray.get(0);
            }
        });
    }


    /*闲聊模式申请上麦*/
    public static Observable<BaseResponse<JSONObject>> gossipChatApply(String liveuid,String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid", liveuid)
                .put("stream", stream)
                .build();
        return RequestFactory.getRequestManager().postNormal("Linkmic.Chat_apply", parmMap);
    }

    /*闲聊模式取消申请上麦*/
    public static Observable<Boolean>  gossipChatCancel(String liveuid,String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid", liveuid)
                .put("stream", stream)
                .build();
        return RequestFactory.getRequestManager().noReturnPost("Linkmic.Chat_cancel", parmMap);
    }

    /*闲聊模式上麦*/
    public static Observable<JSONObject>  gossipChatSetMic(String touid,String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("touid", touid)
                .put("stream", stream)
                .build();
        return RequestFactory.getRequestManager().postNormal("Linkmic.chat_setMic", parmMap)
                .map(new Function<BaseResponse<JSONObject>, JSONObject>() {
                    @Override
                    public JSONObject apply(BaseResponse<JSONObject> jsonObjectBaseResponse) throws Exception {
                        Data data= jsonObjectBaseResponse.getData();
                        ToastUtil.show(data.getMsg());
                        if (data != null && data.getCode() == 700){
                            RouteUtil.forwardLoginInvalid(data.getMsg());
                            return null;
                        }
                        return jsonObjectBaseResponse.getData().getInfo().get(0);
                    }
                });
        }

    /*点歌同意歌手上麦*/
    public static Observable<Boolean>  songSetMic(String touid,String sitid,String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("touid", touid)
                .put("stream", stream)
                .put("sitid", sitid)
                .build();
        return RequestFactory.getRequestManager().noReturnPost("Linkmic.Song_setMic", parmMap);
    }

   /*交友申请列表*/
    public static Observable<ApplyResult> getFriendgApplyList(String liveUid, String stream, int p,int sex){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid",liveUid)
                .put("stream",stream)
                .put("p",p)
                .put("sex",sex)
                .build();
        return RequestFactory.getRequestManager().get("Linkmic.GetJyList", parmMap,ApplyResult.class).map(new Function<List<ApplyResult>, ApplyResult>() {
            @Override
            public ApplyResult apply(List<ApplyResult> applyResults) throws Exception {
                return applyResults.get(0);
            }
        });
    }

    /*同意交友申请*/
    public static Observable<JSONObject>  friendSetMic(String touid,String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("touid", touid)
                .put("stream", stream)
                .build();
        return RequestFactory.getRequestManager().postNormal("Linkmic.Jy_setMic", parmMap)
                .map(new Function<BaseResponse<JSONObject>, JSONObject>() {
                    @Override
                    public JSONObject apply(BaseResponse<JSONObject> jsonObjectBaseResponse) throws Exception {
                         Data data= jsonObjectBaseResponse.getData();
                        if (data != null && data.getCode() == 700){
                            RouteUtil.forwardLoginInvalid(data.getMsg());
                            return null;
                        }
                         ToastUtil.show(data.getMsg());
                        return jsonObjectBaseResponse.getData().getInfo().get(0);
                    }
         });
    }

    /*交友模式取消申请上麦*/
    public static Observable<Boolean>  friendCancel(String liveuid,String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid", liveuid)
                .put("stream", stream)
                .build();
        return RequestFactory.getRequestManager().noReturnPost("Linkmic.Jy_cancel", parmMap);
    }
    /*交友模式的心动选择*/
    public static Observable<Boolean>  setHeart(String liveuid,String stream,String tositid){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid", liveuid)
                .put("stream", stream)
                .put("tositid", tositid)
                .build();
        return RequestFactory.getRequestManager().noReturnPost("Linkmic.SetHeart", parmMap);
    }

    /*交友模式申请上麦*/
    public static Observable<BaseResponse<JSONObject>> friendApply(String liveuid,String stream){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("liveuid", liveuid)
                .put("stream", stream)
                .build();
        return RequestFactory.getRequestManager().postNormal("Linkmic.Jy_apply", parmMap);
    }

    /*获取聊天室banner*/
    public static Observable<List<LiveBannerBean>>  getLiveBanner(){
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())

                .build();
        return RequestFactory.getRequestManager().get("Live.GetSlides", parmMap, LiveBannerBean.class
        );

    }

    /**
     * 获取技能价格
     */
    public static void getSkillPrice(String skillId, HttpCallback callback) {
        HttpClient.getInstance().get("Skill.GetCoins", "GetCoins")
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("skillid", skillId)
                .execute(callback);
    }


    /**
     * .直播间收益榜单
     * @param p
     * @return
     */
    public static Observable<List<ListBean>> getLiveProfitList(String liveUid,int p){
        Map<String, Object> parmMap = MapBuilder.factory()
                .put("uid", liveUid)
                .put("p",p)
                .build();
        return RequestFactory.getRequestManager().get("Zlive.getLiveProfitList", parmMap,ListBean.class);
    }

    /**
     * 直播间消费榜单
     * @param stream
     * @param p
     * @return
     */
    public static Observable<List<ListBean>> getLiveConsumeList(String stream,int p){
        Map<String, Object> parmMap = MapBuilder.factory()
                .put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("stream",stream)
                .put("p",p)
                .build();
        return RequestFactory.getRequestManager().get("Zlive.getLiveConsumeList", parmMap,ListBean.class);
    }


}
