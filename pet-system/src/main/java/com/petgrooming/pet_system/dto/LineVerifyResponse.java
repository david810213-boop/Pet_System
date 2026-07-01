package com.petgrooming.pet_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

// 對應 LINE idToken verify API 的回應格式
// https://api.line.me/oauth2/v2.1/verify
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LineVerifyResponse {
    private String iss;
    private String sub;   // LINE userId，作為 lineUserId 使用
    private String aud;   // 應等於你的 LINE Login Channel ID
    private String name;  // LINE 顯示名稱
    private String picture;
    private Long exp;
}
