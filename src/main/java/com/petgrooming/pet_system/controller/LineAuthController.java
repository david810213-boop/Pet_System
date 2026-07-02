package com.petgrooming.pet_system.controller;

import com.petgrooming.pet_system.dto.LineLoginRequest;
import com.petgrooming.pet_system.dto.LineLoginResponse;
import com.petgrooming.pet_system.dto.LineVerifyResponse;
import com.petgrooming.pet_system.dto.UserResponse;
import com.petgrooming.pet_system.model.User;
import com.petgrooming.pet_system.service.UserService;
import com.petgrooming.pet_system.utils.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.AbstractMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/line")
@RequiredArgsConstructor
public class LineAuthController {

    private static final String LINE_VERIFY_URL = "https://api.line.me/oauth2/v2.1/verify";

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final RestClient restClient = RestClient.create();

    @Value("${line.channel-id}")
    private String channelId;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LineLoginRequest req,
            HttpServletResponse response) {

        // 1. 向 LINE 官方驗證 idToken
        LineVerifyResponse verified;
        try {
            verified = verifyIdToken(req.getIdToken());
        } catch (RestClientResponseException e) {
            log.warn("LINE idToken 驗證失敗: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "LINE_AUTH_FAILED", "message", "idToken 無效或已過期"));
        }

        // 2. 確認 token 是發給本系統的 LINE Login Channel
        if (verified == null || !channelId.equals(verified.getAud())) {
            log.warn("idToken aud 不符，預期: {}，實際: {}", channelId, verified != null ? verified.getAud() : "null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUD_MISMATCH", "message", "idToken 不屬於本系統"));
        }

        // 3. 依 LINE userId 查找或建立會員
        AbstractMap.SimpleEntry<User, Boolean> result = userService.findOrCreateByLine(verified.getSub(), verified.getName());
        User user = result.getKey();
        boolean isNewMember = result.getValue();

        // 4. 簽發本系統的 JWT（標記 source=LINE）
        String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name(), "LINE");

        // 5. 同時將 Token 寫入 Cookie，方便 Thymeleaf 頁面直刷讀取
        Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(86400); // 1 天
        response.addCookie(jwtCookie);

        // 6. ★ 使用你原本專案定義的建構子傳參，完美解決 setUsername 紅字問題
        LineLoginResponse loginResponse = new LineLoginResponse(token, UserResponse.from(user), isNewMember);

        log.info("LINE 用戶登入成功: {}, 是否為新會員: {}", user.getUsername(), isNewMember);
        return ResponseEntity.ok(loginResponse);
    }

    private LineVerifyResponse verifyIdToken(String idToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("id_token", idToken);
        form.add("client_id", channelId);

        return restClient.post()
                .uri(LINE_VERIFY_URL)
                .contentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(LineVerifyResponse.class);
    }
}
