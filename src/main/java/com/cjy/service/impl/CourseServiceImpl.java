package com.cjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjy.domain.College;
import com.cjy.domain.Course;
import com.cjy.domain.CourseWithStudents;
import com.cjy.domain.Student;
import com.cjy.domain.dto.CourseDTO;
import com.cjy.domain.dto.CourseWithStudentsDTO;
import com.cjy.mapper.CollegeMapper;
import com.cjy.mapper.CourseMapper;
import com.cjy.mapper.CourseWithStudentsMapper;
import com.cjy.mapper.StudentMapper;
import com.cjy.service.ICourseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Autowired
    private CourseWithStudentsMapper courseWithStudentsMapper;

    @Autowired
    private CollegeMapper collegeMapper;

    @Autowired
    private StudentMapper studentMapper;

    /**
     * 定义方法将Course对象转换为CourseDTO对象
     */
    private List<CourseDTO> convertToDTO(List<Course> courseList){
        // 构建学号到学生姓名
        Map<String, String> studentMap = new HashMap<>();
        List<Student> allStudents = studentMapper.selectList(null);
        for (Student student : allStudents) {
            studentMap.put(student.getNumber(), student.getName());
        }
        
        // 构建课程DTO
        List<CourseDTO> courseDTOList = new ArrayList<>();
        for(Course course : courseList){
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setId(course.getId());
            courseDTO.setCourseName(course.getCourseName());
            courseDTO.setCredit(course.getCredit());
            courseDTO.setMaxNum(course.getMaxNum());
            courseDTO.setCurrentNum(course.getCurrentNum());
            courseDTO.setCollegeName(collegeMapper.selectById(course.getCollegeId()).getName());
            
            //获取已选学生列表
            QueryWrapper<CourseWithStudents> cwsWrapper = new QueryWrapper<>();
            cwsWrapper.eq("course_id", course.getId());
            List<CourseWithStudents> cwsList = courseWithStudentsMapper.selectList(cwsWrapper);

            List<CourseDTO.StudentInfo> studentInfoList = new ArrayList<>();
            for (CourseWithStudents cws : cwsList) {
                CourseDTO.StudentInfo info = new CourseDTO.StudentInfo();
                info.setNumber(cws.getStudentNumber());
                info.setName(studentMap.get(cws.getStudentNumber()));
                studentInfoList.add(info);
            }
            courseDTO.setStudentInfoList(studentInfoList);
            courseDTOList.add(courseDTO);
        }

        return courseDTOList;


    }

    /**
     * 添加课程及学生选课关系
     * 
     * @param request
     * @return
     */
    @Override
    public CourseWithStudentsDTO addCourseWithStudents(CourseWithStudentsDTO request) {
        try {
            Course course = request.getCourse();
            List<String> studentsNumbers = request.getStudentsNumberList();

            // 保存课程信息
            boolean isSuccess = this.save(course);
            if (!isSuccess) {
                throw new RuntimeException("课程保存失败");
            }

            // 批量添加学生选课关系
            if (studentsNumbers != null && !studentsNumbers.isEmpty()) {
                List<CourseWithStudents> courseWithStudentsList = new ArrayList<>();

                for (String studentNumber : studentsNumbers) {
                    QueryWrapper<CourseWithStudents> checkWrapper = new QueryWrapper<>();
                    checkWrapper.eq("course_id", course.getId())
                            .eq("student_number", studentNumber);

                    CourseWithStudents existing = courseWithStudentsMapper.selectOne(checkWrapper);
                    if (existing == null) {
                        CourseWithStudents courseWithStudents = new CourseWithStudents();
                        courseWithStudents.setCourseId(course.getId());
                        courseWithStudents.setStudentNumber(studentNumber);
                        courseWithStudentsList.add(courseWithStudents);
                    }

                }
                if (!courseWithStudentsList.isEmpty()) {
                    for (CourseWithStudents sc : courseWithStudentsList) {
                        courseWithStudentsMapper.insert(sc);
                    }
                }

            }
            // 返回给前端
            String collegeName = collegeMapper.selectById(course.getCollegeId()).getName();
            CourseWithStudentsDTO response = new CourseWithStudentsDTO();
            response.setCourse(course);
            response.setCollegeName(collegeName);
            response.setStudentsNumberList(studentsNumbers);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("添加课程失败: " + e.getMessage(), e);

        }
    }

    /**
     * 查询课程列表
     * 
     * @param courseName  课程名称
     * @param collegeName 学院名称
     * @return 课程列表
     */
    @Override
    public List<CourseDTO> ListCourse(String courseName, String collegeName) {
        // 查询所有课程
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();

        // 根据课程名称模糊查询
        if (courseName != null && !courseName.trim().isEmpty()) {
            queryWrapper.like(Course::getCourseName, courseName.trim());
        }

        queryWrapper.orderByDesc(Course::getCourseName);

        List<Course> courseList = this.list(queryWrapper);

        return convertToDTO(courseList);
    }

    @Override
    public List<CourseDTO> searchCourse(String SearchType, String keyword) {
        // 根据搜索类型和关键词进行搜索
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        if (SearchType.equals("name")) {
            queryWrapper.like(Course::getCourseName, keyword).orderByDesc(Course::getCourseName);

            List<Course> courseList = this.list(queryWrapper);
            return convertToDTO(courseList);
        } else {
            // 根据学院名称模糊查询学院id
            QueryWrapper<College> collegeWrapper = new QueryWrapper<>();
            collegeWrapper.like("name", keyword);
            // 根据学院id查询课程
            List<College> collegeList = collegeMapper.selectList(collegeWrapper);
            if (collegeList != null && !collegeList.isEmpty()) {
                queryWrapper.in(Course::getCollegeId,
                        collegeList
                                .stream()
                                .map(College::getId)
                                .collect(Collectors.toList()))
                        .orderByDesc(Course::getCourseName); 
            }
            List<Course> courseList = this.list(queryWrapper);
                return convertToDTO(courseList);
        }
    }

    /**
     * 根据课程名称删除课程
     * @param courseName 课程名称
     * @return 是否删除成功
     */
    @Override
    public boolean deleteCourseByCourseId(Integer courseId) {
        try {
            //删除课程与学生选课关系
            QueryWrapper<CourseWithStudents> cwsWrapper = new QueryWrapper<>();
            cwsWrapper.eq("course_id",courseId);
            courseWithStudentsMapper.delete(cwsWrapper);
            
            return this.removeById(courseId);
        } catch (Exception e) {
            throw new RuntimeException("删除课程失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据课程ID获取课程信息
     * @param courseId 课程ID
     * @return 课程信息
     */
    @Override
    public Course getCourseById(Integer courseId) {
        try {
            Course course = this.getById(courseId);
            if(course==null){
                throw new RuntimeException("课程不存在，请检查课程ID是否有效");
            }
            return course;
        } catch (Exception e) {
            throw new RuntimeException("获取课程失败: " + e.getMessage(), e);
        }
        
    }

    /**
     * 更新课程信息
     * @param course 课程信息
     * @return 是否更新成功
     */
    @Override
    public boolean updatingCourseInfo(Course course) {
        try {
            //根据id查询课程所有信息
            Course oldCourse = this.getById(course.getId());
            if(oldCourse==null){
                throw new RuntimeException("课程不存在，请检查课程ID是否有效");
            }
            //更新课程信息
            oldCourse.setCourseName(course.getCourseName());
            oldCourse.setCollegeId(course.getCollegeId());
            oldCourse.setCredit(course.getCredit());
            oldCourse.setMaxNum(course.getMaxNum());
            boolean isSuccess = this.updateById(oldCourse);
            if(!isSuccess){
                throw new RuntimeException("更新课程信息失败");
            }
            return isSuccess;
        } catch (Exception e) {
            throw new RuntimeException("更新课程信息失败: " + e.getMessage(), e);
        }
    }
}
