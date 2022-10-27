package com.yunbao.dynamic.http;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.adapter.radio.CheckEntity;
import com.yunbao.common.server.MapBuilder;
import com.yunbao.common.server.RequestFactory;
import com.yunbao.common.server.entity.BaseResponse;
import com.yunbao.common.server.entity.Data;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.dynamic.bean.CommitPubDynamicBean;
import com.yunbao.dynamic.bean.DynamicBean;
import com.yunbao.dynamic.bean.DynamicCommentBean;
import com.yunbao.dynamic.bean.DynamicUserBean;
import com.yunbao.dynamic.bean.MyDynamicBean;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class DynamicHttpUtil {

    /*动态关注*/
    public static Observable<List<MyDynamicBean>> getDynamicFollow(String sex, String age, String skillid, int p) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("sex", sex)
                .put("age", age)
                .put("skillid", skillid)
                .put("p", p)
                .build();
        return RequestFactory.getRequestManager().get("Dynamic.GetFollow", parmMap, MyDynamicBean.class);
    }
    //.map(filterNewDynamicData())

    /**
     * 关注用户列表
     * http://yuedantest.yunbaozb.com/api/?service=User.getFollowlist&p=1&uid=100349&token=00fea0555f3f958755418609bcdc4216&sex=0&age=0&skillid=0
     * 参数：uid：用户ID
     *            token：token
     *             sex：性别，0不限1男2女
     *              age： 年龄，0不限，1-70，2-80，3-90，4-00，5-10
     *       skillid：技能ID
     *   p：分页
     */
    public static Observable<List<DynamicUserBean>> getUserDynamicFollow(String sex, String age, String skillid, int p) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("sex", sex)
                .put("age", age)
                .put("skillid", skillid)
                .put("p", p)
                .build();
        return RequestFactory.getRequestManager().get("User.getFollowlist", parmMap, DynamicUserBean.class).map(filterUserDynamicData());
    }



    public static Function<List<DynamicUserBean>, List<DynamicUserBean>> filterUserDynamicData(){
        return new Function<List<DynamicUserBean>, List<DynamicUserBean>>() {
            @Override
            public List<DynamicUserBean> apply(List<DynamicUserBean> dynamicBeans) throws Exception {
                if(!ListUtil.haveData(dynamicBeans)){
                    return dynamicBeans;
                }
                Iterator<DynamicUserBean> iterator=dynamicBeans.iterator();
                while (iterator.hasNext()){
                    DynamicUserBean dynamicBean= iterator.next();
//                    if(dynamicBean.getIsblack()==1){
//                        iterator.remove();
//                    }
                }
                return dynamicBeans;
            }
        };
    }

    //从列表中移除被拉黑的人，不知道是何用处
    public static Function<List<MyDynamicBean>, List<MyDynamicBean>> filterNewDynamicData(){
        return new Function<List<MyDynamicBean>, List<MyDynamicBean>>() {
            @Override
            public List<MyDynamicBean> apply(List<MyDynamicBean> dynamicBeans) throws Exception {
                if(!ListUtil.haveData(dynamicBeans)){
                    return dynamicBeans;
                }
                Iterator<MyDynamicBean> iterator=dynamicBeans.iterator();
                while (iterator.hasNext()){
                    MyDynamicBean dynamicBean= iterator.next();
                    if(dynamicBean.getIsblack()==1){
                        iterator.remove();
                    }
                }
                return dynamicBeans;
            }
        };
    }




    public static Function<List<DynamicBean>, List<DynamicBean>> filterDynamicData(){
        return new Function<List<DynamicBean>, List<DynamicBean>>() {
            @Override
            public List<DynamicBean> apply(List<DynamicBean> dynamicBeans) throws Exception {
                if(!ListUtil.haveData(dynamicBeans)){
                    return dynamicBeans;
                }
                Iterator<DynamicBean> iterator=dynamicBeans.iterator();
                while (iterator.hasNext()){
                    DynamicBean dynamicBean= iterator.next();
                    if(dynamicBean.getIsblack()==1){
                      iterator.remove();
                    }
                }
                return dynamicBeans;
            }
        };
    }

    /*动态最新*/
    //.map(filterNewDynamicData())
    public static Observable<List<MyDynamicBean>> getDynamicNew(String sex, String age, String skillid, int p) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("sex", sex)
                .put("age", age)
                .put("skillid", skillid)
                .put("p", p)
                .build();
        return RequestFactory.getRequestManager().get("Dynamic.GetNew", parmMap, MyDynamicBean.class);
    }

    /**
     * 最新 认证三天用户
     * http://yuedantest.yunbaozb.com/api/?service=User.getAuthlist&p=1&uid=100349&token=00fea0555f3f958755418609bcdc4216&sex=0&age=0&skillid=0
     * 参数：uid：用户ID
     *            token：token
     *             sex：性别，0不限1男2女
     *              age： 年龄，0不限，1-70，2-80，3-90，4-00，5-10
     *       skillid：技能ID
     *   p：分页
     */
    public static Observable<List<DynamicUserBean>> getUserAuthlist(String sex, String age, String skillid, int p) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("sex", sex)
                .put("age", age)
                .put("skillid", skillid)
                .put("p", p)
                .build();
        return RequestFactory.getRequestManager().get("User.getAuthlist", parmMap, DynamicUserBean.class).map(filterUserDynamicData());
    }



    /*动态推荐*/
    //.map(filterNewDynamicData())
    public static Observable<List<MyDynamicBean>> getDynamicRecom(String sex, String age, String skillid, int p) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("sex", sex)
                .put("age", age)
                .put("skillid", skillid)
                .put("p", p)
                .build();
        return RequestFactory.getRequestManager().get("Dynamic.GetRecom", parmMap, MyDynamicBean.class);
    }

    /**
     * 用户推荐接口：
     * http://yuedantest.yunbaozb.com/api/?service=User.getRecom&p=1&uid=100349&token=00fea0555f3f958755418609bcdc4216&sex=0&age=0&skillid=0
     * 参数：uid：用户ID
     *            token：token
     *             sex：性别，0不限1男2女
     *              age： 年龄，0不限，1-70，2-80，3-90，4-00，5-10
     *       skillid：技能ID
     *   p：分页
     */
    public static Observable<List<DynamicUserBean>> getUserDynamicRecom(String sex, String age, String skillid, int p) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("sex", sex)
                .put("age", age)
                .put("skillid", skillid)
                .put("p", p)
                .build();
        return RequestFactory.getRequestManager().get("User.getRecom", parmMap, DynamicUserBean.class).map(filterUserDynamicData());
    }


    /*动态点赞2*/
    public static Observable<Integer> dynamicAddLike(String did) {
        return dynamicAddLikeDefault(did).map(new Function<JSONObject, Integer>() {
            @Override
            public Integer apply(JSONObject jsonObject) throws Exception {
                if (jsonObject == null){
                    return null;
                }
                return jsonObject.getIntValue("islike");
            }
        });
    }

    /*动态点赞1*/
    public static Observable<JSONObject> dynamicAddLikeDefault(String did) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("did", did)
                .build();
        String sign=StringUtil.createSign(parmMap,"uid","token","did");
        parmMap.put("sign",sign);
        return RequestFactory.getRequestManager().postNormal("Dynamic.AddLike", parmMap).map(new Function<BaseResponse<com.alibaba.fastjson.JSONObject>, JSONObject>() {
            @Override
            public JSONObject apply(BaseResponse<JSONObject> jsonObjectBaseResponse) throws Exception {
                Data<JSONObject>data=jsonObjectBaseResponse.getData();
                if (data.getCode() == 700){
                    RouteUtil.forwardLoginInvalid(data.getMsg());
                    return null;
                }
                List<JSONObject> jsonObjects =data.getInfo();
                if (jsonObjectBaseResponse.getData().getCode() != 0){
                    ToastUtil.show(jsonObjectBaseResponse.getData().getMsg());
                    return null;
                }
                return jsonObjects.get(0);
            }
        });
    }


    /*发布动态*/
    public static Observable<Boolean> setDynamic(CommitPubDynamicBean commitPubDynamicBean) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("content", commitPubDynamicBean.getContent())
                .put("thumbs", commitPubDynamicBean.getThumbsString())
                .put("skillid", commitPubDynamicBean.getSkillid())
                .put("video", commitPubDynamicBean.getVideo())
                .put("video_t", commitPubDynamicBean.getVideo_t())
                .put("voice", commitPubDynamicBean.getVoice())
                .put("voice_l", commitPubDynamicBean.getVoice_l())
                .put("location", commitPubDynamicBean.getLocation())
                .put("city", commitPubDynamicBean.getCity())
                .build();

        String sign=StringUtil.createSign(parmMap,"content","uid","token","thumbs","video","video_t","voice","skillid");
        parmMap.put("sign",sign);
        return RequestFactory.getRequestManager().noReturnPost("Dynamic.SetDynamic", parmMap);
    }

    /*我的动态列表*/
    public static Observable<List<MyDynamicBean>> getMyDynamic(int p) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("p", p)
                .build();
        return RequestFactory.getRequestManager().get("Dynamic.GetMyDynamics", parmMap, MyDynamicBean.class);
    }


    /*个人主页动态列表*/
    public static Observable<List<MyDynamicBean>> getDynamics(String liveUid,int p) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("p", p)
                .put("liveuid", liveUid)
                .build();
        return RequestFactory.getRequestManager().get("Dynamic.GetDynamics", parmMap, MyDynamicBean.class);
    }


    /*获取动态评论*/
    public static Observable<List<MyDynamicBean>> getComments(String did,String lastid) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("did", did)
                .put("lastid", lastid)
                .build();
        return RequestFactory.getRequestManager().get("Dynamic.GetComments", parmMap, MyDynamicBean.class);
    }


    /*获取举报理由*/
    public static Observable<List<CheckEntity>> getDynamicReport() {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .build();
        return RequestFactory.getRequestManager().get("Dynamic.GetReport", parmMap, CheckEntity.class);
    }




    /*举报动态*/
    public static Observable<Boolean> setDynamicReport(String did,String content,String thumbs) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("did", did)
                .put("thumbs",thumbs)
                .put("content",content)
                .build();
        String sign=StringUtil.createSign(parmMap,"uid","token","did","content");
        parmMap.put("sign",sign);
        return RequestFactory.getRequestManager().noReturnPost("Dynamic.SetReport", parmMap);
    }

    /*删除动态*/
    public static Observable<Boolean> delDynamic(String did) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("did", did)
                .build();
        String sign=StringUtil.createSign(parmMap,"uid","token","did");
        parmMap.put("sign",sign);
        return RequestFactory.getRequestManager().noReturnPost("Dynamic.Del", parmMap);
    }

    /*获取评论*/
    public static Observable<JSONObject> getDynaimcComments(String did,String lastid) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("did", did)
                .put("lastid",lastid)
                .build();
        return RequestFactory.getRequestManager().postNormal("Dynamic.GetComments", parmMap).map(new Function<BaseResponse<JSONObject>, JSONObject>() {
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

    /*获取回复*/
    public static Observable<List<DynamicCommentBean>> getDynaimcReply(String cid,String lastid) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("cid", cid)
                .put("lastid",lastid)
                .build();
        return RequestFactory.getRequestManager().get("Dynamic.GetReplys", parmMap, DynamicCommentBean.class);
    }


    /*评论点赞*/
    public static Observable<JSONObject> addDynamicCommnetLike(String cid,String did) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("did", did)
                .put("cid",cid)
                .build();
        String sign=StringUtil.createSign(parmMap,"uid","token","did","cid");
        parmMap.put("sign",sign);
        return RequestFactory.getRequestManager().postNormal("Dynamic.AddCommnetLike", parmMap).map(new Function<BaseResponse<JSONObject>, JSONObject>() {
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

    /*评论动态*/
    public static Observable<Boolean> setDynamicComment(String touid,String content,String did,String cid,String pid) {
        Map<String, Object> parmMap = MapBuilder.factory().put("uid", CommonAppConfig.getInstance().getUid())
                .put("token", CommonAppConfig.getInstance().getToken())
                .put("touid", touid)
                .put("content", content)
                .put("did", did)
                .put("cid",cid)
                .put("pid",pid)
                .build();
        String sign=StringUtil.createSign(parmMap,"uid","token","touid","content","did","cid","pid");
        parmMap.put("sign",sign);
        return RequestFactory.getRequestManager().noReturnPost("Dynamic.SetComment", parmMap);
    }


}