package com.yunbao.common.adapter.base;

import android.support.v7.widget.RecyclerView;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.yunbao.common.custom.refresh.RxRefreshView;
import java.util.List;

public abstract class BaseMutiRecyclerAdapter<T extends MultiItemEntity, K  extends BaseViewHolder > extends BaseMultiItemQuickAdapter<T, K> implements RxRefreshView.DataAdapter<T> {
    public BaseMutiRecyclerAdapter(List<T> data) {
        super(data);
    }

    @Override
    public void setData(List<T> data) {
        setNewData(data);
        notifyDataSetChanged();
    }


    @Override
    public void appendData(List<T> data) {
        if(data!=null){
            addData(data);
        }else{
            setData(data);
        }
    }


    public int size(){
        return mData==null?0:mData.size();
    }

    @Override
    public List<T> getArray() {
        return mData;
    }

    @Override
    public RecyclerView.Adapter returnRecyclerAdapter() {
        return this;
    }

    @Override
    public void notifyReclyDataChange() {
        notifyDataSetChanged();
    }
}