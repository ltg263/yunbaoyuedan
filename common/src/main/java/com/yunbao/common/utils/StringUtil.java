package com.yunbao.common.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.R;
import com.yunbao.common.http.HttpClient;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by cxf on 2018/9/28.
 */

public class StringUtil {
    private static DecimalFormat sDecimalFormat;
    private static DecimalFormat sDecimalFormat2;
    // private static Pattern sPattern;
    private static Pattern sIntPattern;
    private static Random sRandom;
    private static StringBuilder sStringBuilder;
    private static StringBuilder sTimeStringBuilder;
    private static ClipboardManager mClipboardManager;


    static {
        sDecimalFormat = new DecimalFormat("#.#");
        sDecimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        sDecimalFormat2 = new DecimalFormat("#.##");
        sDecimalFormat2.setRoundingMode(RoundingMode.DOWN);
        //sPattern = Pattern.compile("[\u4e00-\u9fa5]");
        sIntPattern = Pattern.compile("^[-\\+]?[\\d]*$");
        sRandom = new Random();
        sStringBuilder = new StringBuilder();
        sTimeStringBuilder = new StringBuilder();
    }

    public static String format(double value) {
        return sDecimalFormat.format(value);
    }

    /**
     * 把数字转化成多少万
     */
    public static String toWan(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat.format(num / 10000d) + "W";
    }


    /**
     * 把数字转化成多少万
     */
    public static String toWan2(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat.format(num / 10000d);
    }

    /**
     * 把数字转化成多少万
     */
    public static String toWan3(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat2.format(num / 10000d) + "w";
    }

//    /**
//     * 判断字符串中是否包含中文
//     */
//    public static boolean isContainChinese(String str) {
//        Matcher m = sPattern.matcher(str);
//        if (m.find()) {
//            return true;
//        }
//        return false;
//    }

    /**
     * 判断一个字符串是否是数字
     */
    public static boolean isInt(String str) {
        return sIntPattern.matcher(str).matches();
    }


