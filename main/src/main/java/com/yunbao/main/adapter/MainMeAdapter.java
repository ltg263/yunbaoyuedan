package com.yunbao.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.common.Constants;
import com.yunbao.common.bean.UserItemBean;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/28.
 */

public class MainMeAdapter extends RecyclerView.Adapter {

    private static final int HEAD = -1;
    private static final int NORMAL = 0;
    private static final int TEXT = 2;

    private Context mContext;
    private List<UserItemBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private ActionListener mActionListener;
    private View mHeadView;
    private int mAuthNum;

    public MainMeAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mHeadView = mInflater.inflate(R.layout.item_main_me_head, null);
        mHeadView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(190)));
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                UserItemBean bean = mList.get(position - 1);
                if (mActionListener != null) {
                    mActionListener.onItemClick(bean);
                }
            }
        };
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD;
        }
        UserItemBean bean = mList.get(position - 1);
        int id = bean.getId();
        if (id == Constants.MAIN_ME_MY_SKILL) {
            return TEXT;
        }
        return NORMAL;
    }


    public void setAuthNum(int authNum){
        mAuthNum=authNum;
    }


    public void setList(List<UserItemBean> list) {
        if (list == null) {
            return;
        }
        boolean changed = false;
        if (mList.size() != list.size()) {
            changed = true;
        } else {
            for (int i = 0, size = mList.size(); i < size; i++) {
                if (!mList.get(i).equals(list.get(i))) {
                    changed = true;
                    break;
                }
            }
        }
        if (changed) {
            mList = list;
            notifyDataSetChanged();
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            ViewParent viewParent = mHeadView.getParent();
            if (viewParent != null) {
                ((ViewGroup) viewParent).removeView(mHeadView);
            }
            HeadVh headVh = new HeadVh(mHeadView);
            headVh.setIsRecyclable(false);
            return headVh;
        } else if (viewType == TEXT) {
            return new TextVh(mInflater.inflate(R.layout.item_main_me_1, parent, false));
        }
        return new Vh(mInflater.inflate(R.layout.item_main_me_0, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position - 1), position, payload);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    class HeadVh extends RecyclerView.ViewHolder {
        public HeadVh(View itemView) {
            super(itemView);
        }
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

    class TextVh extends Vh {

        TextView mText;

        public TextVh(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.text);
        }
        @Override
        void setData(UserItemBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
               mText.setText(WordUtil.getString(R.string.skill_order_num,mAuthNum));
            }
        }
    }


    public View getHeadView() {
        return mHeadView;
    }


    public interface ActionListener {
        void onItemClick(UserItemBean bean);
    }


}
