package com.yunbao.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.SkillClassBean;

import java.util.List;


/**
 * Created by cxf on 2019/7/19.
 */

public class MainHomeClassAdapter extends RecyclerView.Adapter<MainHomeClassAdapter.Vh> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<SkillClassBean> mList;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<SkillClassBean> mOnItemClickListener;

    public MainHomeClassAdapter(Context context, List<SkillClassBean> list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList = list;
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickUtil.canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick((SkillClassBean) tag, 0);
                }
            }
        };
    }


    public void setList(List<SkillClassBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<SkillClassBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_main_home_class, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mThumb;
        TextView mName;

        public Vh(View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(SkillClassBean bean) {
            itemView.setTag(bean);
            if (bean.isMore()) {
                mName.setText(WordUtil.getString(R.string.more));
                mThumb.setImageResource(R.mipmap.icon_game_more);
            } else {
                mName.setText(bean.getName());
                ImgLoader.display(mContext, bean.getThumb(), mThumb);
            }
        }
    }
}
