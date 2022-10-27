package com.yunbao.common;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.SparseArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.bean.UserItemBean;
import com.yunbao.common.bean.UserItemBean2;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.DeviceUtils;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.VersionUtil;
import com.yunbao.common.utils.WordUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by cxf on 2017/8/4.
 */

public class CommonAppConfig {

    public static final String PACKAGE_NAME ="com.yunbao.shortvideo";
    //Http请求头 Header
    public static final Map<String, String> HEADER = new HashMap<>();
    //域名
    public static final String HOST = getHost();

    //外部sd卡
    public static final String DCMI_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    //内部存储 /data/data/<application package>/files目录
    public static final String INNER_PATH = CommonAppContext.sInstance.getFilesDir().getAbsolutePath();
    //文件夹名字
    private static final String DIR_NAME = "yunbaoyd";
    //保存视频的时候，在sd卡存储短视频的路径DCIM下
    public static final String VIDEO_PATH = DCMI_PATH + "/" + DIR_NAME + "/video/";
    public static final String VIDEO_RECORD_TEMP_PATH = VIDEO_PATH + "recordParts";
    //下载贴纸的时候保存的路径
    public static final String VIDEO_TIE_ZHI_PATH = DCMI_PATH + "/" + DIR_NAME + "/tieZhi/";
    //下载音乐的时候保存的路径
    public static final String MUSIC_PATH = DCMI_PATH + "/" + DIR_NAME + "/music/";
    //拍照时图片保存路径
    public static final String CAMERA_IMAGE_PATH = DCMI_PATH + "/" + DIR_NAME + "/camera/";

    public static final String GIF_PATH = INNER_PATH + "/gif/";
    //log保存路径
    public static final String LOG_PATH = DCMI_PATH + "/" + DIR_NAME + "/log/";

    //QQ登录是否与PC端互通
    public static final boolean QQ_LOGIN_WITH_PC = false;
    //当前app是否是云豹自己的产品
    public static final boolean APP_IS_YUNBAO_SELF = false;

    //是否使用游戏
    public static final boolean GAME_ENABLE = false;
    //是否上下滑动切换直播间
    public static final boolean LIVE_ROOM_SCROLL = true;

    //腾讯IM appId
    public static final int TX_IM_APP_Id = 14042;
    //腾讯音视频sdkAppId
    public static final int TX_TRTC_APP_Id = 14016;
    //腾讯音视频bizid
    public static final int TX_TRTC_BIZID = 732;
    //腾讯音视频sdk 的secretkey
    public static final String TX_TRTC_SECRETKEY = "ca8fb508acec4a7187b85608361db0ea62449063";

    public static final String APP_VERSION = VersionUtil.getVersion();//app版本号
    public static final String SYSTEM_MODEL = android.os.Build.MODEL;//手机型号
    public static final String SYSTEM_RELEASE = android.os.Build.VERSION.RELEASE;//手机系统版本号

    private static CommonAppConfig sInstance;

    private CommonAppConfig() {

    }

    public static CommonAppConfig getInstance() {
        if (sInstance == null) {
            synchronized (CommonAppConfig.class) {
                if (sInstance == null) {
                    sInstance = new CommonAppConfig();
                }
            }
        }
        return sInstance;
    }
    private String mUid;
    private String mToken;
    private ConfigBean mConfig;
    private double mLng;
    private double mLat;
    private String mProvince;//省
    private String mCity;//市
    private String mDistrict;//区
    private UserBean mUserBean;
    private String mVersion;
    private boolean mLoginIM;//IM是否登录了
    private Boolean mLaunched;//App是否启动了
    private Long mLaunchTime;//MainActivity打开的时间戳，极光IM用到
    private String mJPushAppKey;//极光推送的AppKey
    private List<UserItemBean2> mUserItemList;//个人中心功能列表
    private SparseArray<LevelBean> mLevelMap;
    private SparseArray<LevelBean> mAnchorLevelMap;
    private String mTxMapAppKey;//腾讯定位，地图的AppKey
    private String mTxMapAppSecret;//腾讯地图的AppSecret
    private boolean mFrontGround;
    private int mAppIconRes;
    private String mAppName;
    private Boolean mTiBeautyEnable;//是否使用萌颜 true使用萌颜 false 使用基础美颜
    //个人中心开关
    private Boolean mUserSwitchDisturb;//勿扰
    private Boolean mUserSwitchVideo;//视频接听
    private Boolean mUserSwitchVoice;//语音接听
    private String mPriceVideo;//视频接听价格
    private String mPriceVoice;//语音接听价格
    //悬浮窗是否在显示中
    private boolean mFloatButtonShowing;
    private int mIsPwd;
    private int mIsState;
    private int mIsLogin;
    private boolean isTeenagerChange;

