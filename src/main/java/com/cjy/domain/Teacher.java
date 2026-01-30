package com.cjy.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("teacher")
public class Teacher {
    private String id;
    private String name;
    private String collegeId;
    private String collegeName;
    private String gender;
    private String positionName;
    private String positionId;
}


