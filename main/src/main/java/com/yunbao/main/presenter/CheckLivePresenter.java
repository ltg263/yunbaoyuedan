package com.yunbao.main.presenter;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.activity.LiveAudienceActivity;
import com.yunbao.live.bean.LiveBeanReal;
import com.yunbao.live.dialog.LiveRoomCheckDialogFragment2;
import com.yunbao.live.http.LiveHttpConsts;
import com.yunbao.live.http.LiveHttpUtil;
import com.yunbao.main.R;
import com.yunbao.main.activity.YoungOpenedActivity;

/**
 * Created by cxf on 2017/9/29.
 */

public class CheckLivePresenter {

    private Context mContext;
    private LiveBeanReal mLiveBeanReal;//选中的直播间信息
    private String mKey;
    private int mPosition;
    private int mLiveType;//直播间的类型  普通 密码 门票 计时等
    private int mLiveTypeVal;//收费价格等
    private String mLiveTypeMsg;//直播间提示信息或房间密码
    private HttpCallback mCheckLiveCallback;
    private boolean mIsVoiceRoom;

    public CheckLivePresenter(Context context) {
        mContext = context;
    }


    /**
     * 观众 观看直播
     */
    public void watchLive(LiveBeanReal bean) {
        watchLive(bean, "", 0);
    }

    /**
     * 观众 观看直播
     */
    public void watchLive(LiveBeanReal bean, String key, int position) {
        mLiveBeanReal = bean;
        mKey = key;
        mPosition = position;
        if (mCheckLiveCallback == null) {
            mCheckLiveCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        if (info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            mIsVoiceRoom = obj.getIntValue("live_type") == 1;
                            mLiveType = obj.getIntValue("type");
                            mLiveTypeVal = obj.getIntValue("type_val");
                            mLiveTypeMsg = obj.getString("type_msg");

                            if (mLiveType == Constants.LIVE_TYPE_NORMAL) {
                                forwardNormalRoom();
                            } else {
                                if (CommonAppConfig.getInstance().getIsState()==1) {
                                    ToastUtil.show(R.string.teenager_live_tip);
                                    return;
                                }
                                LiveRoomCheckDialogFragment2 fragment = new LiveRoomCheckDialogFragment2();
                                if (mLiveType == Constants.LIVE_TYPE_PWD) {
                                    fragment.setLiveType(mLiveType, mLiveTypeMsg);
                                } else {
                                    fragment.setLiveType(mLiveType, String.valueOf(mLiveTypeVal));
                                }
                                fragment.setActionListener(new LiveRoomCheckDialogFragment2.ActionListener() {
                                    @Override
                                    public void onConfirmClick() {
                                        if (mLiveType == Constants.LIVE_TYPE_PWD) {
                                            forwardNormalRoom();
                                        } else {
                                            if (((AbsActivity) mContext).checkLogin()) {
                                                roomCharge();
                                            }
                                        }
                                    }
                                });
                                fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "LiveRoomCheckDialogFragment2");
                            }
                        }
                    } else {
                        ToastUtil.show(msg);
                    }
                }

                @Override
                public boolean showLoadingDialog() {
                    return true;
                }

                @Override
                public Dialog createLoadingDialog() {
                    return DialogUitl.loadingDialog(mContext);
                }
            };
        }
        LiveHttpUtil.checkLive(bean.getUid(), bean.getStream(), mCheckLiveCallback);
    }


    /**
     * 前往普通房间
     */
    private void forwardNormalRoom() {
        forwardLiveAudienceActivity();
    }


    public void roomCharge() {
        LiveHttpUtil.roomCharge(mLiveBeanReal.getUid(), mLiveBeanReal.getStream(), mRoomChargeCallback);
    }

    private HttpCallback mRoomChargeCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                forwardLiveAudienceActivity();
            } else {
                ToastUtil.show(msg);
            }
        }

        @Override
        public boolean showLoadingDialog() {
            return true;
        }

        @Override
        public Dialog createLoadingDialog() {
            return DialogUitl.loadingDialog(mContext);
        }
    };

    public void cancel() {
        LiveHttpUtil.cancel(LiveHttpConsts.CHECK_LIVE);
        LiveHttpUtil.cancel(LiveHttpConsts.ROOM_CHARGE);
    }

    /**
     * 跳转到直播间
     */
    private void forwardLiveAudienceActivity() {
        if (mIsVoiceRoom) {
            LiveAudienceActivity.forward(mContext, mLiveBeanReal, mLiveType, mLiveTypeVal, "", 0, Constants.LIVE_SDK_TX, true);
        } else {
            LiveAudienceActivity.forward(mContext, mLiveBeanReal, mLiveType, mLiveTypeVal, mKey, mPosition, Constants.LIVE_SDK_TX, false);
        }
    }
}
