package com.yunbao.chatroom.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import com.chad.library.adapter.base.BaseQuickAdapter;

import com.yunbao.common.adapter.base.BaseReclyViewHolder;
import com.yunbao.common.adapter.base.BaseRecyclerAdapter;
import com.yunbao.common.utils.ListUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.chatroom.R;
import com.yunbao.chatroom.bean.LiveSetInfo;

import java.util.List;


public class LiveBgAdapter extends BaseRecyclerAdapter<LiveSetInfo.LiveBgBean,BaseReclyViewHolder> {
    private Checkable selectCheckAble;
    private int selectPosition=-1;
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;
    private String mCheckedString;
    private String mUnCheckedString;
    private onSelectListner mOnSelectListner;

    public LiveBgAdapter(List data) {
        super(data);

        mCheckedString= WordUtil.getString(R.string.selected);
        mUnCheckedString= WordUtil.getString(R.string.use);
        mOnCheckedChangeListener=new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String checkString=isChecked?mCheckedString:mUnCheckedString;
                buttonView.setText(checkString);
            }
        };
        setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Checkable checkable= (Checkable) view;
                setCheck(checkable,position);
            }
        });
    }

    private void setCheck(Checkable checkable, int position) {
        if(selectCheckAble!=null&&selectCheckAble!=checkable){
            selectCheckAble.setChecked(false);
        }
        selectCheckAble=checkable;
        checkable.setChecked(true);
        selectPosition=position;
        if(mOnSelectListner!=null){
           mOnSelectListner.select(ListUtil.safeGetData(mData,selectPosition));
        }
    }

    public LiveSetInfo.LiveBgBean getSelectData(){
        if(size()==0||selectPosition==-1) {
            return null;
        }
        return mData.get(selectPosition);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_bg;
    }

    public void setOnSelectListner(onSelectListner onSelectListner) {
        mOnSelectListner = onSelectListner;
    }

    public int select(String id) {
       if(!ListUtil.haveData(mData)|| TextUtils.isEmpty(id)){
           return -1;
       }
       int index=-1;
       int size=mData.size();
       for(int i=0;i<size;i++){
           String idTemp=mData.get(i).getId();
           if(StringUtil.equals(idTemp,id)){
               index=i;
               break;
           }
       }
        if(index>-1&&index<mData.size()){
            selectPosition=index;
            notifyItemChanged(selectPosition);
        }
        return selectPosition;
    }

    public interface  onSelectListner{
        public void select(LiveSetInfo.LiveBgBean bgBean);
    }
    @Override
    public void setData(List<LiveSetInfo.LiveBgBean> data) {

        super.setData(data);
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, LiveSetInfo.LiveBgBean bgBean) {
        helper.addOnClickListener(R.id.chebox);
        helper.setOnCheckedChangeListener(R.id.chebox,mOnCheckedChangeListener);
        helper.setImageUrl(bgBean.getThumb(),R.id.image);
        Checkable checkable=helper.getView(R.id.chebox);
        int position=helper.getLayoutPosition();
        if(selectPosition==position&&(selectCheckAble==null||selectCheckAble!=checkable)){
            setCheck(checkable,position);
        }
    }


}
