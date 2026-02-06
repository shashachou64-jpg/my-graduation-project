package com.cjy.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("student_with_group")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentWithGroup {
    /**
     * 学生小组id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 学生学号
     */
    private String studentNumber;
    /**
     * 小组id
     */ 
    private Long groupId;
}
