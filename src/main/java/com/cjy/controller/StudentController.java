package com.cjy.controller;

import com.cjy.common.Result;
import com.cjy.domain.Student;
import com.cjy.dto.BatchStudentDTO;
import com.cjy.dto.StudentDTO;
import com.cjy.service.IStudentService;
import com.cjy.vo.StudentTotalVO;
import com.cjy.vo.StudentVO;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/student")
@Validated
public class StudentController {

    @Autowired
    private IStudentService iStudentService;

    /**
     * 添加学生信息
     * @param studentDTO 学生信息
     * @return 结果
     */
    @PutMapping("/addStuInfo")
    public Result addStuInfo(@Valid @RequestBody StudentDTO studentDTO) {
        Result result = iStudentService.addStuInfo(studentDTO);
        return result;
    }

    /**
     * 批量添加学生信息
     * @param batchStudentDTOList 批量学生信息
     * @return 结果
     */
    @PostMapping("/batchAddStuInfo")
    public Result batchAddStuInfo(@Valid @RequestBody List<BatchStudentDTO> batchStudentDTOList) {
        Result result = iStudentService.addBatchStuInfo(batchStudentDTOList);
        return result;
    }   

    /**
     * 获取学生列表
     * @return 学生列表
     */
    @GetMapping("/list")
    public Result getStudentList() {
        List<StudentVO> list = iStudentService.getStudentVOList();
        return Result.success(list);
    }

    /**
     * 按条件搜索学生
     * @param searchType
     * @param keyword
     * @return
     */
    @GetMapping("search")
    public Result searchStudent(@RequestParam String searchType, @RequestParam String keyword) {
        List<StudentVO> studentList = iStudentService.searchStudent(searchType, keyword);
        return Result.success(studentList);
    }

    /**
     * 修改学生信息
     * @param studentDTO
     * @return
     */
    @PutMapping("/updateStuInfo")
    public Result updateStuInfo(@Valid @RequestBody StudentDTO studentDTO) {
        Result result = iStudentService.updateStuInfo(studentDTO);
        return result;
    }

    /**
     * 删除学生
     * @param number
     * @return
     */
    @DeleteMapping("/delete")
    public Result deleteStudent(@RequestParam String number) {
        Result result = iStudentService.deleteStudent(number);
        return result;
    }

    /**
     * 获取学生总数信息
     * @return
     */
    @GetMapping("/total")
    public Result total() {
        StudentTotalVO studentTotalVO = iStudentService.getStudentTotalInfo();
        return Result.success(studentTotalVO);
    }

    /**
     * 根据课程id获取学生列表
     * @param courseId
     * @return
     */
    @GetMapping("/listByCourseId/{courseId}")
    public Result getStudentListByCourseId(@PathVariable Long courseId) {
        List<StudentVO> list = iStudentService.getStudentListByCourseId(courseId);
        return Result.success(list);
    }
}
