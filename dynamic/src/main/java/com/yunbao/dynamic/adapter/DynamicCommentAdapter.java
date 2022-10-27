package com.yunbao.dynamic.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.ValueFrameAnimator;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.bean.DynamicBean;
import com.yunbao.dynamic.bean.DynamicCommentBean;
import com.yunbao.dynamic.business.AnimHelper;
import com.yunbao.dynamic.http.DynamicHttpUtil;
import com.yunbao.dynamic.util.CommentTextRender;
import java.util.List;

/**
 * Created by cxf on 2018/12/3.
 */

public class DynamicCommentAdapter extends RefreshAdapter<DynamicCommentBean> implements RxRefreshView.DataAdapter<DynamicCommentBean> {

    private static final int PARENT = 1;
    private static final int CHILD = 2;
    private Drawable mLikeDrawable;
    private Drawable mUnLikeDrawable;
    private int mLikeColor;
    private int mUnLikeColor;
    private ScaleAnimation mLikeAnimation;
    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mLikeClickListener;
    private View.OnClickListener mExpandClickListener;
    private View.OnClickListener mCollapsedClickListener;
    private ActionListener mActionListener;
    private ImageView mCurLikeImageView;
    private int mCurLikeCommentPosition;
    private DynamicCommentBean mCurLikeCommentBean;
    private DefaultObserver<JSONObject> mLikeObsever;

    private DynamicBean mDynamicBean;


    private Drawable[] mLikeAnimDrawables;//点赞帧动画
    ValueFrameAnimator valueFrameAnimator;

    public DynamicCommentAdapter(Context context, final DynamicBean dynamicBean) {
        super(context);
        this.mDynamicBean=dynamicBean;
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(((DynamicCommentBean) tag), 0);
                }
            }
        };
        mLikeDrawable = ContextCompat.getDrawable(context, R.mipmap.zan_5);
        mUnLikeDrawable = ContextCompat.getDrawable(context, R.mipmap.zan_0);
        mLikeColor = ContextCompat.getColor(context, R.color.red);
        mUnLikeColor = ContextCompat.getColor(context, R.color.gray3);

        mLikeAnimDrawables= AnimHelper.createDrawableArray(context,AnimHelper.FOLLOW_ANIM_LIST);



        mLikeAnimation = new ScaleAnimation(1f, 0.5f, 1f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mLikeAnimation.setDuration(200);
        mLikeAnimation.setRepeatCount(1);
        mLikeAnimation.setRepeatMode(Animation.REVERSE);
        mLikeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                if (mCurLikeCommentBean != null) {
                    if (mCurLikeImageView != null) {
                        mCurLikeImageView.setImageDrawable(mCurLikeCommentBean.getIsLike() == 1 ? mLikeDrawable : mUnLikeDrawable);
                    }
                }
            }
        });
        mLikeObsever =new DefaultObserver<JSONObject>() {
            @Override
            public void onNext(JSONObject obj) {
                int like = obj.getIntValue("islike");
                String likeNum = obj.getString("likes");
                if (mCurLikeCommentBean != null) {
                    mCurLikeCommentBean.setIsLike(like);
                    mCurLikeCommentBean.setLikeNum(likeNum);
                    notifyItemChanged(mCurLikeCommentPosition, Constants.PAYLOAD);
                }
//                if (mCurLikeImageView != null && mLikeAnimation != null) {
//                    mCurLikeImageView.startAnimation(mLikeAnimation);
//                }
                valueFrameAnimator= ValueFrameAnimator.
                        ofFrameAnim(mLikeAnimDrawables)
                        .setSingleInterpolator(new OvershootInterpolator())
                        .durcation(600)
                        .anim(mCurLikeImageView);
                if(like == 1){
                    valueFrameAnimator.start();
                }else{
                    valueFrameAnimator.reverse();
                }
            }
        };

        mLikeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                DynamicCommentBean bean = (DynamicCommentBean) tag;
                String uid = bean.getUid();
                if (!TextUtils.isEmpty(uid) && uid.equals(CommonAppConfig.getInstance().getUid())) {
                    ToastUtil.show(R.string.comment_cannot_self);
                    return;
                }
                mCurLikeImageView = (ImageView) v;
                mCurLikeCommentPosition = bean.getPosition();
                mCurLikeCommentBean = bean;
                DynamicHttpUtil.addDynamicCommnetLike(mCurLikeCommentBean.getId(),mDynamicBean.getId()).subscribe(mLikeObsever);
            }
        };
        mExpandClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mActionListener != null) {
                    mActionListener.onExpandClicked((DynamicCommentBean) tag);
                }
            }
        };
        mCollapsedClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mActionListener != null) {
                    mActionListener.onCollapsedClicked((DynamicCommentBean) tag);
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (int i = 0, size = mList.size(); i < size; i++) {
            count++;
            List<DynamicCommentBean> childList = mList.get(i).getChildList();
            if (childList != null) {
                count += childList.size();
            }
        }
        return count;
    }

    private DynamicCommentBean getItem(int position) {
        int index = 0;
        for (int i = 0, size = mList.size(); i < size; i++) {
            DynamicCommentBean parentNode = mList.get(i);
            if (index == position) {
                return parentNode;
            }
            index++;
            List<DynamicCommentBean> childList = mList.get(i).getChildList();
            if (childList != null) {
                for (int j = 0, childSize = childList.size(); j < childSize; j++) {
                    if (position == index) {
                        return childList.get(j);
                    }
                    index++;
                }
            }
        }
        return null;
    }

    /**
     * 展开子评论
     *
     * @param childNode   在这个子评论下面插入子评论
     * @param insertCount 插入的子评论的个数
     */
    public void insertReplyList(DynamicCommentBean childNode, int insertCount) {
        //这种方式也能达到  notifyItemRangeInserted 的效果，而且不容易出问题
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(childNode.getPosition());
        }
        L.e("--childNode---"+childNode.getContent()+childNode.getParentNodeBean().getContent());
        //notifyItemRangeChanged(childNode.getPosition(), getItemCount(), Constants.PAYLOAD);
