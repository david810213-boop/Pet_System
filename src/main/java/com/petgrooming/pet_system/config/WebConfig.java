package com.petgrooming.pet_system.config;

import com.petgrooming.pet_system.interceptor.LoginInterceptor;
import com.petgrooming.pet_system.interceptor.RoleInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 攔截順序與路徑白名單配置：
 * order(1) LoginInterceptor → 確認是否登入（支援網頁 Cookie 與行動端 Bearer Header）
 * order(2) RoleInterceptor  → 確認角色是否有權限（讀取 @RequireRole）
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final RoleInterceptor roleInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {

        // 1. 登入檢查攔截器（作用於所有路徑，排除不需要登入的白名單）
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",          // 店家登入頁
                        "/auth/login/submit",   // 店家登入送出
                        "/auth/register",       // 店家註冊頁
                        "/auth/register/submit",// 店家註冊送出
                        "/auth/logout",         // 登出
                        "/api/line/login",      // ★ 關鍵：LINE 登入 API（不能攔截）
                        "/liff/**",             // ★ 關鍵：前端 LIFF 的靜態 HTML 網頁目錄
                        "/test/**",             // 測試路徑
                        "/css/**",              // 靜態資源
                        "/js/**", 
                        "/images/**", 
                        "/static/**",
                        "/h2-console/**",       // 資料庫主控台
                        "/error", 
                        "/favicon.ico"
                )
                .order(1);

        // 2. 角色權限檢查攔截器（在登入檢查通過後執行）
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/**",
                        "/api/line/login",      // ★ 關鍵：登入時還不需要檢查角色
                        "/liff/**",             // ★ 關鍵：靜態網頁放行，由內部的 API 請求再行驗證
                        "/test/**", 
                        "/css/**", 
                        "/js/**", 
                        "/images/**", 
                        "/static/**",
                        "/h2-console/**", 
                        "/error", 
                        "/favicon.ico"
                )
                .order(2);
    }
}
