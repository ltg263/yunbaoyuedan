package com.yunbao.im.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupSystemElem;
import com.tencent.imsdk.TIMImage;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMImageType;
import com.tencent.imsdk.TIMLocationElem;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.TIMOfflinePushListener;
import com.tencent.imsdk.TIMOfflinePushNotification;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMSoundElem;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;
import com.tencent.imsdk.ext.message.TIMMessageExt;
import com.tencent.imsdk.session.SessionWrapper;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMConversationResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.ChatReceiveGiftBean;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.LanguageUtil;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.TxImCacheUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.R;
import com.yunbao.im.bean.ChatInfoBean;
import com.yunbao.im.bean.IMLiveBean;
import com.yunbao.im.bean.ImMessageBean;
import com.yunbao.im.bean.ImMsgLocationBean;
import com.yunbao.im.bean.ImUserBean;
import com.yunbao.im.bean.OrderTipBean;
import com.yunbao.im.business.CallIMHelper;
import com.yunbao.im.event.DripEvent;
import com.yunbao.im.event.ImUnReadCountEvent;
import com.yunbao.im.event.ImUserMsgEvent;
import com.yunbao.im.event.NowServiceEvent;
import com.yunbao.im.interfaces.ImClient;
import com.yunbao.im.interfaces.SendMsgResultCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static com.yunbao.im.business.CallIMHelper.ACTION;
import static com.yunbao.im.business.CallIMHelper.CHAT_TYPE;

/**
 * Created by cxf on 2019/4/2.
 */

public class TxImMessageUtil implements ImClient, TIMMessageListener, TIMOfflinePushListener {

    private static final String TAG = "??????IM";
    public static final String PREFIX = "";
    public static final String METHOD = "method";
    public static final String ORDER = "orders";
    public static final String DRIP = "drip";
    public static final String DISPATCH = "dispatch";
    public static final String NOW_SERVER = "nowserver";
    public static final String ORDER_START = "orderstart";
    public static final String REFUND_ORDER = "refundorder";
    public static final String AUTH_NOTICE = "authnotice";//????????????
    public static final String ORDER_HALL = "setdriporder";//?????????????????????


    public static final String CALL = CallIMHelper.IM_CHAT_CALL;
    private SimpleDateFormat mSimpleDateFormat;
    private String mImageString;
    private String mVoiceString;
    private String mLocationString;
    private String mFaceString;
    private TIMCallBack mUnReadCountCallBack;
    private TIMCallBack mEmptyCallBack;
    private SendMsgResultCallback mSendMsgResultCallback;
    private TIMValueCallBack<TIMMessage> mSendCompleteCallback;
    private TIMValueCallBack<TIMMessage> mSendCustomCallback;
    private StringBuilder mStringBuilder;
    private String mGroupId;
    private SoundPool mSoundPool;
    private int mSoundId = -1;
    private boolean mOpenChatActivity;//?????????????????????activity
    private boolean mCloseChatMusic;//?????????????????????
    private Handler mHandler;
    private static final int MSG_UNREAD_COUNT_DELAY_MILLIS = 500;//??????IM????????????????????????????????????????????????????????????????????????????????????????????????


    public TxImMessageUtil() {
        //mMap = new HashMap<>();
        mStringBuilder = new StringBuilder();
        mSimpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        mImageString = WordUtil.getString(R.string.im_type_image);
        mVoiceString = WordUtil.getString(R.string.im_type_voide);
        mLocationString = WordUtil.getString(R.string.im_type_location);
        mFaceString = WordUtil.getString(R.string.face);
        mCloseChatMusic = SpUtil.getInstance().getBooleanValue(SpUtil.CHAT_MUSIC_CLOSE);
        mUnReadCountCallBack = new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                CrashReport.postCatchedException(new IllegalArgumentException("i==" + i + "&&uid=" + s));
            }

            @Override
            public void onSuccess() {
                refreshAllUnReadMsgCount();
            }
        };
        mEmptyCallBack = new TIMCallBack() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess() {
            }
        };
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (CommonAppConfig.getInstance().isFrontGround()) {
//                    ToastUtil.show(WordUtil.getString(R.string.net_work_broken));
                }
            }
        };
    }


    @Override
    public void refreshMsgTypeString() {
        mImageString = WordUtil.getString(R.string.im_type_image);
        mVoiceString = WordUtil.getString(R.string.im_type_voide);
        mLocationString = WordUtil.getString(R.string.im_type_location);
        mFaceString = WordUtil.getString(R.string.face);
    }


    /**
     * ???app????????????uid??????IM?????????uid
     */
    private String getImUid(String uid) {
        if (Constants.IM_MSG_ADMIN.equals(uid)) {
            return uid;
        }
        return StringUtil.contact(PREFIX, uid);
    }

    /**
     * ???IM?????????uid??????app?????????uid
     */
    private String getAppUid(String from) {
        if (Constants.IM_MSG_ADMIN.equals(from)) {
            return from;
        }
        if (!TextUtils.isEmpty(from) && from.length() > PREFIX.length() && from.startsWith(PREFIX)) {
            return from.substring(PREFIX.length());
        }
        return "";
    }

    /**
     * ??????IM?????? ??????App?????????uid
     */
    private String getAppUid(TIMMessage msg) {
        if (msg == null) {
            return "";
        }

        String peer = msg.getConversation().getPeer();
        if (TextUtils.isEmpty(peer)) {
            return "";
        }
        return getAppUid(peer);
    }


    @Override
    public void init() {
        TIMManager timManager = TIMManager.getInstance();
        //??????????????????????????? ????????? SDK ????????????
        if (SessionWrapper.isMainProcess(CommonAppContext.sInstance)) {
            TIMSdkConfig config = new TIMSdkConfig(CommonAppConfig.TX_IM_APP_Id)
                    .enableLogPrint(false)
                    .setLogLevel(TIMLogLevel.OFF);

            timManager.init(CommonAppContext.sInstance, config);
        }


        //??????????????????
        TIMUserConfig userConfig = new TIMUserConfig();
        //???????????????????????????????????????
        userConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                L.e(TAG, "????????????????????????---->");
                RouteUtil.forwardLoginInvalid(WordUtil.getString(R.string.login_status_Invalid));
            }

            @Override
            public void onUserSigExpired() {
                L.e(TAG, "??????????????????????????????????????????---->");
            }
        });

        //?????????????????????????????????
        userConfig.setConnectionListener(new TIMConnListener() {
            @Override
            public void onConnected() {
                L.e(TAG, "????????????---->");
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
            }

            @Override
            public void onDisconnected(int code, String desc) {
                L.e(TAG, "????????????---->");
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(0, 2000);
                }
            }

            @Override
            public void onWifiNeedAuth(String name) {
                L.e(TAG, "onWifiNeedAuth");
            }
        });

