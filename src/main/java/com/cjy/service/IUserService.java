package com.cjy.service;

import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cjy.domain.Result;
import com.cjy.domain.User;
import com.cjy.domain.dto.PasswordDTO;
import com.cjy.domain.dto.PersonalDTO;

import jakarta.validation.constraints.Pattern;

public interface IUserService extends IService<User> {

    /**
     * 用户登陆逻辑校验
     * 
     * @param userName
     * @param password
     * @return
     */
    Result userLogin(String userName, String password);

    /**
     * 获取个人信息
     * 
     * @param username 用户名
     * @param userId   用户id
     * @return 个人信息
     */
    PersonalDTO getPersonalInfo(Long id);

    /**
     * 更新密码
     * @param id
     * @param passwordDTO
     * @return
     */
    Result revisePassword(Long id, PasswordDTO passwordDTO);
}
