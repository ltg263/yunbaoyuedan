package com.yunbao.main.utils;

import android.os.Handler;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;

/**
 * Created by cxf on 2018/6/28.
 */

public class CityUtil {

    private ArrayList<Province> mProvinceList;
    private static CityUtil sInstance;
    private Handler mHandler;

    private CityUtil() {
        mProvinceList = new ArrayList<>();
        mHandler = new Handler();
    }

    public static CityUtil getInstance() {
        if (sInstance == null) {
            synchronized (CityUtil.class) {
                if (sInstance == null) {
                    sInstance = new CityUtil();
                }
            }
        }
        return sInstance;
    }

    public void getCityListFromAssets(final CommonCallback<ArrayList<Province>> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader br = null;
                try {
                    InputStream is = CommonAppContext.sInstance.getAssets().open("city.json");
                    br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    String result = sb.toString();
                    if (!TextUtils.isEmpty(result)) {
                        if (mProvinceList == null) {
                            mProvinceList = new ArrayList<>();
                        }
                        mProvinceList.addAll(JSON.parseArray(result, Province.class));
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.callback(mProvinceList);
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.callback(null);
                            }
                        }
                    });
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public ArrayList<Province> getCityList() {
        return mProvinceList;
    }


    /**
     * 选择时间
     *20200630在原始demo基础上，调整为 只显示当前时间可以选择的时间范围
     */
    public ArrayList<Province> getTimeList(String today, String tomorrow, String tomorrow2) {
        String[] mins = new String[]{"00", "15", "30", "45"};
        String[] hours = new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        String[] days = new String[]{today, tomorrow, tomorrow2};

        Calendar c = Calendar.getInstance();
        int hourIndex = c.get(Calendar.HOUR_OF_DAY);//小时
        int currentMin = c.get(Calendar.MINUTE);//分钟
        L.e("---hourIndex--" + hourIndex + "--currentMin---" + currentMin);
        ArrayList<County> countyList = new ArrayList<>();
        for (String min : mins) {
            County county = new County(min);
            countyList.add(county);
        }
        ArrayList<City> cityList = new ArrayList<>();
        for (int i = 0; i < hours.length; i++) {
            if (i == hourIndex) {
                if (currentMin + 10 > 45) {
                    City city = new City(hours[i]);
                    city.setCounties(countyList);
                    cityList.add(city);
                    continue;
                }
                ArrayList<County> newCountyList = new ArrayList<>();
                City city = new City(hours[hourIndex]);
                //以10分钟为最小时间间隔，当前时间距离下一档位时间大于十分钟，则下一档位时间可选；若小于10分钟，则下一档位时间不可选
                if (currentMin + 10 <= 15) {
                    newCountyList.add(new County("15"));
                }
                if (currentMin + 10 <= 30) {
                    newCountyList.add(new County("30"));
                }
                if (currentMin + 10 <= 45) {
                    newCountyList.add(new County("45"));
                }
                city.setCounties(newCountyList);
                cityList.add(city);
            } else if (i == hourIndex+1){
                if (currentMin + 10 > 50) {
                    ArrayList<County> newCountyList = new ArrayList<>();
                    City city = new City(hours[hourIndex+1]);
                    newCountyList.add(new County("15"));
                    newCountyList.add(new County("30"));
                    newCountyList.add(new County("45"));
                    city.setCounties(newCountyList);
                    cityList.add(city);
                }else {
                    City city = new City(hours[i]);
                    city.setCounties(countyList);
                    cityList.add(city);
                }
            } else {
                City city = new City(hours[i]);
                city.setCounties(countyList);
                cityList.add(city);
            }
        }
        if (currentMin + 10 > 45) {
            hourIndex++;
            //若23点时间不满足条件，天数加一
            if (hourIndex > 23) {
                hourIndex = 0;
                if (currentMin + 10 > 50) {
                    ArrayList<County> newCountyList = new ArrayList<>();
                    City city = new City(hours[hourIndex]);
                    newCountyList.add(new County("15"));
                    newCountyList.add(new County("30"));
                    newCountyList.add(new County("45"));
                    city.setCounties(newCountyList);
                    cityList.set(hourIndex,city);
                }
                days = new String[]{tomorrow, tomorrow2};
            }
        }
        ArrayList<Province> provinceList = new ArrayList<>();
        for (int i = 0, len = days.length; i < len; i++) {
            if (days.length == 3){
                if (i == 0) {
                    //若选择今天，则可选的小时单位 从当前时间开始
                    Province province = new Province("0", days[i]);
                    province.setCities(cityList.subList(hourIndex, hours.length));
                    provinceList.add(province);
                    continue;
                }
            }
            int areaId = i;
            if (days.length == 2){
                //若只有明天、后台可选 type需手动改变 ，先这样凑活吧。。。
                areaId = areaId + 1;
            }
            Province province = new Province(String.valueOf(areaId), days[i]);
            province.setCities(cityList);
            provinceList.add(province);
        }
        return provinceList;
    }

}
