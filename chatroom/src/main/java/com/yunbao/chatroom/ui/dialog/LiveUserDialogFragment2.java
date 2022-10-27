package com.yunbao.chatroom.ui.dialog;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.chatroom.ui.activity.LiveActivity;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.LiveAnthorBean;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.business.liveobsever.LifeObjectHolder;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.SystemUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;

import java.util.ArrayList;
import java.util.List;

/*用户dialog*/
public class LiveUserDialogFragment2 extends AbsDialogFragment implements View.OnClickListener {
    private static final int TYPE_AUD_AUD = 1;//观众点别的观众
    private static final int TYPE_ANC_AUD = 2;//主播点观众
    private static final int TYPE_AUD_ANC = 3;//观众点主播
    private static final int TYPE_AUD_SELF = 4;//观众点自己
    private static final int TYPE_ANC_SELF = 5;//主播点自己

    private static final int SETTING_ACTION_SELF = 0;//设置 自己点自己
    private static final int SETTING_ACTION_AUD = 30;//设置 普通观众点普通观众 或所有人点超管
    private static final int SETTING_ACTION_ADM = 40;//设置 房间管理员点普通观众
    private static final int SETTING_ACTION_SUP = 60;//设置 超管点主播
    private static final int SETTING_ACTION_ANC_AUD = 501;//设置 主播点普通观众
    private static final int SETTING_ACTION_ANC_ADM = 502;//设置 主播点房间管理员

    private RoundedImageView mImgAvator;
    private TextView mTvName;
    private TextView mTvId;
    private TextView mBtnSetOrder;
    private TextView mBtnReward;
    private ViewGroup mBtnAttention;
    private ImageView mImgAttention;
    private TextView mTvAttention;
    private TextView mBtnHomePage;
    private TextView mBtnHomeMsg;
    private TextView mTvFans;
    private ViewGroup mVpSkill;
    private TextView mTvSkillName;
    private TextView mTvSignature;
    private View mVLine1;

    private LinearLayout mSexGroup;
    private ImageView mSex;
    private TextView mAge;
    private ImageView iv_anchor_level;
    private ImageView iv_level;

    private UserBean mUserBean;
    //    private boolean mCanReword;
//    private SkillBean mSkillBean;
//    private String mSkillId;
    private String mToUid;
    private String mLiveUid;
    private int mType;
    private int mAction;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_place_holder_2;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = SystemUtil.getWindowsPixelWidth(getActivity()) - DpUtil.dp2px(50);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void init() {
        super.init();
//        LiveActivityLifeModel liveActivityLifeModel = LiveActivityLifeModel.getByContext(getActivity(), LiveActivityLifeModel.class);
//        if (liveActivityLifeModel != null) {
//            mLiveBean = liveActivityLifeModel.getLiveBean();
//            mSkillId = liveActivityLifeModel.getSkillId();
//            mCanReword = liveActivityLifeModel.isOnWheat(mUserBean);
//        }

        mImgAvator = (RoundedImageView) findViewById(R.id.img_avator);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvId = (TextView) findViewById(R.id.tv_id);
        mBtnSetOrder = (TextView) findViewById(R.id.btn_set_order);
        mBtnReward = (TextView) findViewById(R.id.btn_reward);
        mBtnAttention = (ViewGroup) findViewById(R.id.btn_attention);
        mImgAttention = (ImageView) findViewById(R.id.img_attention);
        mTvAttention = (TextView) findViewById(R.id.tv_attention);
        mBtnHomePage = (TextView) findViewById(R.id.btn_home_page);
        mBtnHomeMsg = (TextView) findViewById(R.id.btn_home_msg);
        mTvFans = (TextView) findViewById(R.id.tv_fans);
        mVpSkill = (ViewGroup) findViewById(R.id.vp_skill);
        mTvSkillName = (TextView) findViewById(R.id.tv_skill_name);
        mTvSignature = (TextView) findViewById(R.id.tv_signature);
        mVLine1 = (View) findViewById(R.id.v_line1);
        mSexGroup = (LinearLayout) findViewById(R.id.sex_group);
        mSex = (ImageView) findViewById(R.id.sex);
        iv_level = findViewById(R.id.iv_level);
        iv_anchor_level = findViewById(R.id.iv_anchor_level);
        mAge = (TextView) findViewById(R.id.age);
        mBtnSetOrder.setOnClickListener(this);

        mBtnHomePage.setOnClickListener(this);
        mBtnHomeMsg.setOnClickListener(this);
        mBtnReward.setOnClickListener(this);
        mBtnAttention.setOnClickListener(this);

        String uid = CommonAppConfig.getInstance().getUid();

        //自己是主播
        boolean selfAnchor = !TextUtils.isEmpty(mLiveUid) && mLiveUid.equals(uid);
        //对方是主播
        boolean toAnchor = !TextUtils.isEmpty(mLiveUid) && mLiveUid.equals(mToUid);
        //自己点自己
        boolean selfClick = !TextUtils.isEmpty(mToUid) && mToUid.equals(uid);
       if (CommonAppConfig.getInstance().getIsState()==1){
            mBtnReward.setVisibility(View.GONE);
            mVLine1.setVisibility(View.GONE);
       }else {
           if (selfClick) {
               mBtnReward.setVisibility(View.GONE);
               mVLine1.setVisibility(View.GONE);
               if (selfAnchor) {
                   mType = TYPE_ANC_SELF;//主播点自己
               } else {
                   mType = TYPE_AUD_SELF;//观众点自己
               }
           } else {
               if (selfAnchor) {
                   mBtnReward.setVisibility(View.GONE);
                   mVLine1.setVisibility(View.GONE);
                   mType = TYPE_ANC_AUD;//主播点观众
               } else {
                   if (toAnchor) {
                       mType = TYPE_AUD_ANC;//观众点主播
                   } else {
                       mBtnReward.setVisibility(View.GONE);
                       mVLine1.setVisibility(View.GONE);
                       mType = TYPE_AUD_AUD;//观众点别的观众
                   }
               }
           }
       }

        requestData();
    }

