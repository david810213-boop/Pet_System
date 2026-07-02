package com.petgrooming.pet_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// 登入輸入 DTO
@Data
public class LoginRequest {

    @NotBlank(message = "帳號不能為空")
    private String username;

    @NotBlank(message = "密碼不能為空")
    private String password;
}