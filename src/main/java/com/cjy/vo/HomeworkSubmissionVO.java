package com.cjy.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomeworkSubmissionVO {
    /**
     * 作业id
     */
    private Long homeworkId;
    /**
     * 学生学号
     */
    private String studentNumber;
    /**
     * 学生姓名
     */
    private String studentName;
    /**
     * 提交状态
     */
    private Long submitStatus;
    /**
     * 提交时间
     */
    private String submitTime;
    /**
     * 分数
     */
    private Long score;
}
