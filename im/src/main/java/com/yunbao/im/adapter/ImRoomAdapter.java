package com.yunbao.im.adapter;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.ChatReceiveGiftBean;
import com.yunbao.common.bean.OrderBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.DrawableTextView;
import com.yunbao.common.event.OrderChangedEvent;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.server.observer.DialogObserver;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.GiftTextRender;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.ResourceUtil;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.ViewUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.im.R;
import com.yunbao.im.bean.ChatInfoBean;
import com.yunbao.im.bean.ImChatImageBean;
import com.yunbao.im.bean.ImMessageBean;
import com.yunbao.im.bean.ImMsgLocationBean;
import com.yunbao.im.bean.OrderTipBean;
import com.yunbao.im.custom.ChatVoiceLayout;
import com.yunbao.im.custom.MyImageView;
import com.yunbao.im.interfaces.SendMsgResultCallback;
import com.yunbao.im.utils.ImDateUtil;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.im.utils.ImTextRender;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/25.
 */

public class ImRoomAdapter extends RecyclerView.Adapter {

    private static final byte TYPE_TEXT_LEFT = 1;
    private static final byte TYPE_TEXT_RIGHT = 2;
    private static final byte TYPE_IMAGE_LEFT = 3;
    private static final byte TYPE_IMAGE_RIGHT = 4;
    private static final byte TYPE_VOICE_LEFT = 5;
    private static final byte TYPE_VOICE_RIGHT = 6;
    private static final byte TYPE_LOCATION_LEFT = 7;
    private static final byte TYPE_LOCATION_RIGHT = 8;
    private static final byte TYPE_GIFT_LEFT = 9;
    private static final byte TYPE_GIFT_RIGHT = 10;

    private static final byte TYPE_CALL_LEFT = 11;
    private static final byte TYPE_CALL_RIGHT = 12;
    private static final byte TYPE_ORDER_NOW = 13;
    private static final byte TYPE_ORDER_NORMAL = 14;
    private static final byte TYPE_ORDER_AGAIN = 15;
    private static final byte TYPE_ORDER_REFUND = 16;

    private Context mContext;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private UserBean mUserBean;
    private UserBean mToUserBean;
    private String mToUid;
    private String mTxMapAppKey;
    private String mTxMapAppSecret;
    private List<ImMessageBean> mList;
    private LayoutInflater mInflater;
    private String mUserAvatar;
    private String mToUserAvatar;
    private long mLastMessageTime;
    private ActionListener mActionListener;
    private View.OnClickListener mOnImageClickListener;
    private View.OnClickListener mOnAvatorClickListner;

    private int[] mLocation;
    private ValueAnimator mAnimator;
    private ChatVoiceLayout mChatVoiceLayout;
    private View.OnClickListener mOnVoiceClickListener;
    private CommonCallback<File> mVoiceFileCallback;
    private Drawable mChatVideoDrawable1;
    private Drawable mChatVideoDrawable2;
    private Drawable mChatVoiceDrawable1;
    private Drawable mChatVoiceDrawable2;


    private int mContainerType;//当前聊天环境，TYPE_ACTIVITY 外部消息activity ； 聊天室内TYPE_DIALOG
    public static final int TYPE_ACTIVITY = 0;
    public static final int TYPE_DIALOG = 1;

