package com.yunbao.common.adapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.custom.refresh.RxRefreshView;
import com.yunbao.common.utils.ListUtil;

import java.util.List;

public abstract class BaseRecyclerAdapter<T,E extends BaseReclyViewHolder> extends BaseQuickAdapter<T,E> implements RxRefreshView.DataAdapter<T> {
    public Context context;
    private DataChangeListner<T> dataChangeListner;


    public BaseRecyclerAdapter(List<T> data) {
        super(data);
        setLayoutId();
    }

    @Override
    public E onCreateViewHolder(ViewGroup parent, int viewType) {
        E e=super.onCreateViewHolder(parent, viewType);
        bindContext(parent.getContext());
        return e;
    }
    public void bindContext(Context context) {
    this.context=context;
    }

    @Override
    public void setData(List<T> data) {
        if(dataChangeListner!=null){
            dataChangeListner.change(data);
        }
        mData = data;
        notifyDataSetChanged();
    }
    public void setLayoutId() {
        mLayoutResId = getLayoutId();
    }

    public abstract int getLayoutId();

    @Override
    public void appendData(List<T> data) {
        if(data!=null){
            addData(data);
        }else{
            setData(data);
        }
    }

    public T getLastData(){
       return ListUtil.haveData(mData) ?mData.get(mData.size()-1):null;
    }

    @Override
    public int getItemCount() {
        return mData==null?0:super.getItemCount();
    }

    public int size(){
        return mData==null?0:mData.size();
    }
    @Override
    public void notifyReclyDataChange() {
        notifyDataSetChanged();
    }
    @Override
    public List<T> getArray() {
        return mData;
    }

    @Override
    public RecyclerView.Adapter returnRecyclerAdapter() {
        return this;
    }

    /*监听数据源的内存地址变化*/
    public void setDataChangeListner(DataChangeListner<T> dataChangeListner) {
        this.dataChangeListner = dataChangeListner;
    }


    public interface DataChangeListner<T>{
        public void change(List<T>t);
    }
}
