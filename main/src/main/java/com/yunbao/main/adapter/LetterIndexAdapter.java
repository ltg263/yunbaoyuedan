package com.yunbao.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.main.R;

/**
 * Created by Sky.L on 2019/9/27
 */
public class LetterIndexAdapter extends RecyclerView.Adapter<LetterIndexAdapter.Vh> {
    private Context mContext;
    private String[] mLetterArray;
    private LayoutInflater mInflater;
    private OnItemClickListener<String> mOnItemClickListener;


    public LetterIndexAdapter(Context context) {
        mContext = context;
        mLetterArray = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        mInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener<String> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_new_contact_letter_index,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        ((Vh)holder).setData(mLetterArray[position],position);
    }

    @Override
    public int getItemCount() {
        return mLetterArray.length;
    }

    public class Vh extends RecyclerView.ViewHolder {
        TextView mTextView;
        String mStringLetter;
        int mPosition;

        public Vh(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null){
                        mOnItemClickListener.onItemClick(mStringLetter,mPosition);
                    }
                }
            });
        }

        void setData(String letter, int position){
            mPosition = position;
            mStringLetter = letter;
            mTextView.setText(letter);
        }
    }
}
