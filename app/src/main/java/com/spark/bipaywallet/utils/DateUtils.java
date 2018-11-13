package com.spark.bipaywallet.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/9/1.
 */

public class DateUtils {
    /**
     * 将时间戳转化成固定格式（默认 yyyy-MM-dd HH:mm:ss 当前时间 ）
     */
    public static String getFormatTime(String format, Date date) {
        if (StringUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        if (date == null) {
            date = new Date();
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String formatTime = sdf.format(date);
        return formatTime;
    }

    /**
     * 将固定格式转化成时间戳（默认 yyyy-MM-dd HH:mm:ss）
     */
    public static long getTimeMillis(String format, String dateString) {
        if (StringUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将时间戳转date
     */
    public static Date getDate(String pattern, Long dateString) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String d = format.format(dateString);
        Date date = null;
        try {
            date = format.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * string转date
     *
     * @param strTime
     * @param formatType
     * @return
     */
    public static Date getDateTransformString(String strTime, String formatType) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        try {
            date = formatter.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * long转date
     *
     * @param pattern
     * @param longDate
     * @return
     */
    public static Date getDateTransformLong(String pattern, Long longDate) {
        Date formmatDate = new Date(longDate); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = getFormatTime(pattern, formmatDate); // 把date类型的时间转换为string
        Date date = getDateTransformString(sDateTime, pattern); // 把String类型转换为Date类型
        return date;
    }
}
