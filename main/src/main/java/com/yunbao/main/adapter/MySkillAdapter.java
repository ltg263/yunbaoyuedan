package com.yunbao.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.adapter.RefreshAdapter;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.bean.SkillClassBean;
import com.yunbao.main.bean.SkillMyBean;
import com.yunbao.main.http.MainHttpUtil;

import java.util.List;

/**
 * Created by cxf on 2019/7/26.
 */

public class MySkillAdapter extends RefreshAdapter<SkillMyBean> {

    private View.OnClickListener mRadioClickListener;
    private View.OnClickListener mAddClickListener;
    private View.OnClickListener mOnItemClickListener;
    private Drawable mCheckedDrawable;
    private Drawable mUnCheckedDrawable;
    private ActionListener mActionListener;
    private String mCoinName;
    private String mOrderOpen;
    private String mOrderClose;

    public MySkillAdapter(Context context) {
        super(context);
        mRadioClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                final int position = (int) tag;
                final SkillMyBean bean = mList.get(position);
                MainHttpUtil.setSkillOpen(bean.getSkillId(), bean.getIsOpen() != 1, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            bean.setIsOpen(bean.getIsOpen() == 1 ? 0 : 1);
                            notifyItemChanged(position, Constants.PAYLOAD);
                        }
                        ToastUtil.show(msg);
                    }
                });
            }
        };
        mAddClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionListener != null) {
                    mActionListener.onAddClick();
                }
            }
        };

        mOnItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mActionListener != null) {
                    mActionListener.onItemClick((SkillMyBean) tag);
                }
            }
        };
        mCheckedDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_btn_radio_1);
        mUnCheckedDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_btn_radio_0);
        mCoinName = CommonAppConfig.getInstance().getCoinName();
        mOrderOpen = WordUtil.getString(R.string.my_skill_order_open);
        mOrderClose = WordUtil.getString(R.string.my_skill_order_close);
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SkillMyBean.ADD) {
            return new AddVh(mInflater.inflate(R.layout.item_my_skill_add, parent, false));
        }
        return new Vh(mInflater.inflate(R.layout.item_my_skill, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        if (vh instanceof Vh) {
            Object payload = payloads.size() > 0 ? payloads.get(0) : null;
            ((Vh) vh).setData(mList.get(position), position, payload);
        }
    }

    class AddVh extends RecyclerView.ViewHolder {

        public AddVh(View itemView) {
            super(itemView);
            itemView.setOnClickListener(mAddClickListener);
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mThumb;
        TextView mName;
        TextView mStatus;
        TextView mPrice;
        ImageView mRadioBtn;

        public Vh(View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mName = itemView.findViewById(R.id.name);
            mStatus = itemView.findViewById(R.id.status);
            mPrice = itemView.findViewById(R.id.price);
            mRadioBtn = itemView.findViewById(R.id.btn_radio);
            mRadioBtn.setOnClickListener(mRadioClickListener);
            itemView.setOnClickListener(mOnItemClickListener);
        }

        void setData(SkillMyBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(bean);
                mRadioBtn.setTag(position);
            }
            SkillClassBean classBean = bean.getSkillClass();
            if (classBean != null) {
                ImgLoader.display(mContext, classBean.getThumb(), mThumb);
            }
            mName.setText(bean.getSkillName());
            mPrice.setText(bean.getPirceResult(mCoinName));
            if (bean.getIsOpen() == 1) {
                mRadioBtn.setImageDrawable(mCheckedDrawable);
                mStatus.setText(mOrderOpen);
            } else {
                mRadioBtn.setImageDrawable(mUnCheckedDrawable);
                mStatus.setText(mOrderClose);
            }
        }
    }

    public interface ActionListener {

        void onItemClick(SkillMyBean bean);

        void onAddClick();
    }
}
