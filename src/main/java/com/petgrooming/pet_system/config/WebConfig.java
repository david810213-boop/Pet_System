package com.petgrooming.pet_system.config;

import com.petgrooming.pet_system.interceptor.LoginInterceptor;
import com.petgrooming.pet_system.interceptor.RoleInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 攔截順序：
 * order(1) LoginInterceptor → 確認是否登入
 * order(2) RoleInterceptor → 確認角色是否有權限（讀 @RequireRole）
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

        private final LoginInterceptor loginInterceptor;
        private final RoleInterceptor roleInterceptor;

        @Override
        public void addInterceptors(@NonNull InterceptorRegistry registry) {

                // 1. 登入檢查（優先，所有路徑）
                registry.addInterceptor(loginInterceptor)
                                .addPathPatterns("/**")
                                .excludePathPatterns(
                                                "/auth/login",
                                                "/auth/login/submit",
                                                "/auth/register",
                                                "/auth/register/submit",
                                                "/auth/logout",
                                                "/css/**",
                                                "/js/**",
                                                "/images/**",
                                                "/static/**",
                                                "/h2-console/**",
                                                "/error",
                                                "/favicon.ico")
                                .order(1);

                // 2. 角色權限（在登入檢查之後）
                // 攔截全部路徑，只對有 @RequireRole 的 Controller 方法生效
                registry.addInterceptor(roleInterceptor)
                                .addPathPatterns("/**")
                                .excludePathPatterns(
                                                "/auth/**",
                                                "/css/**", "/js/**", "/images/**", "/static/**",
                                                "/h2-console/**", "/error", "/favicon.ico")
                                .order(2);
        }
}
