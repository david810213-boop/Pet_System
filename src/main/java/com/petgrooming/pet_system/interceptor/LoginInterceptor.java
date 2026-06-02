package com.petgrooming.pet_system.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登入驗證攔截器 — 對齊 parking-system 的模式
 * Session key 統一用 "loginUser"，存整個 User entity
 */
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {

        log.debug("LoginInterceptor 處理請求: {}", request.getRequestURI());

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("loginUser") != null) {
            return true; // 已登入，放行
        }

        // 未登入：記住原始路徑，登入後可跳回
        String uri = request.getRequestURI();
        log.debug("未登入，redirect 到登入頁，來源路徑: {}", uri);
        response.sendRedirect("/auth/login?redirect=" + uri);
        return false;
    }
}