    private void pushData() {
        if (mUserBean != null) {
            ImgLoader.display(mContext, mUserBean.getAvatar(), mImgAvator);
            mTvId.setText(StringUtil.contact("ID :", mUserBean.getId()));
            mTvFans.setText(StringUtil.contact(mUserBean.getFansNum(), WordUtil.getString(R.string.fans)));
            int sex = mUserBean.getSex();
            mSex.setImageDrawable(CommonIconUtil.getSexDrawable(sex));
            mSexGroup.setBackground(CommonIconUtil.getSexBgDrawable(sex));
            if (mUserBean.isShowAnchorLevel()) {
                iv_anchor_level.setVisibility(View.VISIBLE);
                LevelBean anchorBean = CommonAppConfig.getInstance().getAnchorLevel(mUserBean.getAnchorLevel());
                ImgLoader.display(mContext, anchorBean.getThumb(), iv_anchor_level);
            } else {
                if (iv_anchor_level.getVisibility() == View.VISIBLE) {
                    iv_anchor_level.setVisibility(View.GONE);
                }
            }
            LevelBean levelBean = CommonAppConfig.getInstance().getLevel(mUserBean.getLevel());
            ImgLoader.display(mContext, levelBean.getThumb(), iv_level);
            mTvName.setText(mUserBean.getUserNiceName());
            mAge.setText(mUserBean.getAge());
            mTvSignature.setText(mUserBean.getSignature());
            changeAttentionUI();
            if (mAction == SETTING_ACTION_AUD) {//设置 普通观众点普通观众 或所有人点超管
//                View btnReport = mRootView.findViewById(R.id.btn_report);
//                btnReport.setVisibility(View.VISIBLE);
//                btnReport.setOnClickListener(this);
            } else if (mAction == SETTING_ACTION_ADM//设置 房间管理员点普通观众
                    || mAction == SETTING_ACTION_SUP//设置 超管点主播
                    || mAction == SETTING_ACTION_ANC_AUD//设置 主播点普通观众
                    || mAction == SETTING_ACTION_ANC_ADM) {//设置 主播点房间管理员
                View btnSetting = mRootView.findViewById(R.id.btn_setting);
                btnSetting.setVisibility(View.VISIBLE);
                btnSetting.setOnClickListener(this);
            }
        }
        //&&!TextUtils.isEmpty(mSkillBean.getSkillId())&&!mSkillBean.getSkillId().equals("0")
//        if (mSkillBean != null) {
//            mVpSkill.setVisibility(View.VISIBLE);
//            mTvSkillName.setText(mSkillBean.getSkillName());
//        } else {
//            mVpSkill.setVisibility(View.GONE);
//        }
        // TODO: 2020-10-31 聊天室内，用户信息弹窗 是否显示下单按钮 
        //只有当前聊天室为派单聊天室，且弹窗点击人为BOSS、要显示的用户为麦上用户，才显示下单按钮
//        LiveActivityLifeModel liveActivityLifeModel = LifeObjectHolder.getByContext(getActivity(), LiveActivityLifeModel.class);
//        if (liveActivityLifeModel.getLiveType() == Constants.LIVE_TYPE_DISPATCH) {
//            if (liveActivityLifeModel.isOnWheat(mUserBean)) {
//                List<LiveAnthorBean> anthorBeanList = liveActivityLifeModel.getSeatList();
//                if (anthorBeanList != null && anthorBeanList.size() > 0) {
//                    //自己是老板
//                    if (anthorBeanList.get(7).getUserBean() != null) {
//                        if (CommonAppConfig.getInstance().getUid().equals(anthorBeanList.get(7).getUserBean().getId())) {
//                            if (mSkillBean != null && !TextUtils.isEmpty(mSkillBean.getSkillId()) && !mSkillBean.getSkillId().equals("0")) {
//                                mBtnSetOrder.setVisibility(View.VISIBLE);
//                            } else {
//                                mBtnSetOrder.setVisibility(View.INVISIBLE);
//                            }
//                        }
//                    }
//                }
//            }
//        } else {
//            if (mSkillBean != null && !TextUtils.isEmpty(mSkillBean.getSkillId()) && !mSkillBean.getSkillId().equals("0")) {
//                mBtnSetOrder.setVisibility(View.VISIBLE);
//            } else {
//                mBtnSetOrder.setVisibility(View.INVISIBLE);
//            }
//        }

    }