//        //???????????????????????????
//        userConfig.setGroupEventListener(new TIMGroupEventListener() {
//            @Override
//            public void onGroupTipsEvent(TIMGroupTipsElem elem) {
//                L.e(TAG, "??????????????????----->" + elem.getTipsType());
//            }
//        });
        //???????????????????????????
        userConfig.setRefreshListener(new TIMRefreshListener() {
            @Override
            public void onRefresh() {
                //L.e(TAG, "????????????---onRefresh----->");
            }

            @Override
            public void onRefreshConversation(List<TIMConversation> conversations) {
                // L.e(TAG, "????????????---onRefreshConversation---->size: " + conversations.size());
            }
        });
        timManager.setUserConfig(userConfig);
        timManager.addMessageListener(TxImMessageUtil.this);
        // ???????????????????????????
        timManager.setOfflinePushListener(this);
    }

    @Override
    public void loginImClient(String uid) {
        String sign = SpUtil.getInstance().getStringValue(SpUtil.TX_IM_USER_SIGN);
        if (TextUtils.isEmpty(sign)) {
            ToastUtil.show("??????IM??????????????? ???????????????");
            return;
        }

        TIMManager.getInstance().login(uid, sign, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                L.e(TAG, "???????????? : " + code + " errmsg: " + desc);
                CommonAppConfig.getInstance().setLoginIM(false);
                ToastUtil.show("IM ???????????????" + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess() {
                L.e(TAG, "??????????????????");
                CommonAppConfig.getInstance().setLoginIM(true);
                refreshAllUnReadMsgCount();
                ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
                if (configBean != null) {
                    String groupId = configBean.getTxImGroupId();
                    L.e(TAG, "??????ID------> " + groupId);
                    if (!TextUtils.isEmpty(groupId)) {
                        mGroupId = groupId;
                        TIMGroupManager.getInstance().applyJoinGroup(groupId, "login", new TIMCallBack() {
                            @java.lang.Override
                            public void onError(int code, String desc) {
                                L.e(TAG, "?????????????????? : " + code + " errmsg: " + desc);
                            }

                            @java.lang.Override
                            public void onSuccess() {
                                L.e(TAG, "????????????????????????");
                            }
                        });
                    }
                }
            }
        });
    }


    @Override
    public void logoutImClient() {
        TIMManager timManager = TIMManager.getInstance();
        timManager.logout(null);
        CommonAppConfig.getInstance().setLoginIM(false);
        L.e(TAG, "????????????--->");
    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     *
     * @param list
     * @return
     */
    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        for (TIMMessage msg : list) {
            onReceiveMessage(msg);
        }
        return true; //??????true????????????????????????????????????????????????????????????
    }

    private void onReceiveMessage(final TIMMessage msg) {
        if (msg == null) {
            return;
        }
//        L.e(TAG, "onReceiveMessage--->" +msg.toString()+ getCustomMsgData(msg));
        //??????????????????????????????
        if ("@TIM#SYSTEM".equals(msg.getSender())) {
            if (msg.timestamp() < CommonAppConfig.getInstance().getLaunchTime()) {
                return;
            }
            if (msg.getElementCount() > 0) {
                TIMElem elem0 = msg.getElement(0);
                if (elem0 instanceof TIMGroupSystemElem) {
                    TIMGroupSystemElem systemElem = (TIMGroupSystemElem) elem0;
                    if (systemElem.getGroupId().equals(mGroupId)) {
                        String data = new String(systemElem.getUserData());
                        L.e(TAG, "????????????--------> " + data);
                        return;
                    }
                }
            }
        }
        final String uid = getAppUid(msg);
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        final int type = getMessageType(msg);
        if (type == 0) {
            final String customMsgData = getCustomMsgData(msg);
            if (!TextUtils.isEmpty(customMsgData)) {
                L.e(TAG, "???????????????--->" + customMsgData);
                try {
                    if (StringUtil.equals(uid, CommonAppConfig.getInstance().getConfig().getAdmin_upservice())) {
                        updateNowService(customMsgData);
                    }
                    final JSONObject obj = JSON.parseObject(customMsgData);
                    if (obj != null) {
                        String method = obj.getString(METHOD);
                        if (ORDER.equals(method)) {//????????????
                            refreshAllUnReadMsgCount();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ImUserMsgEvent imUserMsgEvent = new ImUserMsgEvent();
                                    imUserMsgEvent.setUid(uid);
                                    imUserMsgEvent.setType(ImMessageBean.TYPE_ORDER);
                                    imUserMsgEvent.setLastMessage(obj.getString("tips"));
                                    imUserMsgEvent.setUnReadCount(getUnReadMsgCount(uid));
                                    imUserMsgEvent.setLastTime(getMessageTimeString(msg));
                                    EventBus.getDefault().post(imUserMsgEvent);
                                }
                            },MSG_UNREAD_COUNT_DELAY_MILLIS);
                            return;
                        } else if (DRIP.equals(method)) {
                            refreshAllUnReadMsgCount();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ImUserMsgEvent imUserMsgEvent = new ImUserMsgEvent();
                                    imUserMsgEvent.setUid(uid);
                                    imUserMsgEvent.setType(ImMessageBean.TYPE_DIDI);
                                    imUserMsgEvent.setLastMessage(obj.getString("tips"));
                                    imUserMsgEvent.setUnReadCount(getUnReadMsgCount(uid));
                                    imUserMsgEvent.setLastTime(getMessageTimeString(msg));
                                    EventBus.getDefault().post(imUserMsgEvent);
                                    EventBus.getDefault().post(new DripEvent());
                                }
                            },MSG_UNREAD_COUNT_DELAY_MILLIS);
                        } else if (ORDER_HALL.equals(method)){
                            refreshAllUnReadMsgCount();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //?????????????????????
                                    ImUserMsgEvent imUserMsgEvent = new ImUserMsgEvent();
                                    imUserMsgEvent.setUid(uid);
                                    imUserMsgEvent.setType(ImMessageBean.TYPE_ORDER_HALL);
                                    imUserMsgEvent.setLastMessage(obj.getString("tips"));
                                    imUserMsgEvent.setUnReadCount(getUnReadMsgCount(uid));
                                    imUserMsgEvent.setLastTime(getMessageTimeString(msg));
                                    EventBus.getDefault().post(imUserMsgEvent);
                                }
                            },MSG_UNREAD_COUNT_DELAY_MILLIS);
                        } else if (CALL.equals(method)) {
                            refreshAllUnReadMsgCount();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject jsonObject = JSON.parseObject(customMsgData);
                                    jsonObject.put(CallIMHelper.MSG_TIME, msg.timestamp());
                                    CallIMHelper.filterMessage(jsonObject, null);
                                    ImMessageBean bean = new ImMessageBean(uid, msg, type, msg.isSelf());
                                    int action = jsonObject.getIntValue(ACTION);
                                    if (action == CallIMHelper.ACTION_CALL_CANCEL ||
                                            action == CallIMHelper.ACTION_CALL_REFUSE ||
                                            action == CallIMHelper.ACTION_CALL_BUSY
                                    ) {
                                        ChatInfoBean chatInfoBean = new ChatInfoBean();
                                        chatInfoBean.setAction(action);
                                        bean.setType(ImMessageBean.TYPE_CALL);
                                        chatInfoBean.setContent(CallIMHelper.actionString(action, msg.isSelf(), jsonObject));
                                        chatInfoBean.setCallType(obj.getIntValue(CallIMHelper.CHAT_TYPE));
                                        bean.setChatInfoBean(chatInfoBean);
                                        EventBus.getDefault().post(bean);
                                    }
                                    ImUserMsgEvent imUserMsgEvent = new ImUserMsgEvent();
                                    imUserMsgEvent.setUid(uid);
                                    imUserMsgEvent.setType(ImMessageBean.TYPE_CALL);
                                    imUserMsgEvent.setUnReadCount(getUnReadMsgCount(uid));
                                    imUserMsgEvent.setLastTime(getMessageTimeString(msg));
                                    EventBus.getDefault().post(imUserMsgEvent);
                                }
                            },MSG_UNREAD_COUNT_DELAY_MILLIS);
                            return;
                        } else if (DISPATCH.equals(method)) {
                            IMLiveBean liveBean = JSON.parseObject(customMsgData, IMLiveBean.class);
                            liveBean.setTime(getMessageTimeString(msg));
                            liveBean.setType(IMLiveBean.TYPE_LIVE);
                            EventBus.getDefault().post(liveBean);
                        } else if (AUTH_NOTICE.equals(method)) {
                            //??????????????????
                            IMLiveBean liveBean = JSON.parseObject(customMsgData, IMLiveBean.class);
                            liveBean.setTime(getMessageTimeString(msg));
                            liveBean.setType(IMLiveBean.TYPE_AUTH_NOTICE);
                            EventBus.getDefault().post(liveBean);
                        } else if (NOW_SERVER.equals(method)) {
                            refreshAllUnReadMsgCount();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    OrderBean orderBean = obj.toJavaObject(OrderBean.class);
                                    ImMessageBean bean = new ImMessageBean(uid, msg, type, msg.isSelf());
                                    bean.setOrderBean(orderBean);
                                    bean.setType(ImMessageBean.TYPE_ORDER_NOW);
                                    EventBus.getDefault().post(bean);
                                    ImUserMsgEvent imUserMsgEvent = new ImUserMsgEvent();
                                    imUserMsgEvent.setUid(uid);
                                    imUserMsgEvent.setType(ImMessageBean.TYPE_ORDER_NOW);
                                    imUserMsgEvent.setLastMessage("[??????]");
                                    imUserMsgEvent.setUnReadCount(getUnReadMsgCount(uid));
                                    imUserMsgEvent.setLastTime(getMessageTimeString(msg));
                                    EventBus.getDefault().post(imUserMsgEvent);
                                }
                            },MSG_UNREAD_COUNT_DELAY_MILLIS);
                        } else if (ORDER_START.equals(method)) {
                            refreshAllUnReadMsgCount();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ImMessageBean bean = new ImMessageBean(uid, msg, type, msg.isSelf());
                                    OrderTipBean orderTipBean = obj.toJavaObject(OrderTipBean.class);
                                    int action = orderTipBean.getAction();
                                    int typeTemp = 0;
                                    if (action == OrderTipBean.SOON_END) {
                                        typeTemp = ImMessageBean.TYPE_ORDER_TIP_AGAGIN;
                                    } else {
                                        typeTemp = ImMessageBean.TYPE_ORDER_TIP_NORMAL;
                                    }
                                    bean.setType(typeTemp);
                                    bean.setOrderTipBean(orderTipBean);
                                    EventBus.getDefault().post(bean);
                                    ImUserMsgEvent imUserMsgEvent = new ImUserMsgEvent();
                                    imUserMsgEvent.setUid(uid);
                                    imUserMsgEvent.setType(typeTemp);
                                    imUserMsgEvent.setLastMessage("[??????]");
                                    imUserMsgEvent.setUnReadCount(getUnReadMsgCount(uid));
                                    imUserMsgEvent.setLastTime(getMessageTimeString(msg));
                                    EventBus.getDefault().post(imUserMsgEvent);
                                }
                            },MSG_UNREAD_COUNT_DELAY_MILLIS);
                        } else if (REFUND_ORDER.equals(method)) {
                            refreshAllUnReadMsgCount();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ImMessageBean bean = new ImMessageBean(uid, msg, type, msg.isSelf());
                                    OrderTipBean orderTipBean = obj.toJavaObject(OrderTipBean.class);
                                    bean.setType(ImMessageBean.TYPE_ORDER_TIP_REFUND);
                                    bean.setOrderTipBean(orderTipBean);
                                    EventBus.getDefault().post(bean);
                                    ImUserMsgEvent imUserMsgEvent = new ImUserMsgEvent();
                                    imUserMsgEvent.setUid(uid);
                                    imUserMsgEvent.setType(ImMessageBean.TYPE_ORDER_TIP_REFUND);
                                    imUserMsgEvent.setLastMessage("[??????]");
                                    imUserMsgEvent.setUnReadCount(getUnReadMsgCount(uid));
                                    imUserMsgEvent.setLastTime(getMessageTimeString(msg));
                                    EventBus.getDefault().post(imUserMsgEvent);
                                }
                            },MSG_UNREAD_COUNT_DELAY_MILLIS);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        if (type == 0) {
            return;
        }
