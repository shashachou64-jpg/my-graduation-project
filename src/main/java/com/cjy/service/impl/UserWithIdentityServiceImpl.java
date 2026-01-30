package com.cjy.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjy.domain.UserWithIdentity;
import com.cjy.mapper.UserWithIdentityMapper;
import com.cjy.service.IUserWithIdentityService;

@Service
public class UserWithIdentityServiceImpl extends ServiceImpl<UserWithIdentityMapper, UserWithIdentity> implements IUserWithIdentityService{
    
}
