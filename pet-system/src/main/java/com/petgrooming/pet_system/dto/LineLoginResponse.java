package com.petgrooming.pet_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 登入成功後回給 LIFF 前端：token 讓前端自行存放（LIFF webview 不一定能共用 Cookie）
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineLoginResponse {
    private String token;
    private UserResponse user;
    private boolean newMember; // 是否為新建立的會員（前端可藉此導去補資料頁）
}
