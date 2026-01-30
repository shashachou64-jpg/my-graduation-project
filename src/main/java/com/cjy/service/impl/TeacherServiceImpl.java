package com.cjy.service.impl;

import com.cjy.domain.Result;
import com.cjy.domain.Teacher;
import com.cjy.mapper.TeacherMapper;
import com.cjy.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public Result addTeacher(Teacher teacher) {
        /*
         * 1.判断返回的信息是否有空
         * 2.空了返回错误信息
         * 3.没有空，添加老师
         * */
        //判断返回的信息是否有空
        if (teacher.getCollegeId() == null || teacher.getPositionId() == null || teacher.getName() == null) {
            return Result.error("信息不能为空");
        }
        if (teacher.getName().isEmpty()) {
            return Result.error("姓名不能为空");
        }
        if (teacher.getCollegeId().isEmpty()) {
            return Result.error("学院不能为空");
        }
        if (teacher.getPositionId().isEmpty()) {
            return Result.error("职位不能为空");
        }
        //判断性别是否为空
        if (teacher.getGender().isEmpty()) {
            return Result.error("性别不能为空");
        }
        //添加老师
        int rows = teacherMapper.addTeacher(teacher);
        if (rows == 0) {
            return Result.error("添加失败，请稍后重试");
        } else {
            return Result.success("添加成功");
        }

    }

    @Transactional
    @Override
    public List<Teacher> findTeacherList() {
        return teacherMapper.findAllTeacherList();
    }

    @Transactional
    @Override
    public Result deleteTeacher(Teacher teacher) {
        //判断返回的信息是否有空
        if (teacher.getId() == null) {
            return Result.error("信息不能为空");
        }
        if (teacher.getId().isEmpty()) {
            return Result.error("id不能为空");
        }
        //删除老师
        int rows = teacherMapper.deleteTeacher(teacher);
        if (rows == 0) {
            return Result.error("删除失败，请稍后重试");
        } else {
            return Result.success("删除成功");
        }
    }

    @Override
    public Result updateTeacher(Teacher teacher) {
        //判断返回的信息是否有空
        if (teacher.getId() == null) {
            return Result.error("信息不能为空");
        }
        if (teacher.getId().isEmpty()) {
            return Result.error("id不能为空");
        }
        if (teacher.getName().isEmpty()) {
            return Result.error("姓名不能为空");
        }
        if (teacher.getCollegeId().isEmpty()) {
            return Result.error("学院不能为空");
        }
        if (teacher.getPositionId().isEmpty()) {
            return Result.error("职位不能为空");
        }
        if (teacher.getGender().isEmpty()) {
            return Result.error("性别不能为空");
        }
        //更新老师
        int rows = teacherMapper.updateTeacher(teacher);
        if (rows == 0) {
            return Result.error("修改失败，请稍后重试");
        } else {
            return Result.success("修改成功");
        }
    }
}
