package com.cjy.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDTO {
    /**
     * 旧密码
     */

    private String oldPassword;

    /**
     * 新密码
     */
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "密码必须至少8位，且包含字母和数字")
    private String newPassword;

    /**
     * 确认密码
     */

    private String confirmPassword;
}
