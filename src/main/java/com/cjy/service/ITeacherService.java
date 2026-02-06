package com.cjy.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cjy.common.Result;
import com.cjy.domain.Teacher;
import com.cjy.dto.EditTeacherDTO;
import com.cjy.dto.TeacherDTO;
import com.cjy.vo.TeacherTotalVO;
import com.cjy.vo.TeacherVO;

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
    Result editTeacherInfo(EditTeacherDTO editTeacherDTO);

    /**
     * 批量添加教师信息
     * @param file
     * @return 添加成功的教师数量
     */
    Result batchAddTeaInfo(MultipartFile file);

    /**
     * 获取老师总数信息
     * @return
     */
    TeacherTotalVO getTeacherTotalInfo();

    Result getTeacherInfo(Long id);
}
