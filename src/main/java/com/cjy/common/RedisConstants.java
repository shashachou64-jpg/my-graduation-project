package com.cjy.common;

public class RedisConstants {
    // ===============================
    // 教师相关缓存key
    // ===============================
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

    /**
     * 教师作业集合缓存Key（Set结构）
     * 完整格式：teacher:homework:set:{teacherId}
     * 存储：该教师发布的所有作业ID
     */
    public static final String TEACHER_HOMEWORK_SET = "teacher:homework:set:";

    /**
     * 教师作业列表缓存Key（List结构）
     * 完整格式：teacher:homework:list:{teacherId}
     * 存储：该教师发布的所有作业对象（用于快速返回）
     */
    public static final String TEACHER_HOMEWORK_LIST = "teacher:homework:list:";

    // ===============================
    // 作业相关缓存key
    // ===============================
    public static final String HOMEWORK_INFO = "homework:info:";
    public static final Long HOMEWORK_INFO_TTL = 60L;

    public static final String HOMEWORK_LIST = "homework:list:";
    public static final Long HOMEWORK_LIST_TTL = 720L;

    /**
     * 学生作业提交缓存Key（Hash结构）分两段
     * 完整格式：homework:submission:{homeworkId}:{studentId}
     */
    public static final String HOMEWORK_SUBMISSION = "homework:submission:";

    /**
     * 学生作业提交缓存默认过期时间（分钟）
     */
    public static final Long HOMEWORK_SUBMISSION_TTL = 60L;

    /**
     * 作业学生名单缓存Key（Set结构）
     * 完整格式：homework:submission:students:{homeworkId}
     * 存储：该作业的所有学生学号
     */
    public static final String HOMEWORK_SUBMISSION_STUDENTS = "homework:submission:students:";

    /**
     * 作业学生名单缓存默认过期时间（分钟）
     */
    public static final Long HOMEWORK_SUBMISSION_STUDENTS_TTL = 60L;

    // ===============================
    // 学生相关缓存key
    // ===============================
    public static final Long STUDENT_DATA_TTL = 30L;
    public static final String STUDENT_COURSE_GROUP = "student:course:group:";

    // ===============================
    // 互斥锁前缀
    // ===============================
    public static final String LOCK_PREFIX = "lock:";
}
