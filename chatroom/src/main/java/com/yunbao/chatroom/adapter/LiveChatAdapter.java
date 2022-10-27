package com.yunbao.chatroom.adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;


import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.adapter.base.BaseMutiRecyclerAdapter;
import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.LiveChatBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.chatroom.R;

import java.util.List;

public class LiveChatAdapter extends BaseMutiRecyclerAdapter<LiveChatBean, BaseReclyViewHolder> {
    private String mLiveUid;

    public LiveChatAdapter(List<LiveChatBean> data, String liveUid) {
        super(data);
        mLiveUid = liveUid;
        addItemType(LiveChatBean.NORMAL, R.layout.item_recly_live_chat_normal);
        addItemType(LiveChatBean.SYSTEM, R.layout.item_recly_live_chat_sysytem);
        addItemType(LiveChatBean.GIFT, R.layout.item_recly_live_chat_gift);
        addItemType(LiveChatBean.FRIEND, R.layout.item_recly_live_chat_gift);
        addItemType(LiveChatBean.ENTER_ROOM, R.layout.item_recly_live_chat_enter_room);
        addItemType(LiveChatBean.BOSS_PLACE_ORDER, R.layout.item_recly_live_chat_boss_place_order);
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, LiveChatBean item) {
        switch (helper.getItemViewType()) {
            case LiveChatBean.NORMAL:
                convertNormal(helper, item);
                break;
            case LiveChatBean.SYSTEM:
                convertSystem(helper, item);
                break;
            case LiveChatBean.GIFT:
                convertGift(helper, item);
                break;
            case LiveChatBean.FRIEND:
                convertFriend(helper, item);
                break;
            case LiveChatBean.ENTER_ROOM:
                convertEnterRoom(helper, item);
                break;
            case LiveChatBean.BOSS_PLACE_ORDER:
                convertBossOrder(helper, item);
                break;
            default:
                break;
        }
    }

    private void convertFriend(BaseReclyViewHolder helper, LiveChatBean item) {
        helper.setText(R.id.tv_content, spanner(item));
    }

    private void convertGift(BaseReclyViewHolder helper, LiveChatBean item) {
        helper.setText(R.id.tv_content, spanner(item));
    }


    private CharSequence spanner(LiveChatBean item) {
        if (TextUtils.isEmpty(item.getUserNiceName()) || TextUtils.isEmpty(item.getToUserNiceName())) {
            return item.getContent();
        }
        String content = item.getContent();
        SpannableString spanString = new SpannableString(content);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.WHITE);
        ForegroundColorSpan span2 = new ForegroundColorSpan(Color.WHITE);
        spanString.setSpan(span, 0, item.getUserNiceName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int index = content.lastIndexOf(item.getToUserNiceName());
        spanString.setSpan(span2, index, index + item.getToUserNiceName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    private void convertSystem(BaseReclyViewHolder helper, LiveChatBean item) {
        helper.setText(R.id.tv_content, item.getContent());
    }

    private void convertBossOrder(BaseReclyViewHolder helper, LiveChatBean item) {
        helper.setText(R.id.tv_content, item.getContent());
    }

    private void convertEnterRoom(BaseReclyViewHolder helper, LiveChatBean item) {
        helper.setText(R.id.tv_content, item.getContent());
    }

    private void convertNormal(BaseReclyViewHolder helper, LiveChatBean item) {
        helper.setText(R.id.age, item.getAge());
        View sexGroup = helper.getView(R.id.sex_group);
        int sex = item.getSex();
        helper.setImageDrawable(R.id.sex, CommonIconUtil.getSexDrawable(sex));
        sexGroup.setBackground(CommonIconUtil.getSexBgDrawable(sex));
        helper.setText(R.id.tv_content, item.getContent());
        ImageView iv_anchor_level = helper.getView(R.id.iv_anchor_level);
        ImageView iv_level = helper.getView(R.id.iv_level);
        if (item.getId().equals(mLiveUid)) {
            iv_anchor_level.setVisibility(View.VISIBLE);
            LevelBean anchorBean = CommonAppConfig.getInstance().getAnchorLevel(item.getAnchorLevel());
            ImgLoader.display(mContext, anchorBean.getThumb(), iv_anchor_level);
        } else {
            if (iv_anchor_level.getVisibility() == View.VISIBLE) {
                iv_anchor_level.setVisibility(View.GONE);
            }
        }
        LevelBean levelBean = CommonAppConfig.getInstance().getLevel(item.getLevel());
        ImgLoader.display(mContext, levelBean.getThumb(), iv_level);
        helper.setText(R.id.tv_name, item.getUserNiceName());
    }
}
