package com.cjy.service;

import java.util.List;

import com.cjy.domain.Result;
import com.cjy.domain.Teacher;

public interface TeacherService {
    // 添加老师
    Result addTeacher(Teacher teacher);

    // 查找所有老师
    List<Teacher> findTeacherList();

    Result deleteTeacher(Teacher teacher);

    Result updateTeacher(Teacher teacher);
}
