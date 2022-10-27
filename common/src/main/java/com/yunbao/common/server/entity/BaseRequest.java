package com.yunbao.common.server.entity;


import android.util.ArrayMap;


import com.yunbao.common.utils.L;

import java.io.File;


public class BaseRequest{

    public static final int COVERAGE=0;
    public static final int APPEND=1;


    ArrayMap<String, Object> mMap;

   public BaseRequest(){
       mMap=new ArrayMap<>();
   }

   public BaseRequest addArgs(String key, Object value){
       mMap.put(key,value);
       return this;
   }

   public String getStringArgs(String key){
       Object object=mMap.get(key);
       if(object==null){
           L.e("请求参数为NULL");
           return "";
       }
       try {
          return object.toString();
       }catch (Exception e){
           e.printStackTrace();
       }
       return "";
   }




    public Integer getIntegerArgs(String key){
        Object object=mMap.get(key);
        if(object==null){
            L.e("请求参数为NULL");
            return 0;
        }
        try {
            return (Integer) object;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public Boolean getBoolean(String key){
        Object object=mMap.get(key);
        if(object==null){
            L.e("请求参数为NULL");
            return false;
        }
        try {
            return (Boolean) object;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void addBoolean(String key, boolean arg){
        mMap.put(key,arg);
    }

    public File getFileArgs(String key){
        try {
            File file= (File) mMap.get(key);
            return file;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static BaseRequest createCoverage(){
       return new BaseRequest().addArgs("type",COVERAGE);
    }

    public static BaseRequest createAppend(){
        return new BaseRequest().addArgs("type",APPEND);
    }

    public int getType(){
     return   getIntegerArgs("type");
    }


}
