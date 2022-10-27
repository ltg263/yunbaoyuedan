package com.yunbao.im.business;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.UserBean;
import com.yunbao.im.event.AcceptCallEvent;
import com.yunbao.im.event.CancleCallEvent;
import com.yunbao.im.event.RefuseCallEvent;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.R;
import com.yunbao.im.config.CallConfig;
import com.yunbao.im.event.CallBusyEvent;
import com.yunbao.im.utils.ImMessageUtil;
import org.greenrobot.eventbus.EventBus;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class CallIMHelper {

    public static final String IM_CHAT_CALL = "call";//通话消息
    public static final String METHOD = "method";
    public static final String ACTION = "action";
    public static final String TIME = "time";
    public static final String CHAT_TYPE = "type";
    private static final String CONTENT = "content";
    private static final String AVATAR = "avatar";
    private static final String USER_NAME = "user_nickname";
    public static final String MSG_TIME = "msgTime";
    private static final String UID = "id";

    public static final int ACTION_CALL_START=0;   //发起通话
    public static final int ACTION_CALL_CANCEL = 1;//取消通话
    public static final int ACTION_CALL_ACCEPT = 2;//接受通话
    public static final int ACTION_CALL_REFUSE = 3;//拒绝通话
    public static final int ACTION_CALL_BUSY = 9;//正在忙碌
    private static Disposable disposable;

    public static void filterMessage(JSONObject obj, String senderId){
        if (obj == null) {
            return;
        }
        String method=obj.getString(METHOD);
        if(method.equals(IM_CHAT_CALL)){
            receiverCall(obj);
        }
    }

    public  static String actionString(int action,boolean isSelf,JSONObject jsonObject){
        if(action==ACTION_CALL_START){
           return isSelf ?WordUtil.getString(R.string.im_type_chat_start_1):WordUtil.getString(R.string.im_type_chat_start_2);
        }else if(action==ACTION_CALL_REFUSE){
            return isSelf?WordUtil.getString(R.string.im_type_chat_refuse_1):WordUtil.getString(R.string.im_type_chat_refuse_2);
        }

        else if(action==ACTION_CALL_CANCEL){
            String time=jsonObject.getString(TIME);
            if(!TextUtils.isEmpty(time)){
             return WordUtil.getString(R.string.im_type_chat_end,time);
            }else{
             return isSelf?WordUtil.getString(R.string.im_type_chat_cancel_1):WordUtil.getString(R.string.im_type_chat_cancel_2);
            }
        }
        else if(action==ACTION_CALL_BUSY){
            return isSelf?WordUtil.getString(R.string.im_type_chat_busy_1):WordUtil.getString(R.string.im_type_chat_busy_2);
        }
        return "";
    }

    private static void receiverCall(JSONObject obj) {
        int action=obj.getInteger(ACTION);
        if(action==ACTION_CALL_START){
            if(!isBusy(obj)){
              receiverStart(obj);
            }
        }else if(action==ACTION_CALL_CANCEL){
            receiverCancle(obj);
        }else if(action==ACTION_CALL_ACCEPT){
            receiverAccept(obj);
        }else if(action==ACTION_CALL_REFUSE){
            receiverRefuse(obj);
        }else if(action==ACTION_CALL_BUSY){
            receiverBusy(obj);
        }

    }

    private static void receiverBusy(JSONObject obj) {
        EventBus.getDefault().post(new CallBusyEvent());
    }

    private static boolean isBusy(JSONObject obj) {
        if(CallConfig.isBusy()){
            sendBusy(obj.getIntValue(CHAT_TYPE),obj.getString(UID));
        }
       return CallConfig.isBusy();
    }

    private static void receiverRefuse(JSONObject obj) {
        L.e("receiverRefuse==");
        EventBus.getDefault().post(new RefuseCallEvent());
        disposableLaunch();
    }

    private static void receiverAccept(JSONObject obj) {
        L.e("receiverAccept==");
        EventBus.getDefault().post(new AcceptCallEvent());
        disposableLaunch();
    }

    private static void disposableLaunch() {
       if(disposable!=null&&!disposable.isDisposed()){
          disposable.dispose();
          disposable=null;
       }
    }

    private static void receiverCancle(JSONObject obj) {
        L.e("receiverCancle==");
        EventBus.getDefault().post(new CancleCallEvent());
        disposableLaunch();
    }

    /*接收到开始事件 临时这么写*/
    private static void receiverStart(JSONObject obj) {
        long time=obj.getLongValue(MSG_TIME);
        int type=obj.getIntValue(CHAT_TYPE);
        UserBean userBean=new UserBean();
        userBean.setAvatar(obj.getString(AVATAR));
        userBean.setUserNiceName(obj.getString(USER_NAME));
        userBean.setId(obj.getString(UID));
        launchDelay(time,type,userBean);
    }

    private static void launchDelay(final long time,final int type,final UserBean userBean) {
        if(disposable!=null&&!disposable.isDisposed()){
            return;
        }
        disposable= Observable.just(true).delay(800,TimeUnit.MILLISECONDS).
        observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean bl) {
                    //判断收到的消息发出时间 是否比 当前时间 早一分钟，如果早于一分钟，则认为消息超时
                    long time2= System.currentTimeMillis()-time*1000;
                    if(time2>CallConfig.TIME_OUT_DURCATION*1000){
                        return;
                    }
                    RouteUtil.forwardCallActivity(Constants.ROLE_AUDIENCE,Integer.parseInt(CommonAppConfig.getInstance().getUid()),type,userBean);
                }
         });
    }

    /*通知发起聊天*/
    public static void sendStart(int type,String toUid){
        UserBean userBean= CommonAppConfig.getInstance().getUserBean();
        if(userBean==null){
           return;
        }
        try {
            org.json.JSONObject jsonObject=new org.json.JSONObject();
            jsonObject.put(CHAT_TYPE,type)
                    .put(METHOD,IM_CHAT_CALL)
                    .put(AVATAR,userBean.getAvatar())
                    .put(USER_NAME,userBean.getUserNiceName())
                    .put(UID,userBean.getId())
                    .put(ACTION,ACTION_CALL_START);
            ImMessageUtil.getInstance().sendCustomMessage(toUid, jsonObject.toString(), false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /*通知取消聊天*/
    public static void sendCancle(int type,String endCallTime,String toUid){
        try {
            org.json.JSONObject jsonObject=new org.json.JSONObject();
            jsonObject.put(METHOD,IM_CHAT_CALL);
            jsonObject.put(ACTION,ACTION_CALL_CANCEL);
            jsonObject.put(ACTION,ACTION_CALL_CANCEL);
            jsonObject.put(METHOD,IM_CHAT_CALL);
            jsonObject.put(CHAT_TYPE,type);
            if(!TextUtils.isEmpty(endCallTime)){
            jsonObject.put(TIME,endCallTime);
            }
            ImMessageUtil.getInstance().sendCustomMessage(toUid, jsonObject.toString(), true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*通知接受聊天*/
    public static void sendAccept(int type,String toUid){
        try {
            org.json.JSONObject jsonObject=new org.json.JSONObject();
            jsonObject.put(METHOD,IM_CHAT_CALL);
            jsonObject.put(ACTION,ACTION_CALL_ACCEPT);
            jsonObject.put(METHOD,IM_CHAT_CALL);
            jsonObject.put(CHAT_TYPE,type);
            ImMessageUtil.getInstance().sendCustomMessage(toUid, jsonObject.toString(), true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*通知拒绝聊天*/
    public static void sendRefuse(int type,String toUid){
        try {
            org.json.JSONObject jsonObject=new org.json.JSONObject();
            jsonObject.put(ACTION,ACTION_CALL_REFUSE);
            jsonObject.put(METHOD,IM_CHAT_CALL);
            jsonObject.put(CHAT_TYPE,type);
            ImMessageUtil.getInstance().sendCustomMessage(toUid, jsonObject.toString(), true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*通知繁忙*/
    public static void sendBusy(int type,String toUid){
        try {
            org.json.JSONObject jsonObject=new org.json.JSONObject();
            jsonObject.put(ACTION,ACTION_CALL_BUSY);
            jsonObject.put(METHOD,IM_CHAT_CALL);
            jsonObject.put(CHAT_TYPE,type);
            ImMessageUtil.getInstance().sendCustomMessage(toUid, jsonObject.toString(), true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
