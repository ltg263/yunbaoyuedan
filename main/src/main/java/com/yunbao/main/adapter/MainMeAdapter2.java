package com.yunbao.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.bean.UserItemBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.main.R;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.util.ScreenUtils;

/**
 *
 * @author cxf
 * @date 2018/9/28
 */

public class MainMeAdapter2 extends RecyclerView.Adapter {

    private Context mContext;
    private List<UserItemBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private ActionListener mActionListener;
    private int width;
    private int mTag;

    public MainMeAdapter2(Context context) {
        mContext = context;
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                UserItemBean bean = mList.get(position);
                if (mActionListener != null) {
                    mActionListener.onItemClick(bean);
                }
            }
        };
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }


    public void refreshList(List<UserItemBean> list,int tag) {
        mList.clear();
        if (list != null && list.size() > 0) {
            mList.addAll(list);
        }
        mTag=tag;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mTag==2){
            return new Vh(mInflater.inflate(R.layout.item_main_me_wallet, parent, false));
        }
        return new Vh(mInflater.inflate(R.layout.item_main_me, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position), position, payload);
        }
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

        void setData(UserItemBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                ImgLoader.display(mContext, bean.getThumb(), mThumb);
                mName.setText(bean.getName());
            }
        }
    }


    public interface ActionListener {
        void onItemClick(UserItemBean bean);
    }


}
