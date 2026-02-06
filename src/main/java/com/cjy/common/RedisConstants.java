package com.cjy.common;

public class RedisConstants {
    //===============================
    //教师相关缓存key
    //===============================
    /**
     * 教师信息缓存key（登录时获取的个人信息）
     * 完整key格式：teacher:info:{teacherId}
     * 缓存数据：教师的完整个人信息（包含User、Personal、Position、College等关联信息）
     */
    public static final String TEACHER_INFO = "teacher:info:";

    /**
     * 教师信息缓存过期时间（12小时）
     * 因为教师信息在登录时获取，且很少改动
     * 使用较长的TTL减少数据库访问压力
     */
    public static final Long TEACHER_INFO_TTL = 720L;

    //===============================
    //作业相关缓存key
    //===============================
    public static final String HOMEWORK_INFO = "homework:info:";
    public static final Long HOMEWORK_INFO_TTL = 60L;

    public static final String HOMEWORK_LIST = "homework:list:";
    public static final Long HOMEWORK_LIST_TTL = 720L;



    //===============================
    //学生相关缓存key
    //===============================
    public static final Long STUDENT_DATA_TTL=30L;
    public static final String STUDENT_COURSE_GROUP="student:course:group:";
    
    //===============================
    //互斥锁前缀
    //===============================
    public static final String LOCK_PREFIX="lock:";
}
