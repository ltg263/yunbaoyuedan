package com.yunbao.main.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.chatroom.ui.activity.OpenLiveActivity;
import com.yunbao.chatroom.ui.activity.apply.ApplyHostActivity;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.bean.UserItemBean;
import com.yunbao.common.bean.UserItemBean2;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.custom.SpacesItemDecoration;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.common.views.AbsMainViewHolder;
import com.yunbao.dynamic.ui.activity.MyDynamicActivity;
import com.yunbao.main.R;
import com.yunbao.main.activity.ChooseSkillActivity;
import com.yunbao.main.activity.FansActivity;
import com.yunbao.main.activity.FollowActivity;
import com.yunbao.main.activity.FootActivity;
import com.yunbao.main.activity.InviteWebViewActivity;
import com.yunbao.main.activity.MainActivity;
import com.yunbao.main.activity.MyGiftProfitActivity;
import com.yunbao.main.activity.MyPhotoActivity;
import com.yunbao.main.activity.MyProfitActivity;
import com.yunbao.main.activity.MySkillActivity;
import com.yunbao.main.activity.OrderCenterActivity;
import com.yunbao.main.activity.SettingActivity;
import com.yunbao.main.activity.VisitActivity;
import com.yunbao.main.activity.WalletActivity;
import com.yunbao.main.activity.YoungActivity;
import com.yunbao.main.activity.YoungOpenedActivity;
import com.yunbao.main.adapter.MainMeAdapter;
import com.yunbao.main.adapter.MainMeAdapter2;
import com.yunbao.main.bean.BonusBean;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * 我的
 */

public class MainMeViewHolder2 extends AbsMainViewHolder implements View.OnClickListener, MainMeAdapter2.ActionListener {

    private ImageView mAvatar;
    private TextView mName;
    private ImageView iv_anchor_level;
    private ImageView iv_level;
    private TextView mID;
    private TextView mFollow;
    private TextView mFans;
    private boolean mPaused;
    private TextView mFootPrint;
    private TextView mVisit;
    private TextView mVisitNew;
    private MainMeAdapter2 mAdapter1;
    private MainMeAdapter2 mAdapter2;
    private MainMeAdapter2 mAdapter3;
    private MainMeAdapter2 mAdapter4;
    private TextView mTv1;
    private TextView mTv2;
    private TextView mTv0;
    private TextView mTv3;
    private TextView mMyCoin;
    private View mBtnZengZhi;
    private View mBtnWallet;
    private View mBtnMore;

    public MainMeViewHolder2(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_me_2;
    }

    @Override
    public void init() {
        mBtnZengZhi=findViewById(R.id.btn_zengzhi);
        mBtnWallet=findViewById(R.id.btn_wallet);
        mBtnMore=findViewById(R.id.btn_more);
        RecyclerView recyclerView1 = (RecyclerView) findViewById(R.id.recyclerView1);
        recyclerView1.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        mAdapter1 = new MainMeAdapter2(mContext);
        mAdapter1.setActionListener(this);
        recyclerView1.setAdapter(mAdapter1);

        RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        recyclerView2.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        mAdapter2 = new MainMeAdapter2(mContext);
        mAdapter2.setActionListener(this);
        recyclerView2.setAdapter(mAdapter2);

        RecyclerView recyclerView3 = (RecyclerView) findViewById(R.id.recyclerView3);
        recyclerView3.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        mAdapter3 = new MainMeAdapter2(mContext);
        mAdapter3.setActionListener(this);
        recyclerView3.setAdapter(mAdapter3);

        RecyclerView recyclerView4 = (RecyclerView) findViewById(R.id.recyclerView4);
        recyclerView4.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        mAdapter4 = new MainMeAdapter2(mContext);
        recyclerView4.addItemDecoration(new SpacesItemDecoration(35));
        mAdapter4.setActionListener(this);
        recyclerView4.setAdapter(mAdapter4);

        mTv0 = findViewById(R.id.tv0);
        mTv1 = findViewById(R.id.tv1);
        mTv2 = findViewById(R.id.tv2);
        mTv3 = findViewById(R.id.tv3);
        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        iv_level = findViewById(R.id.iv_level);
        iv_anchor_level = findViewById(R.id.iv_anchor_level);
        mID = findViewById(R.id.id_val);
        mFollow = findViewById(R.id.follow);
        mFans = findViewById(R.id.fans);
        mVisit = findViewById(R.id.visit);
        mFootPrint = findViewById(R.id.foot_print);
        mVisitNew = findViewById(R.id.visit_new);
        mMyCoin = findViewById(R.id.myCoin);
        findViewById(R.id.btn_edit).setOnClickListener(this);
        findViewById(R.id.btn_follow).setOnClickListener(this);
        findViewById(R.id.btn_fans).setOnClickListener(this);
        findViewById(R.id.btn_visit).setOnClickListener(this);
        findViewById(R.id.btn_foot_print).setOnClickListener(this);
        findViewById(R.id.btn_wallet).setOnClickListener(this);
        if (CommonAppConfig.getInstance().getIsState()==1){ //青少年模式
            mBtnZengZhi.setVisibility(View.GONE);
            mBtnWallet.setVisibility(View.GONE);
            mBtnMore.setVisibility(View.GONE);
        }
        CommonAppConfig appConfig = CommonAppConfig.getInstance();
        UserBean u = appConfig.getUserBean();
        List<UserItemBean2> list = appConfig.getUserItemList();
        if (u != null) {
            showData(u, list);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isShowed() && mPaused) {
            loadData();
        }
        mPaused = false;
    }

