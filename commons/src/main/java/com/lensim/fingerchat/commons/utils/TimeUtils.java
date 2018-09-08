package com.lensim.fingerchat.commons.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static final String FORMATE_NO_TIME = "yyyy-MM-dd";
    public static final String FORMATE_SIGN_IN = "yyyy年MM月dd日  EEEE";
    public static final String TIME_FORMATE_NO_SECONDS = "yyyy-MM-dd HH:mm";
    public static final String TIME_FORMATE = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMATE_HOUR = "HH:mm";

    public static long getTimeStamp(String dateString) {
        try {
            dateString = dateString.replace("T", " ");
            Date date = new SimpleDateFormat(TIME_FORMATE).parse(dateString);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static long getTimeStampNoSeconds(String dateString) {
        try {
            dateString = dateString.replace("T", " ");
            Date date = new SimpleDateFormat(TIME_FORMATE_NO_SECONDS).parse(dateString);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static String getDateString(String timeStamp) {
        if (isBlank(timeStamp)) {
            return "";
        }
        BigDecimal db = new BigDecimal(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMATE);
        return format.format(db.longValue());
    }

    public static String getDateStringSign(String timeStamp) {
        if (isBlank(timeStamp)) {
            return "";
        }
        BigDecimal db = new BigDecimal(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat(FORMATE_SIGN_IN);
        return format.format(db.longValue());
    }

    public static String getDateHourString(String timeStamp) {
        if (isBlank(timeStamp)) {
            return "";
        }
        BigDecimal db = new BigDecimal(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMATE_HOUR);
        return format.format(db.longValue());
    }

    //根据时间戳毫秒获取格式为yyyy-MM-dd的时间
    public static String getDate() {
        Date date = new Date();//获取当前日期时间
        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMATE);
        return format.format(date);
    }

    public static String getDateNoSeconds() {
        Date date = new Date();//获取当前日期时间
        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMATE_NO_SECONDS);
        return format.format(date);
    }

    //根据时间戳毫秒获取格式为yyyy-MM-dd的时间
    public static String getDateFirstDayThisMonth() {
        Calendar cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 0);
        cale.set(Calendar.DAY_OF_MONTH, 1);
        cale.set(Calendar.HOUR_OF_DAY, 0);
        cale.set(Calendar.MINUTE, 0);
        cale.set(Calendar.SECOND, 0);
        Date resultDate = cale.getTime(); // 结果
        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMATE);
        return format.format(resultDate);
    }

    //今天0点时间戳
    public static long getDateStampToday() {
        Calendar cale = Calendar.getInstance();
        cale.set(Calendar.HOUR_OF_DAY, 0);
        cale.set(Calendar.MINUTE, 0);
        cale.set(Calendar.SECOND, 0);
        Date resultDate = cale.getTime(); // 结果
        return resultDate.getTime();
    }

    public static String getDateNoTime() {
        Date date = new Date();//获取当前日期时间
        SimpleDateFormat format = new SimpleDateFormat(FORMATE_NO_TIME);
        return format.format(date);
    }

    //根据时间戳毫秒获取格式为yyyy-MM-dd的时间
    public static String getTime() {
        Date date = new Date();//获取当前日期时间
        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMATE_HOUR);
        return format.format(date);
    }

    /**
     * 传递进来时间戳秒，返回天数，大于30天则显示具体发帖时间
     * <p>
     * 目前只有收藏用到
     *
     * @return 10天前
     */
    public static String secondToTime(String milliSecond) {
        if (isBlank(milliSecond)) {
            return "";
        }
        Date now = new Date();
        Long time = new Long(milliSecond);
        SimpleDateFormat format = new SimpleDateFormat(FORMATE_NO_TIME);
        String data = format.format(time);      //根据时间戳毫秒获取格式为yyyy-MM-dd的时间

        long between = now.getTime() / 1000 - time.longValue() / 1000;  //8*60*60这个是为了处理时区问题
        //long between = (now.getTime() / 1000 - db.longValue() / 1000 + 8 * 60 * 60);  //8*60*60这个是为了处理时区问题
        long day = between / (24 * 3600);

        long hour = between % (24 * 3600) / 3600;

        long minute = between % 3600 / 60;

        long second = between % 60;

        String result = "";

        if (day > 30) {
            result = data;
        } else if (day <= 30 && day > 0) {
            result = String.valueOf(day) + "天前";
        } else if (hour > 0) {
            result = String.valueOf(hour) + "小时前";
        } else if (minute > 0) {
            result = String.valueOf(minute) + "分钟前";
        } else if (second > 0) {
            result = String.valueOf(second) + "秒前";
        }

        return result;
    }

    /**
     * @param dateFormat format: 2016/04/24 10:00:10
     * @return 返回天数，大于14天则显示具体发帖时间
     */
    public static String progressDate(long dateFormat) {
        long date = dateFormat;
        if (date == 0L) {
            return "";
        }

        Date now = new Date();

        long between = (now.getTime() - date) / 1000; // 2个时间相差多少秒

        long day = between / (24 * 3600);

        long hour = between % (24 * 3600) / 3600;

        long minute = between % 3600 / 60;

        long second = between % 60;

        String result = "";

        if (day > 0) {
            result = String.valueOf(day) + "天前";
        } else if (hour > 0) {
            result = String.valueOf(hour) + "小时前";
        } else if (minute > 0) {
            result = String.valueOf(minute) + "分钟前";
        } else {
            result = "1分钟前";
        }

        return result;
    }

    public static boolean isBlank(String str) {
        if (str == null || str.trim().length() == 0) {
            return true;
        }
        return false;
    }


    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }

    public static String timeFormat(long i){
        Date date = new Date(i);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String s = dateFormat.format(date);
        return s;
    }
}