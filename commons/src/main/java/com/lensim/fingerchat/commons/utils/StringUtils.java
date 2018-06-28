package com.lensim.fingerchat.commons.utils;


import android.content.Context;
import android.content.res.Resources;
import android.text.TextPaint;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Pattern;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


/**
 * 字符串操作工具包
 *
 * @author
 * @created
 */
public class StringUtils {

    public final static Pattern emailer = Pattern
        .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    public final static Pattern IMG_URL = Pattern
        .compile(".*?(gif|jpeg|png|jpg|bmp)");

    public final static Pattern URL = Pattern
        .compile("^(https|http)://.*?$(net|com|.com.cn|org|me|)");

//    public final static Pattern URL1 = Pattern
//            .compile("(https|http)://.*?$(net|com|.com.cn|org|me|)");

    public final static Pattern URL1 =
        Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");

    public final static Pattern JOB_NUM = Pattern
        .compile("^[a-zA-Z]{2}[0-9]{6}$");

    public final static Pattern IDENTIFY_CODE = Pattern
        .compile("^[0-9]{6}$");

    public final static Pattern MOBILE_PHONE = Pattern
        .compile("^1[34578]\\d{9}$");

    private final static Pattern ZH_CN = Pattern.compile("[\\u4e00-\\u9fa5]+");

    private final static Pattern NUM_OR_LETTER = Pattern.compile("^[a-z0-9A-Z]+$");

    private final static Pattern EN_LETTER = Pattern.compile("^[a-zA-Z]+$");


    private final static Pattern ZH_CN_NUM_OR_LETTER = Pattern
        .compile("^[\\u4e00-\\u9fa5_a-zA-Z0-9]+$");

