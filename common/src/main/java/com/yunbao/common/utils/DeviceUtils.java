package com.yunbao.common.utils;

import android.os.Build;
import android.provider.Settings;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;

public class DeviceUtils {

    public static String getDeviceId() {
        String s = StringUtil.contact(
                Settings.System.getString(CommonAppContext.sInstance.getContentResolver(), Settings.System.ANDROID_ID),
                Build.SERIAL,
                Build.FINGERPRINT,
                String.valueOf(Build.TIME),
                Build.USER,
                Build.HOST,
                Build.getRadioVersion(),
                Build.HARDWARE,
                "com.yunbao.shortvideo"
        );
        return MD5Util.getMD5(s);
    }


}