    @Override
    public void loadData() {
        MainHttpUtil.getBaseInfo(mCallback);
    }

    private CommonCallback<UserBean> mCallback = new CommonCallback<UserBean>() {
        @Override
        public void callback(UserBean bean) {
            List<UserItemBean2> list = CommonAppConfig.getInstance().getUserItemList();
            if (bean != null) {
                showData(bean, list);
            }
        }
    };

    private void showData(UserBean u, List<UserItemBean2> list) {
        ImgLoader.displayAvatar(mContext, u.getAvatarThumb(), mAvatar);
        mName.setText(u.getUserNiceName());
        if (u.isShowAnchorLevel()) {
            iv_anchor_level.setVisibility(View.VISIBLE);
            LevelBean anchorBean = CommonAppConfig.getInstance().getAnchorLevel(u.getAnchorLevel());
            ImgLoader.display(mContext, anchorBean.getThumb(), iv_anchor_level);
        } else {
            if (iv_anchor_level.getVisibility() == View.VISIBLE) {
                iv_anchor_level.setVisibility(View.GONE);
            }
        }
        LevelBean levelBean = CommonAppConfig.getInstance().getLevel(u.getLevel());
        ImgLoader.display(mContext, levelBean.getThumb(), iv_level);
        mID.setText(StringUtil.contact("ID:", u.getId()));
        mFollow.setText(StringUtil.toWan(u.getFollowNum()));
        mFans.setText(StringUtil.toWan(u.getFansNum()));
        mVisit.setText(StringUtil.toWan(u.getVisitNums()));
        mFootPrint.setText(StringUtil.toWan(u.getViewNums()));
        mMyCoin.setText(u.getCoin());
        int newNums = u.getNewNums();
        if (newNums > 0) {
            mVisitNew.setText("+" + StringUtil.toWan(newNums));
            mVisitNew.setVisibility(View.VISIBLE);
        } else {
            mVisitNew.setVisibility(View.INVISIBLE);
        }

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                UserItemBean2 bean = list.get(i);
                if (CommonAppConfig.getInstance().getIsState()==1){
                    mTv2.setText(bean.getTitle());
                    mAdapter3.refreshList(bean.getList(),4);
                    return;
                }
                if (i == 0) {
                    mTv0.setText(bean.getTitle());
                    mAdapter1.refreshList(bean.getList(),1);
                } else if (i == 1) {
                    mTv3.setText(bean.getTitle());
                    mAdapter4.refreshList(bean.getList(),2);
                } else if (i == 2) {
                    mTv1.setText(bean.getTitle());
                    mAdapter2.refreshList(bean.getList(),3);
                }else if (i == 3) {
                    mTv2.setText(bean.getTitle());
                    mAdapter3.refreshList(bean.getList(),4);
                }

            }
        }
    }

    @Override
    public void onItemClick(UserItemBean bean) {
        String url = bean.getHref();
        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith(Constants.WEBVIEW_INVITE_SUPERIOR_PREFIX_1)) {
                InviteWebViewActivity.forward(mContext, url);
            } else {
                WebViewActivity.forward(mContext, url);
            }
        } else {
            switch (bean.getId()) {
                case Constants.MAIN_ME_ORDER:
                    OrderCenterActivity.forward(mContext);
                    break;
                case Constants.MAIN_ME_WALLET:
                    forwardWallet();
                    break;
                case Constants.MAIN_ME_AUTH:
                    forwardAuth();
                    break;
                case Constants.MAIN_ME_AUTH_SKILL:
                    ChooseSkillActivity.forward(mContext);
                    break;
                case Constants.MAIN_ME_MY_SKILL:
                    MySkillActivity.forward(mContext);
                    break;
                case Constants.MAIN_ME_SETTING:
                    forwardSetting();
                    break;
                case Constants.MAIN_ME_DYNAMICS:
                    forwardDynamics();
                    break;
                case Constants.MAIN_ME_PHOTO:
                    forwardPhoto();
                    break;
                case Constants.MAIN_ME_APPLY_ROOM:
                    forwardApplyRoom();
                    break;
                case Constants.MAIN_ME_ROOM:
                    forwardOpenRoom();
                    break;
                case Constants.MAIN_ME_BONUS:
                    requestBonus();
                    break;
                case 15:
                    MyProfitActivity.forward(mContext);
                    break;
                case 16:
                    startActivity(MyGiftProfitActivity.class);
                    break;
                case 17:
                    if (CommonAppConfig.getInstance().isFloatButtonShowing()) {
                        ToastUtil.show(WordUtil.getString(R.string.can_not_do_this_in_opening_live_room));
                        return;
                    }
                    if (CommonAppConfig.getInstance().getIsState()==0){ //未开启青少年模式
                        checkUnfinishedOrder();
                    }else{
                        YoungOpenedActivity.forward(mContext);
                    }
                    break;
                    default:

            }
        }
    }

    private void forwardApplyRoom() {
        startActivity(ApplyHostActivity.class);
    }

    private void forwardOpenRoom() {
        if (CommonAppConfig.getInstance().isFloatButtonShowing()) {
            ToastUtil.show(WordUtil.getString(R.string.can_not_do_this_in_opening_live_room));
            return;
        }
        startActivity(OpenLiveActivity.class);
    }

    private void forwardPhoto() {
        startActivity(MyPhotoActivity.class);
    }

    private void forwardDynamics() {
        startActivity(MyDynamicActivity.class);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_edit) {
            RouteUtil.forwardUserHome(CommonAppConfig.getInstance().getUid());
        } else if (i == R.id.btn_follow) {
            forwardFollow();
        } else if (i == R.id.btn_fans) {
            forwardFans();
        } else if (i == R.id.btn_visit) {
            forwardVisit();
        } else if (i == R.id.btn_foot_print) {
            forwardFootPrint();
        }else if (i == R.id.btn_wallet) {
            forwardWallet();
        }
    }

    private void forwardFootPrint() {
        startActivity(FootActivity.class);
    }

    private void forwardVisit() {
        startActivity(VisitActivity.class);
    }

    /**
     * 我的关注
     */
    private void forwardFollow() {
        FollowActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
    }

    /**
     * 我的粉丝
     */
    private void forwardFans() {
        FansActivity.forward(mContext, CommonAppConfig.getInstance().getUid());
    }


    /**
     * 我要认证
     */
    private void forwardAuth() {
        UserBean u = CommonAppConfig.getInstance().getUserBean();
    }


    /**
     * 我的钱包
     */
    private void forwardWallet() {
        mContext.startActivity(new Intent(mContext, WalletActivity.class));
    }


    /**
     * 设置
     */
    private void forwardSetting() {
        mContext.startActivity(new Intent(mContext, SettingActivity.class));
    }

    /**
     * 签到奖励
     */
    private void requestBonus() {
        MainHttpUtil.requestBonus(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    List<BonusBean> list = JSON.parseArray(obj.getString("bonus_list"), BonusBean.class);
                    BonusViewHolder bonusViewHolder = new BonusViewHolder(mContext, ((MainActivity) mContext).getRootContainer());
                    bonusViewHolder.setData(list, obj.getIntValue("bonus_day"), obj.getString("count_day"), obj.getIntValue("bonus_isday") == 1);
                    bonusViewHolder.show();
                }
            }
        });
    }
    /**
     * 检查是否有未完成的订单
     */
    private void checkUnfinishedOrder(){
        MainHttpUtil.checkUnfinishedOrder(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code==0){
                    Intent intent=new Intent(mContext, YoungActivity.class);
                    mContext.startActivity(intent);
                }else if(code==1001){
                    ToastUtil.show(R.string.tip_unfinished_order);
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
    }
}
