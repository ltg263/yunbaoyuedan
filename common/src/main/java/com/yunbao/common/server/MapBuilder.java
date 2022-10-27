package com.yunbao.common.server;

import android.util.ArrayMap;

import com.yunbao.common.utils.L;

import java.util.Map;

public class MapBuilder {
private ArrayMap<String,Object> map;
public MapBuilder(){
    map=new ArrayMap<>();
}

 public MapBuilder put(String key,Object value){
    if(value!=null){
        map.put(key,value);
    }else{
        map.put(key,"");
    }
    return this;
 }

 public Map<String,Object> build(){
    return map;
 }

 public static MapBuilder factory(){
    return new MapBuilder();
 }

}
