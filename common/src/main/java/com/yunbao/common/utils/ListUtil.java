package com.yunbao.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ListUtil {

  public static boolean haveData(Collection list){
      return list!=null&&list.size()>0;
  }

    public static <T> T safeGetData(List<T>list,int position){
       if(!haveData(list)||list.size()<=position||position==-1) {
           return null;
       }
        return list.get(position);
    }

    /*需要重写相关类的toString()方法*/
    public static String listToSingleString(List list){
       if (haveData(list)){
        StringBuilder builder=new StringBuilder();
        for(Object object:list){
            builder.append(object.toString())
            .append(",");
        }
           builder.deleteCharAt(builder.length()-1);
         return builder.toString();
       }
        return null;
    }

    public static <T,E>  ArrayList<E>transForm(List<T>list,Class<E>cs,TransFormListner<T,E>teTransFormListner){
        if(haveData(list)&&teTransFormListner!=null){
            ArrayList<E>newList=new ArrayList<>();
            for(T t:list){
                newList.add(teTransFormListner.transform(t));
            }
            return newList;
        }
        return null;
    }

    public static <T,E>  ArrayList<E>transForm(T[]array,Class<E>cs,TransFormListner<T,E>teTransFormListner){
        if(array!=null&&teTransFormListner!=null){
            ArrayList<E>newList=new ArrayList<>();
            for(T t:array){
                newList.add(teTransFormListner.transform(t));
            }
            return newList;
        }
        return null;
    }

    public static <T> List<T>asList(T...array){
        if(array==null){
           return null;
        }
      return Arrays.asList(array);
    }



    public interface TransFormListner<T,E>{
        public E transform(T t);
    }
}