    private void requestData() {
        if (mActionListener != null) {
            mActionListener.requestData(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        JSONObject jsonObject = JSON.parseObject(info[0]);
                        mUserBean = JSON.parseObject(info[0], UserBean.class);
                        int isattent = jsonObject.getIntValue("isattention");
                        mUserBean.setIsFollow(isattent);
                        mUserBean.setSex(jsonObject.getIntValue("sex"));
                        mUserBean.setAge(jsonObject.getString("age"));
                        mUserBean.setFansNum(jsonObject.getLongValue("fans"));
                        mUserBean.setSignature(jsonObject.getString("signature"));
                        mUserBean.setLevel(jsonObject.getIntValue("level"));
                        mUserBean.setAnchorLevel(jsonObject.getIntValue("level_anchor"));
                        mUserBean.setShowAnchorLevel(jsonObject.getIntValue("isshow_anchorlev"));
                        mAction = jsonObject.getIntValue("action");
//                        mSkillBean = jsonObject.getObject("skillinfo", SkillBean.class);
//                        if (mSkillBean == null) {
//                            mSkillBean = new SkillBean();
//                        }
//                        String skillId = mSkillBean.getSkillId();
//                        if (TextUtils.isEmpty(skillId) || skillId.equals("0")) {
//                            mSkillBean.setSkillId(jsonObject.getString("skill_firstid"));
//                        }
//                        String skillName = mSkillBean.getSkillName();
//                        if (TextUtils.isEmpty(skillName)) {
//                            skillName = jsonObject.getString("skillnames");
//                            if (TextUtils.isEmpty(skillName)) {
//                                skillName = WordUtil.getString(R.string.no_skill);
//                            }
//                        }
//                        mSkillBean.setSkillName(skillName);
                        pushData();
                    }
                }
            });
        }
    }

    public void setToUid(String toUid) {
        mToUid = toUid;
    }


    public void setLiveUid(String liveUid) {
        mLiveUid = liveUid;
    }


    @Override
    public void onClick(View v) {
        if (!ClickUtil.canClick()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.btn_attention) {
            changeAttention();
        } else if (id == R.id.btn_reward) {
//            openReward();
            if (mActionListener != null) {
                mActionListener.clickGift();
            }
            dismiss();
        } else if (id == R.id.btn_home_page) {
            openUserHome();
            dismiss();
        } else if (id == R.id.btn_set_order) {
//            toOrder();
        } else if (id == R.id.btn_home_msg) {
//            openMsgDialog();
            if (mActionListener != null&&mUserBean!=null) {
                mActionListener.clickMessage(mUserBean);
            }
            dismiss();
        } else if (id == R.id.btn_setting) {
            setting();

        } else if (id == R.id.btn_report) {
            dismiss();
            if (mActionListener != null) {
                mActionListener.report(mToUid);
            }

        }
    }


    /**
     * 设置
     */
    private void setting() {
        List<Integer> list = new ArrayList<>();
        switch (mAction) {
            case SETTING_ACTION_ADM://设置 房间管理员点普通观众
//                list.add(R.string.live_setting_kick);
//                list.add(R.string.live_setting_gap);
                list.add(R.string.live_setting_gap_2);
                break;
            case SETTING_ACTION_SUP://设置 超管点主播
                list.add(R.string.live_setting_close_live);
                list.add(R.string.live_setting_close_live_2);
                list.add(R.string.live_setting_forbid_account);
                break;
            case SETTING_ACTION_ANC_AUD://设置 主播点普通观众
//                list.add(R.string.live_setting_kick);
//                list.add(R.string.live_setting_gap);
                list.add(R.string.live_setting_gap_2);
                list.add(R.string.live_setting_admin);
                list.add(R.string.live_setting_admin_list);
                break;
            case SETTING_ACTION_ANC_ADM://设置 主播点房间管理员
//                list.add(R.string.live_setting_kick);
//                list.add(R.string.live_setting_gap);
                list.add(R.string.live_setting_gap_2);
                list.add(R.string.live_setting_admin_cancel);
                list.add(R.string.live_setting_admin_list);
                break;
        }

        DialogUitl.showStringArrayDialog(mContext, list.toArray(new Integer[list.size()]), new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.live_setting_kick) {
                    kick();

                } else if (tag == R.string.live_setting_gap) {//永久禁言
                    setShutUp();

                } else if (tag == R.string.live_setting_gap_2) {//本场禁言
                    setShutUp2();

                } else if (tag == R.string.live_setting_admin || tag == R.string.live_setting_admin_cancel) {
                    setAdmin();

                } else if (tag == R.string.live_setting_admin_list) {
                    adminList();

                } else if (tag == R.string.live_setting_close_live) {
                    closeLive();

                } else if (tag == R.string.live_setting_forbid_account) {
                    forbidAccount();

                } else if (tag == R.string.live_setting_close_live_2) {//禁用直播
                    closeLive2();
                }
//                else if (tag == R.string.a_049) {//语音直播间--下麦
//                    ((LiveActivity) mContext).closeUserVoiceMic(mToUid, mAction == SETTING_ACTION_ADM ? 2 : 1);
//                }
//            else if (tag == R.string.a_050 || tag == R.string.a_053) {//语音直播间--闭麦开麦切换
//                ((LiveActivity) mContext).changeVoiceMicOpen(mToUid);
//            }
            }
        });
    }

    /**
     * 踢人
     */
    private void kick() {
        dismiss();
        if (mActionListener != null && mUserBean != null) {
            mActionListener.kick(mToUid, mUserBean.getUserNiceName());
        }
//        LiveHttpUtil.kicking(mLiveUid, mToUid, new HttpCallback() {
//            @Override
//            public void onSuccess(int code, String msg, String[] info) {
//                if (code == 0) {
//                    ((LiveActivity) mContext).kickUser(mToUid, mToName);
//                } else {
//                    ToastUtil.show(msg);
//                }
//            }
//        });
    }

    /**
     * 永久禁言
     */
    private void setShutUp() {
        if (mActionListener != null & mUserBean != null) {
            mActionListener.shutUpForever(mToUid, mUserBean.getUserNiceName());
        }
//        LiveHttpUtil.setShutUp(mLiveUid, "0", 0, mToUid, new HttpCallback() {
//            @Override
//            public void onSuccess(int code, String msg, String[] info) {
//                if (code == 0) {
//                    ((LiveActivity) mContext).setShutUp(mToUid, mToName, 0);
//                } else {
//                    ToastUtil.show(msg);
//                }
//            }
//        });
    }

    /**
     * 本场禁言
     */
    private void setShutUp2() {
        if (mActionListener != null && mUserBean != null) {
            mActionListener.shutUp(mToUid, mUserBean.getUserNiceName());
        }
//        LiveHttpUtil.setShutUp(mLiveUid, mStream, 1, mToUid, new HttpCallback() {
//            @Override
//            public void onSuccess(int code, String msg, String[] info) {
//                if (code == 0) {
//                    ((LiveActivity) mContext).setShutUp(mToUid, mToName, 1);
//                } else {
//                    ToastUtil.show(msg);
//                }
//            }
//        });
    }


    /**
     * 设置或取消管理员
     */
    private void setAdmin() {
        if (mActionListener != null) {
            mActionListener.setAdmin(mToUid, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        int res = JSON.parseObject(info[0]).getIntValue("isadmin");
                        if (res == 1) {//被设为管理员
                            mAction = SETTING_ACTION_ANC_ADM;
                        } else {//被取消管理员
                            mAction = SETTING_ACTION_ANC_AUD;
                        }
                        if (mActionListener != null) {
                            mActionListener.onSetAdmin(res, mToUid, mUserBean.getUserNiceName());
                        }
                    }
                }
            });
        }
    }


    /**
     * 超管关闭直播间
     */
    private void closeLive() {
        dismiss();
//        LiveHttpUtil.superCloseRoom(mLiveUid, 0, mSuperCloseRoomCallback);
        if (mActionListener != null) {
            mActionListener.closeLive();
        }
    }

    /**
     * 超管关闭直播间并禁止主播直播
     */
    private void closeLive2() {
        dismiss();
//        LiveHttpUtil.superCloseRoom(mLiveUid, 1, mSuperCloseRoomCallback);
        if (mActionListener != null) {
            mActionListener.closeForbidLive();
        }
    }

    /**
     * 超管关闭直播间并禁用主播账户
     */
    private void forbidAccount() {
        dismiss();
//        LiveHttpUtil.superCloseRoom(mLiveUid, 2, mSuperCloseRoomCallback);
        if (mActionListener != null) {
            mActionListener.closeForbidAccount();
        }
    }

