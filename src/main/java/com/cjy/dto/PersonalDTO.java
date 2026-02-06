package com.cjy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 身份名称
     */
    private String identityName;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 电话
     */
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "电话号码或邮箱格式不正确")
    private String phone;

    /**
     * 邮箱
     */
    @Pattern(regexp = "^$|^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "邮箱格式不正确")
    private String email;

    /**
     * 地址
     */
    private String address;
}