    public String getUid() {
        if (TextUtils.isEmpty(mUid)) {
            String[] uidAndToken = SpUtil.getInstance()
                    .getMultiStringValue(new String[]{SpUtil.UID, SpUtil.TOKEN});
            if (uidAndToken != null) {
                if (!TextUtils.isEmpty(uidAndToken[0]) && !TextUtils.isEmpty(uidAndToken[1])) {
                    mUid = uidAndToken[0];
                    mToken = uidAndToken[1];
                }
            } else {
                return "-1";
            }
        }
        return mUid;
    }

    public String getToken() {
        return mToken;
    }

    public String getCoinName() {
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            return configBean.getCoinName();
        }
        return Constants.DIAMONDS;
    }

    public String getVotesName() {
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            return configBean.getVotesName();
        }
        return Constants.VOTES;
    }

    public ConfigBean getConfig() {
        if (mConfig == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                mConfig = JSON.parseObject(configString, ConfigBean.class);
            }
        }
        return mConfig;
    }

    public static String getHost() {
        String host = getMetaDataString("SERVER_HOST");
//        HEADER.put("referer", host);
        HEADER.put("referer", "https://livenew.yunbaozb.com");
        return host;
    }

    public void getConfig(CommonCallback<ConfigBean> callback) {
        if (callback == null) {
            return;
        }
        ConfigBean configBean = getConfig();
        if (configBean != null) {
            callback.callback(configBean);
        } else {
            CommonHttpUtil.getConfig(callback);
        }
    }

    public void setConfig(ConfigBean config) {
        mConfig = config;
    }

    /**
     * 经度
     */
    public double getLng() {
        if (mLng == 0) {
            String lng = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_LNG);
            if (!TextUtils.isEmpty(lng)) {
                try {
                    mLng = Double.parseDouble(lng);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mLng;
    }

    /**
     * 纬度
     */
    public double getLat() {
        if (mLat == 0) {
            String lat = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_LAT);
            if (!TextUtils.isEmpty(lat)) {
                try {
                    mLat = Double.parseDouble(lat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mLat;
    }

    /**
     * 省
     */
    public String getProvince() {
        if (TextUtils.isEmpty(mProvince)) {
            mProvince = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_PROVINCE);
        }
        return mProvince == null ? "" : mProvince;
    }

    /**
     * 市
     */
    public String getCity() {
        if (TextUtils.isEmpty(mCity)) {
            mCity = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_CITY);
        }
        return mCity == null ? "" : mCity;
    }

    /**
     * 区
     */
    public String getDistrict() {
        if (TextUtils.isEmpty(mDistrict)) {
            mDistrict = SpUtil.getInstance().getStringValue(SpUtil.LOCATION_DISTRICT);
        }
        return mDistrict == null ? "" : mDistrict;
    }

    public void setUserBean(UserBean bean) {
        mUserBean = bean;
    }

    public UserBean getUserBean() {
        if (mUserBean == null) {
            String userBeanJson = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
            if (!TextUtils.isEmpty(userBeanJson)) {
                mUserBean = JSON.parseObject(userBeanJson, UserBean.class);
            }
        }
        return mUserBean;
    }

    public void setIsPwd(int ispwd) {
        mIsPwd = ispwd;
    }

    public int getIsPwd() {
        return mIsPwd;
    }

    public int getIsState() {
        return mIsState;
    }

    public void setIsState(int isState) {
        mIsState = isState;
    }

    public int getIsLogin() {
        return mIsLogin;
    }

    public void setIsLogin(int isLogin) {
        mIsLogin = isLogin;
    }

    public boolean isSelf(UserBean userBean){
        if(userBean!=null){
            return  getUserBean().equals(userBean);
        }
        return false;
    }

    public boolean isTeenagerChange() {
        return isTeenagerChange;
    }

    public void setTeenagerChange(boolean teenagerChange) {
        isTeenagerChange = teenagerChange;
    }

    /**
     * 设置萌颜是否可用
     */
    public void setTiBeautyEnable(boolean tiBeautyEnable) {
        mTiBeautyEnable = tiBeautyEnable;
        SpUtil.getInstance().setBooleanValue(SpUtil.TI_BEAUTY_ENABLE, tiBeautyEnable);
    }

    public boolean isTiBeautyEnable() {
        if (mTiBeautyEnable == null) {
            mTiBeautyEnable = SpUtil.getInstance().getBooleanValue(SpUtil.TI_BEAUTY_ENABLE);
        }
        return mTiBeautyEnable;
    }

    /**
     * 设置登录信息
     */
    public void setLoginInfo(String uid, String token, boolean save) {
        L.e("登录成功", "uid------>" + uid);
        L.e("登录成功", "token------>" + token);
        mUid = uid;
        mToken = token;
        if (save) {
            Map<String, String> map = new HashMap<>();
            map.put(SpUtil.UID, uid);
            map.put(SpUtil.TOKEN, token);
            SpUtil.getInstance().setMultiStringValue(map);
        }
    }

    /**
     * 清除登录信息
     */
    public void clearLoginInfo() {
        mUid = null;
        mToken = null;
        mLoginIM = false;
        SpUtil.getInstance().removeValue(
                SpUtil.UID, SpUtil.TOKEN, SpUtil.USER_INFO, SpUtil.TX_IM_USER_SIGN, SpUtil.IM_LOGIN,Constants.CASH_ACCOUNT_ID, Constants.CASH_ACCOUNT, Constants.CASH_ACCOUNT_TYPE
        );
    }


    /**
     * 设置位置信息
     *
     * @param lng      经度
     * @param lat      纬度
     * @param province 省
     * @param city     市
     */
    public void setLocationInfo(double lng, double lat, String province, String city, String district) {
        mLng = lng;
        mLat = lat;
        mProvince = province;
        mCity = city;
        mDistrict = district;
        Map<String, String> map = new HashMap<>();
        map.put(SpUtil.LOCATION_LNG, String.valueOf(lng));
        map.put(SpUtil.LOCATION_LAT, String.valueOf(lat));
        map.put(SpUtil.LOCATION_PROVINCE, province);
        map.put(SpUtil.LOCATION_CITY, city);
        map.put(SpUtil.LOCATION_DISTRICT, district);
        SpUtil.getInstance().setMultiStringValue(map);
    }

    /**
     * 清除定位信息
     */
    public void clearLocationInfo() {
        mLng = 0;
        mLat = 0;
        mProvince = null;
        mCity = null;
        mDistrict = null;
        SpUtil.getInstance().removeValue(
                SpUtil.LOCATION_LNG,
                SpUtil.LOCATION_LAT,
                SpUtil.LOCATION_PROVINCE,
                SpUtil.LOCATION_CITY,
                SpUtil.LOCATION_DISTRICT);

    }


    public boolean isLoginIM() {
        return mLoginIM;
    }

    public void setLoginIM(boolean loginIM) {
        mLoginIM = loginIM;
    }

    /**
     * 获取版本号
     */
    public String getVersion() {
        if (TextUtils.isEmpty(mVersion)) {
            try {
                PackageManager manager = CommonAppContext.sInstance.getPackageManager();
                PackageInfo info = manager.getPackageInfo(CommonAppContext.sInstance.getPackageName(), 0);
                mVersion = info.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mVersion;
    }

    /**
     * 获取App名称
     */
    public String getAppName() {
        if (TextUtils.isEmpty(mAppName)) {
            int res = CommonAppContext.sInstance.getResources().getIdentifier("app_name", "string", "com.yunbao.shortvideo");
            mAppName = WordUtil.getString(res);
        }
        return mAppName;
    }


    /**
     * 获取App图标的资源id
     */
    public int getAppIconRes() {
        if (mAppIconRes == 0) {
            mAppIconRes = CommonAppContext.sInstance.getResources().getIdentifier("ic_launcher", "mipmap", "com.yunbao.shortvideo");
        }
        return mAppIconRes;
    }

    /**
     * 获取MetaData中的极光AppKey
     */
    public String getJPushAppKey() {
        if (mJPushAppKey == null) {
            mJPushAppKey = getMetaDataString("JPUSH_APPKEY");
        }
        return mJPushAppKey;
    }


    /**
     * 获取MetaData中的腾讯定位，地图的AppKey
     *
     * @return
     */
    public String getTxMapAppKey() {
        if (mTxMapAppKey == null) {
            mTxMapAppKey = getMetaDataString("TencentMapSDK");
        }
        return mTxMapAppKey;
    }


    /**
     * 获取MetaData中的腾讯定位，地图的AppSecret
     *
     * @return
     */
    public String getTxMapAppSecret() {
        if (mTxMapAppSecret == null) {
            mTxMapAppSecret = getMetaDataString("TencentMapAppSecret");
        }
        return mTxMapAppSecret;
    }


    public static String getMetaDataString(String key) {
        String res = null;
        try {
            ApplicationInfo appInfo = CommonAppContext.sInstance.getPackageManager().getApplicationInfo(CommonAppContext.sInstance.getPackageName(), PackageManager.GET_META_DATA);
            res = appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }


    /**
     * 个人中心功能列表
     */
    public List<UserItemBean2> getUserItemList() {
        if (mUserItemList == null || mUserItemList.size() == 0) {
            String userBeanJson = SpUtil.getInstance().getStringValue(SpUtil.USER_INFO);
            if (!TextUtils.isEmpty(userBeanJson)) {
                JSONObject obj = JSON.parseObject(userBeanJson);
                if (obj != null) {
                    setUserItemList(obj.getString("list"));
                }
            }
        }
        return mUserItemList;
    }


    public void setUserItemList(String listString) {
        if (!TextUtils.isEmpty(listString)) {
            try {
                mUserItemList = JSON.parseArray(listString, UserItemBean2.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 保存用户等级信息
     */
    public void setLevel(String levelJson) {
        if (TextUtils.isEmpty(levelJson)) {
            return;
        }
        List<LevelBean> list = JSON.parseArray(levelJson, LevelBean.class);
        if (list == null || list.size() == 0) {
            return;
        }
        if (mLevelMap == null) {
            mLevelMap = new SparseArray<>();
        }
        mLevelMap.clear();
        for (LevelBean bean : list) {
            mLevelMap.put(bean.getLevel(), bean);
        }
    }

    /**
     * 保存主播等级信息
     */
    public void setAnchorLevel(String anchorLevelJson) {
        if (TextUtils.isEmpty(anchorLevelJson)) {
            return;
        }
        List<LevelBean> list = JSON.parseArray(anchorLevelJson, LevelBean.class);
        if (list == null || list.size() == 0) {
            return;
        }
        if (mAnchorLevelMap == null) {
            mAnchorLevelMap = new SparseArray<>();
        }
        mAnchorLevelMap.clear();
        for (LevelBean bean : list) {
            mAnchorLevelMap.put(bean.getLevel(), bean);
        }
    }

    /**
     * 获取用户等级
     */
    public LevelBean getLevel(int level) {
        if (mLevelMap == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                JSONObject obj = JSON.parseObject(configString);
                setLevel(obj.getString("level"));
            }
        }

        if (mLevelMap == null) {
            return null;
        }
        int size = mLevelMap.size();
        if (size == 0) {
            return null;
        }
        if (level == 0){
            level = 1;
        }
        return mLevelMap.get(level);
    }

    /**
     * 获取主播等级
     */
    public LevelBean getAnchorLevel(int level) {
        if (mAnchorLevelMap == null) {
            String configString = SpUtil.getInstance().getStringValue(SpUtil.CONFIG);
            if (!TextUtils.isEmpty(configString)) {
                JSONObject obj = JSON.parseObject(configString);
                setAnchorLevel(obj.getString("levelanchor"));
            }
        }

        if (mAnchorLevelMap == null) {
            return null;
        }
        int size = mAnchorLevelMap.size();
        if (size == 0) {
            return null;
        }
        return mAnchorLevelMap.get(level);
    }

    /**
     * 判断某APP是否安装
     */
    public static boolean isAppExist(String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            PackageManager manager = CommonAppContext.sInstance.getPackageManager();
            List<PackageInfo> list = manager.getInstalledPackages(0);
            for (PackageInfo info : list) {
                if (packageName.equalsIgnoreCase(info.packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isLaunched() {
        if (mLaunched == null) {
            mLaunched = SpUtil.getInstance().getBooleanValue(SpUtil.APP_LAUNCHED);
        }
        return mLaunched;
    }

    public void setLaunched(boolean launched) {
        mLaunched = launched;
        SpUtil.getInstance().setBooleanValue(SpUtil.APP_LAUNCHED, launched);
    }


    public Long getLaunchTime() {
        if (mLaunchTime == null) {
            mLaunched = SpUtil.getInstance().getBooleanValue(SpUtil.APP_LAUNCHED);
        }
        return mLaunchTime;
    }

    public void setLaunchTime(Long launchTime) {
        SpUtil.getInstance().setLongValue(SpUtil.APP_LAUNCHED_TIME, launchTime);
        mLaunchTime = launchTime;
    }

    //app是否在前台
    public boolean isFrontGround() {
        return mFrontGround;
    }

    //app是否在前台
    public void setFrontGround(boolean frontGround) {
        mFrontGround = frontGround;
    }

    public boolean isFloatButtonShowing() {
        return mFloatButtonShowing;
    }

    public void setFloatButtonShowing(boolean floatButtonShowing) {
        mFloatButtonShowing = floatButtonShowing;
    }

    /**
     * 判断是否拥有悬浮窗权限
     *
     * @param isApplyAuthorization 是否申请权限
     */
    public static boolean canDrawOverlays(Context context, boolean isApplyAuthorization) {
        //Android 6.0 以下无需申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //判断是否拥有悬浮窗权限，无则跳转悬浮窗权限授权页面
            if (Settings.canDrawOverlays(context)) {
                return true;
            } else {
                ToastUtil.show("请开启悬浮窗权限后尝试");
                if (isApplyAuthorization) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                    if (context instanceof Service) {
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    }
                    context.startActivity(intent);
                    return false;
                } else {
                    return false;
                }
            }
        } else {
            return true;
        }
    }



    private Boolean mMhBeautyEnable;//是否使用美狐 true使用美狐 false 使用基础美颜
    private String mDeviceId;
    /**
     * 设置美狐是否可用
     */
    public void setMhBeautyEnable(boolean mhBeautyEnable) {
        mMhBeautyEnable = mhBeautyEnable;
        SpUtil.getInstance().setBooleanValue(SpUtil.MH_BEAUTY_ENABLE, mhBeautyEnable);
    }

    /**
     * 美狐是否可用
     */
    public boolean isMhBeautyEnable() {
        if (mMhBeautyEnable == null) {
            mMhBeautyEnable = SpUtil.getInstance().getBooleanValue(SpUtil.MH_BEAUTY_ENABLE);
        }
        return mMhBeautyEnable;
    }


    public boolean isLogin(){
        return true;
    }


    public String getDeviceId() {
        if (TextUtils.isEmpty(mDeviceId)) {
            String deviceId = SpUtil.getInstance().getStringValue(SpUtil.DEVICE_ID);
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = DeviceUtils.getDeviceId();
                SpUtil.getInstance().setStringValue(SpUtil.DEVICE_ID, deviceId);
            }
            mDeviceId = deviceId;
        }
        L.e("getDeviceId---mDeviceId-----> " + mDeviceId);
        return mDeviceId;
    }


}
