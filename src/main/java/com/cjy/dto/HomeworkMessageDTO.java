package com.cjy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 作业消息DTO
 * 用于在教师和学生之间传递作业信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkMessageDTO implements Serializable {
    /**
     * 表示教师发布消息
     */
    private static final long serialVersionUID = 1L;  

    /**
     * 作业ID
     */
    private Long homeworkId;

    /**
     * 作业标题
     */
    private String title;

    /**
     * 作业描述
     */
    private String description;

    /**
     * 课程ID（通过ID查询名称，不冗余存储）
     */
    private Long courseId;

    /**
     * 教师ID（通过ID查询名称，不冗余存储）
     */
    private Long teacherId;

    /**
     * 小组ID（通过ID查询名称，不冗余存储）
     */
    private Long groupId;

    /**
     * 学生学号列表（批量通知用）
     */
    private List<String> studentNumbers;

    /**
     * 学生学号（单个通知用）
     */
    private String studentNumber;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 截止时间
     */
    private Date deadline;

    /**
     * 消息创建时间
     */
    private Date createTime;

    /**
     * 备注
     */
    private String remark;
}
