package com.cjy.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cjy.common.Result;
import com.cjy.domain.Course;
import com.cjy.dto.CourseDTO;
import com.cjy.dto.CourseWithStudentsDTO;
import com.cjy.service.ICourseService;
import com.cjy.utils.JwtUtil;
import com.cjy.vo.CourseInfoVO;
import com.cjy.vo.CourseTotalVO;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private ICourseService iCourseService;

    @PostMapping("/addCourseWithStudents")
    public Result addCourseWithStudents(@RequestBody CourseWithStudentsDTO request) {
        /**
         * 逻辑判断
         */
        // 验证请求对象
        if (request == null || request.getCourse() == null) {
            return Result.error("请求数据不能为空");
        }
        System.out.println(request);
        Course course = request.getCourse();

        // 验证课程信息
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            return Result.error("课程名称不能为空");
        }
        if (course.getMaxNum() == null || course.getMaxNum() <= 0) {
            return Result.error("最大人数不能为空且必须大于0");
        }
        if (course.getCredit() == null || course.getCredit() <= 0) {
            return Result.error("学分不能为空且必须大于0");
        }
        if (course.getCollegeId() == null || course.getCollegeId() <= 0) {
            return Result.error("学院ID不能为空且必须有效");
        }
        if (request.getStudentsNumberList() == null || request.getStudentsNumberList().isEmpty()) {
            return Result.error("学生学号列表不能为空");
        }

        CourseWithStudentsDTO result = iCourseService.addCourseWithStudents(request);
        if (result == null) {
            return Result.error("添加课程失败，请检查数据是否正确");
        }
        return Result.success(result, "添加课程成功");
    }

    /**
     * 获取课程列表
     * @param courseName
     * @param collegeName
     * @return
     */
    @GetMapping("/ListCourse")
    public Result ListCourse(@RequestParam(required = false) String courseName,
            @RequestParam(required = false) String collegeName) {
        List<CourseDTO> courseList = iCourseService.ListCourse(courseName, collegeName);
        return Result.success(courseList, "获取课程列表成功");
    }

    /**
     * 按条件搜索课程
     * @param SearchType
     * @param keyword
     * @return
     */
    @GetMapping("/search")
    public Result search(@RequestParam String SearchType,
        @RequestParam(required = false) String keyword){
            //如果关键词为空，返回所有课程
            if(keyword==null || keyword.trim().isEmpty()){
                List<CourseDTO> courseList = iCourseService.ListCourse(null, null);
                return Result.success(courseList, "搜索所有课程列表成功");
            }
            //按条件搜索
            List<CourseDTO> courseList = iCourseService.searchCourse(SearchType, keyword);
            return Result.success(courseList, "按条件搜索课程列表成功");
        }

    /**
     * 删除课程
     * @param courseId
     * @return
     */
    @DeleteMapping("/delete")
    public Result deleteCourse(@RequestParam Integer courseId){
        if (courseId==null || courseId<=0) {
            return Result.error("删除课程失败");
        }
        boolean result = iCourseService.deleteCourseByCourseId(courseId);
        if (result) {
            return Result.success("删除课程成功");
        } else {
            return Result.error("删除课程失败");
        }
    }

    /**
     * 获取课程详情
     * @param courseId
     * @return
     */
    @GetMapping("/getCourseById")
    public Result getCourseById(@RequestParam Integer courseId) {
        if(courseId==null){
            return Result.error("课程ID获取失败，请检查课程ID是否有效");
        }
        Course course = iCourseService.getCourseById(courseId);
        if(course==null){
            return Result.error("课程不存在，请检查课程ID是否有效");
        }
        return Result.success(course, "获取课程成功");
    }

    /**
     * 更新课程信息
     * @param course
     * @return
     */
    @PostMapping("/updatingCourseInfo")
    public Result updatingCourseInfo(@RequestBody Course course) {
        if(course==null){
            return Result.error("课程信息不能为空");
        }
        if(course.getId()==null){
            return Result.error("课程ID获取失败");
        }
        if(course.getCourseName()==null||course.getCourseName().trim().isEmpty()){
            return Result.error("课程名称不能为空");
        }
        if(course.getCollegeId()==null){
            return Result.error("学院ID获取失败");
        }
        if(course.getCredit()==null){
            return Result.error("学分不能为空");
        }

        boolean result = iCourseService.updatingCourseInfo(course);
        if(result){
            return Result.success("更新课程信息成功");
        } else {
            return Result.error("更新课程信息失败");
        }

    }
    
    /**
     * 获取课程总数信息
     * @return
     */
    @GetMapping("/total")
    public Result total() {
        CourseTotalVO courseTotalVO = iCourseService.getCourseTotalInfo();
        return Result.success(courseTotalVO);
    }


    /**
     * 获取教师所教课程列表
     * @param token
     * @return
     */
    @GetMapping("/listByTeacherId/{token}")
    public Result list(@PathVariable String token) {
        log.info("Token: {}", token);
        log.info("================================================");
        
        // 2. JWT解析出username (用户名格式如 "20230001")
        String username = JwtUtil.getUsername(token);
        if (username == null || username.length() < 4) {
            return Result.error("登录过期，请重新登录");
        }
        log.info("Username: {}", username);
        log.info("================================================");
        // 3. 取后面4位作为教师id
        String idStr = username.substring(username.length() - 4);
        
        // 4. 去除前导0
        idStr = idStr.replaceFirst("^0+", "");
        if (idStr.isEmpty()) {
            idStr = "0";
        }
        
        // 5. 转换为Long得到教师id
        Long teacherId = Long.parseLong(idStr);
        log.info("{}", teacherId);
        List<CourseInfoVO> voList = iCourseService.getCourseListByTeacherId(teacherId);
        return Result.success(voList);
    }
}