    public ImRoomAdapter(Context context, String toUid, UserBean toUserBean,int containerType) {
        mContext = context;
        mContainerType = containerType;
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mUserBean = CommonAppConfig.getInstance().getUserBean();
        mToUserBean = toUserBean;
        mToUid = toUid;
        mTxMapAppKey = CommonAppConfig.getInstance().getTxMapAppKey();
        mTxMapAppSecret = CommonAppConfig.getInstance().getTxMapAppSecret();
        mUserAvatar = mUserBean.getAvatarThumb();
        mToUserAvatar = mToUserBean.getAvatarThumb();
        mLocation = new int[2];
        mOnImageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtil.canClick()) {
                    return;
                }
                MyImageView imageView = (MyImageView) v;
                imageView.getLocationOnScreen(mLocation);
                if (mActionListener != null) {
                    mActionListener.onImageClick(imageView, mLocation[0], mLocation[1]);
                }
            }
        };
        mVoiceFileCallback = new CommonCallback<File>() {
            @Override
            public void callback(File file) {
                if (mRecyclerView != null) {
                    mRecyclerView.setLayoutFrozen(true);
                }
                if (mAnimator != null) {
                    mAnimator.start();
                }
                if (mActionListener != null) {
                    mActionListener.onVoiceStartPlay(file);
                }
            }
        };
        mOnVoiceClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtil.canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                final int position = (int) tag;
                ImMessageBean bean = mList.get(position);
                if (mChatVoiceLayout != null) {
                    if (mRecyclerView != null) {
                        mRecyclerView.setLayoutFrozen(false);
                    }
                    mChatVoiceLayout.cancelAnim();
                    if (mChatVoiceLayout.getImMessageBean() == bean) {//同一个消息对象
                        mChatVoiceLayout = null;
                        if (mActionListener != null) {
                            mActionListener.onVoiceStopPlay();
                        }
                    } else {
                        ImMessageUtil.getInstance().setVoiceMsgHasRead(bean, new Runnable() {
                            @Override
                            public void run() {
                                notifyItemChanged(position, Constants.PAYLOAD);
                            }
                        });
                        mChatVoiceLayout = (ChatVoiceLayout) v;
                        ImMessageUtil.getInstance().getVoiceFile(bean, mVoiceFileCallback);
                    }
                } else {
                    ImMessageUtil.getInstance().setVoiceMsgHasRead(bean, new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(position, Constants.PAYLOAD);
                        }
                    });
                    mChatVoiceLayout = (ChatVoiceLayout) v;
                    ImMessageUtil.getInstance().getVoiceFile(bean, mVoiceFileCallback);
                }
            }
        };

        mAnimator = ValueAnimator.ofFloat(0, 900);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(700);
        mAnimator.setRepeatCount(-1);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                if (mChatVoiceLayout != null) {
                    mChatVoiceLayout.animate((int) (v / 300));
                }
            }
        });
        mChatVideoDrawable1 = ContextCompat.getDrawable(mContext, R.mipmap.icon_chat_video_1);
        mChatVideoDrawable2 = ContextCompat.getDrawable(mContext, R.mipmap.icon_chat_video_2);
        mChatVoiceDrawable1 = ContextCompat.getDrawable(mContext, R.mipmap.icon_chat_voice_1);
        mChatVoiceDrawable2 = ContextCompat.getDrawable(mContext, R.mipmap.icon_chat_voice_2);
    }


    /**
     * 停止语音动画
     */


    public void stopVoiceAnim() {
        if (mChatVoiceLayout != null) {
            mChatVoiceLayout.cancelAnim();
        }
        mChatVoiceLayout = null;
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutFrozen(false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ImMessageBean msg = mList.get(position);
        switch (msg.getType()) {
            case ImMessageBean.TYPE_TEXT:
                if (msg.isFromSelf()) {
                    return TYPE_TEXT_RIGHT;
                } else {
                    return TYPE_TEXT_LEFT;
                }
            case ImMessageBean.TYPE_IMAGE:
                if (msg.isFromSelf()) {
                    return TYPE_IMAGE_RIGHT;
                } else {
                    return TYPE_IMAGE_LEFT;
                }
            case ImMessageBean.TYPE_VOICE:
                if (msg.isFromSelf()) {
                    return TYPE_VOICE_RIGHT;
                } else {
                    return TYPE_VOICE_LEFT;
                }
            case ImMessageBean.TYPE_LOCATION:
                if (msg.isFromSelf()) {
                    return TYPE_LOCATION_RIGHT;
                } else {
                    return TYPE_LOCATION_LEFT;
                }
            case ImMessageBean.TYPE_CALL:
                if (msg.isFromSelf()) {
                    return TYPE_CALL_RIGHT;
                } else {
                    return TYPE_CALL_LEFT;
                }
            case ImMessageBean.TYPE_ORDER_NOW:
                return TYPE_ORDER_NOW;

            case ImMessageBean.TYPE_ORDER_TIP_AGAGIN:
                return TYPE_ORDER_AGAIN;
            case ImMessageBean.TYPE_ORDER_TIP_NORMAL:
                return TYPE_ORDER_NORMAL;
            case ImMessageBean.TYPE_ORDER_TIP_REFUND:
                return TYPE_ORDER_REFUND;
        }
        return 0;
    }

    public void setOnAvatorClickListner(View.OnClickListener onAvatorClickListner) {
        mOnAvatorClickListner = onAvatorClickListner;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TEXT_LEFT:
                return new TextVh(mInflater.inflate(R.layout.item_chat_text_left, parent, false));
            case TYPE_TEXT_RIGHT:
                return new SelfTextVh(mInflater.inflate(R.layout.item_chat_text_right, parent, false));
            case TYPE_IMAGE_LEFT:
                return new ImageVh(mInflater.inflate(R.layout.item_chat_image_left, parent, false));
            case TYPE_IMAGE_RIGHT:
                return new SelfImageVh(mInflater.inflate(R.layout.item_chat_image_right, parent, false));
            case TYPE_VOICE_LEFT:
                return new VoiceVh(mInflater.inflate(R.layout.item_chat_voice_left, parent, false));
            case TYPE_VOICE_RIGHT:
                return new SelfVoiceVh(mInflater.inflate(R.layout.item_chat_voice_right, parent, false));
            case TYPE_LOCATION_LEFT:
                return new LocationVh(mInflater.inflate(R.layout.item_chat_location_left, parent, false));
            case TYPE_LOCATION_RIGHT:
                return new SelfLocationVh(mInflater.inflate(R.layout.item_chat_location_right, parent, false));
            case TYPE_GIFT_LEFT:
                return new GiftVh(mInflater.inflate(R.layout.item_chat_gift_left, parent, false));
            case TYPE_GIFT_RIGHT:
                return new GiftVh(mInflater.inflate(R.layout.item_chat_gift_right, parent, false));
            case TYPE_CALL_LEFT:
                return new CallVh(mInflater.inflate(R.layout.item_chat_chat_left, parent, false));
            case TYPE_CALL_RIGHT:
                return new CallVh(mInflater.inflate(R.layout.item_chat_chat_right, parent, false));

            case TYPE_ORDER_NOW:
                if (CommonAppConfig.getInstance().getIsState()==1){
                    return new OrderEmptyVh(mInflater.inflate(R.layout.view_im_order_empty, parent, false));
                }else {
                    return new OrderNowVh(mInflater.inflate(R.layout.view_im_order_message_confirm, parent, false));
                }
            case TYPE_ORDER_NORMAL:
                if (CommonAppConfig.getInstance().getIsState()==1){
                    return new OrderEmptyVh(mInflater.inflate(R.layout.view_im_order_empty, parent, false));
                }else {
                    return new OrderNormalVh(mInflater.inflate(R.layout.view_im_order_message, parent, false));
                }
            case TYPE_ORDER_AGAIN:
                if (CommonAppConfig.getInstance().getIsState()==1){
                    return new OrderEmptyVh(mInflater.inflate(R.layout.view_im_order_empty, parent, false));
                }else {
                    return new OrderAgainVh(mInflater.inflate(R.layout.view_im_order_message_again, parent, false));
                }
            case TYPE_ORDER_REFUND:
                if (CommonAppConfig.getInstance().getIsState()==1){
                    return new OrderEmptyVh(mInflater.inflate(R.layout.view_im_order_empty, parent, false));
                }else {
                    return new RefundOrderVh(mInflater.inflate(R.layout.view_im_order_message, parent, false));
                }

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position, List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        if(vh instanceof Vh){
            ((Vh) vh).setData(mList.get(position), position, payload);
        }else if(vh instanceof BaseOrderVh){
            ((BaseOrderVh) vh).setData(mList.get(position), position, payload);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    }

    public int insertItem(ImMessageBean bean) {
        if (mList != null && bean != null) {
            int size = mList.size();
            mList.add(bean);
            notifyItemInserted(size);
            int lastItemPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
            if (lastItemPosition != size - 1) {
                mRecyclerView.smoothScrollToPosition(size);
            } else {
                mRecyclerView.scrollToPosition(size);
            }
            return size;
        }
        return -1;
    }

    public void insertSelfItem(final ImMessageBean bean) {
        bean.setLoading(true);
        final int position = insertItem(bean);
        if (position != -1) {
            ImMessageUtil.getInstance().sendMessage(mToUid, bean, new SendMsgResultCallback() {
                @Override
                public void onSendFinish(boolean success) {
                    bean.setLoading(false);
                    if (!success) {
                        bean.setSendFail(true);
                        //消息发送失败
                        ToastUtil.show(WordUtil.getString(R.string.im_msg_send_failed));
                        L.e("IM---消息发送失败--->");
                    }
                    notifyItemChanged(position, Constants.PAYLOAD);
                    scrollToBottom();
                }
            });
        }
    }

    public ImChatImageBean getChatImageBean(ImMessageBean imMessageBean) {
        List<ImMessageBean> list = new ArrayList<>();
        int imagePosition = 0;
        for (int i = 0, size = mList.size(); i < size; i++) {
            ImMessageBean bean = mList.get(i);
            if (bean.getType() == ImMessageBean.TYPE_IMAGE) {
                list.add(bean);
                if (bean == imMessageBean) {
                    imagePosition = list.size() - 1;
                }
            }
        }
        return new ImChatImageBean(list, imagePosition);
    }

    public void setList(List<ImMessageBean> list) {
        if (mList != null && list != null && list.size() > 0) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
//            scrollToBottom();
        }
    }

    public void scrollToBottom() {
        if (mList.size() > 0 && mLayoutManager != null) {
            L.e("scrollToBottom---"+(mList.size() - 1));
            mLayoutManager.scrollToPositionWithOffset(mList.size() - 1, -DpUtil.dp2px(20));
        }
    }

    public ImMessageBean getLastMessage() {
        if (mList == null || mList.size() == 0) {
            return null;
        }
        return mList.get(mList.size() - 1);
    }

    class OrderEmptyVh extends RecyclerView.ViewHolder {

        public OrderEmptyVh(View itemView) {
            super(itemView);
        }


        public void setData(ImMessageBean bean, int position, Object payload) {

        }
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView mAvatar;
        TextView mTime;
        ImMessageBean mImMessageBean;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mAvatar.setOnClickListener(mOnAvatorClickListner);
        }

        void setData(ImMessageBean bean, int position, Object payload) {
            mImMessageBean = bean;
            String text = ImMessageUtil.getInstance().getMessageText(bean);
            if (payload == null) {
                if (bean.isFromSelf()) {
                    mAvatar.setOnClickListener(null);
                    ImgLoader.display(mContext, mUserAvatar, mAvatar);
                } else {
                    if (!TextUtils.isEmpty(text)) {
                        if (WordUtil.getString(R.string.tip_order_received).equals(text)&&CommonAppConfig.getInstance().getIsState()==1){
                            mAvatar.setVisibility(View.GONE);
                        }
                    }
                    mAvatar.setOnClickListener(mOnAvatorClickListner);
                    ImgLoader.display(mContext, mToUserAvatar, mAvatar);
                }
                if (position == 0) {
                    mLastMessageTime = bean.getTime();
                    if (mTime.getVisibility() != View.VISIBLE) {
                        mTime.setVisibility(View.VISIBLE);
                    }
                    mTime.setText(ImDateUtil.getTimestampString(mLastMessageTime));
                } else {
                    if (ImDateUtil.isCloseEnough(bean.getTime(), mLastMessageTime)) {
                        if (mTime.getVisibility() == View.VISIBLE) {
                            mTime.setVisibility(View.GONE);
                        }
                    } else {
                        mLastMessageTime = bean.getTime();
                        if (mTime.getVisibility() != View.VISIBLE) {
                            mTime.setVisibility(View.VISIBLE);
                        }
                        mTime.setText(ImDateUtil.getTimestampString(mLastMessageTime));
                    }
                }
            }
        }
    }



    class TextVh extends Vh {
        TextView mText;

        public TextVh(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.text);
        }


        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                String text = ImMessageUtil.getInstance().getMessageText(bean);
                if (!TextUtils.isEmpty(text)) {
                    if (WordUtil.getString(R.string.tip_order_received).equals(text)&&CommonAppConfig.getInstance().getIsState()==1){
                        mText.setVisibility(View.GONE);
                    }else {
                        mText.setText(ImTextRender.renderChatMessage(text));
                    }
                }
            }
        }
    }

    class SelfTextVh extends TextVh {

        View mFailIcon;
        View mLoading;

        public SelfTextVh(View itemView) {
            super(itemView);
            mFailIcon = itemView.findViewById(R.id.icon_fail);
            mLoading = itemView.findViewById(R.id.loading);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (bean.isLoading()) {
                if (mLoading.getVisibility() != View.VISIBLE) {
                    mLoading.setVisibility(View.VISIBLE);
                }
            } else {
                if (mLoading.getVisibility() == View.VISIBLE) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
            }
            if (bean.isSendFail()) {
                if (mFailIcon.getVisibility() != View.VISIBLE) {
                    mFailIcon.setVisibility(View.VISIBLE);
                }
            } else {
                if (mFailIcon.getVisibility() == View.VISIBLE) {
                    mFailIcon.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    class ImageVh extends Vh {

        MyImageView mImg;
        CommonCallback<File> mCommonCallback;
        ImMessageBean mImMessageBean;

        public ImageVh(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mImg.setOnClickListener(mOnImageClickListener);
            mCommonCallback = new CommonCallback<File>() {
                @Override
                public void callback(File file) {
                    if (mImMessageBean != null && mImg != null) {
                        mImMessageBean.setImageFile(file);
                        mImg.setFile(file);
                        ImgLoader.displayInChatList(mContext, file, mImg);
                    }
                }
            };
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                mImMessageBean = bean;
                mImg.setImMessageBean(bean);
                File imageFile = bean.getImageFile();
                if (imageFile != null) {
                    mImg.setFile(imageFile);
                    ImgLoader.displayInChatList(mContext, imageFile, mImg);
                } else {
                    ImMessageUtil.getInstance().displayImageFile(mContext, bean, mCommonCallback);
                }
            }
        }
    }

    class SelfImageVh extends ImageVh {

        View mFailIcon;
        View mLoading;

        public SelfImageVh(View itemView) {
            super(itemView);
            mFailIcon = itemView.findViewById(R.id.icon_fail);
            mLoading = itemView.findViewById(R.id.loading);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (bean.isLoading()) {
                if (mLoading.getVisibility() != View.VISIBLE) {
                    mLoading.setVisibility(View.VISIBLE);
                }
            } else {
                if (mLoading.getVisibility() == View.VISIBLE) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
            }
            if (bean.isSendFail()) {
                if (mFailIcon.getVisibility() != View.VISIBLE) {
                    mFailIcon.setVisibility(View.VISIBLE);
                }
            } else {
                if (mFailIcon.getVisibility() == View.VISIBLE) {
                    mFailIcon.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    class VoiceVh extends Vh {

        TextView mDuration;
        View mRedPoint;
        ChatVoiceLayout mChatVoiceLayout;

        public VoiceVh(View itemView) {
            super(itemView);
            mRedPoint = itemView.findViewById(R.id.red_point);
            mDuration = (TextView) itemView.findViewById(R.id.duration);
            mChatVoiceLayout = itemView.findViewById(R.id.voice);
            mChatVoiceLayout.setOnClickListener(mOnVoiceClickListener);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                mDuration.setText(bean.getVoiceDuration() + "s");
                mChatVoiceLayout.setTag(position);
                mChatVoiceLayout.setImMessageBean(bean);
                mChatVoiceLayout.setDuration(bean.getVoiceDuration());
            }
//            if (bean.isRead()) {
//                if (mRedPoint.getVisibility() == View.VISIBLE) {
//                    mRedPoint.setVisibility(View.INVISIBLE);
//                }
//            } else {
//                if (mRedPoint.getVisibility() != View.VISIBLE) {
//                    mRedPoint.setVisibility(View.VISIBLE);
//                }
//            }
        }
    }

    class SelfVoiceVh extends Vh {

        TextView mDuration;
        ChatVoiceLayout mChatVoiceLayout;
        View mFailIcon;
        View mLoading;

        public SelfVoiceVh(View itemView) {
            super(itemView);
            mDuration = (TextView) itemView.findViewById(R.id.duration);
            mChatVoiceLayout = itemView.findViewById(R.id.voice);
            mChatVoiceLayout.setOnClickListener(mOnVoiceClickListener);
            mFailIcon = itemView.findViewById(R.id.icon_fail);
            mLoading = itemView.findViewById(R.id.loading);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                mDuration.setText(bean.getVoiceDuration() + "s");
                mChatVoiceLayout.setTag(position);
                mChatVoiceLayout.setImMessageBean(bean);
                mChatVoiceLayout.setDuration(bean.getVoiceDuration());
            }
            if (bean.isLoading()) {
                if (mLoading.getVisibility() != View.VISIBLE) {
                    mLoading.setVisibility(View.VISIBLE);
                }
            } else {
                if (mLoading.getVisibility() == View.VISIBLE) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
            }
            if (bean.isSendFail()) {
                if (mFailIcon.getVisibility() != View.VISIBLE) {
                    mFailIcon.setVisibility(View.VISIBLE);
                }
            } else {
                if (mFailIcon.getVisibility() == View.VISIBLE) {
                    mFailIcon.setVisibility(View.INVISIBLE);
                }
            }
        }
    }


    class LocationVh extends Vh {

        TextView mTitle;
        TextView mAddress;
        ImageView mMap;

        public LocationVh(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mAddress = (TextView) itemView.findViewById(R.id.address);
            mMap = (ImageView) itemView.findViewById(R.id.map);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                ImMsgLocationBean locationBean = ImMessageUtil.getInstance().getMessageLocation(bean);
                if (locationBean == null) {
                    return;
                }
                try {
                    JSONObject obj = JSON.parseObject(locationBean.getAddress());
                    mTitle.setText(obj.getString("name"));
                    mAddress.setText(obj.getString("info"));
                } catch (Exception e) {
                    mTitle.setText("");
                    mAddress.setText("");
                }
                int zoom = locationBean.getZoom();
                if (zoom > 18 || zoom < 4) {
                    zoom = 16;
                }
                double lat = locationBean.getLat();
                double lng = locationBean.getLng();
                //腾讯地图生成静态图接口
                String sign = MD5Util.getMD5("/ws/staticmap/v2/?center=" + lat + "," + lng + "&key=" + mTxMapAppKey + "&scale=2&size=200*120&zoom=" + zoom + mTxMapAppSecret);

                String staticMapUrl = "https://apis.map.qq.com/ws/staticmap/v2/?center=" + lat + "," + lng + "&size=200*120&scale=2&zoom=" + zoom + "&key=" + mTxMapAppKey + "&sig=" + sign;
                ImgLoader.display(mContext, staticMapUrl, mMap);
            }
        }
    }

    class SelfLocationVh extends LocationVh {

        View mFailIcon;
        View mLoading;

        public SelfLocationVh(View itemView) {
            super(itemView);
            mFailIcon = itemView.findViewById(R.id.icon_fail);
            mLoading = itemView.findViewById(R.id.loading);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (bean.isLoading()) {
                if (mLoading.getVisibility() != View.VISIBLE) {
                    mLoading.setVisibility(View.VISIBLE);
                }
            } else {
                if (mLoading.getVisibility() == View.VISIBLE) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
            }
            if (bean.isSendFail()) {
                if (mFailIcon.getVisibility() != View.VISIBLE) {
                    mFailIcon.setVisibility(View.VISIBLE);
                }
            } else {
                if (mFailIcon.getVisibility() == View.VISIBLE) {
                    mFailIcon.setVisibility(View.INVISIBLE);
                }
            }
        }
    }


    class GiftVh extends Vh {

        ImageView mGiftIcon;
        TextView mGiftName;
        TextView mGiftCount;

        public GiftVh(View itemView) {
            super(itemView);
            mGiftIcon = itemView.findViewById(R.id.gift_icon);
            mGiftName = itemView.findViewById(R.id.gift_name);
            mGiftCount = itemView.findViewById(R.id.gift_count);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                ChatReceiveGiftBean giftBean = bean.getGiftBean();
                if (giftBean == null) {
                    return;
                }
                ImgLoader.display(mContext, giftBean.getGiftIcon(), mGiftIcon);
                mGiftName.setText(giftBean.getGiftName());
                mGiftCount.setText(GiftTextRender.renderGiftCount2(giftBean.getGiftCount()));
            }
        }
    }

    class CallVh extends Vh {
        ImageView mIcon;
        TextView mText;

        public CallVh(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.text);
            mIcon = itemView.findViewById(R.id.icon);
        }
        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            ChatInfoBean chatInfoBean=bean.getChatInfoBean();
            if (payload == null) {
                if (chatInfoBean != null) {
                    mText.setText(chatInfoBean.getContent());
                    if (chatInfoBean.getCallType() == Constants.CHAT_TYPE_VIDEO) {
                        if (bean.isFromSelf()) {
                            mIcon.setImageDrawable(mChatVideoDrawable1);
                        } else {
                            mIcon.setImageDrawable(mChatVideoDrawable2);
                        }
                    } else {
                        if (bean.isFromSelf()) {
                            mIcon.setImageDrawable(mChatVoiceDrawable1);
                        } else {
                            mIcon.setImageDrawable(mChatVoiceDrawable2);
                        }
                    }
                }
            }
        }
    }


    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onImageClick(MyImageView imageView, int x, int y);
        void onVoiceStartPlay(File voiceFile);
        void onVoiceStopPlay();
    }

    public void release() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mActionListener = null;
        mOnImageClickListener = null;
        mOnVoiceClickListener = null;
        mContext = null;
    }

    public void clear() {
        if (mList != null) {
            mList.clear();
        }
        notifyDataSetChanged();
    }

     class OrderNowVh extends BaseOrderVh{
         private ViewGroup mVpBtn;
         private TextView mBtnRefuse;
         private TextView mBtnConfirm;
         private DrawableTextView mTvState;
         private View.OnClickListener mOnDealClickListner;


         public OrderNowVh(View itemView) {
            super(itemView);
             mVpBtn = (ViewGroup) itemView.findViewById(R.id.vp_btn);
             mBtnRefuse = (TextView) itemView.findViewById(R.id.btn_refuse);
             mBtnConfirm = (TextView) itemView.findViewById(R.id.btn_confirm);
             mTvState = (DrawableTextView) itemView.findViewById(R.id.tv_state);
        }


        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            boolean isSelf=bean.isFromSelf();

            OrderBean orderBean=bean.getOrderBean();
            if(orderBean==null){
                return;
            }
            int status=orderBean.getReceptStatus();
            String serviceTime=orderBean.getServiceTime();
            mTvHint.setText(WordUtil.getString(R.string.order_start_plan_tip_1,serviceTime));
            if(isSelf){
                mTvTitle.setText(WordUtil.getString(R.string.send_service_apply_over));
            }else{
                mTvTitle.setText(WordUtil.getString(R.string.other_side_hope_to_start_service_immediately));
            }
            if(status==OrderBean.STATUS_RECEPT_APPLYED){
                mTvState.setLeftDrawable(null);
                mTvState.setTextColor(ResourceUtil.getColor(R.color.gray_95));

                if(isSelf){
                    ViewUtil.setVisibility(mVpBtn,View.GONE);
                    ViewUtil.setVisibility(mTvState,View.VISIBLE);
                    mTvState.setText(WordUtil.getString(R.string.waiting_for_reply));
                }else{
                    if(mOnDealClickListner==null){
                       mOnDealClickListner=new View.OnClickListener() {
                            @Override
                          public void onClick(View v) {
                            clickButton(v);
                         }
                      };
                    }
                    mBtnConfirm.setTag(position);
                    mBtnRefuse.setTag(position);
                    mBtnConfirm.setOnClickListener(mOnDealClickListner);
                    mBtnRefuse.setOnClickListener(mOnDealClickListner);

                    ViewUtil.setVisibility(mVpBtn,View.VISIBLE);
                    ViewUtil.setVisibility(mTvState,View.GONE);
                }
            }else if(status==OrderBean.STATUS_RECEPT_REFUSE){
                ViewUtil.setVisibility(mVpBtn,View.GONE);
                ViewUtil.setVisibility(mTvState,View.VISIBLE);
                mTvState.setLeftDrawable(ResourceUtil.getDrawable(R.mipmap.icon_recept_order_now_refuse,true));
                mTvState.setText(WordUtil.getString(R.string.rejected));
                mTvState.setTextColor(ResourceUtil.getColor(R.color.red_e06));
            }else if(status==OrderBean.STATUS_RECEPT_ARGREE){
                ViewUtil.setVisibility(mVpBtn,View.GONE);
                ViewUtil.setVisibility(mTvState,View.VISIBLE);
                mTvState.setLeftDrawable(ResourceUtil.getDrawable(R.mipmap.icon_recept_order_now_argree,true));
                mTvState.setText(WordUtil.getString(R.string.approved));
                mTvState.setTextColor(ResourceUtil.getColor(R.color.global));
            }
        }

         private void clickButton(View v) {
             int id=v.getId();
             Object tag=v.getTag();
             if(tag==null||!(tag instanceof Integer)){
                 return;
             }
             final Integer position = (Integer) tag;
             if(position==null){
                 return;
             }

             ImMessageBean messageBean=mList.get(position);
             final OrderBean orderBean=messageBean.getOrderBean();
             if(orderBean==null){
                 return;
             }
             if (mContainerType == TYPE_DIALOG){
                 ToastUtil.show(WordUtil.getString(R.string.go_to_msg_list_plz));
                 return;
             }
             Context context=v.getContext();
             AbsActivity absActivity= (AbsActivity) context;
             if(id==R.id.btn_confirm){
                 CommonHttpUtil.upReceptStatus(orderBean.getId(),OrderBean.STATUS_RECEPT_ARGREE).
                         compose(absActivity.<Boolean>bindToLifecycle()).subscribe(new DialogObserver<Boolean>(absActivity) {
                     @Override
                     public void onNext(Boolean aBoolean) {
                         if(aBoolean){
                            orderBean.setReceptStatus(OrderBean.STATUS_RECEPT_ARGREE);
                            notifyItemChanged(position);
                         }
                     }
                 });
             }else if(id==R.id.btn_refuse){
                 CommonHttpUtil.upReceptStatus(orderBean.getId(),OrderBean.STATUS_RECEPT_REFUSE).
                         compose(absActivity.<Boolean>bindToLifecycle()).subscribe(new DialogObserver<Boolean>(absActivity) {
                     @Override
                     public void onNext(Boolean aBoolean) {
                         if(aBoolean){
                           orderBean.setReceptStatus(OrderBean.STATUS_RECEPT_REFUSE);
                          notifyItemChanged(position);
                         }
                     }
                 });
             }
         }
     }

    public List<ImMessageBean> getList() {
        return mList;
    }

    private class BaseOrderVh extends RecyclerView.ViewHolder {
         TextView mTvTitle;
         TextView mTvHint;
        TextView mTime;
        public BaseOrderVh(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvHint = (TextView) itemView.findViewById(R.id.tv_hint);
            mTime = (TextView) itemView.findViewById(R.id.time);
        }


        public void setData(ImMessageBean bean, int position, Object payload){
            if (position == 0) {
                mLastMessageTime = bean.getTime();
                if (mTime.getVisibility() != View.VISIBLE) {
                    mTime.setVisibility(View.VISIBLE);
                }
                mTime.setText(ImDateUtil.getTimestampString(mLastMessageTime));
            } else {
                if (ImDateUtil.isCloseEnough(bean.getTime(), mLastMessageTime)) {
                    if (mTime.getVisibility() == View.VISIBLE) {
                        mTime.setVisibility(View.GONE);
                    }
                } else {
                    mLastMessageTime = bean.getTime();
                    if (mTime.getVisibility() != View.VISIBLE) {
                        mTime.setVisibility(View.VISIBLE);
                    }
                    mTime.setText(ImDateUtil.getTimestampString(mLastMessageTime));
                }
            }
        }
    }

   //普通item格式
    private class OrderNormalVh extends BaseOrderVh {
        public OrderNormalVh(View itemView) {
            super(itemView);
        }
        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            OrderTipBean orderTipBean=bean.getOrderTipBean();
            if(orderTipBean==null){
                return;
            }
            mTvTitle.setText(orderTipBean.getTip_title());
            int action=orderTipBean.getAction();
            boolean isSelf=bean.isFromSelf();

            if(action==OrderTipBean.START_NOW&&!isSelf){
                mTvHint.setText(orderTipBean.getTip_des2());
            }else{
                mTvHint.setText(orderTipBean.getTip_des());
            }
        }
    }

    private class OrderAgainVh extends BaseOrderVh {
        private TextView mBtnAgain;
        public OrderAgainVh(View itemView) {
            super(itemView);
            mBtnAgain=itemView.findViewById(R.id.btn_again);
            mBtnAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if(!ClickUtil.canClick()){
                        return;
                    }
                    int id=v.getId();
                    Object tag=v.getTag();
                    if(tag==null||!(tag instanceof Integer)){
                        return;
                    }
                    final Integer position = (Integer) tag;
                    if(position==null){
                        return;
                    }
                    if (mContainerType == TYPE_DIALOG){
                        ToastUtil.show(WordUtil.getString(R.string.go_to_msg_list_plz));
                        return;
                    }
                    ImMessageBean messageBean=mList.get(position);
                    final OrderTipBean orderTipBean=messageBean.getOrderTipBean();
                    if(orderTipBean==null){
                        return;
                    }
                     UserBean userBean=orderTipBean.getUserBean();
                    SkillBean skillBean=orderTipBean.getSkillBean();
                    if(skillBean!=null&&userBean!=null){
                       RouteUtil.forwardOrderMake(userBean,skillBean);
                    }else{
                        v.setEnabled(false);
                        CommonHttpUtil.getSkillHome(orderTipBean.getLiveuid(), orderTipBean.getSkillid(), new HttpCallback() {
                         @Override
                         public void onSuccess(int code, String msg, String[] info) {
                             v.setEnabled(true);
                              if(code==0&&info.length>0){
                                  JSONObject obj = JSON.parseObject(info[0]);
                                  UserBean u = obj.toJavaObject(UserBean.class);
                                  SkillBean skillBean = JSON.parseObject(obj.getString("authinfo"), SkillBean.class);
                                  orderTipBean.setSkillBean(skillBean);
                                  orderTipBean.setUserBean(u);
                                  RouteUtil.forwardOrderMake(u,skillBean);
                              }
                         }
                         @Override
                         public void onError() {
                             super.onError();
                             v.setEnabled(true);
                         }
                     });
                    }
                }
            });
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            mBtnAgain.setTag(position);
        }
    }

    private class RefundOrderVh extends BaseOrderVh {
        public RefundOrderVh(View itemView) {
            super(itemView);
        }
        @Override
        public void setData(final ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            final OrderTipBean orderTipBean=bean.getOrderTipBean();
            if(orderTipBean==null){
                return;
            }
            final int action = orderTipBean.getAction();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContainerType == TYPE_DIALOG){
                        ToastUtil.show(WordUtil.getString(R.string.go_to_msg_list_plz));
                        return;
                    }
                    //是否 我为接单人
                    boolean isMyAnchor = StringUtil.equals(orderTipBean.getLiveuid(),CommonAppConfig.getInstance().getUid());
                    final String orderId = bean.getOrderTipBean().getOrderid();
                    if (action == 3 && isMyAnchor){
                        if (TextUtils.isEmpty(orderId)){
                            return;
                        }
                        CommonHttpUtil.getRefundInfoStatus(orderId, new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0){
                                    RouteUtil.forwardOrderRefundDeal(orderId);
                                }else {
                                    ToastUtil.show(msg);
                                }
                            }
                        });
                    }
                    if (action == 4 && !isMyAnchor){
                        if (TextUtils.isEmpty(orderId)){
                            return;
                        }
                        CommonHttpUtil.getOrderRefundStatus(orderId, new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0){
                                    new DialogUitl.Builder(mContext)
                                            .setContent(WordUtil.getString(R.string.order_refund_dialog_1))
                                            .setCancelable(true)
                                            .setBackgroundDimEnabled(true)
                                            .setCancelString(WordUtil.getString(R.string.cancel))
                                            .setConfrimString(WordUtil.getString(R.string.order_refund_dialog_2))
                                            .setClickCallback(new DialogUitl.SimpleCallback2() {
                                                @Override
                                                public void onCancelClick() {

                                                }

                                                @Override
                                                public void onConfirmClick(Dialog dialog, String content) {
                                                    CommonHttpUtil.setRefundStatus(orderId, 6, new HttpCallback() {
                                                        @Override
                                                        public void onSuccess(int code, String msg, String[] info) {
                                                            ToastUtil.show(msg);
                                                        }
                                                    });
                                                }
                                            })
                                            .build()
                                            .show();
                                }else {
                                    ToastUtil.show(msg);
                                }
                            }
                        });
                    }
                }
            });
            mTvTitle.setText(orderTipBean.getTip_title());
            if((action==3 || action == 4 || action==5)&& StringUtil.equals(orderTipBean.getLiveuid(),CommonAppConfig.getInstance().getUid())){
                mTvHint.setText(orderTipBean.getTip_des2());
            }else{
                mTvHint.setText(orderTipBean.getTip_des());
            }
        }
    }



}
