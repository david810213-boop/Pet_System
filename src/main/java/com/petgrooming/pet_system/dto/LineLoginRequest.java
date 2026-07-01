package com.petgrooming.pet_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// LIFF 前端登入時傳來的 idToken
@Data
public class LineLoginRequest {

    @NotBlank(message = "idToken 不能為空")
    private String idToken;
}
