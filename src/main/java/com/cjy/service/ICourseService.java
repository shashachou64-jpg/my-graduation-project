package com.cjy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cjy.domain.Course;
import com.cjy.dto.CourseDTO;
import com.cjy.dto.CourseWithStudentsDTO;
import com.cjy.vo.CourseInfoVO;
import com.cjy.vo.CourseTotalVO;

@Service
public interface ICourseService extends IService<Course> {
    CourseWithStudentsDTO addCourseWithStudents(CourseWithStudentsDTO request);

    List<CourseDTO> ListCourse(String courseName, String collegeName);

    List<CourseDTO> searchCourse(String SearchType, String keyword);

    boolean deleteCourseByCourseId(Integer courseId);

    Course getCourseById(Integer courseId);

    boolean updatingCourseInfo(Course course);

    CourseTotalVO getCourseTotalInfo();

    List<CourseInfoVO> getCourseListByTeacherId(Long teacherId);
}