    /**
     * 把一个long类型的总毫秒数转成时长
     */
    public static String getDurationText(long mms) {
        int hours = (int) (mms / (1000 * 60 * 60));
        int minutes = (int) ((mms % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((mms % (1000 * 60)) / 1000);
        sTimeStringBuilder.delete(0, sTimeStringBuilder.length());
        if (hours > 0) {
            if (hours < 10) {
                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(hours));
            sTimeStringBuilder.append(":");
        }
        if (minutes > 0) {
            if (minutes < 10) {
                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(minutes));
            sTimeStringBuilder.append(":");
        } else {
            sTimeStringBuilder.append("00:");
        }
        if (seconds > 0) {
            if (seconds < 10) {
                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(seconds));
        } else {
            sTimeStringBuilder.append("00");
        }
        return sTimeStringBuilder.toString();
    }


    /**
     * 把一个long类型的总毫秒数转成时长
     */
    public static String getDurationText2(long mms) {
        int hours = (int) (mms / (1000 * 60 * 60));
        int minutes = (int) ((mms % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((mms % (1000 * 60)) / 1000);
        sTimeStringBuilder.delete(0, sTimeStringBuilder.length());
        if (hours < 10) {
            sTimeStringBuilder.append("0");
        }
        sTimeStringBuilder.append(String.valueOf(hours));
        sTimeStringBuilder.append(":");
        if (minutes > 0) {
            if (minutes < 10) {

                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(minutes));
            sTimeStringBuilder.append(":");
        } else {
            sTimeStringBuilder.append("00:");
        }
        if (seconds > 0) {
            if (seconds < 10) {
                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(seconds));
        } else {
            sTimeStringBuilder.append("00");
        }
        return sTimeStringBuilder.toString();
    }


    /**
     * 把一个long类型的总毫秒数转成时长
     */
    public static String getDurationText3(long mms) {
        int hours = (int) (mms / (1000 * 60 * 60));
        int minutes = (int) ((mms % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((mms % (1000 * 60)) / 1000);
        sTimeStringBuilder.delete(0, sTimeStringBuilder.length());
        if (hours > 0) {
            if (hours < 10) {
                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(hours));
            sTimeStringBuilder.append(":");
        }
        if (minutes > 0) {
            if (minutes < 10) {
                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(minutes));
            sTimeStringBuilder.append(":");
        } else {
            sTimeStringBuilder.append("00:");
        }
        if (seconds > 0) {
            if (seconds < 10) {
                sTimeStringBuilder.append("0");
            }
            sTimeStringBuilder.append(String.valueOf(seconds));
        } else {
            sTimeStringBuilder.append("00");
        }
        return sTimeStringBuilder.toString();
    }


    /**
     * 把一个long类型的总毫秒数转成时长
     */
    public static String getDurationText4(long mms) {
        int hours = (int) (mms / (1000 * 60 * 60));
        int minutes = (int) ((mms % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((mms % (1000 * 60)) / 1000);
        sTimeStringBuilder.delete(0, sTimeStringBuilder.length());
        if (hours > 0) {
            sTimeStringBuilder.append(String.valueOf(hours));
            sTimeStringBuilder.append(WordUtil.getString(R.string.time_hour));
        }
        if (minutes > 0) {
            sTimeStringBuilder.append(String.valueOf(minutes));
            sTimeStringBuilder.append(WordUtil.getString(R.string.time_minute));
        }
        if (seconds > 0) {
            sTimeStringBuilder.append(String.valueOf(seconds));
            sTimeStringBuilder.append(WordUtil.getString(R.string.time_second));
        }
        return sTimeStringBuilder.toString();
    }


    /**
     * 设置视频输出路径
     */
    public static String generateVideoOutputPath() {
        String outputDir = CommonAppConfig.VIDEO_PATH;
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        return outputDir + generateFileName() + ".mp4";
    }

    /**
     * 获取随机文件名
     */
    public static String generateFileName() {
        return "android_" + CommonAppConfig.getInstance().getUid() + "_" + DateFormatUtil.getVideoCurTimeString() + sRandom.nextInt(9999);
    }

    /**
     * 多个字符串拼接
     */
    public static String contact(Object... args) {
        sStringBuilder.delete(0, sStringBuilder.length());
        for (Object s : args) {
            sStringBuilder.append(s);
        }
        return sStringBuilder.toString();
    }

    /*自动排序签名,输入的时候不用考虑顺序,避免人工排序带来的失误和时间消耗*/
    public static String createSign(Map<String,Object>map,String...keyArray){
        if(map==null||keyArray==null||keyArray.length==0) {
            return null;
        }
        List<String> wordList = Arrays.asList(keyArray);
        if(!ListUtil.haveData(wordList)){
            return null;
        }
        Collections.sort(wordList,new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        sStringBuilder.delete(0, sStringBuilder.length());
        for(String key:wordList){
            Object obj=map.get(key);
            sStringBuilder.append(key)
            .append("=");
            if(obj!=null){
                sStringBuilder.append(obj);
            }
            sStringBuilder.append("&");
        }
        sStringBuilder.append(HttpClient.SALT);
        return MD5Util.getMD5(sStringBuilder.toString());
    }


    public static boolean equals(String str1,String str2){
        if(TextUtils.isEmpty(str1)||TextUtils.isEmpty(str2)){
            return false;
        }
        return str1.equals(str2);

    }


    public static boolean equalsContainNull(String str1,String str2){
        if(TextUtils.isEmpty(str1)&&TextUtils.isEmpty(str2)){
            return true;
        }else if(!TextUtils.isEmpty(str1)&&!TextUtils.isEmpty(str2)){
            return str1.equals(str2);
        }else {
            return false;
        }
    }

    /**
     * 复制文字
     */
    public static void copyText(Context context,String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (mClipboardManager == null) {
            mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        ClipData clipData = ClipData.newPlainText("text", text);
        mClipboardManager.setPrimaryClip(clipData);
        ToastUtil.show(R.string.order_number_copy_success);
    }
}
