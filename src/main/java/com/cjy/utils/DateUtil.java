package com.cjy.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 * 提供标准格式的日期方法
 */
public class DateUtil {

    /** 标准日期时间格式：yyyy-MM-dd HH:mm:ss */
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 日期格式：yyyy-MM-dd */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** 时间格式：HH:mm:ss */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** 中文日期时间格式：yyyy年MM月dd日 HH:mm:ss */
    public static final DateTimeFormatter CHINESE_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");

    /** 中文日期格式：yyyy年MM月dd日 */
    public static final DateTimeFormatter CHINESE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    /** 纯数字格式：yyyyMMddHHmmss */
    public static final DateTimeFormatter NUMERIC_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** 纯数字日期格式：yyyyMMdd */
    public static final DateTimeFormatter NUMERIC_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 获取当前日期时间的标准格式字符串
     * 格式：yyyy-MM-dd HH:mm:ss
     *
     * @return 标准格式日期时间字符串
     */
    public static String getDateTime() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }

    /**
     * 获取当前日期的标准格式字符串
     * 格式：yyyy-MM-dd
     *
     * @return 标准格式日期字符串
     */
    public static String getDate() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }

    /**
     * 获取当前时间的标准格式字符串
     * 格式：HH:mm:ss
     *
     * @return 标准格式时间字符串
     */
    public static String getTime() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }

    /**
     * 获取当前日期时间的中文格式字符串
     * 格式：yyyy年MM月dd日 HH:mm:ss
     *
     * @return 中文格式日期时间字符串
     */
    public static String getChineseDateTime() {
        return LocalDateTime.now().format(CHINESE_DATETIME_FORMATTER);
    }

    /**
     * 获取当前日期的中文格式字符串
     * 格式：yyyy年MM月dd日
     *
     * @return 中文格式日期字符串
     */
    public static String getChineseDate() {
        return LocalDateTime.now().format(CHINESE_DATE_FORMATTER);
    }

    /**
     * 获取当前日期时间的纯数字格式字符串
     * 格式：yyyyMMddHHmmss
     *
     * @return 纯数字格式日期时间字符串
     */
    public static String getNumericDateTime() {
        return LocalDateTime.now().format(NUMERIC_FORMATTER);
    }

    /**
     * 获取当前日期的纯数字格式字符串
     * 格式：yyyyMMdd
     *
     * @return 纯数字格式日期字符串
     */
    public static String getNumericDate() {
        return LocalDateTime.now().format(NUMERIC_DATE_FORMATTER);
    }

    /**
     * 将 LocalDateTime 转换为标准格式字符串
     * 格式：yyyy-MM-dd HH:mm:ss
     *
     * @param dateTime LocalDateTime 对象
     * @return 标准格式日期时间字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * 将 Date 转换为标准格式字符串
     * 格式：yyyy-MM-dd HH:mm:ss
     *
     * @param date Date 对象
     * @return 标准格式日期时间字符串
     */
    public static String formatDateTime(Date date) {
        return date == null ? null : formatDateTime(dateToLocalDateTime(date));
    }

    /**
     * 将 Date 转换为 LocalDateTime
     *
     * @param date Date 对象
     * @return LocalDateTime 对象
     */
    private static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将标准格式字符串解析为 LocalDateTime
     * 格式：yyyy-MM-dd HH:mm:ss
     *
     * @param dateTimeStr 标准格式日期时间字符串
     * @return LocalDateTime 对象
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return dateTimeStr == null ? null : LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    /**
     * 将标准格式字符串解析为 Date
     * 格式：yyyy-MM-dd HH:mm:ss
     *
     * @param dateTimeStr 标准格式日期时间字符串
     * @return Date 对象
     */
    public static Date parseDate(String dateTimeStr) {
        LocalDateTime localDateTime = parseDateTime(dateTimeStr);
        return localDateTime == null ? null : java.sql.Timestamp.valueOf(localDateTime);
    }

    /**
     * 自定义格式转换
     *
     * @param dateTime LocalDateTime 对象
     * @param pattern  日期格式模式
     * @return 自定义格式的日期字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * 自定义格式转换
     *
     * @param date    Date 对象
     * @param pattern 日期格式模式
     * @return 自定义格式的日期字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return format(dateToLocalDateTime(date), pattern);
    }
}
