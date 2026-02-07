package com.cjy.vo;

import java.util.Date;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomeworkVO {
    /**
     * 作业ID
     */
    private Long id;
    /**
     * 作业标题
     */
    private String title;
    /**
     * 课程名称
     */
    private String courseName;
    /**
     * 教师名称
     */
    private String teacherName;
    /**
     * 小组名称
     */
    private String groupName;
    /**
     * 状态
     */
    private Long status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 提交率
     */
    private Double submitRate;
    /**
     * 发布学生数量
     */
    private Long publishStudentCount;
    /**
     * 所属课程班级人数
     */
    private Long courseClassStudentCount;
    /**
     * 总分数
     */
    private Long totalScore;
    // ========== 格式化时间字段 ==========
    /**
     * 开始时间（格式化字符串 yyyyMMdd HH:mm:ss）
     */
    private String startTimeStr;
    /**
     * 截止时间（格式化字符串 yyyyMMdd HH:mm:ss）
     */
    private String deadlineStr;
    /**
     * 创建时间（格式化字符串 yyyyMMdd HH:mm:ss）
     */
    private String createTimeStr;
    /**
     * 更新时间（格式化字符串 yyyyMMdd HH:mm:ss）
     */
    private String updateTimeStr;
}
