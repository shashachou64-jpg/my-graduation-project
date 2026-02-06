package com.cjy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjy.domain.Course;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 查询所有学院课程数
     * @return
     */
    @Select("SELECT c.name AS collegeName, COUNT(co.id) AS courseCount " +
            "FROM college c LEFT JOIN course co ON c.id = co.college_id " +
            "GROUP BY c.id, c.name")
    List<Map<String, Object>> getCollegeCourseList();

}
