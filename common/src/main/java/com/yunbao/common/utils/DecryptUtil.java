package com.yunbao.common.utils;

import android.text.TextUtils;

/**
 * Created by cxf on 2019/7/27.
 */

public class DecryptUtil {

    private static final String KEY = "1ecxXyLRB.COdrAi:q09Z62ash-QGn8VFNIlb=fM/D74WjS_EUzYuw?HmTPvkJ3otK5gp";
    private static StringBuilder sStringBuilder;


    /**
     * 解密url
     */
    public static String decrypt(String content) {
        return decrypt(KEY, content);
    }

    /**
     * 解密url
     */
    public static String decrypt(String key, String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        if (sStringBuilder == null) {
            sStringBuilder = new StringBuilder();
        }
        sStringBuilder.delete(0, sStringBuilder.length());
        for (int i = 0, len1 = content.length(); i < len1; i++) {
            for (int j = 0, len2 = key.length(); j < len2; j++) {
                if (content.charAt(i) == key.charAt(j)) {
                    if (j - 1 < 0) {
                        sStringBuilder.append(key.charAt(len2 - 1));
                    } else {
                        sStringBuilder.append(key.charAt(j - 1));
                    }
                }
            }
        }
        return sStringBuilder.toString();
    }
}
