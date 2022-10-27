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
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.LiveAnthorBean;
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
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.SystemUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.http.ChatRoomHttpUtil;
import com.yunbao.chatroom.ui.activity.LiveActivity;

import java.util.List;

/*用户dialog*/
public class LiveUserDialogFragment extends AbsDialogFragment implements View.OnClickListener {
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
    private boolean mCanReword;
    private LiveBean mLiveBean;
    private SkillBean mSkillBean;
    private String mSkillId;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_place_holder;
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
        LiveActivityLifeModel liveActivityLifeModel = LiveActivityLifeModel.getByContext(getActivity(), LiveActivityLifeModel.class);
        if (liveActivityLifeModel != null) {
            mLiveBean = liveActivityLifeModel.getLiveBean();
            mSkillId = liveActivityLifeModel.getSkillId();
            mCanReword = liveActivityLifeModel.isOnWheat(mUserBean);
        }

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

        if (!mCanReword||CommonAppConfig.getInstance().getIsState()==1) {
            mBtnReward.setVisibility(View.GONE);
            mVLine1.setVisibility(View.GONE);
        }
        mBtnHomePage.setOnClickListener(this);
        mBtnHomeMsg.setOnClickListener(this);
        mBtnReward.setOnClickListener(this);
        mBtnAttention.setOnClickListener(this);
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
        }
        //&&!TextUtils.isEmpty(mSkillBean.getSkillId())&&!mSkillBean.getSkillId().equals("0")
        if (mSkillBean != null) {
            if (CommonAppConfig.getInstance().getIsState()!=1) {
                mVpSkill.setVisibility(View.VISIBLE);
                mTvSkillName.setText(mSkillBean.getSkillName());
            }else{
                mVpSkill.setVisibility(View.GONE);
            }
        } else {
            mVpSkill.setVisibility(View.GONE);
        }
        // TODO: 2020-10-31 聊天室内，用户信息弹窗 是否显示下单按钮 
        //只有当前聊天室为派单聊天室，且弹窗点击人为BOSS、要显示的用户为麦上用户，才显示下单按钮
        LiveActivityLifeModel liveActivityLifeModel = LifeObjectHolder.getByContext(getActivity(), LiveActivityLifeModel.class);
        if (liveActivityLifeModel.getLiveType() == Constants.LIVE_TYPE_DISPATCH) {
            if (liveActivityLifeModel.isOnWheat(mUserBean)) {
                List<LiveAnthorBean> anthorBeanList = liveActivityLifeModel.getSeatList();
                if (anthorBeanList != null && anthorBeanList.size() > 0) {
                    //自己是老板
                    if (anthorBeanList.get(7).getUserBean() != null) {
                        if (CommonAppConfig.getInstance().getUid().equals(anthorBeanList.get(7).getUserBean().getId())) {
                            if (mSkillBean != null && !TextUtils.isEmpty(mSkillBean.getSkillId()) && !mSkillBean.getSkillId().equals("0")&&CommonAppConfig.getInstance().getIsState()!=1) {
                                mBtnSetOrder.setVisibility(View.VISIBLE);
                            } else {
                                mBtnSetOrder.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
        } else {
            if (mSkillBean != null && !TextUtils.isEmpty(mSkillBean.getSkillId()) && !mSkillBean.getSkillId().equals("0")&&CommonAppConfig.getInstance().getIsState()!=1) {
                mBtnSetOrder.setVisibility(View.VISIBLE);
            } else {
                mBtnSetOrder.setVisibility(View.GONE);
            }
        }

    }

    private void requestData() {
        if (mLiveBean != null && mUserBean != null) {
            ChatRoomHttpUtil.getPop(mLiveBean.getUid(), mUserBean.getId(), mSkillId).compose(this.<JSONObject>bindToLifecycle()).subscribe(new DefaultObserver<JSONObject>() {
                @Override
                public void onNext(JSONObject jsonObject) {
                    int isattent = jsonObject.getIntValue("isattent");
                    mUserBean.setIsFollow(isattent);
                    mUserBean.setSex(jsonObject.getIntValue("sex"));
                    mUserBean.setAge(jsonObject.getString("age"));
                    mUserBean.setFansNum(jsonObject.getLongValue("fans"));
                    mUserBean.setSignature(jsonObject.getString("signature"));
                    mUserBean.setLevel(jsonObject.getIntValue("level"));
                    mUserBean.setAnchorLevel(jsonObject.getIntValue("level_anchor"));
                    mUserBean.setShowAnchorLevel(jsonObject.getIntValue("isshow_anchorlev"));
                    mSkillBean = jsonObject.getObject("skillinfo", SkillBean.class);
                    if (mSkillBean == null) {
                        mSkillBean = new SkillBean();
                    }
                    String skillId = mSkillBean.getSkillId();
                    if (TextUtils.isEmpty(skillId) || skillId.equals("0")) {
                        mSkillBean.setSkillId(jsonObject.getString("skill_firstid"));
                    }
                    String skillName = mSkillBean.getSkillName();
                    if (TextUtils.isEmpty(skillName)) {
                        skillName = jsonObject.getString("skillnames");
                        if (TextUtils.isEmpty(skillName)) {
                            skillName = WordUtil.getString(R.string.no_skill);
                        }
                    }
                    mSkillBean.setSkillName(skillName);
                    pushData();
                }
            });
        }
    }

    public void setUserBean(UserBean userBean) {
        mUserBean = userBean;
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
            openReward();
            dismiss();
        } else if (id == R.id.btn_home_page) {
            openUserHome();
            dismiss();
        } else if (id == R.id.btn_set_order) {
            toOrder();
        } else if (id == R.id.btn_home_msg) {
            openMsgDialog();
            dismiss();
        }
    }

    private void toOrder() {
        if (mUserBean != null && mSkillBean != null) {
            CommonHttpUtil.getSkillHome(mUserBean.getId(), mSkillBean.getSkillId(), new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        SkillBean skillBean = JSON.parseObject(obj.getString("authinfo"), SkillBean.class);
                        RouteUtil.forwardOrderMakeFromLiveActivity(mUserBean, skillBean);
                        dismiss();
                    }
                }
            });

        }
    }


    private void openMsgDialog() {
        if (mContext instanceof LiveActivity) {
            ((LiveActivity) mContext).openChatRoomWindow(mUserBean, mUserBean.getIsFollow() == 1);
        }
    }

    private void openUserHome() {
        if (mUserBean != null) {
            RouteUtil.forwardUserHome(mUserBean.getId());
        }
    }

    private void openReward() {
        if (mUserBean != null) {
            LiveGiftDialogFragment liveGiftDialogFragment = new LiveGiftDialogFragment();
            liveGiftDialogFragment.setSelcectUid(mUserBean.getId());
            liveGiftDialogFragment.setActionListener(new LiveGiftDialogFragment.ActionListener() {
                @Override
                public void onChargeClick() {
                    RouteUtil.forwardMyCoin();
                }
            });
            liveGiftDialogFragment.show(getActivity().getSupportFragmentManager());
        }
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


    public static void showLiveUserFragment(FragmentActivity fragmentActivity, UserBean userBean) {
        if (userBean == null || StringUtil.equals(userBean.getId(), CommonAppConfig.getInstance().getUid()) || fragmentActivity == null) {
            return;
        }
        LiveUserDialogFragment userDialogFragment = new LiveUserDialogFragment();
        userDialogFragment.setUserBean(userBean);
        userDialogFragment.show(fragmentActivity.getSupportFragmentManager());
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
        CommonHttpUtil.cancel(CommonHttpConsts.GET_SKILL_HOME);
    }
}
