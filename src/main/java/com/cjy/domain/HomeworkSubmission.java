package com.cjy.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomeworkSubmission {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 作业id
     */
    private Long homeworkId;
    /**
     * 学生id
     */
    private String studentNumber;
    /**
     * 提交状态
     */
    private Long submitStatus;
    /**
     * 提交内容
     */     
    private String submitContent;
    /**
     * 提交时间
     */
    private Date submitTime;
    /**
     * 评分时间
     */
    private Date gradeTime;
    /**
     * 评分
     */
    private Long score;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

}
