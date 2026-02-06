package com.cjy.controller;

import com.cjy.common.Result;
import com.cjy.domain.Teacher;
import com.cjy.dto.EditTeacherDTO;
import com.cjy.dto.TeacherDTO;
import com.cjy.service.ITeacherService;
import com.cjy.utils.JwtUtil;
import com.cjy.vo.TeacherTotalVO;
import com.cjy.vo.TeacherVO;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.inOrder;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/teacher")
@Validated
public class TeacherController {
    @Autowired
    private ITeacherService iTeacherService;

    /**
     * 添加老师
     * @param teacherDTO 老师信息
     * @param teacher
     * @return
     */
    @PostMapping("/add")
    public Result add(@Valid @RequestBody TeacherDTO teacherDTO) {
        // 验证请求对象
        if (teacherDTO == null) {
            return Result.error("请求数据不能为空");
        }
        // 添加老师
        return iTeacherService.addTeacher(teacherDTO);
    }

    /**
     * 获取老师列表
     * @return
     */
    @GetMapping("/list")
    public Result list() {
        List<TeacherVO> teacherList = iTeacherService.getAllTeacherInfo();
        return Result.success(teacherList);
    }

    /**
     * 删除老师
     * @param username 老师工号
     * @return
     */
    @DeleteMapping("/delete")
    public Result delete(@RequestParam String username) {
        if (username == null) {
            return Result.error("该教师不存在，无法删除");
        }
        return iTeacherService.deleteTeacher(username);
    }

    /**
     * 修改老师信息
     * @param editTeacherDTO 老师信息
     * @return
     */
    @PutMapping("/update")
    public Result update(@Valid @RequestBody EditTeacherDTO editTeacherDTO) {
        return iTeacherService.editTeacherInfo(editTeacherDTO);
    }

    /**
     * 批量添加老师信息
     * @param file 文件
     * @return
     */
    @PostMapping("/batchAddTeaInfo")
    public Result batchAddTeaInfo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        try {
            Result result = iTeacherService.batchAddTeaInfo(file);
            return result;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取老师总数信息
     * @return
     */
    @GetMapping("/total")
    public Result total() {
        TeacherTotalVO teacherTotalVO = iTeacherService.getTeacherTotalInfo();
        return Result.success(teacherTotalVO);
    }

    // ===================================================
    // 教师仪表盘业务
    // ===================================================
    
    /**
     * 从请求头获取token解析成教师id
     * 然后查询redis，存在则返回，不存在则查询数据库，并缓存到redis
     * @param token
     * @return
     */
    @GetMapping("teacherInfo")
    public Result teacherInfo(@RequestHeader("Authorization") String token) {
        // 1. 去掉 "Bearer " 前缀 (Bearer 后面有个空格，共7个字符)
        token = token.substring(7);
        
        // 2. JWT解析出username (用户名格式如 "20230001")
        String username = JwtUtil.getUsername(token);
        if (username == null || username.length() < 4) {
            return Result.error("登录过期，请重新登录");
        }
        
        // 3. 取后面4位作为教师id
        String idStr = username.substring(username.length() - 4);
        
        // 4. 去除前导0
        idStr = idStr.replaceFirst("^0+", "");
        if (idStr.isEmpty()) {
            idStr = "0";
        }
        
        // 5. 转换为Long得到教师id
        Long teacherId = Long.parseLong(idStr);
        
        Result result = iTeacherService.getTeacherInfo(teacherId);
        return result;
    }
    
}
