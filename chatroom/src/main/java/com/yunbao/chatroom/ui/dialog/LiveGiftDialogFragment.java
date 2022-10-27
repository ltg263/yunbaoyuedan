package com.yunbao.chatroom.ui.dialog;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.ChatGiftBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.im.adapter.ChatGiftCountAdapter;
import com.yunbao.im.adapter.ChatGiftPagerAdapter;
import com.yunbao.im.http.ImHttpConsts;
import com.yunbao.im.http.ImHttpUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.adapter.GiftUserAdapter;
import com.yunbao.common.bean.LiveBean;
import com.yunbao.chatroom.business.live.LiveActivityLifeModel;
import com.yunbao.chatroom.business.socket.SocketProxy;
import com.yunbao.chatroom.business.socket.base.mannger.GiftMannger;

import java.util.List;

public class LiveGiftDialogFragment extends AbsDialogFragment implements View.OnClickListener, OnItemClickListener<String>, ChatGiftPagerAdapter.ActionListener {

    private TextView mCoin;
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private View mLoading;
    private View mArrow;
    private View mBtnSend;
    private View mBtnSendGroup;
    private View mBtnSendLian;
    private TextView mBtnChooseCount;
    private PopupWindow mGiftCountPopupWindow;//选择分组数量的popupWindow
    private Drawable mDrawable1;
    private Drawable mDrawable2;
    private ChatGiftPagerAdapter mLiveGiftPagerAdapter;
    private ChatGiftBean mChatGiftBean;
    private static final String DEFAULT_COUNT = "1";
    private String mCount = DEFAULT_COUNT;
    private String mLiveUid;
    private String mSessionId;
    private Handler mHandler;
    private int mLianCountDownCount;//连送倒计时的数字
    private TextView mLianText;
    private static final int WHAT_LIAN = 100;
    private boolean mShowLianBtn;//是否显示了连送按钮
    private HttpCallback mSendGiftCallback;
    private ActionListener mActionListener;
    private RecyclerView mReclyUser;
    private GiftUserAdapter mGiftUserAdapter;
    private LiveBean mLiveBean;
    private GiftMannger mGiftMannger;
    private String mSelcectUid;
    private LiveActivityLifeModel mLiveActivityLifeModel;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_gift_chat;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }


    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    public void setSelcectUid(String selcectUid) {
        mSelcectUid = selcectUid;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLiveActivityLifeModel = LiveActivityLifeModel.getByContext(getActivity(), LiveActivityLifeModel.class);
        if (mLiveActivityLifeModel != null) {
            mLiveBean = mLiveActivityLifeModel.getLiveBean();
            mLiveUid = mLiveBean == null ? null : mLiveBean.getUid();
            SocketProxy socketProxy = mLiveActivityLifeModel.getSocketProxy();
            mGiftMannger = socketProxy == null ? null : socketProxy.getGiftMannger();
        }


        initReclyView();
        mCoin = (TextView) mRootView.findViewById(R.id.coin);
        mLoading = mRootView.findViewById(R.id.loading);
        mArrow = mRootView.findViewById(R.id.arrow);
        mBtnSend = mRootView.findViewById(R.id.btn_send);
        mBtnSendGroup = mRootView.findViewById(R.id.btn_send_group);
        mBtnSendLian = mRootView.findViewById(R.id.btn_send_lian);
        mBtnChooseCount = (TextView) mRootView.findViewById(R.id.btn_choose);
        mLianText = (TextView) mRootView.findViewById(R.id.lian_text);
        mDrawable1 = ContextCompat.getDrawable(mContext, R.drawable.bg_chat_gift_send);
        mDrawable2 = ContextCompat.getDrawable(mContext, R.drawable.bg_chat_gift_send_2);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mRadioGroup != null) {
                    ((RadioButton) mRadioGroup.getChildAt(position)).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mRadioGroup = (RadioGroup) mRootView.findViewById(R.id.radio_group);
        mBtnSend.setOnClickListener(this);
        mBtnSendLian.setOnClickListener(this);
        mBtnChooseCount.setOnClickListener(this);
        setOnClickListener(R.id.charge, this);
        mCoin.setOnClickListener(this);
        setOnClickListener(R.id.ll_pay, this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mLianCountDownCount--;
                if (mLianCountDownCount == 0) {
                    hideLianBtn();
                } else {
                    if (mLianText != null) {
                        mLianText.setText(mLianCountDownCount + "s");
                        if (mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(WHAT_LIAN, 1000);
                        }
                    }
                }
            }
        };
        loadData();
    }

    /*初始化头部reclyview*/
    private void initReclyView() {
        mReclyUser = (RecyclerView) findViewById(R.id.recly_user);
        if(mSendGiftActionListener!=null&&mSendGiftActionListener.isLive()){
            mReclyUser.setVisibility(View.GONE);
        }else{
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            mReclyUser.setLayoutManager(linearLayoutManager);
            if (mLiveBean != null) {
                mGiftUserAdapter = new GiftUserAdapter(GiftUserAdapter.createData(mLiveBean, mLiveActivityLifeModel.getSeatList()));
                mGiftUserAdapter.select(mSelcectUid);
                mGiftUserAdapter.setOnItemClicker(new GiftUserAdapter.OnItemClicker() {
                    @Override
                    public void click() {
                        sendButtonIsEnable();
                    }
                });
                mReclyUser.setAdapter(mGiftUserAdapter);

            }
        }

    }

    private void loadData() {
        ImHttpUtil.getGiftList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    List<ChatGiftBean> list = JSON.parseArray(obj.getString("list"), ChatGiftBean.class);
                    showGiftList(list);
                    mCoin.setText(obj.getString("coin"));
                }
            }

            @Override
            public void onFinish() {
                if (mLoading != null) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void showGiftList(List<ChatGiftBean> list) {
        mLiveGiftPagerAdapter = new ChatGiftPagerAdapter(mContext, list);
        mLiveGiftPagerAdapter.setActionListener(this);
        mViewPager.setAdapter(mLiveGiftPagerAdapter);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0, size = mLiveGiftPagerAdapter.getCount(); i < size; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_chat_indicator, mRadioGroup, false);
            radioButton.setId(i + 10000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            mRadioGroup.addView(radioButton);
        }
    }


    @Override
    public void onDestroy() {
        mActionListener = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mGiftCountPopupWindow != null) {
            mGiftCountPopupWindow.dismiss();
        }
        ImHttpUtil.cancel(ImHttpConsts.GET_GIFT_LIST);
        ImHttpUtil.cancel(ImHttpConsts.SEND_GIFT);
        if (mLiveGiftPagerAdapter != null) {
            mLiveGiftPagerAdapter.release();
        }
        mContext = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_send || i == R.id.btn_send_lian) {
            sendGift();
        } else if (i == R.id.btn_choose) {
            showGiftCount();

        } else if (i == R.id.charge) {
            forwardMyCoin();
        }
    }

    /**
     * 跳转到我的钻石
     */
    private void forwardMyCoin() {
        dismiss();
        if (mActionListener != null) {
            mActionListener.onChargeClick();
        }
    }

    /**
     * 显示分组数量
     */
    private void showGiftCount() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.view_gift_count, null);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, true));
        ChatGiftCountAdapter adapter = new ChatGiftCountAdapter(mContext);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        mGiftCountPopupWindow = new PopupWindow(v, DpUtil.dp2px(70), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mGiftCountPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mGiftCountPopupWindow.setOutsideTouchable(true);
        mGiftCountPopupWindow.showAtLocation(mBtnChooseCount, Gravity.BOTTOM | Gravity.RIGHT, DpUtil.dp2px(70), DpUtil.dp2px(40));
    }

    /**
     * 隐藏分组数量
     */
    private void hideGiftCount() {
        if (mGiftCountPopupWindow != null) {
            mGiftCountPopupWindow.dismiss();
        }
    }

    @Override
    public void onItemClick(String bean, int position) {
        mCount = bean;
        mBtnChooseCount.setText(bean);
        hideGiftCount();
    }

    @Override
    public void onItemChecked(ChatGiftBean bean) {
        mChatGiftBean = bean;
        hideLianBtn();
        sendButtonIsEnable();
        if (!DEFAULT_COUNT.equals(mCount)) {
            mCount = DEFAULT_COUNT;
            mBtnChooseCount.setText(DEFAULT_COUNT);
        }
        if (bean.getType() == ChatGiftBean.TYPE_DELUXE) {
            if (mBtnChooseCount != null && mBtnChooseCount.getVisibility() == View.VISIBLE) {
                mBtnChooseCount.setVisibility(View.INVISIBLE);
                mArrow.setVisibility(View.INVISIBLE);
                mBtnSend.setBackground(mDrawable2);
            }
        } else {
            if (mBtnChooseCount != null && mBtnChooseCount.getVisibility() != View.VISIBLE) {
                mBtnChooseCount.setVisibility(View.VISIBLE);
                mArrow.setVisibility(View.VISIBLE);
                mBtnSend.setBackground(mDrawable1);
            }
        }
    }

    private void sendButtonIsEnable() {
        if (mChatGiftBean != null/*&&mGiftUserAdapter.haveSelect()*/) {
            if (mGiftUserAdapter != null) {
                if (mGiftUserAdapter.haveSelect()) {
                    mBtnSend.setEnabled(true);
                }
            } else {
                mBtnSend.setEnabled(true);
            }
        } else {
            mBtnSend.setEnabled(false);
        }
    }

    /**
     * 赠送礼物
     */

    public void sendGift() {
        String uidArray = null;
        if (mGiftUserAdapter != null) {
            uidArray = mGiftUserAdapter.getUids();
            if (TextUtils.isEmpty(uidArray)) {
                return;
            }
        }

        if (mChatGiftBean == null) {
            return;
        }

        if (mSendGiftCallback == null) {
            mSendGiftCallback = new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        if (info.length > 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            String coin = obj.getString("coin");
                            String gifttoken = obj.getString("gifttoken");
                            if (mSendGiftActionListener != null){
                                mSendGiftActionListener.onSendEnd(mChatGiftBean.getType(),gifttoken);
                            }else{
                                if (mGiftMannger != null) {
                                    mGiftMannger.sendGiftMessage(mChatGiftBean.getType(), gifttoken, mLiveUid);
                                }
                            }

                            if (mCoin != null) {
                                mCoin.setText(coin);
                            }
                            if (mChatGiftBean.getType() == ChatGiftBean.TYPE_NORMAL) {
                                showLianBtn();
                            }
                        }
                    } else {
                        hideLianBtn();
                        ToastUtil.show(msg);
                    }
                }
            };
        }
        if (mSendGiftActionListener != null) {
            mSendGiftActionListener.startSend(mChatGiftBean.getId(), mCount, mSendGiftCallback);
        } else {
            ImHttpUtil.sendGift(uidArray, uidArray, mChatGiftBean.getId(), mCount, mSendGiftCallback);
        }

    }

    /**
     * 隐藏连送按钮
     */

    private void hideLianBtn() {
        mShowLianBtn = false;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_LIAN);
        }
        if (mBtnSendLian != null && mBtnSendLian.getVisibility() == View.VISIBLE) {
            mBtnSendLian.setVisibility(View.INVISIBLE);
        }
        if (mBtnSendGroup != null && mBtnSendGroup.getVisibility() != View.VISIBLE) {
            mBtnSendGroup.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示连送按钮
     */

    private void showLianBtn() {
        if (mLianText != null) {
            mLianText.setText("5s");
        }
        mLianCountDownCount = 5;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_LIAN);
            mHandler.sendEmptyMessageDelayed(WHAT_LIAN, 1000);
        }
        if (mShowLianBtn) {
            return;
        }
        mShowLianBtn = true;
        if (mBtnSendGroup != null && mBtnSendGroup.getVisibility() == View.VISIBLE) {
            mBtnSendGroup.setVisibility(View.INVISIBLE);
        }
        if (mBtnSendLian != null && mBtnSendLian.getVisibility() != View.VISIBLE) {
            mBtnSendLian.setVisibility(View.VISIBLE);
        }
    }

    public interface ActionListener {
        void onChargeClick();
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    private SendGiftActionListener mSendGiftActionListener;

    public void setSendGiftActionListener(SendGiftActionListener sendGiftActionListener) {
        mSendGiftActionListener = sendGiftActionListener;
    }

    public interface SendGiftActionListener {
        void startSend(int giftId, String giftCount, HttpCallback httpCallback);

        void onSendEnd(int giftType,String giftToken);

        boolean isLive();
    }



}
