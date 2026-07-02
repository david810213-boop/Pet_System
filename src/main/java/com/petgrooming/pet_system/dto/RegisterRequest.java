package com.petgrooming.pet_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 接收註冊輸入（role 不開放給前端自己填）
@Data
public class RegisterRequest {

    @NotBlank(message = "帳號不能為空")
    @Email(message = "帳號格式必須是 Email")
    private String username;

    @NotBlank(message = "密碼不能為空")
    @Size(min = 6, message = "密碼至少 6 個字元")
    private String password;

    @NotBlank(message = "姓名不能為空")
    private String name;
}
