package com.yunbao.common.business.liveobsever;

import android.support.annotation.NonNull;
import java.util.LinkedHashSet;
import java.util.Set;

public class DataObsever<T>{
    private Set<DataChangeListner<T>> observerList;

    public void addObsever(@NonNull DataChangeListner<T> dataChangeListner){
        if(observerList==null){
           observerList=new LinkedHashSet<>(1);
        }
        if(dataChangeListner==null){
            return;
        }
        observerList.add(dataChangeListner);
    }
    public void observer(T t)
    {if(observerList==null){
       return;
    }
       for(DataChangeListner<T> listner:observerList){
           listner.change(t);
       }
    }

    public void removeObsever(DataChangeListner dataChangeListner){
        if(observerList==null||dataChangeListner==null){
            return;
        }
        observerList.remove(dataChangeListner);
    }

    public void release(){
        observerList=null;
    }

}
