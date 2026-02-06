package com.cjy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjy.domain.Teacher;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    /**
     * 查询所有学院名称和对应的学院人数
     * @return 学院名称和对应的学院人数
     */
    @Select("select c.name as collegeName,count(t.id) as teacherCount from college c left join teacher t on c.id = t.college_id group by c.id")
    List<Map<String,Object>> getCollegeTeacherList();
}
