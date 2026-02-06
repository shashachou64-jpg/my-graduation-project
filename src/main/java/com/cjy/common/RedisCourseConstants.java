package com.cjy.common;

/**
 * Redis缓存常量定义
 */
public class RedisCourseConstants {
    
    // ===============================
    // 课程相关缓存key
    // ===============================
    
    /**
     * 课程基本信息缓存
     * 完整key格式：course:info:{courseId}
     * 缓存数据：课程的完整信息（包含courseName, collegeId, collegeName, credit, currentNum, maxNum）
     * TTL: 2小时（课程基本信息变更频率低）
     */
    public static final String COURSE_INFO = "course:info:";
    
    /**
     * 课程分组数缓存（频繁变更，单独存储）
     * 完整key格式：course:group:count:{courseId}
     * 缓存数据：该课程的分组总数
     * TTL: 无（强一致性场景，不使用TTL）
     */
    public static final String COURSE_GROUP_COUNT = "course:group:count:";
    
    /**
     * 按学院分类的课程索引
     * 完整key格式：course:college:{collegeId}
     * 缓存数据：ZSet，score为courseId，member为courseId
     * TTL: 2小时
     */
    public static final String COURSE_COLLEGE_INDEX = "course:college:";
    
    /**
     * 课程缓存过期时间（2小时）
     */
    public static final Long COURSE_INFO_TTL = 7200L;
    
    /**
     * 学院课程索引过期时间（2小时）
     */
    public static final Long COURSE_COLLEGE_TTL = 7200L;
    
    // ===============================
    // 缓存更新锁前缀
    // ===============================
    
    /**
     * 课程缓存更新锁前缀
     * 完整key格式：lock:course:update:{courseId}
     * 用途：防止缓存击穿，使用分布式锁
     * TTL: 10秒
     */
    public static final String LOCK_COURSE_UPDATE = "lock:course:update:";
    
    /**
     * 课程分组数更新锁前缀
     * 完整key格式：lock:course:group:{courseId}
     * 用途：保证分组数更新的原子性和一致性
     * TTL: 5秒
     */
    public static final String LOCK_COURSE_GROUP = "lock:course:group:";
    
    /**
     * 锁过期时间
     */
    public static final Long LOCK_TTL = 10L;
}
