package com.cjy.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("homework")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Homework {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 作业标题
     */
    private String title;
    /**
     * 作业描述
     */
    private String description;
    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 教师id
     */
    private Long teacherId;
    /**
     * 开始时间
     */
    private Date createTime;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 截止时间
     */
    private Date deadline;
    /**
     * 小组id
     */
    private Long groupId;
    /**
     * 状态
     */
    private Long status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 满分分数
     */
    private Long totalScore;
}
