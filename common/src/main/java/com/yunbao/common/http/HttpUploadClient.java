package com.yunbao.common.http;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.utils.LanguageUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by cxf on 2018/9/17.
 */

public class HttpUploadClient {

    public static final String SALT = "400d069a791d51ada8af3e6c2979bcd7";
    private static final int TIMEOUT = 50000;
    private static HttpUploadClient sInstance;
    private OkHttpClient mOkHttpClient;
    //    private String mLanguage="en";//语言
    private String mUrl;

    private HttpUploadClient() {
        mUrl = CommonAppConfig.HOST + "/api/?service=";
    }

    public static HttpUploadClient getInstance() {
        if (sInstance == null) {
            synchronized (HttpUploadClient.class) {
                if (sInstance == null) {
                    sInstance = new HttpUploadClient();
                }
            }
        }
        return sInstance;
    }

    public void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        builder.retryOnConnectionFailure(true);
//        Dispatcher dispatcher = new Dispatcher();
//        dispatcher.setMaxRequests(20000);
//        dispatcher.setMaxRequestsPerHost(10000);
//        builder.dispatcher(dispatcher);

        if(CommonAppContext.sInstance.isApkInDebug()){
            //输出HTTP请求 响应信息
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("http");
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(loggingInterceptor);
        }

        mOkHttpClient = builder.build();

        OkGo.getInstance().init(CommonAppContext.sInstance)
                .setOkHttpClient(mOkHttpClient)
                .setCacheMode(CacheMode.NO_CACHE)
                .setRetryCount(1);

    }

    public GetRequest<JsonBean> get(String serviceName, String tag) {
        return OkGo.<JsonBean>get(mUrl + serviceName)
                .headers("Connection", "keep-alive")
                .tag(tag)
                .params(CommonHttpConsts.LANGUAGE, LanguageUtil.getInstance().getLanguage())
                .params("version", CommonAppConfig.APP_VERSION)
                .params("model", CommonAppConfig.SYSTEM_MODEL)
                .params("system", CommonAppConfig.SYSTEM_RELEASE);
    }

    public PostRequest<JsonBean> post(String serviceName, String tag) {
        return OkGo.<JsonBean>post(mUrl + serviceName)
                .headers("Connection", "keep-alive")
                .tag(tag)
                .params(CommonHttpConsts.LANGUAGE, LanguageUtil.getInstance().getLanguage())
                .params("version", CommonAppConfig.APP_VERSION)
                .params("model", CommonAppConfig.SYSTEM_MODEL)
                .params("system", CommonAppConfig.SYSTEM_RELEASE);

    }


    public String getUrl() {
        return mUrl;
    }

    public void cancel(String tag) {
        OkGo.cancelTag(mOkHttpClient, tag);
    }

//    public void setLanguage(String language) {
//        mLanguage = language;
//    }

}
