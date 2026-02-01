package com.cjy.utils;

/**
 * 字符串工具类 - 去除空格
 */
public class TrimConverter {

    /**
     * 去除所有空格：前后空格 + 中间连续空格
     */
    public static String trim(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().replaceAll("\\s+", "");
    }
}