    private final static Pattern MUC_NAME = Pattern
        .compile("^[a-zA-Z0-9\\u4e00-\\u9fa5_\\-()$*<>]{1,20}$");

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };


    private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();


    private static Random randGen = new Random();

    /**
     * 将字符串转位日期类型
     */
    public static Date toDate(String sdate) {
        return toDate(sdate, dateFormater.get());
    }

    public static Date toDate(String sdate, SimpleDateFormat dateFormater) {
        try {
            return dateFormater.parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getDateString(Date date) {
        return dateFormater.get().format(date);
    }

    public static String getDateString2(Date date) {
        return dateFormater2.get().format(date);
    }

    /**
     * 以友好的方式显示时间
     */
//    public static String friendly_time(String sdate) {
//        Date time = null;
//
//        if (TimeZoneUtil.isInEasternEightZones())
//            time = toDate(sdate);
//        else
//            time = TimeZoneUtil.transformTime(toDate(sdate),
//                    TimeZone.getTimeZone("GMT+08"), TimeZone.getDefault());
//
//        if (time == null) {
//            return "Unknown";
//        }
//        String ftime = "";
//        Calendar cal = Calendar.getInstance();
//
//        // 判断是否是同一天
//        String curDate = dateFormater2.get().format(cal.getTime());
//        String paramDate = dateFormater2.get().format(time);
//        if (curDate.equals(paramDate)) {
//            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
//            if (hour == 0)
//                ftime = Math.max(
//                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
//                        + "分钟前";
//            else
//                ftime = hour + "小时前";
//            return ftime;
//        }
//
//        long lt = time.getTime() / 86400000;
//        long ct = cal.getTimeInMillis() / 86400000;
//        int days = (int) (ct - lt);
//        if (days == 0) {
//            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
//            if (hour == 0)
//                ftime = Math.max(
//                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
//                        + "分钟前";
//            else
//                ftime = hour + "小时前";
//        } else if (days == 1) {
//            ftime = "昨天";
//        } else if (days == 2) {
//            ftime = "前天 ";
//        } else if (days > 2 && days < 31) {
//            ftime = days + "天前";
//        } else if (days >= 31 && days <= 2 * 31) {
//            ftime = "一个月前";
//        } else if (days > 2 * 31 && days <= 3 * 31) {
//            ftime = "2个月前";
//        } else if (days > 3 * 31 && days <= 4 * 31) {
//            ftime = "3个月前";
//        } else {
//            ftime = dateFormater2.get().format(time);
//        }
//        return ftime;
//    }
    public static String friendly_time2(String sdate) {
        String res = "";
        if (isEmpty(sdate)) {
            return "";
        }

        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        String currentData = StringUtils.getDataTime("MM-dd");
        int currentDay = toInt(currentData.substring(3));
        int currentMoth = toInt(currentData.substring(0, 2));

        int sMoth = toInt(sdate.substring(5, 7));
        int sDay = toInt(sdate.substring(8, 10));
        int sYear = toInt(sdate.substring(0, 4));
        @SuppressWarnings("deprecation") Date dt = new Date(sYear, sMoth - 1, sDay - 1);

        if (sDay == currentDay && sMoth == currentMoth) {
            res = "今天 / " + weekDays[getWeekOfDate(new Date())];
        } else if (sDay == currentDay + 1 && sMoth == currentMoth) {
            res = "昨天 / " + weekDays[(getWeekOfDate(new Date()) + 6) % 7];
        } else {
            if (sMoth < 10) {
                res = "0";
            }
            res += sMoth + "/";
            if (sDay < 10) {
                res += "0";
            }
            res += sDay + " / " + weekDays[getWeekOfDate(dt)];
        }

        return res;
    }


    /**
     * 智能格式化
     */
    public static String friendly_time3(Date date) {
        String res = "";

        if (date == null) {
            return res;
        }

        SimpleDateFormat format = dateFormater2.get();

        if (isToday(date.getTime())) {
            format.applyPattern(" HH:mm");
            res = format.format(date);
        } else if (isYesterday(date.getTime())) {
            format.applyPattern("昨天");
            res = format.format(date);
        } else if (isCurrentYear(date.getTime())) {
            format.applyPattern("MM-dd");
            res = format.format(date);
        } else {
            format.applyPattern("yyyy-MM-dd HH:mm");
            res = format.format(date);
        }
        return res;
    }

    /**
     * 智能格式化
     */
    public static String friendly_time4(Date date) {
        String res = "";

        if (date == null) {
            return res;
        }

        SimpleDateFormat format = dateFormater2.get();

        if (isToday(date.getTime())) {
            format.applyPattern("HH:mm");
            res = format.format(date);
        } else if (isYesterday(date.getTime())) {
            format.applyPattern("昨天 HH:mm");
            res = format.format(date);
        } else if (isCurrentYear(date.getTime())) {
            format.applyPattern("MM-dd HH:mm");
            res = format.format(date);
        } else {
            format.applyPattern("yyyy-MM-dd HH:mm");
            res = format.format(date);
        }
        return res;
    }

    /**
     * @return 判断一个时间是不是上午
     */
    public static boolean isMorning(long when) {
        GregorianCalendar time = new GregorianCalendar();
        time.setTimeInMillis(when);

        int hour = time.get(Calendar.HOUR);
        return (hour >= 0) && (hour < 12);
    }

    /**
     * @return 判断一个时间是不是今天
     */
    public static boolean isToday(long when) {
        GregorianCalendar time = new GregorianCalendar();
        time.setTimeInMillis(when);
        int thenYear = time.get(Calendar.YEAR);
        int thenMonth = time.get(Calendar.MONTH);
        int thenMonthDay = time.get(Calendar.DAY_OF_MONTH);

        time.setTimeInMillis(System.currentTimeMillis());
        return (thenYear == time.get(Calendar.YEAR))
            && (thenMonth == time.get(Calendar.MONTH))
            && (thenMonthDay == time.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * @return 判断一个时间是不是昨天
     */
    public static boolean isYesterday(long when) {
        GregorianCalendar time = new GregorianCalendar();
        time.setTimeInMillis(when);

        int thenYear = time.get(Calendar.YEAR);
        int thenMonth = time.get(Calendar.MONTH);
        int thenMonthDay = time.get(Calendar.DAY_OF_MONTH);

        time.setTimeInMillis(System.currentTimeMillis());
        return (thenYear == time.get(Calendar.YEAR))
            && (thenMonth == time.get(Calendar.MONTH))
            && (time.get(Calendar.DAY_OF_MONTH) - thenMonthDay == 1);
    }

    /**
     * @return 判断一个时间是不是今年
     */
    @SuppressWarnings("deprecation")
    public static boolean isCurrentYear(long when) {
        android.text.format.Time time = new android.text.format.Time();
        time.set(when);

        int thenYear = time.year;

        time.set(System.currentTimeMillis());
        return (thenYear == time.year);
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @return 当前日期是星期几
     */
    public static int getWeekOfDate(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return w;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 返回long类型的今天的日期
     */
    public static long getToday() {
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormater2.get().format(cal.getTime());
        curDate = curDate.replace("-", "");
        return Long.parseLong(curDate);
    }

    public static String getCurTimeStr() {
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormater.get().format(cal.getTime());
        return curDate;
    }

    public static String parseDbTime(String date) {
        if (!date.contains("T")) {
            return date;
        }
        String prefix = date.split("T")[0];
        String postfix = date.split("T")[1];
        String[] s1 = prefix.split("-");
        String[] s2 = postfix.split(":");
        StringBuilder builder = new StringBuilder();
        builder.append(s1[0])
            .append("年")
            .append(s1[1])
            .append("月")
            .append(s1[2])
            .append("日")
            .append("  ")
            .append(s2[0])
            .append(":")
            .append(s2[1]);
        return builder.toString();
    }


    public static String parseDbTime2(String date) {
        if (date == null) {
            return "";
        }
        if (!date.contains("T")) {
            return date;
        }
        String prefix = date.split("T")[0];
        String postfix = date.split("T")[1];
        String[] s1 = prefix.split("-");
        String[] s2 = postfix.split(":");
        StringBuilder builder = new StringBuilder();
        builder.append(s1[0])
            .append("/")
            .append(s1[1])
            .append("/")
            .append(s1[2])
            .append("  ")
            .append(s2[0])
            .append(":")
            .append(s2[1])
            .append(":")
            .append(s2[2].substring(0, 2));
        return builder.toString();
    }

    /***
     * 计算两个时间差，返回的是的秒s
     *
     * @author 火蚁 2015-2-9 下午4:50:06
     *
     * @return long
     * @param dete1
     * @param date2
     * @return
     */
    public static long calDateDifferent(String dete1, String date2) {

        long diff = 0;

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = dateFormater.get().parse(dete1);
            d2 = dateFormater.get().parse(date2);

            // 毫秒ms
            diff = d2.getTime() - d1.getTime();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return diff / 1000;
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input)) {
            return true;
        }

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0) {
            return false;
        }
        return emailer.matcher(email).matches();
    }

    /**
     * 判断一个url是否为图片url
     */
    public static boolean isImgUrl(String url) {
        if (url == null || url.trim().length() == 0) {
            return false;
        }
        return IMG_URL.matcher(url).matches();
    }

    /**
     * 判断是否为一个合法的url地址
     */
    public static boolean isUrl(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        return URL.matcher(str).matches();
    }

    public static boolean isZhCn(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        return ZH_CN.matcher(str).matches();
    }

    public static boolean valiteMucName(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        return MUC_NAME.matcher(str).matches();
    }

    public static boolean isDigitOrLetter(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        return NUM_OR_LETTER.matcher(str).matches();
    }

    /**
     * 判断是否为一个手机号码
     */
    public static boolean isMobilePhone(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        return MOBILE_PHONE.matcher(str).matches();
    }

    /**
     * 判断是否为工号
     */
    public static boolean isJobNum(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        return JOB_NUM.matcher(str).matches();
    }

    /**
     * 判断是否为验证码
     */
    public static boolean isIdentifyCode(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        return IDENTIFY_CODE.matcher(str).matches();
    }

    /**
     * 字符串转整数
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null) {
            return 0;
        }
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
     *
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    public static String getString(String s) {
        return s == null ? "" : s;
    }

    /**
     * 将一个InputStream流转换成字符串
     */
    public static String toConvertString(InputStream is) {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        try {
            String line;
            line = read.readLine();
            while (line != null) {
                res.append(line + "<br>");
                line = read.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != isr) {
                    isr.close();
                    isr.close();
                }
                if (null != read) {
                    read.close();
                    read = null;
                }
                if (null != is) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
            }
        }
        return res.toString();
    }

    /***
     * 截取字符串
     *
     * @param start
     *            从那里开始，0算起
     * @param num
     *            截取多少个
     * @param str
     *            截取的字符串
     * @return
     */
    public static String getSubString(int start, int num, String str) {
        if (str == null) {
            return "";
        }
        int leng = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > leng) {
            start = leng;
        }
        if (num < 0) {
            num = 1;
        }
        int end = start + num;
        if (end > leng) {
            end = leng;
        }
        return str.substring(start, end);
    }

    /**
     * 获取当前时间为每年第几周
     */
    public static int getWeekOfYear() {
        return getWeekOfYear(new Date());
    }

    /**
     * 获取当前时间为每年第几周
     */
    public static int getWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        int week = c.get(Calendar.WEEK_OF_YEAR) - 1;
        week = week == 0 ? 52 : week;
        return week > 0 ? week : 1;
    }

    public static int[] getCurrentDate() {
        int[] dateBundle = new int[3];
        String[] temp = getDataTime("yyyy-MM-dd").split("-");

        for (int i = 0; i < 3; i++) {
            try {
                dateBundle[i] = Integer.parseInt(temp[i]);
            } catch (Exception e) {
                dateBundle[i] = 0;
            }
        }
        return dateBundle;
    }

    /**
     * 返回当前系统时间
     */

    public static String getDataTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }


    public static String getFormatTime(String format, Date date) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * 返回相应的数据
     */
    public static String getData(String data) {
        String str = "无";
        if (!isEmpty(data)) {
            str = data;
        }
        return str;
    }

    public static String getData(double data) {
        String str = "0";
        if (data != 0f) {
            str = data + "";
        }
        return str;
    }

    public static String getData(Object data) {
        String str = "0";
        if (data instanceof String) {
            str = (String) data;
        } else if (data instanceof Double) {
            str = (Double) data + "";
        } else if (data instanceof Integer) {
            str = (Integer) data + "";
        }
        return str;
    }

    public static String getFristChar(String name) {
        String str = "";
        if (isEmpty(name)) {
            return str;
        }
        str = name;
        String[] strs = null;
        strs = PinyinHelper.toHanyuPinyinStringArray(str.charAt(0));
        if (strs == null) {
            return str.substring(0, 1).toUpperCase(Locale.ENGLISH);
        }
        return strs[0].substring(0, 1).toUpperCase(Locale.ENGLISH);
    }

    /**
     * 提取汉字的首字母，如果里面含有费中文字符则忽略之；如果全为非中文则返回""。
     */
    public static String getPinYinHeadChar(String zn_str) {
        if (zn_str != null && !zn_str.trim().equalsIgnoreCase("")) {
            char[] strChar = zn_str.toCharArray();
            // 汉语拼音格式输出类
            HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();
            // 输出设置，大小写，音标方式等

            hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);

            hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
            StringBuilder pyStringBuffer = new StringBuilder();
            for (int i = 0; i < strChar.length; i++) {
                char c = strChar[i];
                char pyc = strChar[i];
                if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {//是中文或者a-z或者A-Z转换拼音
                    try {
                        String[] pyStirngArray = PinyinHelper
                            .toHanyuPinyinStringArray(strChar[i], hanYuPinOutputFormat);
                        if (null != pyStirngArray && pyStirngArray[0] != null) {
                            pyc = pyStirngArray[0].charAt(0);
                            pyStringBuffer.append(pyc);
                        }
                    } catch (BadHanyuPinyinOutputFormatCombination e) {
                        e.printStackTrace();
                    }
                }
            }
            return pyStringBuffer.toString();
        }
        return null;
    }

    public static String getFullPinYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        String output = "";
        if (inputString != null && inputString.length() > 0
            && !"null".equals(inputString)) {
            char[] input = inputString.trim().toCharArray();
            try {
                for (int i = 0; i < input.length; i++) {
                    if (Character.toString(input[i]).matches(
                        "[\\u4E00-\\u9FA5]+")) {
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                            input[i], format);
                        output += temp[0];
                    } else {
                        output += Character.toString(input[i]);
                    }
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        } else {
            return "*";
        }
        return output;
    }


    /**
     * ============================================================
     **/
    public static final String EMPTY = "";

    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd hh:mm:ss";
    /**
     * 用于生成文件
     */
    private static final String DEFAULT_FILE_PATTERN = "yyyy-MM-dd-HH-mm-ss";
    private static final double KB = 1024.0;
    private static final double MB = 1048576.0;
    private static final double GB = 1073741824.0;
    public static final SimpleDateFormat DATE_FORMAT_PART = new SimpleDateFormat(
        "HH:mm");

    public static String currentTimeString() {
        return DATE_FORMAT_PART.format(Calendar.getInstance().getTime());
    }

    public static char chatAt(String pinyin, int index) {
        if (pinyin != null && pinyin.length() > 0) {
            return pinyin.charAt(index);
        }
        return ' ';
    }

    /**
     * 获取字符串宽度
     */
    public static float GetTextWidth(String Sentence, float Size) {
        if (isEmpty(Sentence)) {
            return 0;
        }
        TextPaint FontPaint = new TextPaint();
        FontPaint.setTextSize(Size);
        return FontPaint.measureText(Sentence.trim()) + (int) (Size * 0.1); // 留点余地
    }

    /**
     * 格式化日期字符串
     */
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static String formatDate(long date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(date));
    }

    /**
     * 格式化日期字符串
     *
     * @return 例如2011-3-24
     */
    public static String formatDate(Date date) {
        return formatDate(date, DEFAULT_DATE_PATTERN);
    }

    public static String formatDate(long date) {
        return formatDate(new Date(date), DEFAULT_DATE_PATTERN);
    }

    /**
     * 获取当前时间 格式为yyyy-MM-dd 例如2011-07-08
     */
    public static String getDate() {
        return formatDate(new Date(), DEFAULT_DATE_PATTERN);
    }

    /**
     * 生成一个文件名，不含后缀
     */
    public static String createFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_FILE_PATTERN);
        return format.format(date);
    }

    /**
     * 获取当前时间
     */
    public static String getDateTime() {
        return formatDate(new Date(), DEFAULT_DATETIME_PATTERN);
    }

    /**
     * 格式化日期时间字符串
     *
     * @return 例如2011-11-30 16:06:54
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, DEFAULT_DATETIME_PATTERN);
    }

    public static String formatDateTime(long date) {
        return formatDate(new Date(date), DEFAULT_DATETIME_PATTERN);
    }

    /**
     * 格林威时间转换
     */
    public static String formatGMTDate(String gmt) {
        TimeZone timeZoneLondon = TimeZone.getTimeZone(gmt);
        return formatDate(Calendar.getInstance(timeZoneLondon)
            .getTimeInMillis());
    }

    /**
     * 拼接数组
     */
    public static String join(final ArrayList<String> array,
        final String separator) {
        StringBuffer result = new StringBuffer();
        if (array != null && array.size() > 0) {
            for (String str : array) {
                result.append(str);
                result.append(separator);
            }
            result.delete(result.length() - 1, result.length());
        }
        return result.toString();
    }

    public static String join(final Iterator<String> iter,
        final String separator) {
        StringBuffer result = new StringBuffer();
        if (iter != null) {
            while (iter.hasNext()) {
                String key = iter.next();
                result.append(key);
                result.append(separator);
            }
            if (result.length() > 0) {
                result.delete(result.length() - 1, result.length());
            }
        }
        return result.toString();
    }


    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     *
     * @param str
     * @return
     */
    public static String trim(String str) {
        return str == null ? EMPTY : str.trim();
    }

    /**
     * 转换时间显示
     *
     * @param time 毫秒
     */
    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes,
            seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    public static boolean isBlank(String s) {
        return TextUtils.isEmpty(s);
    }

    /**
     * 根据秒速获取时间格式
     */
    public static String gennerTime(int totalSeconds) {
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * 转换文件大小
     */
    public static String generateFileSize(long size) {
        String fileSize;
        if (size < KB) {
            fileSize = size + "B";
        } else if (size < MB) {
            fileSize = String.format("%.1f", size / KB) + "KB";
        } else if (size < GB) {
            fileSize = String.format("%.1f", size / MB) + "MB";
        } else {
            fileSize = String.format("%.1f", size / GB) + "GB";
        }

        return fileSize;
    }

    /**
     * 查找字符串，找到返回，没找到返回空
     */
    public static String findString(String search, String start, String end) {
        int start_len = start.length();
        int start_pos = isEmpty(start) ? 0 : search.indexOf(start);
        if (start_pos > -1) {
            int end_pos = isEmpty(end) ? -1 : search.indexOf(end,
                start_pos + start_len);
            if (end_pos > -1) {
                return search.substring(start_pos + start.length(), end_pos);
            }
        }
        return "";
    }

    /**
     * 截取字符串
     *
     * @param search 待搜索的字符串
     * @param start 起始字符串 例如：<title>
     * @param end 结束字符串 例如：</title>
     */
    public static String substring(String search, String start, String end,
        String defaultValue) {
        int start_len = start.length();
        int start_pos = isEmpty(start) ? 0 : search.indexOf(start);
        if (start_pos > -1) {
            int end_pos = isEmpty(end) ? -1 : search.indexOf(end,
                start_pos + start_len);
            if (end_pos > -1) {
                return search.substring(start_pos + start.length(), end_pos);
            } else {
                return search.substring(start_pos + start.length());
            }
        }
        return defaultValue;
    }

    /**
     * 截取字符串
     *
     * @param search 待搜索的字符串
     * @param start 起始字符串 例如：<title>
     * @param end 结束字符串 例如：</title>
     */
    public static String substring(String search, String start, String end) {
        return substring(search, start, end, "");
    }

    /**
     * 拼接字符串
     */
    public static String concat(String... strs) {
        StringBuffer result = new StringBuffer();
        if (strs != null) {
            for (String str : strs) {
                if (str != null) {
                    result.append(str);
                }
            }
        }
        return result.toString();
    }

    /**
     * Helper function for making null strings safe for comparisons, etc.
     *
     * @return (s == null) ? "" : s;
     */
    public static String makeSafe(String s) {
        return (s == null) ? "" : s;
    }


    private static final DateFormat DATE_TIME;
    private static final DateFormat TIME;

    static {
        DATE_TIME = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
            DateFormat.SHORT);
        TIME = new SimpleDateFormat("H:mm");
    }

    private StringUtils() {
    }

    /**
     * @return Plural string for the given quantity.
     */
    public static String getQuantityString(Resources resources,
        int stringArrayResourceId, long quantity) {
        String[] strings = resources.getStringArray(stringArrayResourceId);
        String lang = resources.getConfiguration().locale.getLanguage();
        if ("ru".equals(lang) && strings.length == 3) {
            quantity = quantity % 100;
            if (quantity >= 20) {
                quantity = quantity % 10;
            }
            if (quantity == 1) {
                return strings[0];
            }
            if (quantity >= 2 && quantity < 5) {
                return strings[1];
            }
            return strings[2];
        } else if (("cs".equals(lang) || "pl".equals(lang))
            && strings.length == 3) {
            if (quantity == 1) {
                return strings[0];
            } else if (quantity >= 2 && quantity <= 4) {
                return strings[1];
            } else {
                return strings[2];
            }
        } else {
            if (quantity == 1) {
                return strings[0];
            } else {
                return strings[1];
            }
        }
    }

    /**
     * Escape input chars to be shown in html.
     */
    public static String escapeHtml(String input) {
        StringBuilder builder = new StringBuilder();
        int pos = 0;
        int len = input.length();
        while (pos < len) {
            int codePoint = Character.codePointAt(input, pos);
            if (codePoint == '"') {
                builder.append("&quot;");
            } else if (codePoint == '&') {
                builder.append("&amp;");
            } else if (codePoint == '<') {
                builder.append("&lt;");
            } else if (codePoint == '>') {
                builder.append("&gt;");
            } else if (codePoint == '\n') {
                builder.append("<br />");
            } else if (codePoint >= 0 && codePoint < 160) {
                builder.append(Character.toChars(codePoint));
            } else {
                builder.append("&#").append(codePoint).append(';');
            }
            pos += Character.charCount(codePoint);
        }
        return builder.toString();
    }

    /**
     * @return String with date and time to be display.
     */
    public static String getDateTimeText(Date timeStamp) {
        synchronized (DATE_TIME) {
            return DATE_TIME.format(timeStamp);
        }
    }

    /**
     * @return String with time or with date and time depend on current time.
     */
    public static String getSmartTimeText(Context context, Date timeStamp) {
        if (timeStamp == null) {
            return "";
        }

        // today
        Calendar midnight = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);

        if (timeStamp.getTime() > midnight.getTimeInMillis()) {
            synchronized (TIME) {
                return timeFormat.format(timeStamp);
            }
        } else {
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
            return dateFormat.format(timeStamp) + " " + timeFormat.format(timeStamp);
        }
    }


    public static String randomString(int length) {
        if (length < 1) {
            return null;
        }
        // Create a char buffer to put random letters and numbers in.
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(numbersAndLetters.length)];
        }
        return new String(randBuffer);
    }


    //add by chenQ
    public static boolean isAlphanumeric(String cs) {
        if (isEmpty(cs)) {
            return false;
        } else {
            int sz = cs.length();

            for (int i = 0; i < sz; ++i) {
                if (!Character.isLetterOrDigit(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static List<Integer> getImageSize(String size) {
        List<Integer> list = null;
        if (!TextUtils.isEmpty(size) && size.contains("x")) {
            String[] arr = size.split("x");
            if (arr != null && arr.length == 2) {
                list = new ArrayList<>();
                if (!TextUtils.isEmpty(arr[0])) {
                    list.add(Integer.parseInt(arr[0]));
                } else {

                }
                if (!TextUtils.isEmpty(arr[1])) {
                    list.add(Integer.parseInt(arr[1]));
                } else {

                }
            }
        }
        return list;
    }

    /*
    * 检查性别,默认为男性
    * 男为true
    * 女为false
    * */
    public static boolean checkGender(String sex) {
        if (!TextUtils.isEmpty(sex)) {
            if (sex.equalsIgnoreCase("女")) {
                return false;
            }
        }
        return true;
    }

    public static String getUserNick(String nick, String usreId) {
        return TextUtils.isEmpty(nick) ? usreId : nick;
    }

    public static boolean matchAllLetter(String ch) {
        if (TextUtils.isEmpty(ch)) {
            return false;
        }
        return EN_LETTER.matcher(ch).matches();
    }

    public static List<String> getUserIds(String s) {
        List<String> list = null;
        if (!TextUtils.isEmpty(s)) {
            list = new ArrayList<>();

            if (s.contains(",")) {
                String[] arr = s.split(",");
                if (arr != null && arr.length > 0) {
                    int len = arr.length;
                    for (int i = 0; i < len; i++) {
                        list.add(arr[i]);
                    }
                }
            } else {
                list.add(s);
            }
        }
        return list;
    }
}
