package com.yunbao.chatroom.business.socket;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.tencent.bugly.crashreport.CrashReport;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LiveInfo;
import com.yunbao.common.utils.L;
import com.yunbao.chatroom.bean.SocketReceiveBean;
import com.yunbao.chatroom.bean.SocketSendBean;
import com.yunbao.chatroom.business.socket.base.callback.SocketStateListner;
import com.yunbao.chatroom.business.socket.base.mannger.SocketManager;

import org.json.JSONArray;
import org.json.JSONException;
import java.lang.ref.WeakReference;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIOImpl implements ILiveSocket {
    private static final String TAG = "socket";
    private static final String SOCKET_CONN = "conn";
    private static final String SOCKET_BROADCAST = "broadcastingListen";
    private static final String SOCKET_SEND = "broadcast";

    private static SocketHandler mSocketHandler;
    private Socket mSocket;
    private LiveInfo mLiveInfo;
    private String mUrl;

    public SocketIOImpl(String url, @NonNull SocketManager socketManager,@NonNull SocketStateListner socketMessageListner,LiveInfo liveInfo){
        mLiveInfo=liveInfo;
        mUrl=url;
        if (!TextUtils.isEmpty(url)) {
            try {
                IO.Options option = new IO.Options();
                option.forceNew = true;
                option.reconnection = true;
                option.reconnectionDelay = 2000;
                mSocket = IO.socket(url, option);
                mSocket.on(Socket.EVENT_CONNECT, mConnectListener);//连接成功
                mSocket.on(Socket.EVENT_DISCONNECT, mDisConnectListener);//断开连接
                mSocket.on(Socket.EVENT_CONNECT_ERROR, mErrorListener);//连接错误
                mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, mTimeOutListener);//连接超时
                mSocket.on(Socket.EVENT_RECONNECT, mReConnectListener);//重连
                mSocket.on(SOCKET_CONN, onConn);//连接socket消息
                mSocket.on(SOCKET_BROADCAST, onBroadcast);//接收服务器广播的具体业务逻辑相关的消息
                mSocketHandler = new SocketHandler(socketMessageListner,socketManager);
            } catch (Exception e) {
                L.e(TAG, "socket url 异常--->" + e.getMessage());
            }

        }
    }

    private static class SocketHandler extends Handler {
        private SocketStateListner mListener;
        private SocketManager mSocketManager;

        public SocketHandler(SocketStateListner listener,SocketManager socketManager) {
            mListener = new WeakReference<>(listener).get();
            mSocketManager=new WeakReference<>(socketManager).get();
        }


        @Override
        public void handleMessage(Message msg) {
            if (mListener == null) {
                return;
            }
            switch (msg.what) {
                case Constants.SOCKET_WHAT_CONN:
                    mListener.onConnect((Boolean) msg.obj);
                    break;
                case Constants.SOCKET_WHAT_BROADCAST:
                    processBroadcast((String) msg.obj);
                    break;
                case Constants.SOCKET_WHAT_DISCONN:
                    mListener.onDisConnect();
                    break;
                default:
                    break;
            }
        }

        private void processBroadcast(String socketMsg) {
            L.e("socketMsg=="+socketMsg);
            SocketReceiveBean received = JSON.parseObject(socketMsg, SocketReceiveBean.class);
            try {
                if(mSocketManager!=null){
                    mSocketManager.handle(received.getMsgData());
                }
            }catch (Exception e){
                CrashReport.postCatchedException(new Exception("原始异常的提示=="+e));
                //ToastUtil.show("socket数据异常,请前往bugly查看");
                CrashReport.postCatchedException(new Exception("socket数据=="+socketMsg,e.getCause()));
                e.printStackTrace();
            }
        }

        public void clear(){
            mListener=null;
            mSocketManager=null;
        }
    }

    @Override
    public void connect() {
        mSocket.connect();
    }
    @Override
    public void disConnect() {

       // ToastUtil.show("socket资源释放");
        mSocket.disconnect();
        if(mSocketHandler!=null){
          mSocketHandler.clear();
          mSocketHandler=null;
        }
    }
    @Override
    public void send(SocketSendBean socketSendBean) {
        if (mSocket != null) {
            mSocket.emit(SOCKET_SEND, socketSendBean.create());
        }
    }
    /**
     * 向服务发送连接消息
     */


    private void conn() {
        String liveUid=mLiveInfo!=null?mLiveInfo.getLiveUid():"";
        String roomnum=mLiveInfo!=null?mLiveInfo.getRoomId()+"":"";
        String stream=mLiveInfo!=null?mLiveInfo.getSteam():"";
        org.json.JSONObject data = new org.json.JSONObject();
        try {
            data.put("uid", CommonAppConfig.getInstance().getUid());
            data.put("token", CommonAppConfig.getInstance().getToken());
            data.put("liveuid", liveUid);
            data.put("roomnum", roomnum);
            data.put("stream", stream);
            mSocket.emit("conn", data);
            L.e("socket data=="+data);
        } catch (JSONException e) {
            L.e("JSONException=="+e);
            e.printStackTrace();
        }
    }

    private Emitter.Listener mConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--onConnect-->" + args);
            conn();
        }
    };


    private Emitter.Listener mReConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--reConnect-->" + args);
            //conn();
        }
    };

    private Emitter.Listener mDisConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            L.e(TAG, "--onDisconnect-->" + args);
            if (mSocketHandler != null) {
                mSocketHandler.sendEmptyMessage(Constants.SOCKET_WHAT_DISCONN);
            }
        }
    };

    private Emitter.Listener onConn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mSocketHandler != null) {
                try {
                    String test = ((JSONArray) args[0]).toString();
                    String s = ((JSONArray) args[0]).getString(0);
                    L.e(TAG, "--onConn-->" + s);
                    //showMsg("连接成功",test);
                    Message msg = Message.obtain();
                    msg.what = Constants.SOCKET_WHAT_CONN;
                    msg.obj = s.equals("ok");
                    mSocketHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    private Emitter.Listener onBroadcast = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (mSocketHandler != null) {
                try {
                    JSONArray array = (JSONArray) args[0];
                    for (int i = 0; i < array.length(); i++) {
                        Message msg = Message.obtain();
                        msg.what = Constants.SOCKET_WHAT_BROADCAST;
                        msg.obj = array.getString(i);
                        if (mSocketHandler != null) {
                            mSocketHandler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };

    private Emitter.Listener mErrorListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //showMsg("失败","连接失败");
            L.e(TAG, "--onConnectError-->" + args);
        }
    };

    /*private void showMsg(final String title,final String content) {
        Observable.just("1").observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if(LiveActivity.mActivity!=null){
                    DialogUitl.showSimpleTipDialog(LiveActivity.mActivity,title,content+"url="+mUrl);
                }else{
                    ToastUtil.show(title+" && "+content+"url="+mUrl);
                }
            }
        });
         }*/




   private Emitter.Listener mTimeOutListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //showMsg("失败","连接超时");
            L.e(TAG, "--onConnectTimeOut-->" + args);
        }
    };
}