//    private HttpCallback mSuperCloseRoomCallback = new HttpCallback() {
//        @Override
//        public void onSuccess(int code, String msg, String[] info) {
//            if (code == 0) {
//                ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
//                ((LiveActivity) mContext).superCloseRoom();
//            } else {
//                ToastUtil.show(msg);
//            }
//        }
//    };


    /**
     * 查看管理员列表
     */
    private void adminList() {
        dismiss();
//        ((LiveActivity) mContext).openAdminListWindow();
        if (mActionListener != null) {
            mActionListener.openAdminList();
        }
    }


    private void toOrder() {
//        if (mUserBean != null && mSkillBean != null) {
//            CommonHttpUtil.getSkillHome(mUserBean.getId(), mSkillBean.getSkillId(), new HttpCallback() {
//                @Override
//                public void onSuccess(int code, String msg, String[] info) {
//                    if (code == 0 && info.length > 0) {
//                        JSONObject obj = JSON.parseObject(info[0]);
//                        SkillBean skillBean = JSON.parseObject(obj.getString("authinfo"), SkillBean.class);
//                        RouteUtil.forwardOrderMakeFromLiveActivity(mUserBean, skillBean);
//                        dismiss();
//                    }
//                }
//            });
//
//        }
    }


    private void openMsgDialog() {
//        if (mContext instanceof LiveActivity) {
//            ((LiveActivity) mContext).openChatRoomWindow(mUserBean, mUserBean.getIsFollow() == 1);
//        }
    }

    private void openUserHome() {
        RouteUtil.forwardUserHome(mToUid);
    }

    private void openReward() {
//        if (mUserBean != null) {
//            LiveGiftDialogFragment liveGiftDialogFragment = new LiveGiftDialogFragment();
//            liveGiftDialogFragment.setSelcectUid(mUserBean.getId());
//            liveGiftDialogFragment.setActionListener(new LiveGiftDialogFragment.ActionListener() {
//                @Override
//                public void onChargeClick() {
//                    RouteUtil.forwardMyCoin();
//                }
//            });
//            liveGiftDialogFragment.show(getActivity().getSupportFragmentManager());
//        }
    }

    /*切换关注状态*/
    private void changeAttention() {
        if (mUserBean == null) {
            return;
        }
        CommonHttpUtil.setAttention(mUserBean.getId(), new CommonCallback<Integer>() {
            @Override
            public void callback(Integer isAttention) {
                mUserBean.setIsFollow(isAttention);
                changeAttentionUI();
            }
        });
    }


    private void changeAttentionUI() {
        int isAttention = mUserBean.getIsFollow();
        if (isAttention == 1) {
            mImgAttention.setVisibility(View.GONE);
            mTvAttention.setText(WordUtil.getString(R.string.following));
            mTvAttention.setTextColor(mContext.getResources().getColor(R.color.gray1));
        } else {
            mImgAttention.setVisibility(View.VISIBLE);
            mTvAttention.setText(WordUtil.getString(R.string.follow));
            mTvAttention.setTextColor(mContext.getResources().getColor(R.color.global));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActionListener = null;
//        CommonHttpUtil.cancel(CommonHttpConsts.GET_SKILL_HOME);
    }


    private ActionListener mActionListener;

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void requestData(HttpCallback httpCallback);


        void clickGift();

        void clickMessage(UserBean userBean);

        void setAdmin(String toUid, HttpCallback httpCallback);

        void onSetAdmin(int res, String toUid, String toName);

        void shutUp(String toUid, String toName);

        void shutUpForever(String toUid, String toName);

        void kick(String toUid, String toName);

        void openAdminList();

        void report(String toUid);

        void closeLive();

        void closeForbidLive();

        void closeForbidAccount();
    }
}
