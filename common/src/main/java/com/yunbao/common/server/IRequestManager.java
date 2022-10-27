package com.yunbao.common.server;

import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.server.entity.BaseResponse;
import java.util.List;
import java.util.Map;
import io.reactivex.Observable;

/*create by cfw
* */

public interface IRequestManager {
    <T> Observable<List<T>> get(String url, Map<String, Object> map, Class<T> cs);
    <T> Observable<List<T>> getAndShowMsg(String url, Map<String, Object> map, Class<T> cs);
    <T> Observable<List<T>> post(String url, Map<String, Object> map, Class<T> cs);
    Observable<Boolean> noReturnPost(String url, Map<String, Object> map);
    public Observable<BaseResponse<JSONObject>> postNormal(String url, Map<String, Object> map);
    void cancle(String tag);
}