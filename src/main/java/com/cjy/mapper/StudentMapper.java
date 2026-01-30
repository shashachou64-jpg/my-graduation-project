package com.cjy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjy.domain.Student;
import com.cjy.domain.vo.StudentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 获取学生列表
     *
     * @return 学生列表
     */
    @Select("select s.number, s.name as stu_name, c.name as college_name, m.name as major_name, cl.name as class_name, s.year, s.gender from student s, college c, major m, class cl where s.college_id = c.id and s.major_id = m.id and s.class_id = cl.id")
    @Results({
            @Result(property = "number", column = "number"),
            @Result(property = "name", column = "stu_name"),
            @Result(property = "collegeName", column = "college_name"),
            @Result(property = "majorName", column = "major_name"),
            @Result(property = "className", column = "class_name"),
            @Result(property = "year", column = "year"),
            @Result(property = "gender", column = "gender")
    })
    List<StudentVO> selectStudentVOList();

}
