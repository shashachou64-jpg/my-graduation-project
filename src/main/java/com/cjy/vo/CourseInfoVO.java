package com.cjy.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程信息VO - 包含分组数
 * 用于返回给前端
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseInfoVO {
    
    /**
     * 课程ID
     */
    private Long id;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 学院名称
     */
    private String collegeName;
    
    /**
     * 学分
     */
    private Integer credit;
    
    /**
     * 最大人数
     */
    private Integer maxNum;
    
    /**
     * 当前人数
     */
    private Integer currentNum;
    
    /**
     * 分组数（从Redis单独获取）
     */
    private Long groupCount;
}
