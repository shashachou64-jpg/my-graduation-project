package com.cjy.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjy.domain.CourseWithStudents;

@Mapper
public interface CourseWithStudentsMapper extends BaseMapper<CourseWithStudents> {
    
}
