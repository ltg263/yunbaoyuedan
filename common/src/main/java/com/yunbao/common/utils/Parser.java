package com.yunbao.common.utils;

import android.text.TextUtils;

import com.yunbao.common.R;

public  class Parser{
    public static final int MODE_MINUTE=1;
    public static final int MODE_DEFAULT=0;
    private int model=MODE_DEFAULT;

        private  long hour = 0;
        private  long minute = 0;
        private  long second = 0;
        private  long day= 0;

        private  boolean dayNotAlready = false;
        private  boolean hourNotAlready = false;
        private  boolean minuteNotAlready = false;
        private  boolean secondNotAlready = false;

        private StringBuilder stringBuilder;
        private String beforeString;
        public Parser(){
        }

    public void setModel(int model) {
        this.model = model;
    }

    private void initStringBuilder() {
            if(stringBuilder==null){
                stringBuilder=new StringBuilder();
            }else {
                stringBuilder.setLength(0);
            }
    }

    public  String parse(long totalSecond){
            return parseStringBuilder(totalSecond).toString();
        }


    public  StringBuilder parseStringBuilder(long totalSecond){
        clear();
        if (totalSecond > 0) {
            secondNotAlready = true;
            second = totalSecond;
            if (second >= 60) {
                minuteNotAlready = true;
                minute = second / 60;
                second = second % 60;
                if (minute >= 60) {
                    hourNotAlready = true;
                    hour = minute / 60;
                    minute = minute % 60;
                    if (hour > 24) {
                        dayNotAlready = true;
                        day = hour / 24;
                        hour = hour % 24;
                    }
                }
            }
        }
        if(!TextUtils.isEmpty(beforeString)){
            stringBuilder.append(beforeString);
        }
        if(model!=MODE_MINUTE){
            stringBuilder.append(getTimeString(hour)).append(":");
        }
        return stringBuilder
                .append(getTimeString(minute))
                .append(":")
                .append(getTimeString(second));
    }


    public  StringBuilder parseToDayHourMinute(long totalSecond){
        clear();
        if (totalSecond > 0) {
            secondNotAlready = true;
            second = totalSecond;
            if (second >= 60) {
                minuteNotAlready = true;
                minute = second / 60;
                second = second % 60;
                if (minute >= 60) {
                    hourNotAlready = true;
                    hour = minute / 60;
                    minute = minute % 60;
                    if (hour > 24) {
                        dayNotAlready = true;
                        day = hour / 24;
                        hour = hour % 24;
                    }
                }
            }
        }
        if(!TextUtils.isEmpty(beforeString)){
            stringBuilder.append(beforeString);
        }
        stringBuilder.append(day).append(WordUtil.getString(R.string.day));
        if(model!=MODE_MINUTE){
            stringBuilder.append(getTimeString(hour)).append(WordUtil.getString(R.string.hour));
        }
        return stringBuilder
                .append(getTimeString(minute))
                .append(WordUtil.getString(R.string.minute))
                .append(getTimeString(second))
                .append(WordUtil.getString(R.string.second));
    }

    public void clear(){
            day=0;
            hour=0;
            minute=0;
            initStringBuilder();
        }

        public String getTimeString(long time){
            if(time<10){
               return "0"+time;
            }else{
                return ""+time;
            }
        }

    public void setBeforeString(String beforeString) {
            this.beforeString=beforeString;
    }
}