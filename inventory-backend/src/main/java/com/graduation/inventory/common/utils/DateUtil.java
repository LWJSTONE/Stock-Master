package com.graduation.inventory.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具类
 *
 * @author graduation
 * @version 1.0.0
 */
public class DateUtil {

    /**
     * 日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 时间格式
     */
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间格式（带毫秒）
     */
    public static final String TIME_FORMAT_MS = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 中国日期格式
     */
    public static final String CN_DATE_FORMAT = "yyyy年MM月dd日";

    /**
     * 中国时间格式
     */
    public static final String CN_TIME_FORMAT = "yyyy年MM月dd日 HH时mm分ss秒";

    private DateUtil() {
    }

    /**
     * 格式化日期
     *
     * @param date    日期
     * @param pattern 格式
     * @return 格式化后的字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * 使用默认日期格式格式化
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatDate(Date date) {
        return format(date, DATE_FORMAT);
    }

    /**
     * 使用默认时间格式格式化
     *
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatDateTime(Date date) {
        return format(date, TIME_FORMAT);
    }

    /**
     * 解析日期字符串
     *
     * @param dateStr 日期字符串
     * @param pattern 格式
     * @return 日期
     */
    public static Date parse(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期格式解析失败: " + dateStr, e);
        }
    }

    /**
     * 使用默认日期格式解析
     *
     * @param dateStr 日期字符串
     * @return 日期
     */
    public static Date parseDate(String dateStr) {
        return parse(dateStr, DATE_FORMAT);
    }

    /**
     * 使用默认时间格式解析
     *
     * @param dateStr 日期字符串
     * @return 日期
     */
    public static Date parseDateTime(String dateStr) {
        return parse(dateStr, TIME_FORMAT);
    }

    /**
     * 获取当前日期
     *
     * @return 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期字符串（默认格式）
     *
     * @return 当前日期字符串
     */
    public static String getNowDateStr() {
        return format(getNowDate(), DATE_FORMAT);
    }

    /**
     * 获取当前时间字符串（默认格式）
     *
     * @return 当前时间字符串
     */
    public static String getNowTimeStr() {
        return format(getNowDate(), TIME_FORMAT);
    }

    /**
     * 计算两个日期的差值
     *
     * @param endDate   结束日期
     * @param startDate 开始日期
     * @return 差值（毫秒）
     */
    public static long getDateDiff(Date endDate, Date startDate) {
        if (endDate == null || startDate == null) {
            return 0;
        }
        return endDate.getTime() - startDate.getTime();
    }

    /**
     * 计算两个日期的差值（天数）
     *
     * @param endDate   结束日期
     * @param startDate 开始日期
     * @return 差值（天数）
     */
    public static long getDateDiffInDays(Date endDate, Date startDate) {
        long diff = getDateDiff(endDate, startDate);
        return diff / (24 * 60 * 60 * 1000);
    }

    /**
     * 计算两个日期的差值（小时）
     *
     * @param endDate   结束日期
     * @param startDate 开始日期
     * @return 差值（小时）
     */
    public static long getDateDiffInHours(Date endDate, Date startDate) {
        long diff = getDateDiff(endDate, startDate);
        return diff / (60 * 60 * 1000);
    }

    /**
     * 计算两个日期的差值（分钟）
     *
     * @param endDate   结束日期
     * @param startDate 开始日期
     * @return 差值（分钟）
     */
    public static long getDateDiffInMinutes(Date endDate, Date startDate) {
        long diff = getDateDiff(endDate, startDate);
        return diff / (60 * 1000);
    }

    /**
     * 获取日期差值描述
     *
     * @param endDate   结束日期
     * @param startDate 开始日期
     * @return 差值描述
     */
    public static String getDatePoor(Date endDate, Date startDate) {
        if (endDate == null || startDate == null) {
            return "";
        }

        long diff = getDateDiff(endDate, startDate);
        if (diff < 0) {
            diff = -diff;
        }

        long days = diff / (24 * 60 * 60 * 1000);
        long hours = (diff % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (diff % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (diff % (60 * 1000)) / 1000;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("天");
        }
        if (hours > 0) {
            sb.append(hours).append("小时");
        }
        if (minutes > 0) {
            sb.append(minutes).append("分钟");
        }
        if (seconds > 0 && days == 0) {
            sb.append(seconds).append("秒");
        }

        return sb.length() > 0 ? sb.toString() : "0秒";
    }

    /**
     * 判断日期是否在指定范围内
     *
     * @param date      日期
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 是否在范围内
     */
    public static boolean isBetween(Date date, Date startDate, Date endDate) {
        if (date == null || startDate == null || endDate == null) {
            return false;
        }
        return date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0;
    }

    /**
     * 判断是否为今天
     *
     * @param date 日期
     * @return 是否为今天
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        String todayStr = getNowDateStr();
        String dateStr = format(date, DATE_FORMAT);
        return todayStr.equals(dateStr);
    }
}
