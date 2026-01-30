package com.cjy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("course")
public class Course{
    /* 课程ID */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /* 课程名称 */
    private String courseName;
    /* 课程学分 */
    private Integer credit;
    /* 课程最大人数 */
    private Integer maxNum;
    /*学院id */
    private Integer collegeId;
    /* 现有人数 */
    private Integer currentNum;
}