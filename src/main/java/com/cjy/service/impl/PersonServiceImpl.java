package com.cjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjy.common.Result;
import com.cjy.domain.Personal;
import com.cjy.domain.User;
import com.cjy.dto.PersonalDTO;
import com.cjy.mapper.PersonalMapper;
import com.cjy.mapper.UserMapper;
import com.cjy.service.IPersonalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl extends ServiceImpl<PersonalMapper, Personal> implements IPersonalService{
    @Autowired
    private PersonalMapper personalMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result updatePersonalInfo(Long id, PersonalDTO personalDTO) {
        try {
            //身份验证
            LambdaQueryWrapper<Personal> personalWrapper = new LambdaQueryWrapper<>();
            Personal personal=personalMapper.selectOne(personalWrapper.eq(Personal::getUserId, id));
            if(personal==null){
                return Result.error("用户不存在，更新失败");
            }

            /**
             * 更新字段
             */
            personal.setName(personalDTO.getName());
            personal.setSex(personalDTO.getSex());
            personal.setPhone(personalDTO.getPhone());
            personal.setEmail(personalDTO.getEmail());
            personal.setAddress(personalDTO.getAddress());
            personalMapper.updateById(personal);
            return Result.success("更新成功");
            

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
