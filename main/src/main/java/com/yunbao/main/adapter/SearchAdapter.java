package com.yunbao.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.custom.ItemDecoration;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.CommonIconUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.SkillClassBean;

import java.util.List;

/**
 * Created by cxf on 2018/9/29.
 */

public class SearchAdapter extends RefreshAdapter<UserBean> implements OnItemClickListener<SkillClassBean> {

    private List<SkillClassBean> mGameList;
    private boolean mShowHead;
    private static final int HEAD = -1;
    private View.OnClickListener mClickListener;
    private ActionListener mActionListener;
    private String mIdString;

    public SearchAdapter(Context context) {
        super(context);
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                UserBean bean = (UserBean) tag;
                if (mActionListener != null) {
                    mActionListener.onItemClick(bean);
                }
            }
        };
        mIdString = WordUtil.getString(R.string.search_id);
    }


    public void setGameList(List<SkillClassBean> gameList) {
        mGameList = gameList;
    }

    public void setShowHead(boolean showHead) {
        mShowHead = showHead;
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
            HeadVh headVh = new HeadVh(mInflater.inflate(R.layout.item_search_head, parent, false));
            headVh.setIsRecyclable(false);
            return headVh;
        }
        return new Vh(mInflater.inflate(R.layout.item_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof HeadVh) {
            ((HeadVh) vh).setData();
        } else {
            ((Vh) vh).setData(mList.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    public void clearData2(){
        mShowHead = false;
        if (mGameList != null) {
            mGameList.clear();
        }
        clearData();
    }


    @Override
    public void onItemClick(SkillClassBean bean, int position) {
        if (mActionListener != null) {
            mActionListener.onGameClick(bean);
        }
    }

    class HeadVh extends RecyclerView.ViewHolder {

        View mContainer;
        View mGameGroup;
        RecyclerView mRecyclerView;
        SearchGameAdapter mGameAdapter;

        public HeadVh(View itemView) {
            super(itemView);
            mContainer = itemView.findViewById(R.id.container);
            mGameGroup = itemView.findViewById(R.id.game_group);
            mRecyclerView = itemView.findViewById(R.id.recyclerView_game);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 10, 0);
            decoration.setOnlySetItemOffsetsButNoDraw(true);
            mRecyclerView.addItemDecoration(decoration);
            mGameAdapter = new SearchGameAdapter(mContext);
            mGameAdapter.setOnItemClickListener(SearchAdapter.this);
            mRecyclerView.setAdapter(mGameAdapter);
        }

        void setData() {
            if (mShowHead) {
                if (mContainer.getVisibility() != View.VISIBLE) {
                    mContainer.setVisibility(View.VISIBLE);
                }
                if (mGameList == null || mGameList.size() == 0) {
                    if (mGameGroup.getVisibility() == View.VISIBLE) {
                        mGameGroup.setVisibility(View.GONE);
                    }
                } else {
                    if (mGameGroup.getVisibility() != View.VISIBLE) {
                        mGameGroup.setVisibility(View.VISIBLE);
                    }
                    mGameAdapter.refreshData(mGameList);
                }
            } else {
                if (mContainer.getVisibility() != View.GONE) {
                    mContainer.setVisibility(View.GONE);
                }
            }
        }
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        View mSexGroup;
        ImageView mSex;
        TextView mAge;
        TextView mID;


        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            mSexGroup = itemView.findViewById(R.id.sex_group);
            mSex = itemView.findViewById(R.id.sex);
            mAge = itemView.findViewById(R.id.age);
            mID = itemView.findViewById(R.id.id_val);
            itemView.setOnClickListener(mClickListener);
        }

        void setData(UserBean bean) {
            itemView.setTag(bean);
            ImgLoader.displayAvatar(mContext, bean.getAvatar(), mAvatar);
            mName.setText(bean.getUserNiceName());
            mSexGroup.setBackground(CommonIconUtil.getSexBgDrawable(bean.getSex()));
            mSex.setImageDrawable(CommonIconUtil.getSexDrawable(bean.getSex()));
            mAge.setText(bean.getAge());
            mID.setText(StringUtil.contact(mIdString, bean.getId()));
        }
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onGameClick(SkillClassBean bean);

        void onItemClick(UserBean bean);
    }
}
