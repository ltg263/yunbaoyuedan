package com.yunbao.dynamic.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.SkillBean;
import com.yunbao.common.business.liveobsever.LifeVoiceMediaHelper;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.custom.ValueFrameAnimator;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.dialog.BottomDealFragment;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.server.observer.DefaultObserver;
import com.yunbao.common.server.observer.DialogObserver;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.dynamic.R;
import com.yunbao.dynamic.bean.DynamicBean;
import com.yunbao.dynamic.bean.MyDynamicBean;
import com.yunbao.dynamic.business.AnimHelper;
import com.yunbao.dynamic.event.DynamicLikeEvent;
import com.yunbao.dynamic.http.DynamicHttpUtil;
import com.yunbao.dynamic.ui.activity.DynamicReportActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MyDynamicAdapter extends BaseRecyclerAdapter<MyDynamicBean, BaseReclyViewHolder> {
    private Activity mActivity;
    private Drawable[] mLikeAnimDrawables;//点赞帧动画
    private ValueFrameAnimator mValueFrameAnimator;
    private LifeVoiceMediaHelper mMediaHelper;
    private int mLastSoundPlayPosition;

    public MyDynamicAdapter(List<MyDynamicBean> data, AppCompatActivity activity) {
        super(data);
        this.mActivity = activity;
        mMediaHelper = LifeVoiceMediaHelper.getByContext(activity);
        mMediaHelper.addSingleSoundLisnter(new LifeVoiceMediaHelper.SingleSoundListner() {
            @Override
            public void single(int hashCode) {
                int size = size();
                if (size == 0) {
                    return;
                }
                for (int i = 0; i < size; i++) {
                    MyDynamicBean bean = getItem(i);
                    if (bean.getType() == Constants.DYNAMIC_VOICE) {
                        DynamicResourceAdapter resourceAdapter = bean.getResourceAdapter();
                        if (resourceAdapter != null && resourceAdapter.contain(hashCode)) {
                            mLastSoundPlayPosition = i;
                            break;
                        }
                    }
                }
            }
        });

        mLikeAnimDrawables = AnimHelper.createDrawableArray(activity, AnimHelper.FOLLOW_ANIM_LIST);
        mValueFrameAnimator = ValueFrameAnimator.
                ofFrameAnim(mLikeAnimDrawables)
                .setSingleInterpolator(new OvershootInterpolator())
                .durcation(500);
        setOnItemChildClickListener(onItemChildClickListener);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_my_dynamic;
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, MyDynamicBean item) {
        helper.setImageUrl(item.getAvatar(), R.id.img_avator);
        helper.setText(R.id.tv_name, item.getUser_nickname());

        SkillBean skillBean = item.getSkillinfo();
        if (skillBean != null && !TextUtils.isEmpty(skillBean.getSkillName2())&&CommonAppConfig.getInstance().getIsState()!=1) {
            helper.setVisible(R.id.ll_skill, true);
            helper.setText(R.id.tv_skill_name, skillBean.getSkillName2());
            helper.setText(R.id.tv_skill_price, skillBean.getPirceResult());
        } else {
            helper.setVisible(R.id.ll_skill, false);
        }
        helper.setText(R.id.tv_title, item.getContent());

        String location = item.getLocation();
        TextView tvLocation = helper.getView(R.id.dt_location);
        if (!TextUtils.isEmpty(location)) {
            tvLocation.setText(item.getLocation());
            tvLocation.setVisibility(View.VISIBLE);
        } else {
            tvLocation.setVisibility(View.GONE);
        }
        if (item.getIslike() == 1) {
            helper.setImageDrawable(R.id.img_zan, mLikeAnimDrawables[mLikeAnimDrawables.length - 1]);
        } else {
            helper.setImageDrawable(R.id.img_zan, mLikeAnimDrawables[0]);
        }
        helper.setText(R.id.dt_comment, item.getComments() + "");
        helper.setText(R.id.tv_zan, item.getLikes() + "");
        helper.setImageDrawable(R.id.sex, CommonIconUtil.getSexDrawable(item.getSex()));

        ImageView iv_anchor_level = helper.getView(R.id.iv_anchor_level);
        ImageView iv_level = helper.getView(R.id.iv_level);
        if (item.showAnchorLevel()){
            iv_anchor_level.setVisibility(View.VISIBLE);
            LevelBean anchorBean = CommonAppConfig.getInstance().getAnchorLevel(item.getLevel_anchor());
            ImgLoader.display(mContext,anchorBean.getThumb(),iv_anchor_level);
        }else {
            if (iv_anchor_level.getVisibility() == View.VISIBLE){
                iv_anchor_level.setVisibility(View.GONE);
            }
        }
        LevelBean levelBean = CommonAppConfig.getInstance().getLevel(item.getLevel());
        ImgLoader.display(mContext,levelBean.getThumb(),iv_level);

        View view = helper.getView(R.id.ll_sex_group);
        view.setBackground(CommonIconUtil.getSexBgDrawable(item.getSex()));

        helper.setText(R.id.age, item.getAge() + "");
        helper.setText(R.id.tv_time_addr, item.getAddrAndTime());
        RecyclerView recyclerView = helper.getView(R.id.reclyView);
        DynamicResourceAdapter adapter = (DynamicResourceAdapter) recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new DynamicResourceAdapter(item.getResourseBeanArray(), mActivity);
            adapter.setDynamicBean(item);
            initReclyView(recyclerView, adapter);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setDynamicBean(item);
            adapter.setData(item.getResourseBeanArray());
        }
        item.setResourceAdapter(adapter);
        helper.addOnClickListener(R.id.vp_zan);
        helper.addOnClickListener(R.id.btn_more);
    }

    private void initReclyView(RecyclerView recyclerView, DynamicResourceAdapter dynamicResourceAdapter) {
        /*嵌套adapter的控件冲突解决*/
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View vParent = (View) v.getParent();
                return vParent.onTouchEvent(event);
            }
        });
        ItemDecoration decoration = new ItemDecoration(mActivity, 0xffdd00, 5, 5);
        recyclerView.setLayoutManager(dynamicResourceAdapter.createDefaultGridMannger());
        recyclerView.addItemDecoration(decoration);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(1);
    }

    private OnItemChildClickListener onItemChildClickListener = new OnItemChildClickListener() {
        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            int id = view.getId();
            if (id == R.id.btn_more) {
                showBottomDialog(position);
            } else if (id == R.id.vp_zan) {
                DynamicBean dynamicBean = mData.get(position);
                dynamicAddLike(view, dynamicBean);
            }
        }
    };

    //显示举报/删除弹窗
    private void showBottomDialog(final int position) {
        final DynamicBean dynamicBean = mData.get(position);
        boolean isSelf = false;//是否为自己发布的动态
        if (dynamicBean.getUid().equals(CommonAppConfig.getInstance().getUid())) {
            isSelf = true;
        }
        String btnString = isSelf ? WordUtil.getString(R.string.delete) : WordUtil.getString(R.string.report);
        BottomDealFragment bottomDealFragment = new BottomDealFragment();
        final boolean finalIsSelf = isSelf;
        bottomDealFragment.setDialogButtonArray(new BottomDealFragment.DialogButton(btnString, new BottomDealFragment.ClickListnter() {
            @Override
            public void click(View view) {
                if (finalIsSelf) {
                    deleteDynamic(dynamicBean, position);
                } else {
                    report(dynamicBean);
                }
            }
        }));
        bottomDealFragment.show(((AbsActivity) mContext).getSupportFragmentManager());
    }

    /*删除动态*/
    private void deleteDynamic(DynamicBean dynamicBean, final int position) {
        if (dynamicBean != null) {
            DynamicHttpUtil.delDynamic(dynamicBean.getId()).subscribe(new DialogObserver<Boolean>(mContext) {
                @Override
                public void onNext(Boolean aBoolean) {
                    if (aBoolean) {
                        if (mData.get(position) != null) {
                            mData.remove(position);
                        }
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }
            });
        }
    }

    private void report(DynamicBean dynamicBean) {
        if (dynamicBean != null) {
            DynamicReportActivity.forward(mContext, dynamicBean.getId());
        }
    }

    private void dynamicAddLike(final View view, final DynamicBean dynamicBean) {
        DynamicHttpUtil.dynamicAddLike(dynamicBean.getId()).subscribe(new DefaultObserver<Integer>() {
            @Override
            public void onNext(Integer isLike) {
                addLikeSuccess(view, dynamicBean, isLike);
            }
        });
    }

    private void toggleFollow(View view, boolean isLike) {
        mValueFrameAnimator.anim((ImageView) view);
        if (isLike) {
            mValueFrameAnimator.start();
        } else {
            mValueFrameAnimator.reverse();
        }
    }

    /*点赞成功后*/
    private void addLikeSuccess(View view, DynamicBean dynamicBean, int isLike) {
        toggleFollow(view.findViewById(R.id.img_zan), isLike == 1);
        dynamicBean.setIslike(isLike);
        if (isLike == 1) {
            dynamicBean.setLikes(dynamicBean.getLikes() + 1);
        } else {
            dynamicBean.setLikes(dynamicBean.getLikes() - 1);
        }
        View itemView = (View) view.getParent();
        TextView tvLikeNum = itemView.findViewById(R.id.tv_zan);
        if (tvLikeNum != null) {
            tvLikeNum.setText(dynamicBean.getLikes() + "");
        }
        DynamicLikeEvent dynamicLikeEvent = new DynamicLikeEvent(isLike, dynamicBean.getLikes(), dynamicBean.getId());
        dynamicLikeEvent.setTag(MyDynamicAdapter.class.getName());
        EventBus.getDefault().post(dynamicLikeEvent);
    }

    public void visibleRange(int startPositon, int endPosition) {
        if (startPositon > mLastSoundPlayPosition || mLastSoundPlayPosition > endPosition) {
            mMediaHelper.watchPlay(LifeVoiceMediaHelper.ALL_CANCLE_CODE);
        }
    }

    public void release() {
        if (mValueFrameAnimator != null) {
            mValueFrameAnimator.release();
        }
    }
}
