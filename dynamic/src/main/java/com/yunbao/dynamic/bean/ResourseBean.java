package com.yunbao.dynamic.bean;

import android.text.TextUtils;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import java.util.ArrayList;
import java.util.List;

public class ResourseBean implements MultiItemEntity {
   private int itemType;
   private String resouce;
   private Object object;

    public ResourseBean(int itemType, String resouce,Object object) {
        this.itemType = itemType;
        this.resouce = resouce;
        this.object = object;
    }

    public ResourseBean(int itemType, String resouce) {
        this.itemType = itemType;
        this.resouce = resouce;
    }


    @Override
    public int getItemType() {
        return itemType;
    }
    public String getResouce() {
        return resouce;
    }
    public void setResouce(String resouce) {
        this.resouce = resouce;
    }
    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public static List<ResourseBean> transForm(int type,List<String>list){
        if(list==null) {
            return null;
        }
        List<ResourseBean>resourseList=new ArrayList<>();
        for(String str:list){
          resourseList.add(new ResourseBean(type,str)) ;
        }
        return resourseList;
    }

    public static List<ResourseBean> transForm(int type,String str){
        if(TextUtils.isEmpty(str)) {
            return null;
        }
        List<ResourseBean>resourseList=new ArrayList<>();
        resourseList.add(new ResourseBean(type,str));
        return resourseList;
    }


    public static ArrayList<String> valuesTo(List<ResourseBean>list){
         if(list==null) {
             return null;
         }
        ArrayList<String>stringList=new ArrayList<>();
        for(ResourseBean resourseBean:list){
            stringList.add(resourseBean.getResouce());
        }
        return stringList;
    }


    @Override
    public boolean equals( Object obj) {
        try {
            if(obj instanceof String){
                return resouce.equals(obj);
            }else{
                ResourseBean bean= (ResourseBean) obj;
                return bean.equals(resouce);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return super.equals(obj);
    }

    public Object getObject() {
        return object;
    }
    public void setObject(Object object) {
        this.object = object;
    }
}
