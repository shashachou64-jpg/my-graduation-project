package com.cjy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cjy.domain.Course;
import com.cjy.domain.dto.CourseDTO;
import com.cjy.domain.dto.CourseWithStudentsDTO;

@Service
public interface ICourseService extends IService<Course> {
    CourseWithStudentsDTO addCourseWithStudents(CourseWithStudentsDTO request);

    List<CourseDTO> ListCourse(String courseName, String collegeName);

    List<CourseDTO> searchCourse(String SearchType, String keyword);

    boolean deleteCourseByCourseId(Integer courseId);

    Course getCourseById(Integer courseId);

    boolean updatingCourseInfo(Course course);
}
