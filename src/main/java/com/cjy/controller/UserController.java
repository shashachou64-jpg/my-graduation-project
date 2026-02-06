package com.cjy.controller;

import com.auth0.jwt.JWT;
import com.cjy.common.Result;
import com.cjy.domain.User;
import com.cjy.dto.PasswordDTO;
import com.cjy.dto.PersonalDTO;
import com.cjy.service.IUserService;
import com.cjy.utils.JwtUtil;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@Validated
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService iUserService;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");

        // 逻辑校验
        Result result = iUserService.userLogin(username, password);

        return result;
    }

    /**
     * 获取个人信息
     * 
     * @param authHeader
     * @return
     */
    @GetMapping("/profile")
    public Result profile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);

        String username = JwtUtil.getUsername(token);
        Long id = JwtUtil.getId(token);

        if (username == null || id == null) {
            return Result.error("登录过期，请重新登录");
        }

        PersonalDTO personalDTO = iUserService.getPersonalInfo(id);

        return Result.success(personalDTO, "获取个人信息成功");
    }

    /**
     * 更新密码
     * 
     * @param token
     * @param passwordDTO
     * @return
     */
    @PutMapping("updatePassword/{token}")
    public Result putMethodName(@PathVariable String token, @Valid @RequestBody PasswordDTO passwordDTO) {
        // 逻辑校验
        if (token.trim().isEmpty() == true) {
            return Result.error("获取用户信息失败，请退出重新登录");
        }

        if (passwordDTO.getOldPassword().trim().isEmpty() == true) {
            return Result.error("旧密码不能为空");
        }

        if (passwordDTO.getConfirmPassword().trim().isEmpty() == true
                || passwordDTO.getNewPassword().trim().isEmpty() == true) {
            return Result.error("请输入要更改的密码并确认新密码");
        }
        System.out.println(passwordDTO.getNewPassword() + " " + passwordDTO.getConfirmPassword());
        if (!passwordDTO.getNewPassword().trim().equals(passwordDTO.getConfirmPassword().trim())) {
            return Result.error("确认密码与新密码不一致请重新输入123");
        }

        // 获取用户id
        Long id = JwtUtil.getId(token);
        System.out.println(id);

        Result result = iUserService.revisePassword(id, passwordDTO);

        return result;
    }
}
