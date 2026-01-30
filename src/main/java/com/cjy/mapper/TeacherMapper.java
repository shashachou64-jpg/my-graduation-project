package com.cjy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cjy.domain.Teacher;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    /**
     * 查询所有老师
     * @return 老师列表
     */
    @Select("select t.id,t.name,t.college_id,t.gender,t.position_id,p.name as positionName,c.name as collegeName from teacher t left join teacher_position p on t.position_id = p.id left join college c on t.college_id = c.id")
    List<Teacher> findAllTeacherList();

    /**
     * 添加老师
     * @param teacher
     * @return
     */
    @Insert("insert into teacher (name,college_id,gender,position_id) values (#{name},#{collegeId},#{gender},#{positionId})")
    int addTeacher(Teacher teacher);


    /**
     * 删除老师
     * @param teacher
     * @return
     */
    @Delete("delete from teacher where id = #{id}")
    int deleteTeacher(Teacher teacher);

    /**
     * 修改老师
     * @param teacher
     * @return
     */
    @Update("update teacher set name=#{name},college_id=#{collegeId},gender=#{gender},position_id=#{positionId} where id=#{id}")
    int updateTeacher(Teacher teacher);
}
