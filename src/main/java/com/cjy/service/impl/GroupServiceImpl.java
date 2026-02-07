package com.cjy.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjy.common.RedisConstants;
import com.cjy.common.Result;
import com.cjy.domain.Course;
import com.cjy.domain.CourseWithStudents;
import com.cjy.domain.Group;
import com.cjy.domain.StudentWithGroup;
import com.cjy.domain.Teacher;
import com.cjy.domain.TeacherWithCourse;
import com.cjy.domain.TeacherWithGroup;
import com.cjy.domain.User;
import com.cjy.dto.AddGroupDTO;
import com.cjy.mapper.CourseMapper;
import com.cjy.mapper.CourseWithStudentsMapper;
import com.cjy.mapper.GroupMapper;
import com.cjy.mapper.StudentWithGroupMapper;
import com.cjy.mapper.TeacherMapper;
import com.cjy.mapper.TeacherWithCourseMapper;
import com.cjy.mapper.UserMapper;
import com.cjy.service.IGroupService;
import com.cjy.utils.CacheClient;
import com.cjy.vo.GroupTotalVO;
import com.cjy.vo.GroupTotalVO.GroupInfoVO;

import cn.hutool.core.lang.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements IGroupService {

  @Autowired
  private GroupMapper groupMapper;

  @Autowired
  private TeacherMapper teacherMapper;

  @Autowired
  private CourseMapper courseMapper;

  @Autowired
  private TeacherWithCourseMapper teacherWithCourseMapper;

  @Autowired
  private CourseWithStudentsMapper courseWithStudentsMapper;

  @Autowired
  private StudentWithGroupMapper studentWithGroupMapper;

  @Autowired
  private UserMapper userMapper;

  @Resource
  private CacheClient cacheClient;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Override
  public List<Group> getGroupsByCourseId(Long courseId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getGroupsByCourseId'");
  }

  
}