//        notifyItemRangeChanged(childNode.getPosition(), getItemCount(), null);
        notifyDataSetChanged();
    }

    /**
     * 收起子评论
     *
     * @param childNode   在这个子评论下面收起子评论
     * @param removeCount 被收起的子评论的个数
     */

    public void removeReplyList(DynamicCommentBean childNode, int removeCount) {
        //这种方式也能达到  notifyItemRangeRemoved 的效果，而且不容易出问题
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(childNode.getPosition());
        }
//        notifyItemRangeChanged(childNode.getPosition(), getItemCount(), null);
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        DynamicCommentBean bean = getItem(position);
        if (bean != null && bean.isParentNode()) {
            return PARENT;
        }
        return CHILD;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == PARENT) {
            return new ParentVh(mInflater.inflate(R.layout.item_video_comment_parent, parent, false));
        }
            return new ChildVh(mInflater.inflate(R.layout.item_video_comment_child, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        DynamicCommentBean bean = getItem(position);
        if (bean != null) {
            bean.setPosition(position);
            Object payload = payloads.size() > 0 ? payloads.get(0) : null;
            if (vh instanceof ParentVh) {
                ((ParentVh) vh).setData(bean, payload);
            } else if (vh instanceof ChildVh) {
                ((ChildVh) vh).setData(bean, payload);
            }
        }
    }
    @Override
    public void setData(List<DynamicCommentBean> data) {
        setList(data);
        notifyDataSetChanged();
    }

    @Override
    public void appendData(List<DynamicCommentBean> data) {
        if(getList()!=null&&data!=null){
            getList().addAll(data) ;
        }
         notifyDataSetChanged();
    }

    @Override
    public List<DynamicCommentBean> getArray() {
        return getList();
    }

    @Override
    public RecyclerView.Adapter returnRecyclerAdapter() {
        return this;
    }
    @Override
    public void notifyReclyDataChange() {
        notifyDataSetChanged();
    }
    class Vh extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mContent;
        TextView mTime;

        public Vh(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name);
            mContent = (TextView) itemView.findViewById(R.id.content);
            mTime= (TextView) itemView.findViewById(R.id.time);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(DynamicCommentBean bean, Object payload) {
            itemView.setTag(bean);
            if (payload == null) {
                UserBean u = bean.getUserBean();
                if (u != null) {
                 mName.setText(u.getUserNiceName());
                }
                if(mTime!=null){
                   mTime.setText(bean.getDatetime());
                }
                mContent.setText(CommentTextRender.renderDynamicComment(bean.getContent(),0,-1,""));


            }
        }
    }

    class ParentVh extends Vh {

        ImageView mBtnLike;
        TextView mLikeNum;
        ImageView mAvatar;
        View tv_author;

        public ParentVh(View itemView) {
            super(itemView);
            mBtnLike = (ImageView) itemView.findViewById(R.id.btn_like);
            mLikeNum = (TextView) itemView.findViewById(R.id.like_num);
            mBtnLike.setOnClickListener(mLikeClickListener);
            mAvatar = itemView.findViewById(R.id.avatar);
            tv_author = itemView.findViewById(R.id.tv_author);
        }

        void setData(DynamicCommentBean bean, Object payload) {
            super.setData(bean, payload);
            if(payload==null){
                UserBean u = bean.getUserBean();
                if(u!=null){
                    ImgLoader.displayAvatar(mContext,u.getAvatar(), mAvatar);
                }
            }
            mBtnLike.setTag(bean);
            boolean like = bean.getIsLike() == 1;
            mBtnLike.setImageDrawable(like ? mLikeDrawable : mUnLikeDrawable);
            mLikeNum.setText(bean.getLikeNum());
            mLikeNum.setTextColor(like ? mLikeColor : mUnLikeColor);
            if (mDynamicBean.getUid().equals(bean.getUid())){
                tv_author.setVisibility(View.VISIBLE);
            }else {
                if (tv_author.getVisibility() != View.VISIBLE){
                    tv_author.setVisibility(View.GONE);
                }
            }
        }
    }

    class ChildVh extends Vh {
        View mBtnGroup;
        View mBtnExpand;//展开按钮
        View mBtnbCollapsed;//收起按钮

        public ChildVh(View itemView) {
            super(itemView);
            mBtnGroup = itemView.findViewById(R.id.btn_group);
            mBtnExpand = itemView.findViewById(R.id.btn_expand);
            mBtnbCollapsed = itemView.findViewById(R.id.btn_collapsed);
            mBtnExpand.setOnClickListener(mExpandClickListener);
            mBtnbCollapsed.setOnClickListener(mCollapsedClickListener);
        }

        void setData(DynamicCommentBean bean, Object payload) {
            itemView.setTag(bean);
            if (payload == null) {
                UserBean u = bean.getUserBean();
                if (u != null&&!TextUtils.isEmpty(bean.getToUid())&&!bean.getToUid().equals("0")) {
                    mName.setText(u.getUserNiceName());
                }else if(u != null){
                    mName.setText(u.getUserNiceName()+" : ");
                }
                if(mTime!=null){
                    mTime.setText(bean.getDatetime());
                }
            }
            int startIndex=DynamicCommentBean.REPLY.length();
            int endIndex=!TextUtils.isEmpty(bean.getToUserName())?startIndex+bean.getToUserName().length():-1;
            mContent.setText(CommentTextRender.renderDynamicComment(bean.getContent(),startIndex,endIndex,""));
            mBtnExpand.setTag(bean);
            mBtnbCollapsed.setTag(bean);
            DynamicCommentBean parentNodeBean = bean.getParentNodeBean();
            if (bean.needShowExpand(parentNodeBean)) {
                if (mBtnGroup.getVisibility() != View.VISIBLE) {
                    mBtnGroup.setVisibility(View.VISIBLE);
                }
                if (mBtnbCollapsed.getVisibility() == View.VISIBLE) {
                    mBtnbCollapsed.setVisibility(View.INVISIBLE);
                }
                if (mBtnExpand.getVisibility() != View.VISIBLE) {
                    mBtnExpand.setVisibility(View.VISIBLE);
                }
            } else if (bean.needShowCollapsed(parentNodeBean)) {
                if (mBtnGroup.getVisibility() != View.VISIBLE) {
                    mBtnGroup.setVisibility(View.VISIBLE);
                }
                if (mBtnExpand.getVisibility() == View.VISIBLE) {
                    mBtnExpand.setVisibility(View.INVISIBLE);
                }
                if (mBtnbCollapsed.getVisibility() != View.VISIBLE) {
                    mBtnbCollapsed.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnGroup.getVisibility() == View.VISIBLE) {
                    mBtnGroup.setVisibility(View.GONE);
                }
            }
        }
    }

    public interface ActionListener {
        void onExpandClicked(DynamicCommentBean commentBean);

        void onCollapsedClicked(DynamicCommentBean commentBean);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

}