//        boolean canShow = true;
//        Long lastTime = mMap.get(uid);
//        long curTime = System.currentTimeMillis();
//        if (lastTime != null) {
//            if (curTime - lastTime < 1000) {
//                //?????????????????????????????????????????????????????????1??????????????????????????????
//                canShow = false;
//            } else {
//                mMap.put(uid, curTime);
//            }
//        } else {
//            //??????sMap????????????????????????????????????????????????????????????????????????????????????
//            mMap.put(uid, curTime);
//        }
//        if (canShow) {
//            L.e(TAG, "????????????--->");
//            EventBus.getDefault().post(new ImMessageBean(uid, msg, type, false));
//            ImUserMsgEvent imUserMsgEvent = new ImUserMsgEvent();
//            imUserMsgEvent.setUid(uid);
//            imUserMsgEvent.setLastMessage(getMessageString(msg));
//            imUserMsgEvent.setUnReadCount(getUnReadMsgCount(uid));
//            imUserMsgEvent.setLastTime(getMessageTimeString(msg));
//            EventBus.getDefault().post(imUserMsgEvent);
//            refreshAllUnReadMsgCount();
//        }

        showMessage(uid, msg, type, null);
    }

    private void updateNowService(String customMsgData) {
        OrderBean orderBean = JSON.parseObject(customMsgData, OrderBean.class);
        NowServiceEvent nowServiceEvent = new NowServiceEvent(orderBean.getId(), orderBean.getReceptStatus());
        EventBus.getDefault().post(nowServiceEvent);
    }


    private void showMessage(final String uid, final TIMMessage msg, int type, ChatReceiveGiftBean giftBean) {
        L.e(TAG, "????????????--->");
        ImMessageBean bean = new ImMessageBean(uid, msg, type, msg.isSelf());
        bean.setGiftBean(giftBean);
        EventBus.getDefault().post(bean);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImUserMsgEvent imUserMsgEvent = new ImUserMsgEvent();
                imUserMsgEvent.setUid(uid);
                imUserMsgEvent.setLastMessage(getMessageString(msg));
                imUserMsgEvent.setUnReadCount(getUnReadMsgCount(uid));
                imUserMsgEvent.setLastTime(getMessageTimeString(msg));
                EventBus.getDefault().post(imUserMsgEvent);
            }
        },MSG_UNREAD_COUNT_DELAY_MILLIS);
        refreshAllUnReadMsgCount();
    }



    @Override
    public String getConversationUids() {
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        mStringBuilder.delete(0, mStringBuilder.length());
        if (list != null) {
            for (TIMConversation conversation : list) {
                if (conversation.getType() != TIMConversationType.C2C) {
                    continue;
                }
                String peer = conversation.getPeer();
                if (TextUtils.isEmpty(peer) /*|| peer.length() < 6*/) {
                    continue;
                }
                String from = getAppUid(peer);
                if (!TextUtils.isEmpty(from)) {
                    mStringBuilder.append(from);
                    mStringBuilder.append(",");
                }
            }
        }
        return mStringBuilder.toString();
    }


    @Override
    public List<ImUserBean> getLastMsgInfoList(List<ImUserBean> list) {
        if (list == null) {
            return null;
        }
        TIMManager timManager = TIMManager.getInstance();
        for (ImUserBean bean : list) {
            TIMConversation conversation = timManager.getConversation(TIMConversationType.C2C, getImUid(bean.getId()));
            if (conversation != null) {
                bean.setHasConversation(true);
                TIMConversationExt conExt = new TIMConversationExt(conversation);
                TIMMessage msg = conExt.getLastMsg();

                if (msg != null) {
                    bean.setLastTime(getMessageTimeString(msg));
                    bean.setLastTimeStamp(msg.timestamp());
                    bean.setUnReadCount((int) conExt.getUnreadMessageNum());
                    bean.setLastMessage(getMessageString(msg));
                }
            } else {
                bean.setHasConversation(false);
            }
        }
        return list;
    }

    @Override
    public ImUserBean getLastMsgInfo(String uid) {
        TIMManager timManager = TIMManager.getInstance();
        TIMConversation conversation = timManager.getConversation(TIMConversationType.C2C, getImUid(uid));
        if (conversation != null) {
            TIMConversationExt conExt = new TIMConversationExt(conversation);
            TIMMessage msg = conExt.getLastMsg();
            if (msg != null) {
                ImUserBean bean = new ImUserBean();
                bean.setLastTime(getMessageTimeString(msg));
                bean.setLastTimeStamp(msg.timestamp());
                bean.setUnReadCount((int) conExt.getUnreadMessageNum());
                bean.setLastMessage(getMessageString(msg));
                return bean;
            }
        }
        return null;
    }

    /**
     * ??????????????????
     */
    @Override
    public void getOrderMsgList(final CommonCallback<List<String>> callback) {
        if (callback == null) {
            return;
        }
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, Constants.IM_MSG_ADMIN);
        if (conversation == null) {
            return;
        }
        TIMConversationExt conExt = new TIMConversationExt(conversation);
        conExt.getLocalMessage(50, //???????????????????????? 50 ?????????
                null, //null ??????????????????????????????
                new TIMValueCallBack<List<TIMMessage>>() {
                    @Override
                    public void onError(int code, String desc) {//??????????????????
                        L.e(TAG, "???????????????????????? code: " + code + " errmsg: " + desc);
                    }

                    @Override
                    public void onSuccess(List<TIMMessage> list) {//??????????????????
                        if (list != null && list.size() > 0) {
                            List<String> temp = new ArrayList<>();
                            for (int i = 0, size = list.size(); i < size; i++) {
                                TIMMessage msg = list.get(i);
                                String customMsgData = getCustomMsgData(msg);
                                JSONObject object = JSON.parseObject(customMsgData);
                                //?????????IM?????? ????????????????????????????????????????????????????????? ????????????
                                object.put("timestamp", msg.timestamp());
                                customMsgData = object.toString();
                                if (!TextUtils.isEmpty(customMsgData)) {
                                    try {
                                        JSONObject obj = JSON.parseObject(customMsgData);
                                        if (ORDER.equals(obj.getString(METHOD))) {
                                            temp.add(customMsgData);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            callback.callback(temp);
                        }
                    }
                });

    }

    @Override
    public Observable<List<IMLiveBean>> geSpatchList() {
        return Observable.create(new ObservableOnSubscribe<List<IMLiveBean>>() {

            @Override
            public void subscribe(final ObservableEmitter<List<IMLiveBean>> e) throws Exception {
                geSpatchList(new CommonCallback<List<IMLiveBean>>() {
                    @Override
                    public void callback(List<IMLiveBean> bean) {
                        e.onNext(bean);
                        e.onComplete();
                    }
                });
            }
        });

    }

    public void geSpatchList(final CommonCallback<List<IMLiveBean>> callback) {
        if (callback == null) {
            return;
        }
        String converUser = CommonAppConfig.getInstance().getConfig().getAdmin_dispatch();
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, converUser);
        if (conversation == null) {
            return;
        }
        TIMConversationExt conExt = new TIMConversationExt(conversation);
        conExt.getLocalMessage(50, //???????????????????????? 50 ?????????
                null, //null ??????????????????????????????
                new TIMValueCallBack<List<TIMMessage>>() {
                    @Override
                    public void onError(int code, String desc) {//??????????????????
                        L.e(TAG, "???????????????????????? code: " + code + " errmsg: " + desc);
                    }

                    @Override
                    public void onSuccess(List<TIMMessage> list) {//??????????????????
                        if (ListUtil.haveData(list)) {
                            List<IMLiveBean> temp = new ArrayList<>();
                            for (int i = 0, size = list.size(); i < size; i++) {
                                TIMMessage msg = list.get(i);
                                String customMsgData = getCustomMsgData(msg);
                                if (!TextUtils.isEmpty(customMsgData)) {
                                    try {
                                        JSONObject obj = JSON.parseObject(customMsgData);
                                        String method = obj.getString(METHOD);
                                        if (StringUtil.equals(DISPATCH, method)) {
                                            IMLiveBean liveBean = JSON.parseObject(customMsgData, IMLiveBean.class);
                                            liveBean.setTime(getMessageTimeString(msg));
                                            liveBean.setTip_type(IMLiveBean.TYPE_LIVE);
                                            temp.add(liveBean);
                                        }
                                        if (StringUtil.equals(AUTH_NOTICE, method)) {
                                            IMLiveBean liveBean = JSON.parseObject(customMsgData, IMLiveBean.class);
                                            liveBean.setTime(getMessageTimeString(msg));
                                            liveBean.setTip_type(IMLiveBean.TYPE_AUTH_NOTICE);
                                            liveBean.setTip_des(obj.getString("tip_des"));
                                            liveBean.setTip_title(obj.getString("tip_title"));
                                            liveBean.setAction(obj.getIntValue("action"));
                                            temp.add(liveBean);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            callback.callback(temp);

                        }
                        callback.callback(new ArrayList<IMLiveBean>(1));
                    }
                });

    }

    /**
     * ???????????????????????????
     */
    private String getMessageTimeString(TIMMessage message) {
        return mSimpleDateFormat.format(new Date(message.timestamp() * 1000));
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????null
     */
    private String getCustomMsgData(TIMMessage msg) {
        if (msg == null || msg.getElementCount() <= 0) {
            return null;
        }
        TIMElem elem0 = msg.getElement(0);
        if (elem0.getType() != TIMElemType.Custom) {
            return null;
        }
        TIMCustomElem elem = (TIMCustomElem) elem0;
        return new String(elem.getData());
    }

    /**
     * ?????????????????????
     */
    private int getMessageType(TIMMessage msg) {
        int type = 0;
        if (msg == null || msg.getElementCount() <= 0) {
            return type;
        }
        TIMElem elem0 = msg.getElement(0);
        if (elem0 == null) {
            return type;
        }
        TIMElemType elemType = elem0.getType();
        switch (elemType) {
            case Text://??????
                type = ImMessageBean.TYPE_TEXT;
                break;
            case Image://??????
                type = ImMessageBean.TYPE_IMAGE;
                break;
            case Sound://??????
                type = ImMessageBean.TYPE_VOICE;
                break;
            case Location://??????
                type = ImMessageBean.TYPE_LOCATION;
                break;
        }
        return type;
    }


    /**
     * ??????????????????????????????
     */
    private String getMessageString(TIMMessage msg) {
        String result = "";
        if (msg == null || msg.getElementCount() <= 0) {
            return result;
        }
        TIMElem elem0 = msg.getElement(0);
        if (elem0 == null) {
            return result;
        }
        TIMElemType elemType = elem0.getType();
        switch (elemType) {
            case Text://??????
                result = ((TIMTextElem) elem0).getText();
                if (LanguageUtil.isEn()) {
                    result = result.replaceAll("\\[([\u4e00-\u9fa5\\w])+\\]", mFaceString);
                }
                break;
            case Image://??????
                result = mImageString;
                break;
            case Sound://??????
                result = mVoiceString;
                break;
            case Location://??????
                result = mLocationString;
                break;
            case Custom:
                String customMsgData = getCustomMsgData(msg);
                if (!TextUtils.isEmpty(customMsgData)) {
                    try {
                        JSONObject obj = JSON.parseObject(customMsgData);
                        if (obj != null) {
                            String method = obj.getString(METHOD);
                            if (ORDER.equals(method) || AUTH_NOTICE.equals(method) || DISPATCH.equals(method) ||ORDER_HALL.equals(method) || DRIP.equals(method)) {//???????????? ????????????  ????????????
                                if (LanguageUtil.isEn()) {
                                    result = obj.getString("tips_en");
                                } else {
                                    result = obj.getString("tips");
                                }
                            }
                            if (method.equals(CALL)) {
                                int type = obj.getIntValue(CHAT_TYPE);
                                result = type == Constants.CHAT_TYPE_VIDEO ? "[????????????]" : "[????????????]";
                            } else if (method.equals(NOW_SERVER) || method.equals(ORDER_START) || method.equals(REFUND_ORDER)) {
                                result = "[??????]";
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        return result;
    }


    //TODO
    @Override
    public void getChatMessageList(String toUid, final CommonCallback<List<ImMessageBean>> callback) {
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, getImUid(toUid));
        TIMConversationExt conExt = new TIMConversationExt(conversation);
        conExt.getLocalMessage(50, //???????????????????????? 50 ?????????
                null, //null ??????????????????????????????
                new TIMValueCallBack<List<TIMMessage>>() {
                    @Override
                    public void onError(int code, String desc) {//??????????????????
                        L.e(TAG, "???????????????????????? code: " + code + " errmsg: " + desc);
                    }

                    @Override
                    public void onSuccess(List<TIMMessage> list) {//??????????????????
                        if (ListUtil.haveData(list)) {
                            StringBuilder stringBuilder = new StringBuilder();
                            final List<OrderBean> orderBeanList = new ArrayList<>();

                            final List<ImMessageBean> result = new ArrayList<>();
                            for (int i = list.size() - 1; i >= 0; i--) {
                                TIMMessage msg = list.get(i);
                                String from = getAppUid(msg);
                                int type = getMessageType(msg);
                                if (type == 0) {
                                    String customMsgData = getCustomMsgData(msg);
                                    if (!TextUtils.isEmpty(customMsgData)) {
                                        try {
                                            JSONObject obj = JSON.parseObject(customMsgData);
                                            String method = obj.getString(METHOD);
                                            if (StringUtil.equals(method, CALL)) {
                                                int action = obj.getIntValue(ACTION);
                                                if (action == CallIMHelper.ACTION_CALL_CANCEL ||
                                                        action == CallIMHelper.ACTION_CALL_REFUSE
                                                ) {
                                                    ImMessageBean bean = new ImMessageBean(from, msg, type, msg.isSelf());
                                                    bean.setMsgId(msg.getMsgId());
                                                    ChatInfoBean chatInfoBean = new ChatInfoBean();
                                                    chatInfoBean.setAction(action);
                                                    chatInfoBean.setCallType(obj.getIntValue(CallIMHelper.CHAT_TYPE));
                                                    bean.setType(ImMessageBean.TYPE_CALL);
                                                    chatInfoBean.setContent(CallIMHelper.actionString(action, msg.isSelf(), obj));
                                                    bean.setChatInfoBean(chatInfoBean);
                                                    result.add(bean);
                                                }
                                            } else if (StringUtil.equals(method, NOW_SERVER)) {
                                                OrderBean orderBean = JSON.parseObject(customMsgData, OrderBean.class);
                                                ImMessageBean bean = new ImMessageBean(from, msg, type, msg.isSelf());
                                                bean.setType(ImMessageBean.TYPE_ORDER_NOW);
                                                bean.setOrderBean(orderBean);
                                                bean.setMsgId(msg.getMsgId());
                                                result.add(bean);
                                                stringBuilder.append(obj.getString("id"));
                                                stringBuilder.append(",");
                                                orderBeanList.add(orderBean);
                                            } else if (StringUtil.equals(method, ORDER_START)) {
                                                ImMessageBean bean = new ImMessageBean(from, msg, type, msg.isSelf());
                                                OrderTipBean orderTipBean = obj.toJavaObject(OrderTipBean.class);
                                                int action = orderTipBean.getAction();
                                                if (action == OrderTipBean.SOON_END) {
                                                    bean.setType(ImMessageBean.TYPE_ORDER_TIP_AGAGIN);
                                                } else {
                                                    bean.setType(ImMessageBean.TYPE_ORDER_TIP_NORMAL);
                                                }
                                                bean.setOrderTipBean(orderTipBean);
                                                result.add(bean);
                                            } else if (StringUtil.equals(method, REFUND_ORDER)) {
                                                ImMessageBean bean = new ImMessageBean(from, msg, type, msg.isSelf());
                                                OrderTipBean orderTipBean = obj.toJavaObject(OrderTipBean.class);
                                                bean.setType(ImMessageBean.TYPE_ORDER_TIP_REFUND);
                                                bean.setOrderTipBean(orderTipBean);
                                                EventBus.getDefault().post(bean);
                                                refreshAllUnReadMsgCount();
                                                result.add(bean);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    if (!TextUtils.isEmpty(from)) {
                                        boolean self = msg.isSelf();
                                        ImMessageBean bean = new ImMessageBean(from, msg, type, self);
                                        bean.setMsgId(msg.getMsgId());
                                        if (self && msg.status() == TIMMessageStatus.SendFail) {
                                            bean.setSendFail(true);
                                        }
                                        result.add(bean);
                                    }
                                }
                            }

                            int length = stringBuilder.length();
                            if (length <= 0) {
                                if (callback != null) {
                                    callback.callback(result);
                                }
                            } else {
                                String orderArrayId = stringBuilder.deleteCharAt(length - 1).toString();
                                CommonHttpUtil.getMutiOrderDetail(orderArrayId, new HttpCallback() {
                                    @Override
                                    public void onSuccess(int code, String msg, String[] info) {
                                        if (code == 0 && info.length > 0) {
                                            String jsonString = Arrays.toString(info);
                                            JSONArray jsonArray = JSON.parseArray(jsonString);
                                            int size = orderBeanList.size();
                                            for (int i = 0; i < size; i++) {
                                                if (i < jsonArray.size()) {
                                                    OrderBean orderBean = orderBeanList.get(i);
                                                    int status = jsonArray.getJSONObject(i).getIntValue("recept_status");
                                                    orderBean.setReceptStatus(status);
                                                }
                                            }
                                        }
                                        if (callback != null) {
                                            callback.callback(result);
                                        }
                                    }

                                    @Override
                                    public void onError() {
                                        super.onError();
                                        if (callback != null) {
                                            callback.callback(result);
                                        }
                                    }
                                });
                            }

                        }
                    }
                });
    }

    @Override
    public int getUnReadMsgCount(String uid) {
        TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, getImUid(uid));
        if (con != null) {
//            TIMConversationExt conExt = new TIMConversationExt(con);
//            return (int) conExt.getUnreadMessageNum();
            L.e(TAG,"getUnReadMsgCount---->"+con.getUnreadMessageNum());
            return (int) con.getUnreadMessageNum();
        }
        return 0;
    }

    @Override
    public void refreshAllUnReadMsgCount() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new ImUnReadCountEvent(getAllUnReadMsgCount(), false));
                EventBus.getDefault().post(new ImUnReadCountEvent(getLiveActivityAllUnReadMsgCount(), true));
            }
        },MSG_UNREAD_COUNT_DELAY_MILLIS);
    }

    //???????????? ???????????????
    @Override
    public String getAllUnReadMsgCount() {
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        String admin = null;
        String dripAdmin = null;
        String dispatchAdmin = null;
        String upAdmin = configBean.getAdmin_upservice();
        if (configBean != null) {
            admin = configBean.getAdmin();
            dripAdmin = configBean.getDripAdmin();
            dispatchAdmin = configBean.getAdmin_dispatch();
        }
//        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        List<TIMConversation> list = TIMManager.getInstance().getConversationList();
        int unReadCount = 0;
        if (list != null && list.size() > 0) {
            for (TIMConversation conversation : list) {
                if (conversation == null) {
                    continue;
                }
                if (conversation.getType() != TIMConversationType.C2C) {
                    continue;
                }
                String peer = conversation.getPeer();
                if (TextUtils.isEmpty(peer)) {
                    continue;
                }
                /*StringUtil.equals(peer,admin)||*/
                // TODO: 2020-11-05 20201105??????????????????????????? ||StringUtil.equals(peer,dispatchAdmin)
                // TODO: 2020-11-21 ??????????????????????????? StringUtil.equals(peer,dripAdmin)
                if (StringUtil.equals(peer, upAdmin)) {
                    continue;
                }
                String uid = getAppUid(peer);
                if (TextUtils.isEmpty(uid)) {
                    continue;
                }
//                TIMConversationExt conExt = new TIMConversationExt(conversation);
//                unReadCount += ((int) conExt.getUnreadMessageNum());
                //????????????????????????
                TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, peer);
                //?????????????????????
                unReadCount += (int) con.getUnreadMessageNum();
            }
        }
        L.e(TAG, "??????????????????----->" + unReadCount);
        String res = "";
        if (unReadCount > 99) {
            res = "99+";
        } else {
            if (unReadCount < 0) {
                unReadCount = 0;
            }
            res = String.valueOf(unReadCount);
        }
        return res;
    }


    //???????????? ??????????????????????????????????????????????????????
    @Override
    public String getLiveActivityAllUnReadMsgCount() {
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        String admin = null;
        String dripAdmin = null;
        String dispatchAdmin = null;
        String upAdmin = configBean.getAdmin_upservice();

        if (configBean != null) {
            admin = configBean.getAdmin();
            dripAdmin = configBean.getDripAdmin();
            dispatchAdmin = configBean.getAdmin_dispatch();
        }
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        int unReadCount = 0;
        if (list != null && list.size() > 0) {
            for (TIMConversation conversation : list) {
                if (conversation == null) {
                    continue;
                }
                if (conversation.getType() != TIMConversationType.C2C) {
                    continue;
                }
                String peer = conversation.getPeer();
                if (TextUtils.isEmpty(peer)) {
                    continue;
                }
                /*StringUtil.equals(peer,admin)||*/
                if (StringUtil.equals(peer, admin) || StringUtil.equals(peer, upAdmin) || StringUtil.equals(peer, dripAdmin) || StringUtil.equals(peer, dispatchAdmin)) {
                    continue;
                }
                String uid = getAppUid(peer);
                if (TextUtils.isEmpty(uid)) {
                    continue;
                }

                TIMConversationExt conExt = new TIMConversationExt(conversation);
                unReadCount += ((int) conExt.getUnreadMessageNum());
            }
        }
        L.e(TAG, "Live??????????????????----->" + unReadCount);
        String res = "";
        if (unReadCount > 99) {
            res = "99+";
        } else {
            if (unReadCount < 0) {
                unReadCount = 0;
            }
            res = String.valueOf(unReadCount);
        }
        return res;
    }

    @Override
    public void markAllMessagesAsRead(String toUid, boolean needRefresh) {
        if (!TextUtils.isEmpty(toUid)) {
            TIMConversation con = TIMManager.getInstance().getConversation(TIMConversationType.C2C, getImUid(toUid));
            if (con != null) {
//                TIMConversation conversation = TIMManager.getInstance().getConversation(
//                        TIMConversationType.C2C,    //?????????????????????
//                        getImUid(toUid));           //????????????????????????
//                con.setReadMessage(null,needRefresh ? mUnReadCountCallBack : mEmptyCallBack);
                TIMConversationExt conExt = new TIMConversationExt(con);
                conExt.setReadMessage(null, needRefresh ? mUnReadCountCallBack : mEmptyCallBack);
            }
        }
    }


    /**
     * ???????????????????????????  ?????????????????????
     */
    @Override
    public void markAllConversationAsRead() {
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        if (list != null && list.size() > 0) {
            for (TIMConversation conversation : list) {
                if (conversation != null) {
                    final TIMConversationExt conExt = new TIMConversationExt(conversation);
                    conExt.setReadMessage(null, mEmptyCallBack);
                }
            }
        }
        EventBus.getDefault().post(new ImUnReadCountEvent("0", false));
        EventBus.getDefault().post(new ImUnReadCountEvent("0", true));
    }

    @Override
    public ImMessageBean createTextMessage(String toUid, String content) {
        TIMMessage msg = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText(content);
        if (msg.addElement(elem) != 0) {
            L.e(TAG, "????????????????????????----->");
            return null;
        }
        return new ImMessageBean(CommonAppConfig.getInstance().getUid(), msg, ImMessageBean.TYPE_TEXT, true);
    }

    @Override
    public ImMessageBean createImageMessage(String toUid, String path) {
        TIMMessage msg = new TIMMessage();
        TIMImageElem elem = new TIMImageElem();
        elem.setPath(path);
        if (msg.addElement(elem) != 0) {
            L.e(TAG, "????????????????????????----->");
            return null;
        }
        return new ImMessageBean(CommonAppConfig.getInstance().getUid(), msg, ImMessageBean.TYPE_IMAGE, true);
    }


    @Override
    public ImMessageBean createLocationMessage(String toUid, double lat, double lng, int scale, String address) {
        TIMMessage msg = new TIMMessage();
        TIMLocationElem elem = new TIMLocationElem();
        elem.setLatitude(lat);
        elem.setLongitude(lng);
        elem.setDesc(address);
        if (msg.addElement(elem) != 0) {
            return null;
        }
        return new ImMessageBean(CommonAppConfig.getInstance().getUid(), msg, ImMessageBean.TYPE_LOCATION, true);
    }


    @Override
    public ImMessageBean createVoiceMessage(String toUid, File voiceFile, long duration) {
        TIMMessage msg = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        elem.setPath(voiceFile.getAbsolutePath());
        elem.setDuration(duration / 1000);
        if (msg.addElement(elem) != 0) {
            return null;
        }
        return new ImMessageBean(CommonAppConfig.getInstance().getUid(), msg, ImMessageBean.TYPE_VOICE, true);
    }

    /**
     * ?????????????????????
     *
     * @param toUid
     * @param data  ??????????????????
     */

    @Override
    public void sendCustomMessage(String toUid, String data, final boolean save) {
        final TIMMessage customMsg = new TIMMessage();
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(data.getBytes());
        if (customMsg.addElement(elem) != 0) {
            return;
        }

        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, getImUid(toUid));
        if (conversation != null) {
            if (mSendCustomCallback == null) {
                mSendCustomCallback = new TIMValueCallBack<TIMMessage>() {//??????????????????
                    @Override
                    public void onError(int code, String desc) {//??????????????????
                        L.e(TAG, "???????????????????????????---> code: " + code + " errmsg: " + desc);
                        if (!save) {
                            removeMessage(customMsg);
                        }
                    }

                    @Override
                    public void onSuccess(TIMMessage msg) {//??????????????????
                        L.e(TAG, "?????????????????????????????????");
                        if (!save) {
                            removeMessage(customMsg);
                        }
                    }
                };
            }
            conversation.sendMessage(customMsg, mSendCustomCallback);
        }
    }

    @Override
    public void sendMessage(String toUid, ImMessageBean bean, SendMsgResultCallback callback) {
        if (bean == null || TextUtils.isEmpty(toUid)) {
            return;
        }
        TIMMessage msg = bean.getTimRawMessage();
        if (msg == null) {
            return;
        }
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, getImUid(toUid));
        if (conversation != null) {
            mSendMsgResultCallback = callback;
            if (mSendCompleteCallback == null) {
                mSendCompleteCallback = new TIMValueCallBack<TIMMessage>() {//??????????????????
                    @Override
                    public void onError(int code, String desc) {//??????????????????
                        L.e(TAG, "??????????????????---> code: " + code + " errmsg: " + desc);
                        if (mSendMsgResultCallback != null) {
                            mSendMsgResultCallback.onSendFinish(false);
                        }
                        mSendMsgResultCallback = null;
                    }

                    @Override
                    public void onSuccess(TIMMessage msg) {//??????????????????
                        if (mSendMsgResultCallback != null) {
                            mSendMsgResultCallback.onSendFinish(true);
                        }
                        mSendMsgResultCallback = null;
                    }
                };
            }

            conversation.sendMessage(msg, mSendCompleteCallback);
        }

    }

    /**
     * ????????????
     */
    private boolean removeMessage(TIMMessage msg) {
        if (msg != null) {
            TIMMessageExt timMessageExt = new TIMMessageExt(msg);
            return timMessageExt.remove();
        }
        return false;
    }

    /**
     * ????????????
     *
     * @param toUid
     * @param bean
     */

    @Override
    public void removeMessage(String toUid, ImMessageBean bean) {
        if (bean != null) {
            removeMessage(bean.getTimRawMessage());
        }
    }


    @Override
    public void removeAllConversation() {

    }

    /**
     * ???????????????????????????????????????????????????????????????
     */


    @Override
    public void removeAllMessage(String toUid) {
        if (!TextUtils.isEmpty(toUid)) {
            TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, getImUid(toUid));
            if (conversation != null) {
                TIMConversationExt conExt = new TIMConversationExt(conversation);
                conExt.deleteLocalMessage(mUnReadCountCallBack);
            }
        }
    }

    public void updateLocalMessageBean(ImMessageBean imMessageBean) {
        String msgId = imMessageBean.getMsgId();
        String uid = imMessageBean.getUid();
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, getImUid(imMessageBean.getUid()));
        TIMConversationExt conExt = new TIMConversationExt(conversation);
    }

    /**
     * ??????????????????
     */

    @Override
    public void removeConversation(String toUid) {
        if (!TextUtils.isEmpty(toUid)) {
            markAllMessagesAsRead(toUid, true);
            TIMManagerExt.getInstance().deleteConversationAndLocalMsgs(TIMConversationType.C2C, getImUid(toUid));
        }
    }

    @Override
    public void refreshLastMessage(String uid, ImMessageBean bean) {
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        if (bean == null) {
            return;
        }
        TIMMessage msg = bean.getTimRawMessage();
        if (msg == null) {
            return;
        }
        ImUserMsgEvent imUserMsgEvent = new ImUserMsgEvent();
        imUserMsgEvent.setUid(uid);
        imUserMsgEvent.setLastMessage(getMessageString(msg));
        imUserMsgEvent.setUnReadCount(getUnReadMsgCount(uid));
        imUserMsgEvent.setLastTime(getMessageTimeString(msg));
        EventBus.getDefault().post(imUserMsgEvent);
    }

    @Override
    public void setVoiceMsgHasRead(ImMessageBean bean, Runnable runnable) {
        if (bean != null) {
            TIMMessage msg = bean.getTimRawMessage();
            if (msg != null) {
                TIMMessageExt ext = new TIMMessageExt(msg);
                ext.setCustomInt(1);
                if (runnable != null) {
                    runnable.run();
                }
            }
        }
    }

    @Override
    public String getMessageText(ImMessageBean bean) {
        if (bean != null) {
            TIMMessage msg = bean.getTimRawMessage();
            if (msg == null || msg.getElementCount() <= 0) {
                return "";
            }
            TIMElem elem0 = msg.getElement(0);
            if (elem0 == null) {
                return "";
            }
            if (elem0.getType() == TIMElemType.Text) {
                return ((TIMTextElem) elem0).getText();
            }
        }
        return "";
    }

    @Override
    public ImMsgLocationBean getMessageLocation(ImMessageBean bean) {
        if (bean != null) {
            TIMMessage msg = bean.getTimRawMessage();
            if (msg == null || msg.getElementCount() <= 0) {
                return null;
            }
            TIMElem elem0 = msg.getElement(0);
            if (elem0 == null) {
                return null;
            }
            if (elem0.getType() == TIMElemType.Location) {
                TIMLocationElem locationElem = (TIMLocationElem) elem0;
                return new ImMsgLocationBean(locationElem.getDesc(),
                        0, locationElem.getLatitude(), locationElem.getLongitude());
            }
        }
        return null;
    }

    @Override
    public void displayImageFile(final Context context, final ImMessageBean bean, CommonCallback<File> commonCallback) {
        if (bean == null || commonCallback == null) {
            return;
        }
        TIMMessage msg = bean.getTimRawMessage();
        if (msg == null || msg.getElementCount() <= 0) {
            return;
        }
        TIMElem elem0 = msg.getElement(0);
        if (elem0 == null || elem0.getType() != TIMElemType.Image) {
            return;
        }
        TIMImageElem e = (TIMImageElem) elem0;
        String localPath = e.getPath();
        if (!TextUtils.isEmpty(localPath)) {
            File file = new File(localPath);
            if (file.exists()) {
                commonCallback.callback(file);
            }
            return;
        }
        List<TIMImage> imageList = e.getImageList();
        if (imageList == null || imageList.size() == 0) {
            return;
        }
        TIMImage targetImage = null;
        for (TIMImage img : imageList) {
            if (img.getType() == TIMImageType.Original) {
                targetImage = img;
                break;
            }
        }
        if (targetImage != null) {
            TxImCacheUtil.getImageFile(targetImage.getUuid(), targetImage.getUrl(), commonCallback);
        }
    }

    @Override
    public void getVoiceFile(ImMessageBean bean, final CommonCallback<File> commonCallback) {
        if (bean == null || commonCallback == null) {
            return;
        }
        TIMMessage msg = bean.getTimRawMessage();
        if (msg == null || msg.getElementCount() <= 0) {
            return;
        }
        TIMElem elem0 = msg.getElement(0);
        if (elem0 == null || elem0.getType() != TIMElemType.Sound) {
            return;
        }
        TIMSoundElem e = (TIMSoundElem) elem0;
        String localPath = e.getPath();
        if (!TextUtils.isEmpty(localPath)) {
            File file = new File(localPath);
            if (file.exists()) {
                commonCallback.callback(file);
            }
            return;
        }
        File dir = new File(CommonAppConfig.MUSIC_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final File voiceFile = new File(dir, e.getUuid());
        if (voiceFile.exists()) {
            commonCallback.callback(voiceFile);
            return;
        }
        e.getSoundToFile(voiceFile.getAbsolutePath(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess() {
                if (voiceFile.exists()) {
                    commonCallback.callback(voiceFile);
                }
            }
        });

    }

    /**
     * ???????????????????????????
     */
    @Override
    public void handleNotification(TIMOfflinePushNotification timOfflinePushNotification) {
        L.e(TAG, "????????????--->");
    }

    /**
     * ?????????????????????
     */
    private void playRing() {
        if (mCloseChatMusic || mOpenChatActivity) {
            return;
        }
        if (mSoundPool == null) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            mSoundPool = builder.build();
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if (status == 0 && mSoundId != -1) {
                        soundPool.play(mSoundId, 1, 1, 1, 0, 1);
                    }
                }
            });
        }
        if (mSoundId == -1) {
            mSoundId = mSoundPool.load(CommonAppContext.sInstance, R.raw.msg_ring, 1);
        } else {
            mSoundPool.play(mSoundId, 1, 1, 1, 0, 1);
        }
    }

    @Override
    public void setOpenChatActivity(boolean openChatActivity) {
        mOpenChatActivity = openChatActivity;
    }

    @Override
    public void setCloseChatMusic(boolean closeChatMusic) {
        mCloseChatMusic = closeChatMusic;
    }
}
