package com.cjy.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjy.domain.StudentWithGroup;
import com.cjy.mapper.StudentWithGroupMapper;
import com.cjy.service.IStudentWithGroupService;

@Service
public class StudentWithGroupServiceImpl extends ServiceImpl<StudentWithGroupMapper, StudentWithGroup> implements IStudentWithGroupService {
    
}
