package com.cjy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cjy.domain.Result;
import com.cjy.domain.Student;
import com.cjy.domain.dto.BatchStudentDTO;
import com.cjy.domain.dto.StudentDTO;
import com.cjy.domain.vo.StudentVO;

import java.util.List;

public interface IStudentService extends IService<Student>{
    /**
     * 添加单个学生
     * @param studentDTO 学生信息
     * @return 结果
     */
    Result addStuInfo(StudentDTO studentDTO);

    /**
     * 获取学生列表
     * @return 学生列表
     */
    List<StudentVO> getStudentVOList();

    /**
     * 批量添加学生信息
     * @param batchStudentDTOList 批量学生信息
     * @return 结果
     */
    Result addBatchStuInfo(List<BatchStudentDTO> batchStudentDTOList);

    /**
     * 按条件搜索学生
     * @param searchType
     * @param keyword
     * @return
     */
    List<StudentVO> searchStudent(String searchType, String keyword);

    /**
     * 修改学生信息
     * @param studentDTO 学生信息
     * @return 结果
     */
    Result updateStuInfo(StudentDTO studentDTO);

    /**
     * 删除学生
     * @param number
     * @return
     */
    Result deleteStudent(String number);
}
