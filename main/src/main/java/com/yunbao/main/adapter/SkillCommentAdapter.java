package com.yunbao.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.main.R;
import com.yunbao.common.bean.OrderCommentBean;
import com.yunbao.main.bean.TagBean;
import com.yunbao.main.custom.StarCountView;
import com.yunbao.main.custom.TagGroup;

import java.util.List;

/**
 * Created by cxf on 2018/9/26.
 * 游戏技能 评论
 */

public class SkillCommentAdapter extends RefreshAdapter<OrderCommentBean> {

    private static final int HEAD = -1;
    private View.OnClickListener mOnClickListener;
    private List<TagBean> mTagList;
    private HeadVh mHeadVh;

    public SkillCommentAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
    }


    public void setTagList(List<TagBean> list) {
        mTagList = list;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            if (mHeadVh == null) {
                mHeadVh = new HeadVh(mInflater.inflate(R.layout.item_game_comment_head, parent, false));
                mHeadVh.setIsRecyclable(false);
            }
            return mHeadVh;
        }
        return new Vh(mInflater.inflate(R.layout.item_game_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position - 1), position - 1);
        } else if (vh instanceof HeadVh) {
            ((HeadVh) vh).setData();
        }

    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        TextView mTime;
        StarCountView mStar;
        TextView mComment;
        TextView mTags;


        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mTime = itemView.findViewById(R.id.time);
            mStar = itemView.findViewById(R.id.star);
            mComment = itemView.findViewById(R.id.comment);
            mTags = itemView.findViewById(R.id.tags);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(final OrderCommentBean bean, int position) {
            itemView.setTag(position);
            final UserBean u = bean.getUserBean();
            if (u != null) {
                ImgLoader.display(mContext, u.getAvatar(), mAvatar);
                mName.setText(u.getUserNiceName());
            }
            mAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (u != null){
                        RouteUtil.forwardUserHome(u.getId());
                    }
                }
            });
            mTime.setText(bean.getAddTimeString());
            mStar.setFillCount(bean.getStar());
            mComment.setText(bean.getContent());
            mTags.setText(bean.getLabelString());

        }

    }

    class HeadVh extends RecyclerView.ViewHolder {

        private ViewGroup mTagContainer;
        private boolean mTagShowed;

        public HeadVh(View itemView) {
            super(itemView);
            mTagContainer = itemView.findViewById(R.id.tag_container);
        }


        void setData() {
            if (mTagShowed || mTagList == null || mTagList.size() == 0) {
                return;
            }
            mTagShowed = true;
            int size = mTagList.size();
            int count = size / 3;
            int last = size % 3;
            LayoutInflater inflater = LayoutInflater.from(mContext);
            for (int i = 0; i < count; i++) {
                TagGroup tagGroup = (TagGroup) inflater.inflate(R.layout.item_game_comment_tag, mTagContainer, false);
                TagBean[] tags = new TagBean[3];
                for (int j = 0; j < 3; j++) {
                    tags[j] = mTagList.get(i * 3 + j);
                }
                tagGroup.setTagBeans(tags);
                mTagContainer.addView(tagGroup);
            }
            if (last != 0) {
                TagGroup tagGroup = (TagGroup) inflater.inflate(R.layout.item_game_comment_tag, mTagContainer, false);
                TagBean[] tags = new TagBean[last];
                for (int j = 0; j < last; j++) {
                    tags[j] = mTagList.get(count * 3 + j);
                }
                tagGroup.setTagBeans(tags);
                mTagContainer.addView(tagGroup);
            }
        }
    }


}
