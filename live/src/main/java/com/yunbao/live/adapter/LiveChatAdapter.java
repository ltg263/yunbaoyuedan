package com.yunbao.live.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.live.R;
import com.yunbao.common.bean.LiveChatBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/10.
 */

public class LiveChatAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<LiveChatBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<LiveChatBean> mOnItemClickListener;
    private RecyclerView mRecyclerView;
    private Runnable mRunnable;
    private String mLiveUid;

    public LiveChatAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    LiveChatBean bean = (LiveChatBean) tag;
                    if (bean.getType() != LiveChatBean.SYSTEM && mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean, 0);
                    }
                }
            }
        };
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mList.size() > 0 && mRecyclerView != null) {
                    mRecyclerView.scrollToPosition(mList.size() - 1);
                }
            }
        };

    }


    public void setLiveUid(String liveUid) {
        mLiveUid = liveUid;
    }

    public void setOnItemClickListener(OnItemClickListener<LiveChatBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == LiveChatBean.SYSTEM) {
            return new SystemVh(mInflater.inflate(R.layout.item_recly_live_chat_sysytem, parent, false));
        } else if (viewType == LiveChatBean.ENTER_ROOM) {
            return new SystemVh(mInflater.inflate(R.layout.item_recly_live_chat_enter_room, parent, false));
        } else if (viewType == LiveChatBean.GIFT) {
            return new GiftVh(mInflater.inflate(R.layout.item_recly_live_chat_gift, parent, false));
        }
        return new ChatVh(mInflater.inflate(R.layout.item_recly_live_chat_normal, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }


    class Vh extends RecyclerView.ViewHolder {

        TextView mContent;

        public Vh(@NonNull View itemView) {
            super(itemView);
            mContent = itemView.findViewById(R.id.tv_content);
        }

        void setData(LiveChatBean bean) {

        }

    }

    class SystemVh extends Vh {

        public SystemVh(@NonNull View itemView) {
            super(itemView);
        }

        void setData(LiveChatBean bean) {
            mContent.setText(bean.getContent());
        }

    }


    class GiftVh extends Vh {

        public GiftVh(@NonNull View itemView) {
            super(itemView);
        }

        void setData(LiveChatBean bean) {
            if (TextUtils.isEmpty(bean.getUserNiceName()) || TextUtils.isEmpty(bean.getToUserNiceName())) {
                mContent.setText(bean.getContent());

            } else {
                String content = bean.getContent();
                SpannableString spanString = new SpannableString(content);
                ForegroundColorSpan span = new ForegroundColorSpan(Color.WHITE);
                ForegroundColorSpan span2 = new ForegroundColorSpan(Color.WHITE);
                spanString.setSpan(span, 0, bean.getUserNiceName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                int index = content.lastIndexOf(bean.getToUserNiceName());
                spanString.setSpan(span2, index, index + bean.getToUserNiceName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mContent.setText(spanString);
            }
        }

    }


    class ChatVh extends Vh {

        TextView mAge;
        ImageView mSex;
        View mSexGroup;
        ImageView mImgAnchorLevel;
        ImageView mImgLevel;
        TextView mName;


        public ChatVh(@NonNull View itemView) {
            super(itemView);
            mAge = itemView.findViewById(R.id.age);
            mSex = itemView.findViewById(R.id.sex);
            mSexGroup = itemView.findViewById(R.id.sex_group);
            mImgAnchorLevel = itemView.findViewById(R.id.iv_anchor_level);
            mImgLevel = itemView.findViewById(R.id.iv_level);
            mName = itemView.findViewById(R.id.tv_name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveChatBean bean) {
            itemView.setTag(bean);
            mAge.setText(bean.getAge());
            mSex.setImageDrawable(CommonIconUtil.getSexDrawable(bean.getSex()));
            mSexGroup.setBackground(CommonIconUtil.getSexBgDrawable(bean.getSex()));
            if (bean.getId().equals(mLiveUid)) {
                if (mImgAnchorLevel.getVisibility() != View.VISIBLE) {
                    mImgAnchorLevel.setVisibility(View.VISIBLE);
                }
                LevelBean levelAnchor = CommonAppConfig.getInstance().getAnchorLevel(bean.getAnchorLevel());
                if (levelAnchor != null) {
                    ImgLoader.display(mContext, levelAnchor.getThumb(), mImgAnchorLevel);
                }
            } else {
                if (mImgAnchorLevel.getVisibility() != View.GONE) {
                    mImgAnchorLevel.setVisibility(View.GONE);
                }
            }
            LevelBean levelBean = CommonAppConfig.getInstance().getLevel(bean.getLevel());
            if (levelBean != null) {
                ImgLoader.display(mContext, levelBean.getThumb(), mImgLevel);
            }
            mName.setText(bean.getUserNiceName());
            mContent.setText(bean.getContent());
        }
    }


    public void insertItem(LiveChatBean bean) {
        if (bean == null) {
            return;
        }
        int size = mList.size();
        mList.add(bean);
        notifyItemInserted(size);
        scrollToBottom();
    }

    public void scrollToBottom() {
        if (mList.size() > 0 && mRecyclerView != null) {
            mRecyclerView.scrollToPosition(mList.size() - 1);
            mRecyclerView.postDelayed(mRunnable, 50);
        }
    }


    public void clear() {
        if (mList != null) {
            mList.clear();
        }
        notifyDataSetChanged();
    }
}
