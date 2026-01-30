package com.cjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.repository.IRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjy.domain.Identity;
import com.cjy.domain.Personal;
import com.cjy.domain.Result;
import com.cjy.domain.User;
import com.cjy.domain.UserWithIdentity;
import com.cjy.domain.dto.PasswordDTO;
import com.cjy.domain.dto.PersonalDTO;
import com.cjy.domain.dto.UserDTO;
import com.cjy.mapper.IdentityMapper;
import com.cjy.mapper.UserMapper;
import com.cjy.mapper.UserWithIdentityMapper;
import com.cjy.service.IPersonalService;
import com.cjy.service.IUserService;
import com.cjy.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserWithIdentityMapper userWithIdentityMapper;

    @Autowired
    private IdentityMapper identityMapper;

    @Autowired
    private IPersonalService personalService;

    @Override
    public Result userLogin(String username, String password) {

        try {
            // 逻辑校验
            if (username == null || password == null) {
                return Result.error("用户名或密码不能为空");
            }

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, username);
            User user = userMapper.selectOne(queryWrapper);
            if (user == null) {
                return Result.error("用户不存在");
            }
            if (!user.getPassword().equals(password)) {
                return Result.error("密码错误");
            }

            // 校验成功，返回信息
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(user.getUsername());
            userDTO.setPassword(user.getPassword());

            // 查询identityName
            LambdaQueryWrapper<UserWithIdentity> getWrapper = new LambdaQueryWrapper<>();
            getWrapper.eq(UserWithIdentity::getUserId, user.getId());
            UserWithIdentity userWithIdentity = userWithIdentityMapper.selectOne(getWrapper);
            if (userWithIdentity == null) {
                return Result.error("该用户没有绑定身份，无法跳转");
            }

            LambdaQueryWrapper<Identity> identityWrapper = new LambdaQueryWrapper<>();
            identityWrapper.eq(Identity::getId, userWithIdentity.getIdentityId());
            Identity identity = identityMapper.selectOne(identityWrapper);
            if (identity == null) {
                return Result.error("该用户没有绑定身份，无法跳转");
            }
            userDTO.setIdentityName(identity.getName());

            // 生成token
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", user.getUsername());
            claims.put("id", user.getId());
            String token = JwtUtil.genToken(claims);
            userDTO.setToken(token);

            return Result.success(userDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public PersonalDTO getPersonalInfo(Long id) {
        try {
            LambdaQueryWrapper<Personal> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Personal::getUserId, id);
            Personal personal = personalService.getOne(queryWrapper);

            PersonalDTO personalDTO = new PersonalDTO();
            personalDTO.setName(personal.getName());
            String sex = personal.getSex();
            personalDTO.setSex(!sex.trim().isEmpty() ? sex : "未设置");
            String phone = personal.getPhone();
            personalDTO.setPhone(!phone.trim().isEmpty() ? phone : "未设置");
            String email = personal.getEmail();
            personalDTO.setEmail(!email.trim().isEmpty() ? email : "未设置");
            String address = personal.getAddress();
            personalDTO.setAddress(!address.trim().isEmpty() ? address : "未设置");

            // 获取用户名和密码
            LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
            userQueryWrapper.eq(User::getId, id);
            User user = this.getOne(userQueryWrapper);

            personalDTO.setUsername(user.getUsername());
            personalDTO.setPassword(user.getPassword());

            // 获取身份名称
            LambdaQueryWrapper<UserWithIdentity> userWithIdentityQueryWrapper = new LambdaQueryWrapper<>();
            userWithIdentityQueryWrapper.eq(UserWithIdentity::getUserId, id);
            UserWithIdentity userWithIdentity = userWithIdentityMapper.selectOne(userWithIdentityQueryWrapper);
            LambdaQueryWrapper<Identity> identityQueryWrapper = new LambdaQueryWrapper<>();
            identityQueryWrapper.eq(Identity::getId, userWithIdentity.getIdentityId());
            Identity identity = identityMapper.selectOne(identityQueryWrapper);
            personalDTO.setIdentityName(identity.getName());

            return personalDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    @Override
    public Result revisePassword(Long id, PasswordDTO passwordDTO) {
        // 判断用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        User user = userMapper.selectOne(queryWrapper.eq(User::getId, id));

        if (user == null) {
            return Result.error("该用户不存在，请检查");
        }

        // 判断旧密码是否匹配
        if (!user.getPassword().equals(passwordDTO.getOldPassword())) {
            return Result.error("原密码输入错误请重试");
        }

        // 更新密码
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("password", passwordDTO.getNewPassword());
        boolean isSuccess = this.update(updateWrapper);

        if (isSuccess) {
            return Result.success("更改密码成功");
        } else {
            return Result.success("更改密码失败请稍后再试");
        }

    }
}
