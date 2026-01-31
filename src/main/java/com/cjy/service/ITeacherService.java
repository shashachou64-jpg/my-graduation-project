package com.cjy.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cjy.domain.Result;
import com.cjy.domain.Teacher;
import com.cjy.domain.dto.TeacherDTO;
import com.cjy.domain.vo.TeacherVO;

public interface ITeacherService extends IService<Teacher> {
    /**
     * 添加老师
     * @param teacherDTO
     * @return
     */
    Result addTeacher(TeacherDTO teacherDTO);

    /**
     * 获取所有老师信息
     * @return 老师信息列表
     */
    List<TeacherVO> getAllTeacherInfo();

    /**
     * 删除教师
     * @param username
     * @return
     */
    Result deleteTeacher(String username);

    /**
     * 修改教师信息
     * @param teacherDTO
     * @return
     */
    Result editTeacherInfo(TeacherDTO teacherDTO);
}